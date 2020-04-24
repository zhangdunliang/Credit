package com.yxjr.credit.log;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.yxjr.credit.constants.YxCommonConstant;

import android.annotation.SuppressLint;
import android.util.Log;

/**
 * All rights Reserved, Designed By ClareShaw
 * 
 * @公司:益芯金融
 * @作者:xiaochangyou
 * @版本:V1.0
 * @创建时间:2016-7-18 上午11:56:02
 * @描述:TODO[日志输出类]
 */
public class YxLog {

	/** 是否为发布版本,不是则打印 */
	private static final boolean IS_RELEASE = YxCommonConstant.LogParam.IS_RELEASE;
	public static final String TAG_LOGCAT = "YxjrCredit";

	public static void d(String msg) {
		if (!IS_RELEASE) {
			Log.d(_FILE_(), "[" + getFileMethodLine() + "]" + msg);
		} else {
			YxLogFactory.getInstance().addLog(_TIME_(), YxCommonConstant.LogParam.DEBUG, getFileMethodLine(), msg);
		}
	}

	public static void d(String TAG, String msg) {
		if (!IS_RELEASE) {
			Log.d(TAG, "[" + getFileMethodLine() + "]" + msg);
		} else {
			YxLogFactory.getInstance().addLog(_TIME_(), YxCommonConstant.LogParam.DEBUG, getFileMethodLine(), msg);
		}
	}

	public static void i(String msg) {
		if (!IS_RELEASE) {
			Log.i(_FILE_(), "[" + getFileMethodLine() + "]" + msg);
		} else {
			YxLogFactory.getInstance().addLog(_TIME_(), YxCommonConstant.LogParam.INFO, getFileMethodLine(), msg);
		}
	}

	public static void i(String TAG, String msg) {
		if (!IS_RELEASE) {
			Log.i(TAG, "[" + getFileMethodLine() + "]" + msg);
		} else {
			YxLogFactory.getInstance().addLog(_TIME_(), YxCommonConstant.LogParam.INFO, getFileMethodLine(), msg);
		}
	}

	public static void e(String msg) {
		if (!IS_RELEASE) {
			Log.e(_FILE_(), "[" + getFileMethodLine() + "]" + msg);
		} else {
			YxLogFactory.getInstance().addLog(_TIME_(), YxCommonConstant.LogParam.ERROR, getFileMethodLine(), msg);
		}
	}

	public static void e(String TAG, String msg) {
		if (!IS_RELEASE) {
			Log.e(TAG, "[" + getFileMethodLine() + "]" + msg);
		} else {
			YxLogFactory.getInstance().addLog(_TIME_(), YxCommonConstant.LogParam.ERROR, getFileMethodLine(), msg);
		}
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-9-9 下午3:46:47
	 * @描述:TODO[获取文件名#方法名#行数]
	 * @return String
	 */
	private static String getFileMethodLine() {
		StackTraceElement traceElement = new Exception().getStackTrace()[2];
		StringBuffer toStringBuffer = new StringBuffer("[").append(traceElement.getFileName()).append("#").append(traceElement.getMethodName()).append("#").append(traceElement.getLineNumber()).append("]");
		return toStringBuffer.toString();
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-9-9 下午3:46:14
	 * @描述:TODO[获取方法名#行数]
	 * @return String
	 */
	@SuppressWarnings("unused")
	private static String getMethodLine() {
		StackTraceElement traceElement = new Exception().getStackTrace()[2];
		StringBuffer toStringBuffer = new StringBuffer("[").append(traceElement.getMethodName()).append("#").append(traceElement.getLineNumber()).append("]");
		return toStringBuffer.toString();
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-9-9 下午3:48:10
	 * @描述:TODO[获取文件名]
	 * @return String
	 */
	private static String _FILE_() {
		StackTraceElement traceElement = new Exception().getStackTrace()[2];
		return traceElement.getFileName();
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-9-9 下午3:47:51
	 * @描述:TODO[获取当前时间]
	 * @return String
	 */
	@SuppressLint("SimpleDateFormat")
	private static String _TIME_() {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return sdf.format(now);
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-9-9 下午3:47:25
	 * @描述:TODO[获取方法名]
	 * @return String
	 */
	@SuppressWarnings("unused")
	private static String _FUNC_() {
		StackTraceElement traceElement = new Exception().getStackTrace()[1];
		return traceElement.getMethodName();
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-9-9 下午3:47:40
	 * @描述:TODO[获取行数]
	 * @return int
	 */
	@SuppressWarnings("unused")
	private static int _LINE_() {
		StackTraceElement traceElement = new Exception().getStackTrace()[1];
		return traceElement.getLineNumber();
	}
}
