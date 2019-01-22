package cn.dagongniu.bitman.account.view;

import cn.dagongniu.bitman.base.IView;

public interface IBindPhoneEmailView extends IView {

    String getChooseCountries();//

    String getPhoneOrEmail(); //手机号码或者邮箱

    String getCode();

    void successful(String msg);//成功
    void failure(String msg);//失败
}
