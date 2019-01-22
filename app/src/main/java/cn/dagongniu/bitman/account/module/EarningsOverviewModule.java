package cn.dagongniu.bitman.account.module;

import android.app.Activity;

import java.util.HashMap;

import cn.dagongniu.bitman.account.bean.EarningsOverviewDataBean;
import cn.dagongniu.bitman.base.OAXBaseModule;
import cn.dagongniu.bitman.https.CommonJsonToBean;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.HttpUtils;
import cn.dagongniu.bitman.https.OnDataListener;
import cn.dagongniu.bitman.https.RequestState;

public class EarningsOverviewModule extends OAXBaseModule<HashMap<String, String>, EarningsOverviewDataBean> {

    public EarningsOverviewModule(Activity activity) {
        super(activity);
    }

    @Override
    public void requestServerData(OnDataListener<EarningsOverviewDataBean> onDataListener, RequestState state, HashMap<String, Object> params) {
        HttpUtils.getInstance().commonPost(Http.overview, params, activity, new OnDataListener<EarningsOverviewDataBean>() {

            @Override
            public void onNewData(CommonJsonToBean<EarningsOverviewDataBean> data) {
                onDataListener.onNewData(data);
            }

            @Override
            public void onError(String code) {
                onDataListener.onError(code);
            }
        }, EarningsOverviewDataBean.class, state);
    }
}
