package cn.dagongniu.bitman.main.view;

import cn.dagongniu.bitman.base.IView;
import cn.dagongniu.bitman.main.bean.NoticeCenterMoreBean;

public interface INoticeCenterMorelView extends IView {

    String getNoticeCenterMoreType();//公告类型 4新币上线 5最新公告

    int getNoticeCenterPageIndex();//页码

    int getNoticeCenterPageSize();//	每页显示条数

    void setNoticeCenterMoreData(NoticeCenterMoreBean noticeCenterMoreData);

    void setRefreshNoticeCenterMoreData(NoticeCenterMoreBean noticeCenterMoreData);

    void setRefreshNoticeCenterMoreErrer(String noticeCenterMoreData);

    void setRefreshNoticeCenterLoadMoreData(NoticeCenterMoreBean noticeCenterMoreData);

    void setRefreshNoticeCenterLoadMoreErrer(String noticeCenterMoreData);

}
