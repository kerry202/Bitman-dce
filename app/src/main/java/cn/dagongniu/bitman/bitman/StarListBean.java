package cn.dagongniu.bitman.bitman;

import java.io.Serializable;
import java.util.List;

public class StarListBean implements Serializable {
    /**
     * code : 0
     * msg : success
     * data : {"list":[{"starId":1,"starName":"地球","starNameEn":"Earth","quadrantId":"1","reserves":"50.000000000000000000","remainReserves":"50.000000000000000000","minHoldings":"0.000000000000000000","principal1":"15.000000000000000000","interest1":"1.000000000000000000","coolDown1":10,"harvestNumber1":10,"principal2":"30.000000000000000000","interest2":"2.000000000000000000","coolDown2":360,"harvestNumber2":5,"principal3":"60.000000000000000000","interest3":"5.000000000000000000","coolDown3":720,"harvestNumber3":10}],"pages":{"page":1,"totalCount":"1","defaultPageSize":20}}
     */

    public int code;
    public String msg;
    public DataBean data;


    @Override
    public String toString() {
        return "StarListBean{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }


    public static class DataBean implements Serializable {
        /**
         * list : [{"starId":1,"starName":"地球","starNameEn":"Earth","quadrantId":"1","reserves":"50.000000000000000000","remainReserves":"50.000000000000000000","minHoldings":"0.000000000000000000","principal1":"15.000000000000000000","interest1":"1.000000000000000000","coolDown1":10,"harvestNumber1":10,"principal2":"30.000000000000000000","interest2":"2.000000000000000000","coolDown2":360,"harvestNumber2":5,"principal3":"60.000000000000000000","interest3":"5.000000000000000000","coolDown3":720,"harvestNumber3":10}]
         * pages : {"page":1,"totalCount":"1","defaultPageSize":20}
         */

        public PagesBean pages;
        public List<ListBean> list;


        @Override
        public String toString() {
            return "DataBean{" +
                    "pages=" + pages +
                    ", list=" + list +
                    '}';
        }


        public static class PagesBean implements Serializable {
            /**
             * page : 1
             * totalCount : 1
             * defaultPageSize : 20
             */

            public int page;
            public String totalCount;
            public int defaultPageSize;



            @Override
            public String toString() {
                return "PagesBean{" +
                        "page=" + page +
                        ", totalCount='" + totalCount + '\'' +
                        ", defaultPageSize=" + defaultPageSize +
                        '}';
            }


        }

        public static class ListBean implements Serializable {
            /**
             * starId : 1
             * starName : 地球
             * starNameEn : Earth
             * quadrantId : 1
             * reserves : 50.000000000000000000
             * remainReserves : 50.000000000000000000
             * minHoldings : 0.000000000000000000
             * principal1 : 15.000000000000000000
             * interest1 : 1.000000000000000000
             * coolDown1 : 10
             * harvestNumber1 : 10
             * principal2 : 30.000000000000000000
             * interest2 : 2.000000000000000000
             * coolDown2 : 360
             * harvestNumber2 : 5
             * principal3 : 60.000000000000000000
             * interest3 : 5.000000000000000000
             * coolDown3 : 720
             * harvestNumber3 : 10
             */

            public int starId;
            public String starName;
            public String starNameEn;
            public String quadrantId;
            public String reserves;
            public String remainReserves;
            public String minHoldings;
            public String principal1;
            public String interest1;
            public int coolDown1;
            public int harvestNumber1;
            public String principal2;
            public String interest2;
            public int coolDown2;
            public int harvestNumber2;
            public String principal3;
            public String interest3;
            public int coolDown3;
            public int harvestNumber3;


            @Override
            public String toString() {
                return "ListBean{" +
                        "starId=" + starId +
                        ", starName='" + starName + '\'' +
                        ", starNameEn='" + starNameEn + '\'' +
                        ", quadrantId='" + quadrantId + '\'' +
                        ", reserves='" + reserves + '\'' +
                        ", remainReserves='" + remainReserves + '\'' +
                        ", minHoldings='" + minHoldings + '\'' +
                        ", principal1='" + principal1 + '\'' +
                        ", interest1='" + interest1 + '\'' +
                        ", coolDown1=" + coolDown1 +
                        ", harvestNumber1=" + harvestNumber1 +
                        ", principal2='" + principal2 + '\'' +
                        ", interest2='" + interest2 + '\'' +
                        ", coolDown2=" + coolDown2 +
                        ", harvestNumber2=" + harvestNumber2 +
                        ", principal3='" + principal3 + '\'' +
                        ", interest3='" + interest3 + '\'' +
                        ", coolDown3=" + coolDown3 +
                        ", harvestNumber3=" + harvestNumber3 +
                        '}';
            }
        }
    }
}
