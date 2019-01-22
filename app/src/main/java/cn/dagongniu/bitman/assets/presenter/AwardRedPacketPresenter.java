package cn.dagongniu.bitman.assets.presenter;

import android.app.Activity;

import cn.dagongniu.bitman.assets.bean.AwardRedPacketBean;
import cn.dagongniu.bitman.assets.module.AwardRedPacketModule;
import cn.dagongniu.bitman.assets.view.IAwardRedPacketView;
import cn.dagongniu.bitman.base.BasePresenter;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.https.StateBaseUtils;

/**
 * 发送红包 Presenter
 */
public class AwardRedPacketPresenter extends BasePresenter {

    private AwardRedPacketModule awardRedPacketModule;
    private IAwardRedPacketView iAwardRedPacketView;
    private Activity activity;
    RequestState state;

    public AwardRedPacketPresenter(IAwardRedPacketView iAwardRedPacketView, RequestState state) {
        super(iAwardRedPacketView);
        this.state = state;
        activity = (Activity) iAwardRedPacketView.getContext();
        this.iAwardRedPacketView = iAwardRedPacketView;
        awardRedPacketModule = new AwardRedPacketModule(activity);
    }

    public void getAwardRedPacketModule(int type) {

        awardRedPacketModule.requestServerDataOne(new OnBaseDataListener<AwardRedPacketBean>() {

            @Override
            public void onNewData(AwardRedPacketBean data) {
                if (data.isSuccess()) {
                    //响应请求数据回去
                    iAwardRedPacketView.setAwardRedPacketData(data, type);

                } else {
                    StateBaseUtils.failure(iAwardRedPacketView, state, data.getMsg());
                }
            }

            @Override
            public void onError(String code) {

                StateBaseUtils.error(iAwardRedPacketView, state, code);
            }
        }, state, iAwardRedPacketView.getCoinId(), iAwardRedPacketView.getType(), iAwardRedPacketView.getTotalCoinQty(), iAwardRedPacketView.getTotalPacketQty(), iAwardRedPacketView.getWishWords());
    }


}
