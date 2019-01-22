package cn.dagongniu.bitman.trading.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import cn.dagongniu.bitman.OAXApplication;
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.base.OAXBaseFragment;
import cn.dagongniu.bitman.customview.LoadingState;
import cn.dagongniu.bitman.customview.XHLoadingView;
import cn.dagongniu.bitman.https.CommonJsonToBean;
import cn.dagongniu.bitman.https.OAXStateBaseUtils;
import cn.dagongniu.bitman.https.PrefUtils;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.https.UrlParams;
import cn.dagongniu.bitman.trading.adapter.EntrustOrderAdapter;
import cn.dagongniu.bitman.trading.bean.CurrentEntrustBean;
import cn.dagongniu.bitman.trading.bean.EntrustInfoBean;
import cn.dagongniu.bitman.trading.presenter.EntrustPresenter;
import cn.dagongniu.bitman.utils.Logs;
import cn.dagongniu.bitman.utils.ToastUtil;

/**
 * 委托                                                                                                   , OnLoadMoreListener
 */
public class EntrustFragment extends OAXBaseFragment implements XHLoadingView.OnReTryClickListener, EntrustIView, OnRefreshListener {

    @BindView(R.id.ent_recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_ll_cz)
    TextView tvLlCz;
    @BindView(R.id.lv_loading)
    XHLoadingView mLoadingView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    EntrustOrderAdapter adapter;
    private EntrustPresenter mEntrustPresenter;
    private int canclePosition;
    private int mMarketId;
    private CurrentEntrustBean mCurrentEntrustBean;
    private boolean aBoolean = false;
    private List<CurrentEntrustBean.ListBean> list = new ArrayList<>();

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int id = OAXApplication.defaultMarketId;
            if (id > 0) {
                mMarketId = id;
                getCurrentData();
            } else {
                handler.sendEmptyMessageDelayed(1, 100);
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.entrust_fragment_layout;
    }

    @Override
    protected void initView() {
        super.initView();
        try {
            Bundle bundle = getArguments();
            aBoolean = bundle.getBoolean("state", false);
            mMarketId = bundle.getInt(UrlParams.marketId, 0);
            if (mMarketId <= 0) {
                handler.sendEmptyMessageDelayed(1, 100);
            }
            Logs.s("    ExceptionExceptionException  111     " + mMarketId);
        } catch (Exception e) {

            Logs.s("    ExceptionExceptionException  222     ");
            aBoolean = false;
            mMarketId = 0;
        }

        tvLlCz.setText(R.string.entrust_order_cz);
        mRefreshLayout.setEnableRefresh(false);

        if (!aBoolean) {
            mRefreshLayout.setEnableRefresh(true);
            initSmartRefresh();
        }

        initRecyc();
        initEmpty();

    }

    private void initSmartRefresh() {
        /**
         * 事件处理
         */

//        mClassicsFooter = (ClassicsFooter) mRefreshLayout.getRefreshFooter();
//        mClassicsHeader = (ClassicsHeader) mRefreshLayout.getRefreshHeader();
//        LanguageUtils.setFooterLanguage(mClassicsFooter, getActivity());
//        LanguageUtils.setHeaderLanguage(mClassicsHeader, getActivity());
        mRefreshLayout.setOnRefreshListener(this);
//        mRefreshLayout.setOnLoadMoreListener(this);
    }


    private void initEmpty() {
        mLoadingView.withLoadEmptyText(getResources().getString(R.string.no_data)).
                withEmptyIcon(R.mipmap.no_data_icon).
                withBtnEmptyEnnable(false)
                .withLoadErrorText(getResources().getString(R.string.network_errors))
                .withErrorIco(R.mipmap.net_error_icon)
                .withBtnErrorText(getResources().getString(R.string.http_clickretry))
                .withLoadNoNetworkText(getResources().getString(R.string.network_request_failed))
                .withNoNetIcon(R.drawable.state_no_net)
                .withBtnNoNetText(getResources().getString(R.string.http_clickretry))
                .build();
        mLoadingView.setWithOnRetryListener(this);
    }

