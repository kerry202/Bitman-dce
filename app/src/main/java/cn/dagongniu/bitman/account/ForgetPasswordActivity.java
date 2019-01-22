package cn.dagongniu.bitman.account;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.dagongniu.bitman.OAXApplication;
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.account.presenter.CheckEmailSmsPresenter;
import cn.dagongniu.bitman.account.presenter.CheckPhoneAndEmailPresenter;
import cn.dagongniu.bitman.account.presenter.CheckSmsPresenter;
import cn.dagongniu.bitman.account.presenter.SendEamilCodePresenter;
import cn.dagongniu.bitman.account.presenter.SendSmsPresenter;
import cn.dagongniu.bitman.account.view.CheckEmailPhoneView;
import cn.dagongniu.bitman.account.view.SendSmsView;
import cn.dagongniu.bitman.base.BaseActivity;
import cn.dagongniu.bitman.captcha.Captcha;
import cn.dagongniu.bitman.captcha.CaptchaListener;
import cn.dagongniu.bitman.captcha.OaxCaptcha;
import cn.dagongniu.bitman.captcha.CaptchaPresenter;
import cn.dagongniu.bitman.captcha.CaptchaTask;
import cn.dagongniu.bitman.captcha.ICaptchaView;
import cn.dagongniu.bitman.https.HttpBaseBean;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.utils.AccountValidatorUtil;
import cn.dagongniu.bitman.utils.AppConstants;
import cn.dagongniu.bitman.utils.DialogUtils;
import cn.dagongniu.bitman.utils.Logger;
import cn.dagongniu.bitman.utils.PhoneCodeUtils;
import cn.dagongniu.bitman.utils.ToastUtil;
import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

/**
 * 忘记密码
 */
public class ForgetPasswordActivity extends BaseActivity implements View.OnClickListener, SendSmsView, ICaptchaView, CheckEmailPhoneView {

    private static final String TAG = "ForgetPasswordActivity";

    @BindView(R.id.forgot_close)
    ImageView forgotClose;
    @BindView(R.id.forgot_title)
    TextView forgotTitle;
    @BindView(R.id.im_email_phone_icon)
    ImageView imEmailPhoneIcon;
    @BindView(R.id.tv_internationalization)
    TextView tvInternationalization;
    @BindView(R.id.rl_internationalization)
    LinearLayout rlInternationalization;
    @BindView(R.id.et_email_phone)
    EditText etEmailPhone;
    @BindView(R.id.im_x)
    ImageView imX;
    @BindView(R.id.im_icon_pwd)
    ImageView imIconPwd;
    @BindView(R.id.et_cede)
    EditText etCede;
    @BindView(R.id.tv_cede)
    TextView tvCode;
    @BindView(R.id.rl_forgot)
    TextView rlForgot;
    @BindView(R.id.tv_get_login)
    TextView tvGetForgot;

    String goLoginType = null;
    String LoginType = AppConstants.LOGINPHONE;
    Intent intent;
    private String dfShortName = "CN";//默认中国
    private PhoneNumberUtil utilPhoneNumber = null;
    Bundle bundle = new Bundle();
    String Validate = "";

    SendEamilCodePresenter sendEamilCodePresenter;
    SendSmsPresenter sendSmsPresenter;
    CheckSmsPresenter checkSmsPresenter;
    CheckEmailSmsPresenter checkEmailSmsPresenter;
    CaptchaPresenter captchaPresenter;
    CheckPhoneAndEmailPresenter checkPhoneAndEmailPresenter;

