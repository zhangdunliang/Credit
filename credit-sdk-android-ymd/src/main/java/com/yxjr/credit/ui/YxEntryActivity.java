package com.yxjr.credit.ui;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.yxjr.credit.constants.HttpConstant;
import com.yxjr.credit.constants.JsConstant;
import com.yxjr.credit.constants.SpConstant;
import com.yxjr.credit.constants.YxCommonConstant;
import com.yxjr.credit.log.YxLog;
import com.yxjr.credit.plugin.YxJsPlugin;
import com.yxjr.credit.plugin.YxMoxie;
import com.yxjr.credit.service.YxService;
import com.yxjr.credit.ui.presenter.PEntryActivity;
import com.yxjr.credit.ui.view.YxViewAddAssetCar;
import com.yxjr.credit.ui.view.YxViewAddAssetHouse;
import com.yxjr.credit.ui.view.YxViewAutonymCertify;
import com.yxjr.credit.ui.view.YxViewHqx;
import com.yxjr.credit.ui.view.web.YxWebChromeClient;
import com.yxjr.credit.ui.view.web.YxWebView;
import com.yxjr.credit.ui.view.web.YxWebViewClient;
import com.yxjr.credit.ui.view.web.YxWebViewLayout;
import com.yxjr.credit.util.MediaUtility;
import com.yxjr.credit.util.PermissionUtil;
import com.yxjr.credit.util.YxCommonUtil;
import com.yxjr.credit.util.YxPerDialog;
import com.yxjr.credit.util.YxStoreUtil;

/**
 * All rights Reserved, Designed By ClareShaw
 *
 * @公司:益芯金融
 * @作者:xiaochangyou
 * @版本:V1.0
 * @创建时间:2016-7-15 下午5:13:41
 * @描述:TODO[SDK主要的唯一的Activity]
 */
public class YxEntryActivity extends YxBaseActivity implements YxCallBack {
    private YxJsPlugin mJsPlugin;
    private WebView mExampleWebView = null;
    private YxWebView mWebView = null;
    private YxWebView mQuestionWebView = null;
    private YxWebViewLayout mMainLayout;
    private YxViewAutonymCertify mAutonymCertifyLayout = null;
    private YxViewAddAssetHouse mAddAssetHouseLayout = null;
    private YxViewAddAssetCar mAddAssetCarLayout = null;
    private YxViewHqx mHqxView = null;

    private LocationManager mLocationManager;
    // private String mProvider = null;// 位置提供器
    private YxHandler mHandler = YxHandler.getInstance(this, this, Looper.getMainLooper());
    private SensorManager mSensorManager = null;// 管理器对象

    private int orientation_i = 0;
    private int accelerometer_i = 0;
    private int gravity_i = 0;
    private int gyroscopes_i = 0;
    private int mSensorInterval = 10;
    private DecimalFormat mDecimalFormat = new DecimalFormat("0.000000");// 构造方法的字符格式这里如果小数不足2位,会以0补足.
    private BatteryReceiver mBatteryReceiver = null;
    private Dialog mDialog = null;

