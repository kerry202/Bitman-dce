package cn.dagongniu.bitman.assets.module;

import android.app.Activity;

import com.google.gson.Gson;

import java.util.HashMap;

import cn.dagongniu.bitman.assets.bean.AssetsPropertyListBean;
import cn.dagongniu.bitman.base.BaseModule;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.HttpUtils;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.utils.DebugUtils;

/**
 * 公告查看详情 module
 */
public class AssetsPropertyListModule extends BaseModule<String, AssetsPropertyListBean> {

    public AssetsPropertyListModule(Activity activity) {
        super(activity);
    }

    @Override
    public void requestServerDataOne(OnBaseDataListener<AssetsPropertyListBean> onBaseDataListener, String... parm) {
    }

    @Override
    public void requestServerDataOne(final OnBaseDataListener<AssetsPropertyListBean> onBaseDataListener, RequestState state, String... parm) {

        HashMap<String, String> hashMap = new HashMap<>();
        String coinName = parm[0];
        String type = parm[1];
        hashMap.put("coinName", coinName);              //币种名称
        hashMap.put("type", type);                      //类型 1 显示所有币种资产 2显示有余额

        HttpUtils.getInstance().postLangIdToken(Http.ASSETS_PROPERTYLIST, hashMap, activity, new OnBaseDataListener<String>() {
            @Override
            public void onNewData(String data) {
                DebugUtils.prinlnLog(data);
                try {
                    AssetsPropertyListBean httpBaseBean = new Gson().fromJson(data, AssetsPropertyListBean.class);
                    onBaseDataListener.onNewData(httpBaseBean);
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
