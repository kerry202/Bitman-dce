package cn.dagongniu.bitman.account.view;

import cn.dagongniu.bitman.base.IView;

public interface IUserLoginLoginPwView extends IView {

    void UpdateSuccess();//修改成功

    String getOldPassword();

    String getNewPassword();

    String getRepeatPassword();

    String getGoogleCode();

    String getSmsCode();

    String getEmailCode();

}
