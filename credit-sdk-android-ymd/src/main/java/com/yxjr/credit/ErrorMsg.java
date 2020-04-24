package com.yxjr.credit;

import java.util.HashMap;
import java.util.Map;

public class ErrorMsg {

	/** 成功 */
	final String CODE_00000 = "00000";
	/** 未知 */
	final String CODE_10000 = "10000";
	/** 未初始化 */
	final String CODE_10001 = "10001";
	/** Bundle为空 */
	final String CODE_10002 = "10002";
	/** 初始化失败 */
	final String CODE_10003 = "10003";
	/** key为空 */
	final String CODE_20001 = "20001";
	/** 机构ID为空 */
	final String CODE_20002 = "20002";
	/** 身份证号为空 */
	final String CODE_20003 = "20003";
	/** 用户姓名为空 */
	final String CODE_20004 = "20004";
	/** 手机号为空 */
	final String CODE_20005 = "20005";
	/** 支付的Activity包名为空 */
	final String CODE_20006 = "20006";
	/** 支付的Activity类名为空 */
	final String CODE_20007 = "20007";
	/***/
	private Map<String, String> ERROR_MESSAGE_INFO = new HashMap<String, String>();

	public ErrorMsg() {
		initErrorMsg(this);
	}

	public void initErrorMsg(ErrorMsg errorMsg) {
		errorMsg.addMessageInfo(errorMsg.CODE_00000, "成功");
		errorMsg.addMessageInfo(errorMsg.CODE_10000, "未知");
		errorMsg.addMessageInfo(errorMsg.CODE_10001, "未初始化");
		errorMsg.addMessageInfo(errorMsg.CODE_10002, "Bundle为空");
		errorMsg.addMessageInfo(errorMsg.CODE_10003, "初始化失败");
		errorMsg.addMessageInfo(errorMsg.CODE_20001, "key为空");
		errorMsg.addMessageInfo(errorMsg.CODE_20002, "机构号为空");
		errorMsg.addMessageInfo(errorMsg.CODE_20003, "身份证号为空");
		errorMsg.addMessageInfo(errorMsg.CODE_20004, "用户姓名为空");
		errorMsg.addMessageInfo(errorMsg.CODE_20005, "手机号为空");
		errorMsg.addMessageInfo(errorMsg.CODE_20006, "支付的Activity包名为空");
		errorMsg.addMessageInfo(errorMsg.CODE_20007, "支付的Activity类名为空");
	}

	public String getMessageInfo(String errorCode) {
		if (ERROR_MESSAGE_INFO.containsKey(errorCode)) {
			return ERROR_MESSAGE_INFO.get(errorCode);
		} else {
			return ERROR_MESSAGE_INFO.get(CODE_10000);
		}
	}

	public void addMessageInfo(String errorCode, String errorMsg) {
		ERROR_MESSAGE_INFO.put(errorCode, errorMsg);
	}

}
