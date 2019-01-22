package cn.dagongniu.bitman.kline;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.socks.library.KLog;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.dagongniu.bitman.OAXApplication;
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.account.LoginActivity;
import cn.dagongniu.bitman.base.OAXBaseActivity;
import cn.dagongniu.bitman.constant.Constant;
import cn.dagongniu.bitman.constant.SPConstant;
import cn.dagongniu.bitman.customview.CustomViewPager;
import cn.dagongniu.bitman.https.CommonJsonToBean;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.https.UrlParams;
import cn.dagongniu.bitman.kline.bean.DataParse;
import cn.dagongniu.bitman.kline.bean.TradingInfoBean;
import cn.dagongniu.bitman.kline.fragment.CommitteeFragment;
import cn.dagongniu.bitman.kline.fragment.TransactionFragment;
import cn.dagongniu.bitman.kline.kchart.CoupleChartGestureListener;
import cn.dagongniu.bitman.kline.kchart.MyBottomMarkerView;
import cn.dagongniu.bitman.kline.kchart.MyCombinedChart;
import cn.dagongniu.bitman.kline.kchart.MyLeftMarkerView;
import cn.dagongniu.bitman.kline.presenter.KLineActivityPresenter;
import cn.dagongniu.bitman.kline.view.CurrentItemInfoView;
import cn.dagongniu.bitman.kline.view.CurrentTransactionPriceView;
import cn.dagongniu.bitman.kline.view.KLineDaysSelectPopupWindow;
import cn.dagongniu.bitman.kline.view.KLineHoursSelectPopupWindow;
import cn.dagongniu.bitman.kline.view.KLineMinutesSelectPopupWindow;
import cn.dagongniu.bitman.kline.view.KLineTabView;
import cn.dagongniu.bitman.kline.view.KLineToolbar;
import cn.dagongniu.bitman.main.adapter.FragAdapter;
import cn.dagongniu.bitman.main.bean.IndexPageBean;
import cn.dagongniu.bitman.trading.fragment.OaxTradingIView;
import cn.dagongniu.bitman.utils.KLineUtils;
import cn.dagongniu.bitman.utils.Logs;
import cn.dagongniu.bitman.utils.SPUtils;
import cn.dagongniu.bitman.utils.SkipActivityUtil;
import cn.dagongniu.bitman.utils.ToastUtil;
import cn.dagongniu.bitman.utils.events.KLineBuyOrSellEvent;
import cn.dagongniu.bitman.utils.events.RefreshHomeFragmentEvent;
import cn.dagongniu.bitman.utils.events.TransactionListEvent;
import cn.dagongniu.bitman.utils.events.UpdateTradingInfoEvent;

public class KLinesActivity extends OAXBaseActivity implements OaxTradingIView {

    @BindView(R.id.kline_toolbar)
    KLineToolbar klineToolbar;
    @BindView(R.id.full_screen)
    ImageView fullScreen;
    @BindView(R.id.current_transaction_price)
    CurrentTransactionPriceView currentTransactionPrice;
    @BindView(R.id.kline_tab_id)
    MagicIndicator kline_tab_id;
    @BindView(R.id.main_market_view_pager)
    CustomViewPager viewPager;
    @BindView(R.id.tv_buy)
    TextView tvBuy;
    @BindView(R.id.tv_sell)
    TextView tvSell;
    @BindView(R.id.currentiteminfoview)
    CurrentItemInfoView mCurrentItemInfoView;
    @BindView(R.id.combinedchart)
    MyCombinedChart kChart;
    @BindView(R.id.barchart)
    MyCombinedChart volChart;

    @BindView(R.id.tab_minute)
    KLineTabView mTabMinute;
    @BindView(R.id.tab_minutes)
    KLineTabView mTabMinutes;
    @BindView(R.id.tab_hour)
    KLineTabView mTabHour;
    @BindView(R.id.tab_day)
    KLineTabView mTabDay;
    @BindView(R.id.tab_mouth)
    KLineTabView mTabMouth;

    KLineActivityPresenter mKLinePresenter;
    List<Fragment> transactionFragments = new ArrayList<>();
    String[] indicatorTitles = new String[2];
    XAxis xAxisVol, xAxisK;
    YAxis axisLeftVol, axisLeftK;
    YAxis axisRightVol, axisRightK;
    @BindView(R.id.ll_tab_minute)
    LinearLayout llTabMinute;
    @BindView(R.id.ll_tab_minutes)
    LinearLayout llTabMinutes;
    @BindView(R.id.ll_tab_hour)
    LinearLayout llTabHour;
    @BindView(R.id.ll_tab_day)
    LinearLayout llTabDay;
    @BindView(R.id.ll_tab_mouth)
    LinearLayout llTabMouth;
    private DataParse mData;
    private int mBarSpacePercent = 35;

