package cn.dagongniu.bitman.main.module;

import android.app.Activity;

import cn.dagongniu.bitman.base.BaseModule;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.main.bean.TestBean;

/**
 * 首页
 */

public class HomeModule extends BaseModule<String, TestBean> {

    public HomeModule(Activity activity) {
        super(activity);
    }

    @Override
    public void requestServerDataOne(OnBaseDataListener<TestBean> onBaseDataListener, String... parm) {

    }

    @Override
    public void requestServerDataOne(final OnBaseDataListener<TestBean> onBaseDataListener, RequestState state, String... parm) {
//        OkHttpUtils.getInstance().get(Http.test, activity, new OnBaseDataListener<String>() {
//            @Override
//            public void onNewData(String data) {
//                DebugUtils.prinlnLog(data);
//                try {
//                    TestBean piazzaBean = new Gson().fromJson(data, TestBean.class);
//                    onBaseDataListener.onNewData(piazzaBean);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onError(String code) {
//                onBaseDataListener.onError(code);
//            }
//        },state);
    }




}
