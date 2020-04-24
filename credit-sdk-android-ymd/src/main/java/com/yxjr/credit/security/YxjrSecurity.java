/**  
 * cn.com.yxjr.credit.security
 * cn.com.yxjr.security 
 * YxjrSecurity.java  
 *   
 *  XiaoChangYou 2016-5-6 下午3:12:43
 *  2016 YXJR-版权所有  
 *   
 */
package com.yxjr.credit.security;

import java.io.File;

import android.content.Context;

/**
 * 
 * Security
 * 
 * @author XiaoChangYou
 * @version 修改时间：2016-5-6 下午5:12:43
 */
public class YxjrSecurity {

	private static final String TAG = "YxjrSecurity";
	static {
		System.loadLibrary("YxjrSecurity");
	}

	/**
	 * 
	 * 验证签名是否正确
	 * 
	 * @param context
	 * @return boolean true表示正确，false表示错误
	 * @permission
	 * @exception
	 * @since 1.0.0
	 */
	public static native boolean checkSignature(Context context, String signature);

	/**
	 * 
	 * 对输入string进行rsa加密
	 * 
	 * @param context
	 * @param data
	 *            待加密字符串
	 * @return String RSA加密后的字符串
	 * @permission
	 * @exception
	 * @since 1.0.0
	 */
	public static native String getRsaEncrypt(Context context, String data);

	/**
	 * 
	 * 对输入数据进行rsa加密
	 * 
	 * @param context
	 * @param data
	 *            待加密数据
	 * @return String RSA加密后的字符串
	 * @permission
	 * @exception
	 * @since 1.0.0
	 */
	public static String RsaEncrypt(Context context, String data) {

		if (context == null || data == null) {
			return null;
		}
		return getRsaEncrypt(context, data);
	}

	/**
	 * 
	 * 对输入string进行aes加密
	 * 
	 * @param context
	 * @param data
	 *            待加密字符串
	 * @return String AES加密后的字符串
	 * @permission
	 * @exception
	 * @since 1.0.0
	 */
	public static native String getAesEncrypt(Context context, String data);

	/**
	 * 
	 * 对输入数据进行aes加密
	 * 
	 * @param context
	 * @param data
	 *            待加密数据
	 * @return String AES加密后的字符串
	 * @permission
	 * @exception
	 * @since 1.0.0
	 */
	public static String AesEncrypt(Context context, String data) {

		if (context == null || data == null) {
			return null;
		}
		return getAesEncrypt(context, data);
	}

	/**
	 * 
	 * 对输入string进行aes解密
	 * 
	 * @param context
	 * @param data
	 *            待解密字符串
	 * @return String AES解密后的字符串
	 * @permission
	 * @exception
	 * @since 1.0.0
	 */
	public static native String getAesDecrypt(Context context, String data);

	/**
	 * 
	 * 对输入数据进行aes解密
	 * 
	 * @param context
	 * @param data
	 *            待解密字符串
	 * @return String AES解密后的字符串
	 * @permission
	 * @exception
	 * @since 1.0.0
	 */
	public static String AesDecrypt(Context context, String data) {

		if (context == null || data == null) {
			return null;
		}
		return getAesDecrypt(context, data);
	}

	/**
	 * 
	 * 判断系统是否被root
	 * 
	 * @return boolean true表示没root，false则没有
	 * @permission
	 * @exception
	 * @since 1.0.0
	 */
	public static boolean isRootSystem() {

		File f = null;
		final String kSuSearchPaths[] = { "/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/", "/vendor/bin/" };
		try {
			for (int i = 0; i < kSuSearchPaths.length; i++) {
				f = new File(kSuSearchPaths[i] + "su");
				if (f != null && f.exists()) {
					return true;
				}
			}
		} catch (Exception e) {
		}
		return false;
	}
}
