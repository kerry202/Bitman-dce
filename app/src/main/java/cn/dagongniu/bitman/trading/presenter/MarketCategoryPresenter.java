package cn.dagongniu.bitman.trading.presenter;

import android.app.Activity;

import cn.dagongniu.bitman.base.BasePresenter;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.https.StateBaseUtils;
import cn.dagongniu.bitman.trading.bean.MarketCategoryBean;
import cn.dagongniu.bitman.trading.module.MarketCategoryModule;
import cn.dagongniu.bitman.trading.view.IMarketCategoryView;

/**
 * 联动交易对信息查询 Presenter
 */
public class MarketCategoryPresenter extends BasePresenter {

    private MarketCategoryModule marketCategoryModule;
    private IMarketCategoryView iMarketCategoryView;
    private Activity activity;
    RequestState state;

    public MarketCategoryPresenter(IMarketCategoryView iMarketCategoryView, RequestState state) {
        super(iMarketCategoryView);
        this.state = state;
        activity = (Activity) iMarketCategoryView.getContext();
        this.iMarketCategoryView = iMarketCategoryView;
        marketCategoryModule = new MarketCategoryModule(activity);
    }

    public void getMarketCategoryListModule() {

        marketCategoryModule.requestServerDataOne(new OnBaseDataListener<MarketCategoryBean>() {

            @Override
            public void onNewData(MarketCategoryBean data) {
                if (data.isSuccess()) {
                    //响应请求数据回去
                    iMarketCategoryView.setMarketCategoryData(data);

                } else {
                    StateBaseUtils.failure(iMarketCategoryView, state, data.getMsg());
                }
            }

            @Override
            public void onError(String code) {
                StateBaseUtils.error(iMarketCategoryView, state, code);
            }
        }, state);
    }


}
