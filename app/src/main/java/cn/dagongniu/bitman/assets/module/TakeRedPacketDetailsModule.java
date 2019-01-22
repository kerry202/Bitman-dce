package cn.dagongniu.bitman.assets.module;

import android.app.Activity;

import com.google.gson.Gson;

import java.util.HashMap;

import cn.dagongniu.bitman.assets.bean.TakeRedPacketDetailsBean;
import cn.dagongniu.bitman.base.BaseModule;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.HttpUtils;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.utils.DebugUtils;

/**
 * 发送红包详情 module
 */
public class TakeRedPacketDetailsModule extends BaseModule<String, TakeRedPacketDetailsBean> {

    public TakeRedPacketDetailsModule(Activity activity) {
        super(activity);
    }

    @Override
    public void requestServerDataOne(OnBaseDataListener<TakeRedPacketDetailsBean> onBaseDataListener, String... parm) {
    }

    @Override
    public void requestServerDataOne(final OnBaseDataListener<TakeRedPacketDetailsBean> onBaseDataListener, RequestState state, String... parm) {

        HashMap<String, String> hashMap = new HashMap<>();
        String pageNo = parm[0];
        String pageSize = parm[1];
        String id = parm[2];

        hashMap.put("pageNo", pageNo);
        hashMap.put("pageSize", pageSize);
        hashMap.put("id", id); //红包id

        HttpUtils.getInstance().postLangIdToken(Http.RED_takeRedPacketDetails, hashMap, activity, new OnBaseDataListener<String>() {
            @Override
            public void onNewData(String data) {
                DebugUtils.prinlnLog(data);
                try {
                    TakeRedPacketDetailsBean takeRedPacketDetailsBean = new Gson().fromJson(data, TakeRedPacketDetailsBean.class);
                    onBaseDataListener.onNewData(takeRedPacketDetailsBean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String code) {
                onBaseDataListener.onError(code);
            }
        }, state);


    }
}
