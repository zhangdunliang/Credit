package com.yxjr.credit.ui;

public abstract interface YxCallBack {

	void initWebView(String UPGRADE);

	/**
	 * 调用JS
	 * 
	 * @param param函数名
	 */
	void loadJsFunction(String function);

	/**
	 * 调用JS
	 * 
	 * @param code请求码
	 * @param data数据
	 */

	void loadUrl(String code, String data);

	void loadQuestionUrl(String code, String data);

	void loadCommonUrl(String code, String data);
	/**
	 * 页面*添加实名认证页
	 * 
	 * @param certId
	 * @param categoryCode
	 */
	void addAutonymCertify(String certId, String categoryCode);

	/**
	 * 页面*移除实名认证页
	 */
	void removeAutonymCertify();

	/**
	 * 页面*添加车产认证页
	 * 
	 * @param certId
	 * @param categoryCode
	 */
	void addAssetCar(String certId, String categoryCode);

	/**
	 * 页面*移除车产认证页
	 */
	void removeAssetCar();

	/**
	 * 页面*添加房产认证页
	 * 
	 * @param certId
	 * @param categoryCode
	 */
	void addAssetHouse(String certId, String categoryCode);

	/**
	 * 页面*移除房产认证页
	 */
	void removeAssetHouse();

	/**
	 * 页面*添加实名认证页内的影像示例页
	 */
	void addExample();

	/**
	 * 页面*移除实名认证页内的影像示例页
	 */
	void removeExample();

	/**
	 * 页面*添加常见问题页面
	 */
	void addQuestion();

	/**
	 * 页面*移除常见问题页
	 */
	void removeQuestion();

	/**
	 * 显示对话框 仅一个确定按钮
	 * 
	 * @param message提示文字
	 * @return Dialog
	 */
	void showDialog(CharSequence message);

	/**
	 * 退出activity
	 */
	void exit();

	/**
	 * 去刷卡支付
	 * 
	 * @param packName包名
	 * @param className类名
	 * @param value传递的数据
	 */
	void swipingCardPay(String packName, String className, String data);

	/**
	 * 跳转合其信
	 */
	void addHqx(String url, String htmlLabel, String type, String title);

	/**
	 * 移除合其信
	 */
	void removeHqx();

	void reloadWebView();

	/**
	 * 跳转至通讯录
	 */
	void goContacts();

	void checkAllPermission();

}
