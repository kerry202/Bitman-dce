package cn.dagongniu.bitman.assets.fragment;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.socks.library.KLog;

import org.greenrobot.eventbus.Subscribe;

import java.math.BigDecimal;

import butterknife.BindView;
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.assets.AssetsDetailsActivity;
import cn.dagongniu.bitman.assets.WithTopRecordActivity;
import cn.dagongniu.bitman.assets.WithdTopSearchActivity;
import cn.dagongniu.bitman.assets.adapter.MyAssetsAdapter;
import cn.dagongniu.bitman.assets.bean.AssetsPropertyListBean;
import cn.dagongniu.bitman.assets.presenter.AssetsPropertyListPresenter;
import cn.dagongniu.bitman.assets.view.IAssetsPropertyListView;
import cn.dagongniu.bitman.base.BaseFragment;
import cn.dagongniu.bitman.constant.SPConstant;
import cn.dagongniu.bitman.constant.UMConstant;
import cn.dagongniu.bitman.customview.MyTradingToolbar;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.utils.AppConstants;
import cn.dagongniu.bitman.utils.DialogUtils;
import cn.dagongniu.bitman.utils.Logger;
import cn.dagongniu.bitman.utils.Logs;
import cn.dagongniu.bitman.utils.SPUtils;
import cn.dagongniu.bitman.utils.ToastUtil;
import cn.dagongniu.bitman.utils.events.MyEvents;
import cn.dagongniu.bitman.utils.um.UMManager;
import cn.dagongniu.bitman.views.path2.MoBikeView;


/**
 * 资产首页
 */
public class AssetsFragment extends BaseFragment implements View.OnClickListener, IAssetsPropertyListView, SensorEventListener {

    private static final String TAG = "AssetsFragment";

    @BindView(R.id.assets_toolbar)
    MyTradingToolbar myTradingToolbar;
    @BindView(R.id.assets_recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.rl_topup)
    Button rlTopup;
    @BindView(R.id.rl_withdrawal)
    Button rlWithdrawal;
    @BindView(R.id.tv_assets_btc)
    TextView tvAssetsBtc;
    @BindView(R.id.tv_assets_eth)
    TextView tvAssetsEth;
    @BindView(R.id.tv_assets_cny)
    TextView tvAssetsCny;
    //    @BindView(R.id.refreshLayout)
//    SmartRefreshLayout mRefreshLayout;
//    OnRefreshListener,
    @BindView(R.id.im_open_eyes)
    ImageView imOpenEyes;
    @BindView(R.id.rl_no_data)
    LinearLayout rlNodata;

    @BindView(R.id.mo_bike)
    MoBikeView mMobikeView;

    MyAssetsAdapter myAssetsAdapter;
    AssetsPropertyListPresenter assetsPropertyListPresenter;
    Bundle bundle;
    boolean isOpenEyes = true;

