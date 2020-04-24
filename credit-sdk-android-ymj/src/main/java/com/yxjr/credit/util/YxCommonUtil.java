package com.yxjr.credit.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yxjr.credit.constants.SpConstant;
import com.yxjr.credit.constants.YxConstant;
import com.yxjr.credit.log.YxLog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;

/**
 * All rights Reserved, Designed By ClareShaw
 * 
 * @公司:益芯金融
 * @作者:xiaochangyou
 * @版本:V1.0
 * @创建时间:2016-8-1 下午3:35:17
 * @描述:TODO[请简要说明此类用途]
 */
public class YxCommonUtil {

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2017-3-8 上午11:07:15
	 * @描述:TODO[处理相关参数，处理前确保参数正确性]
	 * @param bundle
	 */
	@SuppressLint("DefaultLocale")
	public static void processParam(Context context, Bundle bundle) {
		String partnerId = bundle.getString(YxConstant.PARTNER_ID).trim();
		String realName = bundle.getString(YxConstant.PARTNER_REAL_NAME).trim();
		String idCardNum = bundle.getString(YxConstant.PARTNER_ID_CARD_NUM).toUpperCase().trim();// 将身份证号里的所有小写转成大写
		String phoneNumber = bundle.getString(YxConstant.PARTNER_PHONE_NUMBER).trim();
		String key = bundle.getString(YxConstant.PARTNER_KEY);
		String payPackageName = null;
		String payClassName = null;
		if (YxCommonUtil.isNotBlank(bundle.getString(YxConstant.PARTNER_PAY_PACKAGE_NAME))) {
			payPackageName = bundle.getString(YxConstant.PARTNER_PAY_PACKAGE_NAME).trim();
		}
		if (YxCommonUtil.isNotBlank(bundle.getString(YxConstant.PARTNER_PAY_CLASS_NAME))) {
			payClassName = bundle.getString(YxConstant.PARTNER_PAY_CLASS_NAME).trim();
		}
		YxStoreUtil.setSpName(phoneNumber + "SP");
		if (isSaveData(context, partnerId, realName, idCardNum, phoneNumber, key, payPackageName, payClassName)) {
			YxStoreUtil.save(context, SpConstant.PARTNER_ID, partnerId);
			YxStoreUtil.save(context, SpConstant.PARTNER_REAL_NAME, realName);
			YxStoreUtil.save(context, SpConstant.PARTNER_ID_CARD_NUM, idCardNum);
			YxStoreUtil.save(context, SpConstant.PARTNER_PHONENUMBER, phoneNumber);
			YxStoreUtil.save(context, SpConstant.PARTNER_KEY, key);
			YxStoreUtil.save(context, SpConstant.PARTNER_PAY_PACKAGE_NAME, payPackageName);
			YxStoreUtil.save(context, SpConstant.PARTNER_PAY_CLASS_NAME, payClassName);
		}
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-15 下午5:14:37
	 * @描述:TODO[通过app传入的数据，判断是否更新保存的数据]
	 * @param partnerId
	 * @param realName
	 * @param idCardNum
	 * @param phoneNumber
	 * @return boolean
	 */
	public static boolean isSaveData(Context context, String partnerId, String realName, String idCardNum, String phoneNumber, String key, String payPackageName, String payClassName) {
		if (YxStoreUtil.get(context, SpConstant.PARTNER_ID).equals("") || YxStoreUtil.get(context, SpConstant.PARTNER_REAL_NAME).equals("") || YxStoreUtil.get(context, SpConstant.PARTNER_ID_CARD_NUM).equals("")
				|| YxStoreUtil.get(context, SpConstant.PARTNER_PHONENUMBER).equals("") || YxStoreUtil.get(context, SpConstant.PARTNER_KEY).equals("")
				|| YxStoreUtil.get(context, SpConstant.PARTNER_PAY_PACKAGE_NAME).equals("") || YxStoreUtil.get(context, SpConstant.PARTNER_PAY_CLASS_NAME).equals("")) {
			// 用户首次进来,没有存储过数据
			return true;
		} else {
			// 用户不是首次进来,没有存储过数据
			if (!YxStoreUtil.get(context, SpConstant.PARTNER_ID).equals(partnerId) || !YxStoreUtil.get(context, SpConstant.PARTNER_REAL_NAME).equals(realName)
					|| !YxStoreUtil.get(context, SpConstant.PARTNER_ID_CARD_NUM).equals(idCardNum) || !YxStoreUtil.get(context, SpConstant.PARTNER_PHONENUMBER).equals(phoneNumber)
					|| YxStoreUtil.get(context, SpConstant.PARTNER_KEY).equals(key) || !YxStoreUtil.get(context, SpConstant.PARTNER_PAY_PACKAGE_NAME).equals(payPackageName)
					|| !YxStoreUtil.get(context, SpConstant.PARTNER_PAY_CLASS_NAME).equals(payClassName)) {
				// 有存数过数据,但是数据不一致,判定为不同用户
				return true;
			} else {
				// if (YxStoreUtil.get(this,
				// SpConstant.SESSION_TOKEN_KEY).equals("") ||
				// YxStoreUtil.get(this,
				// SpConstant.SESSION_TOKEN_KEY).isEmpty()) {
				// // 存储的Token为空
				// } else {
				// // 存储的Token不为空
				// }
				return false;
			}
		}
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:48:26
	 * @描述:TODO[字符串是否为空]
	 * @param value
	 * @return boolean true=不为空
	 */
	public static boolean isNotBlank(String value) {
		if (null != value && !value.equals("") && value.trim().length() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:48:48
	 * @描述:TODO[字符串是否包含字母]
	 * @param str
	 * @return boolean
	 */
	public static boolean isContainLetter(String str) {
		Pattern p = Pattern.compile("[a-zA-Z]");
		// Pattern p1 = Pattern.compile("[A-Z]");//大写字母
		// Pattern p2 = Pattern.compile("[a-z]");//小写字母
		Matcher m = p.matcher(str);
		if (m.matches()) {
			return true;
		}
		return false;
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:49:05
	 * @描述:TODO[是否是平板]
	 * @param mContext
	 * @return boolean true:平板;false:手机
	 */
	public static boolean isTablet(Context mContext) {
		TelephonyManager telephony = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		int type = telephony.getPhoneType();
		if (type == TelephonyManager.PHONE_TYPE_NONE) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:49:37
	 * @描述:TODO[得到当前时间 年-月-日 时:分:秒]
	 * @return String yyyy-MM-dd hh:mm:ss
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getCurrentTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = format.format(new Date());
		return currentTime;
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:49:37
	 * @描述:TODO[得到当前时间 年月日时分秒]
	 * @return String yyyyMMddhhmmss
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getCurrentTime2() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String currentTime = format.format(new Date());
		return currentTime;
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:50:39
	 * @描述:TODO[yyyy-MM-dd HH:mm:ss字符串类型的时间转long]
	 * @param str
	 * @return long
	 */
	@SuppressLint("SimpleDateFormat")
	public static long dateStringToLong(String str) {
		String replace0 = str.replace("-", "");
		String replace1 = replace0.replace(" ", "");
		String replace2 = replace1.replace(":", "");
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");// 24小时制
		Date d = null;
		long time = 0;
		try {
			d = format.parse(replace2);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			YxLog.e("Exception:" + e);
			e.printStackTrace();
		}
		if (null != d) {
			time = d.getTime();
		}
		return time;
	}

	/**
	 * 格式化时间，long转date
	 * yyyy-MM-dd HH:mm:ss
	 * @param l
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String formatDateTime(long l) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(l);
		String format = sdf.format(date);
		return format;
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:51:01
	 * @描述:TODO[当前时间-以前时间 >= 差值]
	 * @param s1
	 *            如 :当前时间
	 * @param s2
	 *            如 :以前时间
	 * @param difference
	 *            差值(天)
	 * @return boolean true:大于等于差值;false:小于差值
	 */
	@SuppressLint("SimpleDateFormat")
	public static boolean dayCompareByDate(String s1, String s2, int difference) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设定时间的模板
		Date d1 = null;
		Date d2 = null;
		try {
			d1 = sdf.parse(s1);// 得到指定模范的时间
			d2 = sdf.parse(s2);// 得到指定模范的时间
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			YxLog.e("Exception:格式化时间错误！" + e);
			e.printStackTrace();
		}
		if (d1 != null && d2 != null) {
			if (Math.abs(((d1.getTime() - d2.getTime()) / (24 * 3600 * 1000))) >= difference) {// 比较
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:51:40
	 * @描述:TODO[当前时间-以前时间 >= 差值]
	 * @param s1
	 *            如 :当前时间
	 * @param s2
	 *            如 :以前时间
	 * @param difference
	 *            差值(分钟)
	 * @return boolean true:大于等于差值;false:小于差值
	 */
	@SuppressLint("SimpleDateFormat")
	public static boolean minuteCompareByDate(String s1, String s2, int difference) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设定时间的模板
		Date d1 = null;
		Date d2 = null;
		try {
			d1 = sdf.parse(s1);// 得到指定模范的时间
			d2 = sdf.parse(s2);// 得到指定模范的时间
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			YxLog.e("Exception:格式化时间错误！" + e);
			e.printStackTrace();
		}
		if (d1 != null && d2 != null) {
			if (d1.getTime() - d2.getTime() >= difference * 60 * 1000) {// 比较
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:52:03
	 * @描述:TODO[获取sd卡路径]
	 * @return String
	 */
	public static String getSdcardPath() {
		boolean sdcardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		String sdpath = null;
		if (sdcardExist) {
			sdpath = Environment.getExternalStorageDirectory().getPath();
		} else {
			sdpath = "/mnt/sdcard";
		}
		return sdpath;
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:52:13
	 * @描述:TODO[字符串转utf-8格式]
	 * @param str
	 * @return String
	 */
	public static String encodeStrToUtf8(String str) {
		try {
			return URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			YxLog.e("Exception:" + e);
		}
		return str;
	}
}
