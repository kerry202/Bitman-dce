package cn.dagongniu.bitman.account.presenter;

import android.app.Activity;

import cn.dagongniu.bitman.account.module.UserIdentityAuthenModule;
import cn.dagongniu.bitman.account.view.IUserIdentityAuthenView;
import cn.dagongniu.bitman.base.BasePresenter;
import cn.dagongniu.bitman.https.HttpBaseBean;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;

/***
 * 身份认证 Presenter
 */
public class UserIdentityAuthenPresenter extends BasePresenter {

    private UserIdentityAuthenModule userIdentityAuthenModule;
    private IUserIdentityAuthenView iUserIdentityAuthenView;
    private Activity activity;
    RequestState state;

    public UserIdentityAuthenPresenter(IUserIdentityAuthenView iUserIdentityAuthenView, RequestState state) {
        super(iUserIdentityAuthenView);
        this.state = state;
        activity = (Activity) iUserIdentityAuthenView.getContext();
        this.iUserIdentityAuthenView = iUserIdentityAuthenView;
        userIdentityAuthenModule = new UserIdentityAuthenModule(activity);
    }

    public void getUserIdentityAuthenModule() {

        userIdentityAuthenModule.requestServerDataOne(new OnBaseDataListener<HttpBaseBean>() {

            @Override
            public void onNewData(HttpBaseBean data) {
                if (data.isSuccess()) {
                    //响应请求数据回去
                    iUserIdentityAuthenView.isSueecss();
                } else {
                    iUserIdentityAuthenView.isfailure(data.getMsg());
                }
            }

            @Override
            public void onError(String code) {
                iUserIdentityAuthenView.isfailure(code);
            }
        }, state, iUserIdentityAuthenView.getIdName(), iUserIdentityAuthenView.getCardType(), iUserIdentityAuthenView.getCardNo(), iUserIdentityAuthenView.getIdImageA(), iUserIdentityAuthenView.getIdImageB(), iUserIdentityAuthenView.getCountry());
    }

}
