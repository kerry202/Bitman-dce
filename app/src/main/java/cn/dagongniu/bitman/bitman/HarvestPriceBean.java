package cn.dagongniu.bitman.bitman;

public class HarvestPriceBean {
    /**
     * code : 0
     * msg : success
     * data : {"logId":90,"currTimestamp":1547550861,"startTimestamp":1547550861,"endTimestamp":1547550871,"coolDown":10}
     */

    public int code;
    public String msg;
    public DataBean data;

    @Override
    public String toString() {
        return "HarvestPriceBean{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public static class DataBean {
        /**
         * logId : 90
         * currTimestamp : 1547550861
         * startTimestamp : 1547550861
         * endTimestamp : 1547550871
         * coolDown : 10
         */

        public int logId;
        public int currTimestamp;
        public int startTimestamp;
        public int endTimestamp;
        public int coolDown;

        @Override
        public String toString() {
            return "DataBean{" +
                    "logId=" + logId +
                    ", currTimestamp=" + currTimestamp +
                    ", startTimestamp=" + startTimestamp +
                    ", endTimestamp=" + endTimestamp +
                    ", coolDown=" + coolDown +
                    '}';
        }
    }
}
