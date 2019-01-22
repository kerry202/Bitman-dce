package cn.dagongniu.bitman.account.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.socks.library.KLog;
import com.umeng.socialize.ShareAction;

import org.greenrobot.eventbus.Subscribe;


import butterknife.BindView;
import butterknife.OnClick;
import cn.dagongniu.bitman.OAXApplication;
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.account.AuthenticateActivity;
import cn.dagongniu.bitman.account.AuthenticationStateActivity;
import cn.dagongniu.bitman.account.CommitMsgActivity;
import cn.dagongniu.bitman.account.HelpActivity;
import cn.dagongniu.bitman.account.LoginActivity;
import cn.dagongniu.bitman.account.MineInvitationActivity;
import cn.dagongniu.bitman.account.SafetyActivity;
import cn.dagongniu.bitman.account.OaxRegisteredActivity;
import cn.dagongniu.bitman.account.SettingActivity;
import cn.dagongniu.bitman.account.bean.IdentityResultBean;
import cn.dagongniu.bitman.account.bean.LoginBean;
import cn.dagongniu.bitman.account.bean.UserCenterBean;
import cn.dagongniu.bitman.account.presenter.IdentityResultPresenter;
import cn.dagongniu.bitman.account.presenter.UserCenterPresenter;
import cn.dagongniu.bitman.account.view.IUserCenterView;
import cn.dagongniu.bitman.account.view.IidentityResultView;
import cn.dagongniu.bitman.account.view.MineFragmentItemView;
import cn.dagongniu.bitman.base.BaseFragment;
import cn.dagongniu.bitman.constant.SPConstant;
import cn.dagongniu.bitman.constant.UMConstant;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.main.AnnouncementMoreActivity;
import cn.dagongniu.bitman.utils.AccountValidatorUtil;
import cn.dagongniu.bitman.utils.AppConstants;
import cn.dagongniu.bitman.utils.Logs;
import cn.dagongniu.bitman.utils.SPUtils;
import cn.dagongniu.bitman.utils.SkipActivityUtil;
import cn.dagongniu.bitman.utils.ToastUtil;
import cn.dagongniu.bitman.utils.events.MyEvents;
import cn.dagongniu.bitman.utils.um.UMManager;

/**
 * 我的
 */
public class MineFragment extends BaseFragment implements IUserCenterView, IidentityResultView {

    @BindView(R.id.iv_head)
    ImageView ivHead;
    @BindView(R.id.tv_register)
    TextView tvRegister;
    @BindView(R.id.tv_login)
    TextView tvLogin;
    @BindView(R.id.ll_unlogin)
    LinearLayout llUnlogin;
    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    @BindView(R.id.tv_user_uid)
    TextView tvUserUid;
    @BindView(R.id.iv_grade)
    View ivGrade;
    @BindView(R.id.tv_authentication)
    TextView tvAuthentication;
    @BindView(R.id.tv_security_reminder)
    TextView tvSecurityReminder;
    @BindView(R.id.mine_invitation)
    MineFragmentItemView mineInvitation;
    @BindView(R.id.mine_authentication)
    MineFragmentItemView mineAuthentication;
    @BindView(R.id.mine_security)
    MineFragmentItemView mineSecurity;
    @BindView(R.id.mine_setting)
    MineFragmentItemView mineSetting;
    @BindView(R.id.mine_help)
    MineFragmentItemView mineHelp;
    @BindView(R.id.my_msg)
    MineFragmentItemView my_msg;
    @BindView(R.id.my_upload)
    MineFragmentItemView my_upload;
    @BindView(R.id.tv_str)
    TextView tv_str;
    @BindView(R.id.mine_login_user_data)
    LinearLayout mineLoginUserData;

    @BindView(R.id.scrollview)
    ScrollView scrollview;

    @BindView(R.id.login_suoccss)
    LinearLayout login_suoccss;
//    @BindView(R.id.rl_certification)
//    RelativeLayout rlCertification;

    UserCenterPresenter userCenterPresenter;
    IdentityResultPresenter identityResultPresenter;
    UserCenterBean userCenterBean = null;
    Bundle bundle;

