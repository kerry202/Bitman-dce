package cn.dagongniu.bitman.main.presenter;

import android.app.Activity;

import cn.dagongniu.bitman.base.BasePresenter;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.https.StateBaseUtils;
import cn.dagongniu.bitman.main.bean.IndexPageBean;
import cn.dagongniu.bitman.main.module.IndexPageModule;
import cn.dagongniu.bitman.main.view.IndexPageView;
import cn.dagongniu.bitman.utils.Logs;

/**
 * 首页banner加载 Presenter
 */
public class IndexPagePresenter extends BasePresenter {

    private IndexPageModule indexPageModule;
    private IndexPageView indexPageView;
    private Activity activity;
    RequestState state;

    public IndexPagePresenter(IndexPageView indexPageView, RequestState state) {
        super(indexPageView);
        this.state = state;
        activity = (Activity) indexPageView.getContext();
        this.indexPageView = indexPageView;
        indexPageModule = new IndexPageModule(activity);
    }

    public void getIndexPageModule() {

        indexPageModule.requestServerDataOne(new OnBaseDataListener<IndexPageBean>() {

            @Override
            public void onNewData(IndexPageBean data) {
                if (data.isSuccess()) {
                    //响应请求数据回去
                    StateBaseUtils.success(indexPageView, state, data);
                    indexPageView.setIndexPageData(data);
                } else {
                    StateBaseUtils.failure(indexPageView, state, data.getMsg());
                    indexPageView.refreshErrer();
                }
            }

            @Override
            public void onError(String code) {
                StateBaseUtils.error(indexPageView, state, code);
                indexPageView.refreshErrer();
            }
        }, state);
    }


    public void getIndexPageRefreshModule() {

        indexPageModule.requestServerDataOne(new OnBaseDataListener<IndexPageBean>() {

            @Override
            public void onNewData(IndexPageBean data) {
                indexPageView.setRefreshIndexPageData(data);
            }

            @Override
            public void onError(String code) {
                indexPageView.setRefreshIndexPageDataErrer(code);
            }
        }, state);
    }

    /**
     * 静默刷新
     */
    public void getIndexPageSilentRefreshModule() {

        indexPageModule.requestServerDataOne(new OnBaseDataListener<IndexPageBean>() {

            @Override
            public void onNewData(IndexPageBean data) {
                Logs.s("    mainindexpager onNewData  "+data);
                indexPageView.setSilentRefreshIndexPageData(data);
            }

            @Override
            public void onError(String code) {
                Logs.s("    mainindexpager onError  ");
                indexPageView.setRefreshIndexPageDataErrer(code);
            }
        }, state);
    }

}