    private PEntryActivity mYxPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        mYxPresenter = new PEntryActivity(this, mHandler, this);
        if (PermissionUtil.isStoragePer(this).equals(PermissionUtil.UNAUTHORIZED)) {
            this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
            new YxPerDialog(this).showPer("存储");
        } else {
            mMainLayout = new YxWebViewLayout(this, this);
            setContentView(mMainLayout);
            init();
        }
    }

    /**
     * @作者:xiaochangyou
     * @创建时间:2016-7-15 下午5:13:57
     * @描述:TODO[初始化]
     */
    @SuppressLint("DefaultLocale")
    private void init() {
        Bundle bundle = getIntent().getExtras();
        YxCommonUtil.processParam(this, bundle);

        // 初始化传感器相关操作
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mYxPresenter.initSensor(mSensorManager, mSensorEventListener);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);// 获得LocationManager的实例
        Location location = mYxPresenter.initLocation(mLocationManager, mLocationListener);
        updateWithNewLocation(location);

        // 注册电池电量广播
        mBatteryReceiver = new BatteryReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBatteryReceiver, filter);// 注册BroadcastReceiver
        //
        mYxPresenter.initServer();
    }

    /**
     * 传感器Listener
     */
    private SensorEventListener mSensorEventListener = new SensorEventListener() {

        @SuppressWarnings("deprecation")
        @Override
        public void onSensorChanged(SensorEvent event) {
            // TODO Auto-generated method stub
            if (event.sensor == null) {
                return;
            }
            if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                if (orientation_i == 0) {
                    float[] valuse = event.values;
                    YxStoreUtil.save(YxEntryActivity.this, SpConstant.SENSOR_ORIENTATION, mDecimalFormat.format(valuse[0]) + "|" + mDecimalFormat.format(valuse[1]) + "|" + mDecimalFormat.format(valuse[2]));
                }
                orientation_i++;
                if (orientation_i == mSensorInterval) {
                    orientation_i = 0;
                }
            }
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                if (accelerometer_i == 0) {
                    float[] valuse = event.values;
                    YxStoreUtil.save(YxEntryActivity.this, SpConstant.SENSOR_ACCELEROMETER, mDecimalFormat.format(valuse[0]) + "|" + mDecimalFormat.format(valuse[1]) + "|" + mDecimalFormat.format(valuse[2]));
                }
                accelerometer_i++;
                if (accelerometer_i == mSensorInterval) {
                    accelerometer_i = 0;
                }
            }
            if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
                if (gravity_i == 0) {
                    float[] valuse = event.values;
                    YxStoreUtil.save(YxEntryActivity.this, SpConstant.SENSOR_GRAVITY, mDecimalFormat.format(valuse[0]) + "|" + mDecimalFormat.format(valuse[1]) + "|" + mDecimalFormat.format(valuse[2]));
                }
                gravity_i++;
                if (gravity_i == mSensorInterval) {
                    gravity_i = 0;
                }
            }
            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                if (gyroscopes_i == 0) {
                    float[] valuse = event.values;
                    YxStoreUtil.save(YxEntryActivity.this, SpConstant.SENSOR_GYROSCOPES, mDecimalFormat.format(valuse[0]) + "|" + mDecimalFormat.format(valuse[1]) + "|" + mDecimalFormat.format(valuse[2]));
                }
                gyroscopes_i++;
                if (gyroscopes_i == mSensorInterval) {
                    gyroscopes_i = 0;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub
        }
    };

    // /**
    // * @作者:xiaochangyou
    // * @创建时间:2016-7-15 下午5:15:03
    // * @描述:TODO[初始化webView设置]
    // */
    // private void initWebView(String UPGRADE) {
    //
    // }

    @Override
    public void initWebView(String UPGRADE) {
        // TODO Auto-generated method stub
        mWebView = ((YxWebView) mMainLayout.getWebView());
        mJsPlugin = new YxJsPlugin(this);
        mJsPlugin.setCallBack(this);
        mWebView.addJavascriptInterface(mJsPlugin, YxJsPlugin.jsInterfaceName);
        loadWeb(UPGRADE);
    }

    private void loadWeb(String UPGRADE) {
        if (YxCommonUtil.isNotBlank(UPGRADE) && UPGRADE.equals("Y")) {
            mWebView.loadUrl(HttpConstant.SYS_UPDATE_URL);
        } else {
            mWebView.loadUrl(HttpConstant.WEB_URL);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == YxCommonConstant.ActivityCode.RequestCode.OPEN_SYS_CRMERA) {
            if (null == mPicPath)
                YxWebChromeClient.getFilePath("");
            else
                YxWebChromeClient.getFilePath(mPicPath);
        }

        if (requestCode == YxCommonConstant.ActivityCode.RequestCode.OPEN_SYS_GALLERY) {
            if (null == data) {
                YxWebChromeClient.getFilePath("");
            } else {
                String path = MediaUtility.getPath(this, data.getData());
                YxWebChromeClient.getFilePath(path);
            }
        }

        if (requestCode == YxCommonConstant.ActivityCode.RequestCode.OPEN_SYS_FILE) {
            if (null == data) {
                YxWebChromeClient.getFilePath("");
            } else {
                String path = MediaUtility.getPath(this, data.getData());
                YxWebChromeClient.getFilePath(path);
            }
        }

        mYxPresenter.activityResult(requestCode, resultCode, data, mAutonymCertifyLayout, mAddAssetHouseLayout, mAddAssetCarLayout);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String mPicPath;

    @Override
    public void openCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File file = new File(Environment.getExternalStorageDirectory(), String.valueOf(System.currentTimeMillis()) + ".jpg");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        } else {
            if (file.exists()) {
                file.delete();
            }
        }

        mPicPath = file.getPath();

        Uri cameraUri;
        if (Build.VERSION.SDK_INT >= 24) { //判读版本是否在7.0以上
            cameraUri =
                    FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
        } else {
            cameraUri = Uri.fromFile(file);
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(intent, YxCommonConstant.ActivityCode.RequestCode.OPEN_SYS_CRMERA);
    }

    @Override
    public void openGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, YxCommonConstant.ActivityCode.RequestCode.OPEN_SYS_GALLERY);
    }

    @Override
    public void openFileManage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, YxCommonConstant.ActivityCode.RequestCode.OPEN_SYS_FILE);
    }

    @Override
    protected void onResume() {
        if (!checkNetwork()) {
            // finish();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // ACache.get(this).clear();//清空缓存
        if (mWebView != null) {// 清空WebView
            mMainLayout.removeView(mWebView);// webview调用destory时,webview仍绑定在Activity上,需先从父容器中移除webview,然后再销毁webview:
            mWebView.stopLoading();
            mWebView.removeAllViews();
            mWebView.setTag(null);
            mWebView.clearHistory();
            mWebView.destroy();
            mWebView = null;
        }
        if (mExampleWebView != null) {
            ((ViewGroup) mExampleWebView.getParent()).removeView(mExampleWebView);
            mExampleWebView.stopLoading();
            mExampleWebView.removeAllViews();
            mExampleWebView.setTag(null);
            mExampleWebView.clearHistory();
            mExampleWebView.destroy();
            mExampleWebView = null;
        }
        if (mQuestionWebView != null) {
            ((ViewGroup) mQuestionWebView.getParent()).removeView(mQuestionWebView);
            mQuestionWebView.stopLoading();
            mQuestionWebView.removeAllViews();
            mQuestionWebView.setTag(null);
            mQuestionWebView.clearHistory();
            mQuestionWebView.destroy();
            mQuestionWebView = null;
        }
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }

        stopService(new Intent(this, YxService.class));// 停止心跳
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(mSensorEventListener);// 解除传感器注册
        }
        if (mBatteryReceiver != null) {
            unregisterReceiver(mBatteryReceiver);// 解除电量广播
        }
    }

    /**
     * @see android.app.Activity#onBackPressed() TODO[单击系统返回键网页回退]
     */
    @Override
    public void onBackPressed() {
        if (mAutonymCertifyLayout != null) {// 实名验证
            if (mExampleWebView != null) {
                this.removeExample();
            } else if (mQuestionWebView != null) {
                this.removeQuestion();
            } else {
                mMainLayout.removeView(mAutonymCertifyLayout);
                mAutonymCertifyLayout = null;
            }
        } else if (mAddAssetHouseLayout != null) {// 房产验证
            mMainLayout.removeView(mAddAssetHouseLayout);
            mAddAssetHouseLayout = null;
        } else if (mAddAssetCarLayout != null) {// 车产验证
            mMainLayout.removeView(mAddAssetCarLayout);
            mAddAssetCarLayout = null;
        } else if (mHqxView != null) {// 合其信
            mHqxView.showHintDialog();
        } else if (mWebView == null) {
            this.finish();
        } else if (mWebView.getUrl().contains("http://www-1.fuiou.com:18670/mobile_pay/") || mWebView.getUrl().contains("https://mpay.fuiou.com:16128/")) {
            this.loadJsFunction("goback");// 富友支付页
        } else {// H5页面内
            this.loadJsFunction("backHistory");
        }
    }

    @Override
    public void loadJsFunction(final String function) {
        mWebView.post(new Runnable() {

            @Override
            public void run() {
                mWebView.loadUrl("javascript:" + function + "()");
            }
        });
        YxLog.d("======functionName：" + function);
    }

    @Override
    public void loadUrl(final String code, final String data) {
        if (code.equals(JsConstant.INIT_PAGE_FINISH)) {
            YxLog.d(code + "=========================================================" + data);
        }
        if (!YxCommonUtil.isNotBlank(code)) {
            YxLog.e("Exception：request of JS Code is null ! ! !");
            return;
        }
        mWebView.post(new Runnable() {// 消息队列，用来存放Message对象的数据结构，按照“先进先出”的原则存放消息

            @Override
            public void run() {
                mWebView.loadUrl("javascript:webViewBridgeService('" + mJsPlugin.formatParam(code, data) + "')");

            }
        });
    }

    @Override
    public void addAutonymCertify(String certId, String categoryCode) {
        mAutonymCertifyLayout = new YxViewAutonymCertify(this, this, certId, categoryCode);
        mMainLayout.post(new Runnable() {
            @Override
            public void run() {
                mMainLayout.addView(mAutonymCertifyLayout);
            }
        });
    }

    @Override
    public void removeAutonymCertify() {
        if (mAutonymCertifyLayout != null) {
            mMainLayout.removeView(mAutonymCertifyLayout);
            mAutonymCertifyLayout = null;
        }
    }

    @Override
    public void addAssetCar(String certId, String categoryCode) {
        mAddAssetCarLayout = new YxViewAddAssetCar(this, this, certId, categoryCode);
        mMainLayout.post(new Runnable() {
            @Override
            public void run() {
                mMainLayout.addView(mAddAssetCarLayout);
            }
        });
    }

    @Override
    public void removeAssetCar() {
        if (mAddAssetCarLayout != null) {
            mMainLayout.removeView(mAddAssetCarLayout);
        }
    }

    @Override
    public void addAssetHouse(String certId, String categoryCode) {
        mAddAssetHouseLayout = new YxViewAddAssetHouse(this, this, certId, categoryCode);
        mMainLayout.post(new Runnable() {
            @Override
            public void run() {
                mMainLayout.addView(mAddAssetHouseLayout);
            }
        });
    }

    @Override
    public void removeAssetHouse() {
        if (mAddAssetHouseLayout != null) {
            mMainLayout.removeView(mAddAssetHouseLayout);
        }
    }

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    public void addExample() {
        mExampleWebView = new WebView(this);
        mExampleWebView.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        WebSettings settings = mExampleWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 不使用缓存||LOAD_CACHE_ELSE_NETWORK优先使用缓存
        settings.setAllowFileAccess(true); // 设置可以访问文件
        settings.setLoadsImagesAutomatically(true); // 支持自动加载图片
        settings.setSaveFormData(false);
        settings.setSupportZoom(false); // 支持缩放
        mExampleWebView.setVerticalScrollBarEnabled(false);
        mExampleWebView.setHorizontalScrollBarEnabled(false);
        mExampleWebView.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                return true;
            }
        });
        mExampleWebView.setWebViewClient(new YxWebViewClient(this, this));
        mExampleWebView.setWebChromeClient(new YxWebChromeClient(this, this));
        mExampleWebView.addJavascriptInterface(mJsPlugin, YxJsPlugin.jsInterfaceName);
        mExampleWebView.loadUrl(HttpConstant.EXAMPLE_URL);
        mMainLayout.post(new Runnable() {
            @Override
            public void run() {
                mMainLayout.addView(mExampleWebView);
            }
        });
    }

    @Override
    public void removeExample() {
        if (mExampleWebView != null) {
            mMainLayout.post(new Runnable() {
                @Override
                public void run() {
                    mMainLayout.removeView(mExampleWebView);
                    mExampleWebView.destroy();
                    mExampleWebView = null;
                }
            });
        }
    }

    @Override
    public void addHqx(final String url, final String htmlLabel, final String type, final String title) {
        mMainLayout.post(new Runnable() {
            @Override
            public void run() {
                mHqxView = new YxViewHqx(YxEntryActivity.this, YxEntryActivity.this, url, htmlLabel, type, title);
                mMainLayout.addView(mHqxView);
            }
        });
    }

    @Override
    public void removeHqx() {
        if (mHqxView != null) {
            mMainLayout.post(new Runnable() {
                @Override
                public void run() {
                    mMainLayout.removeView(mHqxView);
                    mHqxView = null;
                }
            });
        }
    }

    @Override
    public void loadCommonUrl(String code, String data) {
        if (mHqxView != null) {
            mHqxView.loadUrl(YxEntryActivity.this, code, data);
        }
    }

    @Override
    public void showDialog(CharSequence message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton("确认", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        mDialog = builder.create();
        mDialog.show();
    }

    @Override
    public void swipingCardPay(String packName, String className, String data) {
        Intent intent = new Intent();
        intent.setClassName(this, packName + "." + className);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("entranceInfo", data);
        startActivityForResult(intent, YxCommonConstant.ActivityCode.RequestCode.SWIPING_CARD_PAY);
    }

    @Override
    public void loadMoxie(JSONObject data) {
        String taskType = null;
        String certId = null;
        String loginCustom = null;
        try {
            taskType = data.getString("taskType");//魔蝎code码，用于进入不同魔蝎认证
            certId = data.getString("certId");//申请件编号
            loginCustom = data.getString("loginCustom");//自定义登录，由js控制，假如有值，赋值到魔蝎所需参数loginCustom中去
        } catch (JSONException e) {
            YxLog.e("Exception:Error parsing js json!" + e);
            e.printStackTrace();
        }
        YxMoxie moxie = new YxMoxie(YxEntryActivity.this);
        moxie.setMxParamType(taskType);
        moxie.setUserId(certId);
        moxie.setLoginCustom(loginCustom);
        moxie.start(YxEntryActivity.this);
    }

    @Override
    public void returnMoxie(Object obj) {
        this.loadUrl(JsConstant.J266, obj.toString());
    }

    @Override
    public void exit() {
        this.finish();
    }

    @Override
    public void reloadWebView() {
        mWebView.post(new Runnable() {

            @Override
            public void run() {
                loadWeb(YxStoreUtil.get(YxEntryActivity.this, SpConstant.UPGRADE));
            }
        });
    }

    /**
     * @param location
     * @作者:xiaochangyou
     * @创建时间:2016-7-15 下午5:20:49
     * @描述:TODO[位置*更新location信息]
     */
    private void updateWithNewLocation(Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            BigDecimal bdLatitude = new BigDecimal(latitude);
            BigDecimal bdlongitude = new BigDecimal(longitude);
            YxStoreUtil.save(this, SpConstant.LATITUDE, bdLatitude.setScale(6, BigDecimal.ROUND_HALF_UP) + "");// 存储当前的经度,并保留小数点后六位
            YxStoreUtil.save(this, SpConstant.LONGITUDE, bdlongitude.setScale(6, BigDecimal.ROUND_HALF_UP) + "");// 存储当前的纬度,并保留小数点后六位
        } else {
            YxLog.e("Exception：location is null ! ! !");
        }

    }

    /**
     * 位置*位置更新监听
     */
    LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            YxLog.d("======onStatusChanged:provider:" + provider + ",status:" + status);
        }

        @Override
        public void onProviderEnabled(String provider) {
            YxLog.d("======onProviderEnabled:" + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            YxLog.d("======onProviderDisabled:" + provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            // 设备位置发生改变时，执行这里的代码
            YxLog.d("======位置location:" + location.toString());
            YxLog.d("======位置变化 经:" + location.getLatitude() + " 纬：" + location.getLongitude());
            updateWithNewLocation(location);
        }
    };

    @Override
    public void addQuestion() {
        mQuestionWebView = new YxWebView(this, this, 1);
        mJsPlugin = new YxJsPlugin(this, 1);
        mJsPlugin.setCallBack(this);
        mQuestionWebView.addJavascriptInterface(mJsPlugin, YxJsPlugin.jsInterfaceName);
        mQuestionWebView.loadUrl(HttpConstant.USUAL_QUESTION_URL);
        mMainLayout.post(new Runnable() {
            @Override
            public void run() {
                mMainLayout.addView(mQuestionWebView);
            }
        });

    }

    @Override
    public void removeQuestion() {
        if (mQuestionWebView != null) {
            mMainLayout.post(new Runnable() {
                @Override
                public void run() {
                    mMainLayout.removeView(mQuestionWebView);
                    mQuestionWebView.destroy();
                    mQuestionWebView = null;
                }
            });
        }

    }

    @Override
    public void loadQuestionUrl(final String code, final String data) {
        if (!YxCommonUtil.isNotBlank(code)) {
            YxLog.e("Exception：request of JS Code is null ! ! !");
            return;
        }
        mQuestionWebView.post(new Runnable() {// 消息队列，用来存放Message对象的数据结构，按照“先进先出”的原则存放消息
            @Override
            public void run() {
                mQuestionWebView.loadUrl("javascript:webViewBridgeService('" + mJsPlugin.formatParam(code, data) + "')");
            }
        });
    }

    @Override
    public void goContacts() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        // intent.setData(Contacts.People.CONTENT_URI);
        intent.setData(Uri.parse("content://contacts/people"));
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent, YxCommonConstant.ActivityCode.RequestCode.CONTACTS_REQUEST_CODE);
    }

    @Override
    public void checkAllPermission() {
        String info = "";
        if (PermissionUtil.isGpsService(this).equals(PermissionUtil.UNAUTHORIZED)) {
            info = "手机定位服务\n";
        }
        if (PermissionUtil.isLocPer(this).equals(PermissionUtil.UNAUTHORIZED)) {
            info += "应用GPS定位\n";
        }
        if (PermissionUtil.isContactsPer(this).equals(PermissionUtil.UNAUTHORIZED)) {
            info += "手机通讯录\n";
        }
        if (PermissionUtil.isSmsPer(this).equals(PermissionUtil.UNAUTHORIZED)) {
            info += "手机短信\n";
        }
        if (PermissionUtil.isCallLogPer(this).equals(PermissionUtil.UNAUTHORIZED)) {
            info += "通话记录\n";
        }
        if (PermissionUtil.isAppListPer(this).equals(PermissionUtil.UNAUTHORIZED)) {
            info += "应用程序列表\n";
        }
        if (PermissionUtil.isPhoneInfoPer(this).equals(PermissionUtil.UNAUTHORIZED)) {
            info += "手机信息（识别码、IMEI）";
        }
        if (YxCommonUtil.isNotBlank(info)) {
            new YxPerDialog(this).showAllPer(info);
        }
    }

    /**
     * All rights Reserved, Designed By ClareShaw
     *
     * @公司:益芯金融
     * @作者:xiaochangyou
     * @版本:V1.0
     * @创建时间:2017-2-20 下午3:36:26
     * @描述:TODO[电池电量广播]
     */
    private class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int current = intent.getExtras().getInt("level");// 获得当前电量
            int total = intent.getExtras().getInt("scale");// 获得总电量
            int percent = current * 100 / total;
            YxStoreUtil.save(YxEntryActivity.this, SpConstant.BATTERY, percent + "");
            YxLog.d("现在的电量是===" + percent + "%。");
        }
    }
}
