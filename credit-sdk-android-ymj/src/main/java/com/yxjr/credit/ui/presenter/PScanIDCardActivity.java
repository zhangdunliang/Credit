package com.yxjr.credit.ui.presenter;

import java.io.File;
import java.util.StringTokenizer;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Handler;
import android.os.Message;

import com.megvii.idcardquality.IDCardQualityResult;
import com.megvii.idcardquality.bean.IDCardAttr;
import com.megvii.idcardquality.bean.IDCardAttr.IDCardSide;
import com.yxjr.credit.constants.HttpConstant;
import com.yxjr.credit.constants.JsConstant;
import com.yxjr.credit.constants.SpConstant;
import com.yxjr.credit.http.manage.RequestEngine;
import com.yxjr.credit.http.manage.RequestCallBack;
import com.yxjr.credit.http.manage.UploadCallBack;
import com.yxjr.credit.ocr.util.Util;
import com.yxjr.credit.plugin.StatisticalTime;
import com.yxjr.credit.ui.ScanIDCardActivity;
import com.yxjr.credit.ui.view.DialogLoading;
import com.yxjr.credit.util.StringUtil;
import com.yxjr.credit.util.ToastUtil;
import com.yxjr.credit.util.YxCommonUtil;
import com.yxjr.credit.util.YxPictureUtil;
import com.yxjr.credit.util.YxStoreUtil;

public class PScanIDCardActivity {

	private Activity mActivity;
	private File[] files = null;
	private int mScanType;
	private String mAppNo;
	private final String P1 = "P1";
	private final String P2 = "P2";
	private final String P5 = "P5";
	private String mServiceId;
	private final int UPLOAD_FRONT = 3;
	private final int UPLOAD_BACK = 4;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new MyHandler();

