package com.yxjr.credit.ui.view.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import com.yxjr.credit.ui.YxCallBack;
import com.yxjr.credit.ui.view.ItemLongClickedPopWindow;
import com.yxjr.credit.ui.view.ResContainer;
import com.yxjr.credit.util.FileUtil;
import com.yxjr.credit.util.ToastUtil;
import com.yxjr.credit.util.YxDensityUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
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

	public YxWebView(Context context, YxCallBack callBack, int code) {
		super(context);
		this.mContext = context;
		this.mCallBack = callBack;
		this.YxSourceCode = code;
		init();
	}

	int downX, downY;

	@SuppressLint("ClickableViewAccessibility")
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
		setWebViewClient(new YxWebViewClient(mContext, mCallBack, YxSourceCode));
		setWebChromeClient(new YxWebChromeClient(mContext));

		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				downX = (int) event.getRawX();
				downY = (int) event.getRawY();
				return false;
			}
		});

		setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				HitTestResult result = ((YxWebView) v).getHitTestResult();
				if (null == result) {
					return false;
				} else {
					if (result.getType() == HitTestResult.IMAGE_TYPE) {// 处理长按图片的菜单项
						final ItemLongClickedPopWindow itemLongClickedPopWindow = new ItemLongClickedPopWindow(mContext, ItemLongClickedPopWindow.IMAGE_VIEW_POPUPWINDOW, YxDensityUtil.dipToPx(mContext, 100),
								YxDensityUtil.dipToPx(mContext, 40));
						final String img = result.getExtra();
						itemLongClickedPopWindow.showAtLocation(v, Gravity.TOP | Gravity.START, downX, downY);
						itemLongClickedPopWindow.getView(ResContainer.get(mContext).id("item_longclicked_saveImage")).setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								if (img.startsWith("http://") || img.startsWith("https://")) {
									new SaveImage(img).execute(); // Android 4.0以后要使用线程来访问网络
								} else if (img.contains("base64")) {
									int indexOf = img.indexOf("base64,");
									String imgBase64 = img.substring(indexOf + 7);
									Bitmap bitmap = FileUtil.saveBase64ToFile(imgBase64);
									String filePath = FileUtil.saveBitmap2Jpg(String.valueOf(new Date().getTime()), bitmap);
									ToastUtil.showToast(mContext, filePath == null ? "保存失败" : "图片已保存至:" + filePath);
								} else {
									ToastUtil.showToast(mContext, "保存失败");
								}
								itemLongClickedPopWindow.dismiss();
							}
						});
					}
					return true;
				}
			}
		});
	}

	private class SaveImage extends AsyncTask<String, Void, String> {
		private String mImgUrl;

		public SaveImage(String imgUrl) {
			mImgUrl = imgUrl;
		}

		@Override
		protected String doInBackground(String... params) {
			String result = "";
			try {
				File file = new File(FileUtil.PICTURES_PATH);
				if (!file.exists()) {
					file.mkdirs();
				}
				String type = mImgUrl.substring(mImgUrl.lastIndexOf("."));//图片类型
				file = new File(FileUtil.PICTURES_PATH + new Date().getTime() + type);
				InputStream inputStream = null;
				URL url = new URL(mImgUrl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(20000);
				if (conn.getResponseCode() == 200) {
					inputStream = conn.getInputStream();
				}
				byte[] buffer = new byte[4096];
				int len = 0;
				FileOutputStream outStream = new FileOutputStream(file);
				while ((len = inputStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}
				outStream.close();
				result = "图片已保存至：" + file.getAbsolutePath();
			} catch (Exception e) {
				result = "保存失败！" + e.getLocalizedMessage();
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			ToastUtil.showToast(mContext, result);
		}
	}
}