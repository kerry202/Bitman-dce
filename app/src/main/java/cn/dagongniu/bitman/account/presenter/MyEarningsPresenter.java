package cn.dagongniu.bitman.account.presenter;

import android.app.Activity;

import com.google.gson.Gson;
import com.socks.library.KLog;

import java.util.HashMap;

import cn.dagongniu.bitman.account.bean.MyEarningsDataBean;
import cn.dagongniu.bitman.account.module.MyEarningsModule;
import cn.dagongniu.bitman.base.OAXBasePresenter;
import cn.dagongniu.bitman.base.OAXIViewBean;
import cn.dagongniu.bitman.customview.LoadingState;
import cn.dagongniu.bitman.https.CommonJsonToBean;
import cn.dagongniu.bitman.https.OnDataListener;
import cn.dagongniu.bitman.https.RequestState;

public class MyEarningsPresenter extends OAXBasePresenter {

    private MyEarningsModule mMyEarningsModule;
    private Activity activity;
    OAXIViewBean view;
    RequestState mState;

    public MyEarningsPresenter(OAXIViewBean view) {
        super(view);
        activity = (Activity) view.getContext();
        this.view = view;
        mMyEarningsModule = new MyEarningsModule(activity);
    }

    public void getData(HashMap<String, Object> params, RequestState state) {
        this.mState = state;
        mMyEarningsModule.requestServerData(new OnDataListener<MyEarningsDataBean>() {

            @Override
            public void onNewData(CommonJsonToBean<MyEarningsDataBean> data) {
                KLog.d("onNewData == " + new Gson().toJson(data));
                if (data.getSuccess()) {
                    view.setData(data);
                } else {
                    view.setXState(LoadingState.STATE_ERROR, data.getMsg());
                }
            }

            @Override
            public void onError(String code) {
                view.setXState(LoadingState.STATE_ERROR, code);
            }
        }, state, params);
    }
}
