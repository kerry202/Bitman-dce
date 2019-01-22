package cn.dagongniu.bitman.account;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anlia.photofactory.factory.PhotoFactory;
import com.anlia.photofactory.result.ResultData;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.account.presenter.FileUpdoadPresenter;
import cn.dagongniu.bitman.account.view.BottomCertificateFragment;
import cn.dagongniu.bitman.account.view.BottomCountriesFragment;
import cn.dagongniu.bitman.account.view.IFileUpdoadView;
import cn.dagongniu.bitman.base.BaseActivity;
import cn.dagongniu.bitman.customview.LQRPhotoSelectUtils;
import cn.dagongniu.bitman.https.Http;
import cn.dagongniu.bitman.https.HttpBaseBean;
import cn.dagongniu.bitman.https.HttpUtils;
import cn.dagongniu.bitman.https.OnBaseDataListener;
import cn.dagongniu.bitman.https.RequestState;
import cn.dagongniu.bitman.utils.BitmapCompressionUtils;
import cn.dagongniu.bitman.utils.Logs;
import cn.dagongniu.bitman.utils.ToastUtil;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import me.leefeng.promptlibrary.PromptButton;
import me.leefeng.promptlibrary.PromptButtonListener;
import me.leefeng.promptlibrary.PromptDialog;

public class CommitMsgActivity extends BaseActivity implements IFileUpdoadView {

    @BindView(R.id.commit_msg_iv)
    ImageView commitMsgIv;
    @BindView(R.id.upload_recycler)
    RecyclerView upload_recycler;
    @BindView(R.id.commit_bt)
    Button commit_bt;
    @BindView(R.id.msg_et)
    EditText msg_et;
    @BindView(R.id.right_tv)
    TextView right_tv;
    BottomCountriesFragment bottomCountriesFragment;
    BottomCertificateFragment bottomCertificateFragment;
    PhotoFactory photoFactory;
    private ArrayList<String> bitmaps = new ArrayList<>();
    private LoadMapAdapter loadMapAdapter;
    FileUpdoadPresenter fileUpdoadPresenter;
    private int count = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.commit_msg_layout;
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected void initData() {
        super.initData();

        fileUpdoadPresenter = new FileUpdoadPresenter(this, RequestState.STATE_DIALOG);
        photoFactory = new PhotoFactory(this);//(Context context)
        bottomCountriesFragment = new BottomCountriesFragment();
        bottomCertificateFragment = new BottomCertificateFragment();

        upload_recycler.setLayoutManager(new GridLayoutManager(this, 3));

        bitmaps.add(null);
        loadMapAdapter = new LoadMapAdapter(bitmaps);
        upload_recycler.setAdapter(loadMapAdapter);
        msg_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    String s = charSequence.toString();
                    count = 150 - s.length();
                    right_tv.setText(count + "/" + s.length());
                } catch (Exception e) {
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }


    @OnClick({R.id.commit_msg_iv, R.id.commit_bt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.commit_msg_iv:

                finish();
                break;
            case R.id.commit_bt:

                updoadFiles();

                break;
        }
    }

    @Override
    public void setFullFaceContainerSuccess(String data) {
        Logs.s("  setFullFaceContainerSuccess    " + data);
        bitmaps.add(0, data);
        loadMapAdapter.setNewData(bitmaps);

    }

    @Override
    public void setReverseFaceContainerSuccess(String data) {
        Logs.s("  setReverseFaceContainerSuccess    " + data);
    }

    @Override
    public void setFullFaceContainerfailure(String data) {

    }

    @Override
    public void setReverseContainerfailure(String data) {

    }


    public class LoadMapAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
        private List<String> data;

