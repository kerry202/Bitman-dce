package cn.dagongniu.bitman.account.presenter;

import android.app.Activity;

import com.google.gson.Gson;
import com.socks.library.KLog;

import java.util.HashMap;

import cn.dagongniu.bitman.account.bean.HelpDetailInfoBean;
import cn.dagongniu.bitman.account.module.HelpDetailModule;
import cn.dagongniu.bitman.base.OAXBasePresenter;
import cn.dagongniu.bitman.base.OAXIViewBean;
import cn.dagongniu.bitman.https.CommonJsonToBean;
import cn.dagongniu.bitman.https.OAXStateBaseUtils;
import cn.dagongniu.bitman.https.OnDataListener;
import cn.dagongniu.bitman.https.RequestState;

public class HelpDetailPresenter extends OAXBasePresenter {
    private HelpDetailModule mHelpModule;
    private Activity activity;
    OAXIViewBean view;
    RequestState mState;

    public HelpDetailPresenter(OAXIViewBean view) {
        super(view);
        activity = (Activity) view.getContext();
        this.view = view;
        mHelpModule = new HelpDetailModule(activity);
    }

    public void getData(HashMap<String, Object> params, RequestState state) {
        this.mState = state;
        mHelpModule.requestServerData(new OnDataListener<HelpDetailInfoBean>() {

            @Override
            public void onNewData(CommonJsonToBean<HelpDetailInfoBean> data) {
                KLog.d("onNewData == " + new Gson().toJson(data));
                if (data != null && data.getSuccess()) {
                    try {
                        if (data.getData() == null || data.getData().getContent().isEmpty()) {
                            OAXStateBaseUtils.isNull(view, mState, data.getMsg());
                        } else {
                            OAXStateBaseUtils.successBean(view, mState, data);
                        }
                    } catch (Exception e) {

                    }

                } else {
                    OAXStateBaseUtils.failure(view, mState, data.getMsg());
                }
            }

            @Override
            public void onError(String code) {
                OAXStateBaseUtils.error(view, mState, code);

            }
        }, state, params);
    }
}
