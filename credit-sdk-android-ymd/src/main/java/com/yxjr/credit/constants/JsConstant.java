package com.yxjr.credit.constants;

public interface JsConstant {

    /**
     * 请求超时或网络错误
     */
    String CONNECT_ERROR = "99";
    /**
     * 页面初始化完成
     */
    String INIT_PAGE_FINISH = "000";

    /**
     * H5页面刷新
     */
    String H5_REFRESH = "101";
    /**
     * 消息提示
     */
    String MESSAGE_HINT = "102";
    /**
     * 错误页面重新加载
     */
    String ERROR_RELOAD = "103";
    /**
     * 导航栏返回键
     */
    String GET_BACK_ID = "104";

    /**
     * 返回App首页(关闭SDK)
     */
    String BACK_APP = "251";
    /**
     * 调用实名认证页面
     */
    String GO_AUTONYM_CERTIFY = "252";
    /**
     * 调用添加车产页面
     */
    String GO_ADD_ASSET_CAR = "253";
    /**
     * 调用添加房产页面
     */
    String GO_ADD_ASSET_HOUSE = "254";
    /**
     * 关闭影像示例页
     */
    String CLOSE_EXAMPLE = "255";
    /**
     * 活体验证
     */
    String LIVENESS = "256";
    /**
     * 扫描身份证
     */
    String SCAN_IDCARD = "257";
    /**
     * 去刷卡付款||刷卡付款完成
     */
    String SWIPING_CARD_PAY = "262";
    /**
     * 使用合其信征信
     */
    String HQX_CODE = "263";
    /**
     * 跳转手机设置页
     */
    String GO_SETTING = "264";

    /**
     * 服务器通讯||获取通讯得到的信息
     */
    String REQUEST_SERVER = "200";
    /**
     * 获取用户状态||获取用户状态
     */
    String GET_USER_STATUS = "201";
    /**
     * 调用通讯录页面||获取联系人信息
     */
    String GET_CONTACTS = "202";
    /**
     * 获取用户信息（姓名，身份证号,版本）
     */
    String GET_USER_MSG = "205";
    /**
     * 获取产品ID||返回存储状态
     */
    String GET_PRODUCT_ID = "207";
    /**
     * 获取权限值
     */
    String GET_PER = "208";
    /**
     * 获取同盾值
     */
    String GET_TONGDUN = "209";

    String J265 = "265";

    String J258 = "258";
    /**
     * 调用魔蝎SDK
     */
    String J266 = "266";
    /**
     * 是否安装App
     */
    String J267 = "267";

}
