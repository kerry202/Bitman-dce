package cn.dagongniu.bitman.account.module;

import android.app.Activity;

import com.google.gson.Gson;

import cn.dagongniu.bitman.account.bean.MyInvateBean;
import cn.dagongniu.bitman.base.BaseModule;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.HttpUtils;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.utils.DebugUtils;

/**
 * 我的邀请 module
 */
public class MyInvateModule extends BaseModule<String, MyInvateBean> {

    public MyInvateModule(Activity activity) {
        super(activity);
    }

    @Override
    public void requestServerDataOne(OnBaseDataListener<MyInvateBean> onBaseDataListener, String... parm) {

    }

    @Override
    public void requestServerDataOne(final OnBaseDataListener<MyInvateBean> onBaseDataListener, RequestState state, String... parm) {

        HttpUtils.getInstance().getLangIdToKen(Http.USER_MUINVATE, activity, new OnBaseDataListener<String>() {
            @Override
            public void onNewData(String data) {
                DebugUtils.prinlnLog(data);
                try {
                    MyInvateBean myInvateBean = new Gson().fromJson(data, MyInvateBean.class);
                    onBaseDataListener.onNewData(myInvateBean);
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
