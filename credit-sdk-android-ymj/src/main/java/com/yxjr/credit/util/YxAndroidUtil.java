package com.yxjr.credit.util;

import java.io.File;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import com.yxjr.credit.constants.SpConstant;
import com.yxjr.credit.log.YxLog;
import com.yxjr.credit.security.Md5;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings.Secure;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;

/**
 * All rights Reserved, Designed By ClareShaw
 * 
 * @公司:益芯金融
 * @作者:xiaochangyou
 * @版本:V1.0
 * @创建时间:2016-7-18 上午11:15:07
 * @描述:TODO[获取系统相关参数]
 */
public class YxAndroidUtil {

	public static final String OS_TYPE = "2";

	/**
	 * 手机存储容量
	 * 机身存储容量|机身可用存储容量|SD卡存储容量|SD卡可用存储容量
	 */
	public static String getPhoneStorageSize(Context context) {
		String romTotalSize = getRomTotalSize(context);
		String romFreeSize = getRomFreeSize(context);
		String sdTotalSize = getSDTotalSize(context);
		String sdFreeSize = getSDFreeSize(context);
		return romTotalSize + "|" + romFreeSize + "|" + sdTotalSize + "|" + sdFreeSize;
	}

	/** 
	 * 获得SD卡存储容量总大小
	 */
	public static String getSDTotalSize(Context context) {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		@SuppressWarnings("deprecation")
		long blockSize = stat.getBlockSize();
		@SuppressWarnings("deprecation")
		long totalBlocks = stat.getBlockCount();
		return formatFileSize(context, blockSize * totalBlocks);
	}

	/** 
	 * 获得sd卡可用存储容量
	 */
	public static String getSDFreeSize(Context context) {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		@SuppressWarnings("deprecation")
		long blockSize = stat.getBlockSize();
		@SuppressWarnings("deprecation")
		long availableBlocks = stat.getAvailableBlocks();
		return formatFileSize(context, blockSize * availableBlocks);
	}

	/** 
	 * 获得机身存储容量大小 
	 */
	public static String getRomTotalSize(Context context) {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		@SuppressWarnings("deprecation")
		long blockSize = stat.getBlockSize();
		@SuppressWarnings("deprecation")
		long totalBlocks = stat.getBlockCount();
		return formatFileSize(context, blockSize * totalBlocks);
	}

	/** 
	 * 获得机身可用存储容量
	 */
	public static String getRomFreeSize(Context context) {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		@SuppressWarnings("deprecation")
		long blockSize = stat.getBlockSize();
		@SuppressWarnings("deprecation")
		long availableBlocks = stat.getAvailableBlocks();
		return formatFileSize(context, blockSize * availableBlocks);
	}

