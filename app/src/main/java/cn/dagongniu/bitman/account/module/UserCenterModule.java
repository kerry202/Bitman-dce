package cn.dagongniu.bitman.account.module;

import android.app.Activity;

import com.google.gson.Gson;

import cn.dagongniu.bitman.account.bean.UserCenterBean;
import cn.dagongniu.bitman.base.BaseModule;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.HttpUtils;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.utils.DebugUtils;

/**
 * 个人中心
 */
public class UserCenterModule extends BaseModule<String, UserCenterBean> {

    public UserCenterModule(Activity activity) {
        super(activity);
    }

    @Override
    public void requestServerDataOne(OnBaseDataListener<UserCenterBean> onBaseDataListener, String... parm) {

    }

    @Override
    public void requestServerDataOne(final OnBaseDataListener<UserCenterBean> onBaseDataListener, RequestState state, String... parm) {

        HttpUtils.getInstance().getLangIdToKenMine(Http.USER_USERCENTER, activity, new OnBaseDataListener<String>() {
            @Override
            public void onNewData(String data) {
                DebugUtils.prinlnLog(data);
                try {
                    UserCenterBean userCenterBean = new Gson().fromJson(data, UserCenterBean.class);
                    onBaseDataListener.onNewData(userCenterBean);
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
