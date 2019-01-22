package cn.dagongniu.bitman.main.view;

import cn.dagongniu.bitman.base.OAXIView;
import cn.dagongniu.bitman.https.CommonJsonToBean;

public interface MarketIView  extends OAXIView{
    /**
     * 收藏
     */
    void collectMarketState(CommonJsonToBean<String> data);

    /**
     * 取消收藏
     */
    void cancelCollectMarket(CommonJsonToBean<String> data);


}
