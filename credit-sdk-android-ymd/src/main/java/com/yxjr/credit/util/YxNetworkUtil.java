package com.yxjr.credit.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * All rights Reserved, Designed By ClareShaw
 * 
 * @公司:益芯金融
 * @作者:xiaochangyou
 * @版本:V1.0
 * @创建时间:2016-7-18 下午1:56:34
 * @描述:TODO[网络相关工具类]
 */
public class YxNetworkUtil {

	public static final int NO_NET_CONNECT = -1;
	public static final int WAP_CONNECTED = 0;
	public static final int NET_CONNECTED = 1;
	public static final int WIFI_CONNECT = 2;
	public static final int NO_WAP_NET = 3;
	private static ConnectivityManager mConnectivityManager = null;
	private static WifiManager wifiManager = null;

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 下午1:57:08
	 * @描述:TODO[获取网络类型]
	 * @param context
	 * @return int
	 */
	@SuppressLint("DefaultLocale")
	public static int getNetworkType(Context context) {
		mConnectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
		wifiManager = (WifiManager) context.getSystemService("wifi");
		int wifiState = wifiManager.getWifiState();
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		if ((wifiInfo.getNetworkId() != -1) && ((wifiState == 3) || (wifiState == 2))) {
			return 2;
		}
		@SuppressWarnings("deprecation")
		NetworkInfo netInfo = mConnectivityManager.getNetworkInfo(0);
		if ((netInfo == null) || (!netInfo.isConnected()) || (netInfo.getExtraInfo() == null)) {
			return -1;
		}
		if ((netInfo.getType() == 0) && (netInfo.getExtraInfo().toLowerCase().equals("cmwap"))) {
			return 0;
		}
		return 1;
		//		int netType = ConnectivityManager.TYPE_MOBILE;
		//		if (context != null) {
		//			ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		//			NetworkInfo info = manager.getActiveNetworkInfo();
		//			if (info != null) {
		//				netType = info.getType();
		//			}
		//		}
		//		return netType;
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:17:17
	 * @描述:TODO[判断是否已联网]
	 * @param context
	 * @return boolean true:已联网;false:未联网
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = manager.getActiveNetworkInfo();
			if (info != null) {
				return info.isAvailable();
			}
		}
		return false;
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:17:51
	 * @描述:TODO[获取网络类型名称]
	 * @param context
	 * @return String 类型
	 */
	public static String getNetworkTypeName(Context context) {
		String netType = "";
		if (context != null) {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
			if (info == null) {
				netType = "null";
			} else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
				netType = "wifi";
			} else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
				int subType = info.getSubtype();
				if (subType == TelephonyManager.NETWORK_TYPE_CDMA || subType == TelephonyManager.NETWORK_TYPE_GPRS || subType == TelephonyManager.NETWORK_TYPE_EDGE) {
					netType = "2g";
				} else if (subType == TelephonyManager.NETWORK_TYPE_UMTS || subType == TelephonyManager.NETWORK_TYPE_HSDPA || subType == TelephonyManager.NETWORK_TYPE_EVDO_A || subType == TelephonyManager.NETWORK_TYPE_EVDO_0 || subType == TelephonyManager.NETWORK_TYPE_EVDO_B) {
					netType = "3g";
				} else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {// LTE是3g到4g的过渡，是3.9G的全球标准
					netType = "4g";
				}
			}
		}
		return netType;
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:19:46
	 * @描述:TODO[获取MAC地址]
	 * @param context
	 * @return String MAC地址
	 */
	public static String getMAC(Context context) {
		if (context == null) {
			return null;
		}
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		String macAddress = info.getMacAddress();// 4C:AA:16:24:F5:43
		if (TextUtils.isEmpty(macAddress))
			macAddress = "";
		return macAddress;
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:45:03
	 * @描述:TODO[获取IP]
	 * @param context
	 * @return String
	 */
	public static String getIP(Context context) {

		//		switch (getNetworkType(context)) {
		//		case ConnectivityManager.TYPE_WIFI: {
		//			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		//			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		//			int ipAddress = wifiInfo.getIpAddress();
		//			
		//			return ipAddress + "";
		//		}
		//		case ConnectivityManager.TYPE_MOBILE: {
		//		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		//		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		//		int ipAddress = wifiInfo.getIpAddress();
		//		try {
		//			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
		//				NetworkInterface intf = en.nextElement();
		//				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
		//					InetAddress inetAddress = enumIpAddr.nextElement();
		//					if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
		//						return inetAddress.getHostAddress();
		//					}
		//				}
		//			}
		//		} catch (SocketException e) {
		//				CMBCLog.e(TAG, e.getMessage(), e);
		//		}
		//			break;
		//		}
		//		default:
		//			break;
		//		}
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
						return inetAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException e) {
		}
		return null;
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午11:46:11
	 * @描述:TODO[isWifi(判断用户是否 在wifi 网络环境)]
	 * @param context
	 * @return int
	 */
	public static int isWifi(Context context) {
		if (context == null) {
			return 0;
		}
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		@SuppressWarnings("deprecation")
		NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (info != null) {
			if (info.isAvailable()) {
				return 1;
			}
		}
		return 0;
	}

}
