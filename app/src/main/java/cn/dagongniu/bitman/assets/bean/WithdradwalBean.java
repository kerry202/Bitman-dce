package cn.dagongniu.bitman.assets.bean;

import java.util.List;

public class WithdradwalBean {


    /**
     * code : 1
     * success : true
     * msg : null
     * data : [{"address":"0x1f109d18844e32a665c70311e3e0f89ec84f58f1","lock_status":0,"code":"T3GrW1Zl","create_time":1542425783000,"level":1,"parent_coin_id":1,"source":3,"type":1,"email_status":0,"check_status":0,"register_time":1541687483000,"register_type":1,"password":"WkRrNVlURmlaR0V5T0RJME5HSTFaV0prWlRsaE0yUTRNakUzWXpZMU9ETT0=","update_time":1542425783000,"user_id":320163,"phone":"008613261936996","phone_status":1,"id":10251,"need_transaction_password":0,"google_status":0}]
     */

    public String code;
    public boolean success;
    public Object msg;
    public List<DataBean> data;

    public static class DataBean {
        /**
         * address : 0x1f109d18844e32a665c70311e3e0f89ec84f58f1
         * lock_status : 0
         * code : T3GrW1Zl
         * create_time : 1542425783000
         * level : 1
         * parent_coin_id : 1
         * source : 3
         * type : 1
         * email_status : 0
         * check_status : 0
         * register_time : 1541687483000
         * register_type : 1
         * password : WkRrNVlURmlaR0V5T0RJME5HSTFaV0prWlRsaE0yUTRNakUzWXpZMU9ETT0=
         * update_time : 1542425783000
         * user_id : 320163
         * phone : 008613261936996
         * phone_status : 1
         * id : 10251
         * need_transaction_password : 0
         * google_status : 0
         */

        public String address;
        public int lock_status;
        public String code;
        public long create_time;
        public int level;
        public int parent_coin_id;
        public int source;
        public int type;
        public int email_status;
        public int check_status;
        public long register_time;
        public int register_type;
        public String password;
        public long update_time;
        public String id_name;
        public int user_id;
        public String phone;
        public int phone_status;
        public int id;
        public int need_transaction_password;
        public int google_status;
    }
}
