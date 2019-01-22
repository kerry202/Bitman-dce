package cn.dagongniu.bitman.account;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.dagongniu.bitman.OAXApplication;
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.account.bean.LoginBean;
import cn.dagongniu.bitman.account.presenter.CheckLoginPasswordPresenter;
import cn.dagongniu.bitman.account.presenter.LoginPresenter;
import cn.dagongniu.bitman.account.presenter.SendSmsPresenter;
import cn.dagongniu.bitman.account.view.ICheckLoginPasswordView;
import cn.dagongniu.bitman.account.view.ILoginView;
import cn.dagongniu.bitman.account.view.SendSmsView;
import cn.dagongniu.bitman.base.BaseActivity;
import cn.dagongniu.bitman.bitman.NewLoginBean;
import cn.dagongniu.bitman.captcha.Captcha;
import cn.dagongniu.bitman.captcha.CaptchaListener;
import cn.dagongniu.bitman.captcha.OaxCaptcha;
import cn.dagongniu.bitman.captcha.CaptchaPresenter;
import cn.dagongniu.bitman.captcha.CaptchaTask;
import cn.dagongniu.bitman.captcha.ICaptchaView;
import cn.dagongniu.bitman.constant.SPConstant;
import cn.dagongniu.bitman.constant.UMConstant;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.HttpBaseBean;
import cn.dagongniu.bitman.https.HttpUtils;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.main.MainActivity;
import cn.dagongniu.bitman.utils.AccountValidatorUtil;
import cn.dagongniu.bitman.utils.AppConstants;
import cn.dagongniu.bitman.utils.DialogUtils;
import cn.dagongniu.bitman.utils.Logger;
import cn.dagongniu.bitman.utils.Logs;
import cn.dagongniu.bitman.utils.SPUtils;
import cn.dagongniu.bitman.utils.ToastUtil;
import cn.dagongniu.bitman.utils.events.MyEvents;
import cn.dagongniu.bitman.utils.um.UMManager;
import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

/**
 * 登陆
 */
public class LoginActivity extends BaseActivity implements ILoginView, SendSmsView, ICaptchaView, ICheckLoginPasswordView {

    private static final String TAG = "LoginActivity";

