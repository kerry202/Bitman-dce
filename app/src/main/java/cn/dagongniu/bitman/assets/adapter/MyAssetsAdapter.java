package cn.dagongniu.bitman.assets.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.math.BigDecimal;

import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.assets.bean.AssetsPropertyListBean;


/**
 * 资产首页 我的资产 适配器
 */
public class MyAssetsAdapter extends BaseQuickAdapter<AssetsPropertyListBean.DataBean.CoinListBean, BaseViewHolder> {

    Context context;
    boolean isOpenEyes = true;

    public MyAssetsAdapter(Context context, boolean isOpenEyes) {
        super(R.layout.my_assets_item_layout);
        this.context = context;
        this.isOpenEyes = isOpenEyes;
    }

    public void setOpenEyes(boolean openEyes) {
        isOpenEyes = openEyes;
    }

    @Override
    protected void convert(BaseViewHolder helper, AssetsPropertyListBean.DataBean.CoinListBean item) {
//        helper.setText(R.id.tv_market_name, item.getShortName());

        int adapterPosition = helper.getAdapterPosition();

        if (adapterPosition == 0) {
            helper.getView(R.id.my_assets_tv).setVisibility(View.VISIBLE);
            helper.getView(R.id.v_id).setVisibility(View.VISIBLE);
            helper.getView(R.id.RL_assets).setVisibility(View.VISIBLE);
        } else {
            View view = helper.getView(R.id.v_id);
            ViewGroup.LayoutParams lp;
            lp = view.getLayoutParams();
            lp.height = 28;
            view.setLayoutParams(lp);
            helper.getView(R.id.my_assets_tv).setVisibility(View.INVISIBLE);
            helper.getView(R.id.RL_assets).setVisibility(View.INVISIBLE);
            view.setVisibility(View.INVISIBLE);
        }

        ImageView bi_icon = helper.getView(R.id.bi_icon);
        TextView bi_name = helper.getView(R.id.bi_name);
        TextView available_tv = helper.getView(R.id.available_tv);
        TextView price_rmb_tv = helper.getView(R.id.price_rmb_tv);
        TextView freezing_quantity_tv = helper.getView(R.id.freezing_quantity_tv);

        Glide.with(context)
                .load(item.getImage())
                .into(bi_icon);

        bi_name.setText(item.getShortName());

        if (isOpenEyes) {
            BigDecimal banlance = new BigDecimal(item.getCnyPrice());
            price_rmb_tv.setText(subZeroAndDot(banlance.setScale(2, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString()));

            BigDecimal FreezingBanlance = new BigDecimal(item.getFreezingBanlance());
            freezing_quantity_tv.setText(subZeroAndDot(FreezingBanlance.setScale(8, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString()));

            BigDecimal TotalBanlance = new BigDecimal(item.getBanlance());
            available_tv.setText(subZeroAndDot(TotalBanlance.setScale(8, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString()));
        } else {
            BigDecimal banlance = new BigDecimal(item.getBanlance());
            available_tv.setText("******");

            BigDecimal FreezingBanlance = new BigDecimal(item.getFreezingBanlance());
            freezing_quantity_tv.setText("******");
            BigDecimal TotalBanlance = new BigDecimal(item.getTotalBanlance());
            price_rmb_tv.setText("******");
        }

    }

    public String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }

}
