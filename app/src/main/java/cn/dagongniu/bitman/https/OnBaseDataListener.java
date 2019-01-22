package cn.dagongniu.bitman.https;

/**
 * 回调信息接口
 */
public interface OnBaseDataListener<T> {
    public void onNewData(T data);

    public void onError(String code);
}
