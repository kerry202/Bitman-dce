package cn.dagongniu.bitman.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.HashMap;

import cn.dagongniu.bitman.OAXApplication;
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.constant.SPConstant;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.HttpUtils;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.my_ui.CradBean;
import cn.dagongniu.bitman.my_ui.MyTopupCradActivity;

/**
 * dialog工具类
 */
public class DialogUtils {


    private static Dialog dialog1;
    private static String userId;

    /**
     * 方法一：
     * setCanceledOnTouchOutside(false);调用这个方法时，按对话框以外的地方不起作用。按返回键还起作用
     * 方法二：
     * setCanceleable(false);调用这个方法时，按对话框以外的地方不起作用。按返回键也不起作用
     */

    public interface onCommitListener {
        void onCommit(String phoneCode, String emailCode, String googleCode);
    }

    public interface onClickGetEmailListener {
        void onClickGetEmail();
    }

    public interface onClickGetPhoneListener {
        void onClickPhone();
    }

    public interface OnSureListener {
        void onSure();
    }


    /**
     * 验证
     *
     * @param context
     * @param isEmail
     * @param isPhone
     * @param isGoogle
     * @param email
     * @param phone
     * @param onCommitListener
     * @param clickGetEmailListener
     * @param clickGetPhoneListener
     * @return
     */
    public static Dialog getValidationDialog(Context context, boolean isEmail, boolean isPhone, boolean isGoogle,
                                             String email, String phone,
                                             final onCommitListener onCommitListener, final onClickGetEmailListener clickGetEmailListener,
                                             final onClickGetPhoneListener clickGetPhoneListener) {
        View view = LayoutInflater.from(context).inflate(R.layout.validation_layout_dialog, null);

        Logs.s("      安全验证   ：rlEmail " + isEmail);
        Logs.s("      安全验证   ：isPhone " + isPhone);
        Logs.s("      安全验证   ：isGoogle " + isGoogle);
        LinearLayout rlEmail = view.findViewById(R.id.rl_email);
        LinearLayout rlPhone = view.findViewById(R.id.rl_phone);
        LinearLayout rlGoogle = view.findViewById(R.id.rl_google);

        //隐藏显示
        rlEmail.setVisibility(isEmail ? View.GONE : View.VISIBLE);
        rlPhone.setVisibility(isPhone ? View.GONE : View.VISIBLE);
        rlGoogle.setVisibility(isGoogle ? View.GONE : View.VISIBLE);

        TextView tvEmail = view.findViewById(R.id.tv_email);
        tvEmail.setText(email);
        TextView tvPhone = view.findViewById(R.id.tv_phone);
        tvPhone.setText(phone);

        final EditText etEmail = view.findViewById(R.id.et_email);
        final EditText etPhone = view.findViewById(R.id.et_phone);
        final EditText etGoogle = view.findViewById(R.id.et_google);

        final TextView tv_email_get_code = view.findViewById(R.id.tv_email_get_code);
        final TextView tv_phone_get_code = view.findViewById(R.id.tv_phone_get_code);

        final Dialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(true)
                .create();

        //提交
        Button commit = view.findViewById(R.id.bt_commit);
        commit.setClickable(false);
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCommitListener.onCommit(etPhone.getText().toString(), etEmail.getText().toString(), etGoogle.getText().toString());
            }
        });
        //邮箱
        final TextView rlGetEamil = view.findViewById(R.id.tv_email_get_code);
        rlGetEamil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneCodeUtils.getInstance().setDialogDownTime(context,60,  tv_email_get_code);
                clickGetEmailListener.onClickGetEmail();
            }
        });
        //手机号码
        final TextView rlGetPhone = view.findViewById(R.id.tv_phone_get_code);
        rlGetPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneCodeUtils.getInstance().setDialogDownTime(context,60,  tv_phone_get_code);
                clickGetPhoneListener.onClickPhone();
            }
        });
        commit.setEnabled(false);
        //提交按钮有效
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (CheckComitClickable(isEmail, isPhone, isGoogle, etEmail, etPhone, etGoogle)) {
                    commit.setBackgroundResource(R.drawable.white_circle3);
                    commit.setTextColor(context.getResources().getColor(R.color.white));
                    commit.setEnabled(true);
                } else {
                    commit.setBackgroundResource(R.drawable.noclick_circle_gray);
                    commit.setEnabled(false);
                    commit.setTextColor(context.getResources().getColor(R.color.text_str));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (CheckComitClickable(isEmail, isPhone, isGoogle, etEmail, etPhone, etGoogle)) {
                    commit.setBackgroundResource(R.drawable.white_circle3);
                    commit.setTextColor(context.getResources().getColor(R.color.white));
                    commit.setEnabled(true);
                } else {
                    commit.setBackgroundResource(R.drawable.noclick_circle_gray);
                    commit.setTextColor(context.getResources().getColor(R.color.text_str));
                    commit.setEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        etGoogle.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (CheckComitClickable(isEmail, isPhone, isGoogle, etEmail, etPhone, etGoogle)) {
                    commit.setBackgroundResource(R.drawable.white_circle3);
                    commit.setEnabled(true);
                    commit.setTextColor(context.getResources().getColor(R.color.white));
                } else {
                    commit.setBackgroundResource(R.drawable.noclick_circle_gray);
                    commit.setTextColor(context.getResources().getColor(R.color.text_str));
                    commit.setEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    /**
     * 检测是否有效。 验证码 commit按钮是否可点击
     *
     * @param isEmail
     * @param isPhone
     * @param isGoogle
     * @param etEmail
     * @param etPhone
     * @param etGoogle
     * @return
     */
    public static boolean CheckComitClickable(boolean isEmail, boolean isPhone, boolean isGoogle, EditText etEmail, EditText etPhone, EditText etGoogle) {
        boolean isCheckCommit = false;

        String emailStr = etEmail.getText().toString();
        String phoneStr = etPhone.getText().toString();
        String googleStr = etGoogle.getText().toString();

        //三种存在的情况
        if (!isEmail && !isPhone && !isGoogle) {
            if (emailStr.length() == 6 && phoneStr.length() == 6 && googleStr.length() == 6) {
                return true;
            } else {
                return false;
            }
        } else
            //邮箱跟手机
            if (!isEmail && !isPhone) {
                if (emailStr.length() == 6 && phoneStr.length() == 6) {
                    return true;
                } else {
                    return false;
                }
            } else
                //手机跟google
                if (!isPhone && !isGoogle) {
                    if (phoneStr.length() == 6 && googleStr.length() == 6) {
                        return true;
                    } else {
                        return false;
                    }
                } else
                    //google跟邮箱
                    if (!isGoogle && !isEmail) {
                        if (googleStr.length() == 6 && emailStr.length() == 6) {
                            return true;
                        } else {
                            return false;
                        }
                    } else
                        //邮箱
                        if (!isEmail) {
                            if (emailStr.length() == 6) {
                                return true;
                            } else {
                                return false;
                            }
                        } else
                            //手机
                            if (!isPhone) {
                                if (phoneStr.length() == 6) {
                                    return true;
                                } else {
                                    return false;
                                }
                            } else
                                //google
                                if (!isGoogle) {
                                    if (googleStr.length() == 6) {
                                        return true;
                                    } else {
                                        return false;
                                    }
                                }

        return isCheckCommit;
    }

    /**
     * 验证Touch ID
     *
     * @param context
     * @return
     */
    public static Dialog getVerifyTouchIDDialog(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_verify_touchid, null);
        final Dialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(true)
                .create();
        TextView cancel = view.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    /**
     * @param activity
     * @param cradBean
     * @param onSureListener
     * @return
     */
    public static Dialog getCardDialog(MyTopupCradActivity activity, String trim, CradBean cradBean, final OnSureListener onSureListener) {
        View view = LayoutInflater.from(activity).inflate(R.layout.crad_dialog, null);
        dialog1 = new AlertDialog.Builder(activity, R.style.MyCradDialogStyle)
                .setView(view)
                .setCancelable(false)
                .create();
        dialog1.show();

        Window win = dialog1.getWindow();
        win.getDecorView().setPadding(50, 0, 50, 0);
        TextView bi_name = view.findViewById(R.id.bi_name);
        TextView amout_tv = view.findViewById(R.id.amout_tv);
        TextView time_tv = view.findViewById(R.id.time_tv);
        Button cancel_tv = view.findViewById(R.id.cancel_tv);
        Button cenvert_tv = view.findViewById(R.id.cenvert_tv);
        TextView dialog_title = view.findViewById(R.id.dialog_title);

        bi_name.setText(cradBean.data.get(0).coinName);
        amout_tv.setText(cradBean.data.get(0).denomination);
        time_tv.setText(cradBean.data.get(0).expirationTime);

        if (cradBean.code != 0) {
            view.findViewById(R.id.rl1).setVisibility(View.GONE);
            view.findViewById(R.id.rl2).setVisibility(View.GONE);
            view.findViewById(R.id.rl3).setVisibility(View.GONE);
            view.findViewById(R.id.rl4).setVisibility(View.GONE);
            dialog_title.setText(cradBean.msg);
            cancel_tv.setText(activity.getResources().getString(R.string.redempion_str));
            cenvert_tv.setText(activity.getResources().getString(R.string.cancel));
            
            cancel_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog1.dismiss();
                }
            });

            cenvert_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog1.dismiss();
                }
            });

        } else {

            cancel_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog1.dismiss();
                }
            });

            cenvert_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    cenvertCrad(activity, trim);
                }
            });
        }

        return dialog1;
    }

    private static void cenvertCrad(MyTopupCradActivity activity, String trim) {

        HashMap<String, String> map = new HashMap<>();
        userId = SPUtils.getParamString(activity, SPConstant.USER_ID, null);
        String userToken = (String) SPUtils.getParam(activity, SPConstant.USER_TOKEN, "");
        map.put("userId", userId);
        map.put("cdkey", trim);
        map.put("token", userToken);

        HttpUtils.getInstance().postLang(Http.detils2, map, activity, new OnBaseDataListener<String>() {
            @Override
            public void onNewData(String data) {
                Logs.s("  充值卡 兑换 1  " + data);
                DebugUtils.prinlnLog(data);
                dialog1.dismiss();
                CradBean cradBean = new Gson().fromJson(data, CradBean.class);
                Logs.s("  充值卡 兑换 2  " + cradBean);
                if (cradBean.code == 1003) {
                    shouDialog(cradBean, activity);
                } else if (cradBean.code == 0) {
                    shouDialog(cradBean, activity);
                } else {
                    ToastUtil.ShowToast(cradBean.msg);
                }
            }

            @Override
            public void onError(String code) {
                dialog1.dismiss();
            }
        }, RequestState.STATE_REFRESH);
    }

    private static void shouDialog(CradBean cradBean, MyTopupCradActivity activity) {
        View view = LayoutInflater.from(activity).inflate(R.layout.crad_dialog, null);
        final Dialog dialog = new AlertDialog.Builder(activity, R.style.MyCradDialogStyle)
                .setView(view)
                .setCancelable(false)
                .create();
        dialog.show();

        Window win = dialog.getWindow();
        win.getDecorView().setPadding(50, 0, 50, 0);

        view.findViewById(R.id.rl1).setVisibility(View.GONE);
        view.findViewById(R.id.rl2).setVisibility(View.GONE);
        view.findViewById(R.id.rl4).setVisibility(View.GONE);
        view.findViewById(R.id.rl3).setVisibility(View.GONE);
        TextView dialog_title = view.findViewById(R.id.dialog_title);
        dialog_title.setText(activity.getResources().getString(R.string.successful_str) + cradBean.data.get(0).denomination + cradBean.data.get(0).coinName + activity.getResources().getString(R.string.go_asset));
        Button cancel_tv = view.findViewById(R.id.cancel_tv);
        Button cenvert_tv = view.findViewById(R.id.cenvert_tv);

        if (cradBean.code == 0) {

            cancel_tv.setText(activity.getResources().getString(R.string.entrust_order_ok));
            cenvert_tv.setText(activity.getResources().getString(R.string.go_asset2));
            cancel_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            cenvert_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.finish();
                    OAXApplication.state = 3;
                }
            });

        } else {
            cancel_tv.setText(activity.getResources().getString(R.string.redempion_str));
            cenvert_tv.setText(activity.getResources().getString(R.string.cancel));
//            cenvert_tv.setTextColor(activity.getResources().getColor(R.color.black));
//            cenvert_tv.setBackgroundColor(activity.getResources().getColor(R.color.white));
            cancel_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            cenvert_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }


    }

    /**
     * 获取 确认/取消 dialog
     *
     * @param context
     * @param des
     * @param onSureListener
     * @return
     */
    public static Dialog getSureAndCancelDialog(Context context, int des, final OnSureListener onSureListener) {
        View view = LayoutInflater.from(context).inflate(R.layout.sure_and_cancel_dialog, null);
        final Dialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(true)
                .create();
        TextView cancel = view.findViewById(R.id.tv_cancel);
        TextView sure = view.findViewById(R.id.tv_sure);
        TextView content = view.findViewById(R.id.tv_content);
        RelativeLayout rl_cancel = view.findViewById(R.id.rl_cancel);
        RelativeLayout rl_sure = view.findViewById(R.id.rl_sure);
        content.setText(content.getResources().getString(des));

        rl_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        rl_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSureListener.onSure();
            }
        });
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    public static Dialog getRemindDialog(Context context, int title, int des, int sureDes) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_remind, null);
        final Dialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(true)
                .create();
        TextView tvTitle = view.findViewById(R.id.tv_title);
        TextView sure = view.findViewById(R.id.tv_sure);
        TextView content = view.findViewById(R.id.tv_content);

        tvTitle.setText(context.getResources().getString(title));
        content.setText(content.getResources().getString(des));
        content.setText(context.getResources().getString(des));

        sure.setText(context.getResources().getString(sureDes));
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }


    /**
     * 第三方 弹框 img加text
     *
     * @param context
     * @param img
     * @param showStirng
     */
    public static void showDialog(Context context, int img, String showStirng) {
        final KProgressHUD hud;
        ImageView imageView = new ImageView(context);
        imageView.setBackgroundResource(img);//R.drawable.correct

        hud = KProgressHUD.create(context)
                .setCustomView(imageView)
                .setAnimationSpeed(200)
                .setLabel(showStirng)//R.string.assets_withdrawal_successful
                .show();
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hud.dismiss();
            }
        }, 1500);

    }

    /**
     * 第三方 弹框
     *
     * @param context
     * @param showStirng
     */
    public static void showTextDialog(Context context, String showStirng) {
        final KProgressHUD hud;
        ImageView imageView = new ImageView(context);

        hud = KProgressHUD.create(context)
                .setCustomView(imageView)
                .setAnimationSpeed(200)
                .setLabel(showStirng)//R.string.assets_withdrawal_successful
                .show();
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hud.dismiss();
            }
        }, 1500);

    }


    /**
     * 等级介绍
     *
     * @param context
     * @return
     */
    public static Dialog getLvDialog(Context context) {
        String lv1 = (String) SPUtils.getParam(context, SPConstant.LEVEL1_BTC, "0");
        String lv2 = (String) SPUtils.getParam(context, SPConstant.LEVEL2_BTC, "0");

        View view = LayoutInflater.from(context).inflate(R.layout.lv_layout_dialog, null);
        TextView tvTitle = view.findViewById(R.id.tv_title);

        TextView tvLv1 = view.findViewById(R.id.tv_lv1);
        TextView tvLv2 = view.findViewById(R.id.tv_lv2);

        String tvLv1Str = String.format(context.getResources().getString(R.string.lv_1), lv1);
        String tvLv2Str = String.format(context.getResources().getString(R.string.lv_2), lv2);

        tvLv1.setText(tvLv1Str);
        tvLv2.setText(tvLv2Str);

        RelativeLayout rlGo = view.findViewById(R.id.rl_go);

        final Dialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(true)
                .create();


        rlGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }
}
