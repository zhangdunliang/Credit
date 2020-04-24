package com.yxjr.credit.util;

import java.util.Random;

import android.annotation.SuppressLint;
import android.text.TextUtils;

/**
 * All rights Reserved, Designed By ClareShaw
 * 
 * @公司:益芯金融
 * @作者:xiaochangyou
 * @版本:V1.0
 * @创建时间:2016-7-18 上午11:09:14
 * @描述:TODO[随机数生成]
 */
public class YxUtility {

	public static final String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String letterChar = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:09:36
	 * @描述:TODO[返回一个定长的随机字符串(只包含大小写字母、数字)]
	 * @param length
	 *            随机字符串长度
	 * @return String 随机字符串
	 */
	public static String generateString(int length) {
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(allChar.charAt(random.nextInt(allChar.length())));
		}
		return sb.toString();
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:10:12
	 * @描述:TODO[返回一个定长的随机纯字母字符串(只包含大小写字母)]
	 * @param length
	 *            随机字符串长度
	 * @return String 随机字符串
	 */
	public static String generateMixString(int length) {
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(allChar.charAt(random.nextInt(letterChar.length())));
		}
		return sb.toString();
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:10:32
	 * @描述:TODO[返回一个定长的随机纯大写字母字符串(只包含大写字母)]
	 * @param length
	 *            随机字符串长度
	 * @return String 随机字符串
	 */
	@SuppressLint("DefaultLocale")
	public static String generateLowerString(int length) {
		return generateMixString(length).toLowerCase();
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:12:46
	 * @描述:TODO[获取接近参数的2的整数次幂]
	 * @param inSampleSize
	 *            目标参数
	 * @return int 整数次幂
	 */
	public static int computeSampleSize(int inSampleSize) {

		int result = inSampleSize;// 每次除2的商
		int remainder = 0;// 每次除以2的余数
		int i = 0;// 计数器
		boolean flag = true;// 参数是否就是2的整数次幂
		while (result != 1) {// 循环除以2 直到商为1时
			remainder = inSampleSize % 2;
			if (remainder != 0) {
				flag = false;
			}
			result = result / 2;
			i++;
		}
		if (flag) {// 所有余数都为0 则参数即是2的整数次幂
			return inSampleSize;
		}
		// 取最接近的2的整数次幂
		int outSampleSize = (int) (Math.abs(Math.pow(2, i) - inSampleSize) > Math.abs(Math.pow(2, i + 1) - inSampleSize) ? Math.pow(2, i + 1) : Math.pow(2, i));
		return outSampleSize;
	}

	@SuppressWarnings("rawtypes")
	public static Class reflectClass(String className) {

		if (!TextUtils.isEmpty(className)) {
			try {
				return Class.forName(className);
			} catch (ClassNotFoundException e) {
			}
		}

		return null;
	}

}
