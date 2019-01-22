package cn.dagongniu.bitman.trading.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.dagongniu.bitman.OAXApplication;
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.account.LoginActivity;
import cn.dagongniu.bitman.base.BaseFragment;
import cn.dagongniu.bitman.constant.SPConstant;
import cn.dagongniu.bitman.constant.UMConstant;
import cn.dagongniu.bitman.customview.ClassicsHeader;
import cn.dagongniu.bitman.customview.TradingFragmentToolbar;
import cn.dagongniu.bitman.https.CommonJsonToBean;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.HttpUtils;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.https.UrlParams;
import cn.dagongniu.bitman.kline.adapter.CommitteeAdapter;
import cn.dagongniu.bitman.kline.bean.TradingInfoBean;
import cn.dagongniu.bitman.kline.fragment.CommitteeIView;
import cn.dagongniu.bitman.kline.fragment.TransactionFragment;
import cn.dagongniu.bitman.kline.presenter.CommitteePresenter;
import cn.dagongniu.bitman.kline.presenter.KLineActivityPresenter;
import cn.dagongniu.bitman.kline.view.TabEntity;
import cn.dagongniu.bitman.language.LanguageUtils;
import cn.dagongniu.bitman.main.bean.IndexPageBean;
import cn.dagongniu.bitman.main.presenter.IndexPagePresenter;
import cn.dagongniu.bitman.main.view.IndexPageView;
import cn.dagongniu.bitman.trading.EntrustOrderActivity;
import cn.dagongniu.bitman.trading.MarketChooseActivity;
import cn.dagongniu.bitman.trading.bean.Bean;
import cn.dagongniu.bitman.trading.bean.CurrentEntrustBean;
import cn.dagongniu.bitman.trading.bean.EntrustInfoBean;
import cn.dagongniu.bitman.trading.bean.TradeListAndMarketOrdersBean;
import cn.dagongniu.bitman.trading.presenter.EntrustPresenter;
import cn.dagongniu.bitman.utils.AppManager;
import cn.dagongniu.bitman.utils.Logs;
import cn.dagongniu.bitman.utils.SPUtils;
import cn.dagongniu.bitman.utils.SkipActivityUtil;
import cn.dagongniu.bitman.utils.ToastUtil;
import cn.dagongniu.bitman.utils.ViewUtils;
import cn.dagongniu.bitman.utils.events.KLineBuyOrSellEvent;
import cn.dagongniu.bitman.utils.events.MyEvents;
import cn.dagongniu.bitman.utils.events.RefreshHomeFragmentEvent;
import cn.dagongniu.bitman.utils.events.SelectTradingCoinEvent;
import cn.dagongniu.bitman.utils.events.TransactionListEvent;
import cn.dagongniu.bitman.utils.events.UpdateTradingInfoEvent;
import cn.dagongniu.bitman.utils.um.UMManager;
import cn.dagongniu.bitman.views.MyRecyclerView;
import cn.dagongniu.bitman.views.pager.NoScrollPager;
import cn.dagongniu.bitman.views.path.DepthDataBean;
import cn.dagongniu.bitman.views.path.DepthMapView;

/**
 * 交易首页
 * <p>
 * 已在 TabView 的 initTabChildView() 中预加载
 */
public class OaxTradingFragment extends BaseFragment implements OaxTradingIView, CommitteeIView, EntrustIView, IndexPageView, OnRefreshListener {

    @BindView(R.id.mytrading_toolbar)
    TradingFragmentToolbar mytradingToolbar;
    @BindView(R.id.buy_and_sell_tab_layout)
    CommonTabLayout buyAndSellTabLayout;
    @BindView(R.id.bt_trading_buy_sell)
    Button btTradingBuySell;
    @BindView(R.id.buy_recyclerview)
    MyRecyclerView buyRecyclerview;
    @BindView(R.id.sell_recyclerview)
    MyRecyclerView sellRecyclerview;

    @BindView(R.id.et_buy_sell_price)
    EditText etBuySellPrice;
    @BindView(R.id.iv_price_minus)
    RelativeLayout ivPriceMinus;
    @BindView(R.id.iv_price_add)
    RelativeLayout ivPriceAdd;
    @BindView(R.id.et_buy_sell_volume)
    EditText etBuySellVolume;
    @BindView(R.id.iv_volume_minus)
    RelativeLayout ivVolumeMinus;
    @BindView(R.id.iv_volume_add)
    RelativeLayout ivVolumeAdd;
    @BindView(R.id.RadioButton25)
    RadioButton RadioButton25;
    @BindView(R.id.RadioButton50)
    RadioButton RadioButton50;
    @BindView(R.id.RadioButton75)
    RadioButton RadioButton75;
    @BindView(R.id.RadioButton100)
    RadioButton RadioButton100;
    @BindView(R.id.tv_total_prices)
    TextView tvTotalPrices;
    @BindView(R.id.tv_feeRate)
    TextView tvFeeRate;
    @BindView(R.id.tv_available)
    TextView tvAvailable;
    @BindView(R.id.RadioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.trading_refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.depthmap_id)
    DepthMapView depthmap_id;

    @BindView(R.id.price_tv)
    TextView price_tv;//最新成交价
    @BindView(R.id.amout_tv)
    TextView amout_tv;//涨跌幅
    @BindView(R.id.cny_price_tv)
    TextView cny_price_tv;//人民币价格

    @BindView(R.id.withdrawal_order)
    TextView withdrawal_order;//撤单

    @BindView(R.id.tab_layout)
    TabLayout tab_layout;
    @BindView(R.id.trading_pager)
    NoScrollPager trading_pager;
    @BindView(R.id.asset_arrows_iv)
    ImageView asset_arrows_iv;

    @BindView(R.id.coordinator)
    AppBarLayout coordinator;

    @BindView(R.id.coordinator_id)
    CoordinatorLayout coordinator_id;

    private KLineActivityPresenter mKLinePresenter;
    private CommitteePresenter mCommitteePresenter;
    private IndexPagePresenter indexPagePresenter;
    private EntrustPresenter mEntrustPresenter;

    private CommitteeAdapter mBuyAdapter;
    private CommitteeAdapter mSellAdapter;
    private int mMarketId = -1;
    private int minType = 60;
    private TradingInfoBean.AllMaketListBean.MarketListBean currentCoin = null;
    private double mPricePlusOrMinusLimit = 0.001;
    private double mVolumePlusOrMinusLimit = 0.001;
    private EntrustInfoBean mEntrustInfoBean;//余额 委托列表等
    private int mQtyDecimals = 4;//数量精度
    private int mPriceDecimals = 8;//价格精度
    private String mEtBuyPriceText = "";
    private String mEtBuyVolumeText = "";
    private String mEtSellPriceText = "";
    private String mEtSellVolumeText = "";
    private boolean mIsBuy = true;
    private int checkBuyId = 0;
    private int checkSellId = 0;

    private int beforeDot = 20;

