package com.yixin.test.util;

import android.content.Context;

/**
 * Created by xiaochangyou on 2017/10/24.
 */

public class DialogUtil {

    public static void showhint(Context context, String msg) {
        show(context, null, msg);
    }

    public static void showDialog(Context context, String title, String msg) {
        show(context, title, msg);
    }

    private static void show(Context context, String title, String msg) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("确定", null);
        builder.show();
    }
}
