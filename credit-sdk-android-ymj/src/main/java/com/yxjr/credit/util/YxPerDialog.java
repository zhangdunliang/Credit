package com.yxjr.credit.util;

import com.yxjr.credit.ui.view.ResContainer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

;

/**
 * All rights Reserved, Designed By ClareShaw
 * 
 * @公司:益芯金融
 * @作者:xiaochangyou
 * @版本:V1.0
 * @创建时间:2016-7-18 上午10:56:39
 * @描述:TODO[通讯录显示]
 */
public class YxPerDialog {

	private Dialog dialog;
	private TextView perInfo, btnInow, btnSetting;
	private View divide;
	private ResContainer Res;
	private Activity mActivity;

	public YxPerDialog(Activity activity) {
		this.Res = ResContainer.get(activity);
		this.mActivity = activity;
		dialog = new Dialog(activity, android.R.style.Theme_Wallpaper_NoTitleBar);
		dialog.setContentView(Res.layout("yxjr_credit_per_layout"));
		perInfo = (TextView) dialog.findViewById(Res.id("yx_credit_tv_detail"));
		btnInow = (TextView) dialog.findViewById(Res.id("yx_credit_tv_inow"));
		divide = dialog.findViewById(Res.id("v_divide"));
		btnSetting = (TextView) dialog.findViewById(Res.id("yx_credit_tv_setting"));
	}

	public void showAllPer(String info) {
		if (info != null) {
			perInfo.setText(info);
		}
		btnInow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		btnSetting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Settings.ACTION_SETTINGS);
				mActivity.startActivity(intent);
				dismiss();
			}
		});
		dialog.show();
	}

	public void showPer(String info) {
		divide.setVisibility(View.GONE);
		btnSetting.setVisibility(View.GONE);
		if (info != null) {
			perInfo.setText(info);
		}
		btnInow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				mActivity.finish();
			}
		});
		dialog.show();
	}

	public void dismiss() {
		dialog.dismiss();
	}

}
