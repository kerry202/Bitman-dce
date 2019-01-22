package cn.dagongniu.bitman.assets.view;

import cn.dagongniu.bitman.assets.bean.PropertyShowBean;
import cn.dagongniu.bitman.base.IView;

public interface IPropertyShowView extends IView {


    void setRefreshPropertyShowData(PropertyShowBean propertyShowBean);

    String getCoinId();

    void goLogin(String msg);

}
