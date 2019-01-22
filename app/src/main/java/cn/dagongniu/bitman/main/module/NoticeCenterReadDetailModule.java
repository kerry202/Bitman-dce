package cn.dagongniu.bitman.main.module;

import android.app.Activity;

import com.google.gson.Gson;

import java.util.HashMap;

import cn.dagongniu.bitman.base.BaseModule;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.HttpUtils;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.main.bean.NoticeCenterReadDetailBean;
import cn.dagongniu.bitman.utils.DebugUtils;

/**
 * 公告查看详情 module
 */
public class NoticeCenterReadDetailModule extends BaseModule<String, NoticeCenterReadDetailBean> {

    public NoticeCenterReadDetailModule(Activity activity) {
        super(activity);
    }

    @Override
    public void requestServerDataOne(OnBaseDataListener<NoticeCenterReadDetailBean> onBaseDataListener, String... parm) {

    }

    @Override
    public void requestServerDataOne(final OnBaseDataListener<NoticeCenterReadDetailBean> onBaseDataListener, RequestState state, String... parm) {

        HashMap<String, String> hashMap = new HashMap<>();
        String id = parm[0];
        hashMap.put("id", id);              //公告的id

        HttpUtils.getInstance().postLang(Http.noticeCenter_readDetail, hashMap, activity, new OnBaseDataListener<String>() {
            @Override
            public void onNewData(String data) {
                DebugUtils.prinlnLog(data);
                try {
                    NoticeCenterReadDetailBean httpBaseBean = new Gson().fromJson(data, NoticeCenterReadDetailBean.class);
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
