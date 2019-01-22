package cn.dagongniu.bitman.account.view;


import cn.dagongniu.bitman.account.bean.MyInvateBean;
import cn.dagongniu.bitman.base.IView;

public interface IMyInvateView extends IView {

    void goLogin(String msg);

    void setMyInvateData(MyInvateBean myInvateBean);

}
