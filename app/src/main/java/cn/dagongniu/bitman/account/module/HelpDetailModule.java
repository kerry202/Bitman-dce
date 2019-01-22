package cn.dagongniu.bitman.account.module;

import android.app.Activity;

import java.util.HashMap;

import cn.dagongniu.bitman.account.bean.HelpDetailInfoBean;
import cn.dagongniu.bitman.base.OAXBaseModule;
import cn.dagongniu.bitman.https.CommonJsonToBean;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.HttpUtils;
import cn.dagongniu.bitman.https.OnDataListener;
import cn.dagongniu.bitman.https.RequestState;

public class HelpDetailModule extends OAXBaseModule<HashMap<String, String>, HelpDetailInfoBean> {

    public HelpDetailModule(Activity activity) {
        super(activity);
    }


    @Override
    public void requestServerData(final OnDataListener<HelpDetailInfoBean> onBaseDataListener, RequestState state, HashMap<String, Object> params) {
        HttpUtils.getInstance().commonPost(Http.HELP_CENTER_READDETAIL, params, activity, new OnDataListener<HelpDetailInfoBean>() {

            @Override
            public void onNewData(CommonJsonToBean<HelpDetailInfoBean> data) {
                onBaseDataListener.onNewData(data);
            }

            @Override
            public void onError(String code) {
                onBaseDataListener.onError(code);
            }
        }, HelpDetailInfoBean.class, state);
    }
}
