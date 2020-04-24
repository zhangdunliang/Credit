package com.yxjr.credit.plugin;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.webkit.JavascriptInterface;

import cn.tongdun.android.shell.FMAgent;
import cn.tongdun.android.shell.exception.FMException;

import org.json.JSONException;
import org.json.JSONObject;

import com.yxjr.credit.constants.HttpConstant;
import com.yxjr.credit.constants.JsConstant;
import com.yxjr.credit.constants.SpConstant;
import com.yxjr.credit.constants.YxCommonConstant;
import com.yxjr.credit.grab.ContactsGrab;
import com.yxjr.credit.http.manage.RequestEngine;
import com.yxjr.credit.http.manage.RequestCallBack;
import com.yxjr.credit.log.YxLog;
import com.yxjr.credit.ui.LivenessActivity;
import com.yxjr.credit.ui.ScanIDCardActivity;
import com.yxjr.credit.ui.YxCallBack;
import com.yxjr.credit.ui.view.DialogLoading;
import com.yxjr.credit.util.DialogUtil;
import com.yxjr.credit.util.PermissionUtil;
import com.yxjr.credit.util.StringUtil;
import com.yxjr.credit.util.ToastUtil;
import com.yxjr.credit.util.YxCommonUtil;
import com.yxjr.credit.util.YxStoreUtil;

/**
 * All rights Reserved, Designed By ClareShaw
 *
 * @公司:益芯金融
 * @作者:xiaochangyou
 * @版本:V1.0
 * @创建时间:2016-7-18 下午1:58:02
 * @描述:TODO[与JS交互的关键类]
 */
public final class YxJsPlugin {
    public final static String jsInterfaceName = "android";
    private Context mContext;
    private YxCallBack mCallBcak;
    private int YxSourceCode;

    public YxJsPlugin(Context context) {
        this.mContext = context;
    }

    public YxJsPlugin(Context context, int Code) {
        this.mContext = context;
        this.YxSourceCode = Code;
    }

    public void setCallBack(YxCallBack callBcak) {
        this.mCallBcak = callBcak;
    }

