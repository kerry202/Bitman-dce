package cn.dagongniu.bitman.main.presenter;

import android.app.Activity;

import cn.dagongniu.bitman.base.BasePresenter;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.https.StateBaseUtils;
import cn.dagongniu.bitman.main.bean.NoticeCenterBean;
import cn.dagongniu.bitman.main.module.NoticeCenterModule;
import cn.dagongniu.bitman.main.view.NoticeCenterView;

/**
 * 首页公告 Presenter
 */
public class NoticeCenterPresenter extends BasePresenter {

    private NoticeCenterModule noticeCenterModule;
    private NoticeCenterView noticeCenterView;
    private Activity activity;
    RequestState state;

    public NoticeCenterPresenter(NoticeCenterView noticeCenterView, RequestState state) {
        super(noticeCenterView);
        this.state = state;
        activity = (Activity) noticeCenterView.getContext();
        this.noticeCenterView = noticeCenterView;
        noticeCenterModule = new NoticeCenterModule(activity);
    }

    public void getNoticeCenterModule() {

        noticeCenterModule.requestServerDataOne(new OnBaseDataListener<NoticeCenterBean>() {

            @Override
            public void onNewData(NoticeCenterBean data) {
                if (data.isSuccess()) {
                    //响应请求数据回去
                    StateBaseUtils.success(noticeCenterView, state, data);
                    noticeCenterView.setNoticeCenterData(data);
                } else {
                    StateBaseUtils.failure(noticeCenterView, state, data.getMsg());
                }
            }

            @Override
            public void onError(String code) {
                StateBaseUtils.error(noticeCenterView, state, code);
            }
        }, state);
    }

}
