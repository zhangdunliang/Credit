package com.yxjr.credit.ui.view;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.yxjr.credit.constants.JsConstant;
import com.yxjr.credit.log.YxLog;
import com.yxjr.credit.plugin.YxJsPlugin;
import com.yxjr.credit.ui.YxCallBack;
import com.yxjr.credit.ui.view.web.YxWebChromeClient;
import com.yxjr.credit.ui.view.web.YxWebView;
import com.yxjr.credit.ui.view.web.YxWebViewClient;
import com.yxjr.credit.util.DialogUtil;
import com.yxjr.credit.util.StringUtil;
import com.yxjr.credit.util.ToastUtil;
import com.yxjr.credit.util.YxCommonUtil;
import com.yxjr.credit.util.YxDensityUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * All rights Reserved, Designed By ClareShaw
 *
 * @公司:益芯金融
 * @作者:xiaochangyou
 * @版本:V1.0
 * @创建时间:2017-1-4 上午10:27:03
 * @描述:TODO[合其信专用view]
 */
public class YxViewHqx extends LinearLayout {

    private YxWebView mWebView;
    private Context mContext;
    private YxCallBack mCallBack;
    private String mDialogTitle = null;
    private String mType = null;
    private ResContainer R;
    private final String UNIONPAY = "unionPay";

    public YxViewHqx(Context context, YxCallBack callBcak, String url, String htmlLabel, String type, String title) {
        super(context);
        // TODO Auto-generated constructor stub
        this.mContext = context;
        this.mCallBack = callBcak;
        if (title != null) {
            mDialogTitle = title;
        }
        if (type != null) {
            mType = type;
        }
        init(url, htmlLabel);
    }

