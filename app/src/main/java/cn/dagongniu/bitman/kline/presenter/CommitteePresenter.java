package cn.dagongniu.bitman.kline.presenter;

import android.app.Activity;

import cn.dagongniu.bitman.base.OAXBasePresenter;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.kline.fragment.CommitteeIView;
import cn.dagongniu.bitman.kline.module.CommitteeModule;
import cn.dagongniu.bitman.trading.bean.TradeListAndMarketOrdersBean;

public class CommitteePresenter extends OAXBasePresenter {

    private CommitteeModule mModule;
    private Activity activity;
    CommitteeIView view;
    RequestState mState;

    public CommitteePresenter(CommitteeIView view) {
        super(view);
        activity = (Activity) view.getContext();
        this.view = view;
        mModule = new CommitteeModule(activity);
    }

    public void getTradeListAndMarketOrders(String marketId) {
        mModule.getTradeListAndMarketOrders(marketId, new CommitteeModule.OnTradeListAndMarketOrders() {
            @Override
            public void newData(TradeListAndMarketOrdersBean bean) {
                view.onNewCommitteeList(bean);
            }
        });
    }

    public void unTopicTradeListAndMarketOrders() {
        mModule.unTopicTradeListAndMarketOrders();
    }
}
