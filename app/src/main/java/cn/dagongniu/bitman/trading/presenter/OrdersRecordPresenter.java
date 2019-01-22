package cn.dagongniu.bitman.trading.presenter;

import android.app.Activity;

import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.base.BasePresenter;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.trading.bean.OrdersRecordBean;
import cn.dagongniu.bitman.trading.module.OrdersRecordModule;
import cn.dagongniu.bitman.trading.view.IOrdersRecordView;

/**
 * 成交订单 Presenter
 */
public class OrdersRecordPresenter extends BasePresenter {

    private OrdersRecordModule ordersRecordModule;
    private IOrdersRecordView iOrdersRecordView;
    private Activity activity;
    RequestState state;

    public OrdersRecordPresenter(IOrdersRecordView iOrdersRecordView, RequestState state) {
        super(iOrdersRecordView);
        this.state = state;
        activity = (Activity) iOrdersRecordView.getContext();
        this.iOrdersRecordView = iOrdersRecordView;
        ordersRecordModule = new OrdersRecordModule(activity);
    }

    public void getOrdersRecordModule() {

        ordersRecordModule.requestServerDataOne(new OnBaseDataListener<OrdersRecordBean>() {

            @Override
            public void onNewData(OrdersRecordBean data) {
                if (data.isSuccess()) {
                    //响应请求数据回去
                    if (data.getData().getList() != null && data.getData().getList().size() > 0) {
                        iOrdersRecordView.setOrdersRecordData(data);
                    } else {
                        iOrdersRecordView.isNull(data);
                    }
                } else {
                    iOrdersRecordView.setDataErrer(data.getMsg());
                }
            }

            @Override
            public void onError(String code) {
                if (activity.getResources().getString(R.string.to_login_go).equals(code)) {//前往登录
                    iOrdersRecordView.goLogin(code);
                } else {
                    iOrdersRecordView.setDataErrer(code);
                }
            }
        }, state, iOrdersRecordView.getBeginDate(), iOrdersRecordView.getEndDate(), iOrdersRecordView.getMarketId(), iOrdersRecordView.getType(), iOrdersRecordView.getPageNo(), iOrdersRecordView.getPageSize());
    }

    public void getOrdersRecordLoadModule() {
        ordersRecordModule.requestServerDataOne(new OnBaseDataListener<OrdersRecordBean>() {
            @Override
            public void onNewData(OrdersRecordBean data) {
                if (data.isSuccess()) {
                    iOrdersRecordView.setOrdersRecordLoadMoreData(data);
                } else {
                    iOrdersRecordView.setDataLoadErrer(data.getMsg());
                }
            }

            @Override
            public void onError(String code) {
                iOrdersRecordView.setDataLoadErrer(code);
            }
        }, state, iOrdersRecordView.getBeginDate(), iOrdersRecordView.getEndDate(), iOrdersRecordView.getMarketId(), iOrdersRecordView.getType(), iOrdersRecordView.getPageNo(), iOrdersRecordView.getPageSize());
    }


}
