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
import com.yxjr.credit.util.StringUtil;
import com.yxjr.credit.util.YxCommonUtil;
import com.yxjr.credit.util.YxStoreUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.provider.CallLog;

public class CalllogGrab extends Grab {

    private Context mContext;

    public CalllogGrab(Context context) {
        this.mContext = context;
    }

    @Override
    public void upload() {
        new Task().execute();
    }

    private class Task extends NoConfineAsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                int interval = getInterval(mContext, SpConstant.CASQ);
                String callTime = getLastTime(mContext, SpConstant.CA_LASTTIME);
                boolean surpass = isSurpass(mContext, SpConstant.CA_IS);
                if (interval != 0 && surpass) {
                    JSONArray callLogArray = getData(callTime);
                    if (null != callLogArray) {
                        final String idCard = YxStoreUtil.get(mContext, SpConstant.PARTNER_ID_CARD_NUM);
                        final String phoneNo = YxStoreUtil.get(mContext, SpConstant.PARTNER_PHONENUMBER);
                        batchJSONArray(callLogArray, interval, new OnBatchListener() {

                            @Override
                            public void onBatch(JSONArray array) {
                                try {
                                    JSONObject callLogInfo = new JSONObject();
                                    callLogInfo.put("cert", idCard);
                                    callLogInfo.put("mobileNo", phoneNo);
                                    callLogInfo.put("callLogInfo", array);
                                    sendServer(mContext, HttpConstant.Request.SEND_CALL_LOG, callLogInfo);
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

    @SuppressLint("SimpleDateFormat")
    @Override
    public JSONArray getData(String time) {
        String selection = null;
        if (YxCommonUtil.isNotBlank(time)) {
            long record = YxCommonUtil.dateStringToLong(time);
            selection = CallLog.Calls.DATE + " >'" + record + "'";//查询条件
        }
        JSONArray callLogInfo = new JSONArray();
        Cursor callLogCursor = null;
        try {
            String[] projection = new String[]{CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.CACHED_NAME, CallLog.Calls.DURATION};
            callLogCursor = mContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, selection, null, "date desc");
            if (callLogCursor != null && callLogCursor.moveToFirst()) {
                do {
                    //联系号码
                    String callMobile = callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.NUMBER));
                    //通话类型
                    String callType;
                    switch (Integer.parseInt(callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.TYPE)))) {
                        case CallLog.Calls.INCOMING_TYPE:
                            callType = "1";//呼入
                            break;
                        case CallLog.Calls.OUTGOING_TYPE:
                            callType = "2";//呼出
                            break;
                        case CallLog.Calls.MISSED_TYPE:
                            callType = "3";//未接
                            break;
                        default:
                            callType = "4";//挂断
                            break;
                    }
                    //通话时间
                    String callTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.parseLong(callLogCursor.getString(callLogCursor.getColumnIndexOrThrow(CallLog.Calls.DATE)))));
                    //通话号码名字(通讯录有的话)
                    String callName = callLogCursor.getString(callLogCursor.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME));
                    //通话时长(单位:s)
                    String callDuration = callLogCursor.getString(callLogCursor.getColumnIndexOrThrow(CallLog.Calls.DURATION));
                    JSONObject callLog = new JSONObject();
                    callLog.put("callName", subStringByByte(callName, 90));
                    callLog.put("callMobile", subStringByByte(callMobile, 20));
                    callLog.put("callDuration", subStringByByte(verifyCallDuration(callDuration), 20));
                    callLog.put("callTime", subStringByByte(callTime, 20));
                    callLog.put("callType", callType);
                    callLogInfo.put(callLog);
                } while (callLogCursor.moveToNext());
                if (callLogCursor != null) {
                    callLogCursor.close();
                }
            }
        } catch (SQLiteException ex) {
            YxLog.e("SQLiteException:" + ex);
            ex.printStackTrace();
        } catch (SecurityException se) {
            // TODO: handle exception
            YxLog.e("SecurityException:without permission android.permission.READ_CALL_LOG or android.permission.WRITE_CALL_LOG" + se);
        } catch (Exception e) {
            // TODO: handle exception
            YxLog.e("Exception:" + e);
            e.printStackTrace();
        } finally {
            if (null != callLogCursor) {
                callLogCursor.close();
            }
        }
        return callLogInfo;
    }

    /**
     * 通话时长特定格式转换【xx时xx分xx秒|xx分xx秒|xx:xx:xx|xx:xx】
     * 其他格式原样返回
     *
     * @param callDuration
     * @return
     */
    private String verifyCallDuration(String callDuration) {
        /**时*/
        int h = 0;
        /**分*/
        int m = 0;
        /**秒*/
        int f = 0;
        try {
            if (!StringUtil.isEmpty(callDuration)) {
                if (callDuration.contains("时") || callDuration.contains("分") || callDuration.contains("秒")) {//是否存在 时分秒// 00时00分00秒 或 00分00秒
                    if (callDuration.contains("时") && callDuration.contains("分") && callDuration.contains("秒")) {//00时00分00秒
                        String sh = null;//时
                        String sm = null;//分
                        String sf = null;//秒
                        if (callDuration.length() == 9) {
                            sh = callDuration.substring(0, callDuration.indexOf("时"));
                            sm = callDuration.substring(callDuration.indexOf("时") + 1, callDuration.indexOf("分"));
                            sf = callDuration.substring(callDuration.indexOf("分") + 1, callDuration.indexOf("秒"));
                        }
                        if (sh != null && sh.length() == 2 && sm != null && sm.length() == 2 && sf != null && sf.length() == 2) {
                            h = Integer.parseInt(sh);
                            m = Integer.parseInt(sm);
                            f = Integer.parseInt(sf);
                        }
                    } else if (callDuration.contains("分") && callDuration.contains("秒")) {//00分00秒
                        String sm = null;//分
                        String sf = null;//秒
                        if (callDuration.length() == 6) {
                            sm = callDuration.substring(0, callDuration.indexOf("分"));
                            sf = callDuration.substring(callDuration.indexOf("分") + 1, callDuration.indexOf("秒"));
                        }
                        if (sm != null && sm.length() == 2 && sf != null && sf.length() == 2) {
                            m = Integer.parseInt(sm);
                            f = Integer.parseInt(sf);
                        }
                    }
                } else if (callDuration.contains(":")) {//是否存在 : //00：00：00或00：00
                    int count, frist, two;
                    count = frist = two = 0;
                    for (int i = 0; i < callDuration.length(); i++) {
                        if (callDuration.charAt(i) == ':') {
                            count++;
                            if (frist == 0)
                                frist = i;
                            if (frist != 0)
                                two = i;
                        }
                    }
                    if (count == 2) {//00：00：00
                        String sh = null;//时
                        String sm = null;//分
                        String sf = null;//秒
                        if (callDuration.length() == 8) {
                            sh = callDuration.substring(0, frist);
                            sm = callDuration.substring(frist + 1, two);
                            sf = callDuration.substring(two + 1, callDuration.length());
                        }
                        if (sh != null && sh.length() == 2 && sm != null && sm.length() == 2 && sf != null && sf.length() == 2) {
                            h = Integer.parseInt(sh);
                            m = Integer.parseInt(sm);
                            f = Integer.parseInt(sf);
                        }
                    } else if (count == 1) {//00：00
                        String sm = null;//分
                        String sf = null;//秒
                        if (callDuration.length() == 5) {
                            sm = callDuration.substring(0, callDuration.indexOf(":"));
                            sf = callDuration.substring(callDuration.indexOf(":") + 1, callDuration.length());
                        }
                        if (sm != null && sm.length() == 2 && sf != null && sf.length() == 2) {
                            m = Integer.parseInt(sm);
                            f = Integer.parseInt(sf);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            YxLog.e("callDuration change error for call ");
            h = m = f = 0;
        }
        if (h != 0 || m != 0 || f != 0) {
            callDuration = String.valueOf(h * 60 * 60 + m * 60 + f);
        }
        return callDuration;
    }

}
