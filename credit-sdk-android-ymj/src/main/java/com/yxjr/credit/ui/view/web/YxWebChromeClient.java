package com.yxjr.credit.ui.view.web;

import com.yxjr.credit.ui.view.DialogLoading;

import android.content.Context;
import android.graphics.Bitmap;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class YxWebChromeClient extends WebChromeClient {

	private Context mContext;
	private DialogLoading mDialogLoading;

	public YxWebChromeClient(Context context) {
		this.mContext = context;
		mDialogLoading = new DialogLoading(mContext);
	}

	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		super.onProgressChanged(view, newProgress);
		if (newProgress < 100) {
//			YxDialogLoading.start(mContext);
			if (!mDialogLoading.isShowing()) {
				mDialogLoading.show();
			}
		}
		if (newProgress == 100) {
//			YxDialogLoading.end(mContext);
			if (mDialogLoading.isShowing()) {
				mDialogLoading.cancel();
			}
		}
	}

	@Override
	public void onReceivedIcon(WebView view, Bitmap icon) {
		super.onReceivedIcon(view, icon);
	}

	@Override
	public void onReceivedTitle(WebView view, String title) {
		//1.0.8去除，
//		if (title.contains("error") || title.contains("not found") || title.contains("40") || title.contains("50")) {
//			view.loadUrl(YxWebView.loadErrorUrl);
//		}
		super.onReceivedTitle(view, title);
	}

	@Override
	public void onRequestFocus(WebView view) {
		super.onRequestFocus(view);
	}

}
