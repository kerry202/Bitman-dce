package cn.dagongniu.bitman.https;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okgo.request.PostRequest;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.dagongniu.bitman.OAXApplication;
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.account.LoginActivity;
import cn.dagongniu.bitman.bitmanviews.Utils;
import cn.dagongniu.bitman.constant.SPConstant;
import cn.dagongniu.bitman.kline.bean.TradingInfoBean;
import cn.dagongniu.bitman.trading.bean.Bean;
import cn.dagongniu.bitman.utils.AppConstants;
import cn.dagongniu.bitman.utils.Logger;
import cn.dagongniu.bitman.utils.Logs;
import cn.dagongniu.bitman.utils.SPUtils;
import cn.dagongniu.bitman.utils.events.MyEvents;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @describe 网络请求工具类, okGo
 */

public class HttpUtils<T> {

    private static final String TAG = "HttpUtils";

    private static HttpUtils sUtils;
    private KProgressHUD dialog;

    private static final String LANG = "lang";//国际化 头部
    private static final String USERID = "userId";//用户ID 头部
    private static final String USERTOKEN = "accessToken";//用户token
    public Bundle bundle = new Bundle();

    private HttpUtils() {

    }

    public static HttpUtils getInstance() {
        if (sUtils == null)
            sUtils = new HttpUtils();
        return sUtils;
    }

    /**
     * TODO  post请求   (lang，ID ，Token)
     *
     * @param url                地址
     * @param params             上传值
     * @param activity           activity
     * @param onBaseDataListener 回调
     * @param state              网络加载进度条状态
     */
    public void postLangIdToken(String url, HashMap<String, String> params, final Activity activity,
                                final OnBaseDataListener<String> onBaseDataListener,
                                final RequestState state) {

        //网络判断
        if (PrefUtils.getNetState(OAXApplication.getContext(), 0) == 0) {
            onBaseDataListener.onError(activity.getResources().getString(R.string.http_network_errer));
            return;
        }

        //加载状态判断
        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
            showDialog(activity);

        //判断用户ID是否存在
        String userid = SPUtils.getParamString(activity, SPConstant.USER_ID, null);
        //判断用户Token是否存在
        String userToken = (String) SPUtils.getParam(activity, SPConstant.USER_TOKEN, "");

        Logs.s("     用户信息 userToken  ；    " + userToken);
        Logs.s("     用户信息 userid  ；    " + userid);

        String cn = (String) SPUtils.getParam(activity, SPConstant.LANGUAGE, "CN");
        Logs.s("    语言适配 2   " + cn);
        cn = cn.toLowerCase();
        JSONObject jsonObject = new JSONObject(params);
        OkGo.post(url).tag(activity)    // 请求方式和请求url// 请求的 tag, 主要用于取消对应的请求
                .upJson(jsonObject)
                .headers(HttpUtils.USERID, userid)
                .headers(HttpUtils.USERTOKEN, userToken)
                .headers(HttpUtils.LANG, cn) //TODO lang添加头部
                .cacheKey(url + "oax")// 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)    // 缓存模式，详细请看缓存介绍
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        // s 即为所需要的结果
                        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
                            dismissDialog();
                        onBaseDataListener.onNewData(s);
                        /**
                         * 判断token失效
                         */
                        login(activity, s);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
                            dismissDialog();
                        onBaseDataListener.onError(activity.getResources().getString(R.string.http_server_errer));
                    }
                });
    }

    /**
     * TODO  post请求   (带lang国际化请求头 带参数)
     *
     * @param url                地址
     * @param params             上传值
     * @param activity           activity
     * @param onBaseDataListener 回调
     * @param state              网络加载进度条状态
     */
    public void postLang(String url, HashMap<String, String> params, final Activity activity,
                         final OnBaseDataListener<String> onBaseDataListener,
                         final RequestState state) {
        showDialog(activity);
        //网络判断
        if (PrefUtils.getNetState(OAXApplication.getContext(), 0) == 0) {
            onBaseDataListener.onError(activity.getResources().getString(R.string.http_network_errer));
            OAXApplication.bitmanloginstate = 0;
            return;
        }

        //加载状态判断
        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
            showDialog(activity);

        JSONObject jsonObject = new JSONObject(params);
        Logs.s("    cradBeancradBean 22  " + jsonObject);


        String cn = (String) SPUtils.getParam(activity, SPConstant.LANGUAGE, "CN");
        Logs.s("    语言适配 1   " + cn);
        cn = cn.toLowerCase();
        //TODO lang添加头部
//        if (OAXApplication.bitmanloginstate ==1) {
//            OAXApplication.bitmanloginstate =0;
//            OkGo.post(url).tag(activity)    // 请求方式和请求url// 请求的 tag, 主要用于取消对应的请求
//                    .upJson(jsonObject)
//                    .headers("language", cn)
//                    .headers("deviceType", Http.device)
//                    .headers("apiVersion", Utils.getVersionCode(activity))
//                    .headers("loginProject", Http.projectName)
//                    .cacheKey(url + "oax")// 设置当前请求的缓存key,建议每个不同功能的请求设置一个
//                    .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)    // 缓存模式，详细请看缓存介绍
//                    .execute(new StringCallback() {
//                        @Override
//                        public void onSuccess(String s, Call call, Response response) {
//                            Logs.s("   设置当前请求的缓存keyonSuccess     " + s);
//                            Bean bean = new Gson().fromJson(s, Bean.class);
//                            Logs.s("   设置当前请求的缓存bean    " + bean);
//
//                            //  即为所需要的结果
////                        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
//                            dismissDialog();
//                            onBaseDataListener.onNewData(s);
//
//                        }
//
//                        @Override
//                        public void onError(Call call, Response response, Exception e) {
//                            Logs.s("   设置当前请求的缓存key onError    " + e.getMessage());
////                        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
//                            dismissDialog();
//                            onBaseDataListener.onError(activity.getResources().getString(R.string.http_server_errer));
//                        }
//                    });
//        }else {
        OAXApplication.bitmanloginstate = 0;
        OkGo.post(url).tag(activity)    // 请求方式和请求url// 请求的 tag, 主要用于取消对应的请求
                .upJson(jsonObject)
                .headers(HttpUtils.LANG, cn) //TODO lang添加头部
                .cacheKey(url + "oax")// 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)    // 缓存模式，详细请看缓存介绍
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Logs.s("   设置当前请求的缓存keyonSuccess     " + s);
                        //  即为所需要的结果
//                        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
                        dismissDialog();
                        onBaseDataListener.onNewData(s);

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        Logs.s("   设置当前请求的缓存key onError    " + e.getMessage());
//                        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
                        dismissDialog();
                        onBaseDataListener.onError(activity.getResources().getString(R.string.http_server_errer));
                    }
                });
