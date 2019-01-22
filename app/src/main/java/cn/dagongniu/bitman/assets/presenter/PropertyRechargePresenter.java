package cn.dagongniu.bitman.assets.presenter;

import android.app.Activity;

import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.assets.bean.PropertyRechargeBean;
import cn.dagongniu.bitman.assets.module.PropertyRechargeModule;
import cn.dagongniu.bitman.assets.view.IPropertyRechargeView;
import cn.dagongniu.bitman.base.BasePresenter;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.https.StateBaseUtils;

/**
 * 充值记录 Presenter
 */
public class PropertyRechargePresenter extends BasePresenter {

    private PropertyRechargeModule propertyRechargeModule;
    private IPropertyRechargeView iPropertyRechargeView;
    private Activity activity;
    RequestState state;

    public PropertyRechargePresenter(IPropertyRechargeView iPropertyRechargeView, RequestState state) {
        super(iPropertyRechargeView);
        this.state = state;
        activity = (Activity) iPropertyRechargeView.getContext();
        this.iPropertyRechargeView = iPropertyRechargeView;
        propertyRechargeModule = new PropertyRechargeModule(activity);
    }

    public void getPropertyRechargeModule() {

        propertyRechargeModule.requestServerDataOne(new OnBaseDataListener<PropertyRechargeBean>() {

            @Override
            public void onNewData(PropertyRechargeBean data) {
                if (data.isSuccess()) {
                    //响应请求数据回去
                    StateBaseUtils.success(iPropertyRechargeView, state, data);

                    if (data.getData().getList() != null && data.getData().getList().size() > 0) {
                        iPropertyRechargeView.setPropertyRechargeData(data);
                    } else {
                        iPropertyRechargeView.setPropertyRechargeDataNull(data);
                    }
                } else {
                    StateBaseUtils.failure(iPropertyRechargeView, state, data.getMsg());
                }
            }

            @Override
            public void onError(String code) {
                if (activity.getResources().getString(R.string.to_login_go).equals(code)) {//前往登录
                    iPropertyRechargeView.goLogin(code);
                } else {
                    StateBaseUtils.error(iPropertyRechargeView, state, code);
                }
            }
        }, state, iPropertyRechargeView.getCoinName(), iPropertyRechargeView.getPropertyRechargePageIndex() + "", iPropertyRechargeView.getPropertyRechargePageSize() + "");
    }

    public void getPropertyRechargeLoadModule() {

        propertyRechargeModule.requestServerDataOne(new OnBaseDataListener<PropertyRechargeBean>() {
            @Override
            public void onNewData(PropertyRechargeBean data) {
                iPropertyRechargeView.setRefreshPropertyRechargeMoreData(data);
            }

            @Override
            public void onError(String code) {
                iPropertyRechargeView.setRefreshPropertyRechargeLoadMoreErrer(code);
            }
        }, state, iPropertyRechargeView.getCoinName(), iPropertyRechargeView.getPropertyRechargePageIndex() + "", iPropertyRechargeView.getPropertyRechargePageSize() + "");
    }

}
