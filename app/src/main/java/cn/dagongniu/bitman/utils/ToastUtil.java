package cn.dagongniu.bitman.utils;


import android.app.Activity;
import android.view.Gravity;
import android.widget.Toast;

import cn.dagongniu.bitman.OAXApplication;


/**
 * Toast 工具
 */
public class ToastUtil {

    public static final String TAG = "ToastUtil";

    public static void ShowToast(String message) {
        if (null != OAXApplication.sContext) {
            Toast toast = Toast.makeText(OAXApplication.sContext, "" + message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            Logger.d(TAG, "" + message);
        }
    }

    public static void ShowToast(String message, Activity activity) {
        try {
            if (null != activity) {
                Toast toast = Toast.makeText(activity, "" + message, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

            } else {
                Logger.d(TAG, "" + message);
            }
        } catch (Exception e) {
            //解决在子线程中调用Toast的异常情况处理
        }
    }

}
