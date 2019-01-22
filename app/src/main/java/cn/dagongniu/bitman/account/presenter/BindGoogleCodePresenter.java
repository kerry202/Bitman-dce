package cn.dagongniu.bitman.account.presenter;

import android.app.Activity;

import cn.dagongniu.bitman.account.module.BindGoogleCodeModule;
import cn.dagongniu.bitman.account.view.IGoogleCodeView;
import cn.dagongniu.bitman.base.BasePresenter;
import cn.dagongniu.bitman.https.HttpBaseBean;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.https.StateBaseUtils;

/**
 * 绑定或修改谷歌验证 Presenter
 */
public class BindGoogleCodePresenter extends BasePresenter {

    private BindGoogleCodeModule bindGoogleCodeModule;
    private IGoogleCodeView iGoogleCodeView;
    private Activity activity;
    RequestState state;

    public BindGoogleCodePresenter(IGoogleCodeView iGoogleCodeView, RequestState state) {
        super(iGoogleCodeView);
        this.state = state;
        activity = (Activity) iGoogleCodeView.getContext();
        this.iGoogleCodeView = iGoogleCodeView;
        bindGoogleCodeModule = new BindGoogleCodeModule(activity);
    }


    public void getBindGoogleCodeModule() {

        bindGoogleCodeModule.requestServerDataOne(new OnBaseDataListener<HttpBaseBean>() {

            @Override
            public void onNewData(HttpBaseBean data) {
                if (data.isSuccess()) {
                    //响应请求数据回去
                    StateBaseUtils.success(iGoogleCodeView, state, data);
                    iGoogleCodeView.bindSuccess();
                } else {
                    StateBaseUtils.failure(iGoogleCodeView, state, data.getMsg());
                }
            }

            @Override
            public void onError(String code) {
                StateBaseUtils.error(iGoogleCodeView, state, code);
            }
        }, state,iGoogleCodeView.getGoogleKey(),iGoogleCodeView.getGoogleCode());
    }



}