    /**
     * @param param JS带过来的数据
     * @作者:xiaochangyou
     * @创建时间:2016-7-18 下午1:58:50
     * @描述:TODO[JS请求android的唯一入口]
     */
    @JavascriptInterface
    public final void appBridgeService(String param) {
        YxLog.d("======[appBridgeService] synchronized" + param);
        // synchronized (param) {
        String code = null;
        String serviceId = null;
        // String key = null;
        JSONObject data = null;
        String partnerId = YxStoreUtil.get(mContext, SpConstant.PARTNER_ID);
        String realName = YxStoreUtil.get(mContext, SpConstant.PARTNER_REAL_NAME);
        String idCardNum = YxStoreUtil.get(mContext, SpConstant.PARTNER_ID_CARD_NUM);
        String phoneNumber = YxStoreUtil.get(mContext, SpConstant.PARTNER_PHONENUMBER);
        YxLog.d("======[appBridgeService] " + param);
        try {
            JSONObject person = new JSONObject(param);
            code = person.getString("code");
            serviceId = person.getString("serviceId");
            data = person.getJSONObject("data");
            // key = person.getString("key");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            YxLog.e("Exception:Parsing js outer json error!" + e);
            e.printStackTrace();
        }
        if (null != code) {
            switch (code) {
                case JsConstant.J266:
                    mCallBcak.loadMoxie(data);
                    break;
                case JsConstant.GET_BACK_ID:
                    mCallBcak.removeQuestion();
                    break;
                case JsConstant.ERROR_RELOAD:// 错误页的重新加载
                    mCallBcak.reloadWebView();
                    break;
                case JsConstant.GET_CONTACTS:// 调用通讯录页面
                    mCallBcak.goContacts();
                    ContactsGrab contactsGrab = new ContactsGrab(mContext);
                    if (contactsGrab.isSurpass(mContext, SpConstant.C_IS)) {
                        contactsGrab.upload();
                    }
                    break;
                case JsConstant.BACK_APP: // 返回app
                    mCallBcak.exit();
                    break;
                case JsConstant.GO_AUTONYM_CERTIFY:// 调用实名认证页
                    String certId1 = null;
                    String categoryCode1 = null;
                    try {
                        certId1 = data.getString("certId");
                        categoryCode1 = data.getString("categoryCode");
                    } catch (JSONException e) {
                        YxLog.e("Exception:Error parsing js json!" + e);
                        e.printStackTrace();
                    }
                    mCallBcak.addAutonymCertify(certId1, categoryCode1);
                    break;
                case JsConstant.GO_ADD_ASSET_CAR:// 调用车产认证页
                    String certId2 = null;
                    String categoryCode2 = null;
                    try {
                        certId2 = data.getString("certId");
                        categoryCode2 = data.getString("categoryCode");
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        YxLog.e("Exception:Error parsing js json!" + e);
                        e.printStackTrace();
                    }
                    mCallBcak.addAssetCar(certId2, categoryCode2);
                    break;
                case JsConstant.GO_ADD_ASSET_HOUSE:// 调用房产认证页
                    String certId3 = null;
                    String categoryCode3 = null;
                    try {
                        certId3 = data.getString("certId");
                        categoryCode3 = data.getString("categoryCode");
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        YxLog.e("Exception:Error parsing js json!" + e);
                        e.printStackTrace();
                    }
                    mCallBcak.addAssetHouse(certId3, categoryCode3);
                    break;
                case JsConstant.CLOSE_EXAMPLE:// 关闭影像示例页
                    mCallBcak.removeExample();
                    break;
                case JsConstant.SWIPING_CARD_PAY: // 刷卡支付
                    String packName = YxStoreUtil.get(mContext, SpConstant.PARTNER_PAY_PACKAGE_NAME);
                    String className = YxStoreUtil.get(mContext, SpConstant.PARTNER_PAY_CLASS_NAME);
                    if (YxCommonUtil.isNotBlank(packName) && YxCommonUtil.isNotBlank(className)) {
                        mCallBcak.swipingCardPay(packName, className, data.toString());
                    }
                    break;
                case JsConstant.HQX_CODE:
                    String url = null;
                    String backHint = null;
                    String isRefresh = null;
                    String htmlLabel = null;
                    String type = null;
                    try {
                        url = data.getString("entryUrl");
                        backHint = data.getString("backHint");
                        isRefresh = data.getString("refresh");
                        htmlLabel = data.getString("htmlLabel");
                        type = data.getString("type");
                        // 芝麻信用授权时，解密错误临时解决方案
                        // 直接使用缓存 URL
                        if (type.equals("zm")) {//仅芝麻
                            url = YxStoreUtil.get(mContext, zmTemp);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (url != null && !StringUtil.isEmpty(url)) {
                        mCallBcak.addHqx(url, null, type, backHint);
                        if (isRefresh.equals("true")) {
                            mCallBcak.loadUrl(JsConstant.H5_REFRESH, null);
                        }
                    }
                    if (htmlLabel != null && type != null && !StringUtil.isEmpty(htmlLabel) && !StringUtil.isEmpty(type)) {
                        mCallBcak.addHqx(null, htmlLabel, type, backHint);
                        if (isRefresh.equals("true")) {
                            mCallBcak.loadUrl(JsConstant.H5_REFRESH, null);
                        }
                    }

                    break;
                case JsConstant.GO_SETTING:
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    mContext.startActivity(intent);
                    break;
                // -----------------------------下面的需要回传H5-------------------
                case JsConstant.GET_PER:
                    JSONObject perInfo = new JSONObject();
                    try {
                        perInfo.put("authGPS", PermissionUtil.isGpsService(mContext));
                        perInfo.put("authGPSApp", PermissionUtil.isLocPer(mContext));
                        perInfo.put("authContacts", PermissionUtil.isContactsPer(mContext));
                        perInfo.put("authSMS", PermissionUtil.isSmsPer(mContext));
                        perInfo.put("authCall", PermissionUtil.isCallLogPer(mContext));
                        perInfo.put("authAppInfo", PermissionUtil.isAppListPer(mContext));
                        perInfo.put("authPhoneInfo", PermissionUtil.isPhoneInfoPer(mContext));
                    } catch (JSONException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    mCallBcak.loadUrl(JsConstant.GET_PER, perInfo.toString());
                    break;
                case JsConstant.GET_USER_MSG:// 获取用户信息
                    JSONObject userMsg = new JSONObject();
                    try {
                        userMsg.put("partnerId", partnerId);
                        userMsg.put("name", realName);
                        userMsg.put("identityCard", idCardNum);
                        userMsg.put("phoneNum", phoneNumber);
                        userMsg.put("currentSDKVersion", YxCommonConstant.SDK_VERSION);
                        userMsg.put("supportSDKVersion", YxStoreUtil.get(mContext, SpConstant.SUPPORT_SDK_VERSION));
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        YxLog.e("Exception:Error parsing js json!" + e);
                        e.printStackTrace();
                    }
                    if (YxSourceCode == 0) {
                        mCallBcak.loadUrl(JsConstant.GET_USER_MSG, userMsg.toString());
                    } else {
                        mCallBcak.loadQuestionUrl(JsConstant.GET_USER_MSG, userMsg.toString());
                    }
                    break;
                case JsConstant.REQUEST_SERVER:// 与Server通讯
                    if (serviceId != null) {
                        if (serviceId.equals(HttpConstant.Request.SUBMIT) || serviceId.equals(HttpConstant.Request.DEPOSIT_CONFIRM)) {
                            // 如果是提交时或者是放款时，必须提供定位权限且打开gps
                            if (PermissionUtil.isLocPer(mContext).equals(PermissionUtil.UNAUTHORIZED)) {
                                mCallBcak.showDialog("请开启定位服务(权限)\n(如小米手机:设置-其他应用管理-当前APP-权限管理-允许定位)");
                            } else if (PermissionUtil.isGpsService(mContext).equals(PermissionUtil.UNAUTHORIZED)) {
                                mCallBcak.showDialog("请开启GPS服务\n(下拉通知栏-开启GPS)");
                            } else {
                                requestServer(serviceId, data, JsConstant.REQUEST_SERVER);
                            }
                        } else {
                            requestServer(serviceId, data, JsConstant.REQUEST_SERVER);
                        }
                    } else {
                        YxLog.e("Exception:JS requested ServiceId is null ! ! !");
                    }
                    break;
                case JsConstant.SCAN_IDCARD:// 身份证扫描
                    String scanType = null;
                    String appNo = null;
                    String categoryCode = null;

                    try {
                        scanType = data.getString("scanType");
                        JSONObject queryData = data.getJSONObject("queryData");
                        appNo = queryData.getString("appNo");
                        categoryCode = queryData.getString("categoryCode");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    Intent scanIntent = new Intent(mContext, ScanIDCardActivity.class);
                    if (scanType != null)
                        if (scanType.equals("1") || scanType.equals("2"))
                            scanIntent.putExtra("scanType", scanType.equals("1") ? 1 : 2);// 1单独正面扫描
                    // 2单独反面扫描
                    // 0正面扫描上传成功紧接反面扫描
                    scanIntent.putExtra("appNo", StringUtil.isEmpty(appNo) ? "empty" : appNo);
                    scanIntent.putExtra("categoryCode", StringUtil.isEmpty(categoryCode) ? "empty" : categoryCode);
                    mContext.startActivity(scanIntent);
                    ScanIDCardActivity.setCallback(mCallBcak);
                    break;
                case JsConstant.LIVENESS:// 人脸识别
                    String liveAppNo = null;
                    String liveCategoryCode = null;
                    try {
                        liveAppNo = data.getString("appNo");
                        liveCategoryCode = data.getString("categoryCode");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    Intent liveIntent = new Intent(mContext, LivenessActivity.class);
                    liveIntent.putExtra("appNo", StringUtil.isEmpty(liveAppNo) ? "empty" : liveAppNo);
                    liveIntent.putExtra("categoryCode", StringUtil.isEmpty(liveCategoryCode) ? "empty" : liveCategoryCode);
                    mContext.startActivity(liveIntent);
                    LivenessActivity.setCallback(mCallBcak);
                    break;
                case JsConstant.GET_PRODUCT_ID:// 获取产品ID
                    String productId = null;// 产品ID
                    String productIdChild = null;// 产品子ID
                    try {
                        productId = data.getString("productId");
                        productIdChild = data.getString("productIdChild");
                    } catch (JSONException e) {
                        YxLog.e("Exception:Error parsing js json!" + e);
                        e.printStackTrace();
                    }
                    if (null != productId && null != productIdChild) {
                        YxStoreUtil.save(mContext, SpConstant.PRODUCT_ID, productId);
                        YxStoreUtil.save(mContext, SpConstant.PRODUCT_ID_CHILD, productIdChild);
                    }
                    JSONObject saveState = new JSONObject();
                    String saveProductId = YxStoreUtil.get(mContext, SpConstant.PRODUCT_ID);
                    String saveProductIdChild = YxStoreUtil.get(mContext, SpConstant.PRODUCT_ID_CHILD);
                    try {
                        if (YxCommonUtil.isNotBlank(saveProductId) && YxCommonUtil.isNotBlank(saveProductIdChild)) {
                            saveState.put("state", "1");
                        } else {
                            saveState.put("state", "");
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        YxLog.e("Exception:Error parsing js json!" + e);
                        e.printStackTrace();
                    }
                    mCallBcak.loadUrl(JsConstant.GET_PRODUCT_ID, saveState.toString());// 返回存储状态
                    break;
                case JsConstant.GET_TONGDUN:
                    String initStatus = FMAgent.getInitStatus();
                    if (initStatus.equals("successful")) {
                        String blackBox = FMAgent.onEvent(mContext);
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("blackBox", blackBox);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mCallBcak.loadUrl(JsConstant.GET_TONGDUN, obj.toString());
                    } else {
                        if (initStatus.equals("failed") || initStatus.equals("uninit")) {
                            new DialogUtil(mContext).showDialogForHint("提交失败，请稍候重试！");
                            try {
                                FMAgent.init(mContext, YxCommonConstant.Params.TONGDUN);
                            } catch (FMException e) {
                                e.printStackTrace();
                            }
                        } else {// uninit/loading/collecting/profiling
                            new DialogUtil(mContext).showDialogForHint("提交失败，请稍候重试！");
                        }
                    }
                    break;
                case JsConstant.J267:
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
                            mCallBcak.loadUrl(JsConstant.J267, obj.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    break;
            }
        } else {
            YxLog.e("Exception:JS requested Code is null ! ! !");
        }
        // }
    }

    /**
     * @param code 交易码
     * @param data 数据
     * @return String 格式化之后的数据
     * @作者:xiaochangyou
     * @创建时间:2016-7-18 下午1:59:55
     * @描述:TODO[格式化数据,和JS约定好的格式]
     */
    public String formatParam(String code, String data) {
        JSONObject jsonDate = null;
        try {
            if (data == null) {
                jsonDate = new JSONObject();
            } else {
                if (data.contains("%")) {
                    data = data.replaceAll("%", "%25");// 替换%为转义符，防止被转义
                }
                jsonDate = new JSONObject(data);
            }
        } catch (JSONException e) {
            YxLog.e("Exception:Error parsing js json!" + e);
            e.printStackTrace();
        }
        JSONObject format = new JSONObject();
        try {
            format.put("code", code);
            format.put("data", jsonDate);
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            YxLog.e("Exception:Error parsing js json!" + e1);
            e1.printStackTrace();
            return null;
        }
        YxLog.d("======[webViewBridgeService] " + format.toString());
        return format.toString();

    }

    //json String替换单个字符
    private String jsonString(String s) {
        char[] temp = s.toCharArray();
        int n = temp.length;
        for (int i = 0; i < n; i++) {
            if (temp[i] == ':' && temp[i + 1] == '"') {
                for (int j = i + 2; j < n; j++) {
                    //					if (temp[j] == '\\') {
                    if (temp[j] == '\'') {
                        //                             if(temp[j+1]!=',' &&  temp[j+1]!='}'){
                        //                                 temp[j]='”';
                        //                             }else if(temp[j+1]==',' ||  temp[j+1]=='}'){
                        //                                 break ;
                        //                             }
                        temp[j] = '"';
                    }
                }
            }
        }
        return new String(temp);
    }

    private final String zmTemp = "2011";

    /**
     * @param serviceId 与Server的交易码
     * @param param     参数
     * @param JsCode    与js的交易码
     * @作者:xiaochangyou
     * @创建时间:2016-7-18 下午2:00:57
     * @描述:TODO[代表JS请求Server]
     */
    private void requestServer(final String serviceId, final JSONObject param, final String JsCode) {
        final DialogLoading dialogLoading = new DialogLoading(mContext);
        dialogLoading.show();
        new RequestEngine(mContext).execute(serviceId, param, new RequestCallBack(mContext) {
            @Override
            public void onSucces(String result) {
                if (JsCode.equals(JsConstant.GET_USER_STATUS)) {
                    mCallBcak.loadUrl(JsCode, result);
                } else if (JsCode.equals(JsConstant.REQUEST_SERVER)) {
                    mCallBcak.loadUrl(JsCode, result);
                }
                // 芝麻信用授权时，解密错误临时解决方案
                // 截取报文，存储 URL，后续直接使用缓存 URL
                if (serviceId.equals("2110")) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        JSONObject body = obj.getJSONObject("serviceBody");
                        String url = body.getString("url");
                        YxStoreUtil.save(mContext, zmTemp, url);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                dialogLoading.cancel();
                super.onSucces(result);
            }

            @Override
            public void onFailure(String errorCode, String errorMsg) {
                // TODO Auto-generated method stub
                JSONObject data = new JSONObject();
                try {
                    data.put("serviceBody", "null");
                    JSONObject serviceHeader = new JSONObject();
                    serviceHeader.put("serviceId", "null");
                    serviceHeader.put("responseCode", JsConstant.CONNECT_ERROR);
                    serviceHeader.put("responseMsg", "网络出错");
                    data.put("serviceHeader", serviceHeader);
                } catch (JSONException e) {
                    YxLog.e("Exception:" + e);
                    e.printStackTrace();
                }
                if (JsCode.equals(JsConstant.GET_USER_STATUS)) {
                    mCallBcak.loadUrl(JsCode, data.toString());
                } else if (JsCode.equals(JsConstant.REQUEST_SERVER)) {
                    mCallBcak.loadUrl(JsCode, data.toString());
                }
                dialogLoading.cancel();
                super.onFailure(errorCode, errorMsg);
            }
        });
    }
}
