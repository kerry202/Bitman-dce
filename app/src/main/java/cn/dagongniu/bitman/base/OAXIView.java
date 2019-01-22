package cn.dagongniu.bitman.base;

import android.content.Context;

import cn.dagongniu.bitman.customview.LoadingState;

public interface OAXIView {
    //Toast
    void showToast(String msg);

    /**
     * 获取Context
     *
     * @return
     */
    Context getContext();

    /**
     * 请求状态返回，全屏
     *
     * @param xState
     * @param msg
     */
    void setXState(LoadingState xState, String msg);
}
