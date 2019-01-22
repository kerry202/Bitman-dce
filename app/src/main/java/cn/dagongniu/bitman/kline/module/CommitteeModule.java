package cn.dagongniu.bitman.kline.module;

import android.app.Activity;

import com.google.gson.Gson;
import com.socks.library.KLog;

import java.util.HashMap;

import cn.dagongniu.bitman.base.OAXBaseModule;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.WebSocketsManager;
import cn.dagongniu.bitman.kline.bean.CommitteeBean;
import cn.dagongniu.bitman.trading.bean.TradeListAndMarketOrdersBean;
import io.reactivex.disposables.Disposable;
import ua.naiksoftware.stomp.client.StompClient;

public class CommitteeModule extends OAXBaseModule<HashMap<String, String>, CommitteeBean> {

    private StompClient tradeListAndMarketOrders;
    private WebSocketsManager.OnCreateStompClientListener onCreateStompClientListener;
    private Disposable mTopic;

    public CommitteeModule(Activity activity) {
        super(activity);
    }

    public void getTradeListAndMarketOrders(String marketId, OnTradeListAndMarketOrders listener) {
        if (tradeListAndMarketOrders == null) {
            onCreateStompClientListener = new WebSocketsManager.OnCreateStompClientListener() {
                @Override
                public void onOpened() {
                    if (tradeListAndMarketOrders != null) {
                        mTopic = WebSocketsManager.getInstance().topic(tradeListAndMarketOrders, Http.TOPIC_TRADELISTANDMARKETORDERS + marketId, new WebSocketsManager.OnTopicListener() {
                            @Override
                            public void onNewData(String data) {
                                KLog.d("BuySellListdata = " + data);
                                try {
                                    TradeListAndMarketOrdersBean bean = new Gson().fromJson(data, TradeListAndMarketOrdersBean.class);
                                    listener.newData(bean);
                                } catch (Exception e) {
                                    KLog.d("BuySellListdata Exception = " + e);
                                }
                            }
                        });
                    }
                }

                @Override
                public void onError() {
                }

                @Override
                public void onClosed() {
                }
            };
            tradeListAndMarketOrders = WebSocketsManager.getInstance().createStompClient(onCreateStompClientListener);
        }
    }

    public void unTopicTradeListAndMarketOrders() {
        if (tradeListAndMarketOrders != null) {
//            WebSocketsManager.getInstance().disconnect(tradeListAndMarketOrders);
            if (mTopic != null && !mTopic.isDisposed()) {
                mTopic.dispose();
                mTopic = null;
            }
            tradeListAndMarketOrders.disconnect();
            tradeListAndMarketOrders = null;
            onCreateStompClientListener = null;
        }
    }

    public interface OnTradeListAndMarketOrders {
        void newData(TradeListAndMarketOrdersBean bean);
    }
}
