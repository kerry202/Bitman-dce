package cn.dagongniu.bitman.https;

/**
 * 回调信息接口
 */
public interface OnDataListListener<T> {
    public void onNewData(CommonJsonToList<T> data);

    public void onError(String msg);
}
