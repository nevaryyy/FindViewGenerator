package com.nevaryyy.fvg;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by waly6 on 2017/1/12.
 */
public class StringUtil {

    public static String[] splitCamelCase(String string) {
        int lastPos = 0;
        List<String> words = new ArrayList<>();

        for (int i = 1; i < string.length(); i ++) {
            if (Character.isUpperCase(string.charAt(i))) {
                if (i - lastPos > 1) {
                    words.add(string.substring(lastPos, i).toLowerCase());
                    lastPos = i;
                }
            }
        }
        if (lastPos < string.length()) {
            words.add(string.substring(lastPos, string.length()).toLowerCase());
        }

        return words.toArray(new String[0]);
    }

    public static String concatStringsWithUnderline(String[] strings) {
        return concatStringsWithUnderline(strings, 0, strings.length);
    }

    public static String concatStringsWithUnderline(String[] strings, int end) {
        return concatStringsWithUnderline(strings, 0, end);
    }

    public static String concatStringsWithUnderline(String[] strings, int begin, int end) {
        if (begin < 0 || end > strings.length) {
            throw new IllegalArgumentException();
        }
        if (begin == end) {
            return "";
        }

        String string = "";

        for (int i = begin; i < end; i ++) {
            string = string + strings[i] + "_";
        }
        if (string.charAt(string.length() - 1) == '_') {
            string = string.substring(0, string.length() - 1);
        }

        return string;
    }

    public static String toBigCamelCase(String string) {
        char c = Character.toUpperCase(string.charAt(0));
        return c + string.substring(1);
    }

    public static String toSmallCamelCase(String string) {
        char c = Character.toLowerCase(string.charAt(0));
        return c + string.substring(1);
    }

    public static String toBigCamelCase(String[] strings) {
        String res = "";

        for (String string : strings) {
            res += toBigCamelCase(string);
        }

        return res;
    }

    public static String toSmallCamelCase(String[] strings) {
        String res = "";

        for (String string : strings) {
            res += toBigCamelCase(string);
        }

        return toSmallCamelCase(res);
    }
}
