package cn.dagongniu.bitman.account.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.account.bean.MyEarningsItemBean;

public class MyEarningsInnerAdapter extends BaseQuickAdapter<MyEarningsItemBean.CoinsBean, BaseViewHolder> {

    public MyEarningsInnerAdapter() {
        super(R.layout.adapter_myearnings_inner);
    }

    @Override
    protected void convert(BaseViewHolder helper, MyEarningsItemBean.CoinsBean item) {
        helper.setText(R.id.tv_count, item.getCount().toPlainString());
        helper.setText(R.id.tv_coin_name, item.getCoinsName());
    }
}
