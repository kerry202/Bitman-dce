package cn.dagongniu.bitman.account.module;

import android.app.Activity;

import java.util.HashMap;

import cn.dagongniu.bitman.account.bean.MyEarningsDataBean;
import cn.dagongniu.bitman.base.OAXBaseModule;
import cn.dagongniu.bitman.https.CommonJsonToBean;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.HttpUtils;
import cn.dagongniu.bitman.https.OnDataListener;
import cn.dagongniu.bitman.https.RequestState;

public class MyEarningsModule extends OAXBaseModule<HashMap<String, String>, MyEarningsDataBean> {

    public MyEarningsModule(Activity activity) {
        super(activity);
    }

    @Override
    public void requestServerData(OnDataListener<MyEarningsDataBean> onDataListener, RequestState state, HashMap<String, Object> params) {
        HttpUtils.getInstance().commonPost(Http.MyEarnings, params, activity, new OnDataListener<MyEarningsDataBean>() {

            @Override
            public void onNewData(CommonJsonToBean<MyEarningsDataBean> data) {
                onDataListener.onNewData(data);
            }

            @Override
            public void onError(String code) {
                onDataListener.onError(code);
            }
        }, MyEarningsDataBean.class, state);
    }
}
