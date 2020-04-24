package com.yxjr.credit.util;

import com.yxjr.credit.constants.SpConstant;
import com.yxjr.credit.security.YxjrSecurity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * All rights Reserved, Designed By ClareShaw
 * 
 * @公司:益芯金融
 * @作者:xiaochangyou
 * @版本:V1.0
 * @创建时间:2016-7-18 上午11:01:27
 * @描述:TODO[SharedPreferences工具类]
 */
public class YxStoreUtil {
	/** SharedPreferences name */
	private static String spName = null;

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:08:30
	 * @描述:TODO[获取SharedPreferences名字]
	 * @return String SharedPreferences名字
	 */
	public static String getSpName() {
		return spName;
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:08:11
	 * @描述:TODO[设置SharedPreferences名字]
	 * @param spName
	 *            SharedPreferences名字
	 */
	public static void setSpName(String spName) {
		YxStoreUtil.spName = spName;
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:07:35
	 * @描述:TODO[存值String]
	 * @param context
	 * @param key
	 *            键
	 * @param value
	 *            值
	 */
	public static void save(Context context, String key, String value) {
		String aesEncryptValue = YxjrSecurity.AesEncrypt(context, value);
		String aesEncryptKey = YxjrSecurity.AesEncrypt(context, key);
		SharedPreferences sharedPreferences = context.getSharedPreferences(getSpName(), Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString(aesEncryptKey, aesEncryptValue);
		editor.commit();
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:07:08
	 * @描述:TODO[存值Boolean]
	 * @param context
	 * @param key
	 *            键
	 * @param value
	 *            值
	 */
	public static void save(Context context, String key, boolean value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(getSpName(), Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:06:40
	 * @描述:TODO[取值String]
	 * @param context
	 * @param key
	 *            键
	 * @return String 默认为""
	 */
	public static String get(Context context, String key) {
		String aesEncryptKey = YxjrSecurity.AesEncrypt(context, key);
		SharedPreferences sharedPreferences = context.getSharedPreferences(getSpName(), Context.MODE_PRIVATE);
		if (aesEncryptKey.equals(SpConstant.LATITUDE) || aesEncryptKey.equals(SpConstant.LONGITUDE)) {//没有定位相关权限清除相关缓存
			if (!PermissionUtil.isLocPer(context).equals(PermissionUtil.AUTHORIZED) || !PermissionUtil.isGpsService(context).equals(PermissionUtil.AUTHORIZED)) {
				YxStoreUtil.save(context, SpConstant.LATITUDE, "");
				YxStoreUtil.save(context, SpConstant.LONGITUDE, "");
			}
		}
		String value = sharedPreferences.getString(aesEncryptKey, "");
		if (YxCommonUtil.isNotBlank(value)) {
			String aesDecryptValue = YxjrSecurity.AesDecrypt(context, value);
			return aesDecryptValue;
		}
		return "";
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:04:53
	 * @描述:TODO[取值Boolean]
	 * @param context
	 * @param key
	 *            键
	 * @return boolean 默认为true
	 */
	public static boolean getBoolean(Context context, String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(getSpName(), Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(key, true);
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:05:34
	 * @描述:TODO[移除对应值 ]
	 * @param context
	 * @param key
	 *            键
	 */
	public static void removeValue(Context context, String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(getSpName(), Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.remove(key);
		editor.commit();
	}

}
