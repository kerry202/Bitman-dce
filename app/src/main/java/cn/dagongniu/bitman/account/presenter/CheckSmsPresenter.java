package cn.dagongniu.bitman.account.presenter;

import android.app.Activity;

import cn.dagongniu.bitman.OAXApplication;
import cn.dagongniu.bitman.account.module.CheckSmsModule;
import cn.dagongniu.bitman.account.view.SendSmsView;
import cn.dagongniu.bitman.base.BasePresenter;
import cn.dagongniu.bitman.https.HttpBaseBean;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.https.StateBaseUtils;
import cn.dagongniu.bitman.utils.Logs;

/**
 * 效验短信验证码 Presenter
 */
public class CheckSmsPresenter extends BasePresenter {

    private CheckSmsModule sendSmsModule;
    private SendSmsView sendSmsView;
    private Activity activity;
    RequestState state;

    public CheckSmsPresenter(SendSmsView sendSmsView, RequestState state) {
        super(sendSmsView);
        this.state = state;
        activity = (Activity) sendSmsView.getContext();
        this.sendSmsView = sendSmsView;
        sendSmsModule = new CheckSmsModule(activity);
    }

    /***
     * 效验短信验证码
     */
    public void getCheckSmsModule() {
        String chooseCountries = sendSmsView.getChooseCountries();
        String substring = chooseCountries.substring(1, chooseCountries.length());

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("00");
        stringBuffer.append(substring);
        sendSmsModule.requestServerDataOne(new OnBaseDataListener<HttpBaseBean>() {

            @Override
            public void onNewData(HttpBaseBean data) {
                Logs.s("    效验短信验证码    " + data);
                if (data.isSuccess()) {
                    //响应请求数据回去
                    StateBaseUtils.success(sendSmsView, state, data);
                } else {
//                    StateBaseUtils.failure(sendSmsView, state, data.getMsg());
                }
                sendSmsView.setCheckPhoneSms(data);
            }


            @Override
            public void onError(String code) {
                StateBaseUtils.error(sendSmsView, state, code);
            }
        }, state, stringBuffer.toString() + sendSmsView.getPhoneAndEamil(), sendSmsView.getCode());
    }

}
