package cn.dagongniu.bitman.captcha;

import cn.dagongniu.bitman.base.IView;

public interface ICaptchaView extends IView {

    String getValidate(); //验证码组件提交上来的NECaptchaValidate

    void setfailure(String data);

    void setSuccess(String data);

}
