package cn.dagongniu.bitman.account.view;

import cn.dagongniu.bitman.base.IView;

public interface ISwitchCheckView extends IView {

    String getEmailCode(); //emai验证码

    String getSmsCode(); //短信验证码

    String getGoogleCode(); //谷歌验证码

    int getCheckType();//1 手机 2邮箱 3google

    int getStatus();//0 关闭 1开启

    void successful();//成功

    void failure(String msg);//失败
}
