package cn.dagongniu.bitman;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.store.PersistentCookieStore;
import com.socks.library.KLog;
//import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import cn.dagongniu.bitman.broadcast.NotificationUtils;
import cn.dagongniu.bitman.broadcast.ScreenStatusReceiver;
import cn.dagongniu.bitman.constant.SPConstant;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.language.MultiLanguageUtil;
import cn.dagongniu.bitman.main.MainActivity;
import cn.dagongniu.bitman.main.bean.IndexPageBean;
import cn.dagongniu.bitman.main.bean.OaxMarketBean;
import cn.dagongniu.bitman.trading.bean.OrderSelectNoticeBean;
import cn.dagongniu.bitman.utils.Logs;
import cn.dagongniu.bitman.utils.SPUtils;


public class OAXApplication extends Application implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = "OAXApplication";

    private static OAXApplication instance;
    public static Context sContext;
    public static int defaultMarketId = -1;
    private ScreenStatusReceiver mScreenStatusReceiver;
    public static boolean isScreenOn = true;

    public static final String UPDATE_STATUS_ACTION = "com.umeng.message.example.action.UPDATE_STATUS";

    public static HashMap<Integer, IndexPageBean.DataBean.AllMaketListBean.MarketListBean> coinsInfoMap = new HashMap<>();//marketId对应的交易对
    public static HashMap<Integer, IndexPageBean.DataBean.UserMaketListBean> collectCoinsMap = new HashMap<>();//用户收藏的交易对

    public OrderSelectNoticeBean orderSelectNoticeBean;//订单筛选条件模型类
    public static boolean mIsBackground;

    public static OAXApplication getInstance() {
        return instance;
    }

    private static EventBus mEventBus;

    public static Context getContext() {
        return sContext;
    }

    public static int bitmanloginstate;
    public String MarketName = "";
    public String MarketId = "";
    public int type = -1;

    public String UserEmail;
    public String UserPhone;
    public String UserGoogleKe;
    public int UserEmailState;
    public int UserPhoneState;
    public int UserGoogleState;

    public static int state = 0;

    public static boolean isCollect;
    //缓存的搜索历史列表
    public static List<IndexPageBean.DataBean.AllMaketListBean.MarketListBean> coinsInfoHistory = new ArrayList<>();

    private Handler handler;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //初始化内存泄漏检测
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return;
//        }
//        LeakCanary.install(this);

        MultiLanguageUtil.init(this);
        sContext = getApplicationContext();
        initOkHttp();
        mEventBus = EventBus.getDefault();
//            bugly
        CrashReport.initCrashReport(getApplicationContext());
