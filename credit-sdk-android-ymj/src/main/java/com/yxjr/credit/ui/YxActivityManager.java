package com.yxjr.credit.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

/**
 * All rights Reserved, Designed By ClareShaw
 * 
 * @公司:益芯金融
 * @作者:xiaochangyou
 * @版本:V1.0
 * @创建时间:2016-7-15 下午5:25:52
 * @描述:TODO[Activity管理]
 */
public class YxActivityManager {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List<YxBaseActivity> activityList = new ArrayList();

	static void addActivity(YxBaseActivity activity) {
		activityList.add(activity);
	}

	static void removeActivity(Class<?> activityClass) {
		for (int i = 0; i < activityList.size(); i++) {
			if (((YxBaseActivity) activityList.get(i)).getClass().getName().equals(activityClass.getName())) {
				Activity activity = (Activity) activityList.get(i);
				if ((activity != null) && (!activity.isFinishing())) {
					((YxBaseActivity) activityList.get(i)).finish();
				}
			}
		}
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-7-15 下午5:26:10
	 * @描述:TODO[关闭所有Activity]
	 */
	public static void finishAllActivity() {
		int i = 0;
		for (int size = activityList.size(); i < size; i++) {
			if (activityList.get(i) != null) {
				((YxBaseActivity) activityList.get(i)).finish();
			}
		}
		activityList.clear();
	}

	static List<YxBaseActivity> getActivityList() {
		return activityList;
	}

	static void exitAppaction() {
		for (Activity activity : activityList) {
			if ((activity != null) && (!activity.isFinishing())) {
				activity.finish();
				activity = null;
			}
		}
		activityList.clear();
	}

}
