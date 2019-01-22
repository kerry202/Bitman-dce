package cn.dagongniu.bitman.account.module;

import android.app.Activity;

import com.google.gson.Gson;

import cn.dagongniu.bitman.account.bean.CountryCodeBean;
import cn.dagongniu.bitman.base.BaseModule;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.HttpUtils;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.utils.DebugUtils;

/**
 * 国家区号 module
 */
public class CountryCodeModule extends BaseModule<String, CountryCodeBean> {

    public CountryCodeModule(Activity activity) {
        super(activity);
    }

    @Override
    public void requestServerDataOne(OnBaseDataListener<CountryCodeBean> onBaseDataListener, String... parm) {
    }

    @Override
    public void requestServerDataOne(final OnBaseDataListener<CountryCodeBean> onBaseDataListener, RequestState state, String... parm) {

        HttpUtils.getInstance().postLang(Http.user_countryCode, activity, new OnBaseDataListener<String>() {
            @Override
            public void onNewData(String data) {
                DebugUtils.prinlnLog(data);
                try {
                    CountryCodeBean httpBaseBean = new Gson().fromJson(data, CountryCodeBean.class);
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
