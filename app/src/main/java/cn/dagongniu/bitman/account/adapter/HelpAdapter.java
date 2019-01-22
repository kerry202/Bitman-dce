package cn.dagongniu.bitman.account.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.socks.library.KLog;

import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.account.bean.HelpBean;
import cn.dagongniu.bitman.utils.DateUtils;

public class HelpAdapter extends BaseQuickAdapter<HelpBean.ListBean, BaseViewHolder> {

    public HelpAdapter() {
        super(R.layout.adapter_help);
    }

    @Override
    protected void convert(BaseViewHolder helper, HelpBean.ListBean item) {
        helper.setText(R.id.tv_title, item.getName());
        String time = "";
        try {
            long t = Long.parseLong(item.getReleaseTime());
            time = DateUtils.formatDate(t);
        } catch (Exception e) {
            KLog.d("parseLong Exception = " + e.getMessage());
        }
        helper.setText(R.id.tv_time, time);
    }
}
