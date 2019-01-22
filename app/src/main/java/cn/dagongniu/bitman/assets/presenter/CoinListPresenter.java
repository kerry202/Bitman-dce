package cn.dagongniu.bitman.assets.presenter;

import android.app.Activity;

import cn.dagongniu.bitman.assets.bean.CoinListBean;
import cn.dagongniu.bitman.assets.module.CoinListModule;
import cn.dagongniu.bitman.assets.view.ICoinListView;
import cn.dagongniu.bitman.base.BasePresenter;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.https.StateBaseUtils;
import cn.dagongniu.bitman.utils.Logs;

/**
 * 币种列表 Presenter
 */
public class CoinListPresenter extends BasePresenter {

    private CoinListModule coinListModule;
    private ICoinListView iCoinListView;
    private Activity activity;
    RequestState state;

    public CoinListPresenter(ICoinListView iCoinListView, RequestState state) {
        super(iCoinListView);
        this.state = state;
        activity = (Activity) iCoinListView.getContext();
        this.iCoinListView = iCoinListView;
        coinListModule = new CoinListModule(activity);
    }

    public void getCoinListModule() {

        coinListModule.requestServerDataOne(new OnBaseDataListener<CoinListBean>() {

            @Override
            public void onNewData(CoinListBean data) {
                Logs.s("   充值提现搜索   "+data);
                if (data.isSuccess()) {
                    //响应请求数据回去
                    StateBaseUtils.success(iCoinListView, state, data);
                    iCoinListView.setCoinListBeanData(data);
                } else {
                    StateBaseUtils.failure(iCoinListView, state, data.getMsg());
                    iCoinListView.refreshErrer();
                }
            }

            @Override
            public void onError(String code) {
                Logs.s("   充值提现搜索   "+code);
                iCoinListView.refreshErrer();
                StateBaseUtils.error(iCoinListView, state, code);
            }
        }, state);
    }


}
