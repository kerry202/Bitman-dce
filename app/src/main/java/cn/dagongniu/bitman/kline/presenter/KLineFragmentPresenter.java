package cn.dagongniu.bitman.kline.presenter;

import android.app.Activity;

import com.socks.library.KLog;

import java.util.HashMap;
import java.util.List;

import cn.dagongniu.bitman.base.OAXBasePresenter;
import cn.dagongniu.bitman.base.OAXIViewList;
import cn.dagongniu.bitman.https.CommonJsonToList;
import cn.dagongniu.bitman.https.OAXStateBaseUtils;
import cn.dagongniu.bitman.https.OnDataListListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.kline.bean.KlineInfoBean;
import cn.dagongniu.bitman.kline.module.KLineFragmentModule;

public class KLineFragmentPresenter extends OAXBasePresenter {

    private KLineFragmentModule mModule;
    private OAXIViewList mView;
    private Activity mActivity;
    private RequestState mState;

    public KLineFragmentPresenter(OAXIViewList iView, RequestState state) {
        super(iView);
        this.mState = state;
        mActivity = (Activity) iView.getContext();
        mView = iView;
        mModule = new KLineFragmentModule(mActivity);
    }

    public void getData(HashMap<String, Object> params, RequestState state) {
        this.mState = state;
        mModule.requestServerDataList(new OnDataListListener<KlineInfoBean>() {
            @Override
            public void onNewData(CommonJsonToList<KlineInfoBean> data) {
                List<KlineInfoBean> beans = data.getData();
                KLog.d("onNewData size()== " + beans.size());
                if (beans.size() == 0) {
                    OAXStateBaseUtils.isNull(mView, mState, data.getMsg());
                } else {
                    OAXStateBaseUtils.successList(mView, mState, data);
                }
            }

            @Override
            public void onError(String code) {
                OAXStateBaseUtils.error(mView, state, code);
            }
        }, state, params);
    }
}
