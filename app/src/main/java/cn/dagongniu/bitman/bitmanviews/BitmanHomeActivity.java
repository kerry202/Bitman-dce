package cn.dagongniu.bitman.bitmanviews;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.base.BaseActivity;
import cn.dagongniu.bitman.bitman.QuantityBean;
import cn.dagongniu.bitman.bitman.StarListBean;
import cn.dagongniu.bitman.constant.SPConstant;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.HttpUtils;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.utils.Logs;
import cn.dagongniu.bitman.utils.SPUtils;

public class BitmanHomeActivity extends BaseActivity {
    @BindView(R.id.user_grade)
    TextView userGrade;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.user_id)
    TextView userId;
    @BindView(R.id.user_price)
    TextView userPrice;
    @BindView(R.id.topUp_tv)
    TextView topUpTv;
    @BindView(R.id.start_star_iv)
    ImageView startStarIv;
    @BindView(R.id.now_quantity_in)
    TextView nowQuantityIn;
    @BindView(R.id.last_quantity_in)
    TextView lastQuantityIn;
    @BindView(R.id.now_quantity_all)
    TextView nowQuantityAll;
    @BindView(R.id.last_quantity_all)
    TextView lastQuantityAll;
    private List<StarListBean.DataBean.ListBean> list;
    private StarListBean starListBean;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_class;
    }


    @Override
    protected void initView() {
        super.initView();
    }


    @Override
    protected void initData() {
        super.initData();
        getStarListDate();
        getAssetsDetail();
        getSummaryData();

    }

    private void getStarListDate() {

        String cn = (String) SPUtils.getParam(mContext, SPConstant.LANGUAGE, "CN");
        cn = cn.toLowerCase();
        HashMap<String, Object> hashMap = new HashMap();
        String userid = SPUtils.getParamString(mContext, SPConstant.USER_ID, null);
        String username = SPUtils.getParamString(mContext, SPConstant.USER_PHONE, null);
        String userToken = SPUtils.getParamString(mContext, SPConstant.USER_TOKEN, null);
        String userphone = SPUtils.getParamString(mContext, SPConstant.USER_PHONE, null);
        userId.setText(userid);
        userName.setText(userphone);

        hashMap.put("language", cn);
        hashMap.put("deviceType", Http.device);
        hashMap.put("apiVersion", Utils.getVersionCode(mContext));
        hashMap.put("accessToken", userToken);
        hashMap.put("userId", userid);
        hashMap.put("loginProject", Http.projectName);
        hashMap.put("token", userToken);
        hashMap.put("page", "1");
        hashMap.put("pageSize", "20");
        hashMap.put("starId", "1");
        hashMap.put("quadrantId", "1");

        HttpUtils.getInstance().bitmanDate(Http.getStarList, hashMap, mContext, new OnBaseDataListener<String>() {
            @Override
            public void onNewData(String data) {
                Logs.s(" bitmaninterface 星球列表onNewData: " + data);
                starListBean = new Gson().fromJson(data, StarListBean.class);
                StarListBean.DataBean data1 = starListBean.data;
                if (data1 != null) {
                    list = data1.list;
                    userPrice.setText(list.get(0).reserves);
                    startStarIv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, BitmanThorwActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("startlistbean", starListBean);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });
                }

            }

            @Override
            public void onError(String code) {
                Logs.s(" bitmaninterface 星球列表onNewData: " + code);
            }
        }, RequestState.STATE_REFRESH);
    }

    private void getAssetsDetail() {

        String cn = (String) SPUtils.getParam(mContext, SPConstant.LANGUAGE, "CN");
        cn = cn.toLowerCase();
        HashMap<String, Object> hashMap = new HashMap();
        String userid = SPUtils.getParamString(mContext, SPConstant.USER_ID, null);
        String username = SPUtils.getParamString(mContext, SPConstant.USER_PHONE, null);
        String userToken = SPUtils.getParamString(mContext, SPConstant.USER_TOKEN, null);
        hashMap.put("language", cn);
        hashMap.put("deviceType", Http.device);
        hashMap.put("apiVersion", Utils.getVersionCode(mContext));
        hashMap.put("accessToken", userToken);
        hashMap.put("userId", userid);
        hashMap.put("loginProject", Http.projectName);
        hashMap.put("token", userToken);
        hashMap.put("coinId", "23");


        HttpUtils.getInstance().bitmanDate(Http.assetsDetil, hashMap, mContext, new OnBaseDataListener<String>() {
            @Override
            public void onNewData(String data) {

                Logs.s("    bitmaninterface 用户资产详情onNewData:    " + data);

            }

            @Override
            public void onError(String code) {
                Logs.s("    bitmaninterface 用户资产详情onError:    " + code);

            }
        }, RequestState.STATE_REFRESH);

    }

    private void getSummaryData() {
        String cn = (String) SPUtils.getParam(mContext, SPConstant.LANGUAGE, "CN");
        cn = cn.toLowerCase();
        HashMap<String, Object> hashMap = new HashMap();
        String userid = SPUtils.getParamString(mContext, SPConstant.USER_ID, null);
        String username = SPUtils.getParamString(mContext, SPConstant.USER_PHONE, null);
        String userToken = SPUtils.getParamString(mContext, SPConstant.USER_TOKEN, null);
        hashMap.put("language", cn);
        hashMap.put("deviceType", Http.device);
        hashMap.put("apiVersion", Utils.getVersionCode(mContext));
        hashMap.put("accessToken", userToken);
        hashMap.put("userId", userid);
        hashMap.put("loginProject", Http.projectName);
        hashMap.put("queryUid", userid);
        hashMap.put("token", userToken);
        hashMap.put("coinId", "23");

        HttpUtils.getInstance().bitmanDate(Http.getSunnaryDate, hashMap, mContext, new OnBaseDataListener<String>() {
            @Override
            public void onNewData(String data) {
                Logs.s(" bitmaninterface 算力/收益统计信息onNewData: " + data);
                try {
                    QuantityBean quantityBean = new Gson().fromJson(data, QuantityBean.class);
                    nowQuantityIn.setText(quantityBean.data.userComputingPower);
                    lastQuantityIn.setText(quantityBean.data.userMiningRewards);

                    nowQuantityAll.setText(quantityBean.data.allComputingPower);
                    lastQuantityAll.setText(quantityBean.data.allMiningRewards);
                    userName.setText(username);
                    userId.setText(userid);
                } catch (Exception e) {
                }


            }

            @Override
            public void onError(String code) {
                Logs.s(" bitmaninterface 算力/收益统计信息onError: " + code);
            }
        }, RequestState.STATE_REFRESH);

    }


}
