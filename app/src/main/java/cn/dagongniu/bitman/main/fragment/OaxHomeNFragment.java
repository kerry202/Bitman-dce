package cn.dagongniu.bitman.main.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.socks.library.KLog;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.WrapPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.dagongniu.bitman.OAXApplication;
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.account.HelpActivity;
import cn.dagongniu.bitman.account.LoginActivity;
import cn.dagongniu.bitman.account.SafetyActivity;
import cn.dagongniu.bitman.account.bean.UserCenterBean;
import cn.dagongniu.bitman.account.presenter.UserCenterPresenter;
import cn.dagongniu.bitman.account.view.IUserCenterView;
import cn.dagongniu.bitman.assets.WithdTopSearchActivity;
import cn.dagongniu.bitman.base.BaseFragment;
import cn.dagongniu.bitman.constant.SPConstant;
import cn.dagongniu.bitman.constant.UMConstant;
import cn.dagongniu.bitman.customview.ClassicsHeader;
import cn.dagongniu.bitman.customview.ScaleCircleNavigator;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.https.WebSocketsManager;
import cn.dagongniu.bitman.language.LanguageUtils;
import cn.dagongniu.bitman.main.AnnouncementMoreActivity;
import cn.dagongniu.bitman.main.BannerWebActivity;
import cn.dagongniu.bitman.main.SearchCoinActivity;
import cn.dagongniu.bitman.main.WebActivity;
import cn.dagongniu.bitman.main.adapter.HomesViewPagerAdapter;
import cn.dagongniu.bitman.main.bean.IndexPageBean;
import cn.dagongniu.bitman.main.bean.OaxMarketBean;
import cn.dagongniu.bitman.main.presenter.IndexPagePresenter;
import cn.dagongniu.bitman.main.view.IndexPageView;
import cn.dagongniu.bitman.utils.AppConstants;
import cn.dagongniu.bitman.utils.AppManager;
import cn.dagongniu.bitman.utils.Logger;
import cn.dagongniu.bitman.utils.Logs;
import cn.dagongniu.bitman.utils.SPUtils;
import cn.dagongniu.bitman.utils.SkipActivityUtil;
import cn.dagongniu.bitman.utils.ToastUtil;
import cn.dagongniu.bitman.utils.events.CollectMarketStateEvent;
import cn.dagongniu.bitman.utils.events.MyEvents;
import cn.dagongniu.bitman.utils.events.RefreshHomeFragmentEvent;
import cn.dagongniu.bitman.utils.events.UpdateTradingInfoEvent;
import cn.dagongniu.bitman.utils.um.UMManager;
import cn.dagongniu.bitman.views.banner.BGABanner;
import cn.dagongniu.bitman.views.marqueeView.MarqueeView;
import io.reactivex.disposables.Disposable;
import ua.naiksoftware.stomp.client.StompClient;

/**
 * 首页
 */
