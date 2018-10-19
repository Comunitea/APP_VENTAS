package com.cafedered.midban.utils.exceptions;

/**
 * Created by nacho on 19/06/15.
 */
public class ObjectArrayToStringFilter {
    public static String doFilter(Object[] array) {
        String result = "";
        for (Object obj : array) {
            result = result + obj + ";";
        }
        return result;
    }

    public static Object[] doUnfilter(String source) {
        return source.split(";");
    }
}
