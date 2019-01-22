package cn.dagongniu.bitman.assets.presenter;

import android.app.Activity;

import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.assets.bean.PropertyShowBean;
import cn.dagongniu.bitman.assets.module.PropertyShowModule;
import cn.dagongniu.bitman.assets.view.IPropertyShowView;
import cn.dagongniu.bitman.base.BasePresenter;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.https.StateBaseUtils;
import cn.dagongniu.bitman.utils.Logs;

/**
 * 充值显示详情 Presenter
 */
public class PropertyShowPresenter extends BasePresenter {

    private PropertyShowModule propertyShowModule;
    private IPropertyShowView iPropertyShowView;
    private Activity activity;
    RequestState state;

    public PropertyShowPresenter(IPropertyShowView iPropertyShowView, RequestState state) {
        super(iPropertyShowView);
        this.state = state;
        activity = (Activity) iPropertyShowView.getContext();
        this.iPropertyShowView = iPropertyShowView;
        propertyShowModule = new PropertyShowModule(activity);
    }

    public void getPropertyShowModule() {

        propertyShowModule.requestServerDataOne(new OnBaseDataListener<PropertyShowBean>() {

            @Override
            public void onNewData(PropertyShowBean data) {
                Logs.s("        ");
                if (data.isSuccess()) {
                    //响应请求数据回去
                    StateBaseUtils.success(iPropertyShowView, state, data);
                    iPropertyShowView.setRefreshPropertyShowData(data);
                } else {
                    StateBaseUtils.failure(iPropertyShowView, state, data.getMsg());
                }
            }

            @Override
            public void onError(String code) {
                if (activity.getResources().getString(R.string.to_login_go).equals(code)) {//前往登录
                    iPropertyShowView.goLogin(code);
                } else {
                    StateBaseUtils.error(iPropertyShowView, state, code);
                }
            }
        }, state, iPropertyShowView.getCoinId());
    }


}
