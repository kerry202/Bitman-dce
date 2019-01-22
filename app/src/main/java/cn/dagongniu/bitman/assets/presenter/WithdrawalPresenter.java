package cn.dagongniu.bitman.assets.presenter;

import android.app.Activity;

import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.assets.bean.WithdrawalBean;
import cn.dagongniu.bitman.assets.module.WithdrawalModule;
import cn.dagongniu.bitman.assets.view.IWithdrawalView;
import cn.dagongniu.bitman.base.BasePresenter;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.https.StateBaseUtils;

/**
 * 提现 Presenter
 */
public class WithdrawalPresenter extends BasePresenter {

    private WithdrawalModule withdrawalModule;
    private IWithdrawalView iWithdrawalView;
    private Activity activity;
    RequestState state;

    public WithdrawalPresenter(IWithdrawalView iWithdrawalView, RequestState state) {
        super(iWithdrawalView);
        this.state = state;
        activity = (Activity) iWithdrawalView.getContext();
        this.iWithdrawalView = iWithdrawalView;
        withdrawalModule = new WithdrawalModule(activity);
    }

    public void getWithdrawalModule() {
        withdrawalModule.requestServerDataOne(new OnBaseDataListener<WithdrawalBean>() {
            @Override
            public void onNewData(WithdrawalBean data) {
                if (data.isSuccess()) {
                    //响应请求数据回去
                    StateBaseUtils.success(iWithdrawalView, state, data);
                    iWithdrawalView.isSuccess();
                } else {
                    StateBaseUtils.failure(iWithdrawalView, state, data.getMsg());
                }
            }

            @Override
            public void onError(String code) {
                if (activity.getResources().getString(R.string.to_login_go).equals(code)) {//前往登录
                    iWithdrawalView.goLogin(code);
                } else {
                    StateBaseUtils.error(iWithdrawalView, state, code);
                }
            }
        }, state, iWithdrawalView.getAddress(), iWithdrawalView.getRemark(), iWithdrawalView.getqty(), iWithdrawalView.getCoinId());
    }


}
