package cn.dagongniu.bitman.account.presenter;

import android.app.Activity;

import cn.dagongniu.bitman.account.module.SendSmsModule;
import cn.dagongniu.bitman.account.view.SendSmsView;
import cn.dagongniu.bitman.base.BasePresenter;
import cn.dagongniu.bitman.https.HttpBaseBean;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.https.StateBaseUtils;
import cn.dagongniu.bitman.utils.Logs;

/**
 * 短信验证码 Presenter
 */
public class SendSmsPresenter extends BasePresenter {

    private SendSmsModule sendSmsModule;
    private SendSmsView sendSmsView;
    private Activity activity;
    RequestState state;

    public SendSmsPresenter(SendSmsView sendSmsView, RequestState state) {
        super(sendSmsView);
        this.state = state;
        activity = (Activity) sendSmsView.getContext();
        this.sendSmsView = sendSmsView;
        sendSmsModule = new SendSmsModule(activity);
    }

    /***
     * 短信验证
     */
    public void getSendSmsModule1(String str) {
        String chooseCountries = sendSmsView.getChooseCountries();
        String substring = chooseCountries.substring(1, chooseCountries.length());

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("00");
        stringBuffer.append(substring);

        sendSmsModule.requestServerDataOne(new OnBaseDataListener<HttpBaseBean>() {

            @Override
            public void onNewData(HttpBaseBean data) {
                Logs.s("  手机号验证码：  "+data);
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
        }, state, stringBuffer.toString() + sendSmsView.getPhone(), str);
    }


    /***
     * 短信验证  带00过来的号码 不需要做操作
     */
    public void getSendSmsModule(String phone) {

        sendSmsModule.requestServerDataOne(new OnBaseDataListener<HttpBaseBean>() {

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
        }, state, phone, sendSmsView.getType());
    }

}