//        友盟

        UMConfigure.init(this, Http.UmengKey, "Umeng", UMConfigure.DEVICE_TYPE_PHONE,
                "d69eaa6a20ecba3fc34ca42bc74f9569");

        initUpush();
        UMConfigure.setLogEnabled(true);
        registSreenStatusReceiver();
        registerActivityLifecycleCallbacks(this);
    }

    private void initUpush() {
        PushAgent mPushAgent = PushAgent.getInstance(this);
        handler = new Handler(getMainLooper());

        //sdk开启通知声音
        mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);

        UmengMessageHandler messageHandler = new UmengMessageHandler() {

            /**
             * 通知的回调方法（通知送达时会回调）
             */
            @Override
            public void dealWithNotificationMessage(Context context, UMessage msg) {
                //调用super，会展示通知，不调用super，则不展示通知。
                super.dealWithNotificationMessage(context, msg);
            }

            /**
             * 自定义消息的回调方法
             */
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {

                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        // 对自定义消息的处理方式，点击或者忽略
                        boolean isClickOrDismissed = true;
                        if (isClickOrDismissed) {
                            //自定义消息的点击统计
                            UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
                        } else {
                            //自定义消息的忽略统计
                            UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
                        }
                        Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
                    }
                });
            }


            /**
             * 自定义通知栏样式的回调方法
             */
            @Override
            public Notification getNotification(Context context, UMessage msg) {

                Intent resultIntent = new Intent(context, MainActivity.class);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                resultIntent.putExtra("what", 0);
                PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationUtils notificationUtils = new NotificationUtils(context);
                notificationUtils
                        .setContentIntent(resultPendingIntent)
                        .sendNotificationCompat(0, msg.title, msg.text, R.mipmap.oax_icon);

                return null;

            }
        };
        mPushAgent.setMessageHandler(messageHandler);

        /**
         * 自定义行为的回调处理，参考文档：高级功能-通知的展示及提醒-自定义通知打开动作
         * UmengNotificationClickHandler是在BroadcastReceiver中被调用，故
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * */
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {

            @Override
            public void launchApp(Context context, UMessage msg) {
                super.launchApp(context, msg);
            }

            @Override
            public void openUrl(Context context, UMessage msg) {
                super.openUrl(context, msg);
            }

            @Override
            public void openActivity(Context context, UMessage msg) {
                super.openActivity(context, msg);
            }

            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
            }
        };
        //使用自定义的NotificationHandler
        mPushAgent.setNotificationClickHandler(notificationClickHandler);

        //注册推送服务 每次调用register都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                Log.i(TAG, "device token: " + deviceToken);
                sendBroadcast(new Intent(UPDATE_STATUS_ACTION));
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.i(TAG, "register failed: " + s + " " + s1);
                sendBroadcast(new Intent(UPDATE_STATUS_ACTION));
            }
        });
    }


    public static EventBus getmEventBus() {
        return mEventBus;
    }

    public static boolean getAppLog() {
        // TODO　是否开启log模式 (true开启  false关闭)
        return Logs.isDebug;
    }

    /**
     * 获取版本号
     *
     * @return
     */
    public String getAppVersion() {
        String appVersion;
        try {
            appVersion = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (Exception e) {
            appVersion = "1.0.0";
        }
        return appVersion;
    }


    private void initOkHttp() {
        //必须调用初始化
        OkGo.init(this);
        //以下设置的所有参数是全局参数,同样的参数可以在请求的时候再设置一遍,那么对于该请求来讲,请求中的参数会覆盖全局参数
        //好处是全局参数统一,特定请求可以特别定制参数
        try {
//            HttpHeaders customHeader = new HttpHeaders();
//            customHeader.put(HttpHeaders.HEAD_KEY_CONTENT_TYPE, "application/json;charset=utf-8");

            //以下都不是必须的，根据需要自行选择,一般来说只需要 debug,缓存相关,cookie相关的 就可以了
            OkGo.getInstance()
                    // 打开该调试开关,打印级别INFO,并不是异常,是为了显眼,不需要就不要加入该行
                    // 最后的true表示是否打印okgo的内部异常，一般打开方便调试错误
                    .debug("okGo---", Level.INFO, true)
                    //如果使用默认的 60秒,以下三行也不需要传
                    .setConnectTimeout(10000)  //全局的连接超时时间
                    .setReadTimeOut(OkGo.DEFAULT_MILLISECONDS)     //全局的读取超时时间
                    .setWriteTimeOut(OkGo.DEFAULT_MILLISECONDS)    //全局的写入超时时间
                    //可以全局统一设置缓存模式,默认是不使用缓存,可以不传,具体其他模式看 github 介绍 https://github.com/jeasonlzy/
                    .setCacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)
                    //可以全局统一设置缓存时间,默认永不过期,具体使用方法看 github 介绍
                    .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)
                    .setRetryCount(3)
                    //.addCommonHeaders(customHeader)
                    .setCookieStore(new PersistentCookieStore())
                    .setCertificates();                               //方法一：信任所有证书,不安全有风险
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取缓存的搜索历史记录
     *
     * @return
     */
    public static List<IndexPageBean.DataBean.AllMaketListBean.MarketListBean> getCoinsInfoHistory() {
        String coinsInfoHistoryJsonStr = (String) SPUtils.getParam(getContext(), SPConstant.COINSINFO_HISTORY_LIST, "");
        Gson gson = new Gson();
        Type listType = new TypeToken<List<IndexPageBean.DataBean.AllMaketListBean.MarketListBean>>() {
        }.getType();
        List<IndexPageBean.DataBean.AllMaketListBean.MarketListBean> list = gson.fromJson(coinsInfoHistoryJsonStr, listType);
        coinsInfoHistory.clear();
        if (list != null) {
            coinsInfoHistory.addAll(list);
        }
        return coinsInfoHistory;
    }

    /**
     * 设置缓存历史记录
     *
     * @param coinsInfoHistoryBean
     */
    public static void setCoinsInfoHistory(IndexPageBean.DataBean.AllMaketListBean.MarketListBean coinsInfoHistoryBean) {
        //缓存已存在则不存
        List<IndexPageBean.DataBean.AllMaketListBean.MarketListBean> coinsInfoHistory = getCoinsInfoHistory();
        for (IndexPageBean.DataBean.AllMaketListBean.MarketListBean bean : coinsInfoHistory) {
            if (bean.getMarketId() == coinsInfoHistoryBean.getMarketId()) {
                return;
            }
        }
        //只缓存六条数据
        if (OAXApplication.coinsInfoHistory.size() == 6) {
            OAXApplication.coinsInfoHistory.remove(0);
        }
        OAXApplication.coinsInfoHistory.add(coinsInfoHistoryBean);
        //转json存
        Gson gson = new Gson();
        String coinsInfoHistoryJson = gson.toJson(OAXApplication.coinsInfoHistory);
        SPUtils.setParam(getContext(), SPConstant.COINSINFO_HISTORY_LIST, coinsInfoHistoryJson);
    }

    /**
     * 所有交易对信息
     *
     * @param beans
     */
    public static void setCoinsInfo(List<IndexPageBean.DataBean.AllMaketListBean> beans) {
        if (beans != null) {
            for (int i = 0; i < beans.size(); i++) {
                IndexPageBean.DataBean.AllMaketListBean allMaketListBean = beans.get(i);
                List<IndexPageBean.DataBean.AllMaketListBean.MarketListBean> marketList = allMaketListBean.getMarketList();
                if (marketList != null) {
                    for (int j = 0; j < marketList.size(); j++) {
                        IndexPageBean.DataBean.AllMaketListBean.MarketListBean marketListBean = marketList.get(j);
                        int marketId = marketListBean.getMarketId();
                        if (!coinsInfoMap.containsKey(marketId)) {
                            coinsInfoMap.put(marketId, marketListBean);
                        }
                        if (i == 0 && j == 0) {
                            defaultMarketId = marketListBean.getMarketId();
                            KLog.d("defaultMarketId = " + defaultMarketId);

                        }
                    }
                }
            }
        }
        KLog.d("coinsInfoMap = " + new Gson().toJson(coinsInfoMap));
    }

    /**
     * 所有交易对信息
     *
     * @param beans
     */
    public static void setCoinsInfoFromRecommend(List<IndexPageBean.DataBean.RecommendMarketListBean> beans) {
        Gson gson = new Gson();
        if (beans != null) {
            for (int i = 0; i < beans.size(); i++) {
                IndexPageBean.DataBean.RecommendMarketListBean recommendMarketListBean = beans.get(i);
                IndexPageBean.DataBean.RecommendMarketListBean.MarketCoinBean marketCoin = recommendMarketListBean.getMarketCoin();
                if (marketCoin != null) {
                    IndexPageBean.DataBean.AllMaketListBean.MarketListBean marketListBean = gson.fromJson(gson.toJson(marketCoin), IndexPageBean.DataBean.AllMaketListBean.MarketListBean.class);
                    int marketId = marketListBean.getMarketId();
                    if (!coinsInfoMap.containsKey(marketId)) {
                        coinsInfoMap.put(marketId, marketListBean);
                    }
                }
            }
        }
        KLog.d("coinsInfoMap = " + new Gson().toJson(coinsInfoMap));
    }


    /**
     * APP 所有All市场
     *
     * @param beans
     */
    public static void updateCoinsInfo(List<OaxMarketBean> beans) {
        if (beans != null) {
            for (int i = 0; i < beans.size(); i++) {
                OaxMarketBean oaxMarketBean = beans.get(i);
                int marketId = oaxMarketBean.getMarketId();
                try {
                    IndexPageBean.DataBean.AllMaketListBean.MarketListBean bean = new Gson().fromJson(new Gson().toJson(oaxMarketBean), IndexPageBean.DataBean.AllMaketListBean.MarketListBean.class);
                    coinsInfoMap.put(marketId, bean);
                } catch (Exception e) {
                    KLog.d("updateCoinsInfo Exception == " + e.getMessage());
                }
            }
        }
        KLog.d("coinsInfoMap = " + new Gson().toJson(coinsInfoMap));
    }

    /**
     * 设置收藏交易对信息
     *
     * @param beans
     */
    public static void setCollectCoinsMap(List<IndexPageBean.DataBean.UserMaketListBean> beans) {
        collectCoinsMap.clear();
        if (beans != null) {
            for (int i = 0; i < beans.size(); i++) {
                IndexPageBean.DataBean.UserMaketListBean userMaketListBean = beans.get(i);
                if (userMaketListBean != null) {
                    int marketId = userMaketListBean.getMarketId();
                    if (!collectCoinsMap.containsKey(marketId)) {
                        collectCoinsMap.put(marketId, userMaketListBean);
                    }
                }
            }
        }
        KLog.d("collectCoinsMap = " + new Gson().toJson(collectCoinsMap));
    }


    /**
     * 添加收藏交易对信息
     *
     * @param marketId
     */
    public static void addCollectCoinsMap(int marketId) {
        if (coinsInfoMap.containsKey(marketId)) {
            IndexPageBean.DataBean.AllMaketListBean.MarketListBean bean = coinsInfoMap.get(marketId);
            if (bean != null) {
                Gson gson = new Gson();
                String s = gson.toJson(bean);
                try {
                    IndexPageBean.DataBean.UserMaketListBean userMaketListBean = gson.fromJson(s, IndexPageBean.DataBean.UserMaketListBean.class);
                    collectCoinsMap.put(marketId, userMaketListBean);
                } catch (Exception e) {
                    KLog.d("collectCoinsMap Exception = " + e.getMessage());
                }
            }
        }
        KLog.d("collectCoinsMap = " + new Gson().toJson(collectCoinsMap));
    }

    /**
     * Map 转 List  所有市场
     *
     * @return
     */
    public static List<IndexPageBean.DataBean.AllMaketListBean.MarketListBean> toMapCoinsInfoList() {
        List<IndexPageBean.DataBean.AllMaketListBean.MarketListBean> coinsInfoList = new ArrayList<>();

        Iterator iterKey = coinsInfoMap.entrySet().iterator();  //获得map的Iterator

        while (iterKey.hasNext()) {
            Entry entry = (Entry) iterKey.next();
            coinsInfoList.add((IndexPageBean.DataBean.AllMaketListBean.MarketListBean) entry.getValue());
        }
        return coinsInfoList;
    }

    public OrderSelectNoticeBean getOrderSelectNoticeBean() {
        return orderSelectNoticeBean;
    }

    public void setOrderSelectNoticeBean(OrderSelectNoticeBean orderSelectNoticeBean) {
        this.orderSelectNoticeBean = orderSelectNoticeBean;
    }

    private void registSreenStatusReceiver() {
        mScreenStatusReceiver = new ScreenStatusReceiver();
        IntentFilter screenStatusIF = new IntentFilter();
        screenStatusIF.addAction(Intent.ACTION_SCREEN_ON);
        screenStatusIF.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenStatusReceiver, screenStatusIF);
    }


    public void setUserEmailState(int userEmailState) {
        UserEmailState = userEmailState;
    }

    public void setUserPhoneState(int userPhoneState) {
        UserPhoneState = userPhoneState;
    }

    public void setUserGoogleState(int userGoogleState) {
        UserGoogleState = userGoogleState;
    }

    public int getUserEmailState() {
        return UserEmailState;
    }

    public int getUserPhoneState() {
        return UserPhoneState;
    }

    public int getUserGoogleState() {
        return UserGoogleState;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public String getUserGoogleKe() {
        return UserGoogleKe;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }

    public void setUserGoogleKe(String userGoogleKe) {
        UserGoogleKe = userGoogleKe;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        mIsBackground = false;
        KLog.d("mIsBackground = " + mIsBackground);
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        mIsBackground = true;
        KLog.d("mIsBackground = " + mIsBackground);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
