package com.yxjr.credit.http.manage;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.provider.Settings.Secure;
import android.text.TextUtils;

import com.yxjr.credit.constants.SpConstant;
import com.yxjr.credit.constants.YxCommonConstant;
import com.yxjr.credit.log.YxLog;
import com.yxjr.credit.security.Md5;
import com.yxjr.credit.security.YxjrSecurity;
import com.yxjr.credit.util.PermissionUtil;
import com.yxjr.credit.util.ToastUtil;
import com.yxjr.credit.util.YxAndroidUtil;
import com.yxjr.credit.util.YxCommonUtil;
import com.yxjr.credit.util.YxNetworkUtil;
import com.yxjr.credit.util.YxStoreUtil;

public class RequestFactory {

    private Context mContext;
    String uuid = null;
    String partnerId = null;
    String partnerLoginId = null;
    String appPackage = null;
    String osType = null;

    public RequestFactory(Context context) {
        this.mContext = context;
        uuid = YxAndroidUtil.getUUID(mContext);
        partnerId = YxStoreUtil.get(mContext, SpConstant.PARTNER_ID);
        partnerLoginId = YxStoreUtil.get(mContext, SpConstant.PARTNER_ID_CARD_NUM);
        appPackage = YxAndroidUtil.getAppPackage(mContext);
        osType = YxAndroidUtil.OS_TYPE;
    }

    /**
     *
     * @param serviceId
     * @return HashMap<String,String> Http Head
     * @作者:xiaochangyou
     * @创建时间:2016-12-29 下午5:21:33
     * @描述:TODO[拼装Http Head]
     */
    public HashMap<String, String> getHttpHead(String serviceId) {
        HashMap<String, String> httpHead = new HashMap<String, String>();
        httpHead.put("serviceId", serviceId);
        httpHead.put("partnerId", partnerId);
        String mdPld=Md5.encrypt(partnerLoginId);
        httpHead.put("partnerLoginId", TextUtils.isEmpty(mdPld)?"":mdPld);
        httpHead.put("uuid", uuid);
        httpHead.put("bundleId", appPackage);
        httpHead.put("osType", osType);
        YxLog.d("HttpHead: serviceId:" + serviceId + "; partnerId:" + partnerId + "; idNum:" + Md5.encrypt(partnerLoginId) + "; uuid:" + uuid + ";");
        return httpHead;
    }

