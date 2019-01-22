package cn.dagongniu.bitman.assets.view;

import cn.dagongniu.bitman.assets.bean.CoinListBean;
import cn.dagongniu.bitman.base.IView;

public interface ICoinListView extends IView {


    void setCoinListBeanData(CoinListBean coinListBean);

    void refreshErrer();

}
