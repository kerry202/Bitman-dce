package cn.dagongniu.bitman.account.presenter;

import android.app.Activity;

import cn.dagongniu.bitman.account.module.CheckEmailModule;
import cn.dagongniu.bitman.account.module.CheckPhoneModule;
import cn.dagongniu.bitman.account.view.CheckEmailPhoneView;
import cn.dagongniu.bitman.base.BasePresenter;
import cn.dagongniu.bitman.https.HttpBaseBean;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;

/**
 * 判断手机是否已被注册 判断邮箱是否已被注册 Presenter
 */
public class CheckPhoneAndEmailPresenter extends BasePresenter {

    private CheckPhoneModule checkPhoneModule;
    private CheckEmailModule checkEmailModule;

    private CheckEmailPhoneView checkEmailPhoneView;
    private Activity activity;
    RequestState state;

    public CheckPhoneAndEmailPresenter(CheckEmailPhoneView checkEmailPhoneView, RequestState state) {
        super(checkEmailPhoneView);
        this.state = state;
        activity = (Activity) checkEmailPhoneView.getContext();
        this.checkEmailPhoneView = checkEmailPhoneView;
        checkPhoneModule = new CheckPhoneModule(activity);
        checkEmailModule = new CheckEmailModule(activity);
    }

    /***
     * 判断邮箱是否已被注册
     */
    public void getCheckEmailModule() {

        checkEmailModule.requestServerDataOne(new OnBaseDataListener<HttpBaseBean>() {

            @Override
            public void onNewData(HttpBaseBean data) {
                checkEmailPhoneView.setCheckEmailSuccess(data);
            }

            @Override
            public void onError(String code) {
                checkEmailPhoneView.setCheckEmailFailure(code);
            }
        }, state, checkEmailPhoneView.getPhoneAndEamil());
    }

    /***
     * 判断手机是否已被注册
     */
    public void getCheckPhoneModule() {
        String chooseCountries = checkEmailPhoneView.getChooseCountries();
        String substring = chooseCountries.substring(1, chooseCountries.length());

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("00");
        stringBuffer.append(substring);

        checkPhoneModule.requestServerDataOne(new OnBaseDataListener<HttpBaseBean>() {

            @Override
            public void onNewData(HttpBaseBean data) {
                checkEmailPhoneView.setCheckPhoneSuccess(data);
            }


            @Override
            public void onError(String code) {
                checkEmailPhoneView.setCheckPhoneFailure(code);
            }
        }, state, stringBuffer.toString() + checkEmailPhoneView.getPhoneAndEamil());
    }

}
