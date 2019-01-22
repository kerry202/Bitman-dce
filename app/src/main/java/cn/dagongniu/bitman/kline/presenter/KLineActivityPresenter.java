package cn.dagongniu.bitman.kline.presenter;

import android.app.Activity;

import com.socks.library.KLog;

import java.util.HashMap;

import cn.dagongniu.bitman.base.OAXBasePresenter;
import cn.dagongniu.bitman.https.CommonJsonToBean;
import cn.dagongniu.bitman.https.OnDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.kline.bean.TradingInfoBean;
import cn.dagongniu.bitman.kline.module.KLineActivityModule;
import cn.dagongniu.bitman.trading.fragment.OaxTradingIView;

public class KLineActivityPresenter extends OAXBasePresenter {

    private static final String TAG = "KLinesActivity";

    private KLineActivityModule mKLineModule;
    private OaxTradingIView mView;
    private Activity mActivity;
    private RequestState mState;

    public KLineActivityPresenter(OaxTradingIView iView) {
        super(iView);
        mActivity = (Activity) iView.getContext();
        mView = iView;

    }

    public void getData(HashMap<String, Object> map, RequestState state) {
        if (mKLineModule == null) {
            mKLineModule = new KLineActivityModule(mActivity);
        }
        this.mState = state;
        mKLineModule.requestServerData(new OnDataListener<TradingInfoBean>() {
            @Override
            public void onNewData(CommonJsonToBean<TradingInfoBean> data) {
                mView.setTradingInfoData(data);
            }

            @Override
            public void onError(String msg) {
                mView.getTradingInfoDataError(msg);
            }
        }, state, map);
    }

    public void sendBuyOrSellData(HashMap<String, Object> map, RequestState state) {
        this.mState = state;
        if (mKLineModule == null) {
            mKLineModule = new KLineActivityModule(mActivity);
        }
        mKLineModule.sendBuyOrSellData(new OnDataListener<String>() {
            @Override
            public void onNewData(CommonJsonToBean<String> data) {
                mView.setBuyOrSellState(data);
            }

            @Override
            public void onError(String msg) {
                KLog.d("sendBuyOrSellData = " + msg);
                mView.showToast(msg);
            }
        }, state, map);
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
