package cn.dagongniu.bitman.main.presenter;

import android.app.Activity;
import android.content.pm.PackageManager;

import java.util.HashMap;

import cn.dagongniu.bitman.OAXApplication;
import cn.dagongniu.bitman.base.OAXBasePresenter;
import cn.dagongniu.bitman.base.OAXIViewBean;
import cn.dagongniu.bitman.constant.Constant;
import cn.dagongniu.bitman.customview.LoadingState;
import cn.dagongniu.bitman.https.CommonJsonToBean;
import cn.dagongniu.bitman.https.OnDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.https.UrlParams;
import cn.dagongniu.bitman.main.bean.VersionInfoBean;
import cn.dagongniu.bitman.main.module.AppVersionModule;
import cn.dagongniu.bitman.utils.Logs;

public class MainPresenter extends OAXBasePresenter {
    private AppVersionModule mAppVersionModule;
    private Activity activity;
    OAXIViewBean view;
    RequestState mState;

    public MainPresenter(OAXIViewBean view) {
        super(view);
        activity = (Activity) view.getContext();
        this.view = view;
        mAppVersionModule = new AppVersionModule(activity);
    }


    public void checkAppUpdateInfo(RequestState state) {
        this.mState = state;
        String versionName = "";
        try {
            versionName = OAXApplication.getInstance().getPackageManager().getPackageInfo
                    (OAXApplication.getInstance().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            versionName = "";
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put(UrlParams.type, Constant.ANDROID_TYPE);
        map.put(UrlParams.version, versionName);
        mAppVersionModule.requestServerData(new OnDataListener<VersionInfoBean>() {
            @Override
            public void onNewData(CommonJsonToBean<VersionInfoBean> data) {
                if (view != null) {
                    Logs.s("  msgmsgmsg onNewData  " + data);
                    view.setData(data);
                }
            }

            @Override
            public void onError(String msg) {
                if (view != null) {
                    Logs.s("  msgmsgmsg onError " + msg);
                    view.setXState(LoadingState.STATE_ERROR, msg);
                }
            }
        }, state, map);
    }

    public void onDestroy() {
        mAppVersionModule = null;
        activity = null;
        view = null;
    }
}
