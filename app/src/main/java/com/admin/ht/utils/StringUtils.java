package com.admin.ht.utils;

import java.security.MessageDigest;
import java.util.Collection;
import java.util.Map;

/**
 * Created by Spec_Inc on 3/25/2017.
 */

public class StringUtils {

    /**
     * MD5加密
     * @param
     * @return 加密后的String
     */
    public final static String MD5(String s) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 判断obj是否不为null
     * @param obj
     * @return
     */
    public static boolean isNotEmpty(Object obj) {
        return (!(isEmpty(obj)));
    }
    /*
     * 判断是否为null
     */
    public static boolean isEmpty(Object o){
        if (o == null)
            return true;
        if (o instanceof String) {
            if (((String) o).trim().length() == 0) {
                return true;
            }
        } else if (o instanceof Collection) {
            if (((Collection<?>) o).isEmpty()) {
                return true;
            }
        } else if (o.getClass().isArray()) {
            if (((Object[]) (Object[]) o).length == 0) {
                return true;
            }
        } else if (o instanceof Map) {
            if (((Map<?, ?>) o).isEmpty()) {
                return true;
            }
        } else {
            return false;
        }
        return false;
    }

    //判断邮箱格式
    public static boolean isEmail(String email){
        return email.matches("\\w+@\\w+\\.\\w+");
    }
    public static boolean isPhone(String phone) {
        return phone.matches("^[1][3458][0-9]{9}$");
    }
}
