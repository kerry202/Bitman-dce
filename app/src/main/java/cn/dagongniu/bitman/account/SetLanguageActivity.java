package cn.dagongniu.bitman.account;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import butterknife.BindView;
import cn.dagongniu.bitman.OAXApplication;
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.base.BaseActivity;
import cn.dagongniu.bitman.constant.SPConstant;
import cn.dagongniu.bitman.customview.MyTradingToolbar;
import cn.dagongniu.bitman.language.LanguageType;
import cn.dagongniu.bitman.language.MultiLanguageUtil;
import cn.dagongniu.bitman.main.MainActivity;
import cn.dagongniu.bitman.utils.SPUtils;

/**
 * 设置语言页面
 */
public class SetLanguageActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.mytrading_toolbar)
    MyTradingToolbar mytradingToolbar;
    private RelativeLayout rl_simplified_chinese;
    private ImageView iv_simplified_chinese;

    private RelativeLayout rl_english;
    private ImageView iv_english;

    private RelativeLayout rl_ft_china;
    private ImageView iv_ft_china;


    private RelativeLayout rl_ty_lang;
    private ImageView iv_ty_lang;

    private int savedLanguageType;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_set_language;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(MultiLanguageUtil.attachBaseContext(newBase));
    }

    @Override
    protected void initView() {
        super.initView();
        mytradingToolbar.setTitleNameText(R.string.setting_language_title);
        mytradingToolbar.setLeftMoreClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mytradingToolbar.setRightImgVisibility(true);
        mytradingToolbar.setRightTvVisibility(true);
        initViews();
    }

    private void initViews() {
        rl_simplified_chinese = findViewById(R.id.rl_simplified_chinese);
        iv_simplified_chinese = findViewById(R.id.iv_simplified_chinese);

        rl_english = findViewById(R.id.rl_english);
        iv_english = findViewById(R.id.iv_english);

        iv_ft_china = findViewById(R.id.iv_ft_china);
        rl_ft_china = findViewById(R.id.rl_ft_china);

        rl_ty_lang = findViewById(R.id.rl_ty_lang);
        iv_ty_lang = findViewById(R.id.iv_ty_lang);

        rl_ft_china.setOnClickListener(this);
        rl_simplified_chinese.setOnClickListener(this);
        rl_english.setOnClickListener(this);
        rl_ty_lang.setOnClickListener(this);

        savedLanguageType = MultiLanguageUtil.getInstance().getLanguageType();

        if (savedLanguageType == LanguageType.LANGUAGE_FOLLOW_SYSTEM) {
            setSimplifiedVisible();
        } else if (savedLanguageType == LanguageType.LANGUAGE_EN) {
            setEnglishVisible();
        } else if (savedLanguageType == LanguageType.LANGUAGE_CHINESE_SIMPLIFIED) {
            setSimplifiedVisible();
        } else if (savedLanguageType == LanguageType.LANGUAGE_CHINESE_TRADITIONAL) {
            setFollowSytemVisible();
        } else if (savedLanguageType == LanguageType.LANGUAGE_TY_TRADITIONAL) {
            setTyVisible();
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        int selectedLanguage = 0;

        switch (id) {
            case R.id.rl_simplified_chinese:
                if (isCover(iv_simplified_chinese)) {
                    setSimplifiedVisible();
                    selectedLanguage = LanguageType.LANGUAGE_CHINESE_SIMPLIFIED;
                    OAXApplication.state=6;
                    SPUtils.setParam(this, SPConstant.LANGUAGE, "CN");
                    finish();
                    startMain(selectedLanguage);
                }
                break;
            case R.id.rl_english:
                if (isCover(iv_english)) {
                    setEnglishVisible();
                    selectedLanguage = LanguageType.LANGUAGE_EN;
                    OAXApplication.state=6;
                    SPUtils.setParam(this, SPConstant.LANGUAGE, "EN");
                    finish();
                    startMain(selectedLanguage);
                }
                break;
            case R.id.rl_ft_china:
                setTraditionalVisible();
                selectedLanguage = LanguageType.LANGUAGE_CHINESE_TRADITIONAL;
                OAXApplication.state=6;
                SPUtils.setParam(this, SPConstant.LANGUAGE, "TW");
                finish();
                startMain(selectedLanguage);
                break;
            case R.id.rl_ty_lang:
                setTyVisible();

                selectedLanguage = LanguageType.LANGUAGE_TY_TRADITIONAL;
                OAXApplication.state=6;
                SPUtils.setParam(this, SPConstant.LANGUAGE, "TH");
                finish();
                startMain(selectedLanguage);
                break;
        }
    }

    private void setTraditionalVisible() {
        iv_simplified_chinese.setVisibility(View.GONE);
        iv_english.setVisibility(View.GONE);
        rl_ft_china.setVisibility(View.VISIBLE);
    }

    private void startMain(int selectedLanguage) {

        MultiLanguageUtil.getInstance().updateLanguage(selectedLanguage);
        Intent intent = new Intent(SetLanguageActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Bundle bundle = new Bundle();
        bundle.putBoolean("Language", true);
        intent.putExtras(bundle);
        startActivity(intent);
        if (selectedLanguage == LanguageType.LANGUAGE_FOLLOW_SYSTEM) {
//            System.exit(0);
        }

    }

    /**
     * 检测制定View是否被遮住显示不全
     *
     * @return
     */
    protected boolean isCover(View view) {
        boolean cover = false;
        Rect rect = new Rect();
        cover = view.getGlobalVisibleRect(rect);
        if (cover) {
            if (rect.width() >= view.getMeasuredWidth() && rect.height() >= view.getMeasuredHeight()) {
                return !cover;
            }
        }
        return true;
    }

    private void setSimplifiedVisible() {
        iv_english.setVisibility(View.GONE);
        iv_ft_china.setVisibility(View.GONE);
        iv_ty_lang.setVisibility(View.GONE);
        iv_simplified_chinese.setVisibility(View.VISIBLE);
    }

    private void setEnglishVisible() {
        iv_ft_china.setVisibility(View.GONE);
        iv_english.setVisibility(View.VISIBLE);
        iv_simplified_chinese.setVisibility(View.GONE);
        iv_ty_lang.setVisibility(View.GONE);
    }

    private void setFollowSytemVisible() {
        iv_ft_china.setVisibility(View.VISIBLE);
        iv_english.setVisibility(View.GONE);
        iv_simplified_chinese.setVisibility(View.GONE);
        iv_ty_lang.setVisibility(View.GONE);
    }

    private void setTyVisible() {
        iv_ft_china.setVisibility(View.GONE);
        iv_english.setVisibility(View.GONE);
        iv_simplified_chinese.setVisibility(View.GONE);
        iv_ty_lang.setVisibility(View.VISIBLE);
    }

    private void refreshname() {
        mytradingToolbar.setTitleNameText(R.string.setting_language_title);
    }

}
