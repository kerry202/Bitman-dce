package customview;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


import feature.Callback;
import teprinciple.updateapputils.R;


/**
 * Created by Teprinciple on 2016/10/13
 */
public class ConfirmDialog extends Dialog {

    private Callback callback;
    private TextView des_tv;
    private View line_v;
    private String des;

    public ConfirmDialog(Context context, String des, Callback callback) {
        super(context, R.style.CustomDialog);
        this.callback = callback;
        this.des = des;
        setCustomDialog();
        this.setCancelable(false);
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_confirm, null);

        des_tv = mView.findViewById(R.id.des_tv);
        line_v = mView.findViewById(R.id.line_v);
        if (des != null && des.length() > 0) {
            des_tv.setText(des);
            des_tv.setVisibility(View.VISIBLE);
            line_v.setVisibility(View.VISIBLE);
        }

        mView.findViewById(R.id.dialog_confirm_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.callback(1);
                ConfirmDialog.this.cancel();
            }
        });
        mView.findViewById(R.id.dialog_confirm_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.callback(0);
                ConfirmDialog.this.cancel();
            }
        });

        super.setContentView(mView);
    }
}