        public LoadMapAdapter(@Nullable List<String> data) {
            super(R.layout.load_map_adapter_layout, data);
            this.data = data;
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            int adapterPosition = helper.getAdapterPosition();

            RelativeLayout rl_ = helper.getView(R.id.rl_);
            ImageView map_close_iv = helper.getView(R.id.map_close_iv);
            ImageView load_map = helper.getView(R.id.load_map);
            ImageView load_map1 = helper.getView(R.id.load_map1);

            if (data.size() == 4) {
                if (adapterPosition == (data.size() - 1)) {
                    rl_.setVisibility(View.GONE);
//                    map_close_iv.setVisibility(View.INVISIBLE);
//                    load_map1.setVisibility(View.INVISIBLE);
//                    load_map.setVisibility(View.INVISIBLE);
//                    load_map.setImageResource(R.mipmap.map_upload_icon);

                } else {
                    map_close_iv.setVisibility(View.VISIBLE);
                    load_map.setVisibility(View.INVISIBLE);
                    load_map1.setVisibility(View.VISIBLE);

                    Glide.with(mContext)
                            .load(item)
                            .into(load_map1);
                }
            } else {
                if (adapterPosition == (data.size() - 1)) {
                    map_close_iv.setVisibility(View.INVISIBLE);
                    load_map1.setVisibility(View.INVISIBLE);
                    load_map.setVisibility(View.VISIBLE);
                    load_map.setImageResource(R.mipmap.map_upload_icon);

                } else {
                    map_close_iv.setVisibility(View.VISIBLE);
                    load_map.setVisibility(View.INVISIBLE);
                    load_map1.setVisibility(View.VISIBLE);

                    Glide.with(mContext)
                            .load(item)
                            .into(load_map1);
                }
            }


            map_close_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    data.remove(adapterPosition);
                    notifyDataSetChanged();
                }
            });

            load_map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    choosePhotos(load_map);
                }
            });
        }
    }

    private LQRPhotoSelectUtils mLqrPhotoSelectUtils;
    Uri outputUri1 = null;//上传照片路径地址

    private void init() {
        // 1、创建LQRPhotoSelectUtils（一个Activity对应一个LQRPhotoSelectUtils）
        mLqrPhotoSelectUtils = new LQRPhotoSelectUtils(this, new LQRPhotoSelectUtils.PhotoSelectListener() {
            @Override
            public void onFinish(File outputFile, Uri outputUri) {
                // 4、当拍照或从图库选取图片成功后回调
                outputUri1 = outputUri;
                try {
                    FileInputStream fis = new FileInputStream(outputFile);
                    Bitmap bitmap = BitmapFactory.decodeStream(fis);

                    File file = BitmapCompressionUtils.compressImage(bitmap);
                    ArrayList<File> arrayList = new ArrayList<>();
                    arrayList.add(file);

                    if (bitmaps != null && bitmaps.size() < 4) {
                        fileUpdoadPresenter.UploadImg(arrayList, String.valueOf(System.currentTimeMillis()) + ".jpg", "image", 0);
                    } else {
//                        ToastUtil.ShowToast(getResources().getString(R.string.max_two_str));
                    }

                } catch (Exception e) {

                }

            }
        }, false);//true裁剪，false不裁剪

    }


    private void updoadFiles() {

        String trim = msg_et.getText().toString().trim();
        if (trim.length() >= 10) {
            HashMap<String, Object> hashMap = new HashMap();
            hashMap.put("description", trim);
            hashMap.put("image", bitmaps);

            HttpUtils.getInstance().postMsg(Http.upload_msg, hashMap, mContext, new OnBaseDataListener<String>() {
                @Override
                public void onNewData(String data) {
                    HttpBaseBean httpBaseBean = new Gson().fromJson(data, HttpBaseBean.class);
                    if (httpBaseBean.isSuccess()) {
                        CommitMsgActivity.this.finish();
                        ToastUtil.ShowToast(getResources().getString(R.string.msg_commit_success));

                    } else {
                        ToastUtil.ShowToast(httpBaseBean.getMsg());
                    }
                }

                @Override
                public void onError(String code) {
                    ToastUtil.ShowToast(getResources().getString(R.string.msg_commit_error));
                }
            }, RequestState.STATE_REFRESH);
        } else {
            ToastUtil.ShowToast(getResources().getString(R.string.msg_commit_min));
        }
    }

    /**
     * ------------------------------
     */

    @PermissionSuccess(requestCode = LQRPhotoSelectUtils.REQ_TAKE_PHOTO)
    private void takePhoto() {
        mLqrPhotoSelectUtils.takePhoto();
    }

    @PermissionSuccess(requestCode = LQRPhotoSelectUtils.REQ_SELECT_PHOTO)
    private void selectPhoto() {
        mLqrPhotoSelectUtils.selectPhoto();
    }

    @PermissionFail(requestCode = LQRPhotoSelectUtils.REQ_TAKE_PHOTO)
    private void showTip1() {
        //        Toast.makeText(getApplicationContext(), "不给我权限是吧，那就别玩了", Toast.LENGTH_SHORT).show();
        showDialog();
    }

    @PermissionFail(requestCode = LQRPhotoSelectUtils.REQ_SELECT_PHOTO)
    private void showTip2() {
        //        Toast.makeText(getApplicationContext(), "不给我权限是吧，那就别玩了", Toast.LENGTH_SHORT).show();
        showDialog();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    public void showDialog() {
        //创建对话框创建器
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //设置对话框显示小图标
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        //设置标题
        builder.setTitle(this.getResources().getString(R.string.permissions));
        //设置正文
        builder.setMessage(this.getResources().getString(R.string.permissions_show));

        //添加确定按钮点击事件
        builder.setPositiveButton(this.getResources().getString(R.string.go_setting_hint), new DialogInterface.OnClickListener() {//点击完确定后，触发这个事件

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //这里用来跳到手机设置页，方便用户开启权限
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + CommitMsgActivity.this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        //添加取消按钮点击事件
        builder.setNegativeButton(this.getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        //使用构建器创建出对话框对象
        AlertDialog dialog = builder.create();
        dialog.show();//显示对话框
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 2、在Activity中的onActivityResult()方法里与LQRPhotoSelectUtils关联
        mLqrPhotoSelectUtils.attachToActivityForResult(requestCode, resultCode, data);

    }

    /**
     * 从照片中选取
     *
     * @param imageView
     */
    public void choosePhotos(ImageView imageView) {
        init();
        //6.0以上动态获取权限  申请权限，REQUEST_TAKE_PHOTO_PERMISSION是自定义的常量
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }

        /********************底部AlertSheet*************/
        PromptButton cancle = new PromptButton(this.getResources().getString(R.string.cancel), null);

        cancle.setTextColor(getResources().getColor(R.color.text_str));
        cancle.setTextSize(getResources().getDimension(R.dimen.dp8));

        PromptDialog promptDialog = new PromptDialog(this);

        PromptButton promptButton = new PromptButton(this.getResources().getString(R.string.pictures), new PromptButtonListener() {
            @Override
            public void onClick(PromptButton promptButton) {
                PermissionGen.with(CommitMsgActivity.this)
                        .addRequestCode(LQRPhotoSelectUtils.REQ_TAKE_PHOTO)
                        .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA
                        ).request();
            }
        });

        promptButton.setTextColor(getResources().getColor(R.color.text_str));
        promptButton.setTextSize(getResources().getDimension(R.dimen.dp8));

        PromptButton image = new PromptButton(this.getResources().getString(R.string.choose_photos), new PromptButtonListener() {
            @Override
            public void onClick(PromptButton promptButton) {
                photoFactory.FromGallery()
                        .StartForResult(new PhotoFactory.OnResultListener() {
                            @Override
                            public void OnCancel() {

                            }

                            @Override
                            public void OnSuccess(ResultData resultData) {
                                try {
                                    Bitmap bitmap = resultData.GetBitmap();
                                    File file = BitmapCompressionUtils.compressImage(bitmap);
                                    if (bitmaps.size() < 4) {
                                        Logs.s("   filesfiles " + bitmaps.size());
                                        ArrayList<File> arrayList = new ArrayList<>();
                                        arrayList.add(file);
                                        fileUpdoadPresenter.UploadImg(arrayList, String.valueOf(System.currentTimeMillis()) + ".jpg", "image", 0);
                                    }
                                } catch (Exception e) {
                                }
                            }
                        });
            }
        });
        image.setTextColor(getResources().getColor(R.color.text_str));
        image.setTextSize(getResources().getDimension(R.dimen.dp8));

        promptDialog.showAlertSheet("", true, cancle, promptButton, image);
        promptDialog.getDefaultBuilder().backAlpha(150);
    }


}


