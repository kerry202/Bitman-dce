package cn.dagongniu.bitman.account.view;

import cn.dagongniu.bitman.base.IView;

public interface ICheckChangePasswordView extends IView {

    void isSuccess();

    void setShowCheckPasswordErrer(String msg);

    String getPassword();

    String getUsername();


}
