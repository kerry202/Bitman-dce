package cn.dagongniu.bitman.base;

import android.app.Activity;

import java.util.HashMap;

import cn.dagongniu.bitman.https.OnDataListListener;
import cn.dagongniu.bitman.https.OnDataListener;
import cn.dagongniu.bitman.https.RequestState;

/**
 * 模型 base
 */
public abstract class OAXBaseModule<T, V> {
    public Activity activity;

    public OAXBaseModule(Activity activity) {
        this.activity = activity;
    }

    public void requestServerData(OnDataListener<V> onDataListener, RequestState state, HashMap<String, Object> params) {
    }

    public void requestServerDataList(OnDataListListener<V> dataListListener, RequestState state, HashMap<String, Object> params) {
    }

}
