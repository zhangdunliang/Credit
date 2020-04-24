package com.yxjr.credit.ui.view.web;

import com.yxjr.credit.constants.HttpConstant;
import com.yxjr.credit.constants.JsConstant;
import com.yxjr.credit.log.YxLog;
import com.yxjr.credit.ui.YxCallBack;
import com.yxjr.credit.util.ToastUtil;
import com.yxjr.credit.util.YxNetworkUtil;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class YxWebViewClient extends WebViewClient {

	private Context mContext;
	private YxCallBack mCallBack;
	private int YxSourceCode;

	public YxWebViewClient(Context context, YxCallBack callBack) {
		this.mContext = context;
		this.mCallBack = callBack;
	}

	public YxWebViewClient(Context context, YxCallBack callBack, int code) {
		this.mContext = context;
		this.mCallBack = callBack;
		this.YxSourceCode = code;
	}

	@Override
	public void onLoadResource(WebView view, String url) {
		YxLog.d("YxWebViewClient======onLoadResource：" + url);
		super.onLoadResource(view, url);
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		if (!YxNetworkUtil.isNetworkConnected(mContext)) {
			ToastUtil.showToast(mContext, "请检查网络设置!");
			view.loadUrl(YxWebView.loadErrorUrl);
			return super.shouldOverrideUrlLoading(view, url);
		} else {
			if (url != null) {
				if (url.startsWith("http:") || url.startsWith("https:")) {
					view.loadUrl(url);
					return super.shouldOverrideUrlLoading(view, url);
				} else {
					try {
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
						mContext.startActivity(intent);
						return true;
					} catch (Exception e) {
						ToastUtil.showToast(mContext, "未安装相关APP！");
						e.printStackTrace();
						return true;
					}
				}
			} else {
				return super.shouldOverrideUrlLoading(view, url);
			}
		}
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		YxLog.d("YxWebViewClient======onPageStarted：" + url);
		super.onPageStarted(view, url, favicon);
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		YxLog.d("YxWebViewClient======onPageFinished：" + url);
		if (YxSourceCode == 0) {
			// device = new JSONObject().put("deviceType", "android");
			mCallBack.loadUrl(JsConstant.INIT_PAGE_FINISH, null);
		} else if (YxSourceCode == 1) {
			mCallBack.loadCommonUrl(JsConstant.INIT_PAGE_FINISH, null);
		} else {
			mCallBack.loadQuestionUrl(JsConstant.INIT_PAGE_FINISH, null);
		}
		if (url.equals(HttpConstant.WEB_URL)) {
			mCallBack.checkAllPermission();
		}
		super.onPageFinished(view, url);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		YxLog.d("YxWebViewClient======onReceivedError：" + errorCode + description + failingUrl);
		ToastUtil.showToast(mContext, "加载错误!");
		view.loadUrl(YxWebView.loadErrorUrl);
		super.onReceivedError(view, errorCode, description, failingUrl);
	}

	// 重写此方法可以让webview处理https请求
	@Override
	public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
		// handler.cancel(); // Android默认的处理方式
		handler.proceed(); // 接受所有网站的证书
		// handleMessage(Message msg); // 进行其他处理
		super.onReceivedSslError(view, handler, error);
	}
}
