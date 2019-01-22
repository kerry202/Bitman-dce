package cn.dagongniu.bitman.kline.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.math.BigDecimal;

import cn.dagongniu.bitman.R;
import cn.dagongniu.bitman.main.bean.IndexPageBean;
import cn.dagongniu.bitman.utils.Logs;

public class CurrentTransactionPriceView extends RelativeLayout {

    Context context;
    View view;
    private TextView mHigh, mLow, mVolume, mLimit, mCurrentPrice, mCurrentRnb, mCurrentLimit;
    //    IndexPageBean.DataBean.AllMaketListBean.MarketListBean lastCoin;
    int cnyDecimal = 2;
    private int mGreen;
    private int mGray;
    private int mRed;

    public CurrentTransactionPriceView(Context context) {
        this(context, null);
        this.context = context;
    }

    public CurrentTransactionPriceView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        view = View.inflate(context, R.layout.current_transaction_price_view, null);
        addView(view);
        mCurrentPrice = view.findViewById(R.id.tv_current_price);
        mCurrentRnb = view.findViewById(R.id.tv_current_rnb);
        mCurrentLimit = view.findViewById(R.id.tv_current_limit);
        mHigh = view.findViewById(R.id.tv_high);
        mLow = view.findViewById(R.id.tv_low);
        mVolume = view.findViewById(R.id.tv_volume);
        mLimit = view.findViewById(R.id.tv_limit);
        mGreen = getResources().getColor(R.color.bule);
        mGray = getResources().getColor(R.color.white);
        mRed = getResources().getColor(R.color.yellow5);
    }


    public void setDataChange(IndexPageBean.DataBean.AllMaketListBean.MarketListBean bean) {
        if (bean != null) {
            int priceDecimal = bean.getPriceDecimals();
            int volumeDecimal = bean.getQtyDecimals();
            //最近成交价 人民币 涨跌幅
            String blank = ":  ";
            setPriceChange(bean);
            //最高
            String high = new BigDecimal(String.valueOf(bean.getMaxPrice())).setScale(priceDecimal, BigDecimal.ROUND_DOWN).toPlainString();
            mHigh.setText( blank + high);
            //最低
            String low = new BigDecimal(String.valueOf(bean.getMinPrice())).setScale(priceDecimal, BigDecimal.ROUND_DOWN).toPlainString();
            mLow.setText(blank + low);
            //成交额
            String volume = new BigDecimal(String.valueOf(bean.getTotalAmount())).setScale(2, BigDecimal.ROUND_DOWN).toPlainString();
            mVolume.setText(blank + volume +" "+ bean.getMarketCoinName());
            //涨跌幅
            double incRate = Double.parseDouble(bean.getIncRate());
            String limit = new BigDecimal(String.valueOf(bean.getIncRate())).setScale(cnyDecimal, BigDecimal.ROUND_DOWN).toPlainString();
//            mLimit.setText(context.getResources().getString(R.string.limit_24H) + blank + String.valueOf(bean.getIncRate()) + "%");
            if (incRate > 0) {
                mLimit.setText(blank + "+" + limit + "%");
            } else {
                mLimit.setText(blank + limit + "%");
            }

        }

    }

    private void setPriceChange(IndexPageBean.DataBean.AllMaketListBean.MarketListBean currentCoin) {
//        if (lastCoin == null) {
        //最近成交价  lastTradePrice  priceDecimals  cnyPrice
        BigDecimal lastTradePrice = new BigDecimal(currentCoin.getLastTradePrice());
        BigDecimal decimal = lastTradePrice.setScale(currentCoin.getPriceDecimals(), BigDecimal.ROUND_DOWN);
        mCurrentPrice.setText(decimal.toPlainString());

        Logs.s("  人名币价格   "+currentCoin.getCnyPrice());
        if (currentCoin.getCoinId()==25){
            //人民币价格
            BigDecimal cny = currentCoin.getCnyPrice().multiply(new BigDecimal(currentCoin.getLastTradePrice()))
                    .setScale(4, BigDecimal.ROUND_DOWN);

            mCurrentRnb.setText("≈¥" + cny.toPlainString());
        }else {
            //人民币价格
            BigDecimal cny = currentCoin.getCnyPrice().multiply(new BigDecimal(currentCoin.getLastTradePrice()))
                    .setScale(2, BigDecimal.ROUND_DOWN);

            mCurrentRnb.setText("≈¥" + cny.toPlainString());
        }


        //涨跌幅
        double incRate = Double.parseDouble(currentCoin.getIncRate());
        BigDecimal incRateBigDecimal = new BigDecimal(currentCoin.getIncRate()).setScale(2, BigDecimal.ROUND_DOWN);
        if (incRate > 0) {
            mCurrentLimit.setText("+" + incRateBigDecimal.toPlainString() + "%");
        } else {
            mCurrentLimit.setText(incRateBigDecimal.toPlainString() + "%");
        }

        if (incRate > 0) {
            mCurrentPrice.setTextColor(mGreen);
            mCurrentLimit.setTextColor(mGreen);
        } else if (incRate == 0) {
            mCurrentPrice.setTextColor(mGray);
            mCurrentLimit.setTextColor(mGray);
        } else {
            mCurrentPrice.setTextColor(mRed);
            mCurrentLimit.setTextColor(mRed);
        }
    }
}
