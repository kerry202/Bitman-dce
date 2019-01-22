package cn.dagongniu.bitman.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.account.adapter.ChooseCountriesAdapter;
import cn.dagongniu.bitman.account.bean.CountryCodeBean;
import cn.dagongniu.bitman.account.presenter.CountryCodePresenter;
import cn.dagongniu.bitman.base.BaseActivity;
import cn.dagongniu.bitman.base.IView;
import cn.dagongniu.bitman.https.RequestState;

/**
 * 选择 国家区号
 */
public class ChooseCountriesActivity extends BaseActivity implements IView {


    @BindView(R.id.im_colse)
    ImageView close;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    ChooseCountriesAdapter chooseCountriesAdapter;
    CountryCodePresenter countryCodePresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_choose_countries;
    }

    @Override
    protected void initView() {
        super.initView();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chooseCountriesAdapter = new ChooseCountriesAdapter(this);
        countryCodePresenter = new CountryCodePresenter(this, RequestState.STATE_DIALOG);
        countryCodePresenter.getCountryCodeModule();
    }

    @Override
    protected void initData() {
        super.initData();
        chooseCountriesAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intentRegistered = new Intent();
                intentRegistered.putExtra("numberType", "+" + chooseCountriesAdapter.getData().get(position).getCode());
                intentRegistered.putExtra("shortName", "+" + chooseCountriesAdapter.getData().get(position).getShortName());
                // 通过调用setResult方法返回结果给前一个activity。
                ChooseCountriesActivity.this.setResult(RESULT_OK, intentRegistered);
                // 关闭当前activity
                ChooseCountriesActivity.this.finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick(R.id.im_colse)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.im_colse:
                finish();
                break;
        }
    }


    @Override
    public void setData(Object data) {
        CountryCodeBean countryCodeBean = (CountryCodeBean) data;
        chooseCountriesAdapter.setNewData(countryCodeBean.getData());
        mRecyclerView.setAdapter(chooseCountriesAdapter);
    }
}
