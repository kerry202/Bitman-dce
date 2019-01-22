package cn.dagongniu.bitman.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.OnClick;
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.account.bean.UserCenterBean;
import cn.dagongniu.bitman.base.BaseActivity;
import cn.dagongniu.bitman.customview.CommonToolbar;
import cn.dagongniu.bitman.language.MultiLanguageUtil;
import cn.dagongniu.bitman.utils.SkipActivityUtil;

/**
 * 认证状态
 */
public class AuthenticationStateActivity extends BaseActivity {
    @BindView(R.id.commontoolbar)
    CommonToolbar commontoolbar;
    @BindView(R.id.tv_state)
    TextView tvState;
    @BindView(R.id.iv_state)
    ImageView ivState;
    @BindView(R.id.des_tv_1)
    TextView des_tv_1;
    @BindView(R.id.des_tv_2)
    TextView des_tv_2;
    @BindView(R.id.des_tv_3)
    TextView des_tv_3;
    @BindView(R.id.des_tv_4)
    TextView des_tv_4;
    @BindView(R.id.tv_retry)
    TextView tvRetry;

    UserCenterBean userCenterBean = null;
    int state = -1;
    Intent intent;
    String Reason = "";

    @Override
    protected int getLayoutId() {
        return R.layout.authentication_state_activity;
    }

    @OnClick(R.id.tv_retry)
    public void onClicked() {
        SkipActivityUtil.skipAnotherActivity(this, IdentityAuthenticationActivity.class, true);
    }

    @Override
    protected void initView() {
        super.initView();
        initToolbar();
        intent = this.getIntent();
        state = intent.getIntExtra("STATE", -1);
        Bundle bundle = intent.getExtras();
        userCenterBean = (UserCenterBean) bundle.getSerializable("UserCenterBean");
        Reason = bundle.getString("Reason");
        des_tv_1.setText(Reason);
        setState(state);
    }

    private void initToolbar() {
        commontoolbar.setTitleText(getResources().getString(R.string.identity_authentication));
        commontoolbar.setOnCommonToolbarLeftClickListener(new CommonToolbar.CommonToolbarLeftClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }
        });
    }

    private void setState(int state) {
        switch (state) {
            case 0:
                if (MultiLanguageUtil.getInstance().getLanguageStringType().equals("en")) {
                    ivState.setImageResource(R.mipmap.check_z_en_icon);
                } else {
                    ivState.setImageResource(R.mipmap.check_z_icon);
                }
                //审核中
                tvState.setText(getResources().getString(R.string.authentication_reviewing));

                des_tv_1.setVisibility(View.VISIBLE);
                des_tv_1.setText(getResources().getString(R.string.check_progress) + ":");
                des_tv_2.setVisibility(View.VISIBLE);
                des_tv_2.setText(getResources().getString(R.string.identity_state_reviewing_des));
                des_tv_3.setVisibility(View.GONE);
                des_tv_4.setVisibility(View.GONE);
                tvRetry.setVisibility(View.GONE);
                break;
            case 1:
                //成功
                String userName = "";
                if (userCenterBean.getData().getUsercenter().getIdName() != null) {
                    if (userCenterBean.getData().getUsercenter().getIdNo().length() > 1) {
                        userName = "*" + userCenterBean.getData().getUsercenter().getIdName().substring(userCenterBean.getData().getUsercenter().getIdName().length() - 1, userCenterBean.getData().getUsercenter().getIdName().length());
                    } else {
                        userName = userCenterBean.getData().getUsercenter().getIdName();
                    }
                }
                des_tv_2.setText(getResources().getString(R.string.name) + ": " + userName);

                String userCridId = "";
                if (userCenterBean.getData().getUsercenter().getIdNo() != null) {
                    if (userCenterBean.getData().getUsercenter().getIdNo().length() > 6) {
                        userCridId = "****" + userCenterBean.getData().getUsercenter().getIdNo()
                                .substring(userCenterBean.getData().getUsercenter().getIdNo().length() - 6, userCenterBean.getData().getUsercenter().getIdNo().length());
                    } else {
                        userCridId = userCenterBean.getData().getUsercenter().getIdNo();
                    }
                }
                des_tv_4.setText(getResources().getString(R.string.id_number_str) +" "+ userCridId);

                ivState.setImageResource(R.mipmap.check_succeed_icon);
                tvState.setText(getResources().getString(R.string.authentication_success));
                des_tv_1.setVisibility(View.VISIBLE);
                des_tv_1.setText(getResources().getString(R.string.security_msg) + ": ");
                des_tv_2.setVisibility(View.VISIBLE);
                des_tv_3.setVisibility(View.VISIBLE);
                des_tv_3.setText(getResources().getString(R.string.id_type_str) +" "+ getResources().getString(R.string.crad));
                des_tv_4.setVisibility(View.VISIBLE);
                tvRetry.setVisibility(View.GONE);
                break;
            case 2:
                //失败
                ivState.setImageResource(R.mipmap.check_error_icon);
                des_tv_1.setVisibility(View.VISIBLE);
                des_tv_2.setVisibility(View.VISIBLE);
                des_tv_3.setVisibility(View.VISIBLE);
                des_tv_4.setVisibility(View.GONE);
                tvState.setText(getResources().getString(R.string.authentication_fail));
                des_tv_1.setText(getResources().getString(R.string.check_authentication_1));
                des_tv_2.setText(getResources().getString(R.string.check_authentication_2));
                des_tv_3.setText(getResources().getString(R.string.check_authentication_3));
                tvRetry.setVisibility(View.VISIBLE);
                break;
        }
    }
}