    OaxCaptcha mOaxCaptcha = null;
    private CaptchaTask mLoginTask = null;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_forget_password;
    }

    @Override
    protected void initView() {
        super.initView();
        rlForgot.setEnabled(false);
        initEvent();
        intent = getIntent();
        LoginType = intent.getStringExtra(AppConstants.LOGINTYPE);
        initLayout();
    }

    @Override
    protected void initData() {
        super.initData();
        sendEamilCodePresenter = new SendEamilCodePresenter(this, RequestState.STATE_REFRESH);
        sendSmsPresenter = new SendSmsPresenter(this, RequestState.STATE_REFRESH);
        checkSmsPresenter = new CheckSmsPresenter(this, RequestState.STATE_DIALOG);
        checkEmailSmsPresenter = new CheckEmailSmsPresenter(this, RequestState.STATE_DIALOG);
        captchaPresenter = new CaptchaPresenter(this, RequestState.STATE_DIALOG);
        checkPhoneAndEmailPresenter = new CheckPhoneAndEmailPresenter(this, RequestState.STATE_DIALOG);
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
                    imX.setVisibility(View.GONE);
                    etCede.setText("");
                } else {
                    imX.setVisibility(View.VISIBLE);
                }
                String cede = etCede.getText().toString();

                if (LoginType.equals(AppConstants.LOGINEAMIL)) {
                    if (AccountValidatorUtil.isEmail(etEmailPhone.getText().toString()) && cede.length() == 6) {
                        rlForgot.setBackgroundResource(R.drawable.white_circle3);
                        rlForgot.setTextColor(getResources().getColor(R.color.white));
                        rlForgot.setEnabled(true);
                    } else {
                        rlForgot.setBackgroundResource(R.drawable.noclick_circle_gray);
                        rlForgot.setEnabled(false);
                        rlForgot.setTextColor(getResources().getColor(R.color.text_str));
                    }
                    if (AccountValidatorUtil.isEmail(etEmailPhone.getText().toString())) {
                        tvCode.setEnabled(true);
                        tvCode.setBackgroundResource(R.drawable.white_circle3);
                        tvCode.setTextColor(getResources().getColor(R.color.white));
                    } else {
                        tvCode.setEnabled(false);
                        tvCode.setBackgroundResource(R.drawable.noclick_circle_gray);
                        tvCode.setTextColor(getResources().getColor(R.color.text_str));
                    }
                } else if (LoginType.equals(AppConstants.LOGINPHONE)) {
                    if (isPhoneNumber() && cede.length() == 6) {
                        rlForgot.setBackgroundResource(R.drawable.white_circle3);
                        rlForgot.setTextColor(getResources().getColor(R.color.white));
                        rlForgot.setEnabled(true);
                    } else {
                        rlForgot.setBackgroundResource(R.drawable.noclick_circle_gray);
                        rlForgot.setTextColor(getResources().getColor(R.color.text_str));
                        rlForgot.setEnabled(false);
                    }
                    if (isPhoneNumber()) {
                        tvCode.setEnabled(true);
                        tvCode.setBackgroundResource(R.drawable.white_circle3);
                        tvCode.setTextColor(getResources().getColor(R.color.white));
                    } else {
                        tvCode.setEnabled(false);
                        tvCode.setBackgroundResource(R.drawable.noclick_circle_gray);
                        tvCode.setTextColor(getResources().getColor(R.color.text_str));
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

        etCede.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (LoginType.equals(AppConstants.LOGINEAMIL)) {
                    if (AccountValidatorUtil.isEmail(etEmailPhone.getText().toString()) && s.length() == 6) {
                        rlForgot.setBackgroundResource(R.drawable.white_circle3);
                        rlForgot.setTextColor(getResources().getColor(R.color.white));
                        rlForgot.setEnabled(true);
                    } else {
                        rlForgot.setBackgroundResource(R.drawable.noclick_circle_gray);
                        rlForgot.setTextColor(getResources().getColor(R.color.text_str));
                        rlForgot.setEnabled(false);
                    }
                } else if (LoginType.equals(AppConstants.LOGINPHONE)) {
                    if (isPhoneNumber() && s.length() == 6) {
                        rlForgot.setBackgroundResource(R.drawable.white_circle3);
                        rlForgot.setTextColor(getResources().getColor(R.color.white));
                        rlForgot.setEnabled(true);
                    } else {
                        rlForgot.setBackgroundResource(R.drawable.noclick_circle_gray);
                        rlForgot.setTextColor(getResources().getColor(R.color.text_str));
                        rlForgot.setEnabled(false);
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
            Phonenumber.PhoneNumber phoneNumber = utilPhoneNumber.parse(etEmailPhone.getText().toString(), dfShortName);
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
            initializeData(R.string.forgot_email_show,
                    getResources().getDrawable(R.mipmap.emali_icon), true, R.string.login_email_hint,
                    R.string.forgot_phone_show, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            goLoginType = AppConstants.LOGINPHONE;
        } else if (LoginType.equals(AppConstants.LOGINPHONE)) {
            initializeData(R.string.forgot_phone_show,
                    getResources().getDrawable(R.mipmap.phone_icon), false, R.string.login_phone_hint,
                    R.string.forgot_email_show, InputType.TYPE_CLASS_PHONE);
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
        forgotTitle.setText(titleName);
        imEmailPhoneIcon.setImageDrawable(emailPhoneIcon);
        if (isRlInternationalization)
            rlInternationalization.setVisibility(View.GONE);
        else
            rlInternationalization.setVisibility(View.VISIBLE);
        etEmailPhone.setHint(hintName);
        tvGetForgot.setText(goLogin);
        etEmailPhone.setInputType(textType);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


    @OnClick({R.id.forgot_close, R.id.im_x, R.id.tv_get_login, R.id.tv_cede, R.id.rl_forgot, R.id.rl_internationalization})
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.forgot_close:
                finish();
                break;
            case R.id.im_x:
                etEmailPhone.setText("");
                imX.setVisibility(View.GONE);
                break;
            case R.id.tv_get_login:
                finish();
                bundle.putString(AppConstants.LOGINTYPE, goLoginType);
                openActivity(ForgetPasswordActivity.class, bundle);
                break;
            case R.id.tv_cede://获取验证码
                if (LoginType.equals(AppConstants.LOGINEAMIL)) {
                    checkPhoneAndEmailPresenter.getCheckEmailModule();
                } else if (LoginType.equals(AppConstants.LOGINPHONE)) {
                    checkPhoneAndEmailPresenter.getCheckPhoneModule();
                }
                break;
            case R.id.rl_forgot://下一步
                toNext();
                break;
            case R.id.rl_internationalization://国际区号
                Intent intentCC = new Intent(this, ChooseCountriesActivity.class);
                startActivityForResult(intentCC, 1);
                break;
        }
    }

    /**
     * 下一步
     */
    private void toNext() {

        if (LoginType.equals(AppConstants.LOGINEAMIL)) {
            //效验邮箱验证码
            checkEmailSmsPresenter.getCheckEmailSmsModule();
        } else if (LoginType.equals(AppConstants.LOGINPHONE)) {
            //效验手机验证码
            checkSmsPresenter.getCheckSmsModule();
        }

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
                    //ToastUtil.ShowToast(ForgetPasswordActivity.this.getResources().getString(R.string.captcha_successful));
                    Logger.e(TAG, "验证成功：result = " + result + ", validate = " + validate + ", message = " + message);
                } else {
                    ToastUtil.ShowToast(ForgetPasswordActivity.this.getResources().getString(R.string.captcha_failure));
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
     * 获取验证码
     */
    public void getCodeSms() {
        if (LoginType.equals(AppConstants.LOGINEAMIL)) {
            if (!etEmailPhone.getText().toString().equals("")) {
                if (AccountValidatorUtil.isEmail(etEmailPhone.getText().toString())) {
                    PhoneCodeUtils.getInstance().setDownTime(mContext, 60, tvCode);
                    sendEamilCodePresenter.getSendEamilCodeModule();
                } else {
                    ToastUtil.ShowToast(this.getResources().getString(R.string.registered_eamil_errer));
                }
            } else {
                ToastUtil.ShowToast(this.getResources().getString(R.string.login_email_hint));
            }

        } else if (LoginType.equals(AppConstants.LOGINPHONE)) {
            if (!etEmailPhone.getText().toString().equals("")) {
                if (isPhoneNumber()) {
                    PhoneCodeUtils.getInstance().setDownTime(mContext, 60, tvCode);
                    OAXApplication.bitmanloginstate=1;
                    sendSmsPresenter.getSendSmsModule1("5");
                } else {
                    ToastUtil.ShowToast(this.getResources().getString(R.string.registered_phone_errer));
                }
            } else {
                ToastUtil.ShowToast(this.getResources().getString(R.string.login_phone_hint));
            }
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
        return etEmailPhone.getText().toString();
    }

    @Override
    public String getPhoneAndEamil() {
        return etEmailPhone.getText().toString();
    }

    @Override
    public String getType() {
        return AppConstants.SMS_REGISTERED;
    }

    @Override
    public String getCode() {
        return etCede.getText().toString();
    }

    /**
     * 手机短信验证码检查回调
     *
     * @param data
     */
    @Override
    public void setCheckPhoneSms(HttpBaseBean data) {
        if (data.isSuccess()) {
            bundle.putString(AppConstants.LOGINTYPE, AppConstants.LOGINPHONE);
            bundle.putString(AppConstants.REGISTERED_USERNAME, tvInternationalization.getText().toString() + etEmailPhone.getText().toString());
            if (LoginType.equals(AppConstants.LOGINEAMIL)) {
                bundle.putString(AppConstants.REGISTERED_TYPE, "2");
            } else if (LoginType.equals(AppConstants.LOGINPHONE)) {
                bundle.putString(AppConstants.REGISTERED_TYPE, "1");
            }
            openActivity(SetPasswordActivity.class, bundle);
        } else {
            DialogUtils.showDialog(this, R.drawable.errer_icon, data.getMsg());
        }
    }

    /**
     * 邮箱验证码检查回调
     *
     * @param data
     */
    @Override
    public void setCheckEmailSms(HttpBaseBean data) {
        if (data.isSuccess()) {
            bundle.putString(AppConstants.LOGINTYPE, AppConstants.LOGINEAMIL);
            bundle.putString(AppConstants.REGISTERED_USERNAME, etEmailPhone.getText().toString());
            if (LoginType.equals(AppConstants.LOGINEAMIL)) {
                bundle.putString(AppConstants.REGISTERED_TYPE, "2");
            } else if (LoginType.equals(AppConstants.LOGINPHONE)) {
                bundle.putString(AppConstants.REGISTERED_TYPE, "1");
            }
            openActivity(SetPasswordActivity.class, bundle);
        } else {
            DialogUtils.showDialog(this, R.drawable.errer_icon, data.getMsg());
        }
    }

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
        getCodeSms();
    }

    /**
     * 检查手机邮箱是否存在方法 ----------------------
     */

    @Override
    public void setCheckEmailSuccess(HttpBaseBean data) {
        if (!data.isSuccess()) {
            startCaptcha();
        } else {
            DialogUtils.showDialog(this, R.drawable.errer_icon, getContext().getResources().getString(R.string.show_hint_email_thereisno));
        }
    }

    @Override
    public void setCheckEmailFailure(String data) {
        DialogUtils.showDialog(this, R.drawable.errer_icon, data);
    }

    @Override
    public void setCheckPhoneSuccess(HttpBaseBean data) {
        if (!data.isSuccess()) {
            startCaptcha();
        } else {
            DialogUtils.showDialog(this, R.drawable.errer_icon, getContext().getResources().getString(R.string.show_hint_phone_thereisno));
        }
    }

    @Override
    public void setCheckPhoneFailure(String data) {
        DialogUtils.showDialog(this, R.drawable.errer_icon, data);
    }

}
