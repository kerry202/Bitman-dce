package cn.dagongniu.bitman.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.account.LoginActivity;
import cn.dagongniu.bitman.account.fragment.MineFragment;
import cn.dagongniu.bitman.assets.fragment.AssetsFragment;
import cn.dagongniu.bitman.base.OAXBaseActivity;
import cn.dagongniu.bitman.bitman.BitmanHomeFragment;
import cn.dagongniu.bitman.bitman.BitmanThorwsFragment;
import cn.dagongniu.bitman.bitman.StarListBean;
import cn.dagongniu.bitman.bitmanviews.Utils;
import cn.dagongniu.bitman.constant.SPConstant;
import cn.dagongniu.bitman.constant.UMConstant;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.HttpUtils;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.utils.AppConstants;
import cn.dagongniu.bitman.utils.Logs;
import cn.dagongniu.bitman.utils.SPUtils;
import cn.dagongniu.bitman.utils.ToastUtil;
import cn.dagongniu.bitman.utils.um.UMManager;

/**
 * 我的
 */
public class MainActivity extends OAXBaseActivity {

    private static final String TAG = "MainActivity";

    private long firstTime = 0;

    @BindView(R.id.fragment)
    FrameLayout fragment;

    @BindView(R.id.home)
    RadioButton home;

    @BindView(R.id.cls)
    RadioButton cls;

    @BindView(R.id.find)
    RadioButton find;

    @BindView(R.id.my)
    RadioButton my;

    @BindView(R.id.rg)
    RadioGroup rg;

    BitmanThorwsFragment oaxTradingFragment;
    BitmanHomeFragment oaxHomeNFragment;
    public AssetsFragment assetsFragment;//资产
    MineFragment mineFragment;

    Bundle bundle;

