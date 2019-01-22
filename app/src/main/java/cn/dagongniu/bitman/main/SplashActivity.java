package cn.dagongniu.bitman.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gyf.barlibrary.ImmersionBar;
import com.socks.library.KLog;

import java.util.Locale;

import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.account.LoginActivity;
import cn.dagongniu.bitman.base.OAXIViewBean;
import cn.dagongniu.bitman.constant.SPConstant;
import cn.dagongniu.bitman.constant.UMConstant;
import cn.dagongniu.bitman.customview.LoadingState;
import cn.dagongniu.bitman.https.CommonJsonToBean;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.language.LanguageType;
import cn.dagongniu.bitman.language.MultiLanguageUtil;
import cn.dagongniu.bitman.main.bean.VersionInfoBean;
import cn.dagongniu.bitman.main.presenter.MainPresenter;
import cn.dagongniu.bitman.utils.Logs;
import cn.dagongniu.bitman.utils.SkipActivityUtil;
import cn.dagongniu.bitman.utils.SPUtils;
import cn.dagongniu.bitman.utils.ToastUtil;
import cn.dagongniu.bitman.utils.um.UMManager;
import customview.ConfirmDialog;
import feature.Callback;
import util.UpdateAppUtils;

public class SplashActivity extends AppCompatActivity implements OAXIViewBean<VersionInfoBean> {

