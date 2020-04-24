package com.yxjr.credit.util;

import java.util.List;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;

/**
 * All rights Reserved, Designed By ClareShaw
 * 
 * @公司:益芯金融
 * @作者:xiaochangyou
 * @版本:V1.0
 * @创建时间:2016-7-18 上午11:55:45
 * @描述:TODO[请简要说明此类用途]
 */
public class YxLocationUtil {

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-8-1 下午3:23:24
	 * @描述:TODO[强制帮用户打开GPS]
	 * @param mContext
	 */
	public static void openGPS(Context mContext) {
		Intent GPSIntent = new Intent();
		GPSIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
		GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
		GPSIntent.setData(Uri.parse("custom:3"));
		try {
			PendingIntent.getBroadcast(mContext, 0, GPSIntent, 0).send();
		} catch (CanceledException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-8-1 下午3:23:45
	 * @描述:TODO[是否有可用位置提供器，如一个都没有则提示用户打开权限]
	 * @param mContext
	 * @return boolean true有可用
	 */
	public static boolean isLocPermission(Context mContext) {
		LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);// 获得LocationManager的实例
		List<String> providerList = locationManager.getProviders(true);// 获取所有可用的位置提供器
		if (providerList.contains(LocationManager.NETWORK_PROVIDER) || providerList.contains(LocationManager.GPS_PROVIDER)) {//优先使用agps、gps
			return true;
		} else {// 没有可用的位置提供器
			return false;
		}
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-8-1 下午3:24:13
	 * @描述:TODO[判断GPS是否开启，一般手机GPS模块都有，增大获取几率]
	 * @param mContext
	 * @return boolean true开启
	 */

	public static boolean isOpenGPS(Context mContext) {
		LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）  
		//		boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);// 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）  
		//		if (gps || network) {
		//		}
		if (gps) {
			return true;
		}
		return false;
	}

}
