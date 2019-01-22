package cn.dagongniu.bitman.my_ui;

import java.util.List;

public class CradBean {

    /**
     * code : 0
     * msg : success
     * success : true
     * data : [{"denomination":100,"coinName":"UCT","expiration_time":"2018-11-14 11:24:58"}]
     */

    public int code;
    public String msg;
    public boolean success;
    public List<DataBean> data;

    @Override
    public String toString() {
        return "CradBean{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", success=" + success +
                ", data=" + data +
                '}';
    }

    public static class DataBean {
        /**
         * denomination : 100
         * coinName : UCT
         * expiration_time : 2018-11-14 11:24:58
         */

        public String denomination;
        public String coinName;
        public String expirationTime;

        @Override
        public String toString() {
            return "DataBean{" +
                    "denomination=" + denomination +
                    ", coinName='" + coinName + '\'' +
                    ", expiration_time='" + expirationTime + '\'' +
                    '}';
        }
    }
}
