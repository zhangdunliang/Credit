package com.yxjr.credit.util;

public class StringUtil {

	public static boolean isEmpty(CharSequence str) {
		if (str == null || str.length() == 0)
			return true;
		else
			return false;
	}

	public static String laterFour(String str) {
		if (isEmpty(str)) {
			throw new NullPointerException("laterFour() params is null");
		}
		if (str.length() >= 4) {// 判断是否长度大于等于4
			return str.substring(str.length() - 4, str.length());
		} else {
			return str;
		}
	}
}
