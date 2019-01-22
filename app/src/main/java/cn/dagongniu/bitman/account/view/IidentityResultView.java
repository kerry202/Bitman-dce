package cn.dagongniu.bitman.account.view;

import cn.dagongniu.bitman.account.bean.IdentityResultBean;
import cn.dagongniu.bitman.base.IView;

public interface IidentityResultView extends IView {

    void IidentityResultsuccessful(IdentityResultBean httpBaseBean);//成功

    void IidentityResultfailure(String msg);//失败

}
