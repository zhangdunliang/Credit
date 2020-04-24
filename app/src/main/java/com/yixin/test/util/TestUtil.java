package com.yixin.test.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xiaochangyou on 2017/9/20.
 */

public class TestUtil {

    /**
     * @param context
     * @param key
     * @param value
     * @作者:xiaochangyou
     * @创建时间:2016-8-8 下午3:07:08
     * @描述:TODO[存值]
     */
    public static void saveString(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("CreadeTest", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * @param context
     * @param key
     * @return String
     * @作者:xiaochangyou
     * @创建时间:2016-8-8 下午3:07:27
     * @描述:TODO[取值,默认为空字符串]
     */
    public static String getString(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("CreadeTest", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    /**
     * @param context
     * @param key
     * @param value
     * @作者:xiaochangyou
     * @创建时间:2016-8-8 下午3:07:08
     * @描述:TODO[存值]
     */
    public static void saveInt(Context context, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("CreadeTest", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * @param context
     * @param key
     * @return int
     * @作者:xiaochangyou
     * @创建时间:2016-8-8 下午3:07:27
     * @描述:TODO[取值,默认为空字符串]
     */
    public static int getInt(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("CreadeTest", Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }

    /**
     * @param phoneNum
     * @return boolean
     * @作者:xiaochangyou
     * @创建时间:2016-10-19 下午6:21:38
     * @描述:TODO[检验手机号是否合格]
     */
    public static boolean isPhoneNum(String phoneNum) {
        // 手机号码: 13[0-9], 14[5,7], 15[0, 1, 2, 3, 5, 6, 7, 8, 9], 17[0, 1, 6, 7, 8], 18[0-9]
        String MOBILE = "^1(3[0-9]|4[57]|5[0-35-9]|7[01678]|8[0-9])\\d{8}$";
        // 中国移动：China Mobile
        //134,135,136,137,138,139,147,150,151,152,157,158,159,170,178,182,183,184,187,188
        // String CM = "^1(3[4-9]|4[7]|5[0-27-9]|7[08]|8[2-478])\\d{8}$";
        // 中国联通：China Unicom 130,131,132,145,152,155,156,170,171,176,185,186
        // String CU = "^1(3[0-2]|4[5]|5[256]|7[016]|8[56])\\d{8}$";
        // 中国电信：China Telecom 133,134,153,170,177,180,181,189
        // String CT = "^1(3[34]|53|7[07]|8[019])\\d{8}$";
        Pattern p = Pattern.compile(MOBILE);
        Matcher matcher = p.matcher(phoneNum);
        return matcher.matches();
    }

}
