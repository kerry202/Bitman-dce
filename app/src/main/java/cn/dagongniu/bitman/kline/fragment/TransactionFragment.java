package cn.dagongniu.bitman.kline.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.socks.library.KLog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.BindView;
import cn.dagongniu.bitman.OAXApplication;
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.base.OAXBaseFragment;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.PrefUtils;
import cn.dagongniu.bitman.https.UrlParams;
import cn.dagongniu.bitman.https.WebSocketsManager;
import cn.dagongniu.bitman.kline.adapter.TransactionAdapter;
import cn.dagongniu.bitman.main.bean.IndexPageBean;
import cn.dagongniu.bitman.trading.bean.TradeListAndMarketOrdersBean;
import cn.dagongniu.bitman.utils.Logs;
import cn.dagongniu.bitman.utils.events.TradeListAndMarketOrdersEvent;
import cn.dagongniu.bitman.utils.events.TransactionListEvent;
import io.reactivex.disposables.Disposable;
import ua.naiksoftware.stomp.client.StompClient;


/**
 * 实时成交
 */
public class TransactionFragment extends OAXBaseFragment {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    @BindView(R.id.ll_id)
    LinearLayout ll_id;

    @BindView(R.id.lin_load)
    LinearLayout lin_load;

    @BindView(R.id.v_id1)
    View v_id1;

    @BindView(R.id.v_id2)
    View v_id2;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int defaultMarketId = OAXApplication.defaultMarketId;
            if (defaultMarketId != -1) {
                initWebSocket();
                marketId = defaultMarketId;
                Logs.s("    handler  handleMessage 1  " + defaultMarketId);
            } else {
                handler.sendEmptyMessageDelayed(1, 10);
                Logs.s("    handler  handleMessage 2  " + defaultMarketId);
            }
        }
    };
    private TransactionAdapter mTransactionAdapter;
    private int marketId = 0;
    private boolean bloom;
    private boolean bgColorState;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_transaction;
    }


    @Override
    protected void initView() {
        super.initView();
        Bundle bundle = getArguments();
        if (bundle.containsKey(UrlParams.marketId)) {
            marketId = bundle.getInt(UrlParams.marketId);
            bloom = bundle.getBoolean("bloom", false);
            bgColorState = bundle.getBoolean("bgColorState", false);
        }
        if (bgColorState) {
            ll_id.setBackgroundColor(getResources().getColor(R.color.kline_bg));
        } else {
            ll_id.setBackgroundColor(getResources().getColor(R.color.white));
        }

        recyclerview.setLayoutManager(new LinearLayoutManager(mContext) {
            @Override
            public boolean canScrollVertically() {
                if (bloom) {
                    return true;
                } else return true;
            }
        });
        if (!bloom) {
//            recyclerview.setNestedScrollingEnabled(false);
        } else {
            v_id1.setBackgroundColor(getResources().getColor(R.color.app_bg));
            v_id2.setBackgroundColor(getResources().getColor(R.color.app_bg));
        }
        mTransactionAdapter = new TransactionAdapter(mContext, bgColorState);
        recyclerview.setAdapter(mTransactionAdapter);
    }

    @Override
    public void initData() {
        super.initData();

        if (PrefUtils.getNetState(OAXApplication.getContext(), 0) == 0) {
            lin_load.setVisibility(View.VISIBLE);
            recyclerview.setVisibility(View.GONE);
        }
        Logs.s("    handler  handleMessage initData  " + OAXApplication.defaultMarketId);
//        handler.sendEmptyMessageDelayed(1, 10);

    }

    StompClient mTradeListClient;
    WebSocketsManager.OnCreateStompClientListener mOnCreateStompClientListener;
    Disposable mTopic;

    public void initWebSocket() {
        KLog.d("实时成交 initWebSocket  = ");
        if (mTradeListClient == null) {
            mOnCreateStompClientListener = new WebSocketsManager.OnCreateStompClientListener() {
                @Override
                public void onOpened() {
                    KLog.d("实时成交 initWebSocket 2 = ");
                    if (mTradeListClient != null) {
                        KLog.d("实时成交 initWebSocket 3 = " + marketId);
                        mTopic = WebSocketsManager.getInstance().topic(mTradeListClient, Http.TraderList + marketId, new WebSocketsManager.OnTopicListener() {
                            @Override
                            public void onNewData(String data) {
                                KLog.d("实时成交 onNewData  = " + data);
                                try {
//                                    TradeListAndMarketOrdersBean bean = new Gson().fromJson(data, TradeListAndMarketOrdersBean.class);
                                } catch (Exception e) {
                                    KLog.d("实时成交 Exception  = ");
                                }
                            }
                        });
                    }
                }

                @Override
                public void onError() {

                    KLog.d("实时成交 onError  = ");
                }

                @Override
                public void onClosed() {
                    KLog.d("实时成交 onClosed  = ");

                }
            };
            mTradeListClient = WebSocketsManager.getInstance().createStompClient(mOnCreateStompClientListener);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(TransactionListEvent event) {

        if (event != null) {

            Gson gson = new Gson();
            Type type = new TypeToken<List<TradeListAndMarketOrdersBean.TradeListBean>>() {
            }.getType();
            List<TradeListAndMarketOrdersBean.TradeListBean> marketTradeList = null;
            try {
                marketTradeList = gson.fromJson(new Gson().toJson(event.bean.getMarketTradeList()), type);
            } catch (Exception e) {
                KLog.d("setTradingInfoData Exception = " + e.getMessage());
            }

            Logs.s("  onEventMainThreadonsize  " + marketTradeList.toString());

            setDecimal();
            if (marketTradeList != null && marketTradeList.size() > 0) {
                mTransactionAdapter.setNewData(marketTradeList);
                lin_load.setVisibility(View.GONE);
                recyclerview.setVisibility(View.VISIBLE);
            } else {
                lin_load.setVisibility(View.VISIBLE);
                recyclerview.setVisibility(View.GONE);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(TradeListAndMarketOrdersEvent event) {

        Logs.s("    TradeListAndMarketOrdersEvent1       " + event.mBean.getTradeList().size());

        if (event.mBean != null) {
            List<TradeListAndMarketOrdersBean.TradeListBean> marketTradeList = event.mBean.getTradeList();

            if (marketTradeList != null && marketTradeList.size() > 0) {
                mTransactionAdapter.setNewData(marketTradeList);
                lin_load.setVisibility(View.GONE);
                recyclerview.setVisibility(View.VISIBLE);
            } else {
                lin_load.setVisibility(View.VISIBLE);
                recyclerview.setVisibility(View.GONE);
            }
        }
    }

    private void setDecimal() {
        if (OAXApplication.coinsInfoMap.containsKey(marketId)) {
            IndexPageBean.DataBean.AllMaketListBean.MarketListBean marketListBean = OAXApplication.coinsInfoMap.get(marketId);
            mTransactionAdapter.setDecimals(marketListBean.getPriceDecimals(), marketListBean.getQtyDecimals());
        }
    }

}
