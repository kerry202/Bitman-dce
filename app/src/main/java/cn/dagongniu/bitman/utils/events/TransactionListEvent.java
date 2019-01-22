package cn.dagongniu.bitman.utils.events;

import cn.dagongniu.bitman.kline.bean.TradingInfoBean;

public class TransactionListEvent {
    public TradingInfoBean bean;

    public TransactionListEvent(TradingInfoBean bean) {
        this.bean = bean;
    }
}
