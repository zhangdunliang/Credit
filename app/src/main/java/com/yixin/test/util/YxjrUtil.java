package com.yixin.test.util;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.yxjr.credit.YtzCallBack;
import com.yxjr.credit.YxCredit;
import com.yxjr.credit.YxExecuteCallBack;
import com.yxjr.credit.constants.YxConstant;

/**
 * Created by xiaochangyou on 2017/10/23.
 */

public class YxjrUtil {

    public static Bundle getBundle(String partnerId, String channel, String realName, String idCardNum, String phoneNum, String key) {
        Bundle bundle = new Bundle();
        bundle.putString(YxConstant.CHANNEL_NO, channel);// 渠道号
        bundle.putString(YxConstant.PARTNER_ID, partnerId);// 机构号[必传]
        bundle.putString(YxConstant.PARTNER_REAL_NAME, realName);// 用户身份证号对应姓名[必传]
        bundle.putString(YxConstant.PARTNER_ID_CARD_NUM, idCardNum);// 用户身份证号[必传]
        bundle.putString(YxConstant.PARTNER_PHONE_NUMBER, phoneNum);// 用户手机号[必传]
        bundle.putString(YxConstant.PARTNER_KEY, key);// 密钥[必传]
//        bundle.putString(YxConstant.PARTNER_PAY_PACKAGE_NAME, "com.yixin.credit.test.pay");// 支付Activity包名
//        bundle.putString(YxConstant.PARTNER_PAY_CLASS_NAME, "PayTestActivity");// 支付Activity类名
        return bundle;
    }

    /**
     * @作者:xiaochangyou
     * @创建时间:2016-10-19 下午6:00:57
     * @描述:TODO[开始信贷SDK相关]
     */
    public static void startCredit(final Context context, Bundle bundle) {
        // ======信贷SDK相关处理开始======

        // ===第一步：初始化[必须];
        // ===第二步：组装数据[必须];
        // ===第三步：传值,正式开始信贷并获得对应响应码[必须];
        // ======信贷SDK相关处理结束======
        YxCredit.getInstance().init(context);// 必须先初始化
        YxCredit.getInstance().start(context, bundle, new YxExecuteCallBack() {

            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(String errorCode, String errorMsg) {
                DialogUtil.showDialog(context, "SDK返回失败", errorMsg + "[" + errorMsg + "]");
            }
        });

    }

    public static void getUser(final Context context, Bundle bundle) {
        YxCredit.getInstance().init(context);// 必须先初始化
        YxCredit.getInstance().ytzStart(context, bundle, "user", new YtzCallBack() {

            @Override
            public void onStart() {
                Toast.makeText(context, "开始获取用户信息", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMsg) {
                DialogUtil.showDialog(context, "获取用户信息失败", errorMsg);
            }

            @Override
            public void onSuccess(String data) {

                DialogUtil.showDialog(context, "获取用户信息成功", data);
            }

        });
    }

    public static void getAccount(final Context context, Bundle bundle) {
        YxCredit.getInstance().init(context);// 必须先初始化
        YxCredit.getInstance().ytzStart(context, bundle, "account", new YtzCallBack() {

            @Override
            public void onStart() {
                Toast.makeText(context, "开始获取账户信息", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMsg) {
                DialogUtil.showDialog(context, "获取账户信息失败", errorMsg);
            }

            @Override
            public void onSuccess(String data) {
                DialogUtil.showDialog(context, "获取账户信息成功", data);
            }

        });
    }

    public static void getContract(final Context context, Bundle bundle) {
        YxCredit.getInstance().init(context);// 必须先初始化
        YxCredit.getInstance().ytzStart(context, bundle, "contract", new YtzCallBack() {

            @Override
            public void onStart() {
                Toast.makeText(context, "开始获取合同信息", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMsg) {
                DialogUtil.showDialog(context, "获取合同信息失败", errorMsg);
            }

            @Override
            public void onSuccess(String data) {
                DialogUtil.showDialog(context, "获取合同信息成功", data);
            }

        });
    }

    public static void getParam(final Context context, Bundle bundle) {
        YxCredit.getInstance().init(context);// 必须先初始化
        YxCredit.getInstance().ytzStart(context, bundle, "param", new YtzCallBack() {

            @Override
            public void onStart() {
                Toast.makeText(context, "开始获取参数信息", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMsg) {
                DialogUtil.showDialog(context, "获取参数信息失败", errorMsg);
            }

            @Override
            public void onSuccess(String data) {
                DialogUtil.showDialog(context, "获取参数信息成功", data);
            }

        });
    }
}
