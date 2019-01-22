package cn.dagongniu.bitman.assets;

import android.view.View;

import butterknife.BindView;
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.base.BaseActivity;
import cn.dagongniu.bitman.customview.MyTradingToolbar;

/**
 * 红包帮助 遇到问题
 */
public class RedHelpActivity extends BaseActivity {

    @BindView(R.id.assets_toolbar)
    MyTradingToolbar myTradingToolbar;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_red_help;
    }

    @Override
    protected void initView() {
        super.initView();
        initToobar();
    }

    @Override
    protected void initData() {
        super.initData();
    }

    private void initToobar() {

        myTradingToolbar.setRightImgVisibility(true);
        myTradingToolbar.setTvLeftVisibility(true);
        myTradingToolbar.setSjVisibility(true);
        myTradingToolbar.setRightTvVisibility(true);
        myTradingToolbar.setTitleNameText(R.string.red_help);
        myTradingToolbar.setRightTvColor(getContext().getResources().getColor(R.color.df_333333));
        myTradingToolbar.setLeftMoreClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}


