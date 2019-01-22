package cn.dagongniu.bitman.my_ui;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.socks.library.KLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.assets.bean.PropertyRechargeBean;
import cn.dagongniu.bitman.assets.bean.UserCoinTopBean;
import cn.dagongniu.bitman.assets.view.IPropertyRechargeView;
import cn.dagongniu.bitman.base.BaseFragment;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.HttpUtils;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.utils.ImgUtils;
import cn.dagongniu.bitman.utils.Logs;
import cn.dagongniu.bitman.utils.PageToolUtils;
import cn.dagongniu.bitman.utils.QRCode;
import cn.dagongniu.bitman.utils.ToastUtil;

/**
 * 地址 Fragment
 */
public class AddressFragment extends BaseFragment implements IPropertyRechargeView, OnRefreshListener,
        OnLoadMoreListener {

    @BindView(R.id.im_code)
    ImageView imCode;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.rl_save_img)
    Button rlSaveImg;
    @BindView(R.id.rl_copy_add)
    Button rl_copy_add;
    private MyTopupCradActivity activity;
    KProgressHUD dialog = null;

    @Override
    protected int getLayoutId() {
        return R.layout.address_layout;
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
        activity = (MyTopupCradActivity) getActivity();

    }

    @Override
    protected void initData() {
        super.initData();

        try {
            if (dialog == null || !dialog.isShowing()) {
                dialog = KProgressHUD.create(activity)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setCancellable(true)
                        .setAnimationSpeed(2)
                        .setDimAmount(0.5f)
                        .show();
            }
        } catch (Exception e) {
            KLog.d("Exception showDialog = " + e.getMessage());
        }

        HttpUtils.getInstance().getAddressOrCrad(Http.USERCOIN_RECHARGE + "/" + activity.MarketId, activity, new OnBaseDataListener<String>() {
            @Override
            public void onNewData(String data) {
                Logs.s("  addressfragment：     " + data);
                UserCoinTopBean userCoinTopBean = new Gson().fromJson(data, UserCoinTopBean.class);
                if (userCoinTopBean != null) {
                    try {
                        tvAddress.setText(userCoinTopBean.getData().getAddress());
                        imCode.setImageBitmap(QRCode.createQRCode(userCoinTopBean.getData().getAddress()));

                    } catch (Exception e) {
                        
                    }

                    rl_copy_add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            PageToolUtils.CopyText(getActivity(), tvAddress.getText().toString());

                        }
                    });
                    rlSaveImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (imCode.getDrawable() != null) {
                                Bitmap bm = ((BitmapDrawable) ((ImageView) imCode).getDrawable()).getBitmap();
                                ImgUtils.requestPermission(getActivity());
                                if (ImgUtils.saveImageToGallery(getActivity(), bm)) {
                                    ToastUtil.ShowToast(getActivity().getResources().getString(R.string.save_success));
                                } else {
                                    ToastUtil.ShowToast(getActivity().getResources().getString(R.string.save_failure));
                                }
                            }
                        }
                    });
                }
                dialog.dismiss();
            }

            @Override
            public void onError(String code) {
                dialog.dismiss();
            }
        }, RequestState.STATE_DIALOG);
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