    Bundle bundle;
    private MainPresenter mMainPresenter;
    String apkPath;
    private String mVersionName;
    private boolean mISLoop = false;
    private boolean mISUpdate = true;
    private boolean isGoTo = false;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            mISLoop = true;
            if (!mISUpdate) {
                KLog.d("handleMessage goTo");
                goTo();
            }
        }
    };
    private long createTime;
    private long endTime;
    private String description;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (!isTaskRoot()) {
            finish();
            return;
        }


        ImageView my_gif = findViewById(R.id.my_gif);

        Glide.with(this)
                .load(R.drawable.splash_icon)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        if (resource instanceof GifDrawable) {
                            GifDrawable gifDrawable = (GifDrawable) resource;
                            gifDrawable.setLoopCount(1);
                            my_gif.setImageDrawable(resource);
                            gifDrawable.start();
                        }
                    }
                });

        setDefaultLanguage();
        bundle = new Bundle();
        ImmersionBar.with(this)
                .statusBarDarkFont(true)
                .transparentBar()
                .init();


        createTime = System.currentTimeMillis();

        mMainPresenter = new MainPresenter(this);
        mMainPresenter.checkAppUpdateInfo(null);

    }

    private void goTo() {
        isGoTo = true;
        boolean isFirstLaunched = (boolean) SPUtils.getParam(this, SPConstant.IS_FIRST_LAUNCHED, true);
        String userid = SPUtils.getParamString(this, SPConstant.USER_ID, null);
        if (userid == null) {
            SkipActivityUtil.skipAnotherActivity(this, LoginActivity.class, true);
        } else {
            if (isFirstLaunched) {
                SkipActivityUtil.skipAnotherActivity(this, MainActivity.class, true);
            } else {
                SkipActivityUtil.skipAnotherActivity(this, MainActivity.class, true);
            }
        }
    }

    private void setDefaultLanguage() {

        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = getResources().getConfiguration().locale;
        }
        //或者仅仅使用 locale = Locale.getDefault(); 不需要考虑接口 deprecated(弃用)问题
        String lang = locale.getLanguage();
        KLog.d("lang = lang " + lang);
        String LANGUAGE = (String) SPUtils.getParam(this, SPConstant.LANGUAGE, "");

        KLog.d("lang = LANGUAGE " + LANGUAGE);
        if (!TextUtils.isEmpty(LANGUAGE)) {
            if ("CN".equalsIgnoreCase(LANGUAGE)) {
                MultiLanguageUtil.getInstance().updateLanguage(LanguageType.LANGUAGE_CHINESE_SIMPLIFIED);
            } else if ("TW".equalsIgnoreCase(LANGUAGE) || "ZH".equalsIgnoreCase(LANGUAGE)) {
                MultiLanguageUtil.getInstance().updateLanguage(LanguageType.LANGUAGE_CHINESE_TRADITIONAL);
            } else if ("TH".equalsIgnoreCase(LANGUAGE) || "FR".equalsIgnoreCase(LANGUAGE)) {
                MultiLanguageUtil.getInstance().updateLanguage(LanguageType.LANGUAGE_TY_TRADITIONAL);
            } else {
                MultiLanguageUtil.getInstance().updateLanguage(LanguageType.LANGUAGE_EN);
            }
        } else {
            if ("CN".equalsIgnoreCase(lang)) {
                MultiLanguageUtil.getInstance().updateLanguage(LanguageType.LANGUAGE_CHINESE_SIMPLIFIED);
            } else if ("TW".equalsIgnoreCase(lang)) {
                MultiLanguageUtil.getInstance().updateLanguage(LanguageType.LANGUAGE_CHINESE_TRADITIONAL);
            } else if ("TH".equalsIgnoreCase(lang) || "FR".equalsIgnoreCase(lang)) {
                MultiLanguageUtil.getInstance().updateLanguage(LanguageType.LANGUAGE_TY_TRADITIONAL);
            } else if ("ZH".equalsIgnoreCase(lang)) {
                MultiLanguageUtil.getInstance().updateLanguage(LanguageType.LANGUAGE_CHINESE_SIMPLIFIED);
            } else {
                MultiLanguageUtil.getInstance().updateLanguage(LanguageType.LANGUAGE_EN);
            }
        }
    }

    private void checkAndUpdate(String des) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            updateByVersionName(des);
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                updateByVersionName(des);
            } else {//申请权限
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    //根据versionName判断跟新
    private void updateByVersionName(String des) {
        UpdateAppUtils.from(this)
                .checkBy(UpdateAppUtils.CHECK_BY_VERSION_NAME)
                .serverVersionName(mVersionName)
                .apkPath(apkPath)
                .downloadBy(UpdateAppUtils.DOWNLOAD_BY_APP)
                .isForce(true)
                .update(des);
    }

    //权限请求结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateByVersionName(description);
                } else {
                    new ConfirmDialog(this, description, new Callback() {
                        @Override
                        public void callback(int position) {

                            if (position == 1) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName())); // 根据包名打开对应的设置界面
                                startActivity(intent);
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                System.exit(0);
                            } else {
                                System.exit(0);
                            }
                        }
                    }).show();
                }
                break;
        }
    }

    private String localVersionName;
    private int localVersionCode;

    //获取apk的版本号 currentVersionCode
    private void getAPPLocalVersion() {
        PackageManager manager = getContext().getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(getContext().getPackageName(), 0);
            localVersionName = info.versionName; // 版本名
            localVersionCode = info.versionCode; // 版本号
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void setData(CommonJsonToBean<VersionInfoBean> data) {

        endTime = System.currentTimeMillis();
        long second = endTime - createTime;
        if (Http.isUpdate) {
            if (data != null) {
                KLog.d("VersionInfoBean = " + data.toJson(VersionInfoBean.class));
                if (data.getSuccess()) {
                    mISUpdate = true;
                    apkPath = data.getData().getDownloadUrl();
                    mVersionName = data.getData().getVersion();
                    description = data.getData().getDescription();

                    getAPPLocalVersion();

                    String s1 = mVersionName.replace(".", "");
                    String s2 = localVersionName.replace(".", "");

                    int i1 = Integer.parseInt(s1);
                    int i2 = Integer.parseInt(s2);
                    if (i1 > i2) {
                        Logs.s("    更新1 1:   serverversion：" + s1 + "// localversion: " + s2);
                        checkAndUpdate(description);
                    } else {
                        mISUpdate = false;
                        Logs.s("    更新1 2:   serverversion：" + s1 + "// localversion: " + s2);
                        if (second > 2000) {
                            mHandler.sendEmptyMessageDelayed(1, 0);
                        } else {
                            long i = 2000 - second;
                            mHandler.sendEmptyMessageDelayed(1, i);
                        }
                    }
                } else {
                    mISUpdate = false;

                    if (second > 2000) {
                        mHandler.sendEmptyMessageDelayed(1, 0);
                    } else {
                        long i = 2000 - second;
                        mHandler.sendEmptyMessageDelayed(1, i);
                    }
                    if (mISLoop && !isGoTo) {

                    }
                }
            }
        } else {
            mISUpdate = false;
            if (second > 2000) {
                mHandler.sendEmptyMessageDelayed(1, 10);
            } else {
                long i = 2000 - second;
                mHandler.sendEmptyMessageDelayed(1, i);
            }
        }
    }

    @Override
    public void setRefresh(CommonJsonToBean<VersionInfoBean> data) {

    }

    @Override
    public void setLoadMore(CommonJsonToBean<VersionInfoBean> data) {

    }

    @Override
    public void showToast(String msg) {

    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void setXState(LoadingState xState, String msg) {

        ToastUtil.ShowToast(getResources().getString(R.string.connect_server_fail));
    }

    @Override
    protected void onResume() {
        super.onResume();
        UMManager.onResume(this, UMConstant.SplashActivity);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UMManager.onPause(this, UMConstant.SplashActivity);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
        if (mHandler != null) {
            mHandler.removeMessages(1);
            mHandler = null;
        }
        if (mMainPresenter != null) {
            mMainPresenter.onDestroy();
            mMainPresenter = null;
        }
    }
}
