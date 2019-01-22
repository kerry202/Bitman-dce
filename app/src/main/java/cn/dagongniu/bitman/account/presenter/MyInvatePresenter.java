package cn.dagongniu.bitman.account.presenter;

import android.app.Activity;

import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.account.bean.MyInvateBean;
import cn.dagongniu.bitman.account.module.MyInvateModule;
import cn.dagongniu.bitman.account.view.IMyInvateView;
import cn.dagongniu.bitman.base.BasePresenter;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.https.StateBaseUtils;

/**
 * 我的邀请 Presenter
 */
public class MyInvatePresenter extends BasePresenter {

    private MyInvateModule myInvateModule;
    private IMyInvateView iMyInvateView;
    private Activity activity;
    RequestState state;

    public MyInvatePresenter(IMyInvateView iMyInvateView, RequestState state) {
        super(iMyInvateView);
        this.state = state;
        activity = (Activity) iMyInvateView.getContext();
        this.iMyInvateView = iMyInvateView;
        myInvateModule = new MyInvateModule(activity);
    }

    public void getMyInvateModule() {

        myInvateModule.requestServerDataOne(new OnBaseDataListener<MyInvateBean>() {

            @Override
            public void onNewData(MyInvateBean data) {
                if (data.isSuccess()) {
                    //响应请求数据回去
                    StateBaseUtils.success(iMyInvateView, state, data);
                    iMyInvateView.setMyInvateData(data);
                } else {
                    StateBaseUtils.failure(iMyInvateView, state, data.getMsg());
                }
            }

            @Override
            public void onError(String code) {
                if (activity.getResources().getString(R.string.to_login_go).equals(code)) {//前往登录
                    iMyInvateView.goLogin(code);
                } else {
                    StateBaseUtils.error(iMyInvateView, state, code);
                }
            }
        }, state);
    }

}
