package cn.dagongniu.bitman.trading;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import cn.dagongniu.bitman.OAXApplication;
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.base.OAXBaseActivity;
import cn.dagongniu.bitman.trading.fragment.EntrustFragment;
import cn.dagongniu.bitman.trading.fragment.OrderFragment;
import cn.dagongniu.bitman.trading.fragment.RightEntOrdFragment;

/**
 * 委托/订单
 */
public class EntrustOrderActivity extends OAXBaseActivity implements View.OnClickListener {

    @BindView(R.id.ent_ord_pager)
    ViewPager entOrdPager;
    @BindView(R.id.magic_indicator)
    TabLayout magicIndicator;
    @BindView(R.id.rl_close)
    ImageView rlClose;
    @BindView(R.id.rl_screening)
    ImageView rlScreening;

    List<Fragment> fragmentsEntOrd = new ArrayList<>();

    private String[] CHANNELS = null;
    private List<String> mDataList = null;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_entrust_order;
    }

    @Override
    protected void initView() {
        super.initView();
        setEntOrdTitle();
        rlClose.setOnClickListener(this);
        rlScreening.setOnClickListener(this);
        //默认隐藏
        rlScreening.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void initData() {
        super.initData();
    }

    /**
     * 主流市场
     */
    private void setEntOrdTitle() {
        CHANNELS = getResources().getStringArray(R.array.entrustnadorder);
        mDataList = Arrays.asList(CHANNELS);

        fragmentsEntOrd.add(new EntrustFragment());
        fragmentsEntOrd.add(new OrderFragment());

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

        initMagicIndicator();

    }

    private void initMagicIndicator() {


        entOrdPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0)
                    isRlScreeningVi(true);
                else
                    isRlScreeningVi(false);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    /**
     * 是否隐藏   ture=隐藏  iv_false=显示
     */
    public void isRlScreeningVi(boolean is) {
        if (is) {
            rlScreening.setVisibility(View.INVISIBLE);
        } else {
            rlScreening.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_close:
                finish();
                break;
            case R.id.rl_screening:
                //排序
                //如果弹出的dialog里有输入框并且activity里设置了keyboardEnable为true的话，
                //当弹出Dialog的时候，要把activity的keyboardEnable方法设置为false，
                //当dialog关闭时，要把keyboardEnable设置为打开之前的状态
                RightEntOrdFragment rightEntOrdFragment = new RightEntOrdFragment();
                rightEntOrdFragment.setOrderSelectNoticeBean(OAXApplication.getInstance().getOrderSelectNoticeBean());
                rightEntOrdFragment.show(getSupportFragmentManager(), "RightEntOrdFragment");
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OAXApplication.getInstance().setOrderSelectNoticeBean(null);
    }
}
