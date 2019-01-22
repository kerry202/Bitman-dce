package cn.dagongniu.bitman.account.module;

import android.app.Activity;

import com.google.gson.Gson;

import java.util.HashMap;

import cn.dagongniu.bitman.base.BaseModule;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.HttpBaseBean;
import cn.dagongniu.bitman.https.HttpUtils;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.utils.DebugUtils;
import cn.dagongniu.bitman.utils.Logs;

/**
 * 效验邮箱验证码 module
 */
public class CheckEmailSmsModule extends BaseModule<String, HttpBaseBean> {

    public CheckEmailSmsModule(Activity activity) {
        super(activity);
    }

    @Override
    public void requestServerDataOne(OnBaseDataListener<HttpBaseBean> onBaseDataListener, String... parm) {

    }

    @Override
    public void requestServerDataOne(final OnBaseDataListener<HttpBaseBean> onBaseDataListener, RequestState state, String... parm) {

        HashMap<String, String> hashMap = new HashMap<>();
        String email = parm[0];
        String code = parm[1];

        hashMap.put("email", email);                    //邮箱
        hashMap.put("code", code);                      //发短信 类型 暂定 1 注册 2登陆.......
        Logs.s("     邮箱注册email     "+email);
        Logs.s("     邮箱注册code     "+code);
        HttpUtils.getInstance().postLang(Http.user_checkEmailCode, hashMap, activity, new OnBaseDataListener<String>() {
            @Override
            public void onNewData(String data) {
                DebugUtils.prinlnLog(data);
                try {
                    HttpBaseBean httpBaseBean = new Gson().fromJson(data, HttpBaseBean.class);
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
