package cn.dagongniu.bitman.assets.view;

import cn.dagongniu.bitman.assets.bean.PropertyRechargeBean;
import cn.dagongniu.bitman.base.IView;

public interface IPropertyRechargeView extends IView {

    String getCoinName();//币种名称

    int getPropertyRechargePageIndex();//页码

    int getPropertyRechargePageSize();//	每页显示条数

    void setPropertyRechargeData(PropertyRechargeBean propertyRechargeBean);//回调数据

    void setPropertyRechargeDataNull(PropertyRechargeBean propertyRechargeBean);

    void goLogin(String msg);

    void setRefreshPropertyRechargeMoreData(PropertyRechargeBean propertyRechargeBean);

    void setRefreshPropertyRechargeLoadMoreErrer(String noticeCenterMoreData);


    void refreshErrer();



}
