package com.yxjr.credit.http.manage;

import java.io.File;

import org.json.JSONObject;

import android.content.Context;

import com.yxjr.credit.constants.HttpConstant;
import com.yxjr.credit.constants.SpConstant;
import com.yxjr.credit.log.YxLog;
import com.yxjr.credit.security.AESSecurity;
import com.yxjr.credit.security.Md5;
import com.yxjr.credit.security.YxjrSecurity;
import com.yxjr.credit.util.YxAndroidUtil;
import com.yxjr.credit.util.YxStoreUtil;
import com.yxjr.credit.util.YxUtility;
import com.yxjr.http.HttpClient;
import com.yxjr.http.builder.Headers;
import com.yxjr.http.builder.Request;
import com.yxjr.http.builder.RequestParams;
import com.yxjr.http.core.io.JsonContent;

public class RequestEngine {

	private Context mContext;
	private boolean mIsSerial;
	public HttpClient mHttpClient;

	private String mPartnerId;
	private String mPartnerLoginId;
	private String mUuid;
	private String mBundleId;
	private String mOsType;

	private RequestFactory mFactory;

	public RequestEngine(Context context) {
		init(context, true);
	}

	/**
	 * @param context
	 * @param isSerial 是否串行【true 串行；false 并行】
	 */
	public RequestEngine(Context context, boolean isSerial) {
		init(context, isSerial);
	}

	//是否串行：默认true
	//是否显示加载框：默认true
	private void init(Context context, boolean isSerial) {
		this.mContext = context;
		this.mIsSerial = isSerial;
		mHttpClient = new HttpClient(mIsSerial);
		mFactory = new RequestFactory(mContext);
		mPartnerId = YxStoreUtil.get(mContext, SpConstant.PARTNER_ID);
		mPartnerLoginId = YxStoreUtil.get(mContext, SpConstant.PARTNER_ID_CARD_NUM);
		mUuid = YxAndroidUtil.getUUID(mContext);
		mBundleId = YxAndroidUtil.getAppPackage(mContext);
		mOsType = YxAndroidUtil.OS_TYPE;
	}

	public void executeInitToken(RequestCallBack callback) {
		String serviceId = HttpConstant.Request.INIT_SESSION_TOKEN;
		Headers.Builder header = new Headers.Builder().addHeader("serviceId", serviceId).addHeader("partnerId", mPartnerId).addHeader("partnerLoginId", Md5.encrypt(mPartnerLoginId)).addHeader("uuid", mUuid)
				.addHeader("bundleId", mBundleId).addHeader("osType", mOsType).addHeader("content-type", "application/json;charset=UTF-8");
		//======请求参数======
		String generateString = YxUtility.generateString(16);// 生成16位随机数
		callback.setGenerateString(generateString);
		String paramEncrypt = YxjrSecurity.RsaEncrypt(mContext, generateString);// RSA加密
		//======开始请求======
		YxLog.d("======开始请求======" + serviceId);
		Request request = new Request.Builder().url(HttpConstant.SERVER_URL).content(new JsonContent(paramEncrypt)).method("POST").headers(header).build();

		mHttpClient.newCall(request).execute(callback);
	}

	public void execute(String serviceId, JSONObject params, RequestCallBack callback) {
		YxLog.i("---==--------***[" + serviceId + "(start)]***--------");
		// ======HttpHead相关开始======
		Headers.Builder header = new Headers.Builder().addHeader("serviceId", serviceId).addHeader("partnerId", mPartnerId).addHeader("partnerLoginId", Md5.encrypt(mPartnerLoginId)).addHeader("uuid", mUuid)
				.addHeader("bundleId", mBundleId).addHeader("osType", mOsType).addHeader("content-type", "application/json;charset=UTF-8");
		// ======HttpHead相关结束======
		YxLog.d("---======开始" + serviceId + "请求======");
		YxLog.d("---======请求" + serviceId + "参数======" + params);
		JSONObject paramaters = mFactory.getParamaters(mContext, params);
		YxLog.d("---======完整" + serviceId + "参数======" + paramaters);
		String paramEncrypt = null;
		try {
			paramEncrypt = encypt(paramaters.toString(), YxStoreUtil.get(mContext, SpConstant.SESSION_TOKEN_KEY));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Request request = new Request.Builder().url(HttpConstant.SERVER_URL).content(new JsonContent(paramEncrypt)).method("POST").headers(header).build();
		mHttpClient.newCall(request).execute(callback);
	}

	public void upload(String appNo, File[] files, UploadCallBack callback) {
		if (files == null) {
			callback.onFailure("555555", "文件为null");
			return;
		}
		if (files.length == 0) {
			callback.onFailure("555555", "无文件");
			return;
		}
		YxLog.d("---======准备文件" + appNo + "上传======");
		YxLog.d("---======文件数 " + files.length + " 个======");
		RequestParams params = new RequestParams();
		params.put("appNo", appNo);
		for (int i = 0; i < files.length; i++) {
			if (null == files[i]) {
				callback.onFailure("555555", "文件为null");
				return;
			} else {
				params.putFile("files", files[i]);
			}
			YxLog.d("---======添加文件 " + i + 1 + " 个======");
		}
		Request request = new Request.Builder().url(HttpConstant.UPLOAD_URL).method("POST").params(params).headers(new Headers.Builder().addHeader("Cookie", " name=appNo")).build();
		mHttpClient.newCall(request).intercept(callback).execute(callback);
	}

	public String encypt(String data, String mAesKey) throws Exception {
		try {
			return AESSecurity.encrypt(data, mAesKey);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}
}
