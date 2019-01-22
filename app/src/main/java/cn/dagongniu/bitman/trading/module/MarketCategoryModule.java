package cn.dagongniu.bitman.trading.module;

import android.app.Activity;

import com.google.gson.Gson;

import cn.dagongniu.bitman.base.BaseModule;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.HttpUtils;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.trading.bean.MarketCategoryBean;
import cn.dagongniu.bitman.utils.DebugUtils;

/**
 * 联动交易对信息查询 module
 */
public class MarketCategoryModule extends BaseModule<String, MarketCategoryBean> {

    public MarketCategoryModule(Activity activity) {
        super(activity);
    }

    @Override
    public void requestServerDataOne(OnBaseDataListener<MarketCategoryBean> onBaseDataListener, String... parm) {
    }

    @Override
    public void requestServerDataOne(final OnBaseDataListener<MarketCategoryBean> onBaseDataListener, RequestState state, String... parm) {

        HttpUtils.getInstance().getLang(Http.MARKET_MARKETCATEGORYLIST, activity, new OnBaseDataListener<String>() {
            @Override
            public void onNewData(String data) {
                DebugUtils.prinlnLog(data);
                try {
                    MarketCategoryBean marketCategoryBean = new Gson().fromJson(data, MarketCategoryBean.class);
                    onBaseDataListener.onNewData(marketCategoryBean);
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
