package cn.dagongniu.bitman.account.presenter;

import android.app.Activity;

import cn.dagongniu.bitman.account.bean.UserCenterBean;
import cn.dagongniu.bitman.account.module.UserCenterModule;
import cn.dagongniu.bitman.account.view.IUserCenterView;
import cn.dagongniu.bitman.base.BasePresenter;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.utils.Logger;

/***
 * 个人中心 Presenter
 */
public class UserCenterPresenter extends BasePresenter {

    private UserCenterModule userCenterModule;
    private IUserCenterView iUserCenterView;
    private Activity activity;
    RequestState state;

    public UserCenterPresenter(IUserCenterView iUserCenterView, RequestState state) {
        super(iUserCenterView);
        this.state = state;
        activity = (Activity) iUserCenterView.getContext();
        this.iUserCenterView = iUserCenterView;
        userCenterModule = new UserCenterModule(activity);
    }

    public void getUserCenterModule() {

        userCenterModule.requestServerDataOne(new OnBaseDataListener<UserCenterBean>() {

            @Override
            public void onNewData(UserCenterBean data) {
                if (data.isSuccess()) {
                    //响应请求数据回去
                    Logger.e("UserCenterPresenter", "网络请求Success = " + data.getMsg());
                    iUserCenterView.setUserCenterData(data);
                } else {
                    iUserCenterView.setUserCenterFailure(data.getMsg());
                    Logger.e("UserCenterPresenter", "网络请求Failure = " + data.getMsg());
                }
            }

            @Override
            public void onError(String code) {
                Logger.e("UserCenterPresenter", "网络请求error = " + code);
                iUserCenterView.setUserCenterFailure(code);
            }
        }, state);
    }

}
