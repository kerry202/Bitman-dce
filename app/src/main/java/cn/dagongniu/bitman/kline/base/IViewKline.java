package cn.dagongniu.bitman.kline.base;

import android.content.Context;

public interface IViewKline {
    /**
     * 请求返回实体
     */
    void setLineData(Object obj);

    /**
     * 获取Context
     * @return
     */
    Context getContext();

}
