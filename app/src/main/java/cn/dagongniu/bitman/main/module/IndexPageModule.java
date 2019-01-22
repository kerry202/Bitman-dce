package cn.dagongniu.bitman.main.module;

import android.app.Activity;

import com.google.gson.Gson;
import com.socks.library.KLog;

import cn.dagongniu.bitman.base.BaseModule;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.HttpUtils;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.main.bean.IndexPageBean;

/**
 * 首页 module
 */
public class IndexPageModule extends BaseModule<String, IndexPageBean> {

    public IndexPageModule(Activity activity) {
        super(activity);
    }

    @Override
    public void requestServerDataOne(OnBaseDataListener<IndexPageBean> onBaseDataListener, String... parm) {

    }

    @Override
    public void requestServerDataOne(final OnBaseDataListener<IndexPageBean> onBaseDataListener, RequestState state, String... parm) {

        HttpUtils.getInstance().postHeadersId(Http.main_indexPage, activity, new OnBaseDataListener<String>() {
            @Override
            public void onNewData(String data) {
                KLog.d("main_indexPage = " + data);
                try {
                    IndexPageBean indexPageBean = new Gson().fromJson(data, IndexPageBean.class);
                    onBaseDataListener.onNewData(indexPageBean);
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
