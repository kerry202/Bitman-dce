package cn.dagongniu.bitman.account.presenter;

import android.app.Activity;

import cn.dagongniu.bitman.account.module.CheckEmailSmsModule;
import cn.dagongniu.bitman.account.view.SendSmsView;
import cn.dagongniu.bitman.base.BasePresenter;
import cn.dagongniu.bitman.https.HttpBaseBean;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.https.StateBaseUtils;

/**
 * 效验邮箱验证码 Presenter
 */
public class CheckEmailSmsPresenter extends BasePresenter {

    private CheckEmailSmsModule checkEmailSmsModule;
    private SendSmsView sendSmsView;
    private Activity activity;
    RequestState state;

    public CheckEmailSmsPresenter(SendSmsView sendSmsView, RequestState state) {
        super(sendSmsView);
        this.state = state;
        activity = (Activity) sendSmsView.getContext();
        this.sendSmsView = sendSmsView;
        checkEmailSmsModule = new CheckEmailSmsModule(activity);
    }

    /***
     * 效验短信验证码
     */
    public void getCheckEmailSmsModule() {

        checkEmailSmsModule.requestServerDataOne(new OnBaseDataListener<HttpBaseBean>() {

            @Override
            public void onNewData(HttpBaseBean data) {
                sendSmsView.setCheckEmailSms(data);
            }


            @Override
            public void onError(String code) {
                StateBaseUtils.error(sendSmsView, state, code);
            }
        }, state, sendSmsView.getPhoneAndEamil(), sendSmsView.getCode());
    }

}
