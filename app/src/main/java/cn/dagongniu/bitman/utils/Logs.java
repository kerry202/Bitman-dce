package cn.dagongniu.bitman.utils;


public class Logs {

    public static boolean isDebug = true;

    public static void s(String str) {
        if (isDebug) {
            System.out.println("    ybLogs  " + str);
        }
    }
}