    AssetsPropertyListBean assetsPropertyListBean;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_oax_assets_layout;
    }

    @Override
    protected void initView() {
        super.initView();
        initToobar();
        initEyes();
        initSmartRefresh();
        initMob();
        myAssetsAdapter = new MyAssetsAdapter(getContext(), isOpenEyes);
        bundle = new Bundle();
        rlTopup.setOnClickListener(this);
        rlWithdrawal.setOnClickListener(this);
        assetsPropertyListPresenter = new AssetsPropertyListPresenter(this, RequestState.STATE_REFRESH);


        boolean islogin = (boolean) SPUtils.getParam(getActivity(), SPConstant.LOGIN_STATE, false);
        if (islogin) {
//            mRefreshLayout.autoRefresh();
        }
    }

    private SensorManager mSensorManager;

    private int[] mImgs = {
            R.mipmap.btc_icon,
            R.mipmap.btc_icon,
            R.mipmap.btc_icon,
            R.mipmap.btc_icon,
            R.mipmap.btc_icon,
            R.mipmap.eth_icon,
            R.mipmap.eth_icon,
            R.mipmap.eth_icon,
            R.mipmap.eth_icon,
            R.mipmap.eth_icon,
            R.mipmap.usdt_icon,
            R.mipmap.usdt_icon,
            R.mipmap.usdt_icon,
            R.mipmap.usdt_icon,
            R.mipmap.usdt_icon,
    };

    private Sensor mSensor;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];

            mMobikeView.onSensorChanged(y, x * 5);

        }
    }

    private void initMob() {
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mMobikeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMobikeView.onRandomChanged();
            }
        });
        addViews();
    }

    private void addViews() {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        for (int mImg : mImgs) {
            ImageView iv = new ImageView(mContext);
            iv.setImageResource(mImg);
            iv.setTag(R.id.wd_view_circle_tag, true);
            mMobikeView.addView(iv, lp);
        }
    }

    private void initSmartRefresh() {
        /**
         * 事件处理
         */
//        mRefreshLayout.setOnRefreshListener(this);
        imOpenEyes.setOnClickListener(this);
    }

    private void initToobar() {
        myTradingToolbar.setRightImgVisibility(true);
        myTradingToolbar.setLeftImgVisibility(true);
        myTradingToolbar.setTvLeftVisibility(true);
        myTradingToolbar.setSjVisibility(true);
        myTradingToolbar.setRightNameText(R.string.assets_record);
        myTradingToolbar.setTitleNameText(R.string.assets);
        myTradingToolbar.setBackgroundcolor(getContext().getResources().getColor(R.color.asset_title_color));
        myTradingToolbar.setRightTvColor(getContext().getResources().getColor(R.color.white));
        myTradingToolbar.setTitleColor(getContext().getResources().getColor(R.color.white));
        myTradingToolbar.setRightTvClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //历史记录
                toOtherActivity(WithTopRecordActivity.class);
                UMManager.onEvent(mContext, UMConstant.AssetsFragment, UMConstant.history);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        if (myAssetsAdapter != null) {
            myAssetsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    AssetsPropertyListBean.DataBean.CoinListBean bean = (AssetsPropertyListBean.DataBean.CoinListBean) adapter.getData().get(position);
                    bundle.putString("MarketName", bean.getShortName());
                    bundle.putString("MarketId", bean.getId() + "");
                    bundle.putString("icon", bean.getImage() + "");
                    openActivity(AssetsDetailsActivity.class, bundle);
                }
            });
        }
    }

    private void initEyes() {
        isOpenEyes = (boolean) SPUtils.getParam(getActivity(), SPConstant.ASSETS_EYES, true);
        if (isOpenEyes) {
            imOpenEyes.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.assets_hide_icon));
        } else {
            imOpenEyes.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.assets_show_icon));
        }
        setAssetsTopMoney(assetsPropertyListBean);
    }


    @Subscribe
    public void onEvent(MyEvents event) {
        switch (event.status_type) {
            case MyEvents.Withdrawal_Success://提现成功通知
                Logger.e(TAG, event.errmsg);
                assetsPropertyListPresenter.getAssetsPropertyListModule();
//                mRefreshLayout.autoRefresh();
                break;
            case MyEvents.LoginSuccess://登陆成功通知
            case MyEvents.RedPacketSuccess://发送红包成功通知
//                mRefreshLayout.autoRefresh();
                break;
            case MyEvents.ShareSuccess://红包分享成功啦
                DialogUtils.showTextDialog(getActivity(), event.errmsg);
                break;
        }

    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.rl_topup:
                bundle.putString(AppConstants.SEARCH, AppConstants.TOPUP);
                openActivity(WithdTopSearchActivity.class, bundle);
                UMManager.onEvent(mContext, UMConstant.AssetsFragment, UMConstant.TOPUP);
                break;
            case R.id.rl_withdrawal:
                bundle.putString(AppConstants.SEARCH, AppConstants.WITHDRAWAL);
                openActivity(WithdTopSearchActivity.class, bundle);
                UMManager.onEvent(mContext, UMConstant.AssetsFragment, UMConstant.WITHDRAWAL);
                break;
            case R.id.im_open_eyes:
                if (isOpenEyes) {
                    isOpenEyes = false;
                    UMManager.onEvent(mContext, UMConstant.AssetsFragment, UMConstant.hide_Assets);
                } else {
                    isOpenEyes = true;
                    UMManager.onEvent(mContext, UMConstant.AssetsFragment, UMConstant.unhide_Assets);
                }
                myAssetsAdapter.setOpenEyes(isOpenEyes);
                myAssetsAdapter.notifyDataSetChanged();
                SPUtils.setParam(getActivity(), SPConstant.ASSETS_EYES, isOpenEyes);
                initEyes();
                break;

        }
    }

    @Override
    public String getCoinName() {
        return null;
    }

    /**
     * 设置值
     *
     * @param assetsPropertyListBean
     */
    public void setAssetsTopMoney(AssetsPropertyListBean assetsPropertyListBean) {
        KLog.d("assetsPropertyListBean = " + new Gson().toJson(assetsPropertyListBean));
        if (isOpenEyes) {
            if (assetsPropertyListBean != null) {
                if (assetsPropertyListBean.getData().getTotal() != null) {
                    BigDecimal bigDecimalBtc = new BigDecimal(assetsPropertyListBean.getData().getTotal().getBtcPrice());
                    tvAssetsBtc.setText("≈" + bigDecimalBtc.setScale(8, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString() + " BTC");

                    BigDecimal bigDecimalCny = new BigDecimal(assetsPropertyListBean.getData().getTotal().getCnyPrice());
                    tvAssetsCny.setText("≈¥ " + bigDecimalCny.setScale(2, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString());

                    BigDecimal bigDecimalEth = new BigDecimal(assetsPropertyListBean.getData().getTotal().getEthPrice());
                    tvAssetsEth.setText("≈" + bigDecimalEth.setScale(6, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString() + " ETH");
                    return;
                }
            }
            tvAssetsBtc.setText("≈0.00000000 BTC");
            tvAssetsCny.setText("≈¥ 0.00");
            tvAssetsEth.setText("≈0.000000 ETH");

        } else {
            tvAssetsBtc.setText("******");
            tvAssetsCny.setText("******");
            tvAssetsEth.setText("******");
        }

    }

    /**
     * 回调数据
     *
     * @param assetsPropertyListBean
     */
    @Override
    public void setIAssetsPropertyData(AssetsPropertyListBean assetsPropertyListBean) {
        rlNodata.setVisibility(View.GONE);
        this.assetsPropertyListBean = assetsPropertyListBean;
        try {
//            mRefreshLayout.finishRefresh();

        } catch (Exception e) {
        }
        setAssetsTopMoney(assetsPropertyListBean);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        myAssetsAdapter.setNewData(assetsPropertyListBean.getData().getCoinList());
        mRecyclerView.setAdapter(myAssetsAdapter);
    }

    /**
     * 回调数据 列表为空
     *
     * @param noticeCenterReadDetailBean
     */
    @Override
    public void setIAssetsPropertyDataNull(AssetsPropertyListBean noticeCenterReadDetailBean) {
        this.assetsPropertyListBean = noticeCenterReadDetailBean;
        setAssetsTopMoney(assetsPropertyListBean);
        myAssetsAdapter.setNewData(null);
        myAssetsAdapter.notifyDataSetChanged();
//        mRefreshLayout.finishRefresh();
        rlNodata.setVisibility(View.VISIBLE);


        Logs.s("    assetsPropertyListPresenter   setIAssetsPropertyDataNull ");
    }

    /**
     * 刷新错误
     */
    @Override
    public void refreshErrer() {
        try {
//            mRefreshLayout.finishRefresh();
        } catch (Exception e) {
        }
    }

    @Override
    public void goLogin(String msg) {
        ToastUtil.ShowToast(msg);
    }


    @Override
    public int getType() {
        return 2;
    }

    /**
     * 刷新
     * <p>
     * //     * @param refreshLayout
     */
//    @Override
//    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
//        Log.e("okgo", "刷新");
//        assetsPropertyListPresenter.getAssetsPropertyListModule();
//    }
    @Override
    public void onResume() {
        super.onResume();
        String userid = SPUtils.getParamString(getActivity(), SPConstant.USER_ID, null);
        if (mSensorManager != null) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);

        }
        if (userid != null && userid.length() > 0) {
            if (assetsPropertyListPresenter != null) {
                assetsPropertyListPresenter.getAssetsPropertyListModule();
                Logs.s("   ZZZZZZZZZZZZZZz ");
            }

        }
        UMManager.onResume(mContext, UMConstant.AssetsFragment);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        UMManager.onPause(mContext, UMConstant.AssetsFragment);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
