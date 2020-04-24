package com.yxjr.credit.ui;

import com.yxjr.credit.util.ToastUtil;
import com.yxjr.credit.util.YxNetworkUtil;

import android.app.Activity;
import android.os.Bundle;

/**
 * All rights Reserved, Designed By ClareShaw
 * 
 * @公司:益芯金融
 * @作者:xiaochangyou
 * @版本:V1.0
 * @创建时间:2016-7-15 下午5:26:46
 * @描述:TODO[Activity基类,所有Activity类必须继承]
 */
public class YxBaseActivity extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		YxActivityManager.addActivity(this);
	}

	protected void onResume() {
		super.onResume();
	}

	protected void onDestroy() {
		super.onDestroy();
		if (!isFinishing()) {
			YxActivityManager.removeActivity(getClass());
		}
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-15 下午5:27:14
	 * @描述:TODO[网络是否可用]
	 * @return boolean true网络可用
	 */
	protected final boolean checkNetwork() {
		if (YxNetworkUtil.getNetworkType(this) == -1) {
			ToastUtil.showToast(this, "网络不可用,请检查网络!");
			return false;
		}
		return true;
	}
}
