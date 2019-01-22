package cn.dagongniu.bitman.bitmanviews;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.base.BaseActivity;
import cn.dagongniu.bitman.bitman.Bean;
import cn.dagongniu.bitman.bitman.HarvestPriceBean;
import cn.dagongniu.bitman.bitman.StarBean;
import cn.dagongniu.bitman.bitman.StarListBean;
import cn.dagongniu.bitman.constant.SPConstant;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.HttpUtils;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.utils.Logs;
import cn.dagongniu.bitman.utils.SPUtils;
import cn.dagongniu.bitman.utils.ToastUtil;

public class BitmanThorwActivity extends BaseActivity {
    @BindView(R.id.wa_kmin)
    CircleProgressView wa_kmin;  //小投注进度
    @BindView(R.id.wa_kzho)
    CircleProgressView wa_kzho;   //中投注进度
    @BindView(R.id.wa_kmax)
    CircleProgressView wa_kmax;   //大投注进度
    @BindView(R.id.wa_kzho2)
    ImageView waKzho2;    //挖矿图标
    @BindView(R.id.home_ll)
    LinearLayout homeLl;
    @BindView(R.id.home_pager)
    ViewPager homePager;
    @BindView(R.id.user_grade)
    TextView userGrade;   //矿主
    @BindView(R.id.user_name)
    TextView userName;  //
    @BindView(R.id.user_id)
    TextView userId;
    @BindView(R.id.user_price)
    TextView userPrice;
    @BindView(R.id.star_name)
    TextView starName;  //星球名字
    @BindView(R.id.star_price)
    TextView starPrice; //星球价格
    @BindView(R.id.des_)
    TextView des;       //底部描述
    @BindView(R.id.throw_min_times)
    TextView throwMinTimes;    //小投注次数
    @BindView(R.id.throw_min)
    TextView throwMin;  //小投注价格
    @BindView(R.id.throw_min_tv1)
    TextView throwMinTv1;  //小投注 to
    @BindView(R.id.thorows_min_put_tv1)
    TextView thorowsMinPutTv1;  //小投注 收价格
    @BindView(R.id.throw_zho_times)
    TextView throwZhoTimes;  //中投注次数
    @BindView(R.id.throws_zho)
    TextView throwsZho;  //中投注价格
    @BindView(R.id.throw_zho_tv2)
    TextView throwZhoTv2;   //中投注 to
    @BindView(R.id.thorows_zho_put_tv2)
    TextView thorowsZhoPutTv2;  //中投注收价
    @BindView(R.id.throw_max_times)
    TextView throwMaxTimes;  //大投注 次数
    @BindView(R.id.throws_max)
    TextView throwsMax;   //大投注价格
    @BindView(R.id.throw_max_tv3)
    TextView throwMaxTv3;  //大投注 to
    @BindView(R.id.thorows_max_put_tv3)
    TextView thorowsMaxPutTv3; //大投注收价格

    private int conut1 = 0;
    private int conut2 = 0;
    private int conut3 = 0;

    private int state1 = 0;
    private int state2 = 0;
    private int state3 = 0;

