package cn.dagongniu.bitman.assets.view;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;

import butterknife.BindView;
import cn.dagongniu.bitman.OAXApplication;
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.assets.ADDAddressActivity;
import cn.dagongniu.bitman.assets.adapter.AddressAdapter;
import cn.dagongniu.bitman.assets.bean.CoinAddressListBean;
import cn.dagongniu.bitman.assets.bean.WithdradwalBean;
import cn.dagongniu.bitman.assets.presenter.CoinAddressListPresenter;
import cn.dagongniu.bitman.base.BaseDialogFragment;
import cn.dagongniu.bitman.customview.ClassicsHeader;
import cn.dagongniu.bitman.customview.LoadingState;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.HttpUtils;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.language.LanguageUtils;
import cn.dagongniu.bitman.utils.Logs;
import cn.dagongniu.bitman.utils.SkipActivityUtil;
import cn.dagongniu.bitman.utils.ToastUtil;
import cn.dagongniu.bitman.utils.events.MyEvents;

/**
 * 底部悬浮 添加地址
 */
public class BottomAddressFragment extends BaseDialogFragment implements View.OnClickListener, OnRefreshListener, ICoinAddressListView {

    @BindView(R.id.add_recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_no_add)
    TextView tvNoAdd;
    @BindView(R.id.rl_add)
    RelativeLayout rlAdd;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    ClassicsHeader mClassicsHeader;


    protected EventBus eventBus = EventBus.getDefault();
    String MarketId = null;
    String MarketName = null;
    int type = -1;
    AddressAdapter adapter;
    MyDialogFragment_Listener myDialogFragment_Listener;
    CoinAddressListBean coinAddressListBean;
    CoinAddressListPresenter coinAddressListPresenter;
    Bundle bundle;
    private WithdradwalBean withdradwalBean;

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        coinAddressListPresenter = new CoinAddressListPresenter(this, RequestState.STATE_REFRESH);
        coinAddressListPresenter.getCoinAddressListModule();
    }

    // 回调接口，用于传递数据给Activity -------
    public interface MyDialogFragment_Listener {
        void getDataFrom_DialogFragment(String add, boolean is, String remark);
    }

    public void setCoinAddressListBean(CoinAddressListBean coinAddressListBean, String MarketId, String marketName, int type) {
        this.coinAddressListBean = coinAddressListBean;
        this.MarketId = MarketId;
        this.MarketName = marketName;
        this.type = type;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            myDialogFragment_Listener = (MyDialogFragment_Listener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implementon MyDialogFragment_Listener");
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        mWindow.setGravity(Gravity.BOTTOM);
        mWindow.setWindowAnimations(R.style.BottomDialog);
        mWindow.setLayout(mWidth, mHeight / 2);
    }

    @Override
    protected int setLayoutId() {
        return R.layout.bottom_address_fragment_layout;
    }

    @Override
    protected void initView() {
        super.initView();
        eventBus.register(this);
        bundle = new Bundle();
        rlAdd.setOnClickListener(this);
        initSmartRefresh();
    }

    private void initSmartRefresh() {
        /**
         * 事件处理
         */
        mClassicsHeader = (ClassicsHeader) mRefreshLayout.getRefreshHeader();
        LanguageUtils.setHeaderLanguage(mClassicsHeader, getActivity());
        mRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void initData() {
        super.initData();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AddressAdapter(getContext());
        if (coinAddressListBean != null) {
            tvNoAdd.setVisibility(View.GONE);
            adapter.setNewData(coinAddressListBean.getData().getList());
            mRecyclerView.setAdapter(adapter);
        } else {
            tvNoAdd.setVisibility(View.VISIBLE);
        }
        initEnvt();
    }

    private void initEnvt() {
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Logs.s("   adressclick     " + position);
                CoinAddressListBean.DataBean.ListBean listBean = coinAddressListBean.getData().getList().get(position);

                Logs.s("   adressclick  2   " + listBean.getAddress());
                getUserMsg(listBean.getAddress(), position);

            }
        });
    }

    private void getUserMsg(String address, final int position) {

        HashMap<String, String> map = new HashMap<>();

        HttpUtils.getInstance().postLangIdToken(Http.withdrawal_user_msg + "?address=" + address, map, getActivity(), new OnBaseDataListener<String>() {
            @Override
            public void onNewData(String data) {

                Logs.s("     用户信息 onNewData  ；    " + data);
                withdradwalBean = new Gson().fromJson(data, WithdradwalBean.class);

                if (withdradwalBean.data != null && withdradwalBean.data.size() > 0) {
                    CoinAddressListBean.DataBean.ListBean listitem = adapter.getData().get(position);
                    myDialogFragment_Listener.getDataFrom_DialogFragment(listitem.getAddress(), true, listitem.getRemark());
                    BottomAddressFragment.this.dismiss();
                } else {
                    CoinAddressListBean.DataBean.ListBean listitem = adapter.getData().get(position);
                    myDialogFragment_Listener.getDataFrom_DialogFragment(listitem.getAddress(), false, listitem.getRemark());
                    BottomAddressFragment.this.dismiss();
                }

            }

            @Override
            public void onError(String code) {
                Logs.s("     用户信息 onNewData  ；    " + code);
            }
        }, RequestState.STATE_REFRESH);
    }

    public WithdradwalBean getWithdradwalBean(){
        return withdradwalBean;
    }

    /**
     * 刷新回来
     *
     * @param coinAddressListData
     */
    @Override
    public void setCoinAddressListData(CoinAddressListBean coinAddressListData) {
        mRefreshLayout.finishRefresh();
        this.coinAddressListBean = coinAddressListData;
        if (coinAddressListBean != null) {
            tvNoAdd.setVisibility(View.GONE);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter.setNewData(coinAddressListBean.getData().getList());
            mRecyclerView.setAdapter(adapter);
        } else {
            tvNoAdd.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (eventBus.isRegistered(this)) {
            eventBus.unregister(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_add:
                OAXApplication.getInstance().MarketName = MarketName;
                OAXApplication.getInstance().MarketId = MarketId;
                OAXApplication.getInstance().type = type;
                SkipActivityUtil.skipAnotherActivity(getActivity(), ADDAddressActivity.class);
                break;
        }
    }

    @Override
    public String getCoinId() {
        return MarketId;
    }

    @Override
    public int getCoinAddressListIndex() {
        return 1;
    }

    @Override
    public int getCoinAddressListPageSize() {
        return 100;
    }

    /**
     * 参数有误-去登陆
     *
     * @param msg
     */
    @Override
    public void goLogin(String msg) {
        ToastUtil.ShowToast(msg);
    }


    @Subscribe
    public void onEvent(MyEvents event) {
        switch (event.status_type) {
            case MyEvents.Add_Withdrawal_Adderss_Success://新增地址成功的通知
                mRefreshLayout.autoRefresh();
                break;

        }

    }


    @Override
    public void showToask(String str) {
        ToastUtil.ShowToast(str);
    }

    @Override
    public void showDialog(String msg) {

    }

    @Override
    public void toOtherActivity(Class<?> cls) {

    }

    @Override
    public void toFinishActivity() {

    }

    @Override
    public void setData(Object data) {

    }

    @Override
    public void setXState(LoadingState xState, String msg) {

    }

    @Override
    public void setRefresh(Object obj) {

    }

    @Override
    public void setLoadMore(Object obj) {

    }

}
