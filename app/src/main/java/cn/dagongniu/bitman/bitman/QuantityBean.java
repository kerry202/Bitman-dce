package cn.dagongniu.bitman.bitman;

public class QuantityBean {
    /**
     * code : 0
     * msg : success
     * data : {"userComputingPower":1234,"allComputingPower":12345,"userMiningRewards":6789,"allMiningRewards":67890}
     */

    public int code;
    public String msg;
    public DataBean data;

    @Override
    public String toString() {
        return "QuantityBean{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public static class DataBean {
        /**
         * userComputingPower : 1234
         * allComputingPower : 12345
         * userMiningRewards : 6789
         * allMiningRewards : 67890
         */

        public String userComputingPower;
        public String allComputingPower;
        public String userMiningRewards;
        public String allMiningRewards;

        @Override
        public String toString() {
            return "DataBean{" +
                    "userComputingPower=" + userComputingPower +
                    ", allComputingPower=" + allComputingPower +
                    ", userMiningRewards=" + userMiningRewards +
                    ", allMiningRewards=" + allMiningRewards +
                    '}';
        }
    }
}
