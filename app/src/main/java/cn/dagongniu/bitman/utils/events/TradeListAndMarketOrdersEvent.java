package cn.dagongniu.bitman.utils.events;

import cn.dagongniu.bitman.trading.bean.TradeListAndMarketOrdersBean;

public class TradeListAndMarketOrdersEvent {
    public TradeListAndMarketOrdersEvent(TradeListAndMarketOrdersBean bean) {
        mBean = bean;
    }

    public TradeListAndMarketOrdersBean mBean;
}
