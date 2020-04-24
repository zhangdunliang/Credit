package com.yxjr.credit.http.manage;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.yxjr.credit.log.YxLog;
import com.yxjr.http.core.Response;
import com.yxjr.http.core.call.IRequestCallBack;
import com.yxjr.http.core.call.IUploadListener;

public class UploadCallBack implements IUploadListener, IRequestCallBack {

	public void onSucces(String result) {
		onFinish();
	}

	public void onFailure(String errorCode, String errorMsg) {
		onFinish();
	}

	public void onProgress(long currentLength, long totalLength, int index) {

	}

	public void onFinish() {
	}

	@Deprecated
	@Override
	public void onProgress(int index, long currentLength, long totalLength) {
		YxLog.d("---======第 " + index + " 个文件======");
		YxLog.d("---======当前长度 " + currentLength + " ======");
		YxLog.d("---======文件总长度 " + totalLength + "  ======");
		YxLog.d("---======上传进度 " + ((float) currentLength / totalLength) * 100 + "  ======");
		mBundle.putInt("index", index);
		mBundle.putLong("currentLength", currentLength);
		mBundle.putLong("totalLength", totalLength);
		sendMessage(ON_PROGRESS, mBundle);
	}

	private Handler getHandler() {
		synchronized (AsyncTask.class) {
			if (mHandler == null) {
				mHandler = new InternalHandler();
			}
			return mHandler;
		}
	}

	private Bundle mBundle;

	public UploadCallBack() {
		mBundle = new Bundle();
	}

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
			case ON_PROGRESS:
				onProgress(obj.getLong("currentLength"), obj.getLong("totalLength"), obj.getInt("index"));
				break;
			case ON_SUCCES:
				onSucces(obj.getString("result"));
				break;
			case ON_FAILURE:
				onFailure(obj.getString("status"), obj.getString("errorMsg"));
				break;
			default:
				break;
			}
		}
	}

	private final int ON_PROGRESS = 100;
	private final int ON_SUCCES = 200;
	private final int ON_FAILURE = 300;

	private void sendMessage(int what, Object object) {
		Message message = getHandler().obtainMessage(what, object);
		message.sendToTarget();
	}

	@Override
	public void onResponse(Response response) {
		String result = response.getBody();
		YxLog.d("---======文件上传完毕======");
		if (result == null) {
			YxLog.e("---======返回数据为空======");
			return;
		}
		String errorMsg = null;
		String filePath = null;
		String status = null;
		try {
			JSONObject responsej = new JSONObject(result);
			errorMsg = responsej.getString("errorMsg");
			filePath = responsej.getString("filePath");
			status = responsej.getString("status");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (status == null || !status.equals("S")) {
			YxLog.e("---======状态为空或非S======");
			mBundle.putString("status", "" + status == null ? "null" : status);
			mBundle.putString("errorMsg", errorMsg);
			sendMessage(ON_FAILURE, mBundle);
			return;
		}
		if (filePath == null) {
			YxLog.e("---======路径为空======");
			mBundle.putString("status", status);
			mBundle.putString("errorMsg", errorMsg);
			sendMessage(ON_FAILURE, mBundle);
			return;
		}
		YxLog.d("---======文件上传成功======");
		mBundle.putString("result", filePath);
		sendMessage(ON_SUCCES, mBundle);
	}

	@Override
	public void onFailure(Exception e) {
		YxLog.d("---======文件上传失败======" + e);
		mBundle.putString("status", "9999");
		mBundle.putString("errorMsg", "网络出错");
		sendMessage(ON_FAILURE, mBundle);
	}
}
