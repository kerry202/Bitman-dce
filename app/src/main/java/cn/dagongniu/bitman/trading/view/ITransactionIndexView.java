package cn.dagongniu.bitman.trading.view;

import cn.dagongniu.bitman.trading.bean.TransactionIndexBean;
import cn.dagongniu.bitman.base.IView;

public interface ITransactionIndexView extends IView {

    String getMarketId();//交易对id

    String getMinType();//时间类型 查询K线数据的条件

    void setTransactionIndexBeanData(TransactionIndexBean transactionIndexBean);
}
