package com.yxjr.credit.ui.view.web;

import com.yxjr.credit.ui.YxCallBack;

import android.content.Context;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class YxWebViewLayout extends FrameLayout {
	private YxWebView webView;
	private Context mContext;
	private YxCallBack mCallBack;

	public YxWebViewLayout(Context context, YxCallBack callBack) {
		super(context);
		this.mContext = context;
		this.mCallBack = callBack;
		init();
	}

	private void init() {
		setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
		this.webView = new YxWebView(mContext, mCallBack);
		addView(this.webView);
	}

	public WebView getWebView() {
		return this.webView;
	}
}
