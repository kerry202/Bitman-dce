package cn.dagongniu.bitman.account.view;

import cn.dagongniu.bitman.base.IView;

public interface ICheckLoginPasswordView extends IView {


    void isSuccess();

    void setShowCheckPasswordErrer(String msg);

    String getPassword();

    String getUsername();

    String getChooseCountries();//区号

    boolean isEmailPhone();//邮箱还是手机

}
