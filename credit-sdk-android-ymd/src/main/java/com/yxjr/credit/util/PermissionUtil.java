package com.yxjr.credit.util;

import java.util.List;

import com.yxjr.credit.log.YxLog;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;

public class PermissionUtil {

	public static int getTargetSdkVersion(Context context) {
		int targetSdkVersion = 0;
		try {
			final PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			targetSdkVersion = info.applicationInfo.targetSdkVersion;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return targetSdkVersion;
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2017-3-7 上午11:05:03
	 * @描述:TODO[6.0以上存储权限授权状态]
	 * @param context
	 * @return String
	 */
	public static String isStoragePer(Context context) {
		if (isMarshmallow(context)) {
			if (isAuthorized(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
					&& isAuthorized(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
				return AUTHORIZED;
			} else {
				return UNAUTHORIZED;
			}
		} else {
			return NOT_GET;
		}

	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2017-3-7 上午11:05:03
	 * @描述:TODO[6.0以上相机权限授权状态]
	 * @param context
	 * @return String
	 */
	public static String isCameraPer(Context context) {
		if (isMarshmallow(context)) {
			if (isAuthorized(context, Manifest.permission.CAMERA)
					&& isAuthorized(context, Manifest.permission.CAMERA)) {
				return AUTHORIZED;
			} else {
				return UNAUTHORIZED;
			}
		} else {
			return NOT_GET;
		}
	}

	public static String getAllPer(Context context) {
		// 	authGPS 手机gps定位服务权限 
		//		authGPSApp 应用Gps权限 
		//		authContacts通讯录权限
		// 	authSMS短信权限(android) 
		//		authCall通话记录(android) 
		//		authBrower浏览器
		// 	authPhoneInfo手机信息（识别码、IMEI）
		//		authAppInfo应用程序列表(android)
		String authGPS = isGpsService(context);
		String authGPSApp = isLocPer(context);
		String authContacts = isContactsPer(context);
		String authSMS = isSmsPer(context);
		String authCall = isCallLogPer(context);
		String authAppInfo = isAppListPer(context);
		String authPhoneInfo = isPhoneInfoPer(context);
		String authBrower = isBrowerPer(context);
		String data = authGPS + "|" + authGPSApp + "|" + authContacts + "|" + authSMS + "|" + authCall + "|"
				+ authBrower + "|" + authPhoneInfo + "|" + authAppInfo;
		// 1|1|1|0|0|2|1|1
		return data;
	}

	/**
	 * 已授权
	 */
	public static final String AUTHORIZED = "1";
	/**
	 * 未授权
	 */
	public static final String UNAUTHORIZED = "0";
	/**
	 * 无法获取
	 */
	public static final String NOT_GET = "2";

	// 、定位权限， ，安装app列表权限(个别手机)
	/**
	 * @作者:xiaochangyou
	 * @创建时间:2017-2-23 上午10:05:44
	 * @描述:TODO[定位权限判断]
	 * @param context
	 * @return String
	 */
	public static String isLocPer(Context context) {
		if (isMarshmallow(context)) {
			if (isAuthorized(context, Manifest.permission.ACCESS_FINE_LOCATION)
					&& isAuthorized(context, Manifest.permission.ACCESS_COARSE_LOCATION)) {
				return AUTHORIZED;
			} else {
				return UNAUTHORIZED;
			}
		} else {
			return isLowLocPer(context);
		}
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2017-2-23 上午10:05:54
	 * @描述:TODO[手机信息权限判断]
	 * @param context
	 * @return String
	 */
	public static String isPhoneInfoPer(Context context) {
		if (isMarshmallow(context)) {
			if (isAuthorized(context, Manifest.permission.READ_PHONE_STATE)) {
				return AUTHORIZED;
			} else {
				return UNAUTHORIZED;
			}
		} else {
			return isLowPhoneInfoPer(context);
		}
	}

	public static String isBrowerPer(Context context) {
		if (isMarshmallow(context)) {
			if (isAuthorized(context, "com.android.browser.permission.READ_HISTORY_BOOKMARKS")) {
				return AUTHORIZED;
			} else {
				return UNAUTHORIZED;
			}
		} else {
			return NOT_GET;
		}
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2017-2-23 上午10:32:39
	 * @描述:TODO[读取已安装应用列表权限判断(个别手机)]
	 * @param context
	 * @return String
	 */
	public static String isAppListPer(Context context) {
		String applistPer = NOT_GET;
		try {
			PackageManager pm = context.getPackageManager();
			List<ApplicationInfo> listAppcations = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
			for (ApplicationInfo app : listAppcations) {
				if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {// 第三方APP
					String appPkgName = app.packageName;// 包名
					if (!appPkgName.equals(YxAndroidUtil.getAppPackage(context))) {
						applistPer = AUTHORIZED;
						return applistPer;
					}
				}
			}
		} catch (Exception e) {
			YxLog.e("读取已安装应用列表权限判断异常:" + e);
			e.printStackTrace();
		}
		return applistPer;
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2017-2-23 上午10:06:06
	 * @描述:TODO[通讯录权限判断]
	 * @param context
	 * @return String
	 */
	public static String isContactsPer(Context context) {
		if (isMarshmallow(context)) {
			if (isAuthorized(context, Manifest.permission.READ_CONTACTS)) {
				return AUTHORIZED;
			} else {
				return UNAUTHORIZED;
			}
		} else {
			return isLowContacts(context);
		}
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2017-2-23 上午10:09:06
	 * @描述:TODO[短信权限判断]
	 * @param context
	 * @return String
	 */
	public static String isSmsPer(Context context) {
		if (isMarshmallow(context)) {
			if (isAuthorized(context, Manifest.permission.READ_SMS)) {
				return AUTHORIZED;
			} else {
				return UNAUTHORIZED;
			}
		} else {
			return isLowSmsPer(context);
		}
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2017-2-23 上午10:23:28
	 * @描述:TODO[通话记录权限判断]
	 * @param context
	 * @return String
	 */
	public static String isCallLogPer(Context context) {
		if (isMarshmallow(context)) {
			if (isAuthorized(context, Manifest.permission.READ_CALL_LOG)) {
				return AUTHORIZED;
			} else {
				return UNAUTHORIZED;
			}
		} else {
			return isLowCallLog(context);
		}
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2017-2-23 上午10:04:32
	 * @描述:TODO[定位服务是否开启] 需开启定位服务并选择高精准度(这样才回有网络定位，不然只会有GPS定位)
	 * @param mContext
	 * @return String
	 */
	public static String isGpsService(Context mContext) {
		LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		boolean isGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
		if (isGps) {
			return AUTHORIZED;
		}
		return UNAUTHORIZED;
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2017-2-23 上午10:05:03
	 * @描述:TODO[是否为Android 6.0且targetSdkVersion>=23]
	 * @return boolean
	 */
	private static boolean isMarshmallow(Context context) {
		if (getTargetSdkVersion(context) >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			return true;
		} else {
			return false;
		}
		// return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2017-2-23 上午10:05:21
	 * @描述:TODO[是否授予某项权限]
	 * @param context
	 * @param permission
	 * @return boolean
	 */
	@SuppressLint("NewApi")
	private static boolean isAuthorized(Context context, String permission) {
		int checkSelfPermission = context.checkSelfPermission(permission);
		return checkSelfPermission == PackageManager.PERMISSION_GRANTED;
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2017-2-24 上午9:20:26
	 * @描述:TODO[6.0以下定位权限判断]
	 * @param mContext
	 * @return String
	 */
	private static String isLowLocPer(Context mContext) {
		String locPer = UNAUTHORIZED;
		try {
			Location location = null;
			LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);// 获得LocationManager的实例
			List<String> providerList = locationManager.getProviders(true);// 获取所有可用的位置提供器
			if (providerList.contains(LocationManager.NETWORK_PROVIDER)
					|| providerList.contains(LocationManager.GPS_PROVIDER)) {
				if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
					location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					if (null != location && (int) location.getLatitude() != 0) {// location不为null且经度不为0则已授权
						locPer = AUTHORIZED;
					} else {
						if (providerList.contains(LocationManager.GPS_PROVIDER)) {
							locPer = NOT_GET;
						}
					}
				} else if (providerList.contains(LocationManager.GPS_PROVIDER)) {
					location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					if (null != location && (int) location.getLatitude() != 0) {// location不为null且经度不为0则已授权
						locPer = AUTHORIZED;
					} else {
						locPer = NOT_GET;
					}
				} else {
					locPer = NOT_GET;
				}
			}
		} catch (Exception e) {
			YxLog.e("6.0以下定位权限判断异常:" + e);
			e.printStackTrace();
		}
		return locPer;
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2017-2-23 上午10:04:03
	 * @描述:TODO[6.0以下手机信息权限判断]
	 * @param context
	 * @return String
	 */
	private static String isLowPhoneInfoPer(Context context) {
		String phoneInfo = NOT_GET;// 默认未授权
		try {
			String deviceId = "";
			if (context != null) {
				TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				deviceId = tm.getDeviceId();
			}
			if (YxCommonUtil.isNotBlank(deviceId)) {// 成功获取表示已授权
				phoneInfo = AUTHORIZED;
			} else {
				phoneInfo = UNAUTHORIZED;
			}
		} catch (Exception e) {// 抛出异常返回默认值
			YxLog.e("6.0以下手机信息权限判断异常:" + e);
			e.printStackTrace();
		}
		return phoneInfo;
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2017-2-23 上午10:03:21
	 * @描述:TODO[6.0以下通讯录权限判断]
	 * @param context
	 * @return String
	 */
	private static String isLowContacts(Context context) {
		String contactsPer = NOT_GET;// 默认无法获取
		Cursor contactsCursor = null;
		try {
			contactsCursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null,
					"sort_key COLLATE LOCALIZED asc"); // 取得电话本中开始一项的光标
			if (null != contactsCursor && contactsCursor.getCount() > 0) {
				contactsPer = AUTHORIZED;// 有权限
			} else {
				contactsPer = UNAUTHORIZED;
			}
		} catch (Exception e) {
			YxLog.e("6.0以下通讯录权限判断异常：" + e);
			e.printStackTrace();
		} finally {
			if (null != contactsCursor) {
				contactsCursor.close();
			}
		}
		return contactsPer;
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2017-2-23 上午10:23:00
	 * @描述:TODO[6.0以下通话记录权限判断]
	 * @param context
	 * @return String
	 */
	private static String isLowCallLog(Context context) {
		String callLogPer = NOT_GET;
		Cursor callLogCursor = null;
		try {
			String[] projection = new String[] { CallLog.Calls.NUMBER, CallLog.Calls.DATE, CallLog.Calls.DURATION };
			callLogCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, null, null,
					"date desc");
			if (null != callLogCursor && callLogCursor.getCount() > 0) {
				callLogPer = AUTHORIZED;
			} else {
				callLogPer = UNAUTHORIZED;
			}
		} catch (Exception e) {
			YxLog.e("6.0以下通话记录权限判断异常：" + e);
			e.printStackTrace();
		} finally {
			if (null != callLogCursor) {
				callLogCursor.close();
			}
		}
		return callLogPer;
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2017-2-23 上午10:33:24
	 * @描述:TODO[6.0以下短信权限判断]
	 * @param context
	 * @return String
	 */
	private static String isLowSmsPer(Context context) {
		String smsPer = NOT_GET;
		Cursor smsCursor = null;
		try {
			String[] projection = new String[] { "_id", "address", "person", "body", "date", "type" };
			smsCursor = context.getContentResolver().query(Uri.parse("content://sms/"), projection, null, null,
					"date desc");
			if (smsCursor != null && smsCursor.getCount() > 0) {// 查询的数据是否为空
				smsPer = AUTHORIZED;
			} else {
				smsPer = UNAUTHORIZED;
			}
		} catch (Exception e) {
			// TODO: handle exception
			YxLog.e("6.0以下短信权限判断异常:" + e);
			e.printStackTrace();
		} finally {
			if (null != smsCursor) {
				smsCursor.close();
			}
		}
		return smsPer;
	}
}
