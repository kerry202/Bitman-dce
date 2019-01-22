package cn.dagongniu.bitman.bitman;

import java.util.List;

public class Bean {
    /**
     * code : 15009
     * msg : 用户尚未开挖该矿坑
     * data : []
     */

    public int code;
    public String msg;
    public List<?> data;

    @Override
    public String toString() {
        return "Bean{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
