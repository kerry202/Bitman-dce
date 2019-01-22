package cn.dagongniu.bitman.assets.presenter;

import android.app.Activity;

import cn.dagongniu.bitman.assets.bean.TakeRedPacketDetailsBean;
import cn.dagongniu.bitman.assets.module.TakeRedPacketDetailsModule;
import cn.dagongniu.bitman.assets.view.ITakeRedPacketDetailsView;
import cn.dagongniu.bitman.base.BasePresenter;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.https.StateBaseUtils;

/**
 * 发送红包详情 Presenter
 */
public class TakeRedPacketDetailsPresenter extends BasePresenter {

    private TakeRedPacketDetailsModule takeRedPacketDetailsModule;
    private ITakeRedPacketDetailsView iTakeRedPacketDetailsView;
    private Activity activity;
    RequestState state;

    public TakeRedPacketDetailsPresenter(ITakeRedPacketDetailsView iTakeRedPacketDetailsView, RequestState state) {
        super(iTakeRedPacketDetailsView);
        this.state = state;
        activity = (Activity) iTakeRedPacketDetailsView.getContext();
        this.iTakeRedPacketDetailsView = iTakeRedPacketDetailsView;
        takeRedPacketDetailsModule = new TakeRedPacketDetailsModule(activity);
    }

    public void getTakeRedPacketDetailsModule() {

        takeRedPacketDetailsModule.requestServerDataOne(new OnBaseDataListener<TakeRedPacketDetailsBean>() {

            @Override
            public void onNewData(TakeRedPacketDetailsBean data) {
                if (data.isSuccess()) {
                    //响应请求数据回去
                    StateBaseUtils.success(iTakeRedPacketDetailsView, state, data);
                    if (data.getData().getRedPacketLogDetailsPageInfo().getList() == null || data.getData().getRedPacketLogDetailsPageInfo().getList().size() <= 0) {
                        iTakeRedPacketDetailsView.setTakeRedPacketDetailsNull(data);
                    } else {
                        iTakeRedPacketDetailsView.setTakeRedPacketDetailsData(data);
                    }

                } else {
                    StateBaseUtils.failure(iTakeRedPacketDetailsView, state, data.getMsg());
                    iTakeRedPacketDetailsView.setRefreshErrer();
                }
            }

            @Override
            public void onError(String code) {

                StateBaseUtils.error(iTakeRedPacketDetailsView, state, code);
                iTakeRedPacketDetailsView.setRefreshErrer();
            }
        }, state, iTakeRedPacketDetailsView.getTakeRedPacketDetailsPageIndex() + "", iTakeRedPacketDetailsView.getTakeRedPacketDetailsPageSize() + "", iTakeRedPacketDetailsView.getTakeRedPacketDetailsId() + "");
    }

    /**
     * 加载更多
     */
    public void getTakeRedPacketDetailsLoadModule() {

        takeRedPacketDetailsModule.requestServerDataOne(new OnBaseDataListener<TakeRedPacketDetailsBean>() {
            @Override
            public void onNewData(TakeRedPacketDetailsBean data) {
                iTakeRedPacketDetailsView.setTakeRedPacketDetailsMoreData(data);
            }

            @Override
            public void onError(String code) {
                iTakeRedPacketDetailsView.setTakeRedPacketDetailsMoreErrer(code);
            }
        }, state, iTakeRedPacketDetailsView.getTakeRedPacketDetailsPageIndex() + "", iTakeRedPacketDetailsView.getTakeRedPacketDetailsPageSize() + "", iTakeRedPacketDetailsView.getTakeRedPacketDetailsId() + "");
    }

}
