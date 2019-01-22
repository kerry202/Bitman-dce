package cn.dagongniu.bitman.assets.presenter;

import android.app.Activity;

import cn.dagongniu.bitman.assets.bean.RedPacketIndexBean;
import cn.dagongniu.bitman.assets.module.RedPacketIndexModule;
import cn.dagongniu.bitman.assets.view.IRedPacketIndexView;
import cn.dagongniu.bitman.base.BasePresenter;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.https.StateBaseUtils;

/**
 * 红包首页 Presenter
 */
public class RedPacketIndexPresenter extends BasePresenter {

    private RedPacketIndexModule RedPacketIndexModule;
    private IRedPacketIndexView redPacketIndexView;
    private Activity activity;
    RequestState state;

    public RedPacketIndexPresenter(IRedPacketIndexView redPacketIndexView, RequestState state) {
        super(redPacketIndexView);
        this.state = state;
        activity = (Activity) redPacketIndexView.getContext();
        this.redPacketIndexView = redPacketIndexView;
        RedPacketIndexModule = new RedPacketIndexModule(activity);
    }

    /**
     * 普通红包信息获取
     */
    public void getRedIndexOrdinaryModule() {

        RedPacketIndexModule.requestServerDataOne(new OnBaseDataListener<RedPacketIndexBean>() {

            @Override
            public void onNewData(RedPacketIndexBean data) {
                if (data.isSuccess()) {
                    //响应请求数据回去
                    StateBaseUtils.success(redPacketIndexView, state, data.getMsg());
                    redPacketIndexView.setOrdinaryData(data);
                } else {
                    StateBaseUtils.failure(redPacketIndexView, state, data.getMsg());
                    redPacketIndexView.setOrdinaryDataErrer(data.getMsg());
                }
            }

            @Override
            public void onError(String code) {
                redPacketIndexView.setOrdinaryDataErrer(code);
                StateBaseUtils.error(redPacketIndexView, state, code);
            }
        }, state, redPacketIndexView.ordinaryTYPE);
    }

    /**
     * 随机and普通红包数据刷新
     */
    public void getRedIndexRefreshModule(RequestState requestState) {

        RedPacketIndexModule.requestServerDataOne(new OnBaseDataListener<RedPacketIndexBean>() {

            @Override
            public void onNewData(RedPacketIndexBean data) {
                if (data.isSuccess()) {
                    //响应请求数据回去
                    StateBaseUtils.success(redPacketIndexView, requestState, data.getMsg());
                    redPacketIndexView.setOrdinaryRefreshData(data);
                } else {
                    StateBaseUtils.failure(redPacketIndexView, requestState, data.getMsg());
                }
            }

            @Override
            public void onError(String code) {
                StateBaseUtils.error(redPacketIndexView, requestState, code);
            }
        }, requestState, redPacketIndexView.ordinaryTYPE);

        RedPacketIndexModule.requestServerDataOne(new OnBaseDataListener<RedPacketIndexBean>() {

            @Override
            public void onNewData(RedPacketIndexBean data) {
                if (data.isSuccess()) {
                    //响应请求数据回去
                    StateBaseUtils.success(redPacketIndexView, requestState, data.getMsg());
                    redPacketIndexView.setRandomRefreshData(data);
                } else {
                    StateBaseUtils.failure(redPacketIndexView, requestState, data.getMsg());
                }
            }

            @Override
            public void onError(String code) {
                StateBaseUtils.error(redPacketIndexView, requestState, code);
            }
        }, requestState, redPacketIndexView.randomTYPE);
    }

    /**
     * 随机红包信息获取
     */
    public void getRedIndexRandomModule() {

        RedPacketIndexModule.requestServerDataOne(new OnBaseDataListener<RedPacketIndexBean>() {

            @Override
            public void onNewData(RedPacketIndexBean data) {
                if (data.isSuccess()) {
                    //响应请求数据回去
                    StateBaseUtils.success(redPacketIndexView, state, data.getMsg());
                    redPacketIndexView.setRandomData(data);
                } else {
                    StateBaseUtils.failure(redPacketIndexView, state, data.getMsg());
                    redPacketIndexView.setRandomDataErrer(data.getMsg());
                }
            }

            @Override
            public void onError(String code) {
                redPacketIndexView.setRandomDataErrer(code);
                StateBaseUtils.error(redPacketIndexView, state, code);
            }
        }, state, redPacketIndexView.randomTYPE);
    }


}
