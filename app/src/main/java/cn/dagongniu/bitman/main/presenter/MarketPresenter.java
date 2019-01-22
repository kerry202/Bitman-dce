package cn.dagongniu.bitman.main.presenter;

import android.app.Activity;

import com.socks.library.KLog;

import cn.dagongniu.bitman.base.OAXBasePresenter;
import cn.dagongniu.bitman.https.CommonJsonToBean;
import cn.dagongniu.bitman.https.OnDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.kline.module.KLineActivityModule;
import cn.dagongniu.bitman.main.view.MarketIView;

public class MarketPresenter extends OAXBasePresenter {
    private KLineActivityModule mKLineModule;
    private MarketIView mView;
    private Activity mActivity;
    private RequestState mState;

    public MarketPresenter(MarketIView iView) {
        super(iView);
        mActivity = (Activity) iView.getContext();
        mView = iView;

    }

    public void collectMarket(int marketId, RequestState state) {
        this.mState = state;
        if (mKLineModule == null) {
            mKLineModule = new KLineActivityModule(mActivity);
        }
        mKLineModule.collectMarket(new OnDataListener<String>() {
            @Override
            public void onNewData(CommonJsonToBean<String> data) {

                mView.collectMarketState(data);
            }

            @Override
            public void onError(String msg) {
                KLog.d("collectMarket = " + msg);
                mView.showToast(msg);
            }
        }, state, marketId);
    }

    public void cancelCollectMarket(int marketId, RequestState state) {
        this.mState = state;
        if (mKLineModule == null) {
            mKLineModule = new KLineActivityModule(mActivity);
        }
        mKLineModule.cancelCollectMarket(new OnDataListener<String>() {
            @Override
            public void onNewData(CommonJsonToBean<String> data) {

                 mView.cancelCollectMarket(data);

            }

            @Override
            public void onError(String msg) {
                KLog.d("collectMarket = " + msg);
                mView.showToast(msg);
            }
        }, state, marketId);
    }
}
