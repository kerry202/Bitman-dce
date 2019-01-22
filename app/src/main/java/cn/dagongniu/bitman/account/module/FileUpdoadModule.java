package cn.dagongniu.bitman.account.module;

import android.app.Activity;

import com.google.gson.Gson;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import cn.dagongniu.bitman.account.bean.FileUpdoadBean;
import cn.dagongniu.bitman.base.BaseModule;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.HttpUtils;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;

/**
 * 文件上传
 */
public class FileUpdoadModule extends BaseModule<String, FileUpdoadBean> {


    public FileUpdoadModule(Activity activity) {
        super(activity);
    }

    @Override
    public void requestServerDataOne(OnBaseDataListener<FileUpdoadBean> onBaseDataListener, String... parm) {

    }

    @Override
    public void requestServerDataOne(OnBaseDataListener<FileUpdoadBean> onBaseDataListener, RequestState state, String... parm) {

    }

    public void requestServerDataOne(OnBaseDataListener<FileUpdoadBean> onBaseDataListener, Activity activity, List<File> list, RequestState state, String... parm) {

        HashMap<String, String> hashMap = new HashMap<>();
        String fileName = parm[0];
        String name = parm[1];

        hashMap.put("fileName", fileName);
        hashMap.put("name", name);

        HttpUtils.getInstance().postFile(Http.FILEUPDATE_UPDATEPIC, hashMap, activity, state, list, new OnBaseDataListener<String>() {
            @Override
            public void onNewData(String data) {
                try {
                    FileUpdoadBean httpBaseBean = new Gson().fromJson(data, FileUpdoadBean.class);
                    onBaseDataListener.onNewData(httpBaseBean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String code) {
                onBaseDataListener.onError(code);
            }
        });

    }
}
