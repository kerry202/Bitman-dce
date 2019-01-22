package cn.dagongniu.bitman.assets.presenter;

import android.app.Activity;

import cn.dagongniu.bitman.assets.bean.SendRedPacketRecordBean;
import cn.dagongniu.bitman.assets.module.SendRedPacketRecordModule;
import cn.dagongniu.bitman.assets.view.ISendRedPacketRecordView;
import cn.dagongniu.bitman.base.BasePresenter;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.https.StateBaseUtils;

/**
 * 我发出的红包记录 Presenter
 */
public class SendRedPacketRecordPresenter extends BasePresenter {

    private SendRedPacketRecordModule sendRedPacketRecordModule;
    private ISendRedPacketRecordView iSendRedPacketRecordView;
    private Activity activity;
    RequestState state;

    public SendRedPacketRecordPresenter(ISendRedPacketRecordView iSendRedPacketRecordView, RequestState state) {
        super(iSendRedPacketRecordView);
        this.state = state;
        activity = (Activity) iSendRedPacketRecordView.getContext();
        this.iSendRedPacketRecordView = iSendRedPacketRecordView;
        sendRedPacketRecordModule = new SendRedPacketRecordModule(activity);
    }

    public void getSendRedPacketRecordModule() {

        sendRedPacketRecordModule.requestServerDataOne(new OnBaseDataListener<SendRedPacketRecordBean>() {

            @Override
            public void onNewData(SendRedPacketRecordBean data) {
                if (data.isSuccess()) {
                    //响应请求数据回去
                    StateBaseUtils.success(iSendRedPacketRecordView, state, data);
                    if (data.getData().getPageInfo().getList() == null || data.getData().getPageInfo().getList().size() <= 0) {
                        iSendRedPacketRecordView.setSendRedPacketRecordNull(data);
                    } else {
                        iSendRedPacketRecordView.setSendRedPacketRecordData(data);
                    }

                } else {
                    StateBaseUtils.failure(iSendRedPacketRecordView, state, data.getMsg());
                    iSendRedPacketRecordView.setRefreshErrer();
                }
            }

            @Override
            public void onError(String code) {

                StateBaseUtils.error(iSendRedPacketRecordView, state, code);
                iSendRedPacketRecordView.setRefreshErrer();
            }
        }, state, iSendRedPacketRecordView.getSendRedPacketRecordPageIndex() + "", iSendRedPacketRecordView.getSendRedPacketRecordPageSize() + "");
    }

    /**
     * 加载更多
     */
    public void getSendRedPacketRecordLoadModule() {

        sendRedPacketRecordModule.requestServerDataOne(new OnBaseDataListener<SendRedPacketRecordBean>() {
            @Override
            public void onNewData(SendRedPacketRecordBean data) {
                iSendRedPacketRecordView.setSendRedPacketRecordMoreData(data);
            }

            @Override
            public void onError(String code) {
                iSendRedPacketRecordView.setSendRedPacketRecordMoreErrer(code);
            }
        }, state, iSendRedPacketRecordView.getSendRedPacketRecordPageIndex() + "", iSendRedPacketRecordView.getSendRedPacketRecordPageSize() + "");
    }

}