    @BindView(R.id.im_close)
    ImageView imClose;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.im_email_phone_icon)
    ImageView imEmailPhoneIcon;
    @BindView(R.id.tv_internationalization)
    TextView tvInternationalization;
    @BindView(R.id.rl_internationalization)
    RelativeLayout rlInternationalization;
    @BindView(R.id.et_email_phone)
    EditText etEmailPhone;
    @BindView(R.id.im_x)
    ImageView imX;
    @BindView(R.id.rl_x)
    RelativeLayout rlX;
    @BindView(R.id.im_icon_pwd)
    ImageView imIconPwd;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.rl_open_pwd)
    ImageView imOpenPwd;

    @BindView(R.id.rl_login)
    TextView rlLogin;
    @BindView(R.id.tv_forget_pwd)
    TextView tvForgetPwd;
    @BindView(R.id.tv_registered)
    TextView tvRegistered;
    @BindView(R.id.ll_get_login)
    TextView tvGetLogin;

    String LoginType = AppConstants.LOGINPHONE;//默认手机登陆
    boolean LoginFailure = false;//是否登录失效
    String goLoginType = "";
    Bundle bundle = new Bundle();
    boolean isOpenYan = true;
    String Validate = "";
    Intent intent;
    private PhoneNumberUtil utilPhoneNumber = null;
    private String dfShortName = "CN";//默认中国

    LoginPresenter loginPresenter;
    SendSmsPresenter sendSmsPresenter;
    CaptchaPresenter captchaPresenter;
    CheckLoginPasswordPresenter checkLoginPasswordPresenter;

    OaxCaptcha mOaxCaptcha = null;
    private CaptchaTask mLoginTask = null;
    private boolean isSwitchLoginType = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        super.initView();
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        } else {
            //TODO
        }

        rlLogin.setEnabled(false);
        initEvent();
        intent = getIntent();
        LoginFailure = intent.getBooleanExtra(AppConstants.LOGIN_FAILURE, false);

        LoginType = intent.getStringExtra(AppConstants.LOGINTYPE);
        LoginType = LoginType == null ? AppConstants.LOGINPHONE : intent.getStringExtra(AppConstants.LOGINTYPE);
        initLayout();
        if (LoginType != null && LoginType.equals(AppConstants.LOGINPHONE)) {
//            tvHintErrer.setText(R.string.login_show_errer_phone);
        }
    }

    @Override
    protected void initData() {
        super.initData();

        String phone_number = (String) SPUtils.getParam(this, SPConstant.USER_PHONE1, "");
        String malie_number = (String) SPUtils.getParam(this, SPConstant.USER_EMAIL1, "");

        if (LoginType.equals(AppConstants.LOGINEAMIL)) {
            etEmailPhone.setText(malie_number);
        } else if (LoginType.equals(AppConstants.LOGINPHONE)) {
            etEmailPhone.setText(phone_number);
        }

        Logs.s("  phone_number   " + phone_number);

        checkLoginPasswordPresenter = new CheckLoginPasswordPresenter(this, RequestState.STATE_DIALOG);
        captchaPresenter = new CaptchaPresenter(this, RequestState.STATE_DIALOG);
        loginPresenter = new LoginPresenter(this, RequestState.STATE_DIALOG);
        sendSmsPresenter = new SendSmsPresenter(this, RequestState.STATE_REFRESH);
    }

    /**
     * edt 改变事件
     */
    private void initEvent() {
        utilPhoneNumber = PhoneNumberUtil.createInstance(getApplicationContext());

        etEmailPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    rlX.setVisibility(View.GONE);
                    etPwd.setText("");
                } else {
                    rlX.setVisibility(View.VISIBLE);
                }
                String cede = etPwd.getText().toString();

                if (LoginType.equals(AppConstants.LOGINEAMIL)) {
                    if (AccountValidatorUtil.isEmail(etEmailPhone.getText().toString()) && !TextUtils.isEmpty(cede)) {
                        rlLogin.setBackgroundResource(R.drawable.white_circle3);
                        rlLogin.setEnabled(true);
                        rlLogin.setTextColor(getResources().getColor(R.color.white));
                    } else {
                        rlLogin.setBackgroundResource(R.drawable.noclick_circle_gray);
                        rlLogin.setEnabled(false);
                        rlLogin.setTextColor(getResources().getColor(R.color.text_str));
                    }
                } else if (LoginType.equals(AppConstants.LOGINPHONE)) {
                    if (isPhoneNumber() && !TextUtils.isEmpty(cede)) {
                        rlLogin.setBackgroundResource(R.drawable.white_circle3);
                        rlLogin.setEnabled(true);
                        rlLogin.setTextColor(getResources().getColor(R.color.white));
                    } else {
                        rlLogin.setBackgroundResource(R.drawable.noclick_circle_gray);
                        rlLogin.setEnabled(false);
                        rlLogin.setTextColor(getResources().getColor(R.color.text_str));
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (LoginType.equals(AppConstants.LOGINEAMIL)) {
                    if (AccountValidatorUtil.isEmail(etEmailPhone.getText().toString()) && !TextUtils.isEmpty(s.toString())) {
                        rlLogin.setBackgroundResource(R.drawable.white_circle3);
                        rlLogin.setEnabled(true);
                        rlLogin.setTextColor(getResources().getColor(R.color.white));
                    } else {
                        rlLogin.setBackgroundResource(R.drawable.noclick_circle_gray);
                        rlLogin.setEnabled(false);
                        rlLogin.setTextColor(getResources().getColor(R.color.text_str));
                    }
                } else if (LoginType.equals(AppConstants.LOGINPHONE)) {
                    if (isPhoneNumber() && !TextUtils.isEmpty(s.toString())) {
                        rlLogin.setBackgroundResource(R.drawable.white_circle3);
                        rlLogin.setTextColor(getResources().getColor(R.color.white));
                        rlLogin.setEnabled(true);
                    } else {
                        rlLogin.setBackgroundResource(R.drawable.noclick_circle_gray);
                        rlLogin.setEnabled(false);
                        rlLogin.setTextColor(getResources().getColor(R.color.text_str));
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * 效验国际化手机
     *
     * @return
     */
    private boolean isPhoneNumber() {
        if (dfShortName.contains("+"))
            dfShortName = dfShortName.replace("+", "");
        boolean validNumber = false;
        try {
            Phonenumber.PhoneNumber phoneNumber = utilPhoneNumber.parse(tvInternationalization.getText().toString() + etEmailPhone.getText().toString(), dfShortName);
            validNumber = utilPhoneNumber.isValidNumber(phoneNumber);
        } catch (NumberParseException e) {
            e.printStackTrace();
        }
        return validNumber;
    }

    /**
     * 初始化页面
     */
    private void initLayout() {
        if (LoginType.equals(AppConstants.LOGINEAMIL)) {
            initializeData(R.string.login_email,
                    getResources().getDrawable(R.mipmap.emali_icon), true, R.string.login_email_hint,
                    R.string.use_login_phone_hint, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            goLoginType = AppConstants.LOGINPHONE;
        } else if (LoginType.equals(AppConstants.LOGINPHONE)) {
            initializeData(R.string.login_phone,
                    getResources().getDrawable(R.mipmap.phone_icon), false, R.string.login_phone_hint,
                    R.string.use_login_email_hint, InputType.TYPE_CLASS_NUMBER);
            goLoginType = AppConstants.LOGINEAMIL;
        }
    }

    /**
     * 初始化页面
     *
     * @param titleName                title名字
     * @param emailPhoneIcon           账户图标
     * @param isRlInternationalization 是否隐藏国际化 +86
     * @param hintName                 账户hint提示
     * @param goLogin                  使用***登陆
     * @param textType                 账户输入类型
     */
    private void initializeData(int titleName, Drawable emailPhoneIcon,
                                boolean isRlInternationalization,
                                int hintName, int goLogin, int textType) {
        tvTitle.setText(titleName);
        imEmailPhoneIcon.setImageDrawable(emailPhoneIcon);
        if (isRlInternationalization)
            rlInternationalization.setVisibility(View.GONE);
        else
            rlInternationalization.setVisibility(View.VISIBLE);
        etEmailPhone.setHint(hintName);
        tvGetLogin.setText(goLogin);
        etEmailPhone.setInputType(textType);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        UMManager.openActivityDurationTrack(mContext);
    }


    @OnClick({R.id.im_close, R.id.rl_x, R.id.rl_login, R.id.rl_open_pwd, R.id.rl_internationalization, R.id.ll_get_login, R.id.tv_registered, R.id.tv_forget_pwd})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.im_close:
                if (!goLoginType.equals(AppConstants.LOGINPHONE)) {
                    UMManager.onEvent(mContext, UMConstant.LoginActivity_phone, UMConstant.close);
                } else {
                    UMManager.onEvent(mContext, UMConstant.LoginActivity_mail, UMConstant.close);
                }
                finish();
                break;
            case R.id.rl_x:
                etEmailPhone.setText("");
                rlX.setVisibility(View.GONE);
                if (!goLoginType.equals(AppConstants.LOGINPHONE)) {
                    UMManager.onEvent(mContext, UMConstant.LoginActivity_phone, UMConstant.clear_phone);
                } else {
                    UMManager.onEvent(mContext, UMConstant.LoginActivity_mail, UMConstant.clear_mail);
                }
                break;
            case R.id.rl_open_pwd://密码显示隐藏
                if (isOpenYan) {
                    isOpenYan = false;
                    imOpenPwd.setImageDrawable(getResources().getDrawable(R.mipmap.login_show_icon));
                    etPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    if (!goLoginType.equals(AppConstants.LOGINPHONE)) {
                        UMManager.onEvent(mContext, UMConstant.LoginActivity_phone, UMConstant.unwatch_pwd);
                    } else {
                        UMManager.onEvent(mContext, UMConstant.LoginActivity_mail, UMConstant.unwatch_pwd);
                    }
                } else {
                    isOpenYan = true;
                    imOpenPwd.setImageDrawable(getResources().getDrawable(R.mipmap.login_hide_icon));
                    etPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    if (!goLoginType.equals(AppConstants.LOGINPHONE)) {
                        UMManager.onEvent(mContext, UMConstant.LoginActivity_phone, UMConstant.watch_pwd);
                    } else {
                        UMManager.onEvent(mContext, UMConstant.LoginActivity_mail, UMConstant.watch_pwd);
                    }
                }
                break;
            case R.id.ll_get_login://切换登录 邮箱手机
                isSwitchLoginType = true;
                finish();
                bundle.putString(AppConstants.LOGINTYPE, goLoginType);
                openActivity(LoginActivity.class, bundle);
                if (!goLoginType.equals(AppConstants.LOGINPHONE)) {
                    UMManager.onEvent(mContext, UMConstant.LoginActivity_phone, UMConstant.login_mail);
                } else {
                    UMManager.onEvent(mContext, UMConstant.LoginActivity_mail, UMConstant.login_phone);
                }
                break;
            case R.id.tv_registered://注册
                if (goLoginType.equals(AppConstants.LOGINEAMIL)) {
                    bundle.putString(AppConstants.LOGINTYPE, AppConstants.LOGINPHONE);
                    bundle.putBoolean(AppConstants.LOGINTYPE_ISLOGIN, true);
                    openActivity(OaxRegisteredActivity.class, bundle);
                    UMManager.onEvent(mContext, UMConstant.LoginActivity_phone, UMConstant.register);
                } else if (goLoginType.equals(AppConstants.LOGINPHONE)) {
                    bundle.putString(AppConstants.LOGINTYPE, AppConstants.LOGINEAMIL);
                    bundle.putBoolean(AppConstants.LOGINTYPE_ISLOGIN, true);
                    openActivity(OaxRegisteredActivity.class, bundle);
                    UMManager.onEvent(mContext, UMConstant.LoginActivity_mail, UMConstant.register);
                }
                break;
            case R.id.tv_forget_pwd://忘记密码
                if (goLoginType.equals(AppConstants.LOGINEAMIL)) {
                    bundle.putString(AppConstants.LOGINTYPE, AppConstants.LOGINPHONE);
                    openActivity(ForgetPasswordActivity.class, bundle);
                    UMManager.onEvent(mContext, UMConstant.LoginActivity_phone, UMConstant.forget_pwd);
                } else if (goLoginType.equals(AppConstants.LOGINPHONE)) {
                    bundle.putString(AppConstants.LOGINTYPE, AppConstants.LOGINEAMIL);
                    openActivity(ForgetPasswordActivity.class, bundle);
                    UMManager.onEvent(mContext, UMConstant.LoginActivity_mail, UMConstant.forget_pwd);
                }
                break;
            case R.id.rl_internationalization://国际区号
                Intent intentCC = new Intent(this, ChooseCountriesActivity.class);
                startActivityForResult(intentCC, 1);
                UMManager.onEvent(mContext, UMConstant.LoginActivity_phone, UMConstant.area_code);
                break;
            case R.id.rl_login:

//                checkLoginPasswordPresenter.getCheckLoginPasswordModule();
//                if (!goLoginType.equals(AppConstants.LOGINPHONE)) {
//                    UMManager.onEvent(mContext, UMConstant.LoginActivity_phone, UMConstant.login);
//                } else {
//                    UMManager.onEvent(mContext, UMConstant.LoginActivity_mail, UMConstant.login);
//                }

                login();


                break;
        }
    }

    private void login() {
        String phone = etEmailPhone.getText().toString();
        String pwd = etPwd.getText().toString();
        StringBuffer stringBuffer = new StringBuffer();
        String chooseCountries = getChooseCountries();
        HashMap<String, Object> hashMap = new HashMap();

        String substring = chooseCountries.substring(1, chooseCountries.length());
        stringBuffer.append("00");
        stringBuffer.append(substring);
        stringBuffer.append(phone);


        Logs.s("  substringsubstring       " + stringBuffer.toString());
        hashMap.put("userName", stringBuffer.toString());
        hashMap.put("passWord", pwd);
        hashMap.put("loginProject", Http.projectName);

        HttpUtils.getInstance().bitmanDate(Http.sendLogin, hashMap, mContext, new OnBaseDataListener<String>() {
            @Override
            public void onNewData(String data) {
                Logs.s(" bitman登录: " + data);

                NewLoginBean newLoginBean = new Gson().fromJson(data, NewLoginBean.class);
                if (newLoginBean != null) {
                    if (newLoginBean.code == 0) {
                        SPUtils.setParam(mContext, SPConstant.USER_TOKEN, newLoginBean.data.token + "");
                        SPUtils.setParam(mContext, SPConstant.USER_CHOOSECOUNTRIES, tvInternationalization.getText().toString());
                        SPUtils.setParam(mContext, SPConstant.LOGIN_STATE, true);
                        SPUtils.setParam(mContext, SPConstant.USER_ID, newLoginBean.data.userId + "");
                        SPUtils.setParam(mContext, SPConstant.USER_PHONE, etEmailPhone.getText().toString());

                        //发送登录成功通知
                        myEvents.status = MyEvents.status_ok;
                        myEvents.status_type = MyEvents.LoginSuccess;
                        myEvents.errmsg = mContext.getResources().getString(R.string.login_success);
                        eventBus.post(myEvents);
                        Logger.e(TAG, "发送登录成功通知!");
                        Intent intent = new Intent(mContext, MainActivity.class);
                        startActivity(intent);

                    } else {
                        ToastUtil.ShowToast(newLoginBean.msg);
                    }
                }


            }

            @Override
            public void onError(String code) {

            }
        }, RequestState.STATE_REFRESH);
    }


    /**
     * 启动滑块
     */
    public void startCaptcha() {

        mOaxCaptcha = new OaxCaptcha(this, new CaptchaListener() {
            @Override
            public void onReady(boolean ret) {
                //该为调试接口，ret为true表示加载Sdk完成
                if (ret) {
                    Logger.e(TAG, "唤醒网易滑块成功");
                }
            }

            @Override
            public void closeWindow() {
                Logger.e(TAG, "关闭网易云滑块");
            }

            @Override
            public void onError(String errormsg) {
                Logger.e(TAG, "错误信息：" + errormsg);
            }

            @Override
            public void onValidate(String result, String validate, String message) {
                //验证结果，valiadte，可以根据返回的三个值进行用户自定义二次验证
                if (validate.length() > 0) {
                    Validate = validate;
                    captchaPresenter.getCaptchaModule();
                    //ToastUtil.ShowToast(LoginActivity.this.getResources().getString(R.string.captcha_successful));
                    Logger.e(TAG, "验证成功：result = " + result + ", validate = " + validate + ", message = " + message);
                } else {

                    ToastUtil.ShowToast(LoginActivity.this.getResources().getString(R.string.captcha_failure));
                    Logger.e(TAG, "验证失败：result = " + result + ", validate = " + validate + ", message = " + message);
                }
            }

            @Override
            public void onCancel() {
                Logger.e(TAG, "取消滑块线程");
                //用户取消加载或者用户取消验证，关闭异步任务，也可根据情况在其他地方添加关闭异步任务接口
                if (mLoginTask != null) {
                    if (mLoginTask.getStatus() == AsyncTask.Status.RUNNING) {
                        Log.i(TAG, "stop mLoginTask");
                        mLoginTask.cancel(true);
                    }
                }
            }
        });
        Captcha captcha = mOaxCaptcha.getmCaptcha();
        mLoginTask = new CaptchaTask(this, captcha, new CaptchaTask.CaptchaTaskBack() {
            @Override
            public void onCaptchaTashBackCancelled() {
                mLoginTask = null;
            }
        });
        mOaxCaptcha.Start(mLoginTask);
    }

    /**
     * 跳转回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                Bundle bundle = data.getExtras();
                String info = bundle.getString("numberType");
                dfShortName = bundle.getString("shortName");
                etEmailPhone.setText("");
                tvInternationalization.setText(info);
                break;
            default:
                break;
        }
    }

    @Override
    public String getEmailAndPhone() {
        return etEmailPhone.getText().toString();
    }

    /**
     * 效验账户跟密码的正确回调
     */
    @Override
    public void isSuccess() {
        startCaptcha();
    }

    /**
     * 效验账户跟密码错误回调
     *
     * @param msg
     */
    @Override
    public void setShowCheckPasswordErrer(String msg) {
        DialogUtils.showDialog(this, R.drawable.errer_icon, msg);
    }

    @Override
    public String getPassword() {
        return etPwd.getText().toString();
    }

    @Override
    public String getUsername() {
        return etEmailPhone.getText().toString();
    }

    //1 手机 2邮箱
    @Override
    public String getLoginType() {
        if (LoginType.equals(AppConstants.LOGINEAMIL)) {
            return "2";
        } else if (LoginType.equals(AppConstants.LOGINPHONE)) {
            return "1";
        } else {
            return "0";
        }
    }

    String smsPhoneCode = null;
    String smsGoogleCode = null;

    @Override
    public String getSmsCode() {
        return smsPhoneCode;
    }

    @Override
    public String getGoogleCode() {
        return smsGoogleCode;
    }

    /**
     * 登录成功回调
     *
     * @param data
     */
    @Override
    public void isLogin(LoginBean data) {
        if (data.isSuccess()) {
            setSuccess(data);
        } else {
            smsPhoneCode = null;
            smsGoogleCode = null;
            if (TextUtils.isEmpty(data.getMsg())) {
                DialogUtils.showDialog(this, R.drawable.errer_icon, data.getMsg());
            }
            setValidation(data);
        }
    }

    /**
     * 登录失败
     *
     * @param msg
     */
    @Override
    public void setLoginFailure(String msg) {
        smsPhoneCode = null;
        smsGoogleCode = null;
        DialogUtils.showDialog(this, R.drawable.errer_icon, msg);
    }

    /**
     * 手机登陆还是邮箱登陆   手机=true
     *
     * @return
     */
    @Override
    public boolean isEmailPhone() {
        if (LoginType.equals(AppConstants.LOGINEAMIL)) {
            return false;
        } else if (LoginType.equals(AppConstants.LOGINPHONE)) {
            return true;
        }
        return false;
    }

    /**
     * 登录成功
     *
     * @param loginBean
     * @return
     */
    private void setSuccess(LoginBean loginBean) {

        //存储token and id
        SPUtils.setParam(this, SPConstant.USER_TOKEN, loginBean.getData().getAccessToken());

        if (LoginType.equals(AppConstants.LOGINEAMIL)) {
            SPUtils.setParam(this, SPConstant.USER_ACCOUNT, etEmailPhone.getText().toString());
            SPUtils.setParam(this, SPConstant.USER_EMAIL1, etEmailPhone.getText().toString());
        } else if (LoginType.equals(AppConstants.LOGINPHONE)) {
            StringBuffer stringBuffer = new StringBuffer();
            String tvInternationalizationStr = tvInternationalization.getText().toString();
            String substring = tvInternationalizationStr.substring(1, tvInternationalizationStr.length());
            stringBuffer.append("00");
            stringBuffer.append(substring);
            stringBuffer.append(etEmailPhone.getText().toString());
            SPUtils.setParam(this, SPConstant.USER_ACCOUNT, stringBuffer.toString());
            SPUtils.setParam(this, SPConstant.USER_PHONE1, etEmailPhone.getText().toString());
        }

        SPUtils.setParam(this, SPConstant.USER_CHOOSECOUNTRIES, tvInternationalization.getText().toString());
        SPUtils.setParam(this, SPConstant.USER_ID, loginBean.getData().getUserId());
        SPUtils.setParam(this, SPConstant.LOGIN_STATE, true);

        //发送登录成功通知
        myEvents.status = MyEvents.status_ok;
        myEvents.status_type = MyEvents.LoginSuccess;
        myEvents.errmsg = this.getResources().getString(R.string.login_success);
        myEvents.something = loginBean;
        eventBus.post(myEvents);
        Logger.e(TAG, "发送登录成功通知!");

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    Dialog validationDialog = null;

    /**
     * 登录失败 需验证
     *
     * @param data
     */
    private void setValidation(LoginBean data) {
        Logs.s("   登录失败 需验证   " + data);
        if (data.getCode().equals("2")) {//手机短信

            String phoneStr = etEmailPhone.getText().toString();
            phoneStr = "****" + phoneStr.substring(phoneStr.length() - 4);
            OAXApplication.bitmanloginstate = 1;
            validationDialog = DialogUtils.getValidationDialog(this, true, false, true,
                    "", phoneStr,
                    new DialogUtils.onCommitListener() {
                        @Override
                        public void onCommit(String phoneCode, String emailCode, String googleCode) {
                            smsPhoneCode = phoneCode;
                            smsGoogleCode = googleCode;
                            loginPresenter.getLoginModule();
                        }
                    }, null, new DialogUtils.onClickGetPhoneListener() {
                        @Override
                        public void onClickPhone() {
                            OAXApplication.bitmanloginstate = 1;
                            sendSmsPresenter.getSendSmsModule1("6");
                        }
                    });
            validationDialog.show();
        } else if (data.getCode().equals("3")) {//Google
            Logs.s("   ");
            validationDialog = DialogUtils.getValidationDialog(this, true, true, false,
                    "", "",
                    new DialogUtils.onCommitListener() {
                        @Override
                        public void onCommit(String phoneCode, String emailCode, String googleCode) {
                            smsPhoneCode = phoneCode;
                            smsGoogleCode = googleCode;
                            loginPresenter.getLoginModule();

                        }
                    }, null, null);
            validationDialog.show();
        } else {
            DialogUtils.showDialog(this, R.drawable.errer_icon, data.getMsg());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (validationDialog != null) {
            validationDialog.dismiss();
        }
        if (LoginFailure && !isSwitchLoginType) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public String getChooseCountries() {
        return tvInternationalization.getText().toString();
    }

    @Override
    public String getPhone() {
        return etEmailPhone.getText().toString();
    }

    @Override
    public String getEamil() {
        return null;
    }

    @Override
    public String getPhoneAndEamil() {
        return etEmailPhone.getText().toString();
    }

    @Override
    public String getType() {
        return "1";
    }

    @Override
    public String getCode() {
        return null;
    }

    @Override
    public void setCheckPhoneSms(HttpBaseBean data) {
    }

    @Override
    public void setCheckEmailSms(HttpBaseBean data) {
    }

    /**
     * ----------------------------滑块方法
     */
    @Override
    public String getValidate() {
        return Validate;
    }

    @Override
    public void setfailure(String data) {
        ToastUtil.ShowToast(data);
    }

    @Override
    public void setSuccess(String data) {
        loginPresenter.getLoginModule();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!goLoginType.equals(AppConstants.LOGINPHONE)) {
            UMManager.onResume(this, UMConstant.LoginActivity_phone);
        } else {
            UMManager.onResume(this, UMConstant.LoginActivity_mail);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!goLoginType.equals(AppConstants.LOGINPHONE)) {
            UMManager.onPause(this, UMConstant.LoginActivity_phone);
        } else {
            UMManager.onPause(this, UMConstant.LoginActivity_mail);
        }
    }
}
