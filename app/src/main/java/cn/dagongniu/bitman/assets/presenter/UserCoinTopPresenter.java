package cn.dagongniu.bitman.assets.presenter;

import android.app.Activity;

import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.assets.bean.UserCoinTopBean;
import cn.dagongniu.bitman.assets.module.UserCoinTopModule;
import cn.dagongniu.bitman.assets.view.IUserCoinTopView;
import cn.dagongniu.bitman.base.BasePresenter;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.https.StateBaseUtils;

/**
 * 充值 Presenter
 */
public class UserCoinTopPresenter extends BasePresenter {

    private UserCoinTopModule userCoinTopModule;
    private IUserCoinTopView iUserCoinTopView;
    private Activity activity;
    RequestState state;

    public UserCoinTopPresenter(IUserCoinTopView iUserCoinTopView, RequestState state) {
        super(iUserCoinTopView);
        this.state = state;
        activity = (Activity) iUserCoinTopView.getContext();
        this.iUserCoinTopView = iUserCoinTopView;
        userCoinTopModule = new UserCoinTopModule(activity);
    }

    public void getUserCoinTopModule() {

        userCoinTopModule.requestServerDataOne(new OnBaseDataListener<UserCoinTopBean>() {

            @Override
            public void onNewData(UserCoinTopBean data) {
                if (data.isSuccess()) {
                    //响应请求数据回去
                    StateBaseUtils.success(iUserCoinTopView, state, data);
                    iUserCoinTopView.setUserCoinTopData(data);
                } else {
                    StateBaseUtils.failure(iUserCoinTopView, state, data.getMsg());
                }
            }

            @Override
            public void onError(String code) {
                if (activity.getResources().getString(R.string.to_login_go).equals(code)) {//前往登录
                    iUserCoinTopView.goLogin(code);
                } else {
                    StateBaseUtils.error(iUserCoinTopView, state, code);
                }
            }
        }, state, iUserCoinTopView.getCoinId());
    }


}
