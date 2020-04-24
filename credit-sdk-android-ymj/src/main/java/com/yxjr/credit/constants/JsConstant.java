package com.yxjr.credit.constants;

public interface JsConstant {

	/** 请求超时或网络错误 */
	final String CONNECT_ERROR = "99";
	/** 页面初始化完成 */
	final String INIT_PAGE_FINISH = "000";

	/** H5页面刷新 */
	final String H5_REFRESH = "101";
	/** 消息提示 */
	final String MESSAGE_HINT = "102";
	/** 错误页面重新加载 */
	final String ERROR_RELOAD = "103";
	/** 导航栏返回键 */
	final String GET_BACK_ID = "104";

	/** 返回App首页(关闭SDK) */
	final String BACK_APP = "251";
	/** 调用实名认证页面 */
	final String GO_AUTONYM_CERTIFY = "252";
	/** 调用添加车产页面 */
	final String GO_ADD_ASSET_CAR = "253";
	/** 调用添加房产页面 */
	final String GO_ADD_ASSET_HOUSE = "254";
	/** 关闭影像示例页 */
	final String CLOSE_EXAMPLE = "255";
	/** 活体验证 */
	final String LIVENESS = "256";
	/** 扫描身份证 */
	final String SCAN_IDCARD = "257";
	/** 去刷卡付款||刷卡付款完成 */
	final String SWIPING_CARD_PAY = "262";
	/** 使用合其信征信 */
	final String HQX_CODE = "263";
	/** 跳转手机设置页 */
	final String GO_SETTING = "264";

	/** 服务器通讯||获取通讯得到的信息 */
	final String REQUEST_SERVER = "200";
	/** 获取用户状态||获取用户状态 */
	final String GET_USER_STATUS = "201";
	/** 调用通讯录页面||获取联系人信息 */
	final String GET_CONTACTS = "202";
	/** 获取用户信息（姓名，身份证号,版本） */
	final String GET_USER_MSG = "205";
	/** 获取产品ID||返回存储状态 */
	final String GET_PRODUCT_ID = "207";
	/** 获取权限值 */
	final String GET_PER = "208";
	/** 获取同盾值 */
	final String GET_TONGDUN = "209";

	final String J265 = "265";

	final String J258 = "258";
	/**调用魔蝎SDK*/
	final String J266 = "266";
	/**
	 * 是否安装App
	 */
	String J267 = "267";

	// /** 调用算话sdk */
	// final String GO_SUANHUA_SDK = "260";
	// /** 调用百融sdk */
	// final String GO_BAIRONG_SDK = "261";
	// /** 储存缓存数据 */
	// final String STORAGE_CACHE_DATE = "203";
	// /** 获取缓存数据 */
	// final String GET_CACHE_DATE = "204";
	// /** 清除对应Key缓存 */
	// final String CLEAR_CACHE_DATE = "206";
}
