package cn.dagongniu.bitman.account.module;

import android.app.Activity;

import com.google.gson.Gson;

import java.util.HashMap;

import cn.dagongniu.bitman.account.bean.LoginBean;
import cn.dagongniu.bitman.base.BaseModule;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.HttpUtils;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.utils.AppConstants;
import cn.dagongniu.bitman.utils.DebugUtils;
import cn.dagongniu.bitman.utils.Logs;

/**
 * 登录
 */
public class LoginModule extends BaseModule<String, LoginBean> {

    public LoginModule(Activity activity) {
        super(activity);
    }

    @Override
    public void requestServerDataOne(OnBaseDataListener<LoginBean> onBaseDataListener, String... parm) {

    }

    @Override
    public void requestServerDataOne(final OnBaseDataListener<LoginBean> onBaseDataListener, RequestState state, String... parm) {

        HashMap<String, String> hashMap = new HashMap<>();
        String username = parm[0];
        String password = parm[1];
        String type = parm[2];
        String smsCode = parm[3];
        String googleCode = parm[4];

        hashMap.put("username", username);              //手机号码 或邮箱
        hashMap.put("password", password);              //密码
        hashMap.put("source", AppConstants.SOURCE);     //来源 1 pc 2 ios 3 android
        hashMap.put("type", type);                      //注册类型 1 手机 2邮箱
        hashMap.put("smsCode", smsCode);                //短信验证码
        hashMap.put("googleCode", googleCode);          //谷歌验证码

        Logs.s("    googleCode 登录数据 " + googleCode);
        HttpUtils.getInstance().postLang(Http.user_login, hashMap, activity, new OnBaseDataListener<String>() {
            @Override
            public void onNewData(String data) {
                Logs.s("  登录数据 data " + data);
                DebugUtils.prinlnLog(data);
                try {
                    LoginBean loginBean = new Gson().fromJson(data, LoginBean.class);
                    onBaseDataListener.onNewData(loginBean);
                    Logs.s("  httpBaseBeanhttpBaseBean2    " + loginBean.getMsg());
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