    /**
     * @param context
     * @param partnerId      机构ID
     * @param partnerLoginId 身份证号
     * @return JSONObject body heae
     * @作者:xiaochangyou
     * @创建时间:2016-7-18 上午10:53:40
     * @描述:TODO[组装body head]
     */
    public JSONObject getBodyHead(Context context) {
        JSONObject header = new JSONObject();
        try {

            String appPkg = YxAndroidUtil.getAppPackage(context);
            String channel = null;
            if (appPkg.equals("com.ruimin.phonewallet") || appPkg.equals("com.yxjr.yimiaojie")) {
                PackageManager packageManager = context.getPackageManager();
                ApplicationInfo appInfo = packageManager.getApplicationInfo(appPkg, PackageManager.GET_META_DATA);
                channel = appInfo.metaData.getString("RELEASE_CHANNEL");// metadata的value不能全数字，不然为null
            }
            header.put("releaseChannel", channel == null ? "" : channel);

            header.put("partnerId", partnerId);// 合作机构
            header.put("appVersion", YxAndroidUtil.getAppVersion(context));// app版本
            header.put("sdkVersion", YxCommonConstant.SDK_VERSION);// sdk版本
            String mdPld=Md5.encrypt(partnerLoginId);
            header.put("partnerLoginId", TextUtils.isEmpty(mdPld)?"":mdPld);// 身份证

            header.put("channelNo", YxStoreUtil.get(context, SpConstant.CHANNEL_NO));// 身份证

            JSONObject product = new JSONObject();
            product.put("productId", YxStoreUtil.get(context, SpConstant.PRODUCT_ID));// 产品ID
            product.put("productIdChild", YxStoreUtil.get(context, SpConstant.PRODUCT_ID_CHILD));// 产品子ID
            header.put("product", product);

            JSONObject gps = new JSONObject();
            gps.put("longitude", YxStoreUtil.get(context, SpConstant.LONGITUDE));// 经度
            gps.put("latitude", YxStoreUtil.get(context, SpConstant.LATITUDE));// 纬度
            gps.put("altitude", "");// 海拔
            header.put("gps", gps);

            JSONObject net = new JSONObject();
            net.put("ip", YxNetworkUtil.getIP(context));// IP
            net.put("netType", YxNetworkUtil.isWifi(context));// 是否是WiFi;1:true||0:false
            net.put("mac", YxAndroidUtil.getLocalMac());// mac地址
            header.put("net", net);

            header.put("battery", YxStoreUtil.get(mContext, SpConstant.BATTERY));// 电量
            header.put("permissionStatus", PermissionUtil.getAllPer(mContext));// 权限
            header.put("sensors", YxAndroidUtil.getSensorsInfo(mContext));// 传感器信息

            JSONObject device = new JSONObject();
            device.put("isBreakOut", Integer.toString(YxjrSecurity.isRootSystem() ? 1 : 0));// IOS:是否越狱||android:是否Root
            device.put("osVersion", YxAndroidUtil.getOsVersion());// 系统版本
            device.put("osType", YxAndroidUtil.OS_TYPE);// IOS:1||android:2
            device.put("uuid", YxAndroidUtil.getUUID(context));// 服务器识别token使用(保证唯一)
            device.put("imei", YxAndroidUtil.getDeviceId(context));// 设备号及IMEI
            device.put("imsi", YxAndroidUtil.getIMSI(context));// IMSI
            device.put("factoryId", Secure.getString(context.getContentResolver(), Secure.ANDROID_ID));// 出厂ID
            device.put("simNo", YxAndroidUtil.getSimSerialNumber(context));// SIM卡序列号
            device.put("brand", YxAndroidUtil.getDeviceBrand());// 手机厂商
            device.put("model", YxAndroidUtil.getDeviceModel());// 手机型号
            device.put("simMobileNo", YxAndroidUtil.getNativePhoneNumber(context));// 设备手机号(双卡默认获取主卡，无卡则无值无字段)
            device.put("androidId", YxAndroidUtil.getAndroidId(mContext));//AndroidId
            header.put("device", device);

            header.put("phoneStorgeSize", YxAndroidUtil.getPhoneStorageSize(mContext));
        } catch (Exception e) {
            ToastUtil.showToast(context, "参数错误！");
            return null;
        }
        YxLog.d("bodyHead:" + header);
        return header;
    }

    /**
     * @param context
     * @param partnerId      机构ID
     * @param partnerLoginId 身份证号
     * @param body           报文体
     * @return JSONObject 请求报文
     * @作者:xiaochangyou
     * @创建时间:2016-7-18 上午10:49:52
     * @描述:TODO[组装请求报文]
     */
    public JSONObject getParamaters(Context context, JSONObject body) {

        JSONObject request = new JSONObject();
        try {
            request.put("serviceHeader", getBodyHead(context));// 报文头
            if (body == null) {
                request.put("serviceBody", new JSONObject());// 报文体
            } else {
                request.put("serviceBody", body);
            }
        } catch (JSONException e) {
            return null;
        } catch (Exception e) {
            return null;
        }

        return request;
    }