    //static 代码段可以防止内存泄露
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
                return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }

    private FragmentTransaction ft;
    private FragmentManager fm;
    private int indcotr = 1;
    private StarListBean starListBean;

    @Override
    protected int getLayoutId() {

        return R.layout.activity_main;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long secondTime = System.currentTimeMillis();

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (secondTime - firstTime < 2000) {
                System.exit(0);
            } else {
                ToastUtil.ShowToast(getResources().getString(R.string.exit_app_str));
                firstTime = System.currentTimeMillis();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) MainActivity.this
                    .getSystemService(mContext.INPUT_METHOD_SERVICE);
            if (v != null) {
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @OnClick({R.id.home, R.id.cls, R.id.find, R.id.my})
    public void onViewClicked(View view) {
        ft = fm.beginTransaction();
//        oaxHomeNFragment.settext();

        switch (view.getId()) {
            case R.id.home:
                indcotr = 1;
//                ft.show(oaxTradingFragment)
//                        .hide(oaxHomeNFragment)
//                        .hide(assetsFragment)
//                        .hide(mineFragment).commit();
                break;
            case R.id.cls:
//                indcotr = 2;
//                ft.show(oaxHomeNFragment)
//                        .hide(oaxTradingFragment)
//                        .hide(assetsFragment)
//                        .hide(mineFragment).commit();
                break;
            case R.id.find:
                String userId = SPUtils.getParamString(mContext, SPConstant.USER_ID, null);
                if (userId != null) {
                    indcotr = 3;
                    ft.show(assetsFragment)
                            .hide(mineFragment).commit();
                } else {
                    if (indcotr == 1) {
                        rg.check(R.id.home);
                    } else if (indcotr == 2) {
                        rg.check(R.id.cls);
                    } else if (indcotr == 4) {
                        rg.check(R.id.my);
                    }
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.putExtra(AppConstants.LOGINTYPE, AppConstants.LOGINPHONE);
                    startActivity(intent);
                }

                break;
            case R.id.my:
                indcotr = 4;
                ft.show(mineFragment)
                        .hide(assetsFragment).commit();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logs.s("  生命周期   onPause  ");
        UMManager.onResume(this, UMConstant.MainActivity);

    }

    public void showHide(int state) {
        if (state == 0) {
            ft = fm.beginTransaction();
            ft.show(oaxHomeNFragment)
                    .hide(oaxTradingFragment).commit();
        } else {
            ft = fm.beginTransaction();
            ft.show(oaxTradingFragment)
                    .hide(oaxHomeNFragment).commit();
        }
    }

    @Override
    protected void initView() {
        super.initView();

        getStarListDate();

        rg.check(R.id.home);

//        oaxHomeNFragment = new OaxHomeNFragment();
        oaxHomeNFragment = new BitmanHomeFragment();
        oaxTradingFragment = new BitmanThorwsFragment();
//        oaxTradingFragment = new OaxTradingFragment();
//        assetsFragment = new AssetsFragment();
//        mineFragment = new MineFragment();
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        ft.add(R.id.fragment, oaxHomeNFragment);
        ft.add(R.id.fragment, oaxTradingFragment);
//        ft.add(R.id.fragment, assetsFragment);
//        ft.add(R.id.fragment, mineFragment);

        ft.show(oaxHomeNFragment)
                .hide(oaxTradingFragment).commit();


        bundle = new Bundle();
        /**
         * 语言切换
         */
        if (getIntent().getBooleanExtra("Language", false)) {

        }


    }

    private void getStarListDate() {

        String cn = (String) SPUtils.getParam(mContext, SPConstant.LANGUAGE, "CN");
        cn = cn.toLowerCase();
        HashMap<String, Object> hashMap = new HashMap();
        String userid = SPUtils.getParamString(mContext, SPConstant.USER_ID, null);
        String userToken = SPUtils.getParamString(mContext, SPConstant.USER_TOKEN, null);

        hashMap.put("language", cn);
        hashMap.put("deviceType", Http.device);
        hashMap.put("apiVersion", Utils.getVersionCode(mContext));
        hashMap.put("accessToken", userToken);
        hashMap.put("userId", userid);
        hashMap.put("loginProject", Http.projectName);
        hashMap.put("token", userToken);
        hashMap.put("page", "1");
        hashMap.put("pageSize", "20");
        hashMap.put("starId", "1");
        hashMap.put("quadrantId", "1");

        HttpUtils.getInstance().bitmanDate(Http.getStarList, hashMap, this, new OnBaseDataListener<String>() {
            @Override
            public void onNewData(String data) {
                Logs.s(" bitmaninterface 星球列表onNewData: " + data);
                starListBean = new Gson().fromJson(data, StarListBean.class);
                StarListBean.DataBean data1 = starListBean.data;
                if (data1 != null) {
                    List<StarListBean.DataBean.ListBean> list = data1.list;
                    StarListBean.DataBean.ListBean listBean = list.get(0);

                    oaxHomeNFragment.setDatas(listBean);
                    oaxTradingFragment.setDatas(listBean);

//                    userPrice.setText(list.get(0).reserves);
//                    startStarIv.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(mContext, BitmanThorwActivity.class);
//                            Bundle bundle = new Bundle();
//                            bundle.putSerializable("startlistbean", starListBean);
//                            intent.putExtras(bundle);
//                            startActivity(intent);
//                        }
//                    });
                }

            }

            @Override
            public void onError(String code) {
                Logs.s(" bitmaninterface 星球列表onNewData: " + code);
            }
        }, RequestState.STATE_REFRESH);
    }


    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.s("  生命周期   onCreate  ");
    }


    @Override
    protected void onResume() {
        super.onResume();
//        oaxHomeNFragment.settext();
//        买
        Logs.s("  生命周期   onResume  ");
        UMManager.onResume(this, UMConstant.MainActivity);
    }


    @Override
    protected void onDestroy() {
        Logs.s("  生命周期   onDestroy  ");
        super.onDestroy();
//        解决更换系统设置后打开app首页UI问题（语言or字体大小）
        finishAffinity();
    }

    protected void openActivity(Class<?> cls, Bundle bundle) {
        Intent i = new Intent(mContext, cls);
        i.putExtras(bundle);
        startActivity(i);
    }
}