    @Override
    public void initData() {
        super.initData();
        mEntrustPresenter = new EntrustPresenter(this);
        getCurrentData();


    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private void getCurrentData() {

        HashMap<String, Object> map = new HashMap<>();
        map.put(UrlParams.pageNo, 1);
        map.put(UrlParams.pageSize, 100);
        map.put(UrlParams.status, "0,1,2");

        if (aBoolean) {
            map.put(UrlParams.marketId, mMarketId);
        }


        mEntrustPresenter.getCurrentEntrust(map, RequestState.STATE_ALL_SCREEN_AND_DIALOG, false, false);

        if (PrefUtils.getNetState(OAXApplication.getContext(), 0) == 0) {
            mLoadingView.setVisibility(View.VISIBLE);
//            mRecyclerView.setVisibility(View.GONE);
            ToastUtil.ShowToast(getResources().getString(R.string.http_network_errer));
        }
    }


    /**
     * 数据适配器
     */
    private void initRecyc() {

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new EntrustOrderAdapter(EntrustOrderAdapter.TYPE_ENTRUST, mContext);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {

            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                canclePosition = position;
                CurrentEntrustBean.ListBean item = (CurrentEntrustBean.ListBean) adapter.getItem(position);
                if (item != null) {
                    mEntrustPresenter.cancellations(item.getId(), RequestState.STATE_DIALOG);
                }
            }
        });
    }


    @Override
    public void onRetry(View view) {
        getCurrentData();
    }

    @Override
    public void onTopicTradeListData(EntrustInfoBean bean) {

    }

    @Override
    public void onCancellationsState(CommonJsonToBean<String> state) {
        try {
            if (state.getSuccess()) {
                adapter.remove(canclePosition);

            }
            List<CurrentEntrustBean.ListBean> data = adapter.getData();
            if (data == null || data.size() == 0) {
                OAXStateBaseUtils.isNull(this, RequestState.STATE_ALL_SCREEN_AND_DIALOG, getResources().getString(R.string.no_data_available));
            }
            if (!TextUtils.isEmpty(state.getMsg())) {
                ToastUtil.ShowToast(state.getMsg(), getActivity());
            }
        } catch (Exception e) {

        }
    }


    @Override
    public void onCurrentEnstrust(CommonJsonToBean<CurrentEntrustBean> bean, int state) {

        if (state == 1) {
            mLoadingView.setVisibility(View.VISIBLE);
        } else {
            try {
                mLoadingView.setVisibility(View.GONE);
                if (bean != null) {
                    mCurrentEntrustBean = bean.getData();
                    list = bean.getData().getList();
                    adapter.setNewData(list);
                }
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onRefreshCurrentEnstrust(CommonJsonToBean<CurrentEntrustBean> bean) {
        try {
            mLoadingView.setVisibility(View.GONE);
            mRefreshLayout.finishRefresh();
            mRefreshLayout.setNoMoreData(false);
            if (bean != null) {
                mCurrentEntrustBean = bean.getData();
                List<CurrentEntrustBean.ListBean> list = bean.getData().getList();
                adapter.setNewData(list);
            }
        } catch (Exception e) {
            list.clear();
            adapter.setNewData(list);
        }
    }

    @Override
    public void onLoadMoreCurrentEnstrust(CommonJsonToBean<CurrentEntrustBean> bean) {
        try {
            mLoadingView.setVisibility(View.GONE);
            if (bean != null) {
                mCurrentEntrustBean = bean.getData();
                List<CurrentEntrustBean.ListBean> list = bean.getData().getList();
                if (list == null || list.size() == 0) {
//                    mRefreshLayout.finishLoadMoreWithNoMoreData();
                } else {
//                    mRefreshLayout.finishLoadMore();
                }
                adapter.addData(list);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void showToast(String msg) {
        if (mRefreshLayout != null) {
            mRefreshLayout.finishRefresh();
//            mRefreshLayout.finishLoadMore();
        }
        if (!TextUtils.isEmpty(msg)) {
            ToastUtil.ShowToast(msg, getActivity());
        }
    }

    @Override
    public void setXState(LoadingState xState, String msg) {
        if (mLoadingView != null) {
            mLoadingView.setState(xState, msg);
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(UrlParams.pageNo, 1);
        map.put(UrlParams.pageSize, 100);
        map.put(UrlParams.status, "0,1,2");
        if (aBoolean) {
            map.put(UrlParams.marketId, mMarketId);
        }

        mEntrustPresenter.getCurrentEntrust(map, RequestState.STATE_REFRESH, true, false);
    }

    public void setMarketId(int marketId) {
        this.mMarketId = marketId;
        Logs.s("     setMarketIdsetMarketId   " + marketId);
        getCurrentData();
    }


//    @Override
//    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
//        HashMap<String, Object> map = new HashMap<>();
//        int num = 1;
//        if (mCurrentEntrustBean != null) {
//            num = mCurrentEntrustBean.getPageNum() + 1;
//        }
//        map.put(UrlParams.pageNo, num);
//        map.put(UrlParams.pageSize, 100);
//        map.put(UrlParams.status, "0,1,2");
////        map.put(UrlParams.marketId, mMarketId);
//        mEntrustPresenter.getCurrentEntrust(map, RequestState.STATE_LOADMORE, false, true);
//    }
}
