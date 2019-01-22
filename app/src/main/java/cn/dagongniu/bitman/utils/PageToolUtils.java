package cn.dagongniu.bitman.utils;

import android.content.ClipboardManager;
import android.content.Context;

import cn.dagongniu.bitman.R;

/**
 * 页面工具
 */
public class PageToolUtils {

    /**
     * 复制文本
     *
     * @param context
     * @param tvMsg
     */
    public static void CopyText(Context context, String tvMsg) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(tvMsg);
        ToastUtil.ShowToast(context.getResources().getString(R.string.copy_success));
    }

}
