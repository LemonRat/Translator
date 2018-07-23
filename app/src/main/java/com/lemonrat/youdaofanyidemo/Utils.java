package com.lemonrat.youdaofanyidemo;

public class Utils {

    public static boolean isEnglish(String str) {
        if(str.getBytes().length != str.length()) {
            return false;
        } else {
            return true;
        }
    }
}
