package cn.dagongniu.bitman.account.module;

import android.app.Activity;

import com.google.gson.Gson;

import cn.dagongniu.bitman.account.bean.GoogleCodeBean;
import cn.dagongniu.bitman.base.BaseModule;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.HttpUtils;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.utils.DebugUtils;

/**
 * google验证查询 module
 */
public class GoogleCodeModule extends BaseModule<String, GoogleCodeBean> {

    public GoogleCodeModule(Activity activity) {
        super(activity);
    }

    @Override
    public void requestServerDataOne(OnBaseDataListener<GoogleCodeBean> onBaseDataListener, String... parm) {

    }

    @Override
    public void requestServerDataOne(final OnBaseDataListener<GoogleCodeBean> onBaseDataListener, RequestState state, String... parm) {


        HttpUtils.getInstance().getLangIdToKen(Http.USER_GETGOOGLEQRBARCODEURL, activity, new OnBaseDataListener<String>() {
            @Override
            public void onNewData(String data) {
                DebugUtils.prinlnLog(data);
                try {
                    GoogleCodeBean googleCodeBean = new Gson().fromJson(data, GoogleCodeBean.class);
                    onBaseDataListener.onNewData(googleCodeBean);
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
