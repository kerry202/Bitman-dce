package cn.dagongniu.bitman.main.view;

import cn.dagongniu.bitman.base.IView;
import cn.dagongniu.bitman.main.bean.IndexPageBean;

public interface IndexPageView extends IView {

    void setIndexPageData(IndexPageBean indexPageData);

    void setRefreshIndexPageData(IndexPageBean indexPageData);

    void setSilentRefreshIndexPageData(IndexPageBean indexPageData);

    void setRefreshIndexPageDataErrer(String indexPageData);

    void refreshErrer();

}
