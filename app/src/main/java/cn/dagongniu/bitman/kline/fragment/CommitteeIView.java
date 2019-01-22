package cn.dagongniu.bitman.kline.fragment;

import cn.dagongniu.bitman.base.OAXIView;
import cn.dagongniu.bitman.trading.bean.TradeListAndMarketOrdersBean;

public interface CommitteeIView extends OAXIView {
    void onNewCommitteeList(TradeListAndMarketOrdersBean bean);
}
