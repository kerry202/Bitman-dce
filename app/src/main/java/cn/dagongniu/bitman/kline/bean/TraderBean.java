package cn.dagongniu.bitman.kline.bean;

import java.math.BigDecimal;
import java.util.List;

public class TraderBean {

    public List<TradeListBean> tradeList;

    @Override
    public String toString() {
        return "TraderBean{" +
                "tradeList=" + tradeList +
                '}';
    }

    public static class TradeListBean {
        /**
         * id : 841364
         * qty : 1.0
         * price : 1.02
         * type : 2
         * createTime : 19:19:16
         */

        public int id;
        public BigDecimal qty;
        public BigDecimal price;
        public int type;
        public String createTime;

        @Override
        public String toString() {
            return "TradeListBean{" +
                    "id=" + id +
                    ", qty=" + qty +
                    ", price=" + price +
                    ", type=" + type +
                    ", createTime='" + createTime + '\'' +
                    '}';
        }
    }
}
