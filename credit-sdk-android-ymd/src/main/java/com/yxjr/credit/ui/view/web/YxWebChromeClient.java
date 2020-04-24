package com.yxjr.credit.ui.view.web;

import com.yxjr.credit.ui.YxCallBack;
import com.yxjr.credit.ui.view.DialogLoading;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.File;

public class YxWebChromeClient extends WebChromeClient {

    private Context mContext;
    private YxCallBack mCallBack;
    private DialogLoading mDialogLoading;

    public YxWebChromeClient(Context context, YxCallBack callback) {
        this.mContext = context;
        this.mCallBack = callback;
        mDialogLoading = new DialogLoading(mContext);
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        if (newProgress < 100) {
            if (!mDialogLoading.isShowing()) {
                mDialogLoading.show();
            }
        }
        if (newProgress == 100) {
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
        super.onReceivedTitle(view, title);
    }

    @Override
    public void onRequestFocus(WebView view) {
        super.onRequestFocus(view);
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> callback, FileChooserParams fileChooserParams) {//5.0+

        showDialog();
        mValueCallbacks = callback;
        return true;
    }


    //openFileChooser 方法是隐藏方法
    public void openFileChooser(ValueCallback<Uri> callback, String acceptType, String capture) {// android 系统版本>4.1.1

        showDialog();
        mValueCallback = callback;

    }


    private static ValueCallback<Uri> mValueCallback;
    private static ValueCallback<Uri[]> mValueCallbacks;

    public static void getFilePath(String path) {
        Uri uri = Uri.fromFile(new File(path));
        if (Build.VERSION.SDK_INT > 18) {
            mValueCallbacks.onReceiveValue(new Uri[]{uri});
        } else {
            mValueCallback.onReceiveValue(uri);
        }
        if (null != mValueCallback)
            mValueCallback = null;
        if (null != mValueCallbacks)
            mValueCallbacks = null;
    }

    private void showDialog() {
        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setCancelable(false)
                .setItems(new String[]{"拍照", "相册选择", "文件选择"}, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            mCallBack.openCamera();
                        } else if (which == 1) {
                            mCallBack.openGallery();
                        } else if (which == 2) {
                            mCallBack.openFileManage();
                        } else {
                            dialog.dismiss();
                        }
                    }
                }).create();
        dialog.show();
    }
}
