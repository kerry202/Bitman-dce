package cn.dagongniu.bitman.account.presenter;

import android.app.Activity;

import cn.dagongniu.bitman.account.module.SwitchCheckModule;
import cn.dagongniu.bitman.account.view.ISwitchCheckView;
import cn.dagongniu.bitman.base.BasePresenter;
import cn.dagongniu.bitman.https.HttpBaseBean;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.https.StateBaseUtils;

/**
 * 切换安全验证 Presenter
 */
public class SwitchCheckPresenter extends BasePresenter {

    private SwitchCheckModule switchCheckModule;
    private ISwitchCheckView iSwitchCheckView;
    private Activity activity;
    RequestState state;

    public SwitchCheckPresenter(ISwitchCheckView iSwitchCheckView, RequestState state) {
        super(iSwitchCheckView);
        this.state = state;
        activity = (Activity) iSwitchCheckView.getContext();
        this.iSwitchCheckView = iSwitchCheckView;
        switchCheckModule = new SwitchCheckModule(activity);
    }

    public void getSwitchCheckModule() {
        switchCheckModule.requestServerDataOne(new OnBaseDataListener<HttpBaseBean>() {

            @Override
            public void onNewData(HttpBaseBean data) {
                if (data.isSuccess()) {
                    //响应请求数据回去
                    StateBaseUtils.success(iSwitchCheckView, state, data);
                    iSwitchCheckView.successful();
                } else {
                    StateBaseUtils.failure(iSwitchCheckView, state, data.getMsg());
                    iSwitchCheckView.failure(data.getMsg());
                }
            }

            @Override
            public void onError(String code) {
                StateBaseUtils.error(iSwitchCheckView, state, code);
                iSwitchCheckView.failure(code);
            }
        }, state, iSwitchCheckView.getEmailCode(), iSwitchCheckView.getSmsCode(), iSwitchCheckView.getGoogleCode(), iSwitchCheckView.getCheckType() + "", iSwitchCheckView.getStatus() + "");
    }


}
