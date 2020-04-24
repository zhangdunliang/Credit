package com.yxjr.credit.ui.view.web;

import com.yxjr.credit.ui.YxCallBack;

import android.annotation.SuppressLint;
import android.content.Context;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

@SuppressLint("SetJavaScriptEnabled")
public class YxWebView extends WebView {
	private Context mContext;
	private YxCallBack mCallBack;
	public static final String loadErrorUrl = "file:///android_asset/yxjr_credit_load_error.html";
	private int YxSourceCode;
	public YxWebView(Context context, YxCallBack callBack) {
		super(context);
		this.mContext = context;
		this.mCallBack = callBack;
		init();
	}
	public YxWebView(Context context, YxCallBack callBack,int code) {
		super(context);
		this.mContext = context;
		this.mCallBack = callBack;
		this.YxSourceCode=code;
		init();
	}

	private void init() {
		setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
		WebSettings settings = getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 不使用缓存||LOAD_CACHE_ELSE_NETWORK优先使用缓存
		settings.setAllowFileAccess(true); // 设置可以访问文件
		settings.setLoadsImagesAutomatically(true); // 支持自动加载图片
		settings.setSaveFormData(false);
		settings.setSupportZoom(false); // 不支持缩放
		settings.setDomStorageEnabled(true);//配合前端使用Dom Storage（Web Storage）存储机制
		setHorizontalScrollBarEnabled(false);// 水平不显示
		setVerticalScrollBarEnabled(false); // 垂直不显示
		// setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);// 滚动条在WebView内侧显示
		// setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);// 滚动条在WebView外侧显示
		setWebViewClient(new YxWebViewClient(mContext, mCallBack,YxSourceCode));
		setWebChromeClient(new YxWebChromeClient(mContext,mCallBack));
	}
}
