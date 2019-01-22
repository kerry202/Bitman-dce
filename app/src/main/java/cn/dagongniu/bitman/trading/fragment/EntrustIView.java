package cn.dagongniu.bitman.trading.fragment;

import cn.dagongniu.bitman.base.OAXIView;
import cn.dagongniu.bitman.https.CommonJsonToBean;
import cn.dagongniu.bitman.trading.bean.EntrustInfoBean;
import cn.dagongniu.bitman.trading.bean.CurrentEntrustBean;

public interface EntrustIView extends OAXIView {
    void onTopicTradeListData(EntrustInfoBean bean);

    void onCancellationsState(CommonJsonToBean<String> state);

    void onCurrentEnstrust(CommonJsonToBean<CurrentEntrustBean> bean,int state);

    void onRefreshCurrentEnstrust(CommonJsonToBean<CurrentEntrustBean> bean);

    void onLoadMoreCurrentEnstrust(CommonJsonToBean<CurrentEntrustBean> bean);
}
