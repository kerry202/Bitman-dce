package cn.dagongniu.bitman.main.view;

import cn.dagongniu.bitman.base.IView;
import cn.dagongniu.bitman.main.bean.NoticeCenterReadDetailBean;

public interface INoticeCenterReadDetailView extends IView {

    String getReadDetail();//公告ID

    void setNoticeCenterReadDetailData(NoticeCenterReadDetailBean noticeCenterReadDetailData);

}
