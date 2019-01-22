package cn.dagongniu.bitman.trading.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.trading.bean.MarketCategoryBean;

public class SelectMarketAdapter extends BaseQuickAdapter<MarketCategoryBean.DataBean, BaseViewHolder> {


    public SelectMarketAdapter() {
        super(R.layout.adapter_selectmarket);
    }

    @Override
    protected void convert(BaseViewHolder helper, MarketCategoryBean.DataBean item) {

        helper.setText(R.id.tv_coin_name, item.getCategoryName());

    }
}
