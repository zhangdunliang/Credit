package com.yxjr.credit.util;

import com.yxjr.credit.constants.YxCommonConstant;
import com.yxjr.credit.ui.CameraActivity;
import com.yxjr.credit.ui.view.ResContainer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class DialogUtil {

	private Activity mActivity = null;
	private Context mContext = null;

	public DialogUtil(Activity activity) {
		this.mActivity = activity;
		this.mContext = activity;
	}

	public DialogUtil(Context context) {
		this.mContext = context;
	}

	public void showDialogForActivity(String message) {
		if (mActivity != null) {
			AlertDialog alertDialog = new AlertDialog.Builder(mActivity).setTitle(message)
					.setNegativeButton("确认", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mActivity.finish();
						}
					}).setCancelable(false).create();
			if (!mActivity.isFinishing())
				alertDialog.show();
		}
	}

	public void showDialogForHint(CharSequence msg) {
		AlertDialog alertDialog = new AlertDialog.Builder(mContext).setTitle(msg)
				.setNegativeButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).setCancelable(false).create();
		alertDialog.show();
	}

	public void showDialog(String message, String cancelTitle, DialogInterface.OnClickListener cancelListener,
			String confirmTitle, DialogInterface.OnClickListener confirmListener) {
		AlertDialog alertDialog = new AlertDialog.Builder(mContext).setMessage(message)// .setTitle(message)
				.setPositiveButton(confirmTitle, confirmListener).setNegativeButton(cancelTitle, cancelListener)
				.setCancelable(false).create();
		alertDialog.show();
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2017-3-7 上午11:18:14
	 * @描述:TODO[实名认证页，身份证照片预览专用]
	 * @param picPath
	 * @param requestCode
	 * @param picName
	 */
	public void showDialog(final String picPath, final int requestCode, final String picName) {
		if (mActivity == null) {
			return;
		}
		final Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Wallpaper_NoTitleBar);
		LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ResContainer R = ResContainer.get(mActivity);
		View view = inflater.inflate(R.layout("yxjr_credit_dialog"), null);
		ImageView close = (ImageView) view.findViewById(R.id("yx_credit_iv_close"));
		ImageView preview = (ImageView) view.findViewById(R.id("yx_credit_iv_preview"));
		Button again = (Button) view.findViewById(R.id("yx_credit_bt_again"));
		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		preview.setAdjustViewBounds(true);
		preview.setImageBitmap(YxPictureUtil.getSmallBitmap(picPath));

		again.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (requestCode == YxCommonConstant.ActivityCode.RequestCode.AUTONYM_CERTIFY_ID_CARD_HAND) {
					mActivity.startActivityForResult(new Intent(mActivity, CameraActivity.class)
							.putExtra("picName", picName).putExtra("isvertical", true).putExtra("isChange", true),
							requestCode);
				} else {
					mActivity.startActivityForResult(
							new Intent(mActivity, CameraActivity.class).putExtra("picName", picName), requestCode);
				}
				dialog.dismiss();
			}
		});
		dialog.setContentView(view,
				new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		// dialog.setContentView(view);

		Window window = dialog.getWindow();
		window.getDecorView().setPadding(0, 0, 0, 0);
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		window.setAttributes(lp);
		dialog.show();
	}

	public void onDestory() {
		if (mActivity != null) {
			mActivity = null;
		}
		if (mContext != null) {
			mContext = null;
		}
	}
}