package cn.dagongniu.bitman.assets.presenter;

import android.app.Activity;

import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.assets.bean.AssetsPropertyListBean;
import cn.dagongniu.bitman.assets.module.AssetsPropertyListModule;
import cn.dagongniu.bitman.assets.view.IAssetsPropertyListView;
import cn.dagongniu.bitman.base.BasePresenter;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.https.StateBaseUtils;

/**
 * 公告查看详情 Presenter
 */
public class AssetsPropertyListPresenter extends BasePresenter {

    private AssetsPropertyListModule assetsPropertyListModule;
    private IAssetsPropertyListView iAssetsPropertyListView;
    private Activity activity;
    RequestState state;

    public AssetsPropertyListPresenter(IAssetsPropertyListView iAssetsPropertyListView, RequestState state) {
        super(iAssetsPropertyListView);
        this.state = state;
        activity = (Activity) iAssetsPropertyListView.getContext();
        this.iAssetsPropertyListView = iAssetsPropertyListView;
        assetsPropertyListModule = new AssetsPropertyListModule(activity);
    }

    public void getAssetsPropertyListModule() {

        assetsPropertyListModule.requestServerDataOne(new OnBaseDataListener<AssetsPropertyListBean>() {

            @Override
            public void onNewData(AssetsPropertyListBean data) {
                if (data.isSuccess()) {
                    //响应请求数据回去
                    StateBaseUtils.success(iAssetsPropertyListView, state, data);
                    if (data.getData().getCoinList() != null && data.getData().getCoinList().size() > 0) {
                        iAssetsPropertyListView.setIAssetsPropertyData(data);
                    } else {
                        iAssetsPropertyListView.setIAssetsPropertyDataNull(data);
                    }

                } else {
                    iAssetsPropertyListView.refreshErrer();
                    StateBaseUtils.failure(iAssetsPropertyListView, state, data.getMsg());
                }
            }

            @Override
            public void onError(String code) {
                if (activity.getResources().getString(R.string.to_login_go).equals(code)) {//前往登录
                    iAssetsPropertyListView.goLogin(code);
                } else {
                    StateBaseUtils.error(iAssetsPropertyListView, state, code);
                }
                iAssetsPropertyListView.refreshErrer();
            }
        }, state, iAssetsPropertyListView.getCoinName(), iAssetsPropertyListView.getType() + "");
    }


}
