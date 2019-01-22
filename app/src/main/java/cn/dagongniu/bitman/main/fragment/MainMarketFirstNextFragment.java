package cn.dagongniu.bitman.main.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.base.BaseFragment;
import cn.dagongniu.bitman.constant.Constant;
import cn.dagongniu.bitman.main.adapter.MainMarketNextAdapter;
import cn.dagongniu.bitman.main.bean.IndexPageBean;
import cn.dagongniu.bitman.main.bean.OaxMarketBean;
import cn.dagongniu.bitman.utils.ClassConversionUtils;
import cn.dagongniu.bitman.utils.Logger;
import cn.dagongniu.bitman.utils.SkipActivityUtil;
import cn.dagongniu.bitman.utils.events.MyEvents;
import cn.dagongniu.bitman.views.MyGridLayoutManager;


/**
 * 首页推荐主流市场  第二页
 */
public class MainMarketFirstNextFragment extends BaseFragment {

    private static final String TAG = "MainMarketFirstNextFrag";
    private List<IndexPageBean.DataBean.RecommendMarketListBean> recommendMarketListBeans;

    public void setRecommendMarketListNext(List<IndexPageBean.DataBean.RecommendMarketListBean> recommendMarketListBeans) {
        this.recommendMarketListBeans = recommendMarketListBeans;
    }

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    private MainMarketNextAdapter mainMarketNextAdapter;
    //websocket 排序出来的需要刷新的数据
    List<IndexPageBean.DataBean.RecommendMarketListBean> MarKetRefreshBeans = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mainmarket_first;
    }

    @Override
    protected void initView() {
        super.initView();
        MyGridLayoutManager gridLayoutManager = new MyGridLayoutManager(mContext, 3);

        gridLayoutManager.setScrollEnabled(false);
        recyclerview.setLayoutManager(gridLayoutManager);
        recyclerview.setNestedScrollingEnabled(false);
        initAdapter();
    }

    @Override
    protected void initData() {
        super.initData();
        if (mainMarketNextAdapter != null) {
            mainMarketNextAdapter.setNewData(recommendMarketListBeans);

        }
    }

    private void initAdapter() {
        mainMarketNextAdapter = new MainMarketNextAdapter(getContext(), recommendMarketListBeans);
        recyclerview.setAdapter(mainMarketNextAdapter);
        recyclerview.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                IndexPageBean.DataBean.RecommendMarketListBean item = (IndexPageBean.DataBean.RecommendMarketListBean) adapter.getItem(position);
                IndexPageBean.DataBean.RecommendMarketListBean.MarketCoinBean marketCoin = item.getMarketCoin();
                if (marketCoin != null) {
                    SkipActivityUtil.skipToKLineActivity(marketCoin.getMarketId(), Constant.KLINE_MINTYPE_1, getActivity());
                }
            }
        });
    }

    @Subscribe
    public void onEvent(MyEvents event) {
        switch (event.status_type) {
            case MyEvents.Home_WebSocket_Market_List://首页交易对的websocket推送数据数据
                List<OaxMarketBean> webMarketBeans = event.listOaxMarketBeanGson;
                Logger.i(TAG, "第二页websocket收到刷新数据通知！" + webMarketBeans.size());
                MarKetRefreshBeans = ClassConversionUtils.toRecommendMarketListBean(recommendMarketListBeans, webMarketBeans);
                if (MarKetRefreshBeans != null && recommendMarketListBeans != null && MarKetRefreshBeans.size() == recommendMarketListBeans.size()) {
                    mainMarketNextAdapter.setWebSocketMainMarket(MarKetRefreshBeans);
                }
                mainMarketNextAdapter.notifyDataSetChanged();
                break;
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);

    }
}
