package cn.dagongniu.bitman.kline.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;


import java.math.BigDecimal;

import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.trading.bean.TradeListAndMarketOrdersBean;
import cn.dagongniu.bitman.utils.MathUtils;

public class CommitteeAdapter extends BaseQuickAdapter<TradeListAndMarketOrdersBean.BuyOrSellListBean, BaseViewHolder> {
    private int type = 0;
    private Context context;
    private int priceDecimals = 8;//价格精度
    private int qtyDecimals = 8;//数量精度
    private boolean isEmptyData = false;

    public static final int TYPE_BUY = 1;
    public static final int TYPE_SELL = 2;
    private boolean bgColor;
    private BigDecimal maxQty;

    public CommitteeAdapter(Context context, int type, boolean color) {
        super(R.layout.adapter_committee);
        this.type = type;
        this.context = context;
        this.bgColor = color;
    }

    public void maxQty(BigDecimal maxQty) {
        this.maxQty = maxQty;
    }

    @Override
    protected void convert(BaseViewHolder helper, TradeListAndMarketOrdersBean.BuyOrSellListBean item) {
        LinearLayout asset_ll = helper.getView(R.id.asset_ll);
        RelativeLayout asset_rl = helper.getView(R.id.asset_rl);
        int f = 0;
        try {
            f = MathUtils.value(maxQty, item.getQty());

        } catch (Exception e) {

        }

        ViewGroup.LayoutParams lp;
        lp = asset_ll.getLayoutParams();
        lp.width = f;
        asset_ll.setLayoutParams(lp);

        if (bgColor) {

            asset_rl.setBackgroundColor(mContext.getResources().getColor(R.color.kline_bg));
            if (type == 1) {
                asset_ll.setBackgroundResource(R.color.bule3);

            } else {

                asset_ll.setBackgroundResource(R.color.yellow3);
            }

        } else {
            asset_rl.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            if (type == 1) {
                asset_ll.setBackgroundResource(R.color.bule2);

            } else {

                asset_ll.setBackgroundResource(R.color.yellow4);
            }
        }

        if (!isEmptyData) {
            try {
                //数量
                BigDecimal volume = item.getQty().setScale(qtyDecimals, BigDecimal.ROUND_DOWN);
                helper.setText(R.id.tv_volume, volume.stripTrailingZeros().toPlainString());
                //价格
                BigDecimal price = item.getPrice().setScale(priceDecimals, BigDecimal.ROUND_DOWN);
                helper.setText(R.id.tv_price, price.stripTrailingZeros().toPlainString());
                if (type == TYPE_BUY) {
                    helper.setTextColor(R.id.tv_price, context.getResources().getColor(R.color.bule));
                } else if (type == TYPE_SELL) {
                    helper.setTextColor(R.id.tv_price, context.getResources().getColor(R.color.yellow5));
                }
            } catch (Exception e) {

            }
        } else {
            helper.setText(R.id.tv_volume, "--");
            helper.setText(R.id.tv_price, "--");
        }

    }

    public void setDecimals(int priceDecimals, int qtyDecimals) {
        this.priceDecimals = priceDecimals;
        this.qtyDecimals = qtyDecimals;
    }

    public void isEmptyData(boolean isEmptyData) {
        this.isEmptyData = isEmptyData;
    }
}