public class OaxHomeNFragment extends BaseFragment implements OnClickListener, IndexPageView,
        BGABanner.Adapter<ImageView, String>, BGABanner.Delegate<ImageView, String>,
        OnRefreshListener, IUserCenterView {

    private static final String TAG = "OaxHomeNFragment";

    private ImageView.ScaleType mScaleType = ImageView.ScaleType.CENTER_CROP;

    @BindView(R.id.banner_home_zoomCenter)
    BGABanner mContentBanner;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.activity_main_advertView)
    MarqueeView advertView;
    @BindView(R.id.home_viwepager)
    ViewPager home_viwepager;
    @BindView(R.id.main_market_indicator)
    MagicIndicator mainMarketIndicator;
    @BindView(R.id.other_market_indicator)
    MagicIndicator otherMarketIndicator;
    @BindView(R.id.other_market_view_pager)
    ViewPager otherMarketViewPager;
    @BindView(R.id.rl_search)
    RelativeLayout rlSearch;
    @BindView(R.id.tv_header_jg)
    TextView tvHeaderJg;
    @BindView(R.id.rl_notice)
    RelativeLayout rlNotice;
    @BindView(R.id.home_arrows)
    ImageView home_arrows;
    @BindView(R.id.home_help_ll)
    LinearLayout home_help_ll;
    @BindView(R.id.home_safety_ll)
    LinearLayout home_safety_ll;
    @BindView(R.id.home_topup_ll)
    LinearLayout home_topup_ll;
    @BindView(R.id.home_withdrawal_ll)
    LinearLayout home_withdrawal_ll;

    @BindView(R.id.home_ll)
    LinearLayout home_ll;

    @BindView(R.id.appbar_home)
    AppBarLayout appbar_home;

    @BindView(R.id.banner_bg)
    RelativeLayout banner_bg;

    @BindView(R.id.indicator_1)
    View indicator_1;
    @BindView(R.id.indicator_2)
    View indicator_2;
    @BindView(R.id.indicator_3)
    View indicator_3;
    @BindView(R.id.indicator_4)
    View indicator_4;

    @BindView(R.id.indicator_gray1)
    View indicator_gray1;

    @BindView(R.id.indicator_gray2)
    View indicator_gray2;
    @BindView(R.id.indicator_gray3)
    View indicator_gray3;
    @BindView(R.id.indicator_gray4)
    View indicator_gray4;

    IndexPageBean indexPageBean;
    ClassicsHeader mClassicsHeader;
    List<Fragment> fragments = new ArrayList<>();
    List<Fragment> fragmentsMarKet = new ArrayList<>();
    HomesViewPagerAdapter adapter;
    private List<String> recommendMarketListIndicators = new ArrayList<>();
    List<String> listTitle = new ArrayList<>();
    HomesViewPagerAdapter allMaketAdapter = null;
    List<AllMarketFragmnet> listAllMarketFragmnets = new ArrayList<>();
    ZXMarketFragmnet zxMarketFragmnet = new ZXMarketFragmnet();
    IndexPagePresenter indexPagePresenter;
    Bundle bundle = new Bundle();
    private StompClient mMarketCategoryClient;
    private boolean isOnErrorOrOnClosed = false;
    private WebSocketsManager.OnCreateStompClientListener mOnCreateStompClientListener;
    private Disposable mTopic;
    private int indexs = 0;
    UserCenterPresenter userCenterPresenter;
    private AllMarketFragmnet allMarketFragmnet;

    @Override
    public void onDestroy() {
        super.onDestroy();

        Logs.s("   fragment1 onDestroy  ");
        if (mMarketCategoryClient != null) {
//            WebSocketsManager.getInstance().disconnect(mMarketCategoryClient);
            if (mTopic != null && !mTopic.isDisposed()) {
                mTopic.dispose();
                mTopic = null;
            }
            mMarketCategoryClient.disconnect();
            mMarketCategoryClient = null;
            mOnCreateStompClientListener = null;
        }
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.statusBarDarkFont(true).init();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_oax_home_n_frament;
    }

    @Override
    protected void initView() {
        super.initView();

        Logs.s("   fragment1 initView  ");
        userCenterPresenter = new UserCenterPresenter(this, RequestState.STATE_REFRESH);

        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        float density = dm.density;

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mContentBanner.getLayoutParams();
        //获取当前控件的布局对象
        params.height = width / 2;//设置当前控件布局的高度
        mContentBanner.setLayoutParams(params);//将设置好的布局参数应用到控件中

        LinearLayout.LayoutParams banner_bgLayoutParams = (LinearLayout.LayoutParams) banner_bg.getLayoutParams();

        params.height = width / 2;//设置当前控件布局的高度
        banner_bg.setLayoutParams(banner_bgLayoutParams);//将设置好的布局参数应用到控件中

        rlSearch.setOnClickListener(this);
        home_help_ll.setOnClickListener(this);
        home_safety_ll.setOnClickListener(this);
        home_topup_ll.setOnClickListener(this);
        home_withdrawal_ll.setOnClickListener(this);

        //尺寸拉伸
        mClassicsHeader = (ClassicsHeader) mRefreshLayout.getRefreshHeader();
        mClassicsHeader.setSpinnerStyle(SpinnerStyle.Scale);
        mRefreshLayout.setOnRefreshListener(this);
        LanguageUtils.setHeaderLanguage(mClassicsHeader, getActivity());

        home_arrows.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString("ArticleType", indexPageBean.getData().getArticleList().get(0).getType() + "");
                openActivity(AnnouncementMoreActivity.class, bundle);
            }
        });

