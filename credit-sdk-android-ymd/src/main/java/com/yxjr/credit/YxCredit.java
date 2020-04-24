package com.yxjr.credit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.yxjr.credit.constants.YxCommonConstant;
import com.yxjr.credit.constants.YxConstant;
import com.yxjr.credit.log.YxLog;
import com.yxjr.credit.ui.YxEntryActivity;
import com.yxjr.credit.util.ToastUtil;
import com.yxjr.credit.util.YxAndroidUtil;
import com.yxjr.credit.util.YxCommonUtil;
import com.yxjr.credit.util.YxNetworkUtil;

import java.util.HashMap;

import cn.tongdun.android.shell.FMAgent;
import cn.tongdun.android.shell.exception.FMException;

/**
 * All rights Reserved, Designed By ClareShaw
 *
 * @公司:益芯金融
 * @作者:xiaochangyou
 * @版本:V1.0
 * @创建时间:2016-7-15 下午5:07:39
 * @描述:TODO[SDK入口类]
 */
public final class YxCredit {
    /**
     * 初始化状态
     */
    private String initState = null;
    private static YxCredit instance = null;
    private ErrorMsg errorMsg = null;
    private YxExecuteCallBack mCallBcak = null;
    private YtzCallBack mYtzCallback;

    public YxCredit() {
        errorMsg = new ErrorMsg();
    }

    public static YxCredit getInstance() {
        if (instance == null) {
            synchronized (YxCredit.class) {
                if (instance == null) {
                    instance = new YxCredit();
                }
            }
        }
        return instance;
    }

    /**
     * @param context
     * @作者:xiaochangyou
     * @创建时间:2016-7-15 下午5:08:02
     * @描述:TODO[SDK进入前相关初始化]
     */
    public void init(Context context) {
        HashMap<String, Object> options = new HashMap<>();
        options.put(FMAgent.OPTION_INIT_TIMESPAN, 0); //两次调用间隔设为0
        try {
            FMAgent.init(context, YxCommonConstant.Params.TONGDUN, options);
        } catch (FMException e) {
            YxLog.e("FM error : " + e);

            e.printStackTrace();
        }
        if (YxCommonUtil.isTablet(context)) {
            initState = "error";
            ToastUtil.showToast(context, "暂不支持平板！");
        } else if (!YxNetworkUtil.isNetworkConnected(context)) {
            initState = "error";
            ToastUtil.showToast(context, "请连接网络！");
        } else if (Integer.parseInt(YxAndroidUtil.getOsVersion().substring(0, 1)) < 4) {
            initState = "error";
            ToastUtil.showToast(context, "暂不支持 4.0.0 以下安卓版本！");
        } else {
            initState = "succeed";
        }
        if (!BuildConfig.YMD_RELEASE) {
            ToastUtil.showToast(context, BuildConfig.FLAVOR + " [" + BuildConfig.YMD_SDK_VERSION + "]");
        }
    }

