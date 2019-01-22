package cn.dagongniu.bitman.kline.fragment;

import java.util.List;

import cn.dagongniu.bitman.base.OAXIView;
import cn.dagongniu.bitman.kline.bean.TradingInfoBean;

public interface TransactionIView extends OAXIView {
    void onNewTransactionList(List<TradingInfoBean.MarketTradeListBean> beans);
}