//        解决偶尔无法滑动
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appbar_home.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) layoutParams.getBehavior();
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                return true;
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Logs.s("   fragment1 onCreate  ");

    }

    /**
     * web socket初始化
     */
    private void initWebsocket() {

        if (isOnErrorOrOnClosed) {
            mMarketCategoryClient = null;
            mOnCreateStompClientListener = null;
        }
        if (mMarketCategoryClient == null) {
            mOnCreateStompClientListener = new WebSocketsManager.OnCreateStompClientListener() {
                @Override
                public void onOpened() {

                    isOnErrorOrOnClosed = false;
                    mTopic = WebSocketsManager.getInstance().topic(mMarketCategoryClient, Http.marketCategory_all, new WebSocketsManager.OnTopicListener() {
                        @Override
                        public void onNewData(String data) {
                            Logs.s("   WebSocketsManager 111  " + data);
                            List<OaxMarketBean> listOaxMarketBeanGson = getListOaxMarketBeanGson(data);
                            Logs.s("   WebSocketsManager 222  " + listOaxMarketBeanGson);
                            OAXApplication.updateCoinsInfo(listOaxMarketBeanGson);
                            EventBus.getDefault().post(new UpdateTradingInfoEvent());
                            myEvents.status = MyEvents.status_pass;
                            myEvents.status_type = MyEvents.Home_WebSocket_Market_List;
                            myEvents.errmsg = "首页交易对的websocket推送数据数据";
                            myEvents.listOaxMarketBeanGson = listOaxMarketBeanGson;
                            eventBus.post(myEvents);

                        }
                    });
                }

                @Override
                public void onError() {
                    isOnErrorOrOnClosed = true;
                }

                @Override
                public void onClosed() {
                    isOnErrorOrOnClosed = true;
                }
            };
            mMarketCategoryClient = WebSocketsManager.getInstance().createStompClient(mOnCreateStompClientListener);
        }

    }

    //通过Gson解析
    public List<OaxMarketBean> getListOaxMarketBeanGson(String jsonString) {
        List<OaxMarketBean> list = new ArrayList<>();
        Gson gson = new Gson();
        list = gson.fromJson(jsonString, new TypeToken<List<OaxMarketBean>>() {
        }.getType());
        return list;
    }

    @Override
    protected void initData() {
        super.initData();
        Logs.s("   fragment1 initData  ");
        indexPagePresenter = new IndexPagePresenter(this, RequestState.STATE_REFRESH);

        mRefreshLayout.autoRefresh();
        //进入页面显示下拉刷新
    }

    /**
     * 推荐主流市场指标标示点
     */
    private void initRecommendMarketListIndicator() {

        ScaleCircleNavigator scaleCircleNavigator = new ScaleCircleNavigator(getActivity());
        scaleCircleNavigator.setCircleCount(recommendMarketListIndicators.size());
        scaleCircleNavigator.setNormalCircleColor(getContext().getResources().getColor(R.color.home_tj_color3));
        scaleCircleNavigator.setSelectedCircleColor(getContext().getResources().getColor(R.color.home_tj_color2));
        scaleCircleNavigator.setMaxRadius(10);
        scaleCircleNavigator.setMaxRadius(10);

        scaleCircleNavigator.setCircleClickListener(new ScaleCircleNavigator.OnCircleClickListener() {
            @Override
            public void onClick(int index) {
                home_viwepager.setCurrentItem(index);
            }
        });

        mainMarketIndicator.setNavigator(scaleCircleNavigator);

        ViewPagerHelper.bind(mainMarketIndicator, home_viwepager);
    }

    @Override
    public void setUserCenterData(UserCenterBean userCenterData) {
        String userId = SPUtils.getParamString(mContext, SPConstant.USER_ID, null);

        if (userId != null && userCenterData != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("UserCenterBean", userCenterData);
            openActivity(SafetyActivity.class, bundle);

        } else {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.putExtra(AppConstants.LOGINTYPE, AppConstants.LOGINPHONE);
            startActivity(intent);
        }
        Logs.s("   userIduserId setUserCenterData  " + (userCenterData == null));

    }

    @Override
    public void setUserCenterFailure(String msg) {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.putExtra(AppConstants.LOGINTYPE, AppConstants.LOGINPHONE);
        startActivity(intent);
    }

    @OnClick({R.id.rl_search, R.id.rl_notice, R.id.home_help_ll, R.id.home_topup_ll, R.id.home_withdrawal_ll, R.id.home_safety_ll})
    public void onClick(View v) {
        String userId = SPUtils.getParamString(mContext, SPConstant.USER_ID, null);
        switch (v.getId()) {
            //搜索
            case R.id.rl_search:
                SkipActivityUtil.skipAnotherActivity(getActivity(), SearchCoinActivity.class);
                UMManager.onEvent(getActivity(), UMConstant.OaxHomeNFragment, UMConstant.search);
                break;
            case R.id.rl_notice:
                break;
            case R.id.home_help_ll:

                SkipActivityUtil.skipAnotherActivity(mContext, HelpActivity.class);

                break;
            case R.id.home_safety_ll:

                Logs.s("     userIduserId  userId   " + userId);
                userCenterPresenter.getUserCenterModule();

                break;
            case R.id.home_topup_ll:
                if (userId != null) {
                    Intent intent = new Intent(getActivity(), WithdTopSearchActivity.class);
                    intent.putExtra(AppConstants.SEARCH, AppConstants.TOPUP);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.putExtra(AppConstants.LOGINTYPE, AppConstants.LOGINPHONE);
                    startActivity(intent);
                }
                break;
            case R.id.home_withdrawal_ll:
                if (userId != null) {
                    Intent intent = new Intent(getActivity(), WithdTopSearchActivity.class);
                    intent.putExtra(AppConstants.SEARCH, AppConstants.WITHDRAWAL);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.putExtra(AppConstants.LOGINTYPE, AppConstants.LOGINPHONE);
                    startActivity(intent);
                }
                break;

        }
    }

    /**
     * banner 适配器
     *
     * @param banner
     * @param itemView
     * @param model
     * @param position
     */
    @Override
    public void fillBannerItem(BGABanner banner, ImageView itemView, @Nullable String model, int position) {

        Logs.s("  bannerUrl  " + model);

        Glide.with(itemView.getContext())
                .load(model)
                .apply(new RequestOptions().placeholder(R.drawable.banner_df).error(R.drawable.banner_df).dontAnimate().centerCrop())
                .into(itemView);

    }

    /**
     * banner 点击事件
     *
     * @param banner
     * @param itemView
     * @param model
     * @param position
     */
    @Override
    public void onBannerItemClick(BGABanner banner, ImageView itemView, @Nullable String model, int position) {
        if (indexPageBean != null) {
            if (indexPageBean.getData() != null) {
                if (indexPageBean.getData().getBannerList() != null) {
                    bundle.putString("BannerUrl", indexPageBean.getData().getBannerList().get(position).getUrl());

                    Logs.s("    BannerUrl   " + indexPageBean.getData().getBannerList().get(position).getUrl());
                    openActivity(BannerWebActivity.class, bundle);
                }
            }
        }
    }

    /**
     * 设置所以市场的指标标识点
     */
    private void initAllMaketListIndicator(final List<String> mDataList) {

        //默认设置市场价格（ETH）
        String price = getContext().getResources().getString(R.string.home_price) + "(" + mDataList.get(0) + ")";
        tvHeaderJg.setText(price);

        CommonNavigator commonNavigator = new CommonNavigator(getActivity());
        commonNavigator.setScrollPivotX(0.35f);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(mDataList.get(index));
                simplePagerTitleView.setTextSize(16);
                //simplePagerTitleView.setPadding(20,0,20,0);
                simplePagerTitleView.setNormalColor(Color.parseColor("#999999"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#333333"));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        otherMarketViewPager.setCurrentItem(index);

                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                WrapPagerIndicator indicator = new WrapPagerIndicator(context);
                //indicator.setFillColor(Color.parseColor("#ebe4e3"));
                return indicator;
            }
        });

        otherMarketIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(otherMarketIndicator, otherMarketViewPager);
        otherMarketViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //设置市场价格（ETH）
                indexs = position;
                String price = getContext().getResources().getString(R.string.home_price) + "(" + mDataList.get(position) + ")";
                tvHeaderJg.setText(price);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * 刷新
     *
     * @param refreshLayout
     */
    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        Log.e("okgo", "刷新");
        //刷新banner请求

        mRefreshLayout.setFocusable(true);
        mRefreshLayout.requestFocus();
        mRefreshLayout.setFocusableInTouchMode(true);
        mRefreshLayout.requestFocusFromTouch();
        indexPagePresenter.getIndexPageRefreshModule();
        initWebsocket();
    }


    /**
     * 首页刷新的回调方法
     *
     * @param indexPageDataRefresh
     */
    @Override
    public void setRefreshIndexPageData(IndexPageBean indexPageDataRefresh) {
        this.indexPageBean = indexPageDataRefresh;
        Logger.i(TAG, "首页刷新回调11:" + indexPageDataRefresh);
        mRefreshLayout.finishRefresh();
        if (indexPageDataRefresh.isSuccess()) {
            //刷新banner
            setBannerData(indexPageDataRefresh);
            //刷新公告HomesViewPagerAdapter
            setArticleList(indexPageDataRefresh);
            //刷新推荐
            home_viwepager.removeAllViews();
            home_viwepager.removeAllViewsInLayout();
            setRecommendMarketList(indexPageDataRefresh);
            //刷新所有市场
            otherMarketViewPager.removeAllViews();
            otherMarketViewPager.removeAllViewsInLayout();
            setAllMaketList(indexPageDataRefresh);
        } else {
            ToastUtil.ShowToast(indexPageDataRefresh.getMsg());
        }
    }

    /**
     * 首页静默刷新的回调方法
     *
     * @param allMaketList
     */
    @Override
    public void setSilentRefreshIndexPageData(IndexPageBean allMaketList) {

        Logs.s("   正式setSilentRefreshIndexPageData 1:     " + allMaketList.getData().getUserMaketList().size());
        if (allMaketList.isSuccess()) {

            //所有交易对信息
            OAXApplication.setCoinsInfo(allMaketList.getData().getAllMaketList());
            Logs.s("   正式setSilentRefreshIndexPageData 2:     " + allMaketList.getData().getUserMaketList().size());
            //用户收藏
            OAXApplication.setCollectCoinsMap(allMaketList.getData().getUserMaketList());
            Logs.s("   正式setSilentRefreshIndexPageData 3:     " + allMaketList.getData().getUserMaketList().size());

            //判断是否有值 有值在set过去
            zxMarketFragmnet.dataSilentChange();
            Logs.s("   正式setSilentRefreshIndexPageData 4:     " + allMaketList.getData().getUserMaketList().size());
            EventBus.getDefault().post(new UpdateTradingInfoEvent());
            myEvents.status = MyEvents.status_pass;
            myEvents.status_type = MyEvents.COLLECTION_SILENT;
            myEvents.errmsg = "收藏静默刷新";
            eventBus.post(myEvents);
            Logger.e(TAG, "发送收藏静默刷新数据通知!");

        } else {
            ToastUtil.ShowToast(allMaketList.getMsg());
        }
        mRefreshLayout.finishRefresh();
    }

    @Override
    public void setRefreshIndexPageDataErrer(String indexPageData) {
        if (mRefreshLayout != null) {
            mRefreshLayout.finishRefresh();
        }
        Logger.e(TAG, "首页刷新回调 刷新错误");
        ToastUtil.ShowToast(indexPageData);
    }

    @Override
    public void refreshErrer() {
        if (mRefreshLayout != null) {
            mRefreshLayout.finishRefresh();
        }
    }


    /**
     * 首页数据回调
     *
     * @param indexPageData
     */
    @Override
    public void setIndexPageData(final IndexPageBean indexPageData) {
        this.indexPageBean = indexPageData;
        mRefreshLayout.finishRefresh();
        Logs.s("   首页数据回调   setIndexPageData  " + indexPageData);
        setBannerData(indexPageData);
        setArticleList(indexPageData);
        setRecommendMarketList(indexPageData);
        setAllMaketList(indexPageData);
    }

    /**
     * 设置Banner
     */
    public void setBannerData(IndexPageBean bannerData) {

        List<String> listimgs = new ArrayList<>();
        List<String> liststrs = new ArrayList<>();

        if (bannerData.getData().getBannerList() != null && bannerData.getData().getBannerList().size() > 0) {
            for (IndexPageBean.DataBean.BannerListBean bean : bannerData.getData().getBannerList()) {
                listimgs.add(bean.getImage());
                liststrs.add(bean.getTitle());

                Logs.s(" getImage " + bean.getImage());
                Logs.s(" getTitle " + bean.getTitle());
            }

            mContentBanner.setDelegate(this);
            mContentBanner.setData(listimgs, null);
            mContentBanner.setAdapter(this);
        }
    }

    /**
     * 设置公告
     */
    public void setArticleList(final IndexPageBean articleList) {
        ArrayList stringArrayList = new ArrayList<>();
        try {
            for (IndexPageBean.DataBean.ArticleListBean articleListBean : articleList.getData().getArticleList()) {
                stringArrayList.add(articleListBean.getName());
            }
        } catch (Exception e) {
        }

        Logger.e(TAG, "点击公告a：" + stringArrayList.size());
        advertView.startWithList(stringArrayList);
        advertView.setOnItemClickListener(new MarqueeView.OnItemClickListener() {
            @Override
            public void onItemClick(int position, TextView textView) {
                if (articleList.getData().getArticleList().size() <= 0) {
                    return;
                }
                Logger.e(TAG, "点击公告：" + articleList.getData().getArticleList().get(position).getName());
                bundle.putBoolean("More", true);
                bundle.putString("ArticleID", articleList.getData().getArticleList().get(position).getId() + "");
                bundle.putString("ArticleType", articleList.getData().getArticleList().get(position).getType() + "");
                openActivity(WebActivity.class, bundle);
            }
        });
    }

    public void settext() {
        advertView.settext();
    }

    /**
     * 设置推荐交易对的k线图数据
     */
    public void setRecommendMarketList(IndexPageBean articleListMarketList) {
        fragments.clear();
        recommendMarketListIndicators.clear();
        initRecommendMarketListIndicator();

        Logs.s("    首页刷新回调  0:  " + articleListMarketList.getData().getRecommendMarketList().size());
        if (articleListMarketList.getData().getRecommendMarketList().size() > 1) {
            home_ll.setVisibility(View.VISIBLE);
        } else {
            home_ll.setVisibility(View.GONE);
        }

        if (articleListMarketList.getData().getRecommendMarketList().size() <= 3) {
            Logs.s("    首页刷新回调  1  ");
            redDate(articleListMarketList, true);
        } else if (articleListMarketList.getData().getRecommendMarketList().size() <= 6) {
            Logs.s("    首页刷新回调  2  ");
            redDate(articleListMarketList, false);
            redDate2(articleListMarketList, true);

        } else if (articleListMarketList.getData().getRecommendMarketList().size() <= 9) {
            Logs.s("    首页刷新回调  3  ");
            redDate(articleListMarketList, false);
            redDate2(articleListMarketList, false);
            redDate3(articleListMarketList, true);
        } else if (articleListMarketList.getData().getRecommendMarketList().size() <= 12) {
            Logs.s("    首页刷新回调  4  ");
            redDate(articleListMarketList, false);
            redDate2(articleListMarketList, false);
            redDate3(articleListMarketList, false);
            redDate4(articleListMarketList, true);
        } else {
            Logs.s("    首页刷新回调  else  ");
            MainMarketFirstFragment mainMarketFirstFragment1 = new MainMarketFirstFragment();
            mainMarketFirstFragment1.setRecommendMarketList(articleListMarketList.getData().getRecommendMarketList());
            fragments.add(mainMarketFirstFragment1);
        }

        /**
         * 初始化推荐主流市场页面
         */

        Logs.s("    首页刷新回调  5  " + fragments.size());

        indicator_1.setVisibility(View.VISIBLE);

        adapter = new HomesViewPagerAdapter(getActivity().getSupportFragmentManager(), fragments);
        if (fragments.size() == 4) {
            indicator_gray1.setVisibility(View.VISIBLE);
            indicator_gray2.setVisibility(View.VISIBLE);
            indicator_gray3.setVisibility(View.VISIBLE);
            indicator_gray4.setVisibility(View.VISIBLE);
            indicator_2.setVisibility(View.INVISIBLE);
            indicator_3.setVisibility(View.INVISIBLE);
            indicator_4.setVisibility(View.INVISIBLE);


        } else if (fragments.size() == 3) {
            indicator_gray1.setVisibility(View.VISIBLE);
            indicator_gray2.setVisibility(View.VISIBLE);
            indicator_gray3.setVisibility(View.VISIBLE);
            indicator_gray4.setVisibility(View.GONE);
            indicator_2.setVisibility(View.INVISIBLE);
            indicator_3.setVisibility(View.INVISIBLE);
            indicator_4.setVisibility(View.GONE);

        } else if (fragments.size() == 2) {
            indicator_gray1.setVisibility(View.VISIBLE);
            indicator_gray2.setVisibility(View.VISIBLE);
            indicator_gray3.setVisibility(View.GONE);
            indicator_gray4.setVisibility(View.GONE);
            indicator_2.setVisibility(View.INVISIBLE);
            indicator_3.setVisibility(View.GONE);
            indicator_4.setVisibility(View.GONE);
        } else if (fragments.size() == 1) {
            indicator_gray1.setVisibility(View.VISIBLE);
            indicator_gray2.setVisibility(View.GONE);
            indicator_gray3.setVisibility(View.GONE);
            indicator_gray4.setVisibility(View.GONE);
            indicator_1.setVisibility(View.GONE);
            indicator_2.setVisibility(View.GONE);
            indicator_4.setVisibility(View.GONE);
        }

        //设定适配器
        home_viwepager.setAdapter(adapter);
        home_viwepager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                暂时这样处理了
                if (fragments.size() == 4) {
                    if (position == 0) {
                        indicator_1.setVisibility(View.VISIBLE);
                        indicator_2.setVisibility(View.INVISIBLE);
                        indicator_3.setVisibility(View.INVISIBLE);
                        indicator_4.setVisibility(View.INVISIBLE);
                    } else if (position == 1) {
                        indicator_1.setVisibility(View.INVISIBLE);
                        indicator_2.setVisibility(View.VISIBLE);
                        indicator_3.setVisibility(View.INVISIBLE);
                        indicator_4.setVisibility(View.INVISIBLE);
                    } else if (position == 2) {
                        indicator_1.setVisibility(View.INVISIBLE);
                        indicator_2.setVisibility(View.INVISIBLE);
                        indicator_3.setVisibility(View.VISIBLE);
                        indicator_4.setVisibility(View.INVISIBLE);
                    } else if (position == 3) {
                        indicator_1.setVisibility(View.INVISIBLE);
                        indicator_2.setVisibility(View.INVISIBLE);
                        indicator_3.setVisibility(View.INVISIBLE);
                        indicator_4.setVisibility(View.VISIBLE);
                    }
                } else if (fragments.size() == 3) {
                    indicator_4.setVisibility(View.GONE);
                    if (position == 0) {
                        indicator_1.setVisibility(View.VISIBLE);
                        indicator_2.setVisibility(View.INVISIBLE);
                        indicator_3.setVisibility(View.INVISIBLE);
                    } else if (position == 1) {
                        indicator_1.setVisibility(View.INVISIBLE);
                        indicator_2.setVisibility(View.VISIBLE);
                        indicator_3.setVisibility(View.INVISIBLE);
                    } else if (position == 2) {
                        indicator_1.setVisibility(View.INVISIBLE);
                        indicator_2.setVisibility(View.INVISIBLE);
                        indicator_3.setVisibility(View.VISIBLE);
                    }

                } else if (fragments.size() == 2) {
                    indicator_4.setVisibility(View.GONE);
                    indicator_3.setVisibility(View.GONE);
                    if (position == 0) {
                        indicator_1.setVisibility(View.VISIBLE);
                        indicator_2.setVisibility(View.INVISIBLE);
                    } else if (position == 1) {
                        indicator_1.setVisibility(View.INVISIBLE);
                        indicator_2.setVisibility(View.VISIBLE);
                    }
                } else if (fragments.size() == 1) {
                    indicator_4.setVisibility(View.GONE);
                    indicator_3.setVisibility(View.GONE);
                    indicator_2.setVisibility(View.GONE);
                    indicator_1.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onPageSelected(int position) {


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        initRecommendMarketListIndicator();

    }

    private void redDate(IndexPageBean articleListMarketList, boolean is) {
        MainMarketFirstNextFragment mainMarketFirstFragment1 = new MainMarketFirstNextFragment();
        List<IndexPageBean.DataBean.RecommendMarketListBean> list1 = new ArrayList<>();
        if (is) {
            for (int subscript = 0; subscript < articleListMarketList.getData().getRecommendMarketList().size(); subscript++) {
                list1.add(articleListMarketList.getData().getRecommendMarketList().get(subscript));
            }
        } else {
            for (int subscript = 0; subscript < 3; subscript++) {
                list1.add(articleListMarketList.getData().getRecommendMarketList().get(subscript));
            }
        }
        mainMarketFirstFragment1.setRecommendMarketListNext(list1);
        fragments.add(mainMarketFirstFragment1);
        recommendMarketListIndicators.add("");
    }

    private void redDate2(IndexPageBean articleListMarketList, boolean is) {
        MainMarketFirstNextFragment mainMarketFirstFragment1 = new MainMarketFirstNextFragment();
        List<IndexPageBean.DataBean.RecommendMarketListBean> list1 = new ArrayList<>();
        if (is) {
            for (int subscript = 3; subscript < articleListMarketList.getData().getRecommendMarketList().size(); subscript++) {
                list1.add(articleListMarketList.getData().getRecommendMarketList().get(subscript));
            }
        } else {
            for (int subscript = 3; subscript < 6; subscript++) {
                list1.add(articleListMarketList.getData().getRecommendMarketList().get(subscript));
            }
        }

        mainMarketFirstFragment1.setRecommendMarketListNext(list1);
        fragments.add(mainMarketFirstFragment1);
        recommendMarketListIndicators.add("");
    }

    private void redDate3(IndexPageBean articleListMarketList, boolean is) {
        MainMarketFirstNextFragment mainMarketFirstFragment1 = new MainMarketFirstNextFragment();
        List<IndexPageBean.DataBean.RecommendMarketListBean> list1 = new ArrayList<>();
        if (is) {
            for (int subscript = 6; subscript < articleListMarketList.getData().getRecommendMarketList().size(); subscript++) {
                list1.add(articleListMarketList.getData().getRecommendMarketList().get(subscript));
            }
        } else {
            for (int subscript = 6; subscript < 9; subscript++) {
                list1.add(articleListMarketList.getData().getRecommendMarketList().get(subscript));
            }
        }
        mainMarketFirstFragment1.setRecommendMarketListNext(list1);
        fragments.add(mainMarketFirstFragment1);
        recommendMarketListIndicators.add("");
    }

    private void redDate4(IndexPageBean articleListMarketList, boolean is) {
        MainMarketFirstNextFragment mainMarketFirstFragment1 = new MainMarketFirstNextFragment();
        List<IndexPageBean.DataBean.RecommendMarketListBean> list1 = new ArrayList<>();
        if (is) {
            for (int subscript = 9; subscript < articleListMarketList.getData().getRecommendMarketList().size(); subscript++) {
                list1.add(articleListMarketList.getData().getRecommendMarketList().get(subscript));
            }
        } else {
            for (int subscript = 9; subscript < 12; subscript++) {
                list1.add(articleListMarketList.getData().getRecommendMarketList().get(subscript));
            }
        }

        mainMarketFirstFragment1.setRecommendMarketListNext(list1);
        fragments.add(mainMarketFirstFragment1);
        recommendMarketListIndicators.add("");
    }

    /**
     * 设置市场所有交易对数据
     *
     * @param allMaketList
     */
    public void setAllMaketList(IndexPageBean allMaketList) {
        KLog.d("setAllMaketList = " + new Gson().toJson(allMaketList));
        //所有交易对信息
        OAXApplication.setCoinsInfo(allMaketList.getData().getAllMaketList());
        OAXApplication.setCoinsInfoFromRecommend(allMaketList.getData().getRecommendMarketList());
        //用户收藏
        OAXApplication.setCollectCoinsMap(allMaketList.getData().getUserMaketList());

        listTitle.clear();
        fragmentsMarKet.clear();
        listAllMarketFragmnets.clear();

        String userid = SPUtils.getParamString(getActivity(), SPConstant.USER_ID, null);
        //更具id判断自选是否出现

        Logs.s("     自选logs  " + userid);
        if (userid != null) {
            fragmentsMarKet.add(zxMarketFragmnet);
            //判断是否有值 有值在set过去
            if (allMaketList.getData().getUserMaketList() != null) {

                Logs.s("     自选logs  " + allMaketList.getData().getUserMaketList().size());

                if (allMaketList.getData().getUserMaketList().size() > 0) {
                    zxMarketFragmnet.dataChange();
                }
            }
            listTitle.add(getContext().getResources().getString(R.string.home_optional));
        }

        //遍历各个市场
        for (int subscript = 0; subscript < allMaketList.getData().getAllMaketList().size(); subscript++) {
            //新增一个title
            listTitle.add(allMaketList.getData().getAllMaketList().get(subscript).getCategoryName());
            allMarketFragmnet = new AllMarketFragmnet();
            allMarketFragmnet.setAllMaketListList(allMaketList.getData().getAllMaketList().get(subscript).getMarketList());
            listAllMarketFragmnets.add(allMarketFragmnet);
            fragmentsMarKet.add(allMarketFragmnet);
        }

        if (allMaketAdapter == null) {

            //市场页面适配器
            allMaketAdapter = new HomesViewPagerAdapter(getActivity().getSupportFragmentManager(), fragmentsMarKet);
            otherMarketViewPager.setAdapter(allMaketAdapter);
            initAllMaketListIndicator(listTitle);//标题 tablayout

        } else {
            allMaketAdapter.notifyDataSetChanged();
        }

        if (indexs == 0) {
            initAllMaketListIndicator(listTitle);//标题 tablayout
        }
        otherMarketViewPager.setCurrentItem(indexs);

        zxMarketFragmnet.onHiddenChanged(true);

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(CollectMarketStateEvent event) {
        Logs.s("   收藏成功...  ");
        indexPagePresenter.getIndexPageSilentRefreshModule();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(RefreshHomeFragmentEvent event) {
        KLog.d("RefreshHomeFragmentEvent");
        if (mMarketCategoryClient == null) {
            initWebsocket();
        }
        mRefreshLayout.autoRefresh();
    }

    @Subscribe
    public void onEvent(MyEvents event) {
        switch (event.status_type) {
            case MyEvents.LoginEsc://退出登录
                indexPagePresenter.getIndexPageRefreshModule();
                Logs.s(" 登录成功了  重新登录    ");
                indexs = 0;
                break;
            case MyEvents.LoginSuccess://登陆成功通知
                mRefreshLayout.autoRefresh();
                Logs.s(" 登录成功了 登陆成功通知     ");
                indexs = 0;
                break;
            /**
             *  修改密码 重新登录
             */
            case MyEvents.User_Update_Passwrod_Success:
                Logs.s(" 登录成功了  重新登录    ");
                indexPagePresenter.getIndexPageRefreshModule();
                indexs = 0;
                break;
            /**
             * token失效 刷新
             */
            case MyEvents.Token_failure:
                Logs.s(" 登录成功了  token失效    ");
                indexPagePresenter.getIndexPageRefreshModule();
                indexs = 0;
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Logs.s("   fragment1 onResume  ");
        UMManager.onResume(OAXApplication.sContext, UMConstant.OaxHomeNFragment);
        if (!AppManager.isAppIsInBackground(OAXApplication.sContext) && OAXApplication.isScreenOn) {
            initWebsocket();
            KLog.d("WebSocketsManager initWebsocket");
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (OAXApplication.mIsBackground || !OAXApplication.isScreenOn) {
            KLog.d("WebSocketsManager disconnect");
//            WebSocketsManager.getInstance().disconnect(mMarketCategoryClient);
            if (mTopic != null && !mTopic.isDisposed()) {
                mTopic.dispose();
                mTopic = null;
            }
            if (mMarketCategoryClient != null) {
                mMarketCategoryClient.disconnect();
            }
            mMarketCategoryClient = null;
            mOnCreateStompClientListener = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Logs.s("   fragment1 onPause  ");
        UMManager.onPause(OAXApplication.sContext, UMConstant.OaxHomeNFragment);
//        if (OAXApplication.mIsBackground || !OAXApplication.isScreenOn) {
//            KLog.d("WebSocketsManager disconnect");
////            WebSocketsManager.getInstance().disconnect(mMarketCategoryClient);
//            if (mTopic != null && !mTopic.isDisposed()) {
//                mTopic.dispose();
//                mTopic = null;
//            }
//            if (mMarketCategoryClient != null) {
//                mMarketCategoryClient.disconnect();
//            }
//            mMarketCategoryClient = null;
//            mOnCreateStompClientListener = null;
//        }
    }

}
