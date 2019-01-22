package cn.dagongniu.bitman.main.presenter;

import android.app.Activity;

import cn.dagongniu.bitman.base.BasePresenter;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.https.StateBaseUtils;
import cn.dagongniu.bitman.main.bean.NoticeCenterReadDetailBean;
import cn.dagongniu.bitman.main.module.NoticeCenterReadDetailModule;
import cn.dagongniu.bitman.main.view.INoticeCenterReadDetailView;

/**
 * 公告查看详情 Presenter
 */
public class NoticeCenterReadDetailPresenter extends BasePresenter {

    private NoticeCenterReadDetailModule noticeCenterReadDetailModule;
    private INoticeCenterReadDetailView iNoticeCenterReadDetailView;
    private Activity activity;
    RequestState state;

    public NoticeCenterReadDetailPresenter(INoticeCenterReadDetailView iNoticeCenterReadDetailView, RequestState state) {
        super(iNoticeCenterReadDetailView);
        this.state = state;
        activity = (Activity) iNoticeCenterReadDetailView.getContext();
        this.iNoticeCenterReadDetailView = iNoticeCenterReadDetailView;
        noticeCenterReadDetailModule = new NoticeCenterReadDetailModule(activity);
    }

    public void getNoticeCenterReadDetailModule() {

        noticeCenterReadDetailModule.requestServerDataOne(new OnBaseDataListener<NoticeCenterReadDetailBean>() {

            @Override
            public void onNewData(NoticeCenterReadDetailBean data) {
                if (data.isSuccess()) {
                    //响应请求数据回去
                    StateBaseUtils.success(iNoticeCenterReadDetailView, state, data);
                    iNoticeCenterReadDetailView.setNoticeCenterReadDetailData(data);
                } else {
                    StateBaseUtils.failure(iNoticeCenterReadDetailView, state, data.getMsg());
                }
            }

            @Override
            public void onError(String code) {
                StateBaseUtils.error(iNoticeCenterReadDetailView, state, code);
            }
        }, state,iNoticeCenterReadDetailView.getReadDetail());
    }

}
