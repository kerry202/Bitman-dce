package cn.dagongniu.bitman.assets;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import cn.dagongniu.bitman.OAXApplication;
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.account.presenter.SendEamilCodePresenter;
import cn.dagongniu.bitman.account.presenter.SendSmsPresenter;
import cn.dagongniu.bitman.account.view.SendSmsView;
import cn.dagongniu.bitman.assets.bean.QueryCheckTypeBean;
import cn.dagongniu.bitman.assets.presenter.CoinAddressAddPresenter;
import cn.dagongniu.bitman.assets.presenter.QueryCheckTypePresenter;
import cn.dagongniu.bitman.assets.view.ICoinAddressView;
import cn.dagongniu.bitman.assets.view.IQueryCheckTypeView;
import cn.dagongniu.bitman.base.BaseActivity;
import cn.dagongniu.bitman.constant.SPConstant;
import cn.dagongniu.bitman.customview.MyTradingToolbar;
import cn.dagongniu.bitman.https.HttpBaseBean;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.utils.AccountValidatorUtil;
import cn.dagongniu.bitman.utils.DialogUtils;
import cn.dagongniu.bitman.utils.Logger;
import cn.dagongniu.bitman.utils.Logs;
import cn.dagongniu.bitman.utils.SPUtils;
import cn.dagongniu.bitman.utils.ToastUtil;
import cn.dagongniu.bitman.utils.events.MyEvents;
import cn.dagongniu.bitman.zxing.MNScanManager;
import cn.dagongniu.bitman.zxing.other.MNScanCallback;

/**
 * 新增地址
 */
public class ADDAddressActivity extends BaseActivity implements View.OnClickListener, IQueryCheckTypeView, SendSmsView, ICoinAddressView {

    private static final String TAG = "ADDAddressActivity";

