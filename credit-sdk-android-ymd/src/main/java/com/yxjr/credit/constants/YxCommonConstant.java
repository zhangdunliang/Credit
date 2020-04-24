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

    interface LogParam {
        /**
         * 日志输出开关
         */
        boolean IS_RELEASE = false;//是否为发布版本,不是则打印
        String DEBUG = "DEBUG";
        String INFO = "INFO";
        String ERROR = "ERROR";
        String PRINT_LEVEL = "INFO,ERROR";
    }

    /**
     * sdk版本号
     */
    String SDK_VERSION = BuildConfig.YMD_SDK_VERSION;

    /**
     * 相关参数
     */
    interface Params {
        /**
         * 同盾环境
         */
        String TONGDUN = BuildConfig.YMD_RELEASE ? FMAgent.ENV_PRODUCTION : FMAgent.ENV_SANDBOX;//生产环境:测试环境

        /**
         * 魔蝎API Key
         */
        String MOXIE = BuildConfig.YMD_RELEASE ? "938fcfba7378487f80a38fc5927ecb95" : "dc95a5dc34b84cc194f0501c804bfa0a";//生产环境:测试环境
        /**
         * 图片压缩率,100表示不压缩
         */
        int PICTURE_COMPRESS = 70;
        /**
         * GPS最小刷新时间(分钟)
         */
        int GPS_MIN_TIME = 5;
        //		/** 通信上传间隔时间-->60分钟 第二版废弃*/
        //		/** 短信上传间隔时间-->30天 第二版废弃*/
    }

    interface UploadFileName {
        /**
         * 身份证正面A1_yyyyMMddhhmmdd
         */
        String IMG_A1 = "A1_";
        /**
         * 身份证反面A2_yyyyMMddhhmmdd
         */
        String IMG_A2 = "A2_";
        /**
         * 手持身份证A3_yyyyMMddhhmmdd
         */
        String IMG_A3 = "A3_";
        /**
         * 行驶证A4_yyyyMMddhhmmdd
         */
        String IMG_A4 = "A4_";
        /**
         * 房产证A5_yyyyMMddhhmmdd
         */
        String IMG_A5 = "A5_";
    }

    interface ActivityCode {
        interface RequestCode {
            /**
             * 身份证正面拍照
             */
            int AUTONYM_CERTIFY_ID_CARD_FRONT = 1001;
            /**
             * 身份证反面拍照
             */
            int AUTONYM_CERTIFY_ID_CARD_VERSO = 1002;
            /**
             * 手持身份证拍照
             */
            int AUTONYM_CERTIFY_ID_CARD_HAND = 1003;
            /**
             * 行驶证拍照
             */
            int CAR_CREDENTIAL = 2001;
            /**
             * 房产证拍照
             */
            int HOUSE_CREDENTIAL = 3001;
            /**
             * 刷卡付款
             */
            int SWIPING_CARD_PAY = 4001;
            /**
             * 调用系统通讯录
             **/
            int SYSTEM_CONTACT = 5001;
            /**
             * 打开通讯录
             */
            int CONTACTS_REQUEST_CODE = 5001;
            /**
             * webveiw打开系统相机
             */
            int OPEN_SYS_CRMERA = 6001;
            /**
             * webview打开系统相册
             */
            int OPEN_SYS_GALLERY = 6002;
            /**
             * webview打开系统文件
             */
            int OPEN_SYS_FILE = 6003;
        }

        interface ResultCode {
            /**
             * 刷卡完成回传返回码
             */
            int SWIPING_CARD_PAY_BACK_RESCODE = 100;

        }
    }

}
