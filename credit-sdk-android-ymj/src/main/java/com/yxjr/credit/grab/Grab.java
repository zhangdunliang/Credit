package com.yxjr.credit.grab;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yxjr.credit.http.manage.RequestCallBack;
import com.yxjr.credit.http.manage.RequestEngine;
import com.yxjr.credit.log.YxLog;
import com.yxjr.credit.util.StringUtil;
import com.yxjr.credit.util.YxCommonUtil;
import com.yxjr.credit.util.YxStoreUtil;

import android.content.Context;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Grab {

	/**
	 * 上传数据
	 */
	abstract void upload();

	/**
	 * 查询或获取数据
	 *
	 * @return 以JSONArray格式返回
	 */
	protected JSONArray getData() {
		throw new UnsupportedOperationException("Don't have the time parameter");
	}

	/**
	 * 查询或获取数据
	 * 一般增量查询或获取数据时使用
	 *
	 * @return 以JSONArray格式返回
	 */
	protected JSONArray getData(String time) {
		throw new UnsupportedOperationException("Don't use time parameter");
	}

	/**
	 * 发送数据值Server
	 *
	 * @param context
	 * @param serviceId  接口号
	 * @param jsonObject 数据
	 */
	protected void sendServer(Context context, String serviceId, JSONObject jsonObject) {
		String strJson = replaceEmojiToStar(jsonObject.toString());
		try {
			jsonObject = new JSONObject(strJson);
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}
		new RequestEngine(context, false).execute(serviceId, jsonObject, new RequestCallBack(context) {
			@Override
			public void onSucces(String result) {
				super.onSucces(result);
			}
		});
	}

	/**
	 * 获取分批上传条数
	 *
	 * @param context
	 * @param key     缓存key
	 * @return 条数【默认：0】
	 */
	protected int getInterval(Context context, String key) {
		String intervalS = YxStoreUtil.get(context, key);
		int intervalI = 0;
		if (YxCommonUtil.isNotBlank(intervalS)) {
			intervalI = Integer.parseInt(intervalS);
		}
		return intervalI;
	}

	/**
	 * 获取上次上传的最后时间【一般为增量获取数据时使用】
	 *
	 * @param context
	 * @param key     缓存key
	 * @return
	 */
	protected String getLastTime(Context context, String key) {
		return YxStoreUtil.get(context, key);
	}

	/**
	 * 对JSONArray数据分批处理
	 *
	 * @param array         待处理JSONArray数据
	 * @param interval      每批的数量
	 * @param batchListener 返回每批的JSONArray数据
	 */
	protected void batchJSONArray(JSONArray array, int interval, OnBatchListener batchListener) {
		if (interval == 0) {
			batchListener.onBatch(array);
			return;
		}
		JSONArray tempJSONArray = null;
		tempJSONArray = new JSONArray();
		for (int i = 0; i < array.length(); i++) {
			Object object;
			try {
				object = array.get(i);
				tempJSONArray.put(object);
				int tempI = i + 1;
				if (tempI > 0 && tempI % interval == 0) {//不是第0条数据 且 能被interval整除 或 到了最后1条数据
					batchListener.onBatch(tempJSONArray);
					tempJSONArray = null;
					tempJSONArray = new JSONArray();
				} else {
					if (array.length() - 1 % interval != 0 && i == array.length() - 1 || array.length() == 1) {
						batchListener.onBatch(tempJSONArray);
						tempJSONArray = null;
						tempJSONArray = new JSONArray();
					}
				}
			} catch (JSONException e) {
				YxLog.e("jsonarray batch error");
				e.printStackTrace();
			}
		}
	}

	protected static String correctDate(long date) {
		long supportTime = YxCommonUtil.dateStringToLong("20000101000000");//2000-01-01 00:00:00 == 946656000000
		if (date < supportTime) {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(supportTime));
		} else {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(date));
		}
	}

	/**
	 * 按字节截取字符串
	 *
	 * @param string   需要截取的字符串
	 * @param endIndex 字节长度最终位置
	 * @return
	 */
	protected String subStringByByte(String string, int endIndex) {
		String str = replaceEmojiToStar(string);
		try {
			String encoding = "utf-8";

			if (string.getBytes(encoding).length < endIndex) {
				return str;
			}

			//截取到当前字符时的字节数
			int currentByteLen = 0;
			//应当截取到的字符的长度
			int stringLen = 0;

			for (int i = 0; i < str.length(); i++) {
				String tempString = str.substring(i, i + 1);
				int stringByteLen = tempString.getBytes(encoding).length;
				currentByteLen += stringByteLen;
				if (currentByteLen < endIndex || currentByteLen == endIndex) {
					stringLen++;
				} else {
					break;
				}
			}
			return str.substring(0, stringLen);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			if (str.length() > endIndex) {
				str = str.substring(0, endIndex / 2);
			}
			return str;
		}
	}

	/**
	 * 将字符串中的Emoji表情替换为*
	 *
	 * @param source
	 * @return
	 */

	protected String replaceEmojiToStar(String source) {
		String res = "";
		if (StringUtil.isEmpty(source)) {
			return res;
		}
		if (!containsEmoji(source)) {
			return source;
		} else {
			int len = source.length();
			char[] dest = new char[len];
			for (int i = 0; i < len; i++) {
				char codePoint = source.charAt(i);
				if (isNotEmojiCharacter(codePoint)) {
					dest[i] = codePoint;
				} else {
					dest[i] = '*';
				}
			}
			res = String.valueOf(dest);
		}
		return res;
	}

	/**
	 * 检测是否有emoji字符
	 *
	 * @param source
	 * @return 一旦含有就抛出
	 */
	private boolean containsEmoji(String source) {
		if (StringUtil.isEmpty(source)) {
			return false;
		}
		int len = source.length();
		for (int i = 0; i < len; i++) {
			char codePoint = source.charAt(i);
			if (!isNotEmojiCharacter(codePoint)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否是非Emoji字符之外的正常字符，正常字符返回true
	 *
	 * @param codePoint
	 * @return
	 */
	private boolean isNotEmojiCharacter(char codePoint) {
		return (codePoint == 0x0) ||
				(codePoint == 0x9) ||
				(codePoint == 0xA) ||
				(codePoint == 0xD) ||
				((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
				((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
				((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
	}

	/**
	 * 是否上传
	 *
	 * @param context
	 * @param key     缓存key
	 * @return true 上传
	 */
	public boolean isSurpass(Context context, String key) {
		if (YxStoreUtil.get(context, key).equals("Y")) {
			return true;
		}
		return false;
	}

	public interface OnBatchListener {
		/**
		 * @param array 已按照分批数量处理好的分批数据
		 */
		void onBatch(JSONArray array);
	}
}
