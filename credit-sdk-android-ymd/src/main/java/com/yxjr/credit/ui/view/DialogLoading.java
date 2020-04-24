package com.yxjr.credit.ui.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;

public class DialogLoading extends ProgressDialog {

	public DialogLoading(Context context) {
		super(context, android.R.style.Theme_Holo_Dialog_NoActionBar);
		Window window = this.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.alpha = 1.0f; // 设置透明度为0.5
		window.setAttributes(lp);
		window.setDimAmount(0f);
		this.setMessage("加载中...");
		this.setCancelable(false);
		this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
	}

	public DialogLoading(Context context, String message) {
		super(context, android.R.style.Theme_Holo_Dialog_NoActionBar);
		Window window = this.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.alpha = 1.0f; // 设置透明度为0.5
		window.setAttributes(lp);
		window.setDimAmount(0f);
		this.setMessage(message);
		this.setCancelable(false);
		this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

	}
}