	@SuppressLint("HandlerLeak")
	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == UPLOAD_FRONT) {
				uploadP1();
			} else if (msg.what == UPLOAD_BACK) {
				uploadP2();
			}
		}
	}

	public PScanIDCardActivity(Activity activity, int scanType, String appNo, String categoryCode) {
		this.mActivity = activity;
		this.mScanType = scanType;
		this.mAppNo = appNo;
		// this.mCategoryCode = categoryCode;
		mServiceId = StringUtil.isEmpty(categoryCode) ? HttpConstant.Request.IDENTITY_INFO : HttpConstant.Request.PATCH_IDENTITY_INFO;// 补件码不为空视为补件
	}

	public void handleResult(int side, Handler handler, IDCardQualityResult result) {
		//		croppedImageOfPortrait
		//		boolean valid = result.isValid();//身份证质量是否合格
		if (side == 0) {
			//			String picName = P1 + "_" + YxCommonUtil.getCurrentTime2() + ".jpg";
			files = new File[2];
			files[0] = Util.bmp2File(result.croppedImageOfIDCard(), P1 + "_" + YxCommonUtil.getCurrentTime2() + ".jpg");
			files[1] = Util.bmp2File(result.croppedImageOfPortrait(), P5 + "_" + YxCommonUtil.getCurrentTime2() + ".jpg");
			mHandler.sendEmptyMessage(UPLOAD_FRONT);
		} else {
			files = new File[1];
			files[0] = Util.bmp2File(result.croppedImageOfIDCard(), P2 + "_" + YxCommonUtil.getCurrentTime2() + ".jpg");
			mHandler.sendEmptyMessage(UPLOAD_BACK);
		}
	}

	public void uploadP1() {
		final DialogLoading dialogLoading = new DialogLoading(mActivity, "上传中...");
		dialogLoading.show();
		new RequestEngine(mActivity).upload(mAppNo, files, new UploadCallBack() {
			@Override
			public void onSucces(String result) {
				String[] filePathAfterSplit = new String[2];
				// filePathAfterSplit = result.split(","); //
				StringTokenizer token = new StringTokenizer(result, ",");
				while (token.hasMoreTokens()) {//校验字符串正确
					String path = token.nextToken();
					if (path.contains(P1)) {
						filePathAfterSplit[0] = path;
					} else if (path.contains(P5)) {
						filePathAfterSplit[1] = path;
					}
				}

				showError(IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT, "身份证正面照片上传成功", mAppNo);
				send(P1, filePathAfterSplit[0], filePathAfterSplit[1]);
				super.onSucces(result);
			}

			@Override
			public void onFailure(String errorCode, String errorMsg) {
				showError(IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT, "身份证正面照片上传失败" + errorMsg, mAppNo);
				ToastUtil.showToast(mActivity, "身份证正面上传失败！");
				super.onFailure(errorCode, errorMsg);
			}

			@Override
			public void onFinish() {
				dialogLoading.cancel();
//				for (int i = 0; i < files.length; i++) {
//					if (null != files[i]) {
//						if (null != files[i].getAbsolutePath()) {
//							YxPictureUtil.deleteTempFile(files[i].getAbsolutePath());
//						}
//					}
//				}
				super.onFinish();
			}
		});
	}

	public void uploadP2() {
		final DialogLoading dialogLoading = new DialogLoading(mActivity, "上传中...");
		dialogLoading.show();
		new RequestEngine(mActivity).upload(mAppNo, files, new UploadCallBack() {
			@Override
			public void onSucces(String result) {
				showError(IDCardAttr.IDCardSide.IDCARD_SIDE_BACK, "身份证反面照片上传成功", mAppNo);
				send(P2, result, null);
				super.onSucces(result);
			}

			@Override
			public void onFailure(String errorCode, String errorMsg) {
				showError(IDCardAttr.IDCardSide.IDCARD_SIDE_BACK, "身份证反面照片上传失败" + errorMsg, mAppNo);
				ToastUtil.showToast(mActivity, "身份证背面上传失败！");
				super.onFailure(errorCode, errorMsg);
			}

			@Override
			public void onFinish() {
				dialogLoading.cancel();
				for (int i = 0; i < files.length; i++) {
					if (null != files[i]) {
						if (null != files[i].getAbsolutePath()) {
							YxPictureUtil.deleteTempFile(files[i].getAbsolutePath());
						}
					}
				}
				super.onFinish();
			}
		});
	}

	public void send(final String picType, String certFilePath, String certPortraitFilePath) {
		final IDCardAttr.IDCardSide side = picType.equals(P1) ? IDCardSide.IDCARD_SIDE_FRONT : IDCardSide.IDCARD_SIDE_BACK;
		final JSONObject json = new JSONObject();
		try {
			json.put("mobileNo", YxStoreUtil.get(mActivity, SpConstant.PARTNER_PHONENUMBER));// 手机号
			json.put("name", YxStoreUtil.get(mActivity, SpConstant.PARTNER_REAL_NAME));// 姓名
			json.put("cert", YxStoreUtil.get(mActivity, SpConstant.PARTNER_ID_CARD_NUM));// 身份证
			json.put("certFront", picType.equals(P1) ? certFilePath : "");// 身份证正面
			json.put("certBack", picType.equals(P2) ? certFilePath : "");// 身份证反面
			json.put("certFrontPortrait", picType.equals(P1) ? certPortraitFilePath : "");// 身份证正面人像
			json.put("picType", picType);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		final DialogLoading dialogLoading = new DialogLoading(mActivity);
		dialogLoading.show();
		new RequestEngine(mActivity).execute(mServiceId, json, new RequestCallBack(mActivity) {
			@Override
			public void onSucces(String result) {
				String responseCode = null;
				String responseMsg = null;
				try {
					JSONObject jsonObject = new JSONObject(result).getJSONObject("serviceHeader");
					responseCode = jsonObject.getString("responseCode");
					responseMsg = jsonObject.getString("responseMsg");
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				if (responseCode != null && responseCode.equals(HttpConstant.Response.SUCCEED)) {
					showError(side, picType + "验证成功", mServiceId);

					if (picType != null) {
						if (picType.equals(P1)) {
							try {
								ScanIDCardActivity.mYxCallBack.loadUrl(JsConstant.SCAN_IDCARD, new JSONObject().put("identityStatus", "front").toString());
							} catch (JSONException e) {
								e.printStackTrace();
							}
							mActivity.finish();
							if (mScanType == 0) {
								ScanIDCardActivity.startMe(mActivity, IDCardAttr.IDCardSide.IDCARD_SIDE_BACK);
							}
						} else if (picType.equals(P2)) {
							try {
								ScanIDCardActivity.mYxCallBack.loadUrl(JsConstant.SCAN_IDCARD, new JSONObject().put("identityStatus", "reverse").toString());
							} catch (JSONException e) {
								e.printStackTrace();
							}
							AlertDialog hintDaalog = new AlertDialog.Builder(mActivity).setMessage("扫描完成").setCancelable(false).create();
							hintDaalog.show();
							new Handler().postDelayed(new Runnable() {
								public void run() {
									ToastUtil.cancleToast(mActivity);
									mActivity.finish();
								}
							}, HINT_DELAY_TIME); // 5秒
						}
					}
					ToastUtil.showToast(mActivity, responseMsg);
				} else {
					AlertDialog hintDaalog = new AlertDialog.Builder(mActivity).setMessage("验证失败").setCancelable(false).create();
					hintDaalog.show();
					new Handler().postDelayed(new Runnable() {
						public void run() {
							mActivity.finish();
						}
					}, HINT_DELAY_TIME);
					if (responseMsg != null) {
						showError(side, picType + "验证失败" + responseMsg, mServiceId);
						ToastUtil.showToast(mActivity, responseMsg);
					} else {
						showError(side, picType + "验证失败", mServiceId);
					}
				}
				dialogLoading.cancel();
				super.onSucces(result);
			}

			@Override
			public void onFailure(String errorCode, String errorMsg) {
				showError(side, picType + "验证失败" + errorMsg, mServiceId);
				dialogLoading.cancel();
				AlertDialog hintDaalog = new AlertDialog.Builder(mActivity).setMessage("验证失败").setCancelable(false).create();
				hintDaalog.show();
				new Handler().postDelayed(new Runnable() {
					public void run() {
						mActivity.finish();
					}

				}, HINT_DELAY_TIME);
				super.onFailure(errorCode, errorMsg);
			}
		});
	}

	private final int HINT_DELAY_TIME = 2000;

	public void showError(IDCardAttr.IDCardSide side, String errorStr, String sessionId) {
		new StatisticalTime.Builder().addContext(mActivity).addErrorInfo(errorStr).addOperPageName(side == IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT ? "Face++身份证识别-正面" : "Face++身份证识别-反面").addOperElementType("ocr")
				.addOperElementName("身份证扫描").addSessionId(sessionId).build();
	}
}