	@SuppressLint("DefaultLocale")
	private static String formatFileSize(Context context, long number) {
		if (context == null) {
			return "";
		}
		float result = number;
		//		String suffix = "byteShort";
		if (result > 900) {//kb
			//			suffix = "kilobyteShort";
			result = result / 1024;
		}
		if (result > 900) {//mb
			//			suffix = "megabyteShort";
			result = result / 1024;
		}
		//		if (result > 900) {//gb
		//			suffix = "gigabyteShort";
		//			result = result / 1024;
		//		}
		//		if (result > 900) {/t
		//			suffix = "terabyteShort";
		//			result = result / 1024;
		//		}
		//		if (result > 900) {//
		//			suffix = "petabyteShort";
		//			result = result / 1024;
		//		}
		return String.format("%.4f", result);
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2017-2-22 下午3:59:26
	 * @描述:TODO[构建传感器信息标准格式数据]
	 * @param context
	 * @return String 传感器标准格式数据
	 */
	public static String getSensorsInfo(Context context) {
		String orientation = YxStoreUtil.get(context, SpConstant.SENSOR_ORIENTATION);
		if (!YxCommonUtil.isNotBlank(orientation)) {
			orientation = "||";
		}
		String accelerometer = YxStoreUtil.get(context, SpConstant.SENSOR_ACCELEROMETER);
		if (!YxCommonUtil.isNotBlank(accelerometer)) {
			accelerometer = "||";
		}
		String gravity = YxStoreUtil.get(context, SpConstant.SENSOR_GRAVITY);
		if (!YxCommonUtil.isNotBlank(gravity)) {
			gravity = "||";
		}
		String gyroscopes = YxStoreUtil.get(context, SpConstant.SENSOR_GYROSCOPES);
		if (!YxCommonUtil.isNotBlank(gyroscopes)) {
			gyroscopes = "||";
		}
		//顺序：方向传感器、加速传感器、重力传感器、陀螺仪传感器
		return orientation + "," + accelerometer + "," + gravity + "," + gyroscopes;
	}

	public static String getLocalMac() {//wifi mac 移动网络下有为null
		String mac = "null";
		String str = "";
		try {
			Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			for (; null != str;) {
				str = input.readLine();
				if (str != null) {
					mac = str.trim();// 去空格
					break;
				}
			}
		} catch (Exception ex) {
			// 赋予默认值
			ex.printStackTrace();
		}
		return mac;
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:15:23
	 * @描述:TODO[取得应用的版本号]
	 * @param context
	 * @return String 应用版本号
	 * @exception NameNotFoundException
	 */
	public static String getAppVersion(Context context) {

		if (context != null) {
			PackageManager packageManager = context.getPackageManager();
			try {
				PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
				if (packageInfo != null) {
					return packageInfo.versionName;
				}
			} catch (NameNotFoundException e) {
				YxLog.e("获取应用版本号异常:" + e);
			}
		}
		return "null";
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-12-6 下午6:52:49
	 * @描述:TODO[获取应用的包名]
	 * @param context
	 * @return String
	 */
	public static String getAppPackage(Context context) {
		String pkName = null;
		if (context != null) {
			if (context.getPackageName() != null) {
				pkName = context.getPackageName();
			}
		}
		return pkName == null ? "null" : pkName;
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:16:42
	 * @描述:TODO[获取手机序列号]
	 * @param context
	 * @return String 手机序列号
	 */
	public static String getDeviceId(Context context) {
		try {
			String deviceId = null;
			if (context != null) {
				TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				if (!TextUtils.isEmpty(tm.getDeviceId())) {
					deviceId = tm.getDeviceId();
				}
			}
			return deviceId == null ? "null" : deviceId;
		} catch (Exception e) {
			YxLog.e("获取手机DeviceId异常:" + e);
			e.printStackTrace();
			return "error";
		}
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:20:18
	 * @描述:TODO[获取手机厂商信息]
	 * @return String 手机厂商信息
	 */
	public static String getDeviceBrand() {
		return YxCommonUtil.isNotBlank(android.os.Build.BRAND) ? android.os.Build.BRAND : "null";
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:20:35
	 * @描述:TODO[获取手机设备名]
	 * @return String 手机设备名
	 */
	public static String getDeviceName() {
		return YxCommonUtil.isNotBlank(android.os.Build.PRODUCT) ? android.os.Build.PRODUCT : "null";
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:20:53
	 * @描述:TODO[获取设备型号信息]
	 * @return String 设备型号信息
	 */
	public static String getDeviceModel() {
		return YxCommonUtil.isNotBlank(android.os.Build.MODEL) ? android.os.Build.MODEL : "null";
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-12-16 上午10:00:34
	 * @描述:TODO[获取手机号]
	 * @param context
	 * @return String
	 */
	public static String getNativePhoneNumber(Context context) {
		try {
			String nativePhoneNumber = null;
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			nativePhoneNumber = telephonyManager.getLine1Number();
			return nativePhoneNumber == null ? "null" : nativePhoneNumber;
		} catch (Exception e) {
			YxLog.e("获取手机号异常：" + e);
			e.printStackTrace();
			return "error";
		}
	}

	public static String getAndroidId(Context context) {
		try {
			String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
			return androidId == null ? "null" : androidId;
		} catch (Exception e) {
			YxLog.e("获取AndroidId异常：" + e);
			e.printStackTrace();
			return "error";
		}
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:21:13
	 * @描述:TODO[获取手机唯一标示]
	 * @param context
	 * @return String 手机唯一标示
	 */
	public static String getUUID(Context context) {
		String uuid = "";
//				String SimSerialNumber = "";
		String androidId = "";
		String deviceId = "";
		if (context != null) {
			deviceId = getDeviceId(context);
			if (deviceId.equals("error") || deviceId.equals("null")) {
				deviceId = "";
			}
			androidId = getAndroidId(context);
			if (androidId.equals("error") || androidId.equals("null")) {
				androidId = "";
			}
//						androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
//						SimSerialNumber = getSimSerialNumber(context);// sim序列号
//						if (SimSerialNumber.equals("error")) {
//							SimSerialNumber = "";
//						}
		}
//				if (!TextUtils.isEmpty(deviceId) || !TextUtils.isEmpty(androidId) || !TextUtils.isEmpty(SimSerialNumber)) {
//					uuid = deviceId + androidId + SimSerialNumber;
		if (!TextUtils.isEmpty(deviceId) || !TextUtils.isEmpty(androidId)) {
			uuid = deviceId + androidId;
		} else {
			uuid = YxUtility.generateString(10);
		}
		try {
			uuid = Md5.encrypt(uuid);
		} catch (Exception e) {
			YxLog.e("获取手机唯一标识异常:" + e);
		}
		return uuid;

	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:21:50
	 * @描述:TODO[获取系统版本]
	 * @return String 系统版本
	 */
	public static String getOsVersion() {
		return YxCommonUtil.isNotBlank(android.os.Build.VERSION.RELEASE) ? android.os.Build.VERSION.RELEASE : "null";
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:22:07
	 * @描述:TODO[获取服务集标识(SSID)]
	 * @param context
	 * @return String 服务集标识(SSID)
	 */
	@SuppressWarnings("static-access")
	public static String getSSID(Context context) {
		if (context != null) {
			WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);// 获取ssid等数据
			WifiInfo info = wifiManager.getConnectionInfo();
			if (info != null && info.getSSID() != null) {
				return info.getSSID().replace("\"", "");
			}
		}
		return "null";
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:30:25
	 * @描述:TODO[获取sim序列号]
	 * @param context
	 * @return String sim序列号
	 */
	public static String getSimSerialNumber(Context context) {
		try {
			String SimSerialNumber = null;
			if (context != null) {
				TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				if (tm != null) {// sim序列号
					SimSerialNumber = tm.getSimSerialNumber();
				}
			}
			return SimSerialNumber == null ? "null" : SimSerialNumber;
		} catch (Exception e) {
			YxLog.e("获取sim序列号异常：" + e);
			e.printStackTrace();
			return "error";
		}
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-12-6 下午6:34:29
	 * @描述:TODO[获取IMSI]
	 * @param context
	 * @return String
	 */
	public static String getIMSI(Context context) {
		try {
			String imsi = null;
			if (context != null) {
				TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				if (tm.getSubscriberId() != null) {
					imsi = tm.getSubscriberId();//获取手机IMSI号 
				}
			}
			return imsi == null ? "null" : imsi;
		} catch (Exception e) {
			YxLog.e("获取IMSI异常：" + e);
			e.printStackTrace();
			return "error";
		}
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:41:54
	 * @描述:TODO[获取基站编号]
	 * @param context
	 * @return int 基站编号
	 */
	public static int getCid(Context context) {
		int cid = 0;
		if (context != null) {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			if (tm != null) {
				CellLocation celllocation = tm.getCellLocation();
				if (celllocation instanceof GsmCellLocation) {
					GsmCellLocation location = (GsmCellLocation) celllocation;
					if (location != null) {
						cid = location.getCid();
					}
				} else if (celllocation instanceof CdmaCellLocation) {
					CdmaCellLocation location = (CdmaCellLocation) celllocation;
					if (location != null) {
						cid = location.getBaseStationId();
					}
				}
			}
		}
		return cid;
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:43:25
	 * @描述:TODO[检测是否具有SD卡]
	 * @return boolean true:是;false:否
	 */
	public static boolean checkSDCardAvailable() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:43:51
	 * @描述:TODO[获取存储根目录路径]
	 * @return String 如果有sd卡,则返回sd卡的目录;如果没有sd卡,则返回存储目录
	 */
	public static String getSDPath() {
		if (checkSDCardAvailable()) {
			return Environment.getExternalStorageDirectory().getPath();
		} else {
			return Environment.getDownloadCacheDirectory().getPath();
		}
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:44:23
	 * @描述:TODO[获取application的meta-data]
	 * @param context
	 * @param key
	 *            待获取的meta-data的key
	 * @return String meta-data的value值;若无该key值,则返回null
	 */
	public static String getApplicationMetaData(Context context, String key) {

		ApplicationInfo appInfo;
		try {
			appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			return appInfo.metaData.getString(key);
		} catch (NameNotFoundException e) {
		}
		return null;
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:45:26
	 * @描述:TODO[获取运营商名称]
	 * @param context
	 * @return String
	 */
	public static String getOperateName(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String operator = tm.getSimOperator();
		if (operator != null) {
			if ("46000".equals(operator) || "46002".equals(operator)) {
				return "中国移动";
			} else if ("46001".equals(operator)) {
				return "中国联通";
			} else if ("46003".equals(operator)) {
				return "中国电信";
			} else {
				return tm.getSimOperatorName();
			}
		}
		return null;
	}
}
