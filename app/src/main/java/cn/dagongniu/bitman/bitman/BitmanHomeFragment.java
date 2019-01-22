package cn.dagongniu.bitman.bitman;

import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.base.BaseFragment;
import cn.dagongniu.bitman.bitmanviews.Utils;
import cn.dagongniu.bitman.constant.SPConstant;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.HttpUtils;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.main.MainActivity;
import cn.dagongniu.bitman.utils.Logs;
import cn.dagongniu.bitman.utils.SPUtils;

public class BitmanHomeFragment extends BaseFragment {

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
    private MainActivity activity;
    private String userid;
    private String username;
    private String userToken;
    private String cn;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_class;
    }


    @Override
    protected void initView() {
        super.initView();
        activity = (MainActivity) getActivity();

    }

    public void setDatas(StarListBean.DataBean.ListBean listBean) {

    }

    @Override
    protected void initData() {
        super.initData();
        cn = (String) SPUtils.getParam(mContext, SPConstant.LANGUAGE, "CN");
        cn = cn.toLowerCase();
        userid = SPUtils.getParamString(mContext, SPConstant.USER_ID, null);
        username = SPUtils.getParamString(mContext, SPConstant.USER_PHONE, null);
        userToken = SPUtils.getParamString(mContext, SPConstant.USER_TOKEN, null);

        getAssetsDetail();
        getSummaryData();

    }


    private void getAssetsDetail() {

        HashMap<String, Object> hashMap = new HashMap();
        hashMap.put("language", cn);
        hashMap.put("deviceType", Http.device);
        hashMap.put("apiVersion", Utils.getVersionCode(mContext));
        hashMap.put("accessToken", userToken);
        hashMap.put("userId", userid);
        hashMap.put("loginProject", Http.projectName);
        hashMap.put("token", userToken);
        hashMap.put("coinId", "23");


        HttpUtils.getInstance().bitmanDate(Http.assetsDetil, hashMap, getActivity(), new OnBaseDataListener<String>() {
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

        HashMap<String, Object> hashMap = new HashMap();
        hashMap.put("language", cn);
        hashMap.put("deviceType", Http.device);
        hashMap.put("apiVersion", Utils.getVersionCode(mContext));
        hashMap.put("accessToken", userToken);
        hashMap.put("userId", userid);
        hashMap.put("loginProject", Http.projectName);
        hashMap.put("queryUid", userid);
        hashMap.put("token", userToken);
        hashMap.put("coinId", "23");

        HttpUtils.getInstance().bitmanDate(Http.getSunnaryDate, hashMap, getActivity(), new OnBaseDataListener<String>() {
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


    @OnClick(R.id.start_star_iv)
    public void onViewClicked() {
        activity.showHide(1);

    }
}
