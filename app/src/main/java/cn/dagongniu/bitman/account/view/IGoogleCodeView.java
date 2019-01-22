package cn.dagongniu.bitman.account.view;

import cn.dagongniu.bitman.account.bean.GoogleCodeBean;
import cn.dagongniu.bitman.base.IView;

public interface IGoogleCodeView extends IView {

    void setGoogleCodeData(GoogleCodeBean googleCodeData);

    void bindSuccess();

    String getGoogleKey();

    String getGoogleCode();
}
