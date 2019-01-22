package cn.dagongniu.bitman.trading.presenter;

import android.app.Activity;


import java.util.HashMap;

import cn.dagongniu.bitman.base.OAXBasePresenter;
import cn.dagongniu.bitman.https.CommonJsonToBean;
import cn.dagongniu.bitman.https.OAXStateBaseUtils;
import cn.dagongniu.bitman.https.OnDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.trading.bean.EntrustInfoBean;
import cn.dagongniu.bitman.trading.bean.CurrentEntrustBean;
import cn.dagongniu.bitman.trading.fragment.EntrustIView;
import cn.dagongniu.bitman.trading.module.EntrustModule;
import cn.dagongniu.bitman.utils.Logs;

public class EntrustPresenter extends OAXBasePresenter {

    private EntrustModule mModule;
    private Activity activity;
    EntrustIView view;
    RequestState mState;

    public EntrustPresenter(EntrustIView view) {
        super(view);
        activity = (Activity) view.getContext();
        this.view = view;
        mModule = new EntrustModule(activity);
    }

    /**
     * 获取 用户余额 托管订单信息
     *
     * @param marketId
     * @param state
     */
    public void getEntrustInfo(int marketId, RequestState state) {
        mModule.getCommitteeList(marketId + "", state, new EntrustModule.OnEntrustDataListener() {
            @Override
            public void newData(CommonJsonToBean<EntrustInfoBean> bean) {
                if (bean.getSuccess()) {
                    view.onTopicTradeListData(bean.getData());
                } else {
                    view.showToast(bean.getMsg());
                }

            }

            @Override
            public void onError(String msg) {
                view.showToast(msg);
            }
        });
    }

    /**
     * 余额 订单信息
     */
    public void topicTradeList(int marketId, String userId) {
        mModule.topicTradeList(marketId, userId, new EntrustModule.OnUserEntrustInfoListener() {
            @Override
            public void onTopicTradeListData(EntrustInfoBean bean) {
                view.onTopicTradeListData(bean);
            }
        });
    }

    /**
     * 取消订阅 余额 订单信息
     */
    public void unTopicTradeList() {
        if (mModule != null) {
            mModule.unTopicTradeList();
        }
    }

    /**
     * 点对点获取 余额 订单信息
     */
    public void sendTradeList(int marketId, int userId) {
        mModule.sendTradeList(marketId, userId);
    }

    public void cancellations(int id, RequestState state) {
        mModule.cancellations(id, null, state, new EntrustModule.OnCancellationsListener() {
            @Override
            public void newCancellationsInfo(CommonJsonToBean<String> data) {
                view.onCancellationsState(data);
            }

            @Override
            public void onError(String msg) {
                view.showToast(msg);
            }
        });
    }

    /**
     * 当前委托
     *
     * @param map
     * @param state
     */
    public void getCurrentEntrust(HashMap<String, Object> map, RequestState state, boolean isRefresh, boolean isLoadMore) {
        mModule.getCurrentEntrust(map, state, new OnDataListener<CurrentEntrustBean>() {
            @Override
            public void onNewData(CommonJsonToBean<CurrentEntrustBean> data) {
                Logs.s("   当前委托   " + data);
                if (data == null || data.getData() == null || data.getData().getList() == null || data.getData().getList().size() == 0) {
                    view.onCurrentEnstrust(data, 1);
                    OAXStateBaseUtils.isNull(view, state, data.getMsg());
                    Logs.s("   当前委托 33333WW   " + data);
                } else {
                    if (isRefresh) {
                        Logs.s("   当前委托  1111   " + data);
                        view.onRefreshCurrentEnstrust(data);
                    } else if (isLoadMore) {
                        Logs.s("   当前委托  2222   " + data);
                        view.onLoadMoreCurrentEnstrust(data);
                    } else {
                        Logs.s("   当前委托  0000   " + data);
                        view.onCurrentEnstrust(data, 0);
                    }
                }
            }

            @Override
            public void onError(String msg) {
                OAXStateBaseUtils.error(view, state, msg);
            }
        });
    }
}
