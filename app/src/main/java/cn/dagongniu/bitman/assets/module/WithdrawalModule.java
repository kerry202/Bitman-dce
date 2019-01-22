package cn.dagongniu.bitman.assets.module;

import android.app.Activity;

import com.google.gson.Gson;

import java.util.HashMap;

import cn.dagongniu.bitman.assets.bean.WithdrawalBean;
import cn.dagongniu.bitman.base.BaseModule;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.HttpUtils;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.utils.DebugUtils;

/**
 * 提现 module
 */
public class WithdrawalModule extends BaseModule<String, WithdrawalBean> {

    public WithdrawalModule(Activity activity) {
        super(activity);
    }

    @Override
    public void requestServerDataOne(OnBaseDataListener<WithdrawalBean> onBaseDataListener, String... parm) {
    }

    @Override
    public void requestServerDataOne(final OnBaseDataListener<WithdrawalBean> onBaseDataListener, RequestState state, String... parm) {

        HashMap<String, String> hashMap = new HashMap<>();
        String address = parm[0];
        String remark = parm[1];
        String qty = parm[2];
        String coinId = parm[3];
        hashMap.put("address", address);                //提币地址
        hashMap.put("remark", remark);                  //备注
        hashMap.put("qty", qty);                        //数量
        hashMap.put("coinId", coinId);                  //id
        HttpUtils.getInstance().postLangIdToken(Http.USERCOIN_WITHDRAWAL, hashMap, activity, new OnBaseDataListener<String>() {
            @Override
            public void onNewData(String data) {
                DebugUtils.prinlnLog(data);
                try {
                    WithdrawalBean withdrawalBean = new Gson().fromJson(data, WithdrawalBean.class);
                    onBaseDataListener.onNewData(withdrawalBean);
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
