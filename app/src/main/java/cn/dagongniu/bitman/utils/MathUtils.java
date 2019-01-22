package cn.dagongniu.bitman.utils;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class MathUtils {

    public static int value(BigDecimal maxQty, BigDecimal itmeQty) {

        double v = maxQty.doubleValue();
        double v1 = itmeQty.doubleValue();
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);
        String format = numberFormat.format(360f / (v / v1));
        float f = 0.0f;
        String ss = "";

        if (format.contains(".")) {
            String[] split = format.split(",");
            for (String s : split) {
                ss += s;
            }
            f = Float.parseFloat(format);
        } else {
            String[] split = format.split(",");
            for (String s : split) {
                ss += s;
            }
            f = (float) Integer.parseInt(ss);
        }

        int ii = (int) f;

        return ii;
    }
}
