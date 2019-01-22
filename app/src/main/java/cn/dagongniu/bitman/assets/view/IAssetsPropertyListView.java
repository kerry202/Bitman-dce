package cn.dagongniu.bitman.assets.view;

import cn.dagongniu.bitman.assets.bean.AssetsPropertyListBean;
import cn.dagongniu.bitman.base.IView;

public interface IAssetsPropertyListView extends IView {

    String getCoinName();//币种名称

    void setIAssetsPropertyData(AssetsPropertyListBean noticeCenterReadDetailBean);//回调数据

    void setIAssetsPropertyDataNull(AssetsPropertyListBean noticeCenterReadDetailBean);//回调数据

    void refreshErrer();

    void goLogin(String msg);

    int getType();//类型 1 显示所有币种资产 2显示有余额




}
