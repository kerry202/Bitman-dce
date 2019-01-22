package cn.dagongniu.bitman.account.presenter;

import android.app.Activity;

import cn.dagongniu.bitman.account.bean.IdentityResultBean;
import cn.dagongniu.bitman.account.module.IdentityResultModule;
import cn.dagongniu.bitman.account.view.IidentityResultView;
import cn.dagongniu.bitman.base.BasePresenter;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.https.StateBaseUtils;

/**
 * 身份认证审核结果 Presenter
 */
public class IdentityResultPresenter extends BasePresenter {

    private IdentityResultModule identityResultModule;
    private IidentityResultView iidentityResultView;
    private Activity activity;
    RequestState state;

    public IdentityResultPresenter(IidentityResultView iidentityResultView, RequestState state) {
        super(iidentityResultView);
        this.state = state;
        activity = (Activity) iidentityResultView.getContext();
        this.iidentityResultView = iidentityResultView;
        identityResultModule = new IdentityResultModule(activity);
    }

    public void getIdentityResultModule() {
        identityResultModule.requestServerDataOne(new OnBaseDataListener<IdentityResultBean>() {

            @Override
            public void onNewData(IdentityResultBean data) {
                if (data.isSuccess()) {
                    //响应请求数据回去
                    iidentityResultView.IidentityResultsuccessful(data);
                } else {
                    iidentityResultView.IidentityResultfailure(data.getMsg());
                }
            }

            @Override
            public void onError(String code) {
                StateBaseUtils.error(iidentityResultView, state, code);
            }
        }, state);
    }


}
