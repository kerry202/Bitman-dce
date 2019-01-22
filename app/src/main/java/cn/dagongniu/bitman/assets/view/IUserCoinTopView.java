package cn.dagongniu.bitman.assets.view;

import cn.dagongniu.bitman.assets.bean.UserCoinTopBean;
import cn.dagongniu.bitman.base.IView;

public interface IUserCoinTopView extends IView {


    void setUserCoinTopData(UserCoinTopBean userCoinTopBean);

    String getCoinId();

    void goLogin(String msg);

}