    private ShareAction mShareAction;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initView() {
        super.initView();
        bundle = new Bundle();
        if (getActivity().getIntent().getBooleanExtra("Language", false)) {
            SkipActivityUtil.skipAnotherActivity(mContext, SettingActivity.class);
        }
        boolean islogin = (boolean) SPUtils.getParam(getActivity(), SPConstant.LOGIN_STATE, false);
        try {
            if (islogin) {
                llUnlogin.setVisibility(View.GONE);
            } else {
                llUnlogin.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
        }

        identityResultPresenter = new IdentityResultPresenter(this, RequestState.STATE_DIALOG);
        userCenterPresenter = new UserCenterPresenter(this, RequestState.STATE_REFRESH);
        userCenterPresenter.getUserCenterModule();
    }

    //    R.id.mine_notice,
    @OnClick({R.id.tv_register, R.id.tv_login, R.id.my_upload, R.id.tv_authentication, R.id.mine_invitation, R.id.mine_authentication, R.id.mine_security, R.id.my_msg, R.id.mine_setting, R.id.mine_help})
    public void onClicked(View view) {
        Bundle bundle = new Bundle();
        Intent intent = null;
        boolean islogin = (boolean) SPUtils.getParam(getActivity(), SPConstant.LOGIN_STATE, false);
        switch (view.getId()) {
            case R.id.tv_register:
                bundle.putString(AppConstants.LOGINTYPE, AppConstants.LOGINPHONE);
                bundle.putBoolean(AppConstants.LOGINTYPE_ISLOGIN, false);
                openActivity(OaxRegisteredActivity.class, bundle);
                UMManager.onEvent(mContext, UMConstant.MineFragment, UMConstant.register);
                break;
            case R.id.tv_login:
                bundle.putString(AppConstants.LOGINTYPE, AppConstants.LOGINPHONE);
                openActivity(LoginActivity.class, bundle);
                UMManager.onEvent(mContext, UMConstant.MineFragment, UMConstant.login);
                break;
            case R.id.tv_authentication:
                if (userCenterBean != null && islogin) {
                    identityResultPresenter.getIdentityResultModule();
                } else {
                    ToastUtil.ShowToast(getActivity().getResources().getString(R.string.please_login), getActivity());
                }
                UMManager.onEvent(mContext, UMConstant.MineFragment, UMConstant.go_authentication);
                break;
            case R.id.mine_invitation://邀请
                if (islogin) {
                    SkipActivityUtil.skipAnotherActivity(mContext, MineInvitationActivity.class);
                } else {
                    ToastUtil.ShowToast(getActivity().getResources().getString(R.string.please_login), getActivity());
                }
                UMManager.onEvent(mContext, UMConstant.MineFragment, UMConstant.mine_invitation);
                break;
            case R.id.mine_authentication://实名认证
                if (userCenterBean != null && islogin) {
                    identityResultPresenter.getIdentityResultModule();
                } else {
                    ToastUtil.ShowToast(getActivity().getResources().getString(R.string.please_login), getActivity());
                }
                UMManager.onEvent(mContext, UMConstant.MineFragment, UMConstant.mine_authentication);
                break;
            case R.id.mine_security://安全中心
                if (userCenterBean != null && islogin) {
                    bundle.putSerializable("UserCenterBean", userCenterBean);
                    openActivity(SafetyActivity.class, bundle);
                } else {
                    ToastUtil.ShowToast(getActivity().getResources().getString(R.string.please_login), getActivity());
                }
                UMManager.onEvent(mContext, UMConstant.MineFragment, UMConstant.mine_security);
                break;
            case R.id.mine_setting:
                SkipActivityUtil.skipAnotherActivity(mContext, SettingActivity.class);
                UMManager.onEvent(mContext, UMConstant.MineFragment, UMConstant.mine_setting);
                break;
            case R.id.my_msg:
                intent = new Intent(mContext, AnnouncementMoreActivity.class);
                intent.putExtra("ArticleType", "1");
                startActivity(intent);
                break;

            case R.id.my_upload:

                if (userCenterBean != null && islogin) {
                    intent = new Intent(mContext, CommitMsgActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtil.ShowToast(getActivity().getResources().getString(R.string.please_login), getActivity());
                }

                break;
            case R.id.mine_help:
                SkipActivityUtil.skipAnotherActivity(mContext, HelpActivity.class);
                UMManager.onEvent(mContext, UMConstant.MineFragment, UMConstant.mine_help);
                break;
        }
    }

    @Subscribe
    public void onEvent(MyEvents event) {
        switch (event.status_type) {
            /**
             * 登录成功通知 登陆成功后刷新下我的信息
             */
            case MyEvents.LoginSuccess:
                ToastUtil.ShowToast(getResources().getString(R.string.login_success));
                LoginBean loginBean = (LoginBean) event.something;
                setLoginSuccess(loginBean);
                userCenterPresenter.getUserCenterModule();
                break;
            /**
             * 退出登录
             */
            case MyEvents.LoginEsc:
                login_suoccss.setVisibility(View.GONE);
                llUnlogin.setVisibility(View.VISIBLE);
                Logs.s(" 登录成功了  退出登录    ");
                break;
            /**
             * 绑定phone or email成功刷新我的
             */
            case MyEvents.Bind_PhoneOrEmail_Success:
                userCenterPresenter.getUserCenterModule();
                break;
            /**
             * 开启验证通知我的刷新
             */
            case MyEvents.Account_Check_Refresh:
                userCenterPresenter.getUserCenterModule();
                break;
            /**
             * 绑定google成功
             */
            case MyEvents.Bind_Google_Success:
                userCenterPresenter.getUserCenterModule();
                break;
            /**
             * 身份证资料上传成功刷新
             */
            case MyEvents.User_Identity_Authen:
                userCenterPresenter.getUserCenterModule();
                break;
            /**
             * 登陆失效 初始化我的页面
             */
            case MyEvents.Token_failure:
                tokenInitializeData();
                break;
            /**
             *  修改密码 重新登录
             */
            case MyEvents.User_Update_Passwrod_Success:
                tokenInitializeData();
                break;
        }
    }

    /**
     * 登陆成功
     *
     * @param loginSuccess
     */
    public void setLoginSuccess(LoginBean loginSuccess) {
        KLog.d("loginSuccess = " + new Gson().toJson(loginSuccess));
        login_suoccss.setVisibility(View.VISIBLE);
        llUnlogin.setVisibility(View.GONE);
        LoginBean.DataBean data = loginSuccess.getData();
        String name = data.getName();

        if (data.getCheckStatus() == 2) {//已通过认证
            tvUserName.setText(name);
        } else {
            if (AccountValidatorUtil.isEmail(name.toString())) {
                int indexStart = name.indexOf("@");
                String toStr = name.substring(0, indexStart);
                String emailStr = "";
                if (toStr.length() > 4) {
                    emailStr = "****" + name.substring(indexStart - 4, name.length());
                } else {
                    emailStr = name;
                }
                tvUserName.setText(emailStr);
            } else {
                tvUserName.setText("****" + name.substring(name.length() - 4));
            }
        }

        tvUserUid.setText("UID：" + loginSuccess.getData().getUserId());
        if (loginSuccess.getData().getCheckStatus() == 0) {
            ivGrade.setBackground(ContextCompat.getDrawable(getActivity(), R.mipmap.vip_icon1));
            tvAuthentication.setText(R.string.go_authenticate);
            tv_str.setText(mContext.getResources().getString(R.string.vip2_str));
        } else {
            ivGrade.setBackground(ContextCompat.getDrawable(getActivity(), R.mipmap.vip_icon2));
            tv_str.setText(mContext.getResources().getString(R.string.vip3_str));
            tvAuthentication.setText(R.string.authenticated);
        }
    }

    /**
     * 回调
     *
     * @param userCenterData
     */
    @Override
    public void setUserCenterData(UserCenterBean userCenterData) {
        KLog.d("userCenterData = " + new Gson().toJson(userCenterData));
        userCenterBean = userCenterData;
        login_suoccss.setVisibility(View.VISIBLE);
        llUnlogin.setVisibility(View.GONE);

        UserCenterBean.DataBean.UsercenterBean data = userCenterData.getData().getUsercenter();
        String name = data.getIdName();

        if (data.getCheckStatus() == 2) {//已通过认证
            String userName = "";
            if (!TextUtils.isEmpty(name)) {
                if (name.length() > 1) {
                    userName = "*" + name.substring(name.length() - 1, name.length());
                } else {
                    userName = name;
                }
            }
            tvUserName.setText(userName);
        } else {
            if (AccountValidatorUtil.isEmail(name.toString())) {
                int indexStart = name.indexOf("@");
                String toStr = name.substring(0, indexStart);
                String emailStr = "";
                if (toStr.length() > 4) {
                    emailStr = "****" + name.substring(indexStart - 4, name.length());
                } else {
                    emailStr = name;
                }
                tvUserName.setText(emailStr);
            } else {
                tvUserName.setText("****" + name.substring(name.length() - 4));
            }
        }

        tvUserUid.setText("UID：" + userCenterData.getData().getUsercenter().getId());

//        tvGrade.setText("Lv " + userCenterData.getData().getUsercenter().getLevel());

        switch (userCenterData.getData().getUsercenter().getCheckStatus()) {
            case -1://未通过
//                rlCertification.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.border_focused_withd_mine_certification));
                ivGrade.setBackground(ContextCompat.getDrawable(getActivity(), R.mipmap.vip_icon1));
                tv_str.setText(mContext.getResources().getString(R.string.vip2_str));
                tvAuthentication.setText(R.string.not_through);
                break;
            case 0://未认证
//                rlCertification.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.border_focused_withd_mine_certification));
                ivGrade.setBackground(ContextCompat.getDrawable(getActivity(), R.mipmap.vip_icon1));
                tv_str.setText(mContext.getResources().getString(R.string.vip2_str));
                tvAuthentication.setText(R.string.unauthorized);
                break;
            case 1://待审核
//                rlCertification.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.border_focused_withd_mine_certification));
                ivGrade.setBackground(ContextCompat.getDrawable(getActivity(), R.mipmap.vip_icon1));
                tv_str.setText(mContext.getResources().getString(R.string.vip2_str));
                tvAuthentication.setText(R.string.to_audit);
                break;
            case 2://审核通过
                ivGrade.setBackground(ContextCompat.getDrawable(getActivity(), R.mipmap.vip_icon2));
                tv_str.setText(mContext.getResources().getString(R.string.vip3_str));
                tvAuthentication.setText(R.string.approved);
                break;
        }

        if (userCenterData.getData().getUsercenter().getLevel() == 3) {
            ivGrade.setBackground(ContextCompat.getDrawable(getActivity(), R.mipmap.vip_icon3));
            tv_str.setText(mContext.getResources().getString(R.string.vip4_str));
            tvAuthentication.setText(R.string.approved);
        }


        /**
         * 缓存数据
         */
        SPUtils.setParam(mContext, SPConstant.USER_PHONE, userCenterData.getData().getUsercenter().getPhone() != null ? userCenterData.getData().getUsercenter().getPhone() : "");
        SPUtils.setParam(mContext, SPConstant.USER_EMAIL, userCenterData.getData().getUsercenter().getEmail() != null ? userCenterData.getData().getUsercenter().getEmail() : "");

        SPUtils.setParam(mContext, SPConstant.LEVEL1_BTC, userCenterData.getData().getLEVEL1_BTC());
        SPUtils.setParam(mContext, SPConstant.LEVEL2_BTC, userCenterData.getData().getLEVEL2_BTC());
        SPUtils.setParam(mContext, SPConstant.LEVEL3_BTC, userCenterData.getData().getLEVEL3_BTC());

        OAXApplication.getInstance().setUserEmail(userCenterData.getData().getUsercenter().getEmail());
        OAXApplication.getInstance().setUserPhone(userCenterData.getData().getUsercenter().getPhone());
        OAXApplication.getInstance().setUserGoogleKe(userCenterData.getData().getUsercenter().getGoogleKey());
        OAXApplication.getInstance().setUserEmailState(userCenterData.getData().getUsercenter().getEmailStatus());
        OAXApplication.getInstance().setUserGoogleState(userCenterData.getData().getUsercenter().getGoogleStatus());
        OAXApplication.getInstance().setUserPhoneState(userCenterData.getData().getUsercenter().getPhoneStatus());
    }

    /**
     * 登陆失效 获取请求返回错误初始化页面
     */
    public void tokenInitializeData() {
        try {

            login_suoccss.setVisibility(View.GONE);
            llUnlogin.setVisibility(View.VISIBLE);
            tvUserName.setText("");
            tvAuthentication.setText("");
//            rlCertification.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.border_focused_withd_mine_certification));
            tvUserUid.setText("");
//            tvGrade.setText("");
            userCenterBean = null;
        } catch (Exception e) {

        }
    }

    @Override
    public void setUserCenterFailure(String msg) {
        tokenInitializeData();
    }

    @Override
    public void onResume() {
        super.onResume();
        UMManager.onResume(mContext, UMConstant.AssetsFragment);
    }

    @Override
    public void onPause() {
        super.onPause();
        UMManager.onPause(mContext, UMConstant.AssetsFragment);
    }

    @Override
    public void IidentityResultsuccessful(IdentityResultBean httpBaseBean) {
        switch (httpBaseBean.getData().getCheckStatus()) {
            case -1://未通过
                bundle.putInt("STATE", 2);
                bundle.putSerializable("UserCenterBean", userCenterBean);
                bundle.putString("Reason", httpBaseBean.getData().getReason());
                openActivity(AuthenticationStateActivity.class, bundle);
                break;
            case 0://未认证
                openActivity(AuthenticateActivity.class, bundle);
                break;
            case 1://待审核
                bundle.putInt("STATE", 0);
                bundle.putString("Reason", "");
                bundle.putSerializable("UserCenterBean", userCenterBean);
                openActivity(AuthenticationStateActivity.class, bundle);
                break;
            case 2://审核通过
                bundle.putInt("STATE", 1);
                bundle.putString("Reason", "");
                bundle.putSerializable("UserCenterBean", userCenterBean);
                openActivity(AuthenticationStateActivity.class, bundle);
                break;
        }
    }

    @Override
    public void IidentityResultfailure(String msg) {
        ToastUtil.ShowToast(msg);
    }
}
