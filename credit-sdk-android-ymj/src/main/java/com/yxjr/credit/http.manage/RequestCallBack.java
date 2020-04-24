package com.yxjr.credit.http.manage;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.yxjr.credit.constants.HttpConstant;
import com.yxjr.credit.constants.SpConstant;
import com.yxjr.credit.log.YxLog;
import com.yxjr.credit.security.AESSecurity;
import com.yxjr.credit.ui.YxHandler;
import com.yxjr.credit.util.YxCommonUtil;
import com.yxjr.credit.util.YxStoreUtil;
import com.yxjr.http.core.Response;
import com.yxjr.http.core.call.IRequestCallBack;

public class RequestCallBack implements IRequestCallBack {

	public void onSucces(String result) {
	}

	public void onFailure(String errorCode, String errorMsg) {
		//		ToastUtil.showToast(mContext, errorMsg);
	}

	private Context mContext;

	public RequestCallBack(Context context) {
		this.mContext = context;
		this.mBundle = new Bundle();
	}

	public void setGenerateString(String generateString) {
		this.generateString = generateString;
	}

	private String generateString;

	@Override
	public void onResponse(Response responses) {
		String serviceId = responses.getServiceId();
		String responseEncrypt = responses.getBody();
		if (serviceId.equals(HttpConstant.Request.INIT_SESSION_TOKEN)) {
			// ======返回报文======
			YxLog.d("======返回报文======" + serviceId);
			YxLog.d("[InitToken]Response------" + responseEncrypt);
			// ======解析======
			YxLog.d("=======解析=======" + serviceId);
			try {
				JSONObject data = new JSONObject(responseEncrypt);
				JSONObject headJson = data.getJSONObject("head");
				String codeResponse = headJson.getString("code");
				String msgResponse = headJson.getString("msg");
				String tokenResponse = headJson.getString("random");
				if (codeResponse.equals(HttpConstant.Response.SUCCEED)) {// 响应成功才处理其他内容
					// ======报文token解密======
					String sessiontokenDecrypt = decrypt(tokenResponse, generateString);
					// ======保存token======
					if (YxCommonUtil.isNotBlank(sessiontokenDecrypt)) {
						if (sessiontokenDecrypt.length() == 8) {
							YxStoreUtil.save(mContext, SpConstant.SESSION_TOKEN_KEY, generateString.substring(0, 8) + sessiontokenDecrypt);// 随机数前八位+服务器后八位
						} else {
							YxStoreUtil.save(mContext, SpConstant.SESSION_TOKEN_KEY, sessiontokenDecrypt);// 不是八位的则视为不更新token，服务器返回，前端直接保存，不做拼接处理
						}
					}
					YxLog.d("[InitToken]state------" + YxStoreUtil.get(mContext, SpConstant.SESSION_TOKEN_KEY));
					// ======解析body======
					String bodyStr = data.getString("body");
					JSONObject bodyJson = new JSONObject(bodyStr);
					JSONObject serviceHeader = bodyJson.getJSONObject("serviceHeader");
					String responseCode = serviceHeader.getString("responseCode");
					//				String responseMsg = serviceHeader.getString("responseMsg");
					// ======返回码成功状态======
					if (responseCode.equals(HttpConstant.Response.SUCCEED)) {
						new RequestFactory(mContext).parseServiceBody(mContext, bodyJson.getJSONObject("serviceBody"));
					}
					mBundle.putString("result", "0000");
					sendMessage(ON_SUCCES, mBundle);
				} else {// 反之返回响应信息，并关闭所有
					YxStoreUtil.save(mContext, SpConstant.SESSION_TOKEN_KEY, "");
					mBundle.putString("errorCode", codeResponse);
					mBundle.putString("errorMsg", msgResponse);
					sendMessage(ON_FAILURE, mBundle);
				}
			} catch (JSONException e) {
				mBundle.putString("errorCode", "9999");
				mBundle.putString("errorMsg", "解析错误");
				sendMessage(ON_FAILURE, mBundle);
				e.printStackTrace();
			} catch (Exception e) {
				mBundle.putString("errorCode", "9999");
				mBundle.putString("errorMsg", "异常");
				sendMessage(ON_FAILURE, mBundle);
				e.printStackTrace();
			}
			YxLog.i("--------***[InitToken(end)]***--------");
		} else {
			// ======返回报文======
			YxLog.d("---======返回" + serviceId + "报文======)" + responseEncrypt);
			// ======解析外层head======
			YxLog.d("---======解析" + serviceId + "外层head======" + serviceId);
			JSONObject response;
			try {
				response = new JSONObject(responseEncrypt);
				JSONObject headJson = response.getJSONObject("head");// 得到外层head内容
				String codeResponse = headJson.getString("code");// 外层响应吗
				String msgResponse = headJson.getString("msg");// 外层相应信息
				String tokenResponse = headJson.getString("random");// 外层token
				YxLog.d("---======" + serviceId + "外层head======" + headJson);
				if (codeResponse.equals(HttpConstant.Response.SUCCEED)) {// 响应成功才处理其他内容
					// ======检验token======
					if (YxCommonUtil.isNotBlank(tokenResponse)) {// 如果返回token不为空则更新本地token
						String sessiontokenDecrypt = this.decrypt(tokenResponse, YxStoreUtil.get(mContext, SpConstant.SESSION_TOKEN_KEY));// 解密token
						YxStoreUtil.save(mContext, SpConstant.SESSION_TOKEN_KEY, sessiontokenDecrypt);
					}
					// ======解析外层body======
					YxLog.d("---=====解析" + serviceId + "外层body=====" + serviceId);
					String bodyResponse = response.getString("body");// 得到外层body内容
					// ======解密外层body======
					YxLog.d("---=====解密" + serviceId + "外层body=====" + serviceId);
					String bodyDecrypt = "";
					if (!bodyResponse.equals("null")) {
						bodyDecrypt = this.decrypt(bodyResponse, YxStoreUtil.get(mContext, SpConstant.SESSION_TOKEN_KEY));
					}
					YxLog.d("[Post(" + serviceId + ")]------(ResponseBody)" + bodyDecrypt);
					// ======解析内层head======
					YxLog.d("");
					String resultServiceId = null;
					String resultResponseCode = null;
					String resultResponseMsg = null;
					try {
						JSONObject object = new JSONObject(bodyDecrypt);
						JSONObject serviceHeader = object.getJSONObject("serviceHeader");// 内层head
						resultServiceId = serviceHeader.getString("serviceId");
						resultResponseCode = serviceHeader.getString("responseCode");// 内层响应码
						resultResponseMsg = serviceHeader.getString("responseMsg");// 内层响应信息
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						YxHandler mHandler = YxHandler.getInstance(null, mContext, Looper.getMainLooper());
						Message msgToast = mHandler.obtainMessage(mHandler.TOAST, 3, 3, "无效数据");
						mHandler.sendMessage(msgToast);
						e.printStackTrace();
					}
					// ======对原生页面请求做处理======
					if (YxCommonUtil.isNotBlank(resultServiceId) && YxCommonUtil.isNotBlank(resultResponseCode) && YxCommonUtil.isNotBlank(resultResponseMsg)) {// 如果head某一个字段内容为空
						if (resultServiceId.equals(HttpConstant.Request.NAME_ASSET_APPROVE) || resultServiceId.equals(HttpConstant.Request.CAR_ASSET_APPROVE) || resultServiceId.equals(HttpConstant.Request.HOUSE_ASSET_APPROVE) || resultServiceId.equals(HttpConstant.Request.NAME_ASSET_APPROVE_PATCH) || resultServiceId.equals(HttpConstant.Request.CAR_ASSET_APPROVE_PATCH) || resultServiceId.equals(HttpConstant.Request.HOUSE_ASSET_APPROVE_PATCH)) {// 如果是原生页面的请求,对报文进行解析
							if (resultResponseCode.equals(HttpConstant.Response.SUCCEED)) {// 如果响应吗为成功状态则返回数据
								//						bodyReturn = bodyDecrypt;
								mBundle.putString("result", bodyDecrypt);
								sendMessage(ON_SUCCES, mBundle);
							} else {// 反之返回null且提示错误信息
								YxHandler mHandler = YxHandler.getInstance(null, mContext, Looper.getMainLooper());
								Message msgToast = mHandler.obtainMessage(mHandler.TOAST, 3, 3, resultResponseMsg);
								mHandler.sendMessage(msgToast);
							}
						} else {// 反之返回原报文
							//					bodyReturn = bodyDecrypt;
							mBundle.putString("result", bodyDecrypt);
							sendMessage(ON_SUCCES, mBundle);
						}
					}
				} else if (codeResponse.equals(HttpConstant.Response.LOGIN_DATED) || codeResponse.equals(HttpConstant.Response.LOGIN_REPEAT) || codeResponse.equals(HttpConstant.Response.AES_DECODE_ERROR)) {
					// 如果状态为[登录过期，请重新登录][您已经在另一台设备上登录，请重新登录][AES解密错误]则提示并关闭所有
					if ((!serviceId.equals(HttpConstant.Request.SEND_CONTACTS)) && (!serviceId.equals(HttpConstant.Request.SEND_SMS)) && (!serviceId.equals(HttpConstant.Request.SEND_BROWSER_HISTORY)) && (!serviceId.equals(HttpConstant.Request.SEND_CALL_LOG)) && (!serviceId.equals(HttpConstant.Request.SEND_APP_LIST))) {
						YxHandler mHandler = YxHandler.getInstance(null, mContext, Looper.getMainLooper());
						Message finish = mHandler.obtainMessage(mHandler.FINISH, 4, 4, msgResponse);
						mHandler.sendMessage(finish);
					} else {
						mBundle.putString("errorCode", codeResponse);
						mBundle.putString("errorMsg", msgResponse);
						sendMessage(ON_FAILURE, mBundle);
					}
				} else {// 响应失败，提示响应信息
					if ((!serviceId.equals(HttpConstant.Request.SEND_CONTACTS)) && (!serviceId.equals(HttpConstant.Request.SEND_SMS)) && (!serviceId.equals(HttpConstant.Request.SEND_BROWSER_HISTORY)) && (!serviceId.equals(HttpConstant.Request.SEND_CALL_LOG)) && (!serviceId.equals(HttpConstant.Request.SEND_APP_LIST))) {
						YxHandler mHandler = YxHandler.getInstance(null, mContext, Looper.getMainLooper());
						Message msgToast = mHandler.obtainMessage(mHandler.TOAST, 3, 3, msgResponse);
						mHandler.sendMessage(msgToast);
					} else {
						mBundle.putString("errorCode", codeResponse);
						mBundle.putString("errorMsg", msgResponse);
						sendMessage(ON_FAILURE, mBundle);
					}
				}
			} catch (JSONException e1) {
				e1.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Deprecated
	@Override
	public void onFailure(Exception e) {
		mBundle.putString("errorCode", "9999");
		mBundle.putString("errorMsg", "网络出错");
		sendMessage(ON_FAILURE, mBundle);
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-18 上午10:48:58
	 * @描述:TODO[AES解密(需登录)]
	 * @param data待解密数据
	 * @param mAesKey密钥
	 * @return String 解密后数据
	 * @throws Exception
	 */
	public String decrypt(String data, String mAesKey) throws Exception {

		try {
			return AESSecurity.decrypt(data, mAesKey);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	private Handler getHandler() {
//		synchronized (InternalHandler.class) {
//			if (mHandler == null) {
				mHandler = new InternalHandler();
//			}
			return mHandler;
//		}
	}

	private Bundle mBundle;
	private InternalHandler mHandler;

	@SuppressLint("HandlerLeak")
	private class InternalHandler extends Handler {
		public InternalHandler() {
			super(Looper.getMainLooper());
		}

		@Override
		public void handleMessage(Message msg) {
			Bundle obj = (Bundle) msg.obj;
			switch (msg.what) {
			case ON_SUCCES:
				onSucces(obj.getString("result"));
				break;
			case ON_FAILURE:
				onFailure(obj.getString("errorCode"), obj.getString("errorMsg"));
				break;
			default:
				break;
			}
		}
	}

	private final int ON_SUCCES = 200;
	private final int ON_FAILURE = 300;

	private void sendMessage(int what, Object object) {
		Message message = getHandler().obtainMessage(what, object);
		message.sendToTarget();
	}

}
