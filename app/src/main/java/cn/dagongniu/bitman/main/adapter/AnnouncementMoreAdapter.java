package cn.dagongniu.bitman.main.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.main.bean.NoticeCenterMoreBean;
import cn.dagongniu.bitman.utils.DateUtils;
import cn.dagongniu.bitman.utils.Logs;

/**
 * 委托 fragment适配器
 */
public class AnnouncementMoreAdapter extends BaseQuickAdapter<NoticeCenterMoreBean.DataBean.ListBean, BaseViewHolder> {

    public AnnouncementMoreAdapter() {
        super(R.layout.announcenment_more_item_layout);
    }

    @Override
    protected void convert(BaseViewHolder helper, NoticeCenterMoreBean.DataBean.ListBean item) {

        Logs.s("   NoticeCenterMoreBean    " + item);
        helper.setText(R.id.tv_title, item.getName());

        String tiem = DateUtils.formatDate(item.getReleaseTime());
        helper.setText(R.id.tv_releasetime, tiem);
    }
}
