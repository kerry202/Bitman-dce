package cn.dagongniu.bitman.my_ui;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.dagongniu.bitman.OAXApplication;
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.assets.bean.UserCoinTopBean;
import cn.dagongniu.bitman.assets.view.IUserCoinTopView;
import cn.dagongniu.bitman.base.BaseActivity;

public class MyTopupCradActivity extends BaseActivity implements View.OnClickListener, IUserCoinTopView {

    @BindView(R.id.my_ent_ord_pager)
    ViewPager entOrdPager;
    @BindView(R.id.my_magic_indicator)
    TabLayout magicIndicator;
    @BindView(R.id.my_rl_close)
    ImageView rlClose;

    List<Fragment> fragmentsEntOrd = new ArrayList<>();

    private List<String> mDataList = new ArrayList<>();
    Intent intent;
    public String MarketId = null;
    public String MarketName = null;
    private FragmentPagerAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.my_topup_layout;
    }

    @Override
    protected void initView() {
        super.initView();
        intent = this.getIntent();
        MarketId = intent.getStringExtra("MarketId");
        MarketName = intent.getStringExtra("MarketName");
        setRecordTitle();

    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {

            View v = getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) MyTopupCradActivity.this
                    .getSystemService(OAXApplication.sContext.INPUT_METHOD_SERVICE);
            if (v != null) {
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 主流市场
     */
    private void setRecordTitle() {

        if (MarketId.equals("23")) {
//            mDataList.add(MarketName + getResources().getString(R.string.assets_topup));
            mDataList.add(getResources().getString(R.string.assets_topup));
            mDataList.add(getResources().getString(R.string.dk_str));
            fragmentsEntOrd.add(new AddressFragment());
            fragmentsEntOrd.add(new CardFragment());
        } else {
//            mDataList.add(MarketName + getResources().getString(R.string.assets_topup));
            mDataList.add(getResources().getString(R.string.assets_topup));
            fragmentsEntOrd.add(new AddressFragment());
        }
        FragmentPagerAdapter mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentsEntOrd.get(position);
            }

            @Override
            public int getCount() {
                return fragmentsEntOrd.size();
            }

            //ViewPager与TabLayout绑定后，这里获取到PageTitle就是Tab的Text
            @Override
            public CharSequence getPageTitle(int position) {
                return mDataList.get(position);
            }
        };


        //设定适配器
        entOrdPager.setAdapter(mAdapter);
        magicIndicator.setupWithViewPager(entOrdPager);//将TabLayout和ViewPager关联起来。
        magicIndicator.setTabsFromPagerAdapter(mAdapter);//给Tabs设置适配器


        rlClose.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_rl_close:
                finish();
                break;
        }
    }

    @Override
    public void setUserCoinTopData(UserCoinTopBean userCoinTopBean) {

    }

    @Override
    public String getCoinId() {
        return null;
    }

    @Override
    public void goLogin(String msg) {

    }
}
