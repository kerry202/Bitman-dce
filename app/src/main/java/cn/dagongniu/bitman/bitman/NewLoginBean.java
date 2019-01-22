package cn.dagongniu.bitman.bitman;

public class NewLoginBean {
    /**
     * code : 0
     * msg : success
     * data : {"userId":327430,"token":"tsBTtvoBX7Ql96Gv|327430"}
     */

    public int code;
    public String msg;
    public DataBean data;

    @Override
    public String toString() {
        return "NewLoginBean{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public static class DataBean {
        /**
         * userId : 327430
         * token : tsBTtvoBX7Ql96Gv|327430
         */

        public int userId;
        public String token;

        @Override
        public String toString() {
            return "DataBean{" +
                    "userId=" + userId +
                    ", token='" + token + '\'' +
                    '}';
        }
    }
}
