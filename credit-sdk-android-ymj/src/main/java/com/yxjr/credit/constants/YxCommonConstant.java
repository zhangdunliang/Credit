package com.yxjr.credit.constants;

import com.yxjr.credit.BuildConfig;

import cn.tongdun.android.shell.FMAgent;

/**
 * All rights Reserved, Designed By ClareShaw
 *
 * @公司:益芯金融
 * @作者:xiaochangyou
 * @版本:V1.0
 * @创建时间:2016-7-18 上午9:29:25
 * @描述:TODO[公用的常量]
 */
public interface YxCommonConstant {

    public interface LogParam {
        /**
         * 日志输出开关
         */
        final boolean IS_RELEASE = false;//是否为发布版本,不是则打印
        final boolean IS_UAT_RELEASE = true;//是否为UAT发布版本,是则打印
        final String DEBUG = "DEBUG";
        final String INFO = "INFO";
        final String ERROR = "ERROR";
        final String PRINT_LEVEL = "INFO,ERROR";
    }

    //第一版：1.0.1[201708]
    //第二版：1.0.2[201709]
    //第三版：1.0.3[未定]公信宝无法跳转淘宝app问题修复
    /**
     * sdk版本号
     */
    final String SDK_VERSION = BuildConfig.YMJ_SDK_VERSION;

    /**
     * 相关参数
     */
    public interface Params {
        /**
         * 同盾环境
         */
        String TONGDUN = BuildConfig.YMJ_RELEASE ? FMAgent.ENV_PRODUCTION : FMAgent.ENV_SANDBOX;//生产环境:测试环境
        /**
         * 图片压缩率,100表示不压缩
         */
        final int PICTURE_COMPRESS = 70;
        /**
         * GPS最小刷新时间(分钟)
         */
        final int GPS_MIN_TIME = 5;
        //		/** 通信上传间隔时间-->分钟 第二版废弃*/
        //		final int CONTACTS_DIFFERENCE = 60;
        //		/** 短信上传间隔时间-->天 第二版废弃*/
        //		final int SMS_DIFFERENCE = 30;
    }

    public interface UploadFileName {
        /**
         * 身份证正面A1_yyyyMMddhhmmdd
         */
        final String IMG_A1 = "A1_";
        /**
         * 身份证反面A2_yyyyMMddhhmmdd
         */
        final String IMG_A2 = "A2_";
        /**
         * 手持身份证A3_yyyyMMddhhmmdd
         */
        final String IMG_A3 = "A3_";
        /**
         * 行驶证A4_yyyyMMddhhmmdd
         */
        final String IMG_A4 = "A4_";
        /**
         * 房产证A5_yyyyMMddhhmmdd
         */
        final String IMG_A5 = "A5_";
    }

    public interface ActivityCode {
        public interface RequestCode {
            /**
             * 身份证正面拍照
             */
            final int AUTONYM_CERTIFY_ID_CARD_FRONT = 1001;
            /**
             * 身份证反面拍照
             */
            final int AUTONYM_CERTIFY_ID_CARD_VERSO = 1002;
            /**
             * 手持身份证拍照
             */
            final int AUTONYM_CERTIFY_ID_CARD_HAND = 1003;
            /**
             * 行驶证拍照
             */
            final int CAR_CREDENTIAL = 2001;
            /**
             * 房产证拍照
             */
            final int HOUSE_CREDENTIAL = 3001;
            /**
             * 刷卡付款
             */
            final int SWIPING_CARD_PAY = 4001;
            /**
             * 调用系统通讯录
             **/
            final int SYSTEM_CONTACT = 5001;
            /**
             * 打开通讯录
             */
            final int CONTACTS_REQUEST_CODE = 5001;
        }

        public interface ResultCode {
            /**
             * 刷卡完成回传返回码
             */
            final int SWIPING_CARD_PAY_BACK_RESCODE = 100;
        }
    }

    public interface ACacheKey {
        /**
         * 缓存文件名
         */
        final String ACACHE_NAME = "yxjrCache";
        /**
         * 通讯录缓存名
         */
        final String CONTACTS_K = "ContactsKey";
    }

}
