package cn.dagongniu.bitman.kline.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.math.BigDecimal;

import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.trading.bean.TradeListAndMarketOrdersBean;

public class TransactionAdapter extends BaseQuickAdapter<TradeListAndMarketOrdersBean.TradeListBean, BaseViewHolder> {
    private int priceDecimals = 8;//价格精度
    private int qtyDecimals = 8;//数量精度
    Context context;
    boolean bgColorState;

    public TransactionAdapter(Context context, boolean bgColorState) {
        super(R.layout.adapter_transaction);
        this.context = context;
        this.bgColorState = bgColorState;
    }

    @Override
    protected void convert(BaseViewHolder helper, TradeListAndMarketOrdersBean.TradeListBean item) {

        if (bgColorState) {
            helper.setBackgroundColor(R.id.ll_, context.getResources().getColor(R.color.kline_bg));
        } else {
            helper.setBackgroundColor(R.id.ll_, context.getResources().getColor(R.color.white));
        }

        //数量
        BigDecimal qty = item.getQty();
//        BigDecimal volume = qty.setScale(qtyDecimals, BigDecimal.ROUND_DOWN);
        helper.setText(R.id.tv_volume, qty.stripTrailingZeros().toPlainString());
        //价格
        BigDecimal price = item.getPrice();
//        BigDecimal priceDecimal = price.setScale(priceDecimals, BigDecimal.ROUND_DOWN);

        helper.setText(R.id.tv_price, price.stripTrailingZeros().toPlainString());

        if (item.getType() == 1) {
            helper.setTextColor(R.id.tv_price, context.getResources().getColor(R.color.bule));
        } else {
            helper.setTextColor(R.id.tv_price, context.getResources().getColor(R.color.kline_sell_bg));
        }
        helper.setText(R.id.tv_time, item.getCreateTime());
    }

    public void setDecimals(int priceDecimals, int qtyDecimals) {
        this.priceDecimals = priceDecimals;
        this.qtyDecimals = qtyDecimals;
    }
}
