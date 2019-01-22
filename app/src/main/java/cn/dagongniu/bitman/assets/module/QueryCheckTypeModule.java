package cn.dagongniu.bitman.assets.module;

import android.app.Activity;

import com.google.gson.Gson;

import java.util.HashMap;

import cn.dagongniu.bitman.assets.bean.QueryCheckTypeBean;
import cn.dagongniu.bitman.base.BaseModule;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.HttpUtils;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.utils.DebugUtils;
import cn.dagongniu.bitman.utils.Logs;

/**
 * 查询需要的验证类型 module
 */
public class QueryCheckTypeModule extends BaseModule<String, QueryCheckTypeBean> {

    public QueryCheckTypeModule(Activity activity) {
        super(activity);
    }

    @Override
    public void requestServerDataOne(OnBaseDataListener<QueryCheckTypeBean> onBaseDataListener, String... parm) {
    }

    @Override
    public void requestServerDataOne(final OnBaseDataListener<QueryCheckTypeBean> onBaseDataListener, RequestState state, String... parm) {

        HashMap<String, String> hashMap = new HashMap<>();
        String username = parm[0];
        hashMap.put("username", username);    //账号
        Logs.s("    usernameusername   "+username);
        HttpUtils.getInstance().postLang(Http.USER_QUERYCHECKTYPE, hashMap, activity, new OnBaseDataListener<String>() {
            @Override
            public void onNewData(String data) {
                Logs.s("       提现dialog        "+data);
                DebugUtils.prinlnLog(data);
                try {
                    QueryCheckTypeBean queryCheckTypeBean = new Gson().fromJson(data, QueryCheckTypeBean.class);
                    onBaseDataListener.onNewData(queryCheckTypeBean);
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