    /**
     * @param context
     * @param bundle  携带的数据
     * @return String 处理状态
     * @作者:xiaochangyou
     * @创建时间:2016-7-15 下午5:08:17
     * @描述:TODO[SDK主要入口以及开始的地方]
     */
    public void start(Context context, Bundle bundle, YxExecuteCallBack execuuteCallBack) {
        if (execuuteCallBack == null) {
            mCallBcak = new YxExecuteCallBack() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onFailure(String errorCode, String errorMsg) {
                }
            };
        }
        this.mCallBcak = execuuteCallBack;
        execute(context, bundle, null);
    }

    private void execute(Context context, Bundle bundle, String type) {
        if (initState == null) {
            if (mCallBcak != null) {
                mCallBcak.onFailure(errorMsg.CODE_10001, errorMsg.getMessageInfo(errorMsg.CODE_10001));
            } else {
                mYtzCallback.onFailure(errorMsg.getMessageInfo(errorMsg.CODE_10001));
            }
        } else if (initState.equals("error")) {
            if (mCallBcak != null) {
                mCallBcak.onFailure(errorMsg.CODE_10003, errorMsg.getMessageInfo(errorMsg.CODE_10003));
            } else {
                mYtzCallback.onFailure(errorMsg.getMessageInfo(errorMsg.CODE_10003));
            }
        } else if (initState.equals("succeed")) {
            if (bundle == null) {
                if (mCallBcak != null) {
                    mCallBcak.onFailure(errorMsg.CODE_10002, errorMsg.getMessageInfo(errorMsg.CODE_10002));
                } else {
                    mYtzCallback.onFailure(errorMsg.getMessageInfo(errorMsg.CODE_10002));
                }
            } else {
                String partnerId = bundle.getString(YxConstant.PARTNER_ID).trim();
                String realName = bundle.getString(YxConstant.PARTNER_REAL_NAME).trim();
                String idCardNum = bundle.getString(YxConstant.PARTNER_ID_CARD_NUM).trim();
                String phoneNumber = bundle.getString(YxConstant.PARTNER_PHONE_NUMBER).trim();
                String key = bundle.getString(YxConstant.PARTNER_KEY);

//                YxCommonUtil.processParam(context, bundle);
                // String packageName =
                // bundle.getString(YxConstant.PARTNER_PAY_PACKAGE_NAME).trim();
                // String className =
                // bundle.getString(YxConstant.PARTNER_PAY_CLASS_NAME).trim();
                if (!YxCommonUtil.isNotBlank(key)) {
                    if (mCallBcak != null) {
                        mCallBcak.onFailure(errorMsg.CODE_20001, errorMsg.getMessageInfo(errorMsg.CODE_20001));
                    } else {
                        mYtzCallback.onFailure(errorMsg.getMessageInfo(errorMsg.CODE_20001));
                    }
                } else if (!YxCommonUtil.isNotBlank(partnerId)) {
                    if (mCallBcak != null) {
                        mCallBcak.onFailure(errorMsg.CODE_20002, errorMsg.getMessageInfo(errorMsg.CODE_20002));
                    } else {
                        mYtzCallback.onFailure(errorMsg.getMessageInfo(errorMsg.CODE_20002));
                    }
                } else if (!YxCommonUtil.isNotBlank(idCardNum)) {
                    if (mCallBcak != null) {
                        mCallBcak.onFailure(errorMsg.CODE_20003, errorMsg.getMessageInfo(errorMsg.CODE_20003));
                    } else {
                        mYtzCallback.onFailure(errorMsg.getMessageInfo(errorMsg.CODE_20003));
                    }
                } else if (!YxCommonUtil.isNotBlank(realName)) {
                    if (mCallBcak != null) {
                        mCallBcak.onFailure(errorMsg.CODE_20004, errorMsg.getMessageInfo(errorMsg.CODE_20004));
                    } else {
                        mYtzCallback.onFailure(errorMsg.getMessageInfo(errorMsg.CODE_20004));
                    }
                } else if (!YxCommonUtil.isNotBlank(phoneNumber)) {
                    if (mCallBcak != null) {
                        mCallBcak.onFailure(errorMsg.CODE_20005, errorMsg.getMessageInfo(errorMsg.CODE_20005));
                    } else {
                        mYtzCallback.onFailure(errorMsg.getMessageInfo(errorMsg.CODE_20005));
                    }
                } else {
                    if (mCallBcak != null) {
                        Intent intent = new Intent(context, YxEntryActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                        mCallBcak.onSuccess();
                    } else {
                        if (type.equals("user")) {
                            new YtzEdb(context, bundle, mYtzCallback).startUser();
                        } else if (type.equals("account")) {
                            new YtzEdb(context, bundle, mYtzCallback).startAccount();
                        } else if (type.equals("contract")) {
                            new YtzEdb(context, bundle, mYtzCallback).startContract();
                        } else if (type.equals("param")) {
                            new YtzEdb(context, bundle, mYtzCallback).startGetParam();
                        }
                    }
                }
            }
        } else {
            if (mCallBcak != null) {
                mCallBcak.onFailure(errorMsg.CODE_10000, errorMsg.getMessageInfo(errorMsg.CODE_10000));
            } else {
                mYtzCallback.onFailure(errorMsg.getMessageInfo(errorMsg.CODE_10000));
            }
        }
    }

    public void ytzStart(Context context, Bundle bundle, String type, YtzCallBack callback) {
        if (callback == null) {
            mYtzCallback = new YtzCallBack() {
                @Override
                public void onSuccess(String data) {
                }

                @Override
                public void onStart() {
                }

                @Override
                public void onFailure(String errorMsg) {
                }
            };
        } else {
            this.mYtzCallback = callback;
        }
        this.mCallBcak = null;
        if (YxAndroidUtil.getAppPackage(context).equals("com.ruimin.phonewallet") || !YxAndroidUtil.getAppPackage(context).equals("com.yxjr.yimiaojie")) {
            mYtzCallback.onStart();
            execute(context, bundle, type);
        } else {
            mYtzCallback.onFailure("非法请求！");
            return;
        }
    }
}
