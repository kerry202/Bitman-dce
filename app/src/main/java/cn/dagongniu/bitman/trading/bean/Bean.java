package cn.dagongniu.bitman.trading.bean;

public class Bean {
    /**
     * code : 0
     * msg : success
     * success : true
     */

    public int code;
    public String msg;
    public boolean success;

    @Override
    public String toString() {
        return "Bean{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", success=" + success +
                '}';
    }
}
