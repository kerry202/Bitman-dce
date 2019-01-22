package cn.dagongniu.bitman.zxing;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;


import java.util.Collection;
import java.util.Map;

import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.zxing.camera.CameraManager;
import cn.dagongniu.bitman.zxing.manager.BeepManager;
import cn.dagongniu.bitman.zxing.manager.InactivityTimer;
import cn.dagongniu.bitman.zxing.utils.ZXingUtils;

/**
 * @author yangbo
 */
public final class CaptureActivity extends Activity implements SurfaceHolder.Callback {

    private static final String TAG = CaptureActivity.class.getSimpleName();

    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private Result lastResult;
    private boolean hasSurface;
    private Collection<BarcodeFormat> decodeFormats;
    private Map<DecodeHintType, ?> decodeHints;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;

    private LinearLayout btn_scan_light;
    private ImageView iv_scan_light;
    private TextView tv_scan_light;
    private ImageView btn_close;
    private TextView btn_photo;
    private RelativeLayout btn_dialog_bg;
    private ImageView ivScreenshot;
    //闪光灯是否打开
    private boolean is_light_on = false;
    private boolean beepFlag = true;
    private boolean vibrateFlag = true;
    private SurfaceView surfaceView;

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.mn_scan_capture);
        surfaceView = findViewById(R.id.preview_view);
        viewfinderView = findViewById(R.id.viewfinder_view);
        btn_scan_light = findViewById(R.id.btn_scan_light);
        iv_scan_light = findViewById(R.id.iv_scan_light);
        tv_scan_light = findViewById(R.id.tv_scan_light);
        btn_close = findViewById(R.id.btn_close);
        btn_photo = findViewById(R.id.btn_photo);
        btn_dialog_bg = findViewById(R.id.btn_dialog_bg);
        ivScreenshot = findViewById(R.id.ivScreenshot);
        btn_dialog_bg.setVisibility(View.GONE);

        //初始化相关参数
        initIntent();

        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);

        //点击事件
        btn_scan_light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_light_on) {
                    is_light_on = false;
                    cameraManager.offLight();
                    iv_scan_light.setImageResource(R.mipmap.mn_icon_scan_flash_light_off);
                    tv_scan_light.setText(getString(R.string.flashlight_tv));
                } else {
                    is_light_on = true;
                    cameraManager.openLight();
                    iv_scan_light.setImageResource(R.mipmap.mn_icon_scan_flash_light_off);
                    tv_scan_light.setText(getString(R.string.close_flashlight_tv));
                }
            }
        });

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishCancle();
            }
        });

        btn_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromAlbum();
            }
        });

        btn_dialog_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initIntent() {
        Intent intent = getIntent();
        String hintText = intent.getStringExtra(MNScanManager.INTENT_KEY_HINTTEXT);
        String scanColor = intent.getStringExtra(MNScanManager.INTENT_KEY_SCSNCOLOR);
        boolean photoFlag = intent.getBooleanExtra(MNScanManager.INTENT_KEY_PHOTO_FLAG, true);
        beepFlag = intent.getBooleanExtra(MNScanManager.INTENT_KEY_BEEP_FLAG, true);
        vibrateFlag = intent.getBooleanExtra(MNScanManager.INTENT_KEY_VIBRATE_FLAG, true);
        if (!TextUtils.isEmpty(hintText)) {
//            viewfinderView.setHintText(hintText);
        }
        if (!TextUtils.isEmpty(scanColor)) {
            viewfinderView.setScanLineColor(Color.parseColor(scanColor));
        }
        if (!photoFlag) {
            btn_photo.setVisibility(View.GONE);
        }
    }

    /**
     * 获取相册中的图片
     */
    public void getImageFromAlbum() {
        Intent intent = new Intent();
        /* 开启Pictures画面Type设定为image */
        intent.setType("image/*");
        /* 使用Intent.ACTION_GET_CONTENT这个Action */
//        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setAction(Intent.ACTION_PICK);
        /* 取得相片后返回本画面 */
        startActivityForResult(intent, 1000);
        //开始转Dialog
        btn_dialog_bg.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //去相册选择图片
        if (requestCode == 1000) {
            if (data == null) {
                //隐藏Dialog
                btn_dialog_bg.setVisibility(View.GONE);
                return;
            }
            final Uri uri = data.getData();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmapChoose = ZXingUtils.decodeUriAsBitmap(CaptureActivity.this, uri);
                    if (bitmapChoose != null) {
                        final String decodeQRCodeFromBitmap = ZXingUtils.syncDecodeQRCode(bitmapChoose);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btn_dialog_bg.setVisibility(View.GONE);
                                Log.i(TAG, "decodeQRCode:" + decodeQRCodeFromBitmap);
                                if (TextUtils.isEmpty(decodeQRCodeFromBitmap)) {
                                    Toast.makeText(CaptureActivity.this, getString(R.string.no_zxing), Toast.LENGTH_SHORT).show();
                                } else {
                                    finishSuccess(decodeQRCodeFromBitmap);
                                }
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btn_dialog_bg.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            }).start();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        // CameraManager must be initialized here, not in onCreate(). This is necessary because we don't
        // want to open the camera driver and measure the screen size if we're going to show the help on
        // first launch. That led to bugs where the scanning rectangle was the wrong size and partially
        // off screen.
        if (handler != null && cameraManager != null && cameraManager.isOpen()) {
            return;
        }
        cameraManager = new CameraManager(getApplication());
        viewfinderView.setCameraManager(cameraManager);

        handler = null;
        lastResult = null;

        resetStatusView();

        beepManager.updatePrefs(beepFlag, vibrateFlag);

        inactivityTimer.onResume();
        decodeFormats = null;
        characterSet = null;

        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            // 防止sdk8的设备初始化预览异常
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            // The activity was paused but not stopped, so the surface still exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(surfaceHolder);
        } else {
            // Install the callback and wait for surfaceCreated() to init the camera.
            surfaceHolder.addCallback(this);
        }
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        beepManager.close();
        cameraManager.closeDriver();
        //historyManager = null; // Keep for onActivityResult
        if (!hasSurface) {
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        //取消扫码
        finishCancle();
    }

    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        lastResult = rawResult;
        //播放声音和震动
        beepManager.playBeepSoundAndVibrate();
        //关闭页面
        finishSuccess(lastResult.getText());
        //图片显示：测试才显示
        ivScreenshot.setImageBitmap(barcode);

    }

    private void finishFailed(String errorMsg) {
        Intent intent = new Intent();
        intent.putExtra(MNScanManager.INTENT_KEY_RESULT_ERROR, errorMsg);
        this.setResult(MNScanManager.RESULT_FAIL, intent);
        this.finish();
        //关闭窗体动画显示
        this.overridePendingTransition(R.anim.mn_scan_activity_bottom_out, 0);
    }

    private void finishCancle() {
        this.setResult(MNScanManager.RESULT_CANCLE, null);
        this.finish();
        //关闭窗体动画显示
        this.overridePendingTransition(R.anim.mn_scan_activity_bottom_out, 0);
    }

    private void finishSuccess(String result) {
        Intent intent = new Intent();
        intent.putExtra(MNScanManager.INTENT_KEY_RESULT_SUCCESS, result);
        this.setResult(MNScanManager.RESULT_SUCCESS, intent);
        this.finish();
        //关闭窗体动画显示
        this.overridePendingTransition(R.anim.mn_scan_activity_bottom_out, 0);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // do nothing
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            displayFrameworkBugMessageAndExit("SurfaceHolder 不存在");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a RuntimeException.
            if (handler == null) {
                handler = new CaptureActivityHandler(this, decodeFormats, decodeHints, characterSet, cameraManager);
            }
        } catch (Exception e) {
            displayFrameworkBugMessageAndExit(getString(R.string.start_camera_error) + e.toString());
        }
    }

    private void displayFrameworkBugMessageAndExit(String errorMessage) {
        finishFailed(errorMessage);
    }

    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
        resetStatusView();
    }

    private void resetStatusView() {
        viewfinderView.setVisibility(View.VISIBLE);
        lastResult = null;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }
}
