package cn.dagongniu.bitman.assets.view;

import cn.dagongniu.bitman.assets.bean.QueryCheckTypeBean;
import cn.dagongniu.bitman.base.IView;

public interface IQueryCheckTypeView extends IView {

    String getUsername();//账号


    void setQueryCheckTypeData(QueryCheckTypeBean queryCheckTypeData);//回调数据


}
