package com.yxjr.credit.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.yxjr.credit.constants.YxCommonConstant;

import android.annotation.SuppressLint;
import android.os.Environment;

/**
 * All rights Reserved, Designed By ClareShaw
 * 
 * @公司:益芯金融
 * @作者:xiaochangyou
 * @版本:V1.0
 * @创建时间:2016-9-9 上午10:05:36
 * @描述:TODO[日志输出到文件]
 */
public class YxLogFactory {

	public boolean isNeedLog = true;
	private PrintWriter printWriter;

	private static YxLogFactory instance = null;

	private static synchronized void syncInit() {
		if (instance == null) {
			instance = new YxLogFactory();
		}
	}

	public static YxLogFactory getInstance() {
		if (instance == null) {
			syncInit();
		}
		return instance;
	}

	public YxLogFactory() {
		createLogPrintWriter();
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-9-9 上午10:06:05
	 * @描述:TODO[创建日志输出流]
	 */
	@SuppressLint("SimpleDateFormat")
	private void createLogPrintWriter() {
		File fileDir = getLogFileDir();// 获取日志文件目录
		if (isNeedLog && fileDir != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");// 使用当前时间的年月日作为日志文件名
			deleteFiles(fileDir, new Date());
			String fileName = formatter.format(new Date()) + ".log";
			File logfile = new File(fileDir, fileName);// 创建或者打开日志文件
			try {
				printWriter = new PrintWriter(new FileWriter(logfile, true));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-9-9 上午10:06:13
	 * @描述:TODO[添加日志]
	 * @param time日志时间
	 * @param level日志级别
	 * @param loc打印的代码位置
	 *            [类#方法#行数]
	 * @param log日志
	 */
	@SuppressLint("SimpleDateFormat")
	public void addLog(String time, String level, String loc, String log) {
		if (YxCommonConstant.LogParam.PRINT_LEVEL.contains(level)) {
			if (isNeedLog) {
				if (printWriter != null) {
					printWriter.print(time + " " + level + " " + loc + " " + log + "\r\n");
					printWriter.flush();
				}
			}
		}
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-9-9 上午10:06:50
	 * @描述:TODO[只存在sd卡，无sd卡不存储日志]
	 * @return 日志目录
	 */
	private File getLogFileDir() {
		File fileDir = null;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// 如果SD卡存在
			fileDir = new File(Environment.getExternalStorageDirectory(), "/YxCredit/log");
		}
		if (fileDir != null) {
			if (!fileDir.exists()) {// 如果目录不存在
				fileDir.mkdirs();// 新建目录
			}
		}
		return fileDir;

	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-9-9 上午10:07:01
	 * @描述:TODO[只保留一个Log文件，不是当天的全部删掉]
	 * @param fileDir日志存储目录
	 * @param date当天日期
	 */
	@SuppressLint("SimpleDateFormat")
	private void deleteFiles(File fileDir, Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		if (date == null || fileDir == null) {
			return;
		}
		if (fileDir.listFiles() == null) {
			return;
		}
		for (File file : fileDir.listFiles()) {
			String fileName = file.getName();
			String fileTime = fileName.substring(0, fileName.indexOf('.'));
			try {
				Date createTime = formatter.parse(fileTime);
				if (createTime.getTime() != date.getTime()) {
					file.delete();
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
}
