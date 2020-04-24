package com.yxjr.credit.constants;

import com.yxjr.credit.BuildConfig;

public interface HttpConstant {
    String SERVER_URL = BuildConfig.YMD_SERVER_URL;
    String UPLOAD_URL = BuildConfig.YMD_UPLOAD_URL;
    String WEB_URL = BuildConfig.YMD_WEB_URL;
    String EXAMPLE_URL = BuildConfig.YMD_EXAMPLE_URL;
    String SYS_UPDATE_URL = BuildConfig.YMD_SYS_UPDATE_URL;
    String USUAL_QUESTION_URL = BuildConfig.YMD_USUAL_QUESTION_URL;
    String PULL_TIME_URL = BuildConfig.YMD_PULL_TIME_URL;

    //
    int CONN_TIME_OUT = 120;//设置到http里面会出现连不上服务，待具体查明。
    int BUFFER_SIZE = 1024;

    /**
     * http请求码
     */
    interface Request {
        /**
         * 获取产品
         */
        String GET_PRODUCT = "1000";
        /**
         * 初始化Token
         */
        String INIT_SESSION_TOKEN = "1010";
        /**
         * 获取用户状态或登陆状态
         */
        String GET_USER_STATE = "1011";
        /**
         * 心跳
         */
        String HEART_BEAT = "1020";
        /**
         * 同盾心跳
         */
        String BLACK_BOX_HEART_BEAT = "1021";
        /**
         * 实名认证－提交验证 || 补件
         */
        String NAME_ASSET_APPROVE = "2020";
        /**
         * 车产认证－提交验证 || 补件
         */
        String CAR_ASSET_APPROVE = "2027";
        /**
         * 房产认证－提交验证 || 补件
         */
        String HOUSE_ASSET_APPROVE = "2028";
        /**
         * 获取开关参数
         */
        String GET_PARAM = "2058";
        /**
         * 补件实名认证－提交验证
         */
        String NAME_ASSET_APPROVE_PATCH = "2061";
        /**
         * 补件车产认证－提交验证
         */
        String CAR_ASSET_APPROVE_PATCH = "2062";
        /**
         * 补件房产认证－提交验证
         */
        String HOUSE_ASSET_APPROVE_PATCH = "2063";
        /**
         * 提交觐见
         */
        String SUBMIT = "2098";
        /**
         * 记录用户操作
         */
        String RECORD = "2102";
        /**
         * 发送通讯录
         */
        String SEND_CONTACTS = "3010";
        /**
         * 发送短信
         */
        String SEND_SMS = "3011";
        /**
         * 发送浏览器访问记录
         */
        String SEND_BROWSER_HISTORY = "3012";
        /**
         * 发送通话记录
         */
        String SEND_CALL_LOG = "3013";
        /**
         * 发送app列表
         */
        String SEND_APP_LIST = "3014";
        /**
         * 发送照片信息
         */
        String SEND_IMG_EXIF = "3015";
        /**
         * 放款确认
         */
        String DEPOSIT_CONFIRM = "5020";
        /**
         * 查询账户信息
         */
        String GET_ACCOUNT_STATE = "5002";
        /**
         * 查询合同列表
         */
        String GET_CONTRACT_LIST = "5021";

        /**
         * 用户身份证信息，扫描身份证
         */
        String IDENTITY_INFO = "2103";
        /**
         * 用户人脸信息，活体验证
         */
        String FACE_INFO = "2106";
        /**
         * 补件，用户身份证信息，扫描身份证
         */
        String PATCH_IDENTITY_INFO = "2107";
        /**
         * 补件，用户人脸信息，活体验证
         */
        String PATCH_FACE_INFO = "2109";
    }

    /**
     * http响应码
     */
    interface Response {
        /**
         * 成功
         */
        String SUCCEED = "0000";
        /**
         * 登录过期，请重新登录
         */
        String LOGIN_DATED = "0004";
        /**
         * 您已经在另一台设备上登录，请重新登录
         */
        String LOGIN_REPEAT = "0005";
        /**
         * AES解密错误
         */
        String AES_DECODE_ERROR = "0014";
    }

}
