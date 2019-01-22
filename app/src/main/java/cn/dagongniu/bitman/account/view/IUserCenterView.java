package cn.dagongniu.bitman.account.view;

import cn.dagongniu.bitman.account.bean.UserCenterBean;
import cn.dagongniu.bitman.base.IView;

public interface IUserCenterView extends IView {

    void setUserCenterData(UserCenterBean userCenterData);//成功

    void setUserCenterFailure(String msg);//失败


}
