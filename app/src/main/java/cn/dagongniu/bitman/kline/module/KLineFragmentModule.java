package cn.dagongniu.bitman.kline.module;

import android.app.Activity;

import java.util.HashMap;

import cn.dagongniu.bitman.base.OAXBaseModule;
import cn.dagongniu.bitman.https.CommonJsonToList;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.HttpUtils;
import cn.dagongniu.bitman.https.OnDataListListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.kline.bean.KlineInfoBean;

public class KLineFragmentModule extends OAXBaseModule<String, KlineInfoBean> {
    public KLineFragmentModule(Activity activity) {
        super(activity);
    }

    @Override
    public void requestServerDataList(OnDataListListener<KlineInfoBean> dataListListener, RequestState state, HashMap<String, Object> params) {
        HttpUtils.getInstance().commonPostList(Http.KLINE_FINDLISTBYMARKETID, params, activity, new OnDataListListener<KlineInfoBean>() {
            @Override
            public void onNewData(CommonJsonToList<KlineInfoBean> data) {
                dataListListener.onNewData(data);
            }

            @Override
            public void onError(String msg) {
                dataListListener.onError(msg);
            }
        }, KlineInfoBean.class, state);
    }
}