    /**
     * @param mContext
     * @param serviceBody
     * @throws JSONException
     * @作者:xiaochangyou
     * @创建时间:2016-8-24 下午3:35:54
     * @描述:TODO[解析ServiceBody并缓存]
     */
    public void parseServiceBody(Context mContext, JSONObject serviceBody) throws JSONException {
        // json中的字段名
        /** 系统是否处于升级状态 */
        String UPGRADE = "UPGRADE";
        /** 系统最低版本支持 */
        String SUPPORT_SDK_VERSION = "SUPPORT_SDK_VERSION";
        /** 通讯录每次发送量 */
        String CSQ = "CSQ";
        /** 通讯录是否发送 */
        String C = "C";
        /** 通讯录最后发送时间 */
        String CLASTTIME = "CLASTTIME";
        /** 浏览器历史记录每次发送量 */
        String BSQ = "BSQ";
        /** 浏览器历史记录是否发送 */
        String B = "B";
        /** 浏览器历史记录最后发送时间 */
        String BLASTTIME = "BLASTTIME";
        /** 短信每次发送量 */
        String MSQ = "MSQ";
        /** 短信是否发送 */
        String M = "M";
        /** 短信最后发送时间 */
        String MLASTTIME = "MLASTTIME";
        /** APP列表每次发送量 */
        String ASQ = "ASQ";
        /** APP列表是否发送 */
        String A = "A";
        /** APP列表最后发送时间 */
        String ALASTTIME = "ALASTTIME";
        /** 通话记录每次发送量 */
        String CASQ = "CASQ";
        /** 通话记录是否发送 */
        String CA = "CA";
        /** 通话记录最后发送时间 */
        String CALASTTIME = "CALASTTIME";
        /** 心跳时间 */
        String HBSF = "HBSF";
        /** 同盾心跳是否发送 */
        String HB_L = "HB_L";
        /** 同盾心跳间隔时间 */
        String HBSF_L = "HBSF_L";
        /**照片信息是否发送*/
        String P = "P";
        /**照片信息每次发送量*/
        String PSQ = "PSQ";
        /**照片信息最后发送时间*/
        String PLASTTIME = "PLASTTIME";
        // 解析json数据中的字段名
        String UPGRADE_P = null;
        String SUPPORT_SDK_VERSION_P = null;
        String CSQ_P = null;
        String C_P = null;
        String CLASTTIME_P = null;
        String BSQ_P = null;
        String B_P = null;
        String BLASTTIME_P = null;
        String MSQ_P = null;
        String M_P = null;
        String MLASTTIME_P = null;
        String ASQ_P = null;
        String A_P = null;
        String ALASTTIME_P = null;
        String CASQ_P = null;
        String CA_P = null;
        String CALASTTIME_P = null;
        String HBSF_P = null;
        String HB_L_P = null;
        String HBSF_L_P = null;
        String P_P = null;
        String PSQ_P = null;
        String PLASTTIME_P = null;
        // 第一步:判断json数据中是否有这字段,没有则默认解析的变量为null
        if (!serviceBody.isNull(UPGRADE)) {
            UPGRADE_P = serviceBody.getString(UPGRADE);
        }
        if (!serviceBody.isNull(SUPPORT_SDK_VERSION)) {
            SUPPORT_SDK_VERSION_P = serviceBody.getString(SUPPORT_SDK_VERSION);
        }
        if (!serviceBody.isNull(CSQ)) {
            CSQ_P = serviceBody.getString(CSQ);
        }
        if (!serviceBody.isNull(C)) {
            C_P = serviceBody.getString(C);
        }
        if (!serviceBody.isNull(CLASTTIME)) {
            CLASTTIME_P = serviceBody.getString(CLASTTIME);
        }
        if (!serviceBody.isNull(BSQ)) {
            BSQ_P = serviceBody.getString(BSQ);
        }
        if (!serviceBody.isNull(B)) {
            B_P = serviceBody.getString(B);
        }
        if (!serviceBody.isNull(BLASTTIME)) {
            BLASTTIME_P = serviceBody.getString(BLASTTIME);
        }
        if (!serviceBody.isNull(MSQ)) {
            MSQ_P = serviceBody.getString(MSQ);
        }
        if (!serviceBody.isNull(M)) {
            M_P = serviceBody.getString(M);
        }
        if (!serviceBody.isNull(MLASTTIME)) {
            MLASTTIME_P = serviceBody.getString(MLASTTIME);
        }
        if (!serviceBody.isNull(ASQ)) {
            ASQ_P = serviceBody.getString(ASQ);
        }
        if (!serviceBody.isNull(A)) {
            A_P = serviceBody.getString(A);
        }
        if (!serviceBody.isNull(ALASTTIME)) {
            ALASTTIME_P = serviceBody.getString(ALASTTIME);
        }
        if (!serviceBody.isNull(CASQ)) {
            CASQ_P = serviceBody.getString(CASQ);
        }
        if (!serviceBody.isNull(CA)) {
            CA_P = serviceBody.getString(CA);
        }
        if (!serviceBody.isNull(CALASTTIME)) {
            CALASTTIME_P = serviceBody.getString(CALASTTIME);
        }
        if (!serviceBody.isNull(HBSF)) {
            HBSF_P = serviceBody.getString(HBSF);
        }
        if (!serviceBody.isNull(HB_L)) {
            HB_L_P = serviceBody.getString(HB_L);
        }
        if (!serviceBody.isNull(HBSF_L)) {
            HBSF_L_P = serviceBody.getString(HBSF_L);
        }
        if (!serviceBody.isNull(P)) {
            P_P = serviceBody.getString(P);
        }
        if (!serviceBody.isNull(PSQ)) {
            PSQ_P = serviceBody.getString(PSQ);
        }
        if (!serviceBody.isNull(PLASTTIME)) {
            PLASTTIME_P = serviceBody.getString(PLASTTIME);
        }
        // 第二步:数据缓存
        if (YxCommonUtil.isNotBlank(UPGRADE_P)) {
            YxStoreUtil.save(mContext, SpConstant.UPGRADE, UPGRADE_P);
        } else {
            YxStoreUtil.save(mContext, SpConstant.UPGRADE, "N");
        }
        if (YxCommonUtil.isNotBlank(SUPPORT_SDK_VERSION_P)) {
            YxStoreUtil.save(mContext, SpConstant.SUPPORT_SDK_VERSION, SUPPORT_SDK_VERSION_P);
        } else {
            YxStoreUtil.save(mContext, SpConstant.SUPPORT_SDK_VERSION, "1.0.1");
        }
        if (YxCommonUtil.isNotBlank(CSQ_P)) {
            YxStoreUtil.save(mContext, SpConstant.CSQ, CSQ_P);
        } else {
            YxStoreUtil.save(mContext, SpConstant.CSQ, "200");
        }
        if (YxCommonUtil.isNotBlank(C_P)) {
            YxStoreUtil.save(mContext, SpConstant.C_IS, C_P);
        } else {
            YxStoreUtil.save(mContext, SpConstant.C_IS, "N");
        }
        if (YxCommonUtil.isNotBlank(CLASTTIME_P)) {
            YxStoreUtil.save(mContext, SpConstant.C_LASTTIME, CLASTTIME_P);
        } else {
            YxStoreUtil.save(mContext, SpConstant.C_LASTTIME, null);
        }
        if (YxCommonUtil.isNotBlank(BSQ_P)) {
            YxStoreUtil.save(mContext, SpConstant.BSQ, BSQ_P);
        } else {
            YxStoreUtil.save(mContext, SpConstant.BSQ, "200");
        }
        if (YxCommonUtil.isNotBlank(B_P)) {
            YxStoreUtil.save(mContext, SpConstant.B_IS, B_P);
        } else {
            YxStoreUtil.save(mContext, SpConstant.B_IS, "N");
        }
        if (YxCommonUtil.isNotBlank(BLASTTIME_P)) {
            YxStoreUtil.save(mContext, SpConstant.B_LASTTIME, BLASTTIME_P);
        } else {
            YxStoreUtil.save(mContext, SpConstant.B_LASTTIME, null);
        }
        if (YxCommonUtil.isNotBlank(MSQ_P)) {
            YxStoreUtil.save(mContext, SpConstant.MSQ, MSQ_P);
        } else {
            YxStoreUtil.save(mContext, SpConstant.MSQ, "200");
        }
        if (YxCommonUtil.isNotBlank(M_P)) {
            YxStoreUtil.save(mContext, SpConstant.M_IS, M_P);
        } else {
            YxStoreUtil.save(mContext, SpConstant.M_IS, "N");
        }
        if (YxCommonUtil.isNotBlank(MLASTTIME_P)) {
            YxStoreUtil.save(mContext, SpConstant.M_LASTTIME, MLASTTIME_P);
        } else {
            YxStoreUtil.save(mContext, SpConstant.M_LASTTIME, null);
        }
        if (YxCommonUtil.isNotBlank(ASQ_P)) {
            YxStoreUtil.save(mContext, SpConstant.ASQ, ASQ_P);
        } else {
            YxStoreUtil.save(mContext, SpConstant.ASQ, "200");
        }
        if (YxCommonUtil.isNotBlank(A_P)) {
            YxStoreUtil.save(mContext, SpConstant.A_IS, A_P);
        } else {
            YxStoreUtil.save(mContext, SpConstant.A_IS, "N");
        }
        if (YxCommonUtil.isNotBlank(ALASTTIME_P)) {
            YxStoreUtil.save(mContext, SpConstant.A_LASTTIME, ALASTTIME_P);
        } else {
            YxStoreUtil.save(mContext, SpConstant.A_LASTTIME, null);
        }
        if (YxCommonUtil.isNotBlank(CASQ_P)) {
            YxStoreUtil.save(mContext, SpConstant.CASQ, CASQ_P);
        } else {
            YxStoreUtil.save(mContext, SpConstant.CASQ, "200");
        }
        if (YxCommonUtil.isNotBlank(CA_P)) {
            YxStoreUtil.save(mContext, SpConstant.CA_IS, CA_P);
        } else {
            YxStoreUtil.save(mContext, SpConstant.CA_IS, "N");
        }
        if (YxCommonUtil.isNotBlank(CALASTTIME_P)) {
            YxStoreUtil.save(mContext, SpConstant.CA_LASTTIME, CALASTTIME_P);
        } else {
            YxStoreUtil.save(mContext, SpConstant.CA_LASTTIME, null);
        }
        if (YxCommonUtil.isNotBlank(HBSF_P)) {
            YxStoreUtil.save(mContext, SpConstant.HBSF, HBSF_P);
        } else {
            YxStoreUtil.save(mContext, SpConstant.HBSF, "20m");
        }
        if (YxCommonUtil.isNotBlank(HB_L_P)) {
            YxStoreUtil.save(mContext, SpConstant.HB_L, HB_L_P);
        } else {
            YxStoreUtil.save(mContext, SpConstant.HB_L, "N");
        }
        if (YxCommonUtil.isNotBlank(HBSF_L_P)) {
            YxStoreUtil.save(mContext, SpConstant.HBSF_L, HBSF_L_P);
        } else {
            YxStoreUtil.save(mContext, SpConstant.HBSF_L, "20m");
        }
        if (YxCommonUtil.isNotBlank(P_P)) {
            YxStoreUtil.save(mContext, SpConstant.P, P_P);
        } else {
            YxStoreUtil.save(mContext, SpConstant.HBSF_L, "N");
        }
        if (YxCommonUtil.isNotBlank(PSQ_P)) {
            YxStoreUtil.save(mContext, SpConstant.PSQ, PSQ_P);
        } else {
            YxStoreUtil.save(mContext, SpConstant.PSQ, "200");
        }
        if (YxCommonUtil.isNotBlank(PLASTTIME_P)) {
            YxStoreUtil.save(mContext, SpConstant.PLASTTIME, PLASTTIME_P);
        } else {
            YxStoreUtil.save(mContext, SpConstant.PLASTTIME, null);
        }

    }

}