    private OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //防止事件穿透，影响下面那层
            return true;
        }
    };

    @SuppressLint("SetJavaScriptEnabled")
    private void init(String url, String htmlLabel) {
        this.setOnTouchListener(onTouchListener);
        R = ResContainer.get(mContext);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        setOrientation(LinearLayout.VERTICAL);

        RelativeLayout topLayout = new RelativeLayout(mContext);
        topLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, YxDensityUtil.dipToPx(mContext, 50)));
        topLayout.setBackgroundColor(Color.WHITE);

        ImageView closeView = new ImageView(mContext);
        closeView.setImageResource(R.drawable("yx_credit_back"));
        RelativeLayout.LayoutParams closeParam = new RelativeLayout.LayoutParams(YxDensityUtil.dipToPx(mContext, 25), YxDensityUtil.dipToPx(mContext, 25));
        closeParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        closeParam.addRule(RelativeLayout.CENTER_VERTICAL);
        closeParam.setMargins(YxDensityUtil.dipToPx(mContext, 15), 0, 0, 0);
        topLayout.addView(closeView, closeParam);
        closeView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showHintDialog();
            }
        });

        final TextView titleView = new TextView(mContext);
        titleView.setTextSize(YxDensityUtil.dipToPx(mContext, 6));
        RelativeLayout.LayoutParams titleParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        titleParam.addRule(RelativeLayout.CENTER_IN_PARENT);
        topLayout.addView(titleView, titleParam);

        addView(topLayout);

        mWebView = new YxWebView(mContext, mCallBack, 1);

        mWebView.addJavascriptInterface(this, YxJsPlugin.jsInterfaceName);
        mWebView.setWebChromeClient(new YxWebChromeClient(mContext,mCallBack) {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (null != title) {
                    titleView.setText(title);
                }
            }
        });
        mWebView.setWebViewClient(new YxWebViewClient(mContext, mCallBack, 1) {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
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

            @Override
            public void onPageFinished(WebView view, String url) {
                //--------------------------------------------------------
                if (mType != null) {
                    if (mType.equals(UNIONPAY)) {//仅在银联认证的时候插入js获取验证码
                        if (url.contains("mcashier") && url.contains("verify.action") && url.contains("result")) {
                            startTimer();
                        }
                    }
                }

                //--------------------------------------------------------
                super.onPageFinished(view, url);
            }
        });
        if (url != null) {
            mWebView.loadUrl(url);
        } else if (htmlLabel != null) {
            mWebView.loadDataWithBaseURL(null, htmlLabel, "text/html", "utf-8", null);
        }
        addView(mWebView);
    }

    private Timer mTimer = null;
    private TimerTask mTimerTask = null;
    int maxCount = 0;
    private boolean isPause = true;

    private void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    do {
                        if (maxCount >= 10) {
                            stopTimer();
                            return;
                        }
                        mHandler.sendEmptyMessage(0);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                        maxCount++;
                    } while (isPause);
                }
            };
            if (mTimer != null && mTimerTask != null) {
                mTimer.schedule(mTimerTask, 2000, 2000);
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            if (msg.what == 0) {
                if (isPause) {
                    String verifycode_jsload = "javascript:window.android.verifycode(document.getElementById('pay_success').getElementsByClassName('money')[0].innerHTML)";
                    mWebView.loadUrl(verifycode_jsload);
                }
            }
        }

        ;
    };

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (isPause) {
            isPause = false;
        }
    }

    public void showHintDialog() {
        if (mDialogTitle != null) {
            new DialogUtil(mContext).showDialog(mDialogTitle, "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, "确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mCallBack.removeHqx();
                }
            });
        } else {
            mCallBack.removeHqx();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        stopTimer();
        this.removeView(mWebView);// webview调用destory时,webview仍绑定在Activity上,需先从父容器中移除webview,然后再销毁webview:
        mWebView.stopLoading();
        mWebView.removeAllViews();
        mWebView.setTag(null);
        mWebView.clearHistory();
        mWebView.destroy();
        mWebView = null;
        super.onDetachedFromWindow();
    }

    @JavascriptInterface
    public final void verifycode(String code) {
        if (null != code && code.length() > 0) {
            stopTimer();
            //拿到code
            //			ToastUtil.showToast(mContext, code + maxCount);
            if (code != null && !StringUtil.isEmpty(code)) {
                if (mType.equals(UNIONPAY)) {
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("uCode", code);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mCallBack.loadUrl(JsConstant.J265, obj.toString());
                    mCallBack.removeHqx();
                }
            }
        }
    }

    @JavascriptInterface
    public final void appBridgeService(String param) {
        // synchronized (param) {
        String code = null;
        // String serviceId = null;
        // String key = null;
        JSONObject data = null;
        try {
            JSONObject person = new JSONObject(param);
            code = person.getString("code");
            // serviceId = person.getString("serviceId");
            data = person.getJSONObject("data");
            // key = person.getString("key");
        } catch (JSONException e) {
            YxLog.e("Exception:Parsing js outer json error!" + e);
            e.printStackTrace();
        }
        if (null != code) {
            if (code.equals(JsConstant.J258)) {
                mCallBack.loadUrl(JsConstant.J258, data.toString());
                mCallBack.removeHqx();
            }
            if (code.equals(JsConstant.GET_BACK_ID)) {
                mCallBack.removeHqx();
            }
            if (code.equals(JsConstant.J267)) {
                String pkgName = null;
                try {
                    pkgName = data.getString("path");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (pkgName != null) {
                    String isIntall;
                    try {
                        PackageManager packageManager = mContext.getPackageManager();
                        packageManager.getPackageInfo(pkgName, PackageManager.GET_UNINSTALLED_PACKAGES);
                        isIntall = "1";// 1表示能跳转
                    } catch (Exception e) {
                        isIntall = "0";// 0表示未安装不能跳转
                        e.printStackTrace();
                    }
                    try {
                        JSONObject obj = new JSONObject().put("canJump", null == isIntall ? 0 : isIntall);
                        loadUrl(mContext,JsConstant.J267, obj.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void loadUrl(final Context context, final String code, final String data) {
        if (!YxCommonUtil.isNotBlank(code)) {
            YxLog.e("Exception：request of JS Code is null ! ! !");
            return;
        }
        mWebView.post(new Runnable() {// 消息队列，用来存放Message对象的数据结构，按照“先进先出”的原则存放消息
            @Override
            public void run() {
                mWebView.loadUrl("javascript:webViewBridgeService('" + new YxJsPlugin(mContext).formatParam(code, data) + "')");
            }
        });
    }

}
