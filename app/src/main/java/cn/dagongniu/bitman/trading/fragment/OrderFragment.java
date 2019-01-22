package cn.dagongniu.bitman.trading.fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import cn.dagongniu.bitman.OAXApplication;
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.base.BaseFragment;
import cn.dagongniu.bitman.constant.Constant;
import cn.dagongniu.bitman.customview.ClassicsFooter;
import cn.dagongniu.bitman.customview.ClassicsHeader;
import cn.dagongniu.bitman.https.PrefUtils;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.language.LanguageUtils;
import cn.dagongniu.bitman.trading.adapter.OrderRecordAdapter;
import cn.dagongniu.bitman.trading.bean.OrderSelectNoticeBean;
import cn.dagongniu.bitman.trading.bean.OrdersRecordBean;
import cn.dagongniu.bitman.trading.presenter.OrdersRecordPresenter;
import cn.dagongniu.bitman.trading.view.IOrdersRecordView;
import cn.dagongniu.bitman.utils.Logger;
import cn.dagongniu.bitman.utils.ToastUtil;
import cn.dagongniu.bitman.utils.events.MyEvents;

/**
 * 订单
 */
public class OrderFragment extends BaseFragment implements IOrdersRecordView, OnRefreshListener,
        OnLoadMoreListener {

    private static final String TAG = "OrderFragment";

    @BindView(R.id.ent_recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_ll_cz)
    TextView tvLlCz;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.rl_no_data)
    LinearLayout rlNodata;

    ClassicsFooter mClassicsFooter;
    ClassicsHeader mClassicsHeader;
    OrdersRecordPresenter ordersRecordPresenter;
    OrderRecordAdapter orderRecordAdapter;
    int page = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.entrust_fragment_orders_layout;
    }

    @Override
    protected void initView() {
        super.initView();
        orderRecordAdapter = new OrderRecordAdapter(getContext());
        initSmartRefresh();
        tvLlCz.setText(R.string.entrust_order_lx);
        initRecyc();

    }

    @Override
    protected void initData() {
        super.initData();
        ordersRecordPresenter = new OrdersRecordPresenter(this, RequestState.STATE_REFRESH);
        mRefreshLayout.autoRefresh();

        if (PrefUtils.getNetState(OAXApplication.getContext(), 0) == 0) {
            rlNodata.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            ToastUtil.ShowToast(getResources().getString(R.string.http_network_errer));

        }
    }

    private void initSmartRefresh() {
        /**
         * 事件处理
         */
        mClassicsFooter = (ClassicsFooter) mRefreshLayout.getRefreshFooter();
        mClassicsHeader = (ClassicsHeader) mRefreshLayout.getRefreshHeader();
        LanguageUtils.setFooterLanguage(mClassicsFooter, getActivity());
        LanguageUtils.setHeaderLanguage(mClassicsHeader, getActivity());
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadMoreListener(this);
    }


    /**
     * 数据适配器
     */
    private void initRecyc() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    }

    OrderSelectNoticeBean orderSelectNoticeBean;

    @Subscribe
    public void onEvent(MyEvents event) {
        switch (event.status_type) {
            case MyEvents.Order_Select_Notice://发送订单筛选通知
                orderSelectNoticeBean = (OrderSelectNoticeBean) event.something;
                Logger.e(TAG, event.errmsg);
                mRefreshLayout.autoRefresh();
                break;
        }
    }

    /**
     * 开始时间
     *
     * @return
     */
    @Override
    public String getBeginDate() {
        return orderSelectNoticeBean == null ? "" : orderSelectNoticeBean.getBeginDate();
    }

    /**
     * 结束时间
     *
     * @return
     */
    @Override
    public String getEndDate() {
        return orderSelectNoticeBean == null ? "" : orderSelectNoticeBean.getEndDate();
    }

    /**
     * 交易对id
     *
     * @return
     */
    @Override
    public String getMarketId() {
        return orderSelectNoticeBean == null ? "" : orderSelectNoticeBean.getMarketId();
    }

    @Override
    public String getType() {
        return orderSelectNoticeBean == null ? "" : orderSelectNoticeBean.getType();
    }

    @Override
    public String getPageNo() {
        return page + "";
    }

    @Override
    public String getPageSize() {
        return Constant.PAGESIZE + "";
    }

    @Override
    public void goLogin(String msg) {
        mRefreshLayout.finishRefresh();
        ToastUtil.ShowToast(msg);
    }

    /**
     * 暂无数据
     *
     * @param ordersRecordBean
     */
    @Override
    public void isNull(OrdersRecordBean ordersRecordBean) {
        orderRecordAdapter.setNewData(null);
        orderRecordAdapter.notifyDataSetChanged();
        mRefreshLayout.finishRefresh();
        rlNodata.setVisibility(View.VISIBLE);
        if (ordersRecordBean.getData().isHasNextPage()) {
            mRefreshLayout.finishLoadMore();
        } else {
            mRefreshLayout.finishLoadMoreWithNoMoreData();
        }
    }

    /**
     * 数据回调
     *
     * @param ordersRecordBean
     */
    @Override
    public void setOrdersRecordData(OrdersRecordBean ordersRecordBean) {
        rlNodata.setVisibility(View.GONE);
        mRefreshLayout.finishRefresh();
        mRefreshLayout.setNoMoreData(false);
        if (ordersRecordBean.getData().isHasNextPage()) {
            mRefreshLayout.finishLoadMore();
        } else {
            mRefreshLayout.finishLoadMoreWithNoMoreData();
        }
        orderRecordAdapter.setNewData(ordersRecordBean.getData().getList());
        mRecyclerView.setAdapter(orderRecordAdapter);
    }

    /**
     * 加载错误
     *
     * @param msg
     */
    @Override
    public void setDataErrer(String msg) {
        mRefreshLayout.finishRefresh();
    }

    /**
     * 加载更多
     *
     * @param ordersRecordBean
     */
    @Override
    public void setOrdersRecordLoadMoreData(OrdersRecordBean ordersRecordBean) {
        if (ordersRecordBean.getData().isHasNextPage()) {
            mRefreshLayout.finishLoadMore();
        } else {
            mRefreshLayout.finishLoadMoreWithNoMoreData();
        }
        orderRecordAdapter.addData(ordersRecordBean.getData().getList());
        orderRecordAdapter.notifyDataSetChanged();
    }

    @Override
    public void setDataLoadErrer(String msg) {
        mRefreshLayout.finishLoadMore();
        ToastUtil.ShowToast(msg);
    }

    /**
     * 加载更多
     *
     * @param refreshLayout
     */
    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        page++;
        ordersRecordPresenter.getOrdersRecordLoadModule();
    }

    /**
     * 刷新
     *
     * @param refreshLayout
     */
    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        page = 1;
        ordersRecordPresenter.getOrdersRecordModule();
    }
}