    @BindView(R.id.add_toolbar)
    MyTradingToolbar toolbar;
    @BindView(R.id.bt_commit)
    Button btCommit;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.et_adderss)
    EditText etAdderss;
    @BindView(R.id.et_note)
    EditText etNote;
    @BindView(R.id.qr_scan_iv)
    TextView qr_scan_iv;

    String MarketName = null;
    String MarketId = null;
    int Type = -1;//type  ;//  类型 1 ETH 2 BTC 3 ETH代币    是    [number]
    QueryCheckTypePresenter queryCheckTypePresenter;
    Intent intent;
    String accountName = "";
    String accountEmail = "";
    String accountPhone = "";
    String userChooseCountries = "";
    SendSmsPresenter sendSmsPresenter;
    SendEamilCodePresenter sendEamilCodePresenter;
    CoinAddressAddPresenter coinAddressAddPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_addaddress;
    }

    private void initToobar() {
        toolbar.setRightImgVisibility(true);
        toolbar.setTvLeftVisibility(true);
        toolbar.setSjVisibility(true);
        toolbar.setTitleNameText(R.string.assets_add_address);
        toolbar.setRightTvVisibility(true);
        toolbar.setLeftMoreClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initView() {
        super.initView();
        intent = this.getIntent();
        accountName = (String) SPUtils.getParam(this, SPConstant.USER_ACCOUNT, "");
        accountEmail = (String) SPUtils.getParam(this, SPConstant.USER_EMAIL, "");
        accountPhone = (String) SPUtils.getParam(this, SPConstant.USER_PHONE, "");
        userChooseCountries = (String) SPUtils.getParam(this, SPConstant.USER_CHOOSECOUNTRIES, "");
        MarketName = OAXApplication.getInstance().MarketName;
        MarketId = OAXApplication.getInstance().MarketId;
        Type = OAXApplication.getInstance().type;
        tvName.setText(MarketName);
        initToobar();
        initEvent();
        queryCheckTypePresenter = new QueryCheckTypePresenter(this, RequestState.STATE_DIALOG);
        sendSmsPresenter = new SendSmsPresenter(this, RequestState.STATE_REFRESH);
        sendEamilCodePresenter = new SendEamilCodePresenter(this, RequestState.STATE_REFRESH);
        coinAddressAddPresenter = new CoinAddressAddPresenter(this, RequestState.STATE_DIALOG);
        btCommit.setClickable(false);


        qr_scan_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MNScanManager.startScan(ADDAddressActivity.this, new MNScanCallback() {
                    @Override
                    public void onActivityResult(int resultCode, Intent data) {
                        handlerResult(resultCode, data);
                    }
                });

            }
        });
    }

    private void handlerResult(int resultCode, Intent data) {

        if (data == null) {
            return;
        }

        if (Build.VERSION.SDK_INT > 22) {
            if (ContextCompat.checkSelfPermission(mContext,
                    android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //先判断有没有权限 ，没有就在这里进行权限的申请
                ActivityCompat.requestPermissions(mContext,
                        new String[]{android.Manifest.permission.CAMERA}, 0);


            } else {
                //说明已经获取到摄像头权限了 想干嘛干嘛
            }
        } else {
            //这个说明系统版本在6.0之下，不需要动态获取权限。

        }

        switch (resultCode) {
            case MNScanManager.RESULT_SUCCESS:
                String resultSuccess = data.getStringExtra(MNScanManager.INTENT_KEY_RESULT_SUCCESS);
                Logs.s("    resultSuccess  " + resultSuccess);
                etAdderss.setText(resultSuccess);
                break;
            case MNScanManager.RESULT_FAIL:
                String resultError = data.getStringExtra(MNScanManager.INTENT_KEY_RESULT_ERROR);

                break;
            case MNScanManager.RESULT_CANCLE:

                break;
        }
    }


    /**
     * edt 改变事件
     */
    private void initEvent() {
        btCommit.setOnClickListener(this);
        etAdderss.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String AddStr = s.toString();
                switch (Type) {
                    //1 ETH
                    case 1:
                    case 3:
                        if (AccountValidatorUtil.isETHAdd(AddStr) && AddStr.length() > 0) {
                            btCommit.setBackgroundResource(R.drawable.white_circle3);
                            btCommit.setTextColor(getResources().getColor(R.color.white));
                            btCommit.setClickable(true);
                        } else {
                            btCommit.setBackgroundResource(R.drawable.noclick_circle_gray);
                            btCommit.setTextColor(getResources().getColor(R.color.text_str));
                            btCommit.setClickable(false);
                        }
                        break;
                    // 2 BTC
                    case 2:
                        if (AddStr.length() >= 26 && AddStr.length() <= 35) {
                            btCommit.setBackgroundResource(R.drawable.white_circle3);
                            btCommit.setTextColor(getResources().getColor(R.color.white));
                            btCommit.setClickable(true);
                        } else {
                            btCommit.setBackgroundResource(R.drawable.noclick_circle_gray);
                            btCommit.setTextColor(getResources().getColor(R.color.text_str));
                            btCommit.setClickable(false);
                        }
                        break;
                    default:
                        if (!TextUtils.isEmpty(AddStr) && AddStr.length() > 0) {
                            btCommit.setBackgroundResource(R.drawable.white_circle3);
                            btCommit.setTextColor(getResources().getColor(R.color.white));
                            btCommit.setClickable(true);
                        } else {
                            btCommit.setBackgroundResource(R.drawable.noclick_circle_gray);
                            btCommit.setTextColor(getResources().getColor(R.color.text_str));
                            btCommit.setClickable(false);
                        }
                        break;
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
        //失去焦点事件
        etAdderss.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                } else {
                    // 此处为失去焦点时的处理内容
                    String AddStr = etAdderss.getText().toString();
                    switch (Type) {
                        //1 ETH
                        case 1:
                        case 3:
                            if (!AccountValidatorUtil.isETHAdd(AddStr)) {
                                ToastUtil.ShowToast(ADDAddressActivity.this.getResources().getString(R.string.address_error), ADDAddressActivity.this);
                            }
                            break;
                        // 2 BTC
                        case 2:
                            if (AddStr.length() >= 26 && AddStr.length() <= 35) {
                            } else {
                                ToastUtil.ShowToast(ADDAddressActivity.this.getResources().getString(R.string.address_error), ADDAddressActivity.this);
                            }
                            break;
                        default:
                            if (TextUtils.isEmpty(AddStr)) {
                                ToastUtil.ShowToast(ADDAddressActivity.this.getResources().getString(R.string.address_error), ADDAddressActivity.this);
                            }
                            break;
                    }
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_commit:
                queryCheckTypePresenter.getQueryCheckTypeModule();
                //initDialog();
                break;
        }

    }

    @Override
    public String getUsername() {
        return accountName;
    }

    @Override
    public void setQueryCheckTypeData(QueryCheckTypeBean queryCheckTypeData) {
        boolean isEmail = true;
        //1 需要 0不需要
        if (queryCheckTypeData.getData().getEmailStatus() == 1) {
            isEmail = false;
        }

        boolean isGoogle = true;
        if (queryCheckTypeData.getData().getGoogleStatus() == 1) {
            isGoogle = false;
        }
        boolean isPhone = true;
        if (queryCheckTypeData.getData().getPhoneStatus() == 1) {
            isPhone = false;
        }

        showDialog(isEmail, isPhone, isGoogle);

    }

    Dialog validationDialog = null;
    String smsPhoneCode = null;
    String smsGoogleCode = null;
    String smsEamilCode = null;

    public void showDialog(boolean isEmail, boolean isPhone, boolean isGoogle) {

        String accountPhoneStr = "";
        if (!TextUtils.isEmpty(accountPhone)) {
            if (accountPhone.length() > 4) {
                accountPhoneStr = "******" + accountPhone.substring(accountPhone.length() - 4);
            }
        }

        String emailStr = "";
        if (AccountValidatorUtil.isEmail(accountEmail.toString())) {
            int indexStart = accountEmail.indexOf("@");
            String toStr = accountEmail.substring(0, indexStart);
            if (toStr.length() > 4) {
                emailStr = "****" + accountEmail.substring(indexStart - 4, accountEmail.length());
            } else {
                emailStr = accountEmail;
            }
        } else {
            if (!TextUtils.isEmpty(accountEmail)) {
                emailStr = "****" + accountEmail.substring(accountEmail.length() - 4);
            }

        }

        validationDialog = DialogUtils.getValidationDialog(this, isEmail, isPhone, isGoogle,
                emailStr, accountPhoneStr,
                new DialogUtils.onCommitListener() {
                    @Override
                    public void onCommit(String phoneCode, String emailCode, String googleCode) {
                        smsPhoneCode = phoneCode;
                        smsGoogleCode = googleCode;
                        smsEamilCode = emailCode;
                        coinAddressAddPresenter.getCoinAddressAddModule();
                    }
                }, new DialogUtils.onClickGetEmailListener() {
                    @Override
                    public void onClickGetEmail() {
                        sendEamilCodePresenter.getSendEamilCodeModule();
                    }
                }, new DialogUtils.onClickGetPhoneListener() {
                    @Override
                    public void onClickPhone() {
                        sendSmsPresenter.getSendSmsModule(accountPhone);
                    }
                });
        validationDialog.show();
    }

    @Override
    public String getChooseCountries() {
        return userChooseCountries;
    }


    @Override
    public String getPhone() {
        return accountName;
    }

    public String getEamil() {
        return accountEmail;
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
    public String getPhoneAndEamil() {
        return null;
    }

    @Override
    public void setCheckPhoneSms(HttpBaseBean data) {

    }

    @Override
    public void setCheckEmailSms(HttpBaseBean data) {

    }

    @Override
    public String getCoinId() {
        return MarketId;
    }

    @Override
    public String getAddress() {
        return etAdderss.getText().toString();
    }

    @Override
    public String getRemark() {
        return etNote.getText().toString();
    }

    /**
     * 添加成功 通知刷新列表
     */
    @Override
    public void isSuccess() {
        //发送登录成功通知
        ToastUtil.ShowToast(this.getResources().getString(R.string.add_withdrawal_adderss_success));
        myEvents.status = MyEvents.status_ok;
        myEvents.status_type = MyEvents.Add_Withdrawal_Adderss_Success;
        myEvents.errmsg = this.getResources().getString(R.string.add_withdrawal_adderss_success);
        eventBus.post(myEvents);
        Logger.e(TAG, "发送新增地址成功的通知!");
        validationDialog.dismiss();
        finish();

    }

    @Override
    public void goLogin(String msg) {
        ToastUtil.ShowToast(msg);
    }

    @Override
    public String getSmsCode() {
        return smsPhoneCode;
    }

    @Override
    public String getEmailCode() {
        return smsEamilCode;
    }

    @Override
    public String getGoogleCode() {
        return smsGoogleCode;
    }
}