    private int marketId = -1;
    private int minType = Constant.KLINE_MINTYPE_1;
    BarDataSet barDataSet;
    private int mkType = Constant.KLINE_KLINE;
    private boolean isDay = false;
    private int delayMillisMinutes = 20;
    private KLineMinutesSelectPopupWindow mMinutesSelectPopuWindow;
    private KLineHoursSelectPopupWindow mKLineHoursSelectPopupWindow;
    private KLineDaysSelectPopupWindow mKLineDaysSelectPopupWindow;
    private float mXscaleCombin = 35;
    final float mYscaleCombin = 1;
    private KProgressHUD dialog;
    private String mLoadingDataText = "";
    private boolean mIsRefreshKlineData = true;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            notifyDataChange();
        }
    };

    private ViewPortHandler viewPortHandlerCombin;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_klines;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar
                .titleBar(klineToolbar)
                .statusBarDarkFont(true)
                .init();
    }

    @Override
    protected void initView() {
        super.initView();
        Intent intent = getIntent();
        if (intent.hasExtra(UrlParams.marketId)) {
            marketId = intent.getIntExtra(UrlParams.marketId, 0);
        }
        indicatorTitles[0] = getResources().getString(R.string.kline_committee);
        indicatorTitles[1] = getResources().getString(R.string.kline_transaction);
        initFragments();
        initViewPager();
        initMagicIndicator();
        if (klineToolbar != null) {
            IndexPageBean.DataBean.AllMaketListBean.MarketListBean bean = OAXApplication.coinsInfoMap.get(marketId);
            if (bean != null) {
                klineToolbar.setTvTitle(bean.getCoinName() + "/" + bean.getMarketCoinName());

            }
            klineToolbar.setOnKLineToolbarClickListener(new KLineToolbar.OnKLineToolbarClickListener() {
                @Override
                public void onLeftClick() {
                    finish();
                }

                @Override
                public void onRightClick() {
                    String userId = SPUtils.getParamString(mContext, SPConstant.USER_ID, null);
                    if (userId != null) {
                        if (OAXApplication.collectCoinsMap.containsKey(marketId)) {
                            Logs.s("收藏onRightClick111");
                            mKLinePresenter.cancelCollectMarket(marketId, RequestState.STATE_DIALOG);
                            OAXApplication.isCollect = true;
                        } else {
                            Logs.s("收藏onRightClick222");
                            mKLinePresenter.collectMarket(marketId, RequestState.STATE_DIALOG);
                            OAXApplication.isCollect = false;
                        }
                    } else {
                        SkipActivityUtil.skipAnotherActivity(mContext, LoginActivity.class);
                    }
                }

                @Override
                public void onTvTitleClick() {

                }
            });
        }
        mLoadingDataText = getResources().getString(R.string.loading_data);
        changeCurrentTransactionPrice();
        initCollect();
        initChart();
    }

    private void initFragments() {

        CommitteeFragment committeeFragment = new CommitteeFragment();
        TransactionFragment transactionFragment = new TransactionFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(UrlParams.marketId, marketId);
        bundle.putBoolean("bgColorState", true);
        committeeFragment.setArguments(bundle);
        transactionFragment.setArguments(bundle);
//        viewPager.setObjectForPosition();
        transactionFragments.add(committeeFragment);
        transactionFragments.add(transactionFragment);
    }

    /**
     * 初始化图表
     */
    private void initChart() {

        volChart.setDrawBorders(true);
        volChart.setBorderWidth(1);
        volChart.setBorderColor(getResources().getColor(R.color.minute_grayLine));
        volChart.setDescription("");
        volChart.setDragEnabled(true);
        volChart.setScaleYEnabled(false);
        volChart.setNoDataText(mLoadingDataText);

        Legend barChartLegend = volChart.getLegend();
        barChartLegend.setEnabled(false);

        //bar x y轴
        xAxisVol = volChart.getXAxis();
        xAxisVol.setDrawLabels(false);
        xAxisVol.setDrawGridLines(false);
        xAxisVol.setDrawAxisLine(false);
        xAxisVol.setTextColor(getResources().getColor(R.color.bule));
        xAxisVol.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisVol.setGridColor(getResources().getColor(R.color.minute_grayLine));

        axisLeftVol = volChart.getAxisLeft();
        axisLeftVol.setDrawGridLines(false);
        axisLeftVol.setDrawAxisLine(false);
        axisLeftVol.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        axisLeftVol.setTextColor(getResources().getColor(R.color.bule));
        axisLeftVol.setDrawLabels(true);
        axisLeftVol.setSpaceTop(10);
        axisLeftVol.setSpaceBottom(0);
        axisLeftVol.setShowOnlyMinMax(true);

        axisRightVol = volChart.getAxisRight();
        axisRightVol.setDrawLabels(false);
        axisRightVol.setDrawGridLines(false);
        axisRightVol.setDrawAxisLine(false);

        /****************************************************************/

        kChart.setDrawBorders(true);
        kChart.setBorderWidth(1);
        kChart.setBorderColor(getResources().getColor(R.color.minute_grayLine));
        kChart.setDescription("");
        kChart.setDragEnabled(true);
        kChart.setScaleYEnabled(false);
        kChart.setNoDataText(mLoadingDataText);
        Legend combinedchartLegend = kChart.getLegend();
        combinedchartLegend.setEnabled(false);

        //K线 x y轴
        xAxisK = kChart.getXAxis();
        xAxisK.setDrawLabels(true);
        xAxisK.setDrawGridLines(false);
        xAxisK.setDrawAxisLine(false);
        xAxisK.setTextColor(getResources().getColor(R.color.bule));
        xAxisK.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisK.setGridColor(getResources().getColor(R.color.minute_grayLine));
        xAxisK.setAvoidFirstLastClipping(true);

        axisLeftK = kChart.getAxisLeft();
        axisLeftK.setDrawGridLines(true);
        axisLeftK.setDrawAxisLine(false);
        axisLeftK.setDrawLabels(true);
        axisLeftK.setTextColor(getResources().getColor(R.color.bule));
        axisLeftK.setGridColor(getResources().getColor(R.color.minute_grayLine));
        axisLeftK.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        axisLeftK.setLabelCount(5, true);
        axisLeftK.setSpaceTop(10);

        axisRightK = kChart.getAxisRight();
        axisRightK.setDrawLabels(false);
        axisRightK.setDrawGridLines(false);
        axisRightK.setDrawAxisLine(false);
        axisRightK.setGridColor(getResources().getColor(R.color.minute_grayLine));

        kChart.setDragDecelerationEnabled(true);
        volChart.setDragDecelerationEnabled(true);

        kChart.setDragDecelerationFrictionCoef(0.2f);
        volChart.setDragDecelerationFrictionCoef(0.2f);
        setHighLight();
    }

    private void setHighLight() {
        // 将K线控的滑动事件传递给交易量控件
        kChart.setOnChartGestureListener(new CoupleChartGestureListener(kChart, new Chart[]{volChart}));
        // 将交易量控件的滑动事件传递给K线控件
        volChart.setOnChartGestureListener(new CoupleChartGestureListener(volChart, new Chart[]{kChart}));

        volChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                int index = h.getXIndex();
//                kChart.setHighlightValue(new Highlight(index, dataSetIndex));
                kChart.setHighlightValue(new Highlight(index, Float.NaN, 1, dataSetIndex));
                setCurrentItemInfo(index);
                Logs.s("  按下k线：1  " + viewPortHandlerCombin.getMaxScaleX());
                Logs.s("  按下k线：1  " + viewPortHandlerCombin.getMaxScaleX());
            }

            @Override
            public void onNothingSelected() {
                kChart.highlightValue(null);
                Logs.s("  按下k线：2  ");
                Logs.s("  按下k线：2  " + viewPortHandlerCombin.getMaxScaleX());
                postCurrentItemInfo(false, null, 0, 0, 0, 0);
            }
        });
        kChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                int index = h.getXIndex();
                volChart.setHighlightValue(new Highlight(index, dataSetIndex));
                Logs.s("  按下k线：3  ");
                Logs.s("  按下k线：3  " + viewPortHandlerCombin.getMaxScaleX());
                setCurrentItemInfo(index);
            }

            @Override
            public void onNothingSelected() {
                KLog.d("onNothingSelected");
                Logs.s("  按下k线：4  ");
                volChart.highlightValue(null);
                postCurrentItemInfo(false, null, 0, 0, 0, 0);
            }
        });
    }

    private void setCurrentItemInfo(int index) {
        if (mData != null) {
            float ma5 = 0;
            float ma10 = 0;
            float ma30 = 0;
            float ma60 = 0;
            if (mData.getMa5Vols().containsKey(index)) {
                ma5 = mData.getMa5Vols().get(index);
            }
            if (mData.getMa10Vols().containsKey(index)) {
                ma10 = mData.getMa10Vols().get(index);
            }
            if (mData.getMa30Vols().containsKey(index)) {
                ma30 = mData.getMa30Vols().get(index);
            }
            if (mData.getMa60Vols().containsKey(index)) {
                ma60 = mData.getMa60Vols().get(index);
            }
            postCurrentItemInfo(true, mData.getKLineDatas().get(index), ma5, ma10, ma30, ma60);
        }
    }

    private void postCurrentItemInfo(boolean isVisible, TradingInfoBean.KlineListBean bean, float ma5, float ma10, float ma30, float ma60) {
        if (isVisible) {
            mCurrentItemInfoView.setVisibility(View.VISIBLE);
            mCurrentItemInfoView.dataChange(bean, ma5, ma10, ma30, ma60);
        } else {
            mCurrentItemInfoView.setVisibility(View.GONE);
        }
    }

    private void refreshKlineData() {
//        showDialog(this);
        mIsRefreshKlineData = true;
        kChart.setNoDataText(mLoadingDataText);
        volChart.setNoDataText(mLoadingDataText);
        volChart.clear();
        kChart.clear();
        kChart.fitScreen();
        volChart.fitScreen();
        HashMap<String, Object> map = new HashMap<>();
        map.put(UrlParams.marketId, marketId);
        map.put(UrlParams.minType, minType);
        Logs.s("  mKLinePresenter     "+marketId);
        Logs.s("  mKLinePresenter     "+minType);
        mKLinePresenter.getData(map, RequestState.STATE_REFRESH);
    }

    private void initViewPager() {

        FragAdapter adapter = new FragAdapter(getSupportFragmentManager(), transactionFragments);
        viewPager.setAdapter(adapter);
        viewPager.setScanScroll(false);
    }


    private void initMagicIndicator() {
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return indicatorTitles == null ? 0 : indicatorTitles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setNormalColor(getResources().getColor(R.color.c4));
                simplePagerTitleView.setSelectedColor(getResources().getColor(R.color.white));
                simplePagerTitleView.setText(indicatorTitles[index]);
                simplePagerTitleView.setTextSize(15);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                linePagerIndicator.setColors(getResources().getColor(R.color.white));
                return linePagerIndicator;
            }
        });
        kline_tab_id.setNavigator(commonNavigator);
        ViewPagerHelper.bind(kline_tab_id, viewPager);

    }


    @OnClick({R.id.full_screen, R.id.tv_buy, R.id.tv_sell, R.id.ll_tab_minute, R.id.ll_tab_minutes, R.id.ll_tab_hour, R.id.ll_tab_day, R.id.ll_tab_mouth})
    public void onClicked(View view) {
        switch (view.getId()) {
            case R.id.full_screen:
                Intent intent = new Intent(mContext, KLinesFullScreenActivity.class);
                intent.putExtra(UrlParams.marketId, marketId);
                intent.putExtra(UrlParams.minType, minType);
                intent.putExtra("kType", mkType);
                startActivityForResult(intent, 2);
                break;
            case R.id.tv_buy:
                String userId = SPUtils.getParamString(mContext, SPConstant.USER_ID, null);
                if (userId != null) {
                    OAXApplication.defaultMarketId=marketId;
                    EventBus.getDefault().post(new KLineBuyOrSellEvent(marketId, minType, true));
                    OAXApplication.state = 1;
                    finish();
                } else {
                    SkipActivityUtil.skipAnotherActivity(mContext, LoginActivity.class);
                }
//                EventBus.getDefault().post(new SelectTradingCoinEvent(item.getMarketId()));
                break;
            case R.id.tv_sell:
                String Id = SPUtils.getParamString(mContext, SPConstant.USER_ID, null);
                if (Id != null) {
                    OAXApplication.state = 2;
                    OAXApplication.defaultMarketId=marketId;
                    EventBus.getDefault().post(new KLineBuyOrSellEvent(marketId, minType, false));
                    finish();
                } else {
                    SkipActivityUtil.skipAnotherActivity(mContext, LoginActivity.class);
                }
                break;
            case R.id.ll_tab_minute:
                if (mIsRefreshKlineData) return;
                setMinuteClick();
                break;
            case R.id.ll_tab_minutes:
                if (mIsRefreshKlineData) return;
                initMinutesSelect();
                break;
            case R.id.ll_tab_hour:
                if (mIsRefreshKlineData) return;
                initHoursSelect();
                break;
            case R.id.ll_tab_day:
                if (mIsRefreshKlineData) return;
                initDaysSelect();
                break;
            case R.id.ll_tab_mouth:
                if (mIsRefreshKlineData) return;
                setMouthClick();
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            if (requestCode == 2) {
                mkType = data.getIntExtra("kType", Constant.KLINE_KLINE);
                minType = data.getIntExtra(UrlParams.minType, Constant.KLINE_MINTYPE_1);
                initSelect();
            }
        }
    }

    private void initSelect() {
        if (mkType == Constant.KLINE_MINUTE) {
            setMinuteClick();
        } else {
            switch (minType) {
                case Constant.KLINE_MINTYPE_1:
                    setMinutesClick(minType, getResources().getString(R.string.kline_one_min));
                    break;
                case Constant.KLINE_MINTYPE_5:
                    setMinutesClick(minType, getResources().getString(R.string.kline_five_min));
                    break;
                case Constant.KLINE_MINTYPE_10:
                    setMinutesClick(minType, getResources().getString(R.string.kline_ten_min));
                    break;
                case Constant.KLINE_MINTYPE_30:
                    setMinutesClick(minType, getResources().getString(R.string.kline_thirty_min));
                    break;
                case Constant.KLINE_MINTYPE_60:
                    setHourClick(minType, getResources().getString(R.string.kline_one_hour));
                    break;
                case Constant.KLINE_MINTYPE_240:
                    setHourClick(minType, getResources().getString(R.string.kline_four_hour));
                    break;
                case Constant.KLINE_MINTYPE_480:
                    setHourClick(minType, getResources().getString(R.string.kline_eight_hour));
                    break;
                case Constant.KLINE_MINTYPE_1440:
                    setDayClick(minType, getResources().getString(R.string.kline_one_day));
                    break;
                case Constant.KLINE_MINTYPE_10080:
                    setDayClick(minType, getResources().getString(R.string.kline_seven_day));
                    break;
                case Constant.KLINE_MINTYPE_43200:
                    setMouthClick();
                    break;
            }
        }
    }

    private void initDaysSelect() {
        if (mKLineDaysSelectPopupWindow == null) {
            mKLineDaysSelectPopupWindow = new KLineDaysSelectPopupWindow(this);
            mKLineDaysSelectPopupWindow.setOnItemClickListener(new KLineDaysSelectPopupWindow.KLineDaysSelectPopupWindowClickListener() {
                @Override
                public void oneDay() {
                    mXscaleCombin = 1.5f;
                    setDayClick(Constant.KLINE_MINTYPE_1440, getResources().getString(R.string.kline_one_day));
                }

                @Override
                public void sevenDay() {
                    mXscaleCombin = 0.15f;
                    setDayClick(Constant.KLINE_MINTYPE_10080, getResources().getString(R.string.kline_seven_day));
                }
            });
        }
        mKLineDaysSelectPopupWindow.showAsDropDown(llTabDay);
    }

    private void initHoursSelect() {
        if (mKLineHoursSelectPopupWindow == null) {
            mKLineHoursSelectPopupWindow = new KLineHoursSelectPopupWindow(this);
            mKLineHoursSelectPopupWindow.setOnItemClickListener(new KLineHoursSelectPopupWindow.KLineHoursSelectPopupWindowClickListener() {
                @Override
                public void oneHour() {
                    mXscaleCombin = 20;
                    setHourClick(Constant.KLINE_MINTYPE_60, getResources().getString(R.string.kline_one_hour));
                }

                @Override
                public void fourHour() {
                    mXscaleCombin = 6;
                    setHourClick(Constant.KLINE_MINTYPE_240, getResources().getString(R.string.kline_four_hour));
                }

                @Override
                public void eightHour() {
                    mXscaleCombin = 3;
                    setHourClick(Constant.KLINE_MINTYPE_480, getResources().getString(R.string.kline_eight_hour));
                }
            });
        }
        mKLineHoursSelectPopupWindow.showAsDropDown(llTabHour);
    }

    private void initMinutesSelect() {
        if (mMinutesSelectPopuWindow == null) {
            mMinutesSelectPopuWindow = new KLineMinutesSelectPopupWindow(this);
            mMinutesSelectPopuWindow.setOnItemClickListener(new KLineMinutesSelectPopupWindow.KLineMinutesSelectPopupWindowClickListener() {
                @Override
                public void oneMin() {
                    mXscaleCombin = 55;
                    setMinutesClick(Constant.KLINE_MINTYPE_1, getResources().getString(R.string.kline_one_min));
                }

                @Override
                public void fiveMin() {
                    mXscaleCombin = 52;
                    setMinutesClick(Constant.KLINE_MINTYPE_5, getResources().getString(R.string.kline_five_min));
                }

                @Override
                public void tenMin() {
                    mXscaleCombin = 50;
                    setMinutesClick(Constant.KLINE_MINTYPE_10, getResources().getString(R.string.kline_ten_min));
                }

                @Override
                public void fifteenMin() {
                    mXscaleCombin = 25;
                    setMinutesClick(Constant.KLINE_MINTYPE_15, getResources().getString(R.string.kline_fifteen_min));
                }

                @Override
                public void thirtyMin() {
                    mXscaleCombin = 35;
                    setMinutesClick(Constant.KLINE_MINTYPE_30, getResources().getString(R.string.kline_thirty_min));
                }
            });
        }
        mMinutesSelectPopuWindow.showAsDropDown(llTabMinutes);
    }

    private void setMouthClick() {
        mXscaleCombin = 0.04f;
        mCurrentItemInfoView.setVisibility(View.GONE);
        isDay = true;
        mkType = Constant.KLINE_KLINE;
        this.minType = Constant.KLINE_MINTYPE_43200;
        mTabMinute.setSelectState(false, "");
        mTabMinutes.setSelectState(false, getResources().getString(R.string.kline_minutes));
        mTabHour.setSelectState(false, getResources().getString(R.string.hour));
        mTabDay.setSelectState(false, getResources().getString(R.string.kline_daily_line));
        mTabMouth.setSelectState(true, "");
        refreshKlineData();
    }

    private void setDayClick(int minType, String name) {
        mCurrentItemInfoView.setVisibility(View.GONE);
        isDay = true;
        mkType = Constant.KLINE_KLINE;
        this.minType = minType;
        mTabMinute.setSelectState(false, "");
        mTabMinutes.setSelectState(false, getResources().getString(R.string.kline_minutes));
        mTabHour.setSelectState(false, getResources().getString(R.string.hour));
        mTabDay.setSelectState(true, name);
        mTabMouth.setSelectState(false, "");
        refreshKlineData();
    }

    private void setHourClick(int minType, String name) {
        mCurrentItemInfoView.setVisibility(View.GONE);
        isDay = false;
        mkType = Constant.KLINE_KLINE;
        this.minType = minType;
        mTabMinute.setSelectState(false, "");
        mTabMinutes.setSelectState(false, getResources().getString(R.string.kline_minutes));
        mTabHour.setSelectState(true, name);
        mTabDay.setSelectState(false, getResources().getString(R.string.kline_daily_line));
        mTabMouth.setSelectState(false, "");
        refreshKlineData();
    }

    private void setMinutesClick(int minType, String name) {
        mCurrentItemInfoView.setVisibility(View.GONE);
        isDay = false;
        mkType = Constant.KLINE_KLINE;
        this.minType = minType;
        mTabMinute.setSelectState(false, "");
        mTabMinutes.setSelectState(true, name);
        mTabHour.setSelectState(false, getResources().getString(R.string.hour));
        mTabDay.setSelectState(false, getResources().getString(R.string.kline_daily_line));
        mTabMouth.setSelectState(false, "");
        refreshKlineData();
    }

    private void setMinuteClick() {
        mCurrentItemInfoView.setVisibility(View.GONE);
        isDay = false;
        mkType = Constant.KLINE_MINUTE;
        minType = Constant.KLINE_MINTYPE_1;
        mTabMinute.setSelectState(true, "");
        mTabMinutes.setSelectState(false, getResources().getString(R.string.kline_minutes));
        mTabHour.setSelectState(false, getResources().getString(R.string.hour));
        mTabDay.setSelectState(false, getResources().getString(R.string.kline_daily_line));
        mTabMouth.setSelectState(false, "");
        refreshKlineData();
    }

    @Override
    protected void initData() {
        super.initData();
        mKLinePresenter = new KLineActivityPresenter(this);
        setMinutesClick(Constant.KLINE_MINTYPE_30, getResources().getString(R.string.kline_thirty_min));
    }

    private void setKLineDatas() {
        setMarkerView(mData.getKLineDatas());
        //成交量
        setVolData();
        //MA
        setMaData();
        setOffset();
        handler.sendEmptyMessageDelayed(0, delayMillisMinutes);
    }

    /**
     * 设置ma（5 10 30 60 ）和蜡烛图 或者 分时线
     */
    private void setMaData() {
        if (kChart == null) {
            return;
        }
        if (mData == null) {
            return;
        }
        int size = mData.getKLineDatas().size();   //点的个数
        CandleDataSet candleDataSet = new CandleDataSet(mData.getCandleEntries(), "KLine");
        if (mkType == Constant.KLINE_MINUTE) {
            candleDataSet.setVisible(false);
            candleDataSet.setHighlightEnabled(false);
            candleDataSet.setDrawHorizontalHighlightIndicator(false);
            candleDataSet.setDrawVerticalHighlightIndicator(false);
        } else {
            candleDataSet.setVisible(true);
            candleDataSet.setHighlightEnabled(true);
            candleDataSet.setDrawHorizontalHighlightIndicator(true);
            candleDataSet.setDrawVerticalHighlightIndicator(true);
        }
        candleDataSet.setHighLightColor(Color.WHITE);
        candleDataSet.setDecreasingColor(getResources().getColor(R.color.kline_sell_bg));//设置开盘价高于收盘价的颜色
        candleDataSet.setDecreasingPaintStyle(Paint.Style.FILL);
        candleDataSet.setIncreasingColor(getResources().getColor(R.color.bule));//设置开盘价地狱收盘价的颜色
        candleDataSet.setIncreasingPaintStyle(Paint.Style.FILL);
        candleDataSet.setNeutralColor(getResources().getColor(R.color.bule));//设置开盘价等于收盘价的颜色    可在CandleStickChartRenderer drawDataSet（）中改规则
        candleDataSet.setShadowColorSameAsCandle(true);
        candleDataSet.setValueTextSize(10f);
        candleDataSet.setDrawValues(false);
        candleDataSet.setShadowWidth(1f);
        candleDataSet.setBarSpace(0.2f);
        candleDataSet.setShadowColor(Color.DKGRAY);
        candleDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        CandleData candleData = new CandleData(mData.getXVals(), candleDataSet);
        ArrayList<ILineDataSet> sets = new ArrayList<>();
        mData.initKLineMA(mData.getKLineDatas());
        /******此处修复如果显示的点的个数达不到MA均线的位置所有的点都从0开始计算最小值的问题******************************/
        if (mkType == Constant.KLINE_KLINE) {
            if (size >= 60) {
                sets.add(setMaLine(5, mData.getXVals(), mData.getMa5DataL()));
                sets.add(setMaLine(10, mData.getXVals(), mData.getMa10DataL()));
                sets.add(setMaLine(30, mData.getXVals(), mData.getMa30DataL()));
                sets.add(setMaLine(60, mData.getXVals(), mData.getMa60DataL()));
            } else if (size >= 30 && size < 60) {
                sets.add(setMaLine(5, mData.getXVals(), mData.getMa5DataL()));
                sets.add(setMaLine(10, mData.getXVals(), mData.getMa10DataL()));
                sets.add(setMaLine(30, mData.getXVals(), mData.getMa30DataL()));
            } else if (size >= 10 && size < 30) {
                sets.add(setMaLine(5, mData.getXVals(), mData.getMa5DataL()));
                sets.add(setMaLine(10, mData.getXVals(), mData.getMa10DataL()));
            } else if (size >= 5 && size < 10) {
                sets.add(setMaLine(5, mData.getXVals(), mData.getMa5DataL()));
            }
        } else if (mkType == Constant.KLINE_MINUTE) {
            sets.add(setMinuteLine(mData.getMinuteLineEntries()));
        }
        CombinedData combinedData = new CombinedData(mData.getXVals());
        LineData lineData = new LineData(mData.getXVals(), sets);
        combinedData.setData(candleData);
        combinedData.setData(lineData);
        kChart.setData(combinedData);

        viewPortHandlerCombin = kChart.getViewPortHandler();

        viewPortHandlerCombin.setMaximumScaleX(culcMaxscale(mData.getXVals().size()));
        Matrix matrixCombin = viewPortHandlerCombin.getMatrixTouch();

        matrixCombin.postScale(mXscaleCombin, mYscaleCombin);

        kChart.moveViewToX(mData.getKLineDatas().size() - 1);

    }

    /**
     * MA线
     *
     * @param lineEntries
     * @return
     */
    @NonNull
    private LineDataSet setMaLine(int ma, ArrayList<String> xVals, ArrayList<Entry> lineEntries) {

        LineDataSet lineDataSetMa = new LineDataSet(lineEntries, "ma" + ma);
        lineDataSetMa.setHighlightEnabled(false);
        lineDataSetMa.setDrawHorizontalHighlightIndicator(false);
        lineDataSetMa.setDrawVerticalHighlightIndicator(false);
        lineDataSetMa.setDrawValues(false);
        if (ma == 5) {
            lineDataSetMa.setColor(getResources().getColor(R.color.kline_ma5));
        } else if (ma == 10) {
            lineDataSetMa.setColor(getResources().getColor(R.color.kline_ma10));
        } else if (ma == 30) {
            lineDataSetMa.setColor(getResources().getColor(R.color.bule));
        } else {
            lineDataSetMa.setColor(getResources().getColor(R.color.kline_ma60));
        }
        lineDataSetMa.setLineWidth(1f);
        lineDataSetMa.setDrawCircles(false);
        lineDataSetMa.setAxisDependency(YAxis.AxisDependency.LEFT);
        return lineDataSetMa;
    }

    /**
     * 分时线
     *
     * @param lineEntries
     * @return
     */
    @NonNull
    private LineDataSet setMinuteLine(ArrayList<Entry> lineEntries) {
        LineDataSet lineDataSetMinute = new LineDataSet(lineEntries, "minute");
        lineDataSetMinute.setHighlightEnabled(true);
        lineDataSetMinute.setDrawHorizontalHighlightIndicator(true);
        lineDataSetMinute.setDrawVerticalHighlightIndicator(true);
        lineDataSetMinute.setHighLightColor(Color.WHITE);
        lineDataSetMinute.setDrawValues(false);
        lineDataSetMinute.setColor(getResources().getColor(R.color.minute_blue));
        lineDataSetMinute.setDrawFilled(true);
        lineDataSetMinute.setLineWidth(1f);
        lineDataSetMinute.setDrawCircles(false);
        lineDataSetMinute.setAxisDependency(YAxis.AxisDependency.LEFT);
        return lineDataSetMinute;
    }

    /*设置量表对齐*/
    private void setOffset() {
        float lineLeft = kChart.getViewPortHandler().offsetLeft();
        float barLeft = volChart.getViewPortHandler().offsetLeft();
        float lineRight = kChart.getViewPortHandler().offsetRight();
        float barRight = volChart.getViewPortHandler().offsetRight();
        float barBottom = volChart.getViewPortHandler().offsetBottom();
        float offsetLeft, offsetRight;
        float transLeft = 0, transRight = 0;
        /*注：setExtraLeft...函数是针对图表相对位置计算，比如A表offLeftA=20dp,B表offLeftB=30dp,则A.setExtraLeftOffset(10),并不是30，还有注意单位转换*/
        if (barLeft < lineLeft) {
           /* offsetLeft = Utils.convertPixelsToDp(lineLeft - barLeft);
            volChart.setExtraLeftOffset(offsetLeft);*/
            transLeft = lineLeft;
        } else {
            offsetLeft = Utils.convertPixelsToDp(barLeft - lineLeft);
            kChart.setExtraLeftOffset(offsetLeft);
            transLeft = barLeft;
        }
        /*注：setExtraRight...函数是针对图表绝对位置计算，比如A表offRightA=20dp,B表offRightB=30dp,则A.setExtraLeftOffset(30),并不是10，还有注意单位转换*/
        if (barRight < lineRight) {
          /*  offsetRight = Utils.convertPixelsToDp(lineRight);
            volChart.setExtraRightOffset(offsetRight);*/
            transRight = lineRight;
        } else {
            offsetRight = Utils.convertPixelsToDp(barRight);
            kChart.setExtraRightOffset(offsetRight);
            transRight = barRight;
        }
        volChart.setViewPortOffsets(transLeft, 1, transRight, barBottom);
    }

    private void notifyDataChange() {
        if (volChart == null) {
            return;
        }
        if (kChart == null) {
            return;
        }

        volChart.setAutoScaleMinMaxEnabled(true);
        kChart.setAutoScaleMinMaxEnabled(true);

        kChart.notifyDataSetChanged();
        volChart.notifyDataSetChanged();

        kChart.invalidate();
        volChart.invalidate();

//        dismissDialog();
    }

    /**
     * 设置成交量数据
     */
    private void setVolData() {
        if (volChart == null) {
            return;
        }
        if (mData == null) {
            return;
        }

        mData.initBaseDatas(mData.getKLineDatas(), isDay);
        barDataSet = new BarDataSet(mData.getBarEntries(), "成交量");
        barDataSet.setBarSpacePercent(mBarSpacePercent); //bar空隙
        barDataSet.setHighlightEnabled(true);
        barDataSet.setHighLightAlpha(255);
        barDataSet.setHighLightColor(Color.WHITE);
        barDataSet.setDrawValues(false); //BarChartRenderer drawDataSet（）中修改规则
        barDataSet.setColor(Color.RED);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.bule));
        colors.add(getResources().getColor(R.color.kline_sell_bg));
        barDataSet.setColors(colors);
        BarData barData = new BarData(mData.getXVals(), barDataSet);
        CombinedData combinedBarData = new CombinedData(mData.getXVals());
        combinedBarData.setData(barData);
        volChart.setData(combinedBarData);


        final ViewPortHandler viewPortHandlerBar = volChart.getViewPortHandler();
        viewPortHandlerBar.setMaximumScaleX(culcMaxscale(mData.getXVals().size()));
        Matrix touchmatrix = viewPortHandlerBar.getMatrixTouch();
        touchmatrix.postScale(mXscaleCombin, mYscaleCombin);
        volChart.moveViewToX(mData.getKLineDatas().size() - 1);

    }

    private void setMarkerView(List<TradingInfoBean.KlineListBean> mData) {
        if (kChart == null) {
            return;
        }
        if (volChart == null) {
            return;
        }
        MyLeftMarkerView leftMarkerView = new MyLeftMarkerView(mContext, R.layout.mymarkerview, KLineUtils.confirmLeftMarkerPattern(marketId));
//        MyRightMarkerView rightMarkerView = new MyRightMarkerView(mContext, R.layout.mymarkerview);
        MyBottomMarkerView bottomMarkerView = new MyBottomMarkerView(mContext, R.layout.mymarkerview);
        kChart.setMarker(leftMarkerView, bottomMarkerView, null, mData);
        volChart.setMarker(null, null, null, mData);
    }

    private float culcMaxscale(float count) {
        float max = 1;
        max = count / 127 * 5;
        return max;
    }

    /**
     * 收藏状态
     */
    private void initCollect() {
        KLog.d("marketId = " + marketId);
        if (OAXApplication.collectCoinsMap.containsKey(marketId)) {
            klineToolbar.setIvRightState(true);
        } else {
            klineToolbar.setIvRightState(false);
        }
    }

    /**
     * 实时价格 涨跌幅
     */
    private void changeCurrentTransactionPrice() {
        if (OAXApplication.coinsInfoMap.containsKey(marketId)) {
            IndexPageBean.DataBean.AllMaketListBean.MarketListBean bean = OAXApplication.coinsInfoMap.get(marketId);
            KLog.d("changeCurrentTransactionPrice = " + new Gson().toJson(bean));
            currentTransactionPrice.setDataChange(bean);
        }
    }

    /**
     * k线数据回调
     *
     * @param data
     */
    @Override
    public void setTradingInfoData(CommonJsonToBean<TradingInfoBean> data) {
        if (kChart == null) {
            return;
        }
        if (volChart == null) {
            return;
        }
        Logs.s(" k线图data   " + data);
        EventBus.getDefault().post(new TransactionListEvent(data.getData()));
        List<TradingInfoBean.KlineListBean> klineList = data.getData().getKlineList();
        if (klineList != null && klineList.size() > 0) {
            mData = new DataParse();
            mData.setKLine(klineList);
            setKLineDatas();
        } else {
            String s = getResources().getString(R.string.no_data_available);
            if (kChart != null) {
                kChart.setNoDataText(s);
                kChart.invalidate();
            }
            if (volChart != null) {
                volChart.setNoDataText(s);
                volChart.invalidate();
            }
//            dismissDialog();
        }
        mIsRefreshKlineData = false;
    }

    @Override
    public void getTradingInfoDataError(String msg) {
        mIsRefreshKlineData = false;
        String s = getResources().getString(R.string.load_data_fail);
        if (kChart != null) {
            kChart.setNoDataText(s);
            kChart.invalidate();
        }
        if (volChart != null) {
            volChart.setNoDataText(s);
            volChart.invalidate();
        }

    }

    @Override
    public void setBuyOrSellState(CommonJsonToBean<String> data) {

    }

    public void showDialog(Activity activity) {
        if (dialog == null || !dialog.isShowing()) {
            dialog = KProgressHUD.create(activity)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setCancellable(true)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();
        }
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    public void showToast(String str) {
        super.showToast(str);
    }

    /**
     * 收藏回调
     *
     * @param bean
     */
    @Override
    public void collectMarketState(CommonJsonToBean<String> bean) {
        ToastUtil.ShowToast(bean.getMsg(), this);
        klineToolbar.setIvRightState(bean.getSuccess());
        OAXApplication.addCollectCoinsMap(marketId);
        EventBus.getDefault().post(new RefreshHomeFragmentEvent());
    }

    /**
     * 取消收藏回调
     *
     * @param bean
     */
    @Override
    public void cancelCollectMarket(CommonJsonToBean<String> bean) {
        Logs.s("    bean.getMsg()    " + bean.getMsg());
        ToastUtil.ShowToast(bean.getMsg(), this);
        klineToolbar.setIvRightState(!bean.getSuccess());
        OAXApplication.collectCoinsMap.remove(marketId);
        EventBus.getDefault().post(new RefreshHomeFragmentEvent());
    }

    @Override
    public void setTopicKlineData(List<TradingInfoBean.KlineListBean> data) {

    }


    /**
     * 改变实时价格 涨跌幅
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(UpdateTradingInfoEvent event) {
        KLog.d("UpdateTradingInfoEvent");
        changeCurrentTransactionPrice();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler = null;
    }
}
