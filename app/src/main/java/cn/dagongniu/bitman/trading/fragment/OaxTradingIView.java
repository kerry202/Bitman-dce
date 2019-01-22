package cn.dagongniu.bitman.trading.fragment;

import java.util.List;

import cn.dagongniu.bitman.base.OAXIView;
import cn.dagongniu.bitman.https.CommonJsonToBean;
import cn.dagongniu.bitman.kline.bean.TradingInfoBean;

public interface OaxTradingIView extends OAXIView {

    /**
     * 市场交易对，用户搜藏交易对，k线图，实时委托，实时交易
     *
     * @param data
     */
    void setTradingInfoData(CommonJsonToBean<TradingInfoBean> data);

    void getTradingInfoDataError(String msg);

    /**
     * 买卖
     *
     * @param data
     */
    void setBuyOrSellState(CommonJsonToBean<String> data);

    /**
     * 收藏
     */
    void collectMarketState(CommonJsonToBean<String> data);

    /**
     * 取消收藏
     */
    void cancelCollectMarket(CommonJsonToBean<String> data);

    /**
     * 订阅k线数据
     */
    void setTopicKlineData(List<TradingInfoBean.KlineListBean> data);
}
