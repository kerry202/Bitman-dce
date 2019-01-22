package cn.dagongniu.bitman.account.presenter;

import android.app.Activity;

import com.google.gson.Gson;
import com.socks.library.KLog;

import java.util.HashMap;

import cn.dagongniu.bitman.account.bean.TotalFeedbackDataBean;
import cn.dagongniu.bitman.account.module.TotalFeedbackModule;
import cn.dagongniu.bitman.base.OAXBasePresenter;
import cn.dagongniu.bitman.base.OAXIViewBean;
import cn.dagongniu.bitman.customview.LoadingState;
import cn.dagongniu.bitman.https.CommonJsonToBean;
import cn.dagongniu.bitman.https.OnDataListener;
import cn.dagongniu.bitman.https.RequestState;

public class TotalFeedbackPresenter extends OAXBasePresenter {

    private TotalFeedbackModule mModule;
    private Activity activity;
    OAXIViewBean view;
    RequestState mState;

    public TotalFeedbackPresenter(OAXIViewBean view) {
        super(view);
        activity = (Activity) view.getContext();
        this.view = view;
        mModule = new TotalFeedbackModule(activity);
    }

    public void getData(HashMap<String, Object> params, RequestState state) {
        this.mState = state;
        mModule.requestServerData(new OnDataListener<TotalFeedbackDataBean>() {
            @Override
            public void onNewData(CommonJsonToBean<TotalFeedbackDataBean> data) {
                KLog.d("onNewData == " + new Gson().toJson(data));
                if (data.getSuccess()) {
                    view.setData(data);
                } else {
                    view.setXState(LoadingState.STATE_ERROR, data.getMsg());
                }
            }

            @Override
            public void onError(String msg) {
                view.setXState(LoadingState.STATE_ERROR, msg);
            }
        }, state, params);
    }
}
