package cn.dagongniu.bitman.my_ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.dagongniu.bitman.OAXApplication;
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.assets.bean.PropertyRechargeBean;
import cn.dagongniu.bitman.assets.view.IPropertyRechargeView;
import cn.dagongniu.bitman.base.BaseFragment;
import cn.dagongniu.bitman.constant.SPConstant;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.HttpUtils;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.utils.DebugUtils;
import cn.dagongniu.bitman.utils.DialogUtils;
import cn.dagongniu.bitman.utils.Logs;
import cn.dagongniu.bitman.utils.SPUtils;
import cn.dagongniu.bitman.utils.ToastUtil;


/**
 * 地址 Fragment
 */
public class CardFragment extends BaseFragment implements IPropertyRechargeView, OnRefreshListener,
        OnLoadMoreListener {

    @BindView(R.id.et_card)
    EditText et_card;
    @BindView(R.id.ok)
    Button ok;

    @Override
    protected int getLayoutId() {
        return R.layout.card_layout;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initView() {
        super.initView();

    }


    @Override
    protected void initData() {
        super.initData();

        ok.setClickable(true);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ok.setClickable(false);
                Logs.s("   onclick setOnClickListener   ");
                String trim = "";
                try {
                    trim = et_card.getText().toString().trim();
                } catch (Exception e) {
                    trim = "";
                }

                if (trim.length() > 11) {
                    sendResult(trim);

                } else {
                    ok.setClickable(true);
                    ToastUtil.ShowToast(getResources().getString(R.string.input_str));
                }
            }
        });
    }



    private void sendResult(String trim) {
        Logs.s("    cradBeancradBean 11  " + trim);
        HashMap<String, String> map = new HashMap<>();
        String userId = SPUtils.getParamString(OAXApplication.sContext, SPConstant.USER_ID, null);
        String userToken = (String) SPUtils.getParam(OAXApplication.sContext, SPConstant.USER_TOKEN, "");
        map.put("userId", userId);
        map.put("cdkey", trim);
        map.put("token", userToken);

        HttpUtils.getInstance().postLang(Http.detils, map, getActivity(), new OnBaseDataListener<String>() {
            @Override
            public void onNewData(String data) {
                ok.setClickable(true);
                DebugUtils.prinlnLog(data);
                CradBean cradBean = new Gson().fromJson(data, CradBean.class);
                Logs.s("    cradBeancradBean  " + cradBean);
                if (cradBean.code == 0) {
                    if (cradBean != null) {
                        DialogUtils.getCardDialog((MyTopupCradActivity) getActivity(), trim, cradBean, new DialogUtils.OnSureListener() {
                            @Override
                            public void onSure() {

                            }
                        });
                    }
                } else {

                    ToastUtil.ShowToast(cradBean.msg);
                }
            }

            @Override
            public void onError(String code) {
                ok.setClickable(true);
            }
        }, RequestState.STATE_REFRESH);
    }

    @Override
    public String getCoinName() {
        return null;
    }

    @Override
    public int getPropertyRechargePageIndex() {
        return 0;
    }

    @Override
    public int getPropertyRechargePageSize() {
        return 0;
    }

    @Override
    public void setPropertyRechargeData(PropertyRechargeBean propertyRechargeBean) {

    }

    @Override
    public void setPropertyRechargeDataNull(PropertyRechargeBean propertyRechargeBean) {

    }

    @Override
    public void goLogin(String msg) {

    }

    @Override
    public void setRefreshPropertyRechargeMoreData(PropertyRechargeBean propertyRechargeBean) {

    }

    @Override
    public void setRefreshPropertyRechargeLoadMoreErrer(String noticeCenterMoreData) {

    }

    @Override
    public void refreshErrer() {

    }


    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {

    }
}
