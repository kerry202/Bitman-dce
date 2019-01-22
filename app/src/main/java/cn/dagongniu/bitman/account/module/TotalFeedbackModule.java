package cn.dagongniu.bitman.account.module;

import android.app.Activity;

import java.util.HashMap;

import cn.dagongniu.bitman.account.bean.TotalFeedbackDataBean;
import cn.dagongniu.bitman.base.OAXBaseModule;
import cn.dagongniu.bitman.https.CommonJsonToBean;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.HttpUtils;
import cn.dagongniu.bitman.https.OnDataListener;
import cn.dagongniu.bitman.https.RequestState;

public class TotalFeedbackModule extends OAXBaseModule<HashMap<String, String>, TotalFeedbackDataBean> {

    public TotalFeedbackModule(Activity activity) {
        super(activity);
    }

    @Override
    public void requestServerData(OnDataListener<TotalFeedbackDataBean> onDataListener, RequestState state, HashMap<String, Object> params) {
        HttpUtils.getInstance().commonPost(Http.Total_Feedback, params, activity, new OnDataListener<TotalFeedbackDataBean>() {

            @Override
            public void onNewData(CommonJsonToBean<TotalFeedbackDataBean> data) {
                onDataListener.onNewData(data);
            }

            @Override
            public void onError(String code) {
                onDataListener.onError(code);
            }
        }, TotalFeedbackDataBean.class, state);
    }
}
