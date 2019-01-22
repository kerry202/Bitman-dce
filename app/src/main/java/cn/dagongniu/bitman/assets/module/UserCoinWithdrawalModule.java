package cn.dagongniu.bitman.assets.module;

import android.app.Activity;

import com.google.gson.Gson;

import java.util.HashMap;

import cn.dagongniu.bitman.assets.bean.UserCoinWithdrawalBean;
import cn.dagongniu.bitman.base.BaseModule;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.HttpUtils;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.utils.DebugUtils;

/**
 * 提现 module
 */
public class UserCoinWithdrawalModule extends BaseModule<String, UserCoinWithdrawalBean> {

    public UserCoinWithdrawalModule(Activity activity) {
        super(activity);
    }

    @Override
    public void requestServerDataOne(OnBaseDataListener<UserCoinWithdrawalBean> onBaseDataListener, String... parm) {
    }

    @Override
    public void requestServerDataOne(final OnBaseDataListener<UserCoinWithdrawalBean> onBaseDataListener, RequestState state, String... parm) {

        HashMap<String, String> hashMap = new HashMap<>();
        String coinId = parm[0];
        hashMap.put("coinId", coinId);                  //币种id

        HttpUtils.getInstance().getLangIdToKen(Http.USERCOIN_QUERYCOININFO + "/" + coinId, activity, new OnBaseDataListener<String>() {
            @Override
            public void onNewData(String data) {
                DebugUtils.prinlnLog(data);
                try {
                    UserCoinWithdrawalBean userCoinWithdrawalBean = new Gson().fromJson(data, UserCoinWithdrawalBean.class);
                    onBaseDataListener.onNewData(userCoinWithdrawalBean);
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
