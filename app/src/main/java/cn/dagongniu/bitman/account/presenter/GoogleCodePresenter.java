package cn.dagongniu.bitman.account.presenter;

import android.app.Activity;

import cn.dagongniu.bitman.account.bean.GoogleCodeBean;
import cn.dagongniu.bitman.account.module.GoogleCodeModule;
import cn.dagongniu.bitman.account.view.IGoogleCodeView;
import cn.dagongniu.bitman.base.BasePresenter;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.https.StateBaseUtils;

/**
 * google验证查询 Presenter
 */
public class GoogleCodePresenter extends BasePresenter {

    private GoogleCodeModule googleCodeModule;
    private IGoogleCodeView iGoogleCodeView;
    private Activity activity;
    RequestState state;

    public GoogleCodePresenter(IGoogleCodeView iGoogleCodeView, RequestState state) {
        super(iGoogleCodeView);
        this.state = state;
        activity = (Activity) iGoogleCodeView.getContext();
        this.iGoogleCodeView = iGoogleCodeView;
        googleCodeModule = new GoogleCodeModule(activity);
    }


    public void getGoogleCodeModule() {

        googleCodeModule.requestServerDataOne(new OnBaseDataListener<GoogleCodeBean>() {

            @Override
            public void onNewData(GoogleCodeBean data) {
                if (data.isSuccess()) {
                    //响应请求数据回去
                    StateBaseUtils.success(iGoogleCodeView, state, data);
                    iGoogleCodeView.setGoogleCodeData(data);
                } else {
                    StateBaseUtils.failure(iGoogleCodeView, state, data.getMsg());
                }
            }

            @Override
            public void onError(String code) {
                StateBaseUtils.error(iGoogleCodeView, state, code);
            }
        }, state);
    }



}
