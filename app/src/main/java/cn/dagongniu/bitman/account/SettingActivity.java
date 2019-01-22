package cn.dagongniu.bitman.account;

import android.app.Dialog;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.kaopiz.kprogresshud.KProgressHUD;

import butterknife.BindView;
import butterknife.OnClick;
import cn.dagongniu.bitman.OAXApplication;
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.base.BaseActivity;
import cn.dagongniu.bitman.constant.SPConstant;
import cn.dagongniu.bitman.customview.CommonToolbar;
import cn.dagongniu.bitman.utils.DialogUtils;
import cn.dagongniu.bitman.utils.Logger;
import cn.dagongniu.bitman.utils.SPUtils;
import cn.dagongniu.bitman.utils.SkipActivityUtil;
import cn.dagongniu.bitman.utils.ToastUtil;
import cn.dagongniu.bitman.utils.events.MyEvents;

public class SettingActivity extends BaseActivity {
    private static final String TAG = "SettingActivity";

    @BindView(R.id.commontoolbar)
    CommonToolbar commontoolbar;
    @BindView(R.id.touch_id)
    Switch touchId;
    @BindView(R.id.rl_set_language)
    RelativeLayout rlSetLanguage;
    @BindView(R.id.rl_set_version)
    RelativeLayout rlSetVersion;
    @BindView(R.id.tv_logout)
    TextView tvLogin;

    private KProgressHUD hud;
    private Dialog sureDialog;
    private Dialog validationDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
        super.initView();
        initToolbar();
        //暂时不实现指纹登录功能
        touchId.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    validationDialog = DialogUtils.getValidationDialog(mContext, false, false, true, "", "",
                            new DialogUtils.onCommitListener() {
                                @Override
                                public void onCommit(String phoneCode, String emailCode, String googleCode) {
                                    //ViewUtils.showKProgressHUD(mContext, R.drawable.correct, R.string.touch_id_open_success);
                                    validationDialog.dismiss();
                                }
                            },
                            new DialogUtils.onClickGetEmailListener() {
                                @Override
                                public void onClickGetEmail() {

                                }
                            },
                            new DialogUtils.onClickGetPhoneListener() {
                                @Override
                                public void onClickPhone() {

                                }
                            });
                } else {
                    DialogUtils.getVerifyTouchIDDialog(mContext);
                }
            }
        });
        setLoginState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OAXApplication.state == 6) {
            OAXApplication.state = 0;
            finish();
        }
    }

    private void setLoginState() {
        //判断是否登录
        boolean isLoginState = (boolean) SPUtils.getParam(this, SPConstant.LOGIN_STATE, false);
        if (isLoginState) {
            tvLogin.setVisibility(View.VISIBLE);
        } else {
            tvLogin.setVisibility(View.GONE);
        }
    }

    private void initToolbar() {
        commontoolbar.setTitleText(getResources().getString(R.string.mine_setting));
        commontoolbar.setOnCommonToolbarLeftClickListener(new CommonToolbar.CommonToolbarLeftClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }
        });
    }

    @OnClick({R.id.rl_set_language, R.id.rl_set_version, R.id.tv_logout})
    public void onClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_set_language://国际化
                SkipActivityUtil.skipAnotherActivity(mContext, SetLanguageActivity.class);
                break;
            case R.id.rl_set_version://关于大公牛
                SkipActivityUtil.skipAnotherActivity(mContext, VersionActivity.class);
                break;
            case R.id.tv_logout:
                sureDialog = DialogUtils.getSureAndCancelDialog(mContext, R.string.exit_app_des, new DialogUtils.OnSureListener() {
                    @Override
                    public void onSure() {
                        SPUtils.remove(SettingActivity.this, SPConstant.LOGIN_STATE);
                        SPUtils.remove(SettingActivity.this, SPConstant.USER_ID);
                        SPUtils.remove(SettingActivity.this, SPConstant.USER_TOKEN);
                        SPUtils.remove(SettingActivity.this, SPConstant.USER_ACCOUNT);
                        SPUtils.remove(SettingActivity.this, SPConstant.USER_CHOOSECOUNTRIES);
                        OAXApplication.collectCoinsMap.clear();
                        ToastUtil.ShowToast(SettingActivity.this.getString(R.string.esc_login_success));
                        sureDialog.dismiss();
                        //发送退出登录通知
                        myEvents.status = MyEvents.status_ok;
                        myEvents.status_type = MyEvents.LoginEsc;
                        myEvents.errmsg = SettingActivity.this.getString(R.string.esc_login_success);
                        eventBus.post(myEvents);
                        Logger.e(TAG, "发送登录成功通知!");
                        finish();
                    }
                });
                break;
        }
    }
}
