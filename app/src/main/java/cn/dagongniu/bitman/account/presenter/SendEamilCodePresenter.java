package cn.dagongniu.bitman.account.presenter;

import android.app.Activity;

import cn.dagongniu.bitman.account.module.SendEamilCodeModule;
import cn.dagongniu.bitman.account.view.SendSmsView;
import cn.dagongniu.bitman.base.BasePresenter;
import cn.dagongniu.bitman.https.HttpBaseBean;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.https.StateBaseUtils;

/**
 * 邮箱验证码 Presenter
 */
public class SendEamilCodePresenter extends BasePresenter {

    private SendEamilCodeModule sendEamilCodeModule;
    private SendSmsView sendSmsView;
    private Activity activity;
    RequestState state;

    public SendEamilCodePresenter(SendSmsView sendSmsView, RequestState state) {
        super(sendSmsView);
        this.state = state;
        activity = (Activity) sendSmsView.getContext();
        this.sendSmsView = sendSmsView;
        sendEamilCodeModule = new SendEamilCodeModule(activity);
    }

    /***
     * 邮箱验证
     */
    public void getSendEamilCodeModule() {
        sendEamilCodeModule.requestServerDataOne(new OnBaseDataListener<HttpBaseBean>() {

            @Override
            public void onNewData(HttpBaseBean data) {
                if (data.isSuccess()) {
                    //响应请求数据回去
                    StateBaseUtils.success(sendSmsView, state, data);
                } else {
                    StateBaseUtils.failure(sendSmsView, state, data.getMsg());
                }
            }

            @Override
            public void onError(String code) {
                StateBaseUtils.error(sendSmsView, state, code);
            }
        }, state, sendSmsView.getEamil());
    }

}
