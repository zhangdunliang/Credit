package com.yxjr.credit.service;

import org.json.JSONException;
import org.json.JSONObject;

import com.yxjr.credit.constants.HttpConstant;
import com.yxjr.credit.constants.SpConstant;
import com.yxjr.credit.constants.YxCommonConstant;
import com.yxjr.credit.http.manage.RequestEngine;
import com.yxjr.credit.http.manage.RequestCallBack;
import com.yxjr.credit.log.YxLog;
import com.yxjr.credit.util.YxCommonUtil;
import com.yxjr.credit.util.YxStoreUtil;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import cn.tongdun.android.shell.FMAgent;
import cn.tongdun.android.shell.exception.FMException;

/**
 * All rights Reserved, Designed By ClareShaw
 * 
 * @公司:益芯金融
 * @作者:xiaochangyou
 * @版本:V1.0
 * @创建时间:2016-7-15 下午5:25:14
 * @描述:TODO[主要用于心跳]
 */
public class YxService extends Service {
	private Thread mHeartThread;
	private Thread mBlackBoxThread;

	private boolean HEART_IS_EXIT = false;
	private boolean BLACK_BOX_IS_EXIT = false;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				YxLog.d("service======normal handle");
				sendMsg(HttpConstant.Request.HEART_BEAT);
				break;
			case 1:
				YxLog.d("service======BLACK handle");
				sendBlackBox(HttpConstant.Request.BLACK_BOX_HEART_BEAT);
				break;
			default:
				break;
			}
		}

	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		YxLog.i("======In Server onCreate");
		final int HEART_SLEEP_TIME = formatDate(YxStoreUtil.get(this, SpConstant.HBSF));
		if (HEART_SLEEP_TIME != 0) {
			HEART_IS_EXIT = true;
		}
		mHeartThread = new Thread(new Runnable() {
			@SuppressWarnings("static-access")
			@Override
			public void run() {
				while (HEART_IS_EXIT) {
					try {
						YxLog.d("service======normal run");
						mHandler.sendEmptyMessage(0);
						mHeartThread.sleep(HEART_SLEEP_TIME);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		if (HEART_IS_EXIT) {
			mHeartThread.start();
		}
		final int BLACK_BOX_SLEEP_TIME = formatDate(YxStoreUtil.get(this, SpConstant.HBSF_L));
		if (BLACK_BOX_SLEEP_TIME != 0) {
			BLACK_BOX_IS_EXIT = true;
		}
		mBlackBoxThread = new Thread(new Runnable() {
			@SuppressWarnings("static-access")
			@Override
			public void run() {
				while (BLACK_BOX_IS_EXIT) {
					try {
						YxLog.d("service======BLACK run");
						mHandler.sendEmptyMessageDelayed(1, 5000);//延迟五秒，尽量避免同盾SDK初始化未完成
						mBlackBoxThread.sleep(BLACK_BOX_SLEEP_TIME);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		if (BLACK_BOX_IS_EXIT) {
			String initStatus = FMAgent.getInitStatus();
			if (initStatus.equals("successful")) {
				mBlackBoxThread.start();
			} else {
				if (initStatus.equals("failed")) {
					try {
						FMAgent.init(this, YxCommonConstant.Params.TONGDUN);
					} catch (FMException e) {
						e.printStackTrace();
					}
				}
				mBlackBoxThread.start();
			}
		}
	}

	@Override
	public void onDestroy() {
		YxLog.i("======In Server onDestroy");
		HEART_IS_EXIT = false;
		//		BLACK_BOX_IS_EXIT = false;
		super.onDestroy();
	}

	public int formatDate(String sf) {
		int iHbf = 0;
		String time = null;// 时间
		String unit = null;// 单位d:天,h：消失,m：分钟,s：秒
		// sf:间隔时间，带单位的(服务器返回)
		if (YxCommonUtil.isNotBlank(sf)) {
			time = sf.substring(0, sf.length() - 1);
			unit = sf.substring(sf.length() - 1, sf.length());
			if (YxCommonUtil.isNotBlank(time)) {
				iHbf = Integer.parseInt(time);
			}
		}
		int sleep = 0;// 1000等于1秒
		if (unit != null) {
			switch (unit) {
			case "d":// 天
				sleep = iHbf * 24 * 60 * 60 * 1000;
				break;
			case "h":// 小时
				sleep = iHbf * 60 * 60 * 1000;
				break;
			case "m":// 分钟
				sleep = iHbf * 60 * 1000;
				break;
			case "s":// 秒
				sleep = iHbf * 1000;
				break;

			default:
				break;
			}
		}
		return sleep;
	}

	// 发送心跳
	public void sendMsg(final String serviceId) {
		YxLog.d("======Service onExecute");
		String idCardNum = YxStoreUtil.get(YxService.this, SpConstant.PARTNER_ID_CARD_NUM);
		String phoneNumber = YxStoreUtil.get(YxService.this, SpConstant.PARTNER_PHONENUMBER);
		String name = YxStoreUtil.get(YxService.this, SpConstant.PARTNER_REAL_NAME);
		JSONObject user = new JSONObject();
		try {
			user.put("cert", idCardNum);
			user.put("name", name);
			user.put("mobileNo", phoneNumber);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		new RequestEngine(YxService.this, false).execute(serviceId, user, new RequestCallBack(YxService.this) {
			@Override
			public void onSucces(String result) {
				super.onSucces(result);
			}
		});
	}

	public void sendBlackBox(final String serviceId) {
		String blackBox = FMAgent.onEvent(this);
		//		String initStatus = FMAgent.getInitStatus();
		String idCardNum = YxStoreUtil.get(YxService.this, SpConstant.PARTNER_ID_CARD_NUM);
		String phoneNumber = YxStoreUtil.get(YxService.this, SpConstant.PARTNER_PHONENUMBER);
		JSONObject obj = new JSONObject();
		try {
			obj.put("cert", idCardNum);
			obj.put("mobileNo", phoneNumber);
			obj.put("blackBox", blackBox);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		new RequestEngine(YxService.this, false).execute(serviceId, obj, new RequestCallBack(YxService.this) {
			@Override
			public void onSucces(String result) {
				super.onSucces(result);
			}
		});
	}
}