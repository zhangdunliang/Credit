package com.yxjr.credit.grab;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yxjr.credit.constants.HttpConstant;
import com.yxjr.credit.constants.SpConstant;
import com.yxjr.credit.http.manage.NoConfineAsyncTask;
import com.yxjr.credit.log.YxLog;
import com.yxjr.credit.util.YxStoreUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class ApplistGrab extends Grab {

    private Context mContext;

    public ApplistGrab(Context context) {
        this.mContext = context;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public JSONArray getData() {
        JSONArray appList = null;
        try {//SM-A8000 app.publicSourceDir为null，暂无设备，try carch掉整个逻辑避免闪退
            PackageManager pm = mContext.getPackageManager();
            // 查询所有已经安装的应用程序
            List<ApplicationInfo> listAppcations = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
            Collections.sort(listAppcations, new ApplicationInfo.DisplayNameComparator(pm));// 排序
            appList = new JSONArray();
            for (ApplicationInfo app : listAppcations) {
                if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {//第三方APP
                    CharSequence appName = app.loadLabel(pm);//名字
                    String appPkgName = app.packageName;//包名
                    //SM-A8000 app.publicSourceDir为null，暂无设备，try carch掉整个逻辑避免闪退
                    String installTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new File(app.publicSourceDir).lastModified());//安装时间
                    JSONObject apps = new JSONObject();
                    apps.put("appName", subStringByByte(appName.toString(), 60));
                    apps.put("appPkgName", subStringByByte(appPkgName, 60));
                    apps.put("installTime", subStringByByte(installTime, 20));
                    appList.put(apps);
                }
            }
        } catch (Exception e) {
            YxLog.e("getAppInfo exception...." + e);
            e.printStackTrace();
        }
        return appList;
    }

    @Override
    public void upload() {
        new Task().execute();
    }

    private class Task extends NoConfineAsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                int interval = getInterval(mContext, SpConstant.ASQ);
                boolean surpass = isSurpass(mContext, SpConstant.A_IS);
                if (interval != 0 && surpass) {
                    JSONArray appArray = getData();
                    if (null != appArray) {
                        final String idCard = YxStoreUtil.get(mContext, SpConstant.PARTNER_ID_CARD_NUM);
                        final String phoneNo = YxStoreUtil.get(mContext, SpConstant.PARTNER_PHONENUMBER);
                        batchJSONArray(appArray, interval, new OnBatchListener() {

                            @Override
                            public void onBatch(JSONArray array) {
                                try {
                                    JSONObject appInfo = new JSONObject();
                                    appInfo.put("cert", idCard);
                                    appInfo.put("mobileNo", phoneNo);
                                    appInfo.put("appListInfo", array);
                                    sendServer(mContext, HttpConstant.Request.SEND_APP_LIST, appInfo);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }
    }
}
