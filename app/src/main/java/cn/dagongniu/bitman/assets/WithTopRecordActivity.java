package cn.dagongniu.bitman.assets;

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
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.assets.fragment.TopUpFragment;
import cn.dagongniu.bitman.assets.fragment.WithdrawalFragment;
import cn.dagongniu.bitman.base.BaseActivity;

/**
 * 历史记录 充值提现
 */
public class WithTopRecordActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.ent_ord_pager)
    ViewPager entOrdPager;
    @BindView(R.id.tab_layout_id)
    TabLayout tab_layout_id;
    @BindView(R.id.rl_close)
    ImageView rlClose;

    List<Fragment> fragmentsEntOrd = new ArrayList<>();

    private String[] CHANNELS = null;
    private List<String> mDataList = null;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_with_top_record;
    }

    @Override
    protected void initView() {
        super.initView();
        setRecordTitle();
        rlClose.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
    }


    /**
     * 主流市场
     */
    private void setRecordTitle() {
        CHANNELS = getResources().getStringArray(R.array.assets_record);
        mDataList = Arrays.asList(CHANNELS);

        fragmentsEntOrd.add(new TopUpFragment());
        fragmentsEntOrd.add(new WithdrawalFragment());


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

        tab_layout_id.setupWithViewPager(entOrdPager);//将TabLayout和ViewPager关联起来。
        tab_layout_id.setTabsFromPagerAdapter(mAdapter);//给Tabs设置适配器
        initMagicIndicator();

    }

    private void initMagicIndicator() {


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_close:
                finish();
                break;
        }
    }
}