    ClassicsHeader mClassicsHeader;
    private boolean isRefreshError = false;
    private int mGreen;
    private int mGray;
    private int mRed;
    private int delay = 10000;
    private int msg_what = 101;
    private String price;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            topicTradeList();
            mHandler.removeMessages(msg_what);
            mHandler.sendEmptyMessageDelayed(msg_what, delay);
        }
    };
    private boolean mIsBackgroundOrUnScreenOn;
    private TradingInfoBean data1;
    private FragmentPagerAdapter mAdapter;
    private EntrustFragment entrustFragment;
    private String userId;

    //    private boolean mIsTopicTradeList;
    public void setSell(int position) {
        if (position == 0) {
            buyAndSellTabLayout.setIndicatorColor(getResources().getColor(R.color.bule));
            buyAndSellTabLayout.setTextSelectColor(getResources().getColor(R.color.bule));
        } else {
            buyAndSellTabLayout.setIndicatorColor(getResources().getColor(R.color.yellow5));
            buyAndSellTabLayout.setTextSelectColor(getResources().getColor(R.color.yellow5));
        }

        buyAndSellTabLayout.setCurrentTab(position);
        setTabLayoutSelect(position);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_oax_trading_frament;
    }

    @Override
    protected void initView() {
        super.initView();

        userId = SPUtils.getParamString(mContext, SPConstant.USER_ID, null);

        mGreen = getResources().getColor(R.color.bule);
        mGray = getResources().getColor(R.color.df_gray_666);
        mRed = getResources().getColor(R.color.kline_sell_bg);

        //尺寸拉伸
        mClassicsHeader = (ClassicsHeader) mRefreshLayout.getRefreshHeader();
        mClassicsHeader.setSpinnerStyle(SpinnerStyle.Scale);
        mRefreshLayout.setOnRefreshListener(this);

        LanguageUtils.setHeaderLanguage(mClassicsHeader, getActivity());

        buyRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        sellRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));

        initToolbar();

        initBuyAdapter();

        initSellAdapter();

        initPriceWatcher();

        initVolumeWatcher();

        initTabLayout();

        initTabLayout2();

        if (userId == null) {
            asset_arrows_iv.setVisibility(View.VISIBLE);
            withdrawal_order.setVisibility(View.GONE);
        } else {
            asset_arrows_iv.setVisibility(View.GONE);
            withdrawal_order.setVisibility(View.VISIBLE);
        }


        DisplayMetrics metrics = new DisplayMetrics();

        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int height = metrics.heightPixels;

        Logs.s("  addOnOffsetChangedListener   height  " + height);
        trading_pager.setMinimumHeight(height);


        coordinator.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                Logs.s("  addOnOffsetChangedListener    " + verticalOffset);
                if (height < 1300) {
                    if (verticalOffset < -850) {
                        asset_arrows_iv.setImageResource(R.mipmap.arrows_s_icon);
                    } else {
                        asset_arrows_iv.setImageResource(R.mipmap.arrows_x_icon);
                    }
                } else if (height < 1600) {
                    if (verticalOffset < -1000) {
                        asset_arrows_iv.setImageResource(R.mipmap.arrows_s_icon);
                    } else {
                        asset_arrows_iv.setImageResource(R.mipmap.arrows_x_icon);
                    }
                } else if (height < 2200) {
                    if (verticalOffset < -1300) {
                        asset_arrows_iv.setImageResource(R.mipmap.arrows_s_icon);
                    } else {
                        asset_arrows_iv.setImageResource(R.mipmap.arrows_x_icon);
                    }
                } else if (height < 2500) {
                    if (verticalOffset < -1400) {
                        asset_arrows_iv.setImageResource(R.mipmap.arrows_s_icon);
                    } else {
                        asset_arrows_iv.setImageResource(R.mipmap.arrows_x_icon);
                    }
                } else if (height < 3000) {
                    if (verticalOffset < -1500) {
                        asset_arrows_iv.setImageResource(R.mipmap.arrows_s_icon);
                    } else {
                        asset_arrows_iv.setImageResource(R.mipmap.arrows_x_icon);
                    }
                }

            }
        });
    }


    private void initTabLayout2() {

        ArrayList<Fragment> fragments = new ArrayList<>();
        ArrayList<String> listTitles = new ArrayList<>();
        listTitles.add(getResources().getString(R.string.my_entrust));
        listTitles.add(getResources().getString(R.string.kline_transaction));
        Bundle bundle = new Bundle();
        bundle.putInt(UrlParams.marketId, mMarketId);
        bundle.putBoolean("bloom", true);
        TransactionFragment transactionFragment = new TransactionFragment();
        transactionFragment.setArguments(bundle);
        Bundle bundle2 = new Bundle();
        bundle2.putBoolean("state", true);
        bundle2.putInt(UrlParams.marketId, mMarketId);
        entrustFragment = new EntrustFragment();
        entrustFragment.setArguments(bundle2);
        fragments.add(entrustFragment);
        fragments.add(transactionFragment);


        //ViewPager与TabLayout绑定后，这里获取到PageTitle就是Tab的Text
        mAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            //ViewPager与TabLayout绑定后，这里获取到PageTitle就是Tab的Text
            @Override
            public CharSequence getPageTitle(int position) {
                return listTitles.get(position);
            }
        };

        trading_pager.setScanScroll(true);
        trading_pager.setAdapter(mAdapter);
        tab_layout.setupWithViewPager(trading_pager);//将TabLayout和ViewPager关联起来。
        tab_layout.setTabsFromPagerAdapter(mAdapter);//给Tabs设置适配器

        withdrawal_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                withdrawal_order.setClickable(false);
                String userid = SPUtils.getParamString(getActivity(), SPConstant.USER_ID, null);
                if (userid != null && userid.length() > 0) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("marketId", mMarketId + "");
                    Logs.s("     withdrawal_order onNewData  " + mMarketId);
                    HttpUtils.getInstance().postLangIdToken(Http.Withdrawal_Order, hashMap, getActivity(), new OnBaseDataListener<String>() {
                        @Override
                        public void onNewData(String data) {
                            Bean bean = null;
                            try {
                                bean = new Gson().fromJson(data, Bean.class);

                            } catch (Exception e) {
                                bean = null;
                            }
                            Logs.s("     withdrawal_order onNewData  " + bean);

                            if (bean != null) {
                                if (bean.success) {
                                    entrustFragment.setMarketId(mMarketId);
                                    Logs.s("   entrustFragmentsetMarketId 1   " + mMarketId);
                                    ToastUtil.ShowToast(getResources().getString(R.string.success));
                                } else {
                                    ToastUtil.ShowToast(bean.msg);
                                }
                            }
                            withdrawal_order.setClickable(true);

                        }

                        @Override
                        public void onError(String code) {
                            withdrawal_order.setClickable(true);
                            Logs.s("     withdrawal_order onNewData  " + code);
                            ToastUtil.ShowToast(getResources().getString(R.string.network_errors));

                        }
                    }, RequestState.STATE_REFRESH);
                } else {
                    ToastUtil.ShowToast(getActivity().getResources().getString(R.string.please_login), getActivity());
                }


            }
        });
    }

    private void newDepth(List<TradeListAndMarketOrdersBean.BuyOrSellListBean> buyList, List<TradeListAndMarketOrdersBean.BuyOrSellListBean> sellList) {

        final List<DepthDataBean> listDepthBuy = new ArrayList<>();
        final List<DepthDataBean> listDepthSell = new ArrayList<>();

        final List<DepthDataBean> listDepthBuy1 = new ArrayList<>();
        final List<DepthDataBean> listDepthSell1 = new ArrayList<>();
        DepthDataBean obj;
        DepthDataBean obj1;

        for (TradeListAndMarketOrdersBean.BuyOrSellListBean buyOrSellListBean : buyList) {

            obj = new DepthDataBean();
            obj1 = new DepthDataBean();
            BigDecimal price = buyOrSellListBean.getPrice();

            BigDecimal amount = buyOrSellListBean.getAmount();


            BigDecimal bigDecimal = price.stripTrailingZeros();
            String s = bigDecimal.toPlainString();

            float v3 = Float.parseFloat(s);

            BigDecimal bigDecimal1 = amount.stripTrailingZeros();
            String s2 = bigDecimal1.toPlainString();
            float v2 = Float.parseFloat(s2);
            Logs.s("   bigDecimal1bigDecimal3  " + bigDecimal1);
            Logs.s("   bigDecimal1bigDecimal4  " + v2);
            float v = v3;
            float v1 = v2;
            obj.setPrice(v);
            obj.setVolume(v1);
            obj1.setPrice(v);
            obj1.setVolume(v1);
            listDepthBuy.add(obj);
            listDepthBuy1.add(obj1);
        }

        for (TradeListAndMarketOrdersBean.BuyOrSellListBean buyOrSellListBean : sellList) {

            obj = new DepthDataBean();
            obj1 = new DepthDataBean();
            BigDecimal price = buyOrSellListBean.getPrice();
            BigDecimal amount = buyOrSellListBean.getAmount();

            BigDecimal bigDecimal = price.stripTrailingZeros();

            String s = bigDecimal.toPlainString();

            float v3 = Float.parseFloat(s);

            BigDecimal bigDecimal1 = amount.stripTrailingZeros();
            String s2 = bigDecimal1.toPlainString();

            float v2 = Float.parseFloat(s2);

            Logs.s("   bigDecimal1bigDecimal1  " + bigDecimal1);
            Logs.s("   bigDecimal1bigDecimal2  " + v2);

            float v = v3;
            float v1 = v2;
            obj.setPrice(v);
            obj.setVolume(v1);
            obj1.setPrice(v);
            obj1.setVolume(v1);
            listDepthSell.add(obj);
            listDepthSell1.add(obj1);
        }
        try {
            getActivity().runOnUiThread(() -> {
                depthmap_id.setData(listDepthBuy, listDepthSell);
                depthmap_id.setData(listDepthBuy1, listDepthSell1);
            });

        } catch (Exception e) {

        }
    }

    private void initTabLayout() {
        buyAndSellTabLayout.setIndicatorColor(getResources().getColor(R.color.bule));
        buyAndSellTabLayout.setTextSelectColor(getResources().getColor(R.color.bule));
        buyAndSellTabLayout.setTextUnselectColor(getResources().getColor(R.color.df_9B9B9B));
        buyAndSellTabLayout.setIndicatorWidth(getResources().getDimension(R.dimen.dp15));
        ArrayList<CustomTabEntity> list = new ArrayList();
        final String[] timeSelectIndicatorTitles = {getResources().getString(R.string.buy), getResources().getString(R.string.sell)};
        for (int i = 0; i < timeSelectIndicatorTitles.length; i++) {
            list.add(new TabEntity(timeSelectIndicatorTitles[i], 0, 0));
        }
        buyAndSellTabLayout.setTabData(list);
        buyAndSellTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                Logs.s("  tablayoutonclick   " + position);
                if (position == 1) {
                    buyAndSellTabLayout.setIndicatorColor(getResources().getColor(R.color.yellow5));
                    buyAndSellTabLayout.setTextSelectColor(getResources().getColor(R.color.yellow5));
                } else {
                    buyAndSellTabLayout.setIndicatorColor(getResources().getColor(R.color.bule));
                    buyAndSellTabLayout.setTextSelectColor(getResources().getColor(R.color.bule));
                }
                setTabLayoutSelect(position);
            }

            @Override
            public void onTabReselect(int position) {
                setTabLayoutSelect(position);
            }
        });
    }

    private void setTabLayoutSelect(int position) {
        Logs.s(" 交易 mEtBuyPriceText   " + mEtBuyPriceText);
        Logs.s(" 交易 mEtBuyVolumeText   " + mEtBuyVolumeText);
        if (position == 0) {
            mIsBuy = true;
            setBuySellVolumeText(mEtBuyVolumeText);
            setEtBuyPriceText(price);
            Logs.s("   初始化单价3：  " + mEtBuyPriceText);
            setRightAvailable();
            radioGroup.clearCheck();
            if (checkBuyId != 0) {
                radioGroup.check(checkBuyId);
            }
            btTradingBuySell.setBackgroundResource(R.mipmap.buy_bg);
            btTradingBuySell.setText(R.string.buy);


        } else if (position == 1) {
            mIsBuy = false;
            setBuySellVolumeText(mEtSellVolumeText);
            setEtBuyPriceText(price);
            setLeftAvailable();
            radioGroup.clearCheck();
            if (checkSellId != 0) {
                radioGroup.check(checkSellId);
            }
            btTradingBuySell.setBackgroundResource(R.mipmap.sell_bg);
            btTradingBuySell.setText(R.string.sell);


        }

    }

    private void initToolbar() {

        mytradingToolbar.setLeftImgVisibility(false);
        mytradingToolbar.setRightNameText(R.string.entrust_order);
        mytradingToolbar.setLeftImgDroable(mContext);
        mytradingToolbar.setNameClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toOtherActivity(MarketChooseActivity.class);
                UMManager.onEvent(mContext, UMConstant.OaxTradingFragment, UMConstant.select_market);
            }
        });

        mytradingToolbar.setRightTvColor(getContext().getResources().getColor(R.color.df_gray_666));

        mytradingToolbar.setRightTvClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = SPUtils.getParamString(mContext, SPConstant.USER_ID, null);
                if (userId != null) {
                    //委托 订单
                    HashMap<String, Object> map = new HashMap<>();
                    map.put(UrlParams.marketId, mMarketId);
                    SkipActivityUtil.skipAnotherActivity(getActivity(), EntrustOrderActivity.class, map, false);
                } else {
                    SkipActivityUtil.skipAnotherActivity(mContext, LoginActivity.class);
                }
                UMManager.onEvent(mContext, UMConstant.OaxTradingFragment, UMConstant.Entrust_orders);
            }
        });
        mytradingToolbar.setLeftMoreClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //K线
                SkipActivityUtil.skipToKLineActivity(mMarketId, minType, getActivity());
                UMManager.onEvent(mContext, UMConstant.OaxTradingFragment, UMConstant.kline);
            }
        });
    }

    private void initBuyAdapter() {
        mBuyAdapter = new CommitteeAdapter(mContext, CommitteeAdapter.TYPE_BUY, false);
        mBuyAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            }
        });
        buyRecyclerview.setAdapter(mBuyAdapter);
        buyRecyclerview.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                TradeListAndMarketOrdersBean.BuyOrSellListBean bean = (TradeListAndMarketOrdersBean.BuyOrSellListBean) adapter.getItem(position);
                BigDecimal decimal = bean.getPrice();
                if (decimal != null) {
                    String price = decimal.setScale(mPriceDecimals, BigDecimal.ROUND_DOWN).toPlainString();
                    etBuySellPrice.setText(price);
                }
            }
        });
    }

    private void initSellAdapter() {
        mSellAdapter = new CommitteeAdapter(mContext, CommitteeAdapter.TYPE_SELL, false);
        mSellAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            }
        });
