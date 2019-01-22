package cn.dagongniu.bitman.main.module;

import android.app.Activity;

import com.google.gson.Gson;

import cn.dagongniu.bitman.base.BaseModule;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.HttpUtils;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.main.bean.BannerBean;
import cn.dagongniu.bitman.utils.AppConstants;
import cn.dagongniu.bitman.utils.DebugUtils;

/**
 * 首页banner加载 module
 */
public class BannerModule extends BaseModule<String, BannerBean> {

    public BannerModule(Activity activity) {
        super(activity);
    }

    @Override
    public void requestServerDataOne(OnBaseDataListener<BannerBean> onBaseDataListener, String... parm) {

    }

    @Override
    public void requestServerDataOne(final OnBaseDataListener<BannerBean> onBaseDataListener, RequestState state, String... parm) {

        HttpUtils.getInstance().getLang(Http.main_banner + "/" + AppConstants.SOURCE_PC_APP, activity, new OnBaseDataListener<String>() {
            @Override
            public void onNewData(String data) {
                DebugUtils.prinlnLog(data);
                try {
                    BannerBean bannerBean = new Gson().fromJson(data, BannerBean.class);
                    onBaseDataListener.onNewData(bannerBean);
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
