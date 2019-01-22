package cn.dagongniu.bitman.account.presenter;

import android.app.Activity;

import cn.dagongniu.bitman.account.module.UserUpdateLoginPwModule;
import cn.dagongniu.bitman.account.view.IUserLoginLoginPwView;
import cn.dagongniu.bitman.base.BasePresenter;
import cn.dagongniu.bitman.https.HttpBaseBean;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.https.StateBaseUtils;

/**
 * 修改登录密码 Presenter
 */
public class UserUpdateLoginPwPresenter extends BasePresenter {

    private UserUpdateLoginPwModule userUpdateLoginPwModule;
    private IUserLoginLoginPwView iUserLoginLoginPwView;
    private Activity activity;
    RequestState state;

    public UserUpdateLoginPwPresenter(IUserLoginLoginPwView iUserLoginLoginPwView, RequestState state) {
        super(iUserLoginLoginPwView);
        this.state = state;
        activity = (Activity) iUserLoginLoginPwView.getContext();
        this.iUserLoginLoginPwView = iUserLoginLoginPwView;
        userUpdateLoginPwModule = new UserUpdateLoginPwModule(activity);
    }


    public void getUserUpdateLoginPwModule() {

        userUpdateLoginPwModule.requestServerDataOne(new OnBaseDataListener<HttpBaseBean>() {
                                                         @Override
                                                         public void onNewData(HttpBaseBean data) {
                                                             if (data.isSuccess()) {
                                                                 //响应请求数据回去
                                                                 iUserLoginLoginPwView.UpdateSuccess();
                                                             } else {
                                                                 StateBaseUtils.failure(iUserLoginLoginPwView, state, data.getMsg());
                                                             }
                                                         }

                                                         @Override
                                                         public void onError(String code) {
                                                             StateBaseUtils.error(iUserLoginLoginPwView, state, code);
                                                         }
                                                     },
                state,
                iUserLoginLoginPwView.getOldPassword(),
                iUserLoginLoginPwView.getNewPassword(),
                iUserLoginLoginPwView.getRepeatPassword(),
                iUserLoginLoginPwView.getSmsCode(),
                iUserLoginLoginPwView.getEmailCode(),
                iUserLoginLoginPwView.getGoogleCode());
    }

}
