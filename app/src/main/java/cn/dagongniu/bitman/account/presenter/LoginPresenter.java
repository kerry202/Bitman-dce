package cn.dagongniu.bitman.account.presenter;

import android.app.Activity;

import cn.dagongniu.bitman.account.bean.LoginBean;
import cn.dagongniu.bitman.account.module.LoginModule;
import cn.dagongniu.bitman.account.view.ILoginView;
import cn.dagongniu.bitman.base.BasePresenter;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;

/***
 * 登录 Presenter
 */
public class LoginPresenter extends BasePresenter {

    private LoginModule loginModule;
    private ILoginView iLoginView;
    private Activity activity;
    RequestState state;

    public LoginPresenter(ILoginView iLoginView, RequestState state) {
        super(iLoginView);
        this.state = state;
        activity = (Activity) iLoginView.getContext();
        this.iLoginView = iLoginView;
        loginModule = new LoginModule(activity);
    }

    public void getLoginModule() {

        StringBuffer stringBuffer = new StringBuffer();

        if (iLoginView.isEmailPhone()) {
            String chooseCountries = iLoginView.getChooseCountries();
            String substring = chooseCountries.substring(1, chooseCountries.length());
            stringBuffer.append("00");
            stringBuffer.append(substring);
            stringBuffer.append(iLoginView.getEmailAndPhone());
        } else {
            stringBuffer.append(iLoginView.getEmailAndPhone());
        }

        loginModule.requestServerDataOne(new OnBaseDataListener<LoginBean>() {

            @Override
            public void onNewData(LoginBean data) {
                iLoginView.isLogin(data);
            }

            @Override
            public void onError(String code) {
                iLoginView.setLoginFailure(code);
            }
        }, state, stringBuffer.toString(), iLoginView.getPassword(), iLoginView.getLoginType(), iLoginView.getSmsCode(), iLoginView.getGoogleCode());
    }

}
