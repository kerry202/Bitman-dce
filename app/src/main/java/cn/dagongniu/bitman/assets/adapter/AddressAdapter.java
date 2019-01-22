package cn.dagongniu.bitman.assets.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.assets.bean.CoinAddressListBean;

/**
 * 添加地址  适配器
 */
public class AddressAdapter extends BaseQuickAdapter<CoinAddressListBean.DataBean.ListBean, BaseViewHolder> {


    Context context;

    public AddressAdapter(Context context) {
        super(R.layout.bottom_address_item_layout);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, CoinAddressListBean.DataBean.ListBean data) {
        helper.setText(R.id.tv_address, data.getAddress());
        helper.setText(R.id.tv_remrak, data.getRemark());
    }
}
