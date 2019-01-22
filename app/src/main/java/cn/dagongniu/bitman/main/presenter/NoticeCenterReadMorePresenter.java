package cn.dagongniu.bitman.main.presenter;

import android.app.Activity;

import cn.dagongniu.bitman.base.BasePresenter;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.https.StateBaseUtils;
import cn.dagongniu.bitman.main.bean.NoticeCenterMoreBean;
import cn.dagongniu.bitman.main.module.NoticeCenterReadMoreModule;
import cn.dagongniu.bitman.main.view.INoticeCenterMorelView;

/**
 * 公告查看更多 Presenter
 */
public class NoticeCenterReadMorePresenter extends BasePresenter {

    private NoticeCenterReadMoreModule noticeCenterReadDetailModule;
    private INoticeCenterMorelView iNoticeCenterMorelView;
    private Activity activity;
    RequestState state;

    public NoticeCenterReadMorePresenter(INoticeCenterMorelView iNoticeCenterMorelView, RequestState state) {
        super(iNoticeCenterMorelView);
        this.state = state;
        activity = (Activity) iNoticeCenterMorelView.getContext();
        this.iNoticeCenterMorelView = iNoticeCenterMorelView;
        noticeCenterReadDetailModule = new NoticeCenterReadMoreModule(activity);
    }

    public void getNoticeCenterReadMoreModule() {

        noticeCenterReadDetailModule.requestServerDataOne(new OnBaseDataListener<NoticeCenterMoreBean>() {

            @Override
            public void onNewData(NoticeCenterMoreBean data) {
                if (data.isSuccess()) {
                    //响应请求数据回去
                    StateBaseUtils.success(iNoticeCenterMorelView, state, data);
                    iNoticeCenterMorelView.setNoticeCenterMoreData(data);
                } else {
                    StateBaseUtils.failure(iNoticeCenterMorelView, state, data.getMsg());
                }
            }

            @Override
            public void onError(String code) {
                StateBaseUtils.error(iNoticeCenterMorelView, state, code);
            }
        }, state, iNoticeCenterMorelView.getNoticeCenterMoreType(), iNoticeCenterMorelView.getNoticeCenterPageIndex() + "", iNoticeCenterMorelView.getNoticeCenterPageSize() + "");
    }


    public void getNoticeCenterReadMoreRefreshModule() {

        noticeCenterReadDetailModule.requestServerDataOne(new OnBaseDataListener<NoticeCenterMoreBean>() {

            @Override
            public void onNewData(NoticeCenterMoreBean data) {
                iNoticeCenterMorelView.setRefreshNoticeCenterMoreData(data);
            }

            @Override
            public void onError(String code) {
                iNoticeCenterMorelView.setRefreshNoticeCenterMoreErrer(code);
            }
        }, state, iNoticeCenterMorelView.getNoticeCenterMoreType(), iNoticeCenterMorelView.getNoticeCenterPageIndex() + "", iNoticeCenterMorelView.getNoticeCenterPageSize() + "");
    }

    public void getNoticeCenterReadMoreLoadModule() {

        noticeCenterReadDetailModule.requestServerDataOne(new OnBaseDataListener<NoticeCenterMoreBean>() {
            @Override
            public void onNewData(NoticeCenterMoreBean data) {
                iNoticeCenterMorelView.setRefreshNoticeCenterLoadMoreData(data);
            }

            @Override
            public void onError(String code) {
                iNoticeCenterMorelView.setRefreshNoticeCenterLoadMoreErrer(code);
            }
        }, state, iNoticeCenterMorelView.getNoticeCenterMoreType(), iNoticeCenterMorelView.getNoticeCenterPageIndex() + "", iNoticeCenterMorelView.getNoticeCenterPageSize() + "");
    }


}
