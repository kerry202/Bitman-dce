package cn.dagongniu.bitman.account;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.account.bean.MyInvateBean;
import cn.dagongniu.bitman.account.presenter.MyInvatePresenter;
import cn.dagongniu.bitman.account.view.IMyInvateView;
import cn.dagongniu.bitman.base.BaseActivity;
import cn.dagongniu.bitman.customview.CommonToolbar;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.utils.ImgUtils;
import cn.dagongniu.bitman.utils.PageToolUtils;
import cn.dagongniu.bitman.utils.QRCode;
import cn.dagongniu.bitman.utils.ToastUtil;

/**
 * 我的邀请
 */
public class MineInvitationActivity extends BaseActivity implements IMyInvateView {
    @BindView(R.id.commontoolbar)
    CommonToolbar commontoolbar;
    @BindView(R.id.tv_register_count)
    TextView tvRegisterCount;
    @BindView(R.id.tv_authenticated_count)
    TextView tvAuthenticatedCount;
    @BindView(R.id.tv_copy_links)
    TextView tvCopyLinks;
    @BindView(R.id.tv_mine_links)
    TextView tvMineLinks;
    @BindView(R.id.tv_save_quick_mark)
    TextView tvSaveQuickMark;
    @BindView(R.id.im_add)
    ImageView imAdd;

    MyInvatePresenter myInvatePresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mine_invitation;
    }

    @Override
    protected void initView() {
        super.initView();
        initToolbar();
        myInvatePresenter = new MyInvatePresenter(this, RequestState.STATE_DIALOG);
        myInvatePresenter.getMyInvateModule();
    }

    private void initToolbar() {
        commontoolbar.setTitleText(getResources().getString(R.string.mine_invitation));
        commontoolbar.setOnCommonToolbarLeftClickListener(new CommonToolbar.CommonToolbarLeftClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }
        });
    }

    @OnClick({R.id.tv_copy_links, R.id.tv_save_quick_mark})
    public void onClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_copy_links:
                PageToolUtils.CopyText(this, tvMineLinks.getText().toString());
                break;
            case R.id.tv_save_quick_mark:
                try{
                    Bitmap bm = ((BitmapDrawable) ((ImageView) imAdd).getDrawable()).getBitmap();
                    ImgUtils.requestPermission(this);
                    if (ImgUtils.saveImageToGallery(this, bm)) {
                        ToastUtil.ShowToast(this.getResources().getString(R.string.save_success));
                    } else {
                        ToastUtil.ShowToast(this.getResources().getString(R.string.save_failure));
                    }
                }catch (Exception e){}

                break;
        }
    }

    @Override
    public void goLogin(String msg) {
        ToastUtil.ShowToast(msg);
    }

    @Override
    public void setMyInvateData(MyInvateBean myInvateBean) {
        tvRegisterCount.setText(myInvateBean.getData().getRegistercounts()+"");
        tvAuthenticatedCount.setText(myInvateBean.getData().getVertycounts()+"");
        tvMineLinks.setText(myInvateBean.getData().getInvateAddress());
        imAdd.setImageBitmap(QRCode.createQRCode(myInvateBean.getData().getInvateAddress()));
    }
}