//        }

    }

    public void bitmanDate(String url, HashMap<String, Object> params, final Activity activity,
                           final OnBaseDataListener<String> onBaseDataListener,
                           final RequestState state) {
        showDialog(activity);
        //网络判断
        if (PrefUtils.getNetState(OAXApplication.getContext(), 0) == 0) {
            onBaseDataListener.onError(activity.getResources().getString(R.string.http_network_errer));
            return;
        }

        //加载状态判断
        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
            showDialog(activity);

        //判断用户ID是否存在
        String userid = SPUtils.getParamString(activity, SPConstant.USER_ID, null);
        //判断用户Token是否存在
        String userToken = (String) SPUtils.getParam(activity, SPConstant.USER_TOKEN, "");

        JSONObject jsonObject = new JSONObject(params);
        Logs.s(" bitmaninterface jsonObject " + jsonObject.toString());
        Logs.s(" bitmaninterface userid " + userid);
        Logs.s(" bitmaninterface accessToken " + userToken);
        String cn = (String) SPUtils.getParam(activity, SPConstant.LANGUAGE, "CN");
        cn = cn.toLowerCase();


        // 请求方式和请求url// 请求的 tag, 主要用于取消对应的请求
        OkGo.post(url).tag(activity)
                .upJson(jsonObject)
                .headers("userId", userid)
                .headers("accessToken", userToken)
                .headers("token", userToken)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        dismissDialog();
                        onBaseDataListener.onNewData(s);

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        dismissDialog();
                        onBaseDataListener.onError(e.getMessage());
                    }
                });
    }


    //工单
    public void postMsg(String url, HashMap<String, Object> params, final Activity activity,
                        final OnBaseDataListener<String> onBaseDataListener,
                        final RequestState state) {
        showDialog(activity);
        //网络判断
        if (PrefUtils.getNetState(OAXApplication.getContext(), 0) == 0) {
            onBaseDataListener.onError(activity.getResources().getString(R.string.http_network_errer));
            return;
        }

        //加载状态判断
        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
            showDialog(activity);


        //判断用户ID是否存在
        String userid = (String) SPUtils.getParamString(activity, SPConstant.USER_ID, null);
        //判断用户Token是否存在
        String userToken = (String) SPUtils.getParam(activity, SPConstant.USER_TOKEN, "");

        String cn = (String) SPUtils.getParam(activity, SPConstant.LANGUAGE, "CN");
        JSONObject jsonObject = new JSONObject(params);
        Logs.s(" jsonObjectjsonObject " + jsonObject.toString());
        cn = cn.toLowerCase();
        Logs.s("    语言适配 3   " + cn);
        OkGo.post(url).tag(activity)    // 请求方式和请求url// 请求的 tag, 主要用于取消对应的请求
                .upJson(jsonObject)
                .headers(HttpUtils.LANG, cn) //TODO lang添加头部
                .headers(HttpUtils.USERID, userid)
                .headers(HttpUtils.USERTOKEN, userToken)
                .cacheKey(url + "oax")// 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)    // 缓存模式，详细请看缓存介绍
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Logs.s("   设置当前请求的缓存keyonSuccess     " + s);
                        //  即为所需要的结果
//                        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
                        dismissDialog();
                        onBaseDataListener.onNewData(s);

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        Logs.s("   设置当前请求的缓存key onError    " + e.getMessage());
//                        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
                        dismissDialog();
                        onBaseDataListener.onError(activity.getResources().getString(R.string.http_server_errer));
                    }
                });
    }

    /**
     * TODO  post请求   (带lang国际化请求头 不带参数)
     *
     * @param url                地址
     * @param activity           activity
     * @param onBaseDataListener 回调
     * @param state              网络加载进度条状态
     */
    public void postLang(String url, final Activity activity,
                         final OnBaseDataListener<String> onBaseDataListener,
                         final RequestState state) {

        //网络判断
        if (PrefUtils.getNetState(OAXApplication.getContext(), 0) == 0) {
            onBaseDataListener.onError(activity.getResources().getString(R.string.http_network_errer));
            return;
        }
        //加载状态判断
        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
            showDialog(activity);

        String cn = (String) SPUtils.getParam(activity, SPConstant.LANGUAGE, "CN");
        cn = cn.toLowerCase();
//        String languageStringType = MultiLanguageUtil.getInstance().getLanguageStringType();
        Logs.s("    语言适配 4   " + cn);
        OkGo.post(url).tag(activity)    // 请求方式和请求url// 请求的 tag, 主要用于取消对应的请求
                .headers(HttpUtils.LANG, cn) //TODO lang添加头部
                .cacheKey(url + "oax")// 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)    // 缓存模式，详细请看缓存介绍
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        // s 即为所需要的结果
                        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
                            dismissDialog();
                        onBaseDataListener.onNewData(s);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
                            dismissDialog();
                        onBaseDataListener.onError(activity.getResources().getString(R.string.http_server_errer));
                    }
                });
    }


    /**
     * TODO  post请求   (首页接口专用，ID可传可不传)
     *
     * @param url                地址
     * @param activity           activity
     * @param onBaseDataListener 回调
     * @param state              网络加载进度条状态
     */
    public void postHeadersId(String url, final Activity activity,
                              final OnBaseDataListener<String> onBaseDataListener,
                              final RequestState state) {

        //网络判断
        if (PrefUtils.getNetState(OAXApplication.getContext(), 0) == 0) {
            onBaseDataListener.onError(activity.getResources().getString(R.string.http_network_errer));
            return;
        }
        //加载状态判断
        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
            showDialog(activity);
        String cn = (String) SPUtils.getParam(activity, SPConstant.LANGUAGE, "CN");
        //判断用户ID是否存在
        String userid = SPUtils.getParamString(activity, SPConstant.USER_ID, null);
        cn = cn.toLowerCase();

        Logger.e("okgo", "SharedPreferences --- UserId : " + userid);
        Logs.s("    语言适配 5   " + cn);
        OkGo.post(url).tag(activity)    // 请求方式和请求url// 请求的 tag, 主要用于取消对应的请求
                .headers(HttpUtils.USERID, userid)
                .headers(HttpUtils.LANG, cn) //TODO lang添加头部
                .cacheKey(url + "oax")// 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)    // 缓存模式，详细请看缓存介绍
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        // s 即为所需要的结果
                        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
                            dismissDialog();
                        onBaseDataListener.onNewData(s);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
                            dismissDialog();
                        onBaseDataListener.onError(activity.getResources().getString(R.string.http_server_errer));
                    }
                });
    }


    /**
     * TODO get请求   (带lang国际化请求头)
     *
     * @param url
     * @param activty
     * @param onBaseDataListener
     * @param state
     */
    public void getLang(String url, final Activity activty, final OnBaseDataListener<String> onBaseDataListener, final RequestState state) {
        if (PrefUtils.getNetState(OAXApplication.getContext(), 0) == 0) {
            onBaseDataListener.onError(activty.getResources().getString(R.string.http_network_errer));
            return;
        }
        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
            showDialog(activty);
        HttpDebugLogUtils.prinlnLog("get---request---" + url);
        String cn = (String) SPUtils.getParam(activty, SPConstant.LANGUAGE, "CN");
        Logs.s("    语言适配 6   " + cn);
        cn = cn.toLowerCase();
        OkGo.get(url).tag(activty)    // 请求方式和请求url// 请求的 tag, 主要用于取消对应的请求
                .headers(HttpUtils.LANG, cn) //TODO lang添加头部
                .cacheKey(url + "oax")// 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)    // 缓存模式，详细请看缓存介绍
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        // s 即为所需要的结果
                        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
                            dismissDialog();
                        onBaseDataListener.onNewData(s);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
                            dismissDialog();
                        onBaseDataListener.onError(activty.getResources().getString(R.string.http_server_errer));
                    }
                });

    }


    /**
     * TODO get请求   (带lang国际化请求头)
     *
     * @param url
     * @param activty
     * @param onBaseDataListener
     * @param state
     */
    public void getLangIdToKen(String url, final Activity activty, final OnBaseDataListener<String> onBaseDataListener, final RequestState state) {
        if (PrefUtils.getNetState(OAXApplication.getContext(), 0) == 0) {
            onBaseDataListener.onError(activty.getResources().getString(R.string.http_network_errer));
            return;
        }
        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
            showDialog(activty);
        HttpDebugLogUtils.prinlnLog("get---request---" + url);

        //判断用户ID是否存在
        String userid = SPUtils.getParamString(activty, SPConstant.USER_ID, null);
        //判断用户Token是否存在
        String userToken = (String) SPUtils.getParam(activty, SPConstant.USER_TOKEN, "");
        String cn = (String) SPUtils.getParam(activty, SPConstant.LANGUAGE, "CN");
        cn = cn.toLowerCase();
        Logs.s("    语言适配 7   " + cn);
        OkGo.get(url).tag(activty)    // 请求方式和请求url// 请求的 tag, 主要用于取消对应的请求
                .headers(HttpUtils.LANG, cn) //TODO lang添加头部
                .headers(HttpUtils.USERID, userid)
                .headers(HttpUtils.USERTOKEN, userToken)
                .cacheKey(url + "oax")// 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)    // 缓存模式，详细请看缓存介绍
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        // s 即为所需要的结果
                        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
                            dismissDialog();
                        onBaseDataListener.onNewData(s);
                        /**
                         * 判断token失效
                         */
                        login(activty, s);

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
                            dismissDialog();
                        onBaseDataListener.onError(activty.getResources().getString(R.string.http_server_errer));
                    }
                });

    }

    public void getAddressOrCrad(String url, final Activity activty, final OnBaseDataListener<String> onBaseDataListener, final RequestState state) {
        if (PrefUtils.getNetState(OAXApplication.getContext(), 0) == 0) {
            onBaseDataListener.onError(activty.getResources().getString(R.string.http_network_errer));
            return;
        }
//        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
        showDialog(activty);
        HttpDebugLogUtils.prinlnLog("get---request---" + url);
        String cn = (String) SPUtils.getParam(activty, SPConstant.LANGUAGE, "CN");
        cn = cn.toLowerCase();
        //判断用户ID是否存在
        String userid = SPUtils.getParamString(activty, SPConstant.USER_ID, null);
        //判断用户Token是否存在
        String userToken = (String) SPUtils.getParam(activty, SPConstant.USER_TOKEN, "");
        Logs.s("    语言适配 8   " + cn);
        OkGo.get(url).tag(activty)    // 请求方式和请求url// 请求的 tag, 主要用于取消对应的请求
                .headers(HttpUtils.LANG, cn) //TODO lang添加头部
                .headers(HttpUtils.USERID, userid)
                .headers(HttpUtils.USERTOKEN, userToken)
                .cacheKey(url + "oax")// 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)    // 缓存模式，详细请看缓存介绍
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        // s 即为所需要的结果
                        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
                            dismissDialog();
                        onBaseDataListener.onNewData(s);
                        /**
                         * 判断token失效
                         */
                        login(activty, s);

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
                            dismissDialog();
                        onBaseDataListener.onError(activty.getResources().getString(R.string.http_server_errer));
                    }
                });

    }


    /**
     * TODO get请求 我的里面请求接口 效验token
     *
     * @param url
     * @param activty
     * @param onBaseDataListener
     * @param state
     */
    public void getLangIdToKenMine(String url, final Activity activty, final OnBaseDataListener<String> onBaseDataListener, final RequestState state) {
        if (PrefUtils.getNetState(OAXApplication.getContext(), 0) == 0) {
            onBaseDataListener.onError(activty.getResources().getString(R.string.http_network_errer));
            return;
        }
        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
            showDialog(activty);
        HttpDebugLogUtils.prinlnLog("get---request---" + url);

        //判断用户ID是否存在
        String userid = (String) SPUtils.getParamString(activty, SPConstant.USER_ID, null);
        //判断用户Token是否存在
        String userToken = (String) SPUtils.getParam(activty, SPConstant.USER_TOKEN, "");
        String cn = (String) SPUtils.getParam(activty, SPConstant.LANGUAGE, "CN");
        cn = cn.toLowerCase();
        Logs.s("    语言适配 9   " + cn);
        OkGo.get(url).tag(activty)    // 请求方式和请求url// 请求的 tag, 主要用于取消对应的请求
                .headers(HttpUtils.LANG, cn) //TODO lang添加头部
                .headers(HttpUtils.USERID, userid)
                .headers(HttpUtils.USERTOKEN, userToken)
                .cacheKey(url + "oax")// 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)    // 缓存模式，详细请看缓存介绍
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        // s 即为所需要的结果
                        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
                            dismissDialog();
                        onBaseDataListener.onNewData(s);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
                            dismissDialog();
                        onBaseDataListener.onError(activty.getResources().getString(R.string.http_server_errer));
                    }
                });
    }

    /**
     * token 失效
     *
     * @param activty
     * @param json
     */
    public void login(Activity activty, String json) {
        HttpBaseBean httpBaseBean = new Gson().fromJson(json, HttpBaseBean.class);
        if (httpBaseBean.getCode() == -1) {
            Intent intent = new Intent(activty, LoginActivity.class);
            intent.putExtra(AppConstants.LOGIN_FAILURE, true);
            intent.putExtra(AppConstants.LOGINTYPE, AppConstants.LOGINPHONE);
            activty.startActivity(intent);
            SPUtils.remove(activty, SPConstant.LOGIN_STATE);
            SPUtils.remove(activty, SPConstant.USER_ID);
            SPUtils.remove(activty, SPConstant.USER_TOKEN);
            SPUtils.remove(activty, SPConstant.USER_ACCOUNT);
            SPUtils.remove(activty, SPConstant.USER_CHOOSECOUNTRIES);

            //发送token失效通知aa
            EventBus eventBus = OAXApplication.getmEventBus();
            MyEvents myEvents = new MyEvents();
            myEvents.status = MyEvents.status_ok;
            myEvents.status_type = MyEvents.Token_failure;
            eventBus.post(myEvents);
            Logger.e(TAG, "发送token失效通知!");

        }
    }

    /**
     * 文件上传
     *
     * @param url
     * @param activty
     * @param state
     * @param files
     * @param onBaseDataListener
     */
    public void postFile(String url, HashMap<String, String> map, final Activity activty, final RequestState state, List<File> files, final OnBaseDataListener<String> onBaseDataListener) {
        if (PrefUtils.getNetState(OAXApplication.getContext(), 0) == 0) {
            onBaseDataListener.onError(activty.getResources().getString(R.string.http_network_errer));
            return;
        }
        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
            showDialog(activty);


        Logs.s(" descriptionmap " + map.toString());

        PostRequest post = OkGo.post(url);
        String reQuestParam = "?";
        for (String param : map.keySet()) {
            post.params(param, map.get(param));
            reQuestParam = reQuestParam + param + "=" + map.get(param) + "&";
        }
        for (File file : files) {
            post.params(file.getName(), file);
        }
        reQuestParam = reQuestParam.substring(0, reQuestParam.length() - 1);

        HttpDebugLogUtils.prinlnLog("post---request--" + url + reQuestParam);

        //判断用户ID是否存在
        String userid = (String) SPUtils.getParamString(activty, SPConstant.USER_ID, null);
        //判断用户Token是否存在
        String userToken = (String) SPUtils.getParam(activty, SPConstant.USER_TOKEN, "");
        String cn = (String) SPUtils.getParam(activty, SPConstant.LANGUAGE, "CN");
        Logs.s("    语言适配 10   " + cn);
        cn = cn.toLowerCase();
        post.cacheKey(url + "uid")
                .isMultipart(true).cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)
                .headers(HttpUtils.LANG, cn) //TODO lang添加头部
                .headers(HttpUtils.USERID, userid)
                .headers(HttpUtils.USERTOKEN, userToken)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        dismissDialog();
                        onBaseDataListener.onNewData(s);
                        /**
                         * 判断token失效
                         */
                        login(activty, s);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        dismissDialog();
                        onBaseDataListener.onError(activty.getResources().getString(R.string.http_server_errer));
                    }
                });

    }


    /**
     * 普通的post请求 返回数据结构
     * {private String code;private boolean success;private String msg;private DataBean data;}
     *
     * @param url
     * @param params
     * @param activity
     * @param onBaseDataListener
     * @param clazz
     * @param state
     */
    public void commonPost(String url, Map<String, Object> params, Activity activity, OnDataListener<T> onBaseDataListener, Class<T> clazz, final RequestState state) {
        if (PrefUtils.getNetState(OAXApplication.getContext(), 0) == 0) {
            onBaseDataListener.onError(activity.getResources().getString(R.string.http_network_errer));
            return;
        }
        //判断弹框的显示状态 是否显示等等
        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
            showDialog(activity);
        KLog.d("okGo_upLoad", "upload:-->" + url + params);
        //判断用户ID是否存在
        String userid = (String) SPUtils.getParamString(activity, SPConstant.USER_ID, null);
        KLog.d("okGo_upLoad", "userid = " + userid);
        //判断用户Token是否存在
        String userToken = (String) SPUtils.getParam(activity, SPConstant.USER_TOKEN, "");
        KLog.d("okGo_upLoad", "userToken = " + userToken);
        JSONObject jsonObject = new JSONObject();
        if (params != null) {
            jsonObject = new JSONObject(params);
        }
        String cn = (String) SPUtils.getParam(activity, SPConstant.LANGUAGE, "CN");
        cn = cn.toLowerCase();
        Logs.s("    语言适配 11   " + cn);
        PostRequest request = OkGo.<String>post(url)
                .upJson(jsonObject)
                .headers(HttpUtils.LANG, cn)
                .headers(HttpUtils.USERID, userid)
                .headers(HttpUtils.USERTOKEN, userToken)
                .tag(activity)
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)
                .cacheKey(url);
        request.execute(new StringCallBack(activity, clazz, onBaseDataListener, url, state));
    }

    public void commonPost2(String url, Map<String, Object> params, Activity activity, OnDataListener<T> onBaseDataListener, Class<T> clazz, final RequestState state) {
        if (PrefUtils.getNetState(OAXApplication.getContext(), 0) == 0) {
            onBaseDataListener.onError(activity.getResources().getString(R.string.http_network_errer));
            return;
        }
        //判断弹框的显示状态 是否显示等等
        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
            showDialog(activity);
        KLog.d("okGo_upLoad", "upload:-->" + url + params);
        //判断用户ID是否存在
        String userid = SPUtils.getParamString(activity, SPConstant.USER_ID, null);
        KLog.d("okGo_upLoad", "userid = " + userid);
        //判断用户Token是否存在
        String userToken = (String) SPUtils.getParam(activity, SPConstant.USER_TOKEN, "");
        KLog.d("okGo_upLoad", "userToken = " + userToken);
        JSONObject jsonObject = new JSONObject();
        if (params != null) {
            jsonObject = new JSONObject(params);
        }
        String s = new Gson().toJson(params);

        KLog.d("okGo_upLoad", "sssss = " + s);
        KLog.d("okGo_upLoad", "jsonObject = " + jsonObject);
        PostRequest request = OkGo.<String>post(url)
                .upJson(jsonObject)
                .tag(activity)
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)
                .cacheKey(url);
        request.execute(new StringCallBack(activity, clazz, onBaseDataListener, url, state));

    }

    /**
     * 解决请求慢的问题。 重新定义请求
     *
     * @param url
     * @param activity
     * @param onBaseDataListener
     * @param tradingInfoBeanClass
     * @param state
     */
    public void commonKlinePost(String url, Map<String, Object> params, Activity activity,
                                OnDataListener<T> onBaseDataListener,
                                Class<TradingInfoBean> tradingInfoBeanClass, RequestState state) {
        if (PrefUtils.getNetState(OAXApplication.getContext(), 0) == 0) {
            onBaseDataListener.onError(activity.getResources().getString(R.string.http_network_errer));
            return;
        }
        //判断用户ID是否存在
        String userid = (String) SPUtils.getParam(activity, SPConstant.USER_ID, "");
        KLog.d("okGo_upLoad", "userid = " + userid);
        //判断用户Token是否存在
        String userToken = (String) SPUtils.getParam(activity, SPConstant.USER_TOKEN, "");
        KLog.d("okGo_upLoad", "userToken = " + userToken);
        try {
            String responseStr = "";
            // 定义okhttp
            OkHttpClient httpClient = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json;charset=utf-8");//数据类型为json格式，

            // 定义请求体
            // 执行okhttp
            JSONObject josnStr = new JSONObject();
            if (params != null) {
                josnStr = new JSONObject(params);
            }
            RequestBody body = RequestBody.create(JSON, josnStr.toString());

//            RequestBody body = new FormBody.Builder()
//                    .add(UrlParams.marketId, marketId + "")//添加参数体
//                    .add(UrlParams.minType, minType + "")
//                    .build();
            String cn = (String) SPUtils.getParam(activity, SPConstant.LANGUAGE, "CN");

            Request request = new Request.Builder()
                    .post(body) //请求参数
                    .addHeader(HttpUtils.LANG, cn)
                    .addHeader(HttpUtils.USERID, userid)
                    .addHeader(HttpUtils.USERTOKEN, userToken)
                    .url(url)
                    .build();
            Call call = httpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // 错误关闭弹框
                    if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
                        dismissDialog();
                    onBaseDataListener.onError(activity.getResources().getString(R.string.http_check));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // 成功关闭弹框
                    if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
                        dismissDialog();
                    String jsonStr = response.body().string();
                    CommonJsonToBean<T> bean = null;
                    try {
                        bean = CommonJsonToBean.fromJson(jsonStr, tradingInfoBeanClass);
                    } catch (Exception e) {
                        KLog.d("okGo_onSuccess   Exception == " + e.getMessage());
                    }
                    if (bean != null) {
                        onBaseDataListener.onNewData(bean);
                        if (bean.getCode() == -1) {
                            Intent intent = new Intent(activity, LoginActivity.class);
                            intent.putExtra(AppConstants.LOGIN_FAILURE, true);
                            intent.putExtra(AppConstants.LOGINTYPE, AppConstants.LOGINPHONE);
                            activity.startActivity(intent);
                            SPUtils.remove(activity, SPConstant.LOGIN_STATE);
                            SPUtils.remove(activity, SPConstant.USER_ID);
                            SPUtils.remove(activity, SPConstant.USER_TOKEN);
                            SPUtils.remove(activity, SPConstant.USER_ACCOUNT);
                            SPUtils.remove(activity, SPConstant.USER_CHOOSECOUNTRIES);

                            //发送token失效通知
                            EventBus eventBus = OAXApplication.getmEventBus();
                            MyEvents myEvents = new MyEvents();
                            myEvents.status = MyEvents.status_ok;
                            myEvents.status_type = MyEvents.Token_failure;
                            eventBus.post(myEvents);
                            Logger.e(TAG, "发送token失效通知!");

                        }
                    } else {
                        onBaseDataListener.onError(activity.getResources().getString(R.string.load_error));
                    }
                }
            });


        } catch (Exception e) {
            e.getMessage();
        }
    }


    /**
     * 普通的post请求 返回数据结构
     * {private String code;private boolean success;private String msg;private List<DataBean> data;}
     *
     * @param url
     * @param params
     * @param activity
     * @param onDataListListener
     * @param clazz
     * @param state
     */
    public void commonPostList(String url, Map<String, Object> params, Activity activity, OnDataListListener<T> onDataListListener, Class<T> clazz, final RequestState state) {
        //判断弹框的显示状态 是否显示等等
        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
            showDialog(activity);
        KLog.d("okGo_upLoad", "upload:-->" + url + params);
        //判断用户ID是否存在
        String userid = (String) SPUtils.getParamString(activity, SPConstant.USER_ID, null);
        KLog.d("okGo_upLoad", "userid = " + userid);
        //判断用户Token是否存在
        String userToken = (String) SPUtils.getParam(activity, SPConstant.USER_TOKEN, "");
        KLog.d("okGo_upLoad", "userToken = " + userToken);

        JSONObject jsonObject = new JSONObject();
        if (params != null) {
            jsonObject = new JSONObject(params);
        }

        String cn = (String) SPUtils.getParam(activity, SPConstant.LANGUAGE, "CN");
        cn = cn.toLowerCase();
        Logs.s("    语言适配 12   " + cn);
        PostRequest request = OkGo.<String>post(url)
                .upJson(jsonObject)
                .headers(HttpUtils.USERID, userid)
                .headers(HttpUtils.USERTOKEN, userToken)
                .headers(HttpUtils.LANG, cn)
                .tag(activity)
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)
                .cacheKey(url);
        request.execute(new StringCallBackList(activity, clazz, onDataListListener, url, state));
    }

    /**
     * 回调  StringCallback实现
     */
    private class StringCallBack extends StringCallback {
        private Activity activity;
        private Class<T> mClass;
        private OnDataListener<T> onBaseDataListener;
        private String url;
        private RequestState state;

        /**
         * @param activity activity
         * @param clazz    解析类
         * @param
         * @param url      地址
         */
        public StringCallBack(Activity activity, Class<T> clazz, OnDataListener<T> onBaseDataListener, String url, RequestState state) {
            this.activity = activity;
            mClass = clazz;
            this.onBaseDataListener = onBaseDataListener;
            this.url = url;
            this.state = state;
        }

        /**
         * 成功
         *
         * @param s        返回json
         * @param call     回调
         * @param response 返回response
         */
        @Override
        public void onSuccess(String s, Call call, Response response) {
            // 成功关闭弹框
            if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
                dismissDialog();

            KLog.d("okGo_onSuccess", "HttpData-->" + url + s);

            CommonJsonToBean<T> bean = null;

            try {
                bean = CommonJsonToBean.fromJson(s, mClass);
            } catch (Exception e) {
                KLog.d("okGo_onSuccess   Exception == " + e.getMessage());
            }

            if (bean != null) {
                onBaseDataListener.onNewData(bean);
                if (bean.getCode() == -1) {
                    Intent intent = new Intent(activity, LoginActivity.class);
                    intent.putExtra(AppConstants.LOGIN_FAILURE, true);
                    intent.putExtra(AppConstants.LOGINTYPE, AppConstants.LOGINPHONE);
                    activity.startActivity(intent);
                    SPUtils.remove(activity, SPConstant.LOGIN_STATE);
                    SPUtils.remove(activity, SPConstant.USER_ID);
                    SPUtils.remove(activity, SPConstant.USER_TOKEN);
                    SPUtils.remove(activity, SPConstant.USER_ACCOUNT);
                    SPUtils.remove(activity, SPConstant.USER_CHOOSECOUNTRIES);

                    //发送token失效通知
                    EventBus eventBus = OAXApplication.getmEventBus();
                    MyEvents myEvents = new MyEvents();
                    myEvents.status = MyEvents.status_ok;
                    myEvents.status_type = MyEvents.Token_failure;
                    eventBus.post(myEvents);
                    Logger.e(TAG, "发送token失效通知!");

                }
            } else {
                onBaseDataListener.onError(activity.getResources().getString(R.string.load_error));
            }
        }

        /**
         * @param call     回调
         * @param response 返回Response
         * @param e        异常
         */
        @Override
        public void onError(Call call, Response response, Exception e) {
            super.onError(call, response, e);
            // 错误关闭弹框
            if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
                dismissDialog();
            KLog.e("okGo_onError", "HttpDataError-->" + e.getMessage());
            onBaseDataListener.onError(activity.getResources().getString(R.string.http_check));
        }
    }


    /**
     * 回调  StringCallBackList
     */
    private class StringCallBackList extends StringCallback {
        private Activity activity;
        private Class<T> mClass;
        private OnDataListListener<T> onDataListener;
        private String url;
        private RequestState state;

        /**
         * @param activity activity
         * @param clazz    解析类
         * @param
         * @param url      地址
         */
        public StringCallBackList(Activity activity, Class<T> clazz, OnDataListListener<T> onDataListener, String url, RequestState state) {
            this.activity = activity;
            mClass = clazz;
            this.onDataListener = onDataListener;
            this.url = url;
            this.state = state;
        }

        /**
         * 成功
         *
         * @param s        返回json
         * @param call     回调
         * @param response 返回response
         */
        @Override
        public void onSuccess(String s, Call call, Response response) {
            // 成功关闭弹框
            if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
                dismissDialog();

            KLog.d("okGo_onSuccess", "HttpData-->" + url + s);

            CommonJsonToList<T> bean = null;
            try {
                bean = CommonJsonToList.fromJson(s, mClass);
            } catch (Exception e) {
                KLog.d("okGo_onSuccess   Exception == " + e.getMessage());
            }
            if (bean != null) {
                onDataListener.onNewData(bean);
                if ("-1".equals(bean.getCode())) {
                    Intent intent = new Intent(activity, LoginActivity.class);
                    intent.putExtra(AppConstants.LOGIN_FAILURE, true);
                    intent.putExtra(AppConstants.LOGINTYPE, AppConstants.LOGINPHONE);
                    activity.startActivity(intent);
                    SPUtils.remove(activity, SPConstant.LOGIN_STATE);
                    SPUtils.remove(activity, SPConstant.USER_ID);
                    SPUtils.remove(activity, SPConstant.USER_TOKEN);
                    SPUtils.remove(activity, SPConstant.USER_ACCOUNT);
                    SPUtils.remove(activity, SPConstant.USER_CHOOSECOUNTRIES);

                    //发送token失效通知
                    EventBus eventBus = OAXApplication.getmEventBus();
                    MyEvents myEvents = new MyEvents();
                    myEvents.status = MyEvents.status_ok;
                    myEvents.status_type = MyEvents.Token_failure;
                    eventBus.post(myEvents);
                    Logger.e(TAG, "发送token失效通知!");

                }
            } else {
                onDataListener.onError(activity.getResources().getString(R.string.load_error));

            }
        }

        /**
         * @param call     回调
         * @param response 返回Response
         * @param e        异常
         */
        @Override
        public void onError(Call call, Response response, Exception e) {
            super.onError(call, response, e);
            // 错误关闭弹框
            if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
                dismissDialog();

            Logger.e("okGo_onError", "HttpDataError-->" + e.getMessage());
            onDataListener.onError(activity.getResources().getString(R.string.http_check));
        }
    }


    /**
     * 成功失败回调
     *
     * @param <T>
     */
    public interface OnCallBack<T> {
        void success(T t);

        void onError(String msg);
    }

    /**
     * 网络加载弹框
     */
    public void showDialog(Activity activity) {
        try {
            if (dialog == null || !dialog.isShowing()) {
                dialog = KProgressHUD.create(activity)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setCancellable(true)
                        .setAnimationSpeed(2)
                        .setDimAmount(0.5f)
                        .show();
            }
        } catch (Exception e) {
            KLog.d("Exception showDialog = " + e.getMessage());
        }

    }


    /**
     * 关闭网络加载弹框
     */
    public void dismissDialog() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
                dialog = null;
            }
        } catch (Exception e) {
            KLog.d("Exception dismissDialog = " + e.getMessage());
        }

    }

    /**
     * 只支持一个参数
     *
     * @param url
     * @param param
     * @param activity
     * @param onBaseDataListener
     * @param clazz
     * @param state
     */
    public void commonGet(String url, String param, Activity activity, OnDataListener<T> onBaseDataListener, Class<T> clazz, final RequestState state) {
        //判断弹框的显示状态 是否显示等等
        if (state == RequestState.STATE_DIALOG || state == RequestState.STATE_ALL_SCREEN_AND_DIALOG)
            showDialog(activity);
        KLog.d("okGo_upLoad", "upload:-->" + url + param);
        //判断用户ID是否存在
        String userid = (String) SPUtils.getParamString(activity, SPConstant.USER_ID, null);
        KLog.d("okGo_upLoad", "userid = " + userid);
        //判断用户Token是否存在
        String userToken = (String) SPUtils.getParam(activity, SPConstant.USER_TOKEN, "");
        KLog.d("okGo_upLoad", "userToken = " + userToken);

        String cn = (String) SPUtils.getParam(activity, SPConstant.LANGUAGE, "CN");
        cn = cn.toLowerCase();
        GetRequest request = OkGo.<String>get(url + param)
                .headers(HttpUtils.LANG, cn)
                .headers(HttpUtils.USERID, userid)
                .headers(HttpUtils.USERTOKEN, userToken)
                .tag(activity)
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)
                .cacheKey(url);
        request.execute(new StringCallBack(activity, clazz, onBaseDataListener, url, state));
    }
}