//        卖
        sellRecyclerview.setAdapter(mSellAdapter);
        sellRecyclerview.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                TradeListAndMarketOrdersBean.BuyOrSellListBean bean = (TradeListAndMarketOrdersBean.BuyOrSellListBean) adapter.getItem(position);
                BigDecimal decimal = bean.getPrice();
                if (decimal != null) {
                    String price = decimal.setScale(mPriceDecimals, BigDecimal.ROUND_DOWN).toPlainString();
                    etBuySellPrice.setText(price);
                }

            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        indexPagePresenter = new IndexPagePresenter(this, RequestState.STATE_REFRESH);
//        indexPagePresenter.getIndexPageModule();

    }

    private void updateCurrentCoin() {
        Logs.s(" IndexPageBean  updateCurrentCoin  1   ");
        IndexPageBean.DataBean.AllMaketListBean.MarketListBean marketListBean = OAXApplication.coinsInfoMap.get(mMarketId);

        Logs.s(" IndexPageBean  updateCurrentCoin  1   " + marketListBean.getMarketId());
        if (marketListBean != null) {
            if (currentCoin == null) {
                currentCoin = new TradingInfoBean.AllMaketListBean.MarketListBean();
            }
            currentCoin.setCnyPrice(marketListBean.getCnyPrice());
            currentCoin.setCoinId(marketListBean.getCoinId());
            currentCoin.setCoinName(marketListBean.getCoinName());
            currentCoin.setIncRate(marketListBean.getIncRate());
            currentCoin.setLastTradePrice(marketListBean.getLastTradePrice());
            currentCoin.setMarketCoinId(marketListBean.getMarketCoinId());
            currentCoin.setMarketCoinName(marketListBean.getMarketCoinName());
            currentCoin.setMarketId(marketListBean.getMarketId());
            currentCoin.setMaxPrice(marketListBean.getMaxPrice());
            currentCoin.setMinPrice(marketListBean.getMinPrice());
            currentCoin.setPriceDecimals(marketListBean.getPriceDecimals());
            currentCoin.setQtyDecimals(marketListBean.getQtyDecimals());
            currentCoin.setTotalAmount(marketListBean.getTotalAmount());
            currentCoin.setTradeQty(marketListBean.getTradeQty());
            mQtyDecimals = currentCoin.getQtyDecimals();
            mPriceDecimals = currentCoin.getPriceDecimals();
            KLog.d("currentCoin = " + new Gson().toJson(currentCoin));
        }
    }

    private void getData() {
        //基本数据
        HashMap<String, Object> map = new HashMap<>();
        map.put(UrlParams.marketId, mMarketId);
        map.put(UrlParams.minType, minType);
        mKLinePresenter = new KLineActivityPresenter(this);
        mKLinePresenter.getData(map, RequestState.STATE_REFRESH);
        //个人数据
        String userId = SPUtils.getParamString(mContext, SPConstant.USER_ID, null);
        if (userId != null) {
            if (mEntrustPresenter == null) {
                mEntrustPresenter = new EntrustPresenter(this);
            }
            mEntrustPresenter.getEntrustInfo(mMarketId, RequestState.STATE_REFRESH);//接口获取 用户余额 订单信息
//            mEntrustPresenter.topicTradeList(mMarketId, userId);//订阅 用户余额 订单信息
//            mIsTopicTradeList = true;
            mHandler.sendEmptyMessageDelayed(msg_what, delay);
        }
        initBuyAndSellWebSocket();
    }

    private void initBuyAndSellWebSocket() {
        //webSocket数据
        if (mCommitteePresenter == null) {
            mCommitteePresenter = new CommitteePresenter(this);
        }
        mCommitteePresenter.getTradeListAndMarketOrders(mMarketId + "");//买卖列表


    }


    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar
                .titleBar(mytradingToolbar)
                .statusBarDarkFont(true, 0.2f)
                .init();
    }

    @Override
    public void setTradingInfoData(CommonJsonToBean<TradingInfoBean> data) {
        KLog.d("setTradingInfoData  = " + new Gson().toJson(data));

        data1 = data.getData();

        EventBus.getDefault().post(new TransactionListEvent(data1));

        List<TradeListAndMarketOrdersBean.BuyOrSellListBean> sellList = new ArrayList<>();
        List<TradeListAndMarketOrdersBean.BuyOrSellListBean> buyList = new ArrayList<>();
        //设置买卖列表 费率
        if (data != null) {

            TradingInfoBean.MarketOrdersMapBean bean = data.getData().getMarketOrdersMap();
            String feeRate = data.getData().getFeeRate();
            KLog.d("feeRate = " + feeRate);
//            priceDecimals

            //人民币价+
            try {
                BigDecimal cnyPrice = data.getData().getAllMaketList().get(0).getMarketList().get(0).getCnyPrice();
                BigDecimal maxPrice = data.getData().getAllMaketList().get(0).getMarketList().get(0).getMaxPrice();
                String incRate = data.getData().getAllMaketList().get(0).getMarketList().get(0).getIncRate();
                Logs.s("   数据重叠： " + cnyPrice + " : " + maxPrice + " : " + incRate);

            } catch (Exception e) {

            }


            if (!TextUtils.isEmpty(feeRate)) {
                if (tvFeeRate == null) {
                    return;
                }
                try {
                    BigDecimal decimal = new BigDecimal(feeRate);
                    BigDecimal h = new BigDecimal(100);
                    tvFeeRate.setText(getResources().getString(R.string.trading_sxfy) + " " + subZeroAndDot(decimal.multiply(h).toPlainString()) + "%");
                } catch (Exception e) {
                    tvFeeRate.setText(getResources().getString(R.string.trading_sxfy) + "");
                }
            } else {
                if (tvFeeRate != null) {
                    tvFeeRate.setText(getResources().getString(R.string.trading_sxfy) + "");
                }
            }
            if (bean != null) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<TradeListAndMarketOrdersBean.BuyOrSellListBean>>() {
                }.getType();


                try {
                    sellList = gson.fromJson(new Gson().toJson(bean.getSellList()), type);
                    buyList = gson.fromJson(new Gson().toJson(bean.getBuyList()), type);
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

                mBuyAdapter.maxQty(bigDecimal);
                mBuyAdapter.isEmptyData(false);
                mBuyAdapter.setNewData(buyList);

                //取最大值
                BigDecimal bigDecimal2 = new BigDecimal(0);
                for (TradeListAndMarketOrdersBean.BuyOrSellListBean buyOrSellListBean : sellList) {
                    BigDecimal qty = buyOrSellListBean.getQty();
                    int i = qty.compareTo(bigDecimal2);
                    if (i == 1) {
                        bigDecimal2 = qty;
                    }
                }

                if (sellList != null && sellList.size() > 8) {
                    Collections.reverse(sellList);
                    List<TradeListAndMarketOrdersBean.BuyOrSellListBean> sellList2 = new ArrayList<>();
                    for (TradeListAndMarketOrdersBean.BuyOrSellListBean buyOrSellListBean : sellList) {
                        if (sellList2.size() < 8) {
                            sellList2.add(buyOrSellListBean);
                        }
                    }
                    Collections.reverse(sellList2);
                    mSellAdapter.maxQty(bigDecimal2);
                    mSellAdapter.isEmptyData(false);
                    mSellAdapter.setNewData(sellList2);
                    Logs.s("  sellList2sellList1 " + sellList2.size());
                } else if (sellList != null && sellList.size() == 0) {
//                    Collections.reverse(sellList);
                    mSellAdapter.maxQty(bigDecimal2);
                    mSellAdapter.isEmptyData(false);
                    mSellAdapter.setNewData(sellList);
                } else {
                    mSellAdapter.maxQty(bigDecimal2);
                    mSellAdapter.setNewData(sellList);
                }

                Logs.s("   深度图data：buyList " + buyList);
                Logs.s("   深度图data：sellList " + sellList);

            }
            newDepth(buyList, sellList);

        }
        //确定当前的交易对
        List<TradingInfoBean.AllMaketListBean> allMaketList = data.getData().getAllMaketList();
        if (allMaketList != null) {
            for (int i = 0; i < allMaketList.size(); i++) {
                TradingInfoBean.AllMaketListBean allMaketListBean = allMaketList.get(i);
                List<TradingInfoBean.AllMaketListBean.MarketListBean> marketList = allMaketListBean.getMarketList();
                if (marketList != null) {
                    for (int j = 0; j < marketList.size(); j++) {
                        TradingInfoBean.AllMaketListBean.MarketListBean marketListBean = marketList.get(j);
                        if (mMarketId == marketListBean.getMarketId()) {
                            currentCoin = marketListBean;
                            KLog.d("currentCoin = " + new Gson().toJson(currentCoin));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void getTradingInfoDataError(String msg) {

    }


    //买买买
    @Override
    public void setBuyOrSellState(CommonJsonToBean<String> data) {
        KLog.d("setBuyOrSellState = " + data.toJson(String.class));
        ToastUtil.ShowToast(data.getMsg(), getActivity());
        entrustFragment.setMarketId(mMarketId);
        Logs.s("   entrustFragmentsetMarketId 2   " + mMarketId);
        entrustFragment.initData();
        if (data.getSuccess()) {
            etBuySellVolume.setText("");
            etBuySellPrice.setText(price);
//            MainActivity activity = (MainActivity) getActivity();
//            activity.assetsFragment.onResume();
            myEvents.status_type = MyEvents.Withdrawal_Success;
            eventBus.post(myEvents);
//            mEtBuyPriceText = "";
//            mEtBuyVolumeText = "";
//            mEtSellPriceText = "";
//            mEtSellVolumeText = "";

        }
//        topicTradeList();
        if (mEntrustPresenter != null)
            mEntrustPresenter.getEntrustInfo(mMarketId, RequestState.STATE_REFRESH);//接口获取 用户余额 订单信息
    }

    @Override
    public void collectMarketState(CommonJsonToBean<String> data) {

    }

    @Override
    public void cancelCollectMarket(CommonJsonToBean<String> data) {

    }

    @Override
    public void setTopicKlineData(List<TradingInfoBean.KlineListBean> data) {

    }

    /**
     * 点对点获取 余额 订单信息
     */
    private void topicTradeList() {
//        if (!mIsTopicTradeList) {
//            return;
//        }
//        String userId = (String) SPUtils.getParamString(mContext, SPConstant.USER_ID, null);
//        if (userId != null) {
//            mEntrustPresenter.sendTradeList(mMarketId, Integer.parseInt(userId));
//        }
    }

    @Override
    public void showToast(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        ToastUtil.ShowToast(msg, getActivity());
    }

    /**
     * 买卖列表
     */
    @Override
    public void onNewCommitteeList(TradeListAndMarketOrdersBean bean) {
        KLog.d("onNewCommitteeList" + new Gson().toJson(bean));

        EventBus.getDefault().post(new TransactionListEvent(data1));
        List<TradeListAndMarketOrdersBean.BuyOrSellListBean> buyList = new ArrayList<>();
        List<TradeListAndMarketOrdersBean.BuyOrSellListBean> sellList = new ArrayList<>();
        if (bean != null) {
            buyList = bean.getBuyList();
            sellList = bean.getSellList();
            mBuyAdapter.isEmptyData(false);
            mSellAdapter.isEmptyData(false);

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
            mBuyAdapter.maxQty(bigDecimal);

            //刷新买卖列表
            mBuyAdapter.setNewData(buyList);
//            if (sellList != null && sellList.size() > 1) {
//                Collections.reverse(sellList);
//            }

            if (sellList != null && sellList.size() >= 8) {
                Collections.reverse(sellList);
                List<TradeListAndMarketOrdersBean.BuyOrSellListBean> sellList2 = new ArrayList<>();
                for (TradeListAndMarketOrdersBean.BuyOrSellListBean buyOrSellListBean : sellList) {
                    if (sellList2.size() < 8) {
                        sellList2.add(buyOrSellListBean);
                    }
                }
                Collections.reverse(sellList2);
                mSellAdapter.maxQty(bigDecimal1);
                mSellAdapter.setNewData(sellList2);
                Logs.s("  sellList2sellList2 " + sellList2.size());
            } else if (sellList != null && sellList.size() > 0) {
//                Collections.reverse(sellList);
                mSellAdapter.maxQty(bigDecimal1);
                mSellAdapter.setNewData(sellList);
            } else {
                mSellAdapter.maxQty(bigDecimal1);
                mSellAdapter.setNewData(sellList);
            }

//            mSellAdapter.maxQty(bigDecimal1);
//            mSellAdapter.setNewData(sellList);
        }
        newDepth(buyList, sellList);

    }


    private void setCurrentCoin() {
        updateCurrentCoin();

        if (currentCoin != null) {
            mytradingToolbar.setTitleNameText(currentCoin.getCoinName() + "/" + currentCoin.getMarketCoinName());
            mBuyAdapter.setDecimals(mPriceDecimals, mQtyDecimals);
            mSellAdapter.setDecimals(mPriceDecimals, mQtyDecimals);
            //设置价格变化
            setPriceChange();
//            updateLastCoin();
            changBuyOrSellInfo();
        }
        getData();
        setNoAvailable();
    }

    private void changBuyOrSellInfo() {
        KLog.d("changBuyOrSellInfo = " + currentCoin.getLastTradePrice());
        BigDecimal lastTradePrice = new BigDecimal(currentCoin.getLastTradePrice()).setScale(currentCoin.getPriceDecimals(), BigDecimal.ROUND_DOWN);
        String s = lastTradePrice.toPlainString();
        confirmPricePlusOrMinusLimit();
        confirmVolumePlusOrMinusLimit();
        etBuySellPrice.setHint(getResources().getString(R.string.entrust_order_jg) + "(" + currentCoin.getMarketCoinName() + ")");
        setEtBuyPriceText(s);
        price = s;
        Logs.s("   初始化单价4：  " + s);
        etBuySellVolume.setHint(getResources().getString(R.string.committee_amount) + "(" + currentCoin.getCoinName() + ")");
    }

    /**
     * 确定数量的加减幅度
     */
    private void confirmVolumePlusOrMinusLimit() {
        String s = "0.";
        int qtyDecimals = mQtyDecimals;
        for (int i = 0; i < qtyDecimals; i++) {
            if (i == qtyDecimals - 1) {
                s = s + "1";
            } else {
                s = s + "0";
            }
        }
        try {
            mVolumePlusOrMinusLimit = new BigDecimal(s).doubleValue();
        } catch (Exception e) {
        }
        KLog.d("mVolumePlusOrMinusLimit = " + mVolumePlusOrMinusLimit);
    }

    /**
     * 确定价格的加减幅度
     */
    private void confirmPricePlusOrMinusLimit() {
        int priceDecimal = currentCoin.getPriceDecimals();
        String s = "0.";
        for (int i = 0; i < priceDecimal; i++) {
            if (i == priceDecimal - 1) {
                s = s + "1";
            } else {
                s = s + "0";
            }
        }
        try {
            mPricePlusOrMinusLimit = new BigDecimal(s).doubleValue();
        } catch (Exception e) {
        }

        KLog.d("mPricePlusOrMinusLimit = " + mPricePlusOrMinusLimit);
    }

    private void setPriceChange() {
        //最近成交价
        try {
            BigDecimal lastTradePrice = new BigDecimal(currentCoin.getLastTradePrice());
            BigDecimal decimal = lastTradePrice.setScale(currentCoin.getPriceDecimals(), BigDecimal.ROUND_DOWN);
            price_tv.setText(decimal.toPlainString());
        } catch (Exception e) {

        }

        setCny(currentCoin.getCnyPrice(), currentCoin.getLastTradePrice());

        //涨跌幅
        double incRate = 0;
        try {
            incRate = Double.parseDouble(currentCoin.getIncRate());
            BigDecimal incRateBigDecimal = new BigDecimal(currentCoin.getIncRate()).setScale(2, BigDecimal.ROUND_DOWN);
            if (incRate > 0) {
                amout_tv.setText("+" + incRateBigDecimal.toPlainString() + "%");
            } else {
                amout_tv.setText(incRateBigDecimal.toPlainString() + "%");
            }
        } catch (Exception e) {
        }
        if (incRate > 0) {
            price_tv.setTextColor(mGreen);
            amout_tv.setTextColor(mGreen);
        } else if (incRate == 0) {
            price_tv.setTextColor(mGray);
            amout_tv.setTextColor(mGray);
        } else {
            price_tv.setTextColor(mRed);
            amout_tv.setTextColor(mRed);
        }
    }

    private void setCny(BigDecimal str, String str2) {
        //人民币价格
        try {
            if (currentCoin.getCoinId() == 25) {
                //人民币价格
                BigDecimal cny = str.multiply(new BigDecimal(str2))
                        .setScale(4, BigDecimal.ROUND_DOWN);

                cny_price_tv.setText("≈¥" + cny.toPlainString());
            } else {
                //人民币价格
                BigDecimal cny = currentCoin.getCnyPrice().multiply(new BigDecimal(currentCoin.getLastTradePrice()))
                        .setScale(2, BigDecimal.ROUND_DOWN);

                Logs.s("   currentCoin.getCnyPrice() 1  " + currentCoin.getCnyPrice());
                Logs.s("   currentCoin.getCnyPrice() 2  " + currentCoin.getLastTradePrice());
                Logs.s("   currentCoin.getCnyPrice() 3  " + cny);


                cny_price_tv.setText("≈¥" + cny.toPlainString());
            }
        } catch (Exception e) {

        }
    }

    /**
     * 去掉小数后的0
     *
     * @param s
     * @return
     */
    public String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }

    private void showNoDefaultId() {
        ViewUtils.showKProgressHUD(mContext, R.drawable.errer_icon, R.string.network_request_failed);
    }

    @OnClick({R.id.bt_trading_buy_sell, R.id.tv_left_name, R.id.iv_price_minus, R.id.iv_price_add, R.id.iv_volume_minus, R.id.iv_volume_add, R.id.RadioButton25, R.id.RadioButton50, R.id.RadioButton75, R.id.RadioButton100})
    public void onClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_trading_buy_sell:

//                withdrawal_order.setClickable(true);

                String userId = SPUtils.getParamString(mContext, SPConstant.USER_ID, null);
                if (userId != null) {
                    checkBuyOrSellData();
                } else {
                    SkipActivityUtil.skipAnotherActivity(mContext, LoginActivity.class);
                }
                if (mIsBuy) {
                    UMManager.onEvent(mContext, UMConstant.OaxTradingFragment, UMConstant.buy);
                } else {
                    UMManager.onEvent(mContext, UMConstant.OaxTradingFragment, UMConstant.sell);
                }
                break;
            case R.id.iv_price_minus:
                priceMinus();
                break;
            case R.id.iv_price_add:
                priceAdd();
                break;
            case R.id.iv_volume_minus:
                volumeMinus();
                break;
            case R.id.iv_volume_add:
                volumeAdd();
                break;
            case R.id.RadioButton25:
                setVolumeRate("0.25", R.id.RadioButton25);
                UMManager.onEvent(mContext, UMConstant.OaxTradingFragment, UMConstant.percent_25);
                break;
            case R.id.RadioButton50:
                setVolumeRate("0.50", R.id.RadioButton50);
                UMManager.onEvent(mContext, UMConstant.OaxTradingFragment, UMConstant.percent_50);
                break;
            case R.id.RadioButton75:
                setVolumeRate("0.75", R.id.RadioButton75);
                UMManager.onEvent(mContext, UMConstant.OaxTradingFragment, UMConstant.percent_75);
                break;
            case R.id.RadioButton100:
                setVolumeRate("1.00", R.id.RadioButton100);
                UMManager.onEvent(mContext, UMConstant.OaxTradingFragment, UMConstant.percent_100);
                break;
        }
    }

    private void checkBuyOrSellData() {
        KLog.d("checkBuyOrSellData");
        String price = etBuySellPrice.getText().toString();
        if (TextUtils.isEmpty(price)) {
            ToastUtil.ShowToast(getResources().getString(R.string.please_input_price), getActivity());
            return;
        }
        String volume = etBuySellVolume.getText().toString();
        if (TextUtils.isEmpty(volume)) {
            ToastUtil.ShowToast(getResources().getString(R.string.please_input_volume), getActivity());
            return;
        }
        BigDecimal priceDecimal = new BigDecimal(price);
        KLog.d("checkBuyOrSellData priceDecimal = " + priceDecimal.toPlainString());
        if (priceDecimal.compareTo(BigDecimal.ZERO) == 0) {
            ToastUtil.ShowToast(getResources().getString(R.string.price_can_not_be_zero), getActivity());
            return;
        }
        BigDecimal volumeDecimal = new BigDecimal(volume);
        KLog.d("checkBuyOrSellData priceDecimal = " + volumeDecimal.toPlainString());
        if (volumeDecimal.compareTo(BigDecimal.ZERO) == 0) {
            ToastUtil.ShowToast(getResources().getString(R.string.volume_can_not_be_zero), getActivity());
            return;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put(UrlParams.marketId, mMarketId);
        map.put(UrlParams.price, price);
        map.put(UrlParams.qty, volume);
        if (mIsBuy) {
            map.put(UrlParams.type, "1");
        } else {
            map.put(UrlParams.type, "2");
        }
        mKLinePresenter.sendBuyOrSellData(map, RequestState.STATE_DIALOG);
    }

    private void setVolumeRate(String rate, int id) {
        if (mIsBuy) {
            checkBuyId = id;
        } else {
            checkSellId = id;
        }
        if (mEntrustInfoBean != null) {
            BigDecimal accountDecimal = null;
            if (mIsBuy) {
                try {
                    accountDecimal = mEntrustInfoBean.getCoinBalance().getRightCoinBalance().divide(new BigDecimal(etBuySellPrice.getText().toString()), mPriceDecimals, BigDecimal.ROUND_DOWN);
                } catch (Exception e) {
                    KLog.d("accountDecimal = " + e.getMessage());
                    accountDecimal = new BigDecimal("0");
                }
            } else {
                accountDecimal = mEntrustInfoBean.getCoinBalance().getLeftCoinBalance();
            }
            BigDecimal rateDecimal = new BigDecimal(rate);

            String s = accountDecimal.multiply(rateDecimal).setScale(mQtyDecimals, BigDecimal.ROUND_DOWN).toPlainString();
            setBuySellVolumeText(s);
        }
    }

    private void setBuySellVolumeText(String s) {
        etBuySellVolume.setText(s);
        if (!TextUtils.isEmpty(s)) {
            try {
                etBuySellVolume.setSelection(s.length());
            } catch (Exception e) {

            }
        }
    }

    private void setEtBuyPriceText(String s) {
        Logs.s("   交易单价:   " + s);

        etBuySellPrice.setText(s);
        if (!TextUtils.isEmpty(s)) {
            try {
                etBuySellPrice.setSelection(s.length());
            } catch (Exception e) {

            }
        }
    }

    private void volumeAdd() {
        String volume = etBuySellVolume.getText().toString();
        if (TextUtils.isEmpty(volume)) {
            String s = new BigDecimal(mVolumePlusOrMinusLimit).setScale(mQtyDecimals, BigDecimal.ROUND_DOWN).toPlainString();
            setBuySellVolumeText(s);
        } else {
            try {
                BigDecimal etVolume = new BigDecimal(volume);
                BigDecimal plusOrMinusLimit = new BigDecimal(subZeroAndDot(Double.toString(mVolumePlusOrMinusLimit)));
                int up = getScale(1);
                setBuySellVolumeText(etVolume.add(plusOrMinusLimit).setScale(up, BigDecimal.ROUND_HALF_UP).toPlainString());
            } catch (Exception e) {

            }
        }
    }

    private void volumeMinus() {
        String volume = etBuySellVolume.getText().toString();
        if (TextUtils.isEmpty(volume)) {
            BigDecimal pricePlusOrMinusLimit = new BigDecimal(mVolumePlusOrMinusLimit);
            setBuySellVolumeText(String.valueOf(pricePlusOrMinusLimit.subtract(pricePlusOrMinusLimit).doubleValue()));
        } else {
            try {
                BigDecimal etVolume = new BigDecimal(volume);
                double c = etVolume.doubleValue();
                if (c == 0) {
                    return;
                }
                BigDecimal plusOrMinusLimit = new BigDecimal(mVolumePlusOrMinusLimit);
                int up = getScale(1);
                setBuySellVolumeText(etVolume.subtract(plusOrMinusLimit).setScale(up, BigDecimal.ROUND_HALF_UP).toPlainString());
            } catch (Exception e) {

            }
        }

    }

    private void priceMinus() {
        String price = etBuySellPrice.getText().toString();
        if (TextUtils.isEmpty(price)) {
            BigDecimal pricePlusOrMinusLimit = new BigDecimal(mPricePlusOrMinusLimit);
            setEtBuyPriceText(String.valueOf(pricePlusOrMinusLimit.subtract(pricePlusOrMinusLimit).doubleValue()));
            Logs.s("   初始化单价1：  " + String.valueOf(pricePlusOrMinusLimit.subtract(pricePlusOrMinusLimit).doubleValue()));
        } else {
            try {
                BigDecimal etPrice = new BigDecimal(price);
                double c = etPrice.doubleValue();
                if (c == 0) {
                    return;
                }
                BigDecimal plusOrMinusLimit = new BigDecimal(mPricePlusOrMinusLimit);
                int up = getScale(2);
                setEtBuyPriceText(etPrice.subtract(plusOrMinusLimit).setScale(up, BigDecimal.ROUND_HALF_UP).toPlainString());
                Logs.s("   初始化单价5：  " + etPrice.subtract(plusOrMinusLimit).setScale(up, BigDecimal.ROUND_HALF_UP).toPlainString());
            } catch (Exception e) {

            }
        }
    }

    private void priceAdd() {
        String price = etBuySellPrice.getText().toString();
        if (TextUtils.isEmpty(price)) {
            BigDecimal decimal = new BigDecimal(mPricePlusOrMinusLimit);
            String s = decimal.setScale(mPriceDecimals, BigDecimal.ROUND_DOWN).toPlainString();
            setEtBuyPriceText(s);
//            String trim = ivPriceAdd.getText().toString().trim();
            Logs.s("   初始化单价5：  " + s);

        } else {
            try {
                BigDecimal etPrice = new BigDecimal(price);
                BigDecimal plusOrMinusLimit = new BigDecimal(mPricePlusOrMinusLimit);
                int up = getScale(2);
                setEtBuyPriceText(etPrice.add(plusOrMinusLimit).setScale(up, BigDecimal.ROUND_HALF_UP).toPlainString());
                Logs.s("   初始化单价2：  " + etPrice.add(plusOrMinusLimit).setScale(up, BigDecimal.ROUND_HALF_UP).toPlainString());
//                String trim = ivPriceMinus.getText().toString().trim();
            } catch (Exception e) {
            }
        }
    }


    private void initPriceWatcher() {
        etBuySellPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mIsBuy) {
                    mEtBuyPriceText = s.toString();
                    setBuyTextChange();
                } else {
                    mEtSellPriceText = s.toString();
                    setSellTextChange();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                formatText(editable, true);
            }
        });
    }

    private void formatText(Editable editable, boolean isFromPrice) {
        String temp = editable.toString();
        int posDot = temp.indexOf(".");
        //直接输入小数点的情况
        if (posDot == 0) {
            editable.insert(0, "0");
            KLog.d("editable = " + editable.toString());
            return;
        }
        //连续输入0
        if (temp.equals("00")) {
            editable.delete(1, 2);
            return;
        }
        //输入"08" 等类似情况
        if (temp.startsWith("0") && temp.length() > 1 && (posDot == -1 || posDot > 1)) {
            editable.delete(0, 1);
            return;
        }

        //不包含小数点 不限制小数点前位数
        if (posDot < 0 && beforeDot == -1) {
            //do nothing 仅仅为了理解逻辑而已
            return;
        } else if (posDot < 0 && beforeDot != -1) {
            //不包含小数点 限制小数点前位数
            if (temp.length() <= beforeDot) {
                //do nothing 仅仅为了理解逻辑而已
            } else {
                editable.delete(beforeDot, beforeDot + 1);
            }
            return;
        }
        if (isFromPrice) {
            //如果包含小数点 限制小数点后位数
            if (temp.length() - posDot - 1 > mPriceDecimals && mPriceDecimals != -1) {
                editable.delete(posDot + mPriceDecimals + 1, posDot + mPriceDecimals + 2);//删除小数点后多余位数
            }
        } else {
            if (temp.length() - posDot - 1 > mQtyDecimals && mQtyDecimals != -1) {
                editable.delete(posDot + mQtyDecimals + 1, posDot + mQtyDecimals + 2);//删除小数点后多余位数
            }
        }
    }

    private void initVolumeWatcher() {
        etBuySellVolume.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mIsBuy) {
                    mEtBuyVolumeText = s.toString();
                    setBuyTextChange();
                } else {
                    mEtSellVolumeText = s.toString();
                    setSellTextChange();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                formatText(editable, false);
            }
        });
    }


    private void setSellTextChange() {
        String totalDes = getResources().getString(R.string.total_price) + ": ";
        if (TextUtils.isEmpty(mEtSellPriceText) || TextUtils.isEmpty(mEtSellVolumeText)) {
            BigDecimal decimal = new BigDecimal("0");

            if (currentCoin == null) {
                Logs.s("   总价 mPriceDecimals  1  " + mPriceDecimals);
//                BigDecimal bigDecimal = decimal.setScale(mPriceDecimals, BigDecimal.ROUND_DOWN);
                String s = decimal.stripTrailingZeros().toPlainString();

                tvTotalPrices.setText(totalDes + s);
            } else {
                Logs.s("   总价 mPriceDecimals  2  " + mPriceDecimals);
//                String s = decimal.setScale(mPriceDecimals, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
                BigDecimal bigDecimal = decimal.stripTrailingZeros();
                tvTotalPrices.setText(totalDes + bigDecimal.toPlainString() + "(" + currentCoin.getMarketCoinName() + ")");
            }
            return;
        }
        try {
            BigDecimal price = new BigDecimal(mEtSellPriceText);
            BigDecimal volume = new BigDecimal(mEtSellVolumeText);
            price.stripTrailingZeros();
            volume.stripTrailingZeros();
            if (currentCoin == null) {
                Logs.s("   总价 mPriceDecimals  price1  " + price);
                Logs.s("   总价 mPriceDecimals  volume1  " + volume);
//                .setScale(mPriceDecimals, BigDecimal.ROUND_DOWN);
                BigDecimal bigDecimal = price.multiply(volume);
                BigDecimal bigDecimal1 = bigDecimal.stripTrailingZeros();
                tvTotalPrices.setText(totalDes + bigDecimal1.toPlainString());
            } else {
                Logs.s("   总价 mPriceDecimals  price  " + price);
                Logs.s("   总价 mPriceDecimals  volume  " + volume);
//                .setScale(mPriceDecimals, BigDecimal.ROUND_DOWN);
                BigDecimal bigDecimal = price.multiply(volume);
                BigDecimal bigDecimal1 = bigDecimal.stripTrailingZeros();
                tvTotalPrices.setText(totalDes + bigDecimal1.toPlainString() + "(" + currentCoin.getMarketCoinName() + ")");
            }
        } catch (Exception e) {
            KLog.d("Exception = " + e.getMessage());
        }
    }

    private void setBuyTextChange() {
        String totalDes = getResources().getString(R.string.total_price) + ": ";
        if (TextUtils.isEmpty(mEtBuyPriceText) || TextUtils.isEmpty(mEtBuyVolumeText)) {
            BigDecimal decimal = new BigDecimal("0");

            if (currentCoin == null) {
//                BigDecimal bigDecimal = decimal.setScale(mPriceDecimals, BigDecimal.ROUND_DOWN);
                BigDecimal bigDecimal1 = decimal.stripTrailingZeros();
                tvTotalPrices.setText(totalDes + bigDecimal1.toPlainString());

            } else {
//                BigDecimal bigDecimal = decimal.setScale(mPriceDecimals, BigDecimal.ROUND_DOWN);
                BigDecimal bigDecimal1 = decimal.stripTrailingZeros();
                tvTotalPrices.setText(totalDes + bigDecimal1.toPlainString() + "(" + currentCoin.getMarketCoinName() + ")");
            }
            return;
        }
        try {
            BigDecimal price = new BigDecimal(mEtBuyPriceText);
            BigDecimal volume = new BigDecimal(mEtBuyVolumeText);
            if (currentCoin == null) {
//                .setScale(mPriceDecimals, BigDecimal.ROUND_DOWN);
                BigDecimal bigDecimal = price.multiply(volume);
                tvTotalPrices.setText(totalDes + bigDecimal.stripTrailingZeros().toPlainString());

                Logs.s("   交易单价：2  " + totalDes + price.multiply(volume).setScale(mPriceDecimals, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString());
            } else {
//                .setScale(mPriceDecimals, BigDecimal.ROUND_DOWN);
                BigDecimal bigDecimal = price.multiply(volume);

                tvTotalPrices.setText(totalDes + bigDecimal.stripTrailingZeros().toPlainString() + "(" + currentCoin.getMarketCoinName() + ")");
            }
        } catch (Exception e) {
            KLog.d("Exception = " + e.getMessage());
        }
    }

    /**
     * @param type 1数量精度 2价格精度
     * @return
     */
    private int getScale(int type) {
        int up = 3;
        if (type == 1) {
            if (currentCoin != null) {
                up = mQtyDecimals;
            }
        } else if (type == 2) {
            if (currentCoin != null) {
                up = mPriceDecimals;
            }
        }
        return up;
    }

    /**
     * 用户余额 订单信息
     *
     * @param bean
     */
    @Override
    public void onTopicTradeListData(EntrustInfoBean bean) {
        KLog.d("onTopicTradeListData = " + new Gson().toJson(bean));//个人数据
        String userId = SPUtils.getParamString(mContext, SPConstant.USER_ID, null);
        KLog.d("userId = " + userId);
        if (userId != null) {
            mEntrustInfoBean = bean;
            if (mIsBuy) {
                setRightAvailable();
            } else {
                setLeftAvailable();
            }
        } else {
            KLog.d(" mEntrustInfoBean = null");
            mEntrustInfoBean = null;
            setNoAvailable();
        }
    }

    @Override
    public void onCancellationsState(CommonJsonToBean<String> state) {

    }

    @Override
    public void onCurrentEnstrust(CommonJsonToBean<CurrentEntrustBean> bean, int state) {

    }

    @Override
    public void onRefreshCurrentEnstrust(CommonJsonToBean<CurrentEntrustBean> bean) {

    }

    @Override
    public void onLoadMoreCurrentEnstrust(CommonJsonToBean<CurrentEntrustBean> bean) {

    }

    private void setRightAvailable() {
        if (currentCoin != null) {
            if (mEntrustInfoBean != null) {
                EntrustInfoBean.CoinBalanceBean coinBalance = mEntrustInfoBean.getCoinBalance();
                if (coinBalance == null) {
                    setNoAvailable();
                } else {
                    BigDecimal decimal = coinBalance.getRightCoinBalance().setScale(mPriceDecimals, BigDecimal.ROUND_DOWN);
                    if (tvAvailable != null) {
                        tvAvailable.setText(getResources().getString(R.string.trading_ky) + " " + decimal.stripTrailingZeros().toPlainString() + currentCoin.getMarketCoinName());
                    }
                }
            } else {
                setNoAvailable();
            }
        }
    }

    private void setLeftAvailable() {
        if (currentCoin != null) {
            if (mEntrustInfoBean != null) {
                EntrustInfoBean.CoinBalanceBean coinBalance = mEntrustInfoBean.getCoinBalance();
                if (coinBalance == null) {
                    setNoAvailable();
                } else {
                    BigDecimal decimal = coinBalance.getLeftCoinBalance().setScale(mPriceDecimals, BigDecimal.ROUND_DOWN);
                    if (tvAvailable != null) {
                        tvAvailable.setText(mContext.getResources().getString(R.string.trading_ky) + " " + decimal.stripTrailingZeros().toPlainString() + currentCoin.getCoinName());
                    }
                }
            } else {
                setNoAvailable();
            }
        }
    }

    @Subscribe
    public void onEvent(MyEvents event) {
        switch (event.status_type) {


            case MyEvents.LoginSuccess://登录成功通知
                KLog.d("setCurrentCoin LoginSuccess");
                setCurrentCoin();

                try {
                    asset_arrows_iv.setVisibility(View.GONE);
                    withdrawal_order.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                }

                break;
            case MyEvents.LoginEsc://退出登录
            case MyEvents.Token_failure://token失效通知
                mEntrustInfoBean = null;

                try {
                    asset_arrows_iv.setVisibility(View.VISIBLE);
                    withdrawal_order.setVisibility(View.GONE);
                } catch (Exception e) {
                }


//                if (mEntrustPresenter != null) {
//                    mIsTopicTradeList = false;
//                    mEntrustPresenter.unTopicTradeList();
//                }
                setNoAvailable();
                KLog.d("setCurrentCoin LoginEsc");
                setCurrentCoin();
                break;
        }
    }

    /**
     * 实时更新 （成交价，人民币，涨跌幅）
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(UpdateTradingInfoEvent event) {
        IndexPageBean.DataBean.AllMaketListBean.MarketListBean bean = OAXApplication.coinsInfoMap.get(mMarketId);
        int marketId = bean.getMarketId();

        Logs.s(" IndexPageBean  updateCurrentCoin  2   " + marketId);

        //最近成交价
        try {
            BigDecimal lastTradePrice = new BigDecimal(bean.getLastTradePrice());
            BigDecimal decimal = lastTradePrice.setScale(bean.getPriceDecimals(), BigDecimal.ROUND_DOWN);
            price_tv.setText(decimal.toPlainString());
        } catch (Exception e) {

        }

        //人民币价格
        try {
            BigDecimal cny = bean.getCnyPrice().multiply(new BigDecimal(bean.getLastTradePrice()))
                    .setScale(2, BigDecimal.ROUND_DOWN);
            KLog.d("CnyPrice = " + bean.getCnyPrice().multiply(new BigDecimal(bean.getLastTradePrice())).toPlainString());
            cny_price_tv.setText("≈¥" + cny.toPlainString());
        } catch (Exception e) {
        }

        //涨跌幅
        double incRate = 0;
        try {
            incRate = Double.parseDouble(bean.getIncRate());
            BigDecimal incRateBigDecimal = new BigDecimal(bean.getIncRate()).setScale(2, BigDecimal.ROUND_DOWN);
            if (incRate > 0) {
                amout_tv.setText("+" + incRateBigDecimal.toPlainString() + "%");
            } else {
                amout_tv.setText(incRateBigDecimal.toPlainString() + "%");
            }
        } catch (Exception e) {
        }
        if (incRate > 0) {
            price_tv.setTextColor(mGreen);
            amout_tv.setTextColor(mGreen);
        } else if (incRate == 0) {
            price_tv.setTextColor(mGray);
            amout_tv.setTextColor(mGray);
        } else {
            price_tv.setTextColor(mRed);
            amout_tv.setTextColor(mRed);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(KLineBuyOrSellEvent event) {
        KLog.d("mMarketId = " + mMarketId);
        mMarketId = event.marketId;
        entrustFragment.setMarketId(event.marketId);
        Logs.s("   entrustFragmentsetMarketId 3   " + event.marketId);
        KLog.d("event.marketId = " + event.marketId);
        mIsBuy = event.isBuy;
        setEditTextEmpty();
        if (mMarketId != event.marketId) {
            currentCoin = null;
            mMarketId = event.marketId;
            KLog.d("setCurrentCoin 2  " + mMarketId);
            if (mCommitteePresenter != null) {
                mCommitteePresenter.unTopicTradeListAndMarketOrders();
            }
//            if (mEntrustPresenter != null) {
//                mIsTopicTradeList = false;
//                mEntrustPresenter.unTopicTradeList();
//            }
            initToolbar();
            KLog.d("setCurrentCoin KLineBuyOrSellEvent");
            setCurrentCoin();
        }
        if (event.isBuy) {
            buyAndSellTabLayout.setCurrentTab(0);
            setTabLayoutSelect(0);
        } else {
            buyAndSellTabLayout.setCurrentTab(1);
            setTabLayoutSelect(1);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SelectTradingCoinEvent event) {
        KLog.d("SelectTradingCoinEvent = " + mMarketId);
        KLog.d("SelectTradingCoinEvent = " + event.marketId);
        if (mMarketId != event.marketId) {
            radioGroup.clearCheck();
            currentCoin = null;
            mMarketId = event.marketId;
            KLog.d("setCurrentCoin 3  " + mMarketId);
            if (mCommitteePresenter != null) {
                mCommitteePresenter.unTopicTradeListAndMarketOrders();
            }
//            if (mEntrustPresenter != null) {

//                mIsTopicTradeList = false;
//                mEntrustPresenter.unTopicTradeList();
//            }
            initToolbar();
            setEditTextEmpty();
            KLog.d("setCurrentCoin SelectTradingCoinEvent");
            setCurrentCoin();
            if (entrustFragment != null) {
                entrustFragment.setMarketId(mMarketId);
                Logs.s("   entrustFragmentsetMarketId 4   " + mMarketId);
            }
//            entrustFragment
        }
    }

    private void setEditTextEmpty() {
//        mEtBuyPriceText = "";
//        mEtBuyVolumeText = "";
//        mEtSellPriceText = "";
        mEtSellVolumeText = "";
//        etBuySellPrice.setText("");
        etBuySellVolume.setText("");
    }

    @Override
    public void setIndexPageData(IndexPageBean indexPageData) {
        KLog.d("setIndexPageData = " + new Gson().toJson(indexPageData));

        //所有交易对信息
        OAXApplication.setCoinsInfo(indexPageData.getData().getAllMaketList());
        OAXApplication.setCoinsInfoFromRecommend(indexPageData.getData().getRecommendMarketList());
        //用户收藏
        OAXApplication.setCollectCoinsMap(indexPageData.getData().getUserMaketList());
        mRefreshLayout.finishRefresh();
        //确定marketid
        if (indexPageData != null) {
            List<IndexPageBean.DataBean.AllMaketListBean> allMaketList = indexPageData.getData().getAllMaketList();
            if (allMaketList != null && allMaketList.size() > 0) {
                for (int i = 0; i < allMaketList.size(); i++) {
                    IndexPageBean.DataBean.AllMaketListBean allMaketListBean = allMaketList.get(i);
                    if (allMaketListBean != null) {
                        List<IndexPageBean.DataBean.AllMaketListBean.MarketListBean> marketList = allMaketListBean.getMarketList();
                        if (marketList != null && marketList.size() > 0) {
                            for (int j = 0; j < marketList.size(); j++) {
                                IndexPageBean.DataBean.AllMaketListBean.MarketListBean bean = marketList.get(j);
                                if (bean != null) {
                                    int marketId = bean.getMarketId();
                                    if (marketId > 0 && mMarketId == -1) {
                                        mMarketId = marketId;

                                        KLog.d("setCurrentCoin mMarketId  " + mMarketId);
                                        try {
                                            parseCurrentCoin(bean);
                                        } catch (Exception e) {

                                        }
                                        setCurrentCoin();
                                        setIsErrorData();
                                        return;
                                    } else if (marketId == mMarketId) {
                                        try {
                                            parseCurrentCoin(bean);
                                        } catch (Exception e) {

                                        }
                                        KLog.d("setCurrentCoin marketId == mMarketId");
                                        setCurrentCoin();
                                        setIsErrorData();
                                        return;
                                    } else {
                                        KLog.d("setCurrentCoin else");
                                        setCurrentCoin();
                                        setIsErrorData();
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void setIsErrorData() {
        if (isRefreshError) {
            isRefreshError = false;
            EventBus.getDefault().post(new RefreshHomeFragmentEvent());
        }
        //如果还是默认的marketID，则填充假数据
        if (mMarketId == -1) {
            setNoMarketIdState();
        }
    }

    private void parseCurrentCoin(IndexPageBean.DataBean.AllMaketListBean.MarketListBean bean) {
        Logs.s(" IndexPageBean  updateCurrentCoin  3   ");
        Logs.s(" IndexPageBean  updateCurrentCoin  3   " + bean.getMarketId());
        Gson gson = new Gson();
        String s = gson.toJson(bean);
        currentCoin = gson.fromJson(s, TradingInfoBean.AllMaketListBean.MarketListBean.class);
    }

    private void setNoMarketIdState() {
        try {
            mytradingToolbar.setTitleNameText("--/--");
            price_tv.setText("--");
            cny_price_tv.setText("--");
            amout_tv.setText("--");
            List<TradeListAndMarketOrdersBean.BuyOrSellListBean> list = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                TradeListAndMarketOrdersBean.BuyOrSellListBean bean = new TradeListAndMarketOrdersBean.BuyOrSellListBean();
                list.add(bean);
            }
            mBuyAdapter.isEmptyData(true);
            mSellAdapter.isEmptyData(true);
            mBuyAdapter.setNewData(list);
            mSellAdapter.setNewData(list);
        } catch (Exception e) {

        }

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        indexPagePresenter.getIndexPageModule();
        entrustFragment.initData();
    }

    @Override
    public void setRefreshIndexPageData(IndexPageBean indexPageData) {

    }

    @Override
    public void setSilentRefreshIndexPageData(IndexPageBean indexPageData) {

    }

    @Override
    public void setRefreshIndexPageDataErrer(String indexPageData) {

    }

    @Override
    public void refreshErrer() {
        if (mRefreshLayout != null) {
            mRefreshLayout.finishRefresh();
        }

        isRefreshError = true;
        mEntrustInfoBean = null;
        currentCoin = null;
        if (tvFeeRate != null) {
            tvFeeRate.setText(getResources().getString(R.string.trading_sxfy) + "");
        }
        setNoAvailable();
        setNoMarketIdState();
    }

    private void setNoAvailable() {
        String df = "0";
        if (tvAvailable != null) {
            if (currentCoin != null) {
                if (mIsBuy) {
                    tvAvailable.setText(getResources().getString(R.string.trading_ky) + " " + df + currentCoin.getMarketCoinName());
                } else {
                    tvAvailable.setText(getResources().getString(R.string.trading_ky) + " " + df + currentCoin.getCoinName());
                }
            } else {
                tvAvailable.setText(getResources().getString(R.string.trading_ky) + " " + df);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        indexPagePresenter.getIndexPageModule();

        UMManager.onResume(mContext, UMConstant.OaxTradingFragment);
        KLog.d("WebSocketsManager initWebsocket " + AppManager.isAppIsInBackground(mContext));
        KLog.d("WebSocketsManager initWebsocket " + OAXApplication.isScreenOn);
        if (!AppManager.isAppIsInBackground(mContext) && OAXApplication.isScreenOn && mIsBackgroundOrUnScreenOn) {
            String userId = (String) SPUtils.getParamString(mContext, SPConstant.USER_ID, null);
//            if (userId != null && mEntrustPresenter != null) {
//                mEntrustPresenter.topicTradeList(mMarketId, userId);
//                mIsTopicTradeList = true;
//            }
            if (mCommitteePresenter != null) {
                initBuyAndSellWebSocket();
            }
        }
    }

    @Override
    public void onStop() {
        KLog.d("mIsBackground = " + OAXApplication.mIsBackground);
        if (OAXApplication.mIsBackground || !OAXApplication.isScreenOn) {
            mIsBackgroundOrUnScreenOn = true;
//            if (mEntrustPresenter != null) {
//                mIsTopicTradeList = false;
//                mEntrustPresenter.unTopicTradeList();
//            }
            if (mCommitteePresenter != null) {
                mCommitteePresenter.unTopicTradeListAndMarketOrders();
            }
        } else {
            mIsBackgroundOrUnScreenOn = false;
        }
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        UMManager.onPause(mContext, UMConstant.OaxTradingFragment);
//        if (AppManager.isAppIsInBackground(mContext) || !OAXApplication.isScreenOn) {
//            mIsBackgroundOrUnScreenOn = true;
//            KLog.d("WebSocketsManager disconnect");
//            if (mEntrustPresenter != null) {
//                mEntrustPresenter.unTopicTradeList();
//            }
//            if (mCommitteePresenter != null) {
//                mCommitteePresenter.unTopicTradeListAndMarketOrders();
//            }
//        } else {
//            mIsBackgroundOrUnScreenOn = false;
//        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeMessages(msg_what);
            mHandler = null;
        }
        if (mCommitteePresenter != null) {
            mCommitteePresenter.unTopicTradeListAndMarketOrders();
        }
//        if (mEntrustPresenter != null) {
//            mIsTopicTradeList = false;
//            mEntrustPresenter.unTopicTradeList();
//        }
    }
}
