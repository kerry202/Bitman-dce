package cn.dagongniu.bitman.main.module;

import android.app.Activity;

import java.util.HashMap;

import cn.dagongniu.bitman.base.OAXBaseModule;
import cn.dagongniu.bitman.https.CommonJsonToBean;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.HttpUtils;
import cn.dagongniu.bitman.https.OnDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.main.bean.VersionInfoBean;
import cn.dagongniu.bitman.utils.Logs;

public class AppVersionModule extends OAXBaseModule<HashMap<String, Object>, VersionInfoBean> {
    public AppVersionModule(Activity activity) {
        super(activity);
    }

    @Override
    public void requestServerData(final OnDataListener<VersionInfoBean> onBaseDataListener, RequestState state, HashMap<String, Object> params) {
        HttpUtils.getInstance().commonPost(Http.APP_CHECKVERSION, params, activity, new OnDataListener<VersionInfoBean>() {

            @Override
            public void onNewData(CommonJsonToBean<VersionInfoBean> data) {
                onBaseDataListener.onNewData(data);
                Logs.s("     requestServerData  onError    "+data);
            }

            @Override
            public void onError(String code) {
                Logs.s("     requestServerData  onError    "+code);
                onBaseDataListener.onError(code);
            }
        }, VersionInfoBean.class, state);
    }
}
