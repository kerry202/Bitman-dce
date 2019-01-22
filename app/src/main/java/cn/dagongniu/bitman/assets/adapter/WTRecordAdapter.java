package cn.dagongniu.bitman.assets.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.kline.bean.CommitteeBean;

/**
 * 充值 提现  适配器
 */
public class WTRecordAdapter extends BaseQuickAdapter<CommitteeBean, BaseViewHolder> {


    public WTRecordAdapter() {
        super(R.layout.top_with_record_item_layout);
    }

    @Override
    protected void convert(BaseViewHolder helper, CommitteeBean item) {
    }

}
