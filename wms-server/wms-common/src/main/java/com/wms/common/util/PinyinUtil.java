package com.wms.common.util;

import net.sourceforge.pinyin4j.PinyinHelper;

public class PinyinUtil {

    public static String toFirstChar(String chinese) {
        if (chinese == null || chinese.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (char c : chinese.toCharArray()) {
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c);
            if (pinyinArray != null && pinyinArray.length > 0) {
                sb.append(pinyinArray[0].charAt(0));
            } else {
                sb.append(c);
            }
        }
        return sb.toString().toUpperCase();
    }

    public static String toFullPinyin(String chinese) {
        if (chinese == null || chinese.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (char c : chinese.toCharArray()) {
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c);
            if (pinyinArray != null && pinyinArray.length > 0) {
                sb.append(pinyinArray[0].replaceAll("[^a-zA-Z]", ""));
            } else {
                sb.append(c);
            }
        }
        return sb.toString().toLowerCase();
    }
}
