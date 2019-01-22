package cn.dagongniu.bitman.assets.view;

import cn.dagongniu.bitman.assets.bean.UserCoinWithdrawalBean;
import cn.dagongniu.bitman.base.IView;

public interface IUserCoinWithdrawalView extends IView {


    void setUserCoinWithdrawalData(UserCoinWithdrawalBean userCoinTopBean);

    String getCoinId();

    void goLogin(String msg);

}
