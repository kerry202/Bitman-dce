package cn.dagongniu.bitman.trading.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.trading.bean.TransactionIndexBean;


/**
 * 首页 买 卖 数据适配器
 */
public class TradingFallAdapter extends BaseQuickAdapter<TransactionIndexBean.DataBean.MarketOrdersMapBean.SellListBean, BaseViewHolder> {

    Context context;


    public TradingFallAdapter(Context context) {
        super(R.layout.trading_fall_item_layout);
        this.context = context;
    }


    @Override
    protected void convert(BaseViewHolder helper, TransactionIndexBean.DataBean.MarketOrdersMapBean.SellListBean item) {
        //if (currentType == TYPE_BUY) {
        helper.setTextColor(R.id.fall_item_jiage, context.getResources().getColor(R.color.button_bu));
        //} else if (currentType == TYPE_SELL) {
        helper.setTextColor(R.id.fall_item_jiage, context.getResources().getColor(R.color.kline_sell_bg));
        //}

        //helper.setText(R.id.fall_item_jiage, item.getPrice());
        //helper.setText(R.id.fall_item_count, item.getQty());
    }
}
