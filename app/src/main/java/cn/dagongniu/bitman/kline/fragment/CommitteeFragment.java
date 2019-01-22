package cn.dagongniu.bitman.kline.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.socks.library.KLog;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import cn.dagongniu.bitman.OAXApplication;
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.base.OAXBaseFragment;
import cn.dagongniu.bitman.constant.UMConstant;
import cn.dagongniu.bitman.customview.LoadingState;
import cn.dagongniu.bitman.https.UrlParams;
import cn.dagongniu.bitman.kline.adapter.CommitteeAdapter;
import cn.dagongniu.bitman.kline.bean.TradingInfoBean;
import cn.dagongniu.bitman.kline.presenter.CommitteePresenter;
import cn.dagongniu.bitman.main.bean.IndexPageBean;
import cn.dagongniu.bitman.trading.bean.TradeListAndMarketOrdersBean;
import cn.dagongniu.bitman.utils.AppManager;
import cn.dagongniu.bitman.utils.events.TradeListAndMarketOrdersEvent;
import cn.dagongniu.bitman.utils.events.TransactionListEvent;
import cn.dagongniu.bitman.utils.um.UMManager;

/**
 * 实时委托
 */
public class CommitteeFragment extends OAXBaseFragment implements CommitteeIView {

    @BindView(R.id.buy_recyclerview)
    RecyclerView buyRecyclerview;
    @BindView(R.id.sell_recyclerview)
    RecyclerView sellRecyclerview;
    private CommitteeAdapter mBuyAdapter;
    private CommitteeAdapter mSellAdapter;
    private CommitteePresenter mCommitteePresenter;
    private int marketId = 0;
    private int mPriceDecimal = 8;
    private int mVolDecimal = 5;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_committee;
    }

    @Override
    protected void initView() {
        super.initView();
        Bundle bundle = getArguments();
        if (bundle.containsKey(UrlParams.marketId)) {
            marketId = bundle.getInt(UrlParams.marketId);
        }
        if (OAXApplication.coinsInfoMap.containsKey(marketId)) {
            IndexPageBean.DataBean.AllMaketListBean.MarketListBean bean = OAXApplication.coinsInfoMap.get(marketId);
            mPriceDecimal = bean.getPriceDecimals();
            mVolDecimal = bean.getQtyDecimals();
        }

        mCommitteePresenter = new CommitteePresenter(this);
        buyRecyclerview.setLayoutManager(new LinearLayoutManager(mContext) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        });
        buyRecyclerview.setNestedScrollingEnabled(false);

        sellRecyclerview.setLayoutManager(new LinearLayoutManager(mContext) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        });
        sellRecyclerview.setNestedScrollingEnabled(false);
        initBuyAdapter();
        initSellAdapter();

    }

    @Override
    public void initData() {
        super.initData();
//        WebSocketManager.getInstance().registerMainMarket(Http.TOPIC_MARKETORDERS + marketId, new WebSocketManager.MainMarketStompListener() {
//            @Override
//            public void callData(StompMessage stompMessage) {
//                KLog.d("stompMessage = ", stompMessage.getPayload());
//            }
//        });
        mCommitteePresenter.getTradeListAndMarketOrders(marketId + "");
    }

    private void initBuyAdapter() {
        mBuyAdapter = new CommitteeAdapter(mContext, CommitteeAdapter.TYPE_BUY, true);
        mBuyAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            }
        });
        buyRecyclerview.setAdapter(mBuyAdapter);
        mBuyAdapter.setDecimals(mPriceDecimal, mVolDecimal);
    }

    private void initSellAdapter() {
        mSellAdapter = new CommitteeAdapter(mContext, CommitteeAdapter.TYPE_SELL, true);
        mSellAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            }
        });
        sellRecyclerview.setAdapter(mSellAdapter);
        mSellAdapter.setDecimals(mPriceDecimal, mVolDecimal);
    }

    @Override
    public void showToast(String str) {

    }


    @Override
    public void setXState(LoadingState xState, String msg) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(TransactionListEvent event) {
        if (event != null) {
            TradingInfoBean.MarketOrdersMapBean marketTradeList = event.bean.getMarketOrdersMap();
            if (marketTradeList != null) {
                KLog.d("onEventMainThread = size" + marketTradeList.getBuyList().size());
                KLog.d("onEventMainThread = size" + marketTradeList.getSellList().size());

                Gson gson = new Gson();
                Type type = new TypeToken<List<TradeListAndMarketOrdersBean.BuyOrSellListBean>>() {
                }.getType();
                List<TradeListAndMarketOrdersBean.BuyOrSellListBean> sellList = null;
                List<TradeListAndMarketOrdersBean.BuyOrSellListBean> buyList = null;
                try {
                    sellList = gson.fromJson(new Gson().toJson(marketTradeList.getSellList()), type);
                    buyList = gson.fromJson(new Gson().toJson(marketTradeList.getBuyList()), type);
                } catch (Exception e) {
                    KLog.d("setTradingInfoData Exception = " + e.getMessage());
                }

                BigDecimal bigDecimal = new BigDecimal(0);
                for (TradeListAndMarketOrdersBean.BuyOrSellListBean buyOrSellListBean : buyList) {
                    BigDecimal qty = buyOrSellListBean.getQty();
                    int i = qty.compareTo(bigDecimal);
                    if (i == 1) {
                        bigDecimal = qty;
                    }
                }

                BigDecimal bigDecimal1 = new BigDecimal(0);
                for (TradeListAndMarketOrdersBean.BuyOrSellListBean buyOrSellListBean : buyList) {
                    BigDecimal qty = buyOrSellListBean.getQty();
                    int i = qty.compareTo(bigDecimal1);
                    if (i == 1) {
                        bigDecimal1 = qty;
                    }
                }

                if (buyList != null && buyList.size() > 0) {
                    mBuyAdapter.maxQty(bigDecimal1);
                    mBuyAdapter.setNewData(buyList);
                }

                if (sellList != null && sellList.size() > 1) {
                    Collections.reverse(sellList);
                }

                if (sellList != null && sellList.size() > 0) {
                    mSellAdapter.maxQty(bigDecimal);
                    mSellAdapter.setNewData(sellList);
                }

            }
        }
    }

    @Override
    public void onNewCommitteeList(TradeListAndMarketOrdersBean bean) {
        KLog.d("TradeListAndMarketOrdersBean");
        if (bean != null) {
            EventBus.getDefault().post(new TradeListAndMarketOrdersEvent(bean));
            List<TradeListAndMarketOrdersBean.BuyOrSellListBean> buyList = bean.getBuyList();
            List<TradeListAndMarketOrdersBean.BuyOrSellListBean> sellList = bean.getSellList();

            BigDecimal bigDecimal = new BigDecimal(0);
            for (TradeListAndMarketOrdersBean.BuyOrSellListBean buyOrSellListBean : buyList) {
                BigDecimal qty = buyOrSellListBean.getQty();
                int i = qty.compareTo(bigDecimal);
                if (i == 1) {
                    bigDecimal = qty;
                }
            }


            BigDecimal bigDecimal1 = new BigDecimal(0);
            for (TradeListAndMarketOrdersBean.BuyOrSellListBean buyOrSellListBean : sellList) {
                BigDecimal qty = buyOrSellListBean.getQty();
                int i = qty.compareTo(bigDecimal1);
                if (i == 1) {
                    bigDecimal1 = qty;
                }
            }

            if (buyList != null && buyList.size() > 0) {
                mBuyAdapter.maxQty(bigDecimal);

                mBuyAdapter.setNewData(buyList);
            }

            if (sellList != null && sellList.size() > 1) {
                Collections.reverse(sellList);
            }


            if (sellList != null && sellList.size() > 0) {
                mSellAdapter.maxQty(bigDecimal1);
                mSellAdapter.setNewData(sellList);
            }

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        UMManager.onResume(mContext, UMConstant.OaxTradingFragment);
        if (!AppManager.isAppIsInBackground(mContext) && OAXApplication.isScreenOn) {
//            String userId = (String) SPUtils.getParamString(mContext, SPConstant.USER_ID, null);
            if (mCommitteePresenter != null) {
                mCommitteePresenter.getTradeListAndMarketOrders(marketId + "");
            }
            KLog.d("WebSocketsManager initWebsocket");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (OAXApplication.mIsBackground || !OAXApplication.isScreenOn) {
            KLog.d("WebSocketsManager disconnect");
            if (mCommitteePresenter != null) {
                mCommitteePresenter.unTopicTradeListAndMarketOrders();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        UMManager.onPause(mContext, UMConstant.OaxTradingFragment);
//        if (AppManager.isAppIsInBackground(mContext) || !OAXApplication.isScreenOn) {
//            KLog.d("WebSocketsManager disconnect");
//            if (mCommitteePresenter != null) {
//                mCommitteePresenter.unTopicTradeListAndMarketOrders();
//            }
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCommitteePresenter.unTopicTradeListAndMarketOrders();
    }
}
