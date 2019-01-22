package cn.dagongniu.bitman.main;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.base.OAXBaseActivity;
import cn.dagongniu.bitman.constant.UMConstant;
import cn.dagongniu.bitman.customview.ScaleCircleNavigator;
import cn.dagongniu.bitman.main.adapter.FragAdapter;
import cn.dagongniu.bitman.main.fragment.GuidanceFirstFragment;
import cn.dagongniu.bitman.main.fragment.GuidanceSecondFragment;
import cn.dagongniu.bitman.main.fragment.GuidanceThirdFragment;
import cn.dagongniu.bitman.utils.SkipActivityUtil;
import cn.dagongniu.bitman.utils.um.UMManager;

/**
 * 引导页
 */
public class GuidanceActivity extends OAXBaseActivity {

    @BindView(R.id.main_market_view_pager)
    ViewPager mViewPager;
    @BindView(R.id.magic_indicator)
    MagicIndicator mMagicIndicator;
    List<Fragment> fragments = new ArrayList<>();
    @BindView(R.id.tv_skip)
    TextView tvSkip;

    private int currentPosition = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_guidance;
    }

    @Override
    protected void initView() {
        super.initView();
        initFragment();
        initMagicIndicator();

    }

    private void initFragment() {
        fragments.add(new GuidanceFirstFragment());
        fragments.add(new GuidanceSecondFragment());
        fragments.add(new GuidanceThirdFragment());
        FragAdapter adapter = new FragAdapter(getSupportFragmentManager(), fragments);
        //设定适配器
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                if (position == 2) {
                    tvSkip.setVisibility(View.GONE);
                } else {
                    tvSkip.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initMagicIndicator() {
        ScaleCircleNavigator scaleCircleNavigator = new ScaleCircleNavigator(this);
        scaleCircleNavigator.setCircleCount(fragments.size());
        scaleCircleNavigator.setNormalCircleColor(getResources().getColor(R.color.df_9B9B9B));
        scaleCircleNavigator.setSelectedCircleColor(getResources().getColor(R.color.df_FACE1F));
        scaleCircleNavigator.setCircleClickListener(new ScaleCircleNavigator.OnCircleClickListener() {
            @Override
            public void onClick(int index) {
                mViewPager.setCurrentItem(index);
            }
        });
        mMagicIndicator.setNavigator(scaleCircleNavigator);
        ViewPagerHelper.bind(mMagicIndicator, mViewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UMManager.onResume(this, UMConstant.GuidanceActivity);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UMManager.onPause(this, UMConstant.GuidanceActivity);
    }

    @OnClick(R.id.tv_skip)
    public void onClicked(View view) {
        SkipActivityUtil.guidanceSkipToMain(this, mContext);
        switch (currentPosition) {
            case 0:
                UMManager.onEvent(mContext, UMConstant.GuidanceActivity, UMConstant.GuidanceActivity_first_jump);
                break;
            case 1:
                UMManager.onEvent(mContext, UMConstant.GuidanceActivity, UMConstant.GuidanceActivity_second_jump);
                break;
        }
    }
}
