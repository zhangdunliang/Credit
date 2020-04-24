package com.yxjr.credit.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

	public static Toast mToast;

	/**
	 * 输出toast
	 */
	public static void showToast(Context context, String str) {
		if (context != null) {
			if (mToast != null) {
				mToast.cancel();
			}
			mToast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
			// 可以控制toast显示的位置
			//			toast.setGravity(Gravity.TOP, 0, 30);
			mToast.show();
		}
	}

	/**
	 * 取消弹出toast
	 */
	public static void cancleToast(Context context) {
		if (context != null) {
			if (mToast != null) {
				mToast.cancel();
			}
		}
	}
}