    private int times = 100;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {
                conut1 += 20;
                if (conut1 <= 100) {
                    wa_kmin.setProgress(conut1);
                    handler.sendEmptyMessageDelayed(1, times);
                } else {
                    if (state1 == 1) {
                        conut1 = 20;
                        wa_kmin.setProgress(conut1);
                        wa_kmin.setReachBarColor(mContext.getResources().getColor(R.color.yellow));
                        state1 = 2;
                        handler.sendEmptyMessageDelayed(1, times);
                        defHarvestNumber1 = false;
                    } else {
                        if (harvestNumber1 == 0) {
                            defHarvestNumber1 = true;
                            throwMinTimes.setVisibility(View.INVISIBLE);
                            harvestNumber1 = listBean.harvestNumber1;
                            wa_kmin.setReachBarColor(mContext.getResources().getColor(R.color.transparent2));
                            state1 = 0;
                        } else {
                            defHarvestNumber1 = false;
                            conut1 = 20;
                            state1 = 3;
                        }
                        animation_iv.setVisibility(View.GONE);
                        wa_kmin.setClickable(true);
                    }
                }
            } else if (msg.what == 2) {
                conut2 += 20;
                if (conut2 <= 100) {
                    wa_kzho.setProgress(conut2);
                    handler.sendEmptyMessageDelayed(2, times);
                } else {
                    if (state2 == 1) {
                        conut2 = 20;
                        wa_kzho.setProgress(conut2);
                        wa_kzho.setReachBarColor(mContext.getResources().getColor(R.color.yellow));
                        handler.sendEmptyMessageDelayed(2, times);
                        state2 = 2;
                        defHarvestNumber2 = false;
                    } else {
                        if (harvestNumber2 == 0) {
                            throwZhoTimes.setVisibility(View.INVISIBLE);
                            harvestNumber2 = listBean.harvestNumber2;
                            wa_kzho.setReachBarColor(mContext.getResources().getColor(R.color.transparent2));
                            defHarvestNumber2 = true;
                            state2 = 0;
                        } else {
                            conut2 = 20;
                            state2 = 3;
                            defHarvestNumber2 = false;
                        }
                        animation_iv.setVisibility(View.GONE);
                        wa_kzho.setClickable(true);
                    }
                }
            } else if (msg.what == 3) {
                conut3 += 20;
                if (conut3 <= 100) {
                    wa_kmax.setProgress(conut3);
                    handler.sendEmptyMessageDelayed(3, times);
                } else {

                    if (state3 == 1) {
                        conut3 = 20;
                        wa_kmax.setProgress(conut3);
                        wa_kmax.setReachBarColor(mContext.getResources().getColor(R.color.yellow));
                        handler.sendEmptyMessageDelayed(3, times);
                        state3 = 2;
                        defHarvestNumber3 = false;
                    } else {
                        if (harvestNumber3 == 0) {
                            throwMaxTimes.setVisibility(View.INVISIBLE);
                            harvestNumber3 = listBean.harvestNumber3;
                            defHarvestNumber3 = true;
                            state3 = 0;
                            wa_kmax.setReachBarColor(mContext.getResources().getColor(R.color.transparent2));
                        } else {
                            defHarvestNumber3 = false;
                            conut3 = 20;
                            state3 = 3;
                        }
                        animation_iv.setVisibility(View.GONE);
                        wa_kmax.setClickable(true);
                    }

                }
            }

        }
    };
    private HomePagerAdapter homePagerAdapter;
    private ArrayList<StarBean> list;
    private StarBean starBean;
    private StarListBean.DataBean.ListBean listBean;
    private int harvestNumber1;
    private int harvestNumber3;
    private int harvestNumber2;

    private boolean defHarvestNumber1 = true;
    private boolean defHarvestNumber3 = true;
    private boolean defHarvestNumber2 = true;

    private double reserverDouble;
    private double minPriceDouble;
    private double zhoPriceDouble;
    private double maxPriceDouble;
    private double interest1Double;
    private double interest2Double;
    private double interest3Double;
    private Long startCurrentTimes_min;
    private Long cancleCurrentTimes_min;

    private Long startCurrentTimes_zho;
    private Long cancleCurrentTimes_zho;

    private Long startCurrentTimes_max;
    private Long cancleCurrentTimes_max;

    @OnClick({R.id.wa_kmin, R.id.wa_kzho, R.id.wa_kmax, R.id.wa_kzho2})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.wa_kmin:
                if (defHarvestNumber1) {
                    if (reserverDouble >= minPriceDouble) {
                        animation_iv.setVisibility(View.VISIBLE);
                        throwMinTimes.setVisibility(View.VISIBLE);
                        if (state1 == 0) {
                            startCurrentTimes_min = System.currentTimeMillis();
                            harvestNumber1--;
                            reserverDouble = reserverDouble - minPriceDouble;
                            userPrice.setText(reserverDouble + "");
                            starPrice.setText(reserverDouble + "");
                            throwMinTimes.setText(harvestNumber1 + "/" + listBean.harvestNumber1 + "");
                            wa_kmin.setClickable(false);
                            handler.sendEmptyMessageDelayed(1, times);
                            wa_kmin.setReachBarColor(mContext.getResources().getColor(R.color.bule2));
                            wa_kmin.setProgress(conut1);
                            state1 = 1;
                        } else if (state1 == 2 || state1 == 3) {
                            startCurrentTimes_min = System.currentTimeMillis();
                            wa_kmin.setClickable(false);
                            harvestNumber1--;
                            throwMinTimes.setText(harvestNumber1 + "/" + listBean.harvestNumber1 + "");
                            state1 = 1;
                            conut1 = 20;
                            reserverDouble = reserverDouble + interest1Double;
                            userPrice.setText(reserverDouble + "");
                            starPrice.setText(reserverDouble + "");
                            handler.sendEmptyMessageDelayed(1, times);
                            wa_kmin.setReachBarColor(mContext.getResources().getColor(R.color.bule2));
                            wa_kmin.setProgress(conut1);
                            Toast.makeText(mContext, "chenge", Toast.LENGTH_LONG).show();
                            getThorwPrice();
                        }
                    } else {
                        ToastUtil.ShowToast("资产不足");
                    }
                } else {
                    animation_iv.setVisibility(View.VISIBLE);
                    throwMinTimes.setVisibility(View.VISIBLE);
                    if (state1 == 0) {
                        startCurrentTimes_min = System.currentTimeMillis();
                        harvestNumber1--;
                        reserverDouble = reserverDouble - minPriceDouble;
                        userPrice.setText(reserverDouble + "");
                        starPrice.setText(reserverDouble + "");
                        throwMinTimes.setText(harvestNumber1 + "/" + listBean.harvestNumber1 + "");
                        wa_kmin.setClickable(false);
                        handler.sendEmptyMessageDelayed(1, times);
                        wa_kmin.setReachBarColor(mContext.getResources().getColor(R.color.bule2));
                        wa_kmin.setProgress(conut1);
                        state1 = 1;
                    } else if (state1 == 2 || state1 == 3) {
                        startCurrentTimes_min = System.currentTimeMillis();
                        wa_kmin.setClickable(false);
                        harvestNumber1--;
                        throwMinTimes.setText(harvestNumber1 + "/" + listBean.harvestNumber1 + "");
                        state1 = 1;
                        conut1 = 20;
                        reserverDouble = reserverDouble + interest1Double;
                        userPrice.setText(reserverDouble + "");
                        starPrice.setText(reserverDouble + "");
                        handler.sendEmptyMessageDelayed(1, times);
                        wa_kmin.setReachBarColor(mContext.getResources().getColor(R.color.bule2));
                        wa_kmin.setProgress(conut1);
                        Toast.makeText(mContext, "chenge", Toast.LENGTH_LONG).show();
                        getThorwPrice();
                    }
                }


                break;
            case R.id.wa_kzho:
                if (defHarvestNumber2) {
                    if (reserverDouble >= zhoPriceDouble) {
                        animation_iv.setVisibility(View.VISIBLE);
                        throwZhoTimes.setVisibility(View.VISIBLE);

                        if (state2 == 0) {
                            startCurrentTimes_zho = System.currentTimeMillis();
                            harvestNumber2--;
                            reserverDouble = reserverDouble - zhoPriceDouble;
                            userPrice.setText(reserverDouble + "");
                            starPrice.setText(reserverDouble + "");
                            throwZhoTimes.setText(harvestNumber2 + "/" + listBean.harvestNumber2 + "");
                            wa_kzho.setClickable(false);
                            handler.sendEmptyMessageDelayed(2, times);
                            wa_kzho.setReachBarColor(mContext.getResources().getColor(R.color.bule2));
                            wa_kzho.setProgress(conut2);
                            state2 = 1;
                        } else if (state2 == 2 || state2 == 3) {
                            startCurrentTimes_zho = System.currentTimeMillis();
                            wa_kzho.setClickable(false);
                            harvestNumber2--;
                            throwZhoTimes.setText(harvestNumber2 + "/" + listBean.harvestNumber2 + "");
                            state2 = 1;
                            conut2 = 20;
                            reserverDouble = reserverDouble + interest2Double;
                            userPrice.setText(reserverDouble + "");
                            starPrice.setText(reserverDouble + "");
                            handler.sendEmptyMessageDelayed(2, times);
                            wa_kzho.setReachBarColor(mContext.getResources().getColor(R.color.bule2));
                            wa_kzho.setProgress(conut2);
                            Toast.makeText(mContext, "chenge", Toast.LENGTH_LONG).show();
                            getThorwPrice();
                        }
                    } else {
                        ToastUtil.ShowToast("资产不足");
                    }
                } else {
                    animation_iv.setVisibility(View.VISIBLE);
                    throwZhoTimes.setVisibility(View.VISIBLE);

                    if (state2 == 0) {
                        startCurrentTimes_zho = System.currentTimeMillis();
                        harvestNumber2--;
                        reserverDouble = reserverDouble - zhoPriceDouble;
                        userPrice.setText(reserverDouble + "");
                        starPrice.setText(reserverDouble + "");
                        throwZhoTimes.setText(harvestNumber2 + "/" + listBean.harvestNumber2 + "");
                        wa_kzho.setClickable(false);
                        handler.sendEmptyMessageDelayed(2, times);
                        wa_kzho.setReachBarColor(mContext.getResources().getColor(R.color.bule2));
                        wa_kzho.setProgress(conut2);
                        state2 = 1;
                    } else if (state2 == 2 || state2 == 3) {
                        startCurrentTimes_zho = System.currentTimeMillis();
                        wa_kzho.setClickable(false);
                        harvestNumber2--;
                        throwZhoTimes.setText(harvestNumber2 + "/" + listBean.harvestNumber2 + "");
                        state2 = 1;
                        conut2 = 20;
                        reserverDouble = reserverDouble + interest2Double;
                        userPrice.setText(reserverDouble + "");
                        starPrice.setText(reserverDouble + "");
                        handler.sendEmptyMessageDelayed(2, times);
                        wa_kzho.setReachBarColor(mContext.getResources().getColor(R.color.bule2));
                        wa_kzho.setProgress(conut2);
                        Toast.makeText(mContext, "chenge", Toast.LENGTH_LONG).show();
                        getThorwPrice();
                    }
                }


                break;
            case R.id.wa_kmax:
                if (defHarvestNumber3) {
                    if (reserverDouble >= maxPriceDouble) {
                        animation_iv.setVisibility(View.VISIBLE);
                        throwMaxTimes.setVisibility(View.VISIBLE);

                        if (state3 == 0) {
                            startCurrentTimes_max = System.currentTimeMillis();
                            reserverDouble = reserverDouble - maxPriceDouble;
                            userPrice.setText(reserverDouble + "");
                            starPrice.setText(reserverDouble + "");
                            harvestNumber3--;
                            throwMaxTimes.setText(harvestNumber3 + "/" + listBean.harvestNumber3 + "");
                            wa_kmax.setClickable(false);
                            handler.sendEmptyMessageDelayed(3, times);
                            wa_kmax.setReachBarColor(mContext.getResources().getColor(R.color.bule2));
                            wa_kmax.setProgress(conut3);
                            state3 = 1;
                        } else if (state3 == 2 || state3 == 3) {
                            startCurrentTimes_max = System.currentTimeMillis();
                            harvestNumber3--;
                            wa_kmax.setClickable(false);
                            throwMaxTimes.setText(harvestNumber3 + "/" + listBean.harvestNumber3 + "");
                            state3 = 1;
                            conut3 = 20;
                            reserverDouble = reserverDouble + interest3Double;
                            userPrice.setText(reserverDouble + "");
                            starPrice.setText(reserverDouble + "");
                            handler.sendEmptyMessageDelayed(3, times);
                            wa_kmax.setReachBarColor(mContext.getResources().getColor(R.color.bule2));
                            wa_kmax.setProgress(conut3);
                            Toast.makeText(mContext, "chenge", Toast.LENGTH_LONG).show();
                            getThorwPrice();
                        }
                    } else {
                        ToastUtil.ShowToast("资产不足");
                    }
                } else {
                    animation_iv.setVisibility(View.VISIBLE);
                    throwMaxTimes.setVisibility(View.VISIBLE);

                    if (state3 == 0) {
                        startCurrentTimes_max = System.currentTimeMillis();
                        reserverDouble = reserverDouble - maxPriceDouble;
                        userPrice.setText(reserverDouble + "");
                        starPrice.setText(reserverDouble + "");
                        harvestNumber3--;
                        throwMaxTimes.setText(harvestNumber3 + "/" + listBean.harvestNumber3 + "");
                        wa_kmax.setClickable(false);
                        handler.sendEmptyMessageDelayed(3, times);
                        wa_kmax.setReachBarColor(mContext.getResources().getColor(R.color.bule2));
                        wa_kmax.setProgress(conut3);
                        state3 = 1;
                    } else if (state3 == 2 || state3 == 3) {
                        startCurrentTimes_max = System.currentTimeMillis();
                        harvestNumber3--;
                        wa_kmax.setClickable(false);
                        throwMaxTimes.setText(harvestNumber3 + "/" + listBean.harvestNumber3 + "");
                        state3 = 1;
                        conut3 = 20;
                        reserverDouble = reserverDouble + interest3Double;
                        userPrice.setText(reserverDouble + "");
                        starPrice.setText(reserverDouble + "");
                        handler.sendEmptyMessageDelayed(3, times);
                        wa_kmax.setReachBarColor(mContext.getResources().getColor(R.color.bule2));
                        wa_kmax.setProgress(conut3);
                        Toast.makeText(mContext, "chenge", Toast.LENGTH_LONG).show();
                        getThorwPrice();
                    }
                }

                break;
            case R.id.wa_kzho2:
                startActivity(new Intent(this, BitmanHomeActivity.class));
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logs.s("    onDestroy  onPause    ");

        cancleCurrentTimes_min = System.currentTimeMillis();
        cancleCurrentTimes_zho = System.currentTimeMillis();
        cancleCurrentTimes_max = System.currentTimeMillis();

        SPUtils.setParam(mContext, "startCurrentTimes_min", startCurrentTimes_min + "");
        SPUtils.setParam(mContext, "startCurrentTimes_zho", startCurrentTimes_zho + "");
        SPUtils.setParam(mContext, "startCurrentTimes_max", startCurrentTimes_max + "");


        SPUtils.setParam(mContext, "cancleCurrentTimes_min", cancleCurrentTimes_min + "");
        SPUtils.setParam(mContext, "cancleCurrentTimes_zho", cancleCurrentTimes_zho + "");
        SPUtils.setParam(mContext, "cancleCurrentTimes_max", cancleCurrentTimes_max + "");


        Logs.s("    onDestroy  startCurrentTimes_min1    " + startCurrentTimes_min);
        Logs.s("    onDestroy  startCurrentTimes_min2    " + startCurrentTimes_zho);
        Logs.s("    onDestroy  startCurrentTimes_min3    " + startCurrentTimes_max);
        Logs.s("    onDestroy  startCurrentTimes_min4    " + cancleCurrentTimes_min);
        Logs.s("    onDestroy  startCurrentTimes_min5    " + cancleCurrentTimes_zho);
        Logs.s("    onDestroy  startCurrentTimes_min6    " + cancleCurrentTimes_max);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Logs.s("    onDestroy  onResume    ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Logs.s("    onDestroy  onRestart    ");
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        super.initView();

        String startCurrentTimes_min = (String) SPUtils.getParam(mContext, "startCurrentTimes_min", "0");
        String startCurrentTimes_zho = (String) SPUtils.getParam(mContext, "startCurrentTimes_zho", "0");
        String startCurrentTimes_max = (String) SPUtils.getParam(mContext, "startCurrentTimes_max", "0");

        String cancleCurrentTimes_min = (String) SPUtils.getParam(mContext, "cancleCurrentTimes_min", "0");
        String cancleCurrentTimes_zho = (String) SPUtils.getParam(mContext, "cancleCurrentTimes_zho", "0");
        String cancleCurrentTimes_max = (String) SPUtils.getParam(mContext, "cancleCurrentTimes_max", "0");


        Logs.s("      startCurrentTimes_min1    " + startCurrentTimes_min);
        Logs.s("      startCurrentTimes_min2    " + startCurrentTimes_zho);
        Logs.s("      startCurrentTimes_min3    " + startCurrentTimes_max);
        Logs.s("      startCurrentTimes_min4    " + cancleCurrentTimes_min);
        Logs.s("      startCurrentTimes_min5    " + cancleCurrentTimes_zho);
        Logs.s("      startCurrentTimes_min6    " + cancleCurrentTimes_max);


        if (startCurrentTimes_min != null && !startCurrentTimes_min.equals("null") && startCurrentTimes_min.length() > 0) {
            long startTimes = Long.parseLong(startCurrentTimes_min);
            long cancleTimes = Long.parseLong(cancleCurrentTimes_min);
            long xTimes = cancleTimes - startTimes;
            Logs.s("    xTimes  " + xTimes);
            int s = (int) (xTimes / 1000);
            SPUtils.remove(mContext, "startCurrentTimes_min");
            SPUtils.remove(mContext, "cancleCurrentTimes_min");
        }

        if (startCurrentTimes_zho != null) {

        }

        if (startCurrentTimes_max != null) {

        }

        list = new ArrayList<>();
        starBean = new StarBean();
        starBean.isShow = false;
        list.add(starBean);
        homePagerAdapter = new HomePagerAdapter();

        homePager.setAdapter(homePagerAdapter);

        StarListBean startlistbean = (StarListBean) getIntent().getSerializableExtra("startlistbean");

        listBean = startlistbean.data.list.get(0);

        Logs.s("    getParcelableExtra     " + startlistbean);
    }

    @Override
    protected void initData() {
        super.initData();

        getStarAdd();

        starName.setText(listBean.starName);

        String minPrice = listBean.principal1;
        String zhoPrice = listBean.principal2;
        String maxPrice = listBean.principal3;

        minPriceDouble = Double.parseDouble(minPrice);
        zhoPriceDouble = Double.parseDouble(zhoPrice);
        maxPriceDouble = Double.parseDouble(maxPrice);

        throwMin.setText(listBean.principal1);
        throwsZho.setText(listBean.principal2);
        throwsMax.setText(listBean.principal3);
        String interest1 = listBean.interest1;
        String interest2 = listBean.interest2;
        String interest3 = listBean.interest3;

        interest1Double = Double.parseDouble(interest1);
        interest2Double = Double.parseDouble(interest2);
        interest3Double = Double.parseDouble(interest3);

        thorowsMinPutTv1.setText(listBean.interest1);
        thorowsZhoPutTv2.setText(listBean.interest2);
        thorowsMaxPutTv3.setText(listBean.interest3);
        harvestNumber1 = listBean.harvestNumber1;
        harvestNumber2 = listBean.harvestNumber2;
        harvestNumber3 = listBean.harvestNumber3;
        throwMinTimes.setText(listBean.harvestNumber1 + "");
        throwZhoTimes.setText(listBean.harvestNumber2 + "");
        throwMaxTimes.setText(listBean.harvestNumber3 + "");

        String reserves = listBean.reserves;
        reserverDouble = Double.parseDouble(reserves);
        userPrice.setText(listBean.reserves + "");
        starPrice.setText(listBean.remainReserves + "");
        String userid = SPUtils.getParamString(mContext, SPConstant.USER_ID, null);
        String userphone = SPUtils.getParamString(mContext, SPConstant.USER_PHONE, null);
        userId.setText(userid);
        userName.setText(userphone);

    }

    private void getStarAdd() {
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
        hashMap.put("starId", "1");

        HttpUtils.getInstance().bitmanDate(Http.startAdd, hashMap, mContext, new OnBaseDataListener<String>() {
            @Override
            public void onNewData(String data) {
                Logs.s(" bitmaninterface 激活星球onNewData: " + data);

            }

            @Override
            public void onError(String code) {
                Logs.s(" bitmaninterface 激活星球onError: " + code);

            }
        }, RequestState.STATE_REFRESH);
    }

    private void getThorwPrice() {

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
        hashMap.put("coinId", "23");
        hashMap.put("starId", "1");
        hashMap.put("pitId", "1");
        hashMap.put("action", "1");
        hashMap.put("timestamp", "1");

        HttpUtils.getInstance().bitmanDate(Http.getMining, hashMap, mContext, new OnBaseDataListener<String>() {
            @Override
            public void onNewData(String data) {

                try {
                    HarvestPriceBean harvestPriceBean = new Gson().fromJson(data, HarvestPriceBean.class);
                    Logs.s(" bitmaninterface 收取挖矿收益onNewData: " + harvestPriceBean);

                } catch (Exception e) {
                    Bean bean = new Gson().fromJson(data, Bean.class);
                    Logs.s(" bitmaninterface 收取挖矿收益 bean : " + bean);
                }

            }

            @Override
            public void onError(String code) {
                Logs.s(" bitmaninterface 收取挖矿收益onError: " + code);

            }
        }, RequestState.STATE_REFRESH);

    }


    private ImageView animation_iv;

    public class HomePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }


        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            View view = View.inflate(mContext, R.layout.home_pager_layout, null);

            animation_iv = view.findViewById(R.id.animation_iv);

            Glide.with(mContext)
                    .load(R.mipmap.animation_gif)
                    .into(animation_iv);


            container.addView(view);
            if (position == 1) {
                view.setBackgroundColor(mContext.getResources().getColor(R.color.bule2));
            } else if (position == 2) {
                view.setBackgroundColor(mContext.getResources().getColor(R.color.yellow));
            }
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}
