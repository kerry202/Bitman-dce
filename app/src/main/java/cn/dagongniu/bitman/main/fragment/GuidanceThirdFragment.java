package cn.dagongniu.bitman.main.fragment;

import butterknife.OnClick;
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.base.BaseFragment;
import cn.dagongniu.bitman.constant.UMConstant;
import cn.dagongniu.bitman.utils.SkipActivityUtil;
import cn.dagongniu.bitman.utils.um.UMManager;

public class GuidanceThirdFragment extends BaseFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_guidance_third;
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @OnClick(R.id.tv_skip)
    public void onClicked() {
        UMManager.onEvent(mContext, UMConstant.GuidanceActivity, UMConstant.GuidanceActivity_third_jump);
        SkipActivityUtil.guidanceSkipToMain(getActivity(), mContext);
    }
}
