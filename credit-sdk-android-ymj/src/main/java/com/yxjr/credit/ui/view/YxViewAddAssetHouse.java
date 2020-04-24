package com.yxjr.credit.ui.view;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import com.yxjr.credit.constants.HttpConstant;
import com.yxjr.credit.constants.JsConstant;
import com.yxjr.credit.constants.YxCommonConstant;
import com.yxjr.credit.http.manage.RequestEngine;
import com.yxjr.credit.http.manage.RequestCallBack;
import com.yxjr.credit.http.manage.UploadCallBack;
import com.yxjr.credit.log.YxLog;
import com.yxjr.credit.plugin.YxBmpFactory;
import com.yxjr.credit.ui.YxCallBack;
import com.yxjr.credit.ui.YxEntryActivity;
import com.yxjr.credit.ui.view.YxDialogSelectAddress.OnAddressCListener;
import com.yxjr.credit.ui.view.YxDialogSelectDate.OnSelectDateListener;
import com.yxjr.credit.util.ToastUtil;
import com.yxjr.credit.util.YxCommonUtil;
import com.yxjr.credit.util.YxEmojiFilter;
import com.yxjr.credit.util.YxPictureUtil;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * All rights Reserved, Designed By ClareShaw
 * 
 * @公司:益芯金融
 * @作者:xiaochangyou
 * @版本:V1.0
 * @创建时间:2016-7-18 上午10:59:21
 * @描述:TODO[添加房产页]
 */
public class YxViewAddAssetHouse extends RelativeLayout {

	/** 房产布局文件(XML) */
	private String addAssetHouseID = "yxjr_credit_add_asset_house";
	/** 返回按钮(RelativeLayout) */
	private String houseBackID = "yx_credit_asset_house_Back";
	/** 标题(TextView) */
	private String houseTitleID = "yx_credit_asset_house_Title";
	/** 添加购房省市区(RelativeLayout) */
	private String houseProvinceAndCity_rlID = "yx_credit_asset_house_ProvinceAndCity_rl";
	/** 购房省市区(TextView)[购房地址 省市区] */
	private String houseProvinceAndCityID = "yx_credit_asset_house_ProvinceAndCity";
	/** 购房详细地址(EditText)[输入所购房详细地址] */
	private String houseAddressID = "yx_credit_asset_house_Address";
	/** 购房面积(EditText)[输入购房面积 (㎡)] */
	private String houseAreaID = "yx_credit_asset_house_Area";
	/** 添加购房时间(RelativeLayout) */
	private String houseTime_rlID = "yx_credit_asset_house_Time_rl";
	/** 购房时间(TextView)[购买时间] */
	private String houseTimeID = "yx_credit_asset_house_Time";
	/** 购房价格(EditText)[输入购房价格] */
	private String housePriceID = "yx_credit_asset_house_Price";
	/** 添加房产证按钮(ImageView) */
	private String houseCredentialPicBtnID = "yx_credit_asset_house_CredentialPic_btn";
	/** 显示房产证图片和重拍整块(FrameLayout) */
	private String houseCredentialPicFlID = "yx_credit_asset_house_CredentialPic_fl";
	/** 显示房产证照片(ImageView) */
	private String houseCredentialPicID = "yx_credit_asset_house_CredentialPic";
	/** 重拍房产证照片(TextView)[点击重拍] */
	private String houseCredentialPicAgainID = "yx_credit_asset_house_CredentialPic_Again";
	/** 提交房产信息(Button)[提交] */
	private String houseSubmitID = "yx_credit_asset_house_Submit";
	/** [个人房产信息(可选)] */
	private String houseText1ID = "yx_credit_asset_house_Text1";
	/** [房产证照片] */
	private String houseText2ID = "yx_credit_asset_house_Text2";
	/** [*信息一经提交则无法更改，请仔细核对，以免影响您的申请] */
	private String houseText3ID = "yx_credit_asset_house_Text3";

	/** 布局文件 */
	private View layout;
	/** 返回按钮 */
	private RelativeLayout houseBack;
	/** 标题 */
	private TextView houseTitle;
	/** 添加购房省市区 */
	private RelativeLayout houseProvinceAndCity_rl;
	/** 购房省市区 */
	private TextView houseProvinceAndCity;
	/** 购房详细地址 */
	private EditText houseAddress;
	/** 购房面积 */
	private EditText houseArea;
	/** 添加购房时间 */
	private RelativeLayout houseTime_rl;
	/** 购房时间 */
	private TextView houseTime;
	/** 购房价格 */
	private EditText housePrice;
	/** 添加房产证按钮 */
	private ImageView houseCredentialPicBtn;
	/** 显示房产证图片和重拍整块 */
	private FrameLayout houseCredentialPicFl;
	/** 显示房产证照片 */
	private ImageView houseCredentialPic;
	/** 重拍房产证照片 */
	private TextView houseCredentialPicAgain;
	/* 提交房产信息* */
	private Button houseSubmit;
	/** [个人房产信息(可选)] */
	private TextView houseText1;
	/** [房产证照片] */
	private TextView houseText2;
	/** [*信息一经提交则无法更改，请仔细核对，以免影响您的申请] */
	private TextView houseText3;

	private String houseProvinceAndCity_text = "购房地址 省市区";
	private String houseTime_text = "购买时间";

	private Context mContext;
	private YxEntryActivity mActivity;
	private YxCallBack mCallBack;
	private ResContainer R;
	private String certId;
	private String categoryCode;
	public YxBmpFactory bmpFactory;
	private String houseBmpPath = null;

	public YxViewAddAssetHouse(Activity mActivity, YxCallBack callBack, String certId, String categoryCode) {
		super(mActivity);
		// TODO Auto-generated constructor stub
		this.mActivity = (YxEntryActivity) mActivity;
		this.mContext = this.mActivity;
		this.mCallBack = callBack;
		this.certId = certId;
		this.categoryCode = categoryCode;
		this.R = ResContainer.get(mContext);
		findView();
		init();
		bmpFactory = new YxBmpFactory(mActivity);
	}

	private void findView() {
		// TODO Auto-generated method stub
		layout = inflate(mContext, R.layout(addAssetHouseID), null);
		layout.setLayoutParams(new LayoutParams(-1, -1));
		houseBack = (RelativeLayout) layout.findViewById(R.id(houseBackID));
		houseTitle = (TextView) layout.findViewById(R.id(houseTitleID));
		houseProvinceAndCity_rl = (RelativeLayout) layout.findViewById(R.id(houseProvinceAndCity_rlID));
		houseProvinceAndCity = (TextView) layout.findViewById(R.id(houseProvinceAndCityID));
		houseAddress = (EditText) layout.findViewById(R.id(houseAddressID));
		houseArea = (EditText) layout.findViewById(R.id(houseAreaID));
		houseTime_rl = (RelativeLayout) layout.findViewById(R.id(houseTime_rlID));
		houseTime = (TextView) layout.findViewById(R.id(houseTimeID));
		housePrice = (EditText) layout.findViewById(R.id(housePriceID));
		houseCredentialPicBtn = (ImageView) layout.findViewById(R.id(houseCredentialPicBtnID));
		houseCredentialPicFl = (FrameLayout) layout.findViewById(R.id(houseCredentialPicFlID));
		houseCredentialPic = (ImageView) layout.findViewById(R.id(houseCredentialPicID));
		houseCredentialPicAgain = (TextView) layout.findViewById(R.id(houseCredentialPicAgainID));
		houseSubmit = (Button) layout.findViewById(R.id(houseSubmitID));
		houseText1 = (TextView) layout.findViewById(R.id(houseText1ID));
		houseText2 = (TextView) layout.findViewById(R.id(houseText2ID));
		houseText3 = (TextView) layout.findViewById(R.id(houseText3ID));
	}

	private void init() {
		houseTitle.setText("添加房产信息");
		houseProvinceAndCity.setText(houseProvinceAndCity_text);
		houseAddress.setHint("输入所购房详细地址");
		houseAddress.setFilters(new InputFilter[] { new InputFilter.LengthFilter(50) });
		houseArea.setHint("输入购房面积 (㎡)");
		houseArea.setInputType(InputType.TYPE_CLASS_NUMBER);
		houseArea.setFilters(new InputFilter[] { new InputFilter.LengthFilter(6) });// 设置输入长度为6
		houseTime.setText(houseTime_text);
		housePrice.setHint("输入购房价格(元)");
		housePrice.setInputType(InputType.TYPE_CLASS_NUMBER);
		housePrice.setFilters(new InputFilter[] { new InputFilter.LengthFilter(8) });// 设置输入长度为8
		houseCredentialPicBtn.setImageResource(R.drawable("yx_credit_photograph_btn"));
		houseCredentialPicFl.setVisibility(View.GONE);
		houseSubmit.setText("提交");
		houseText1.setText("个人房产信息(可选) ");
		houseText2.setText("房产证照片");
		houseText3.setText("*信息一经提交则无法更改，请仔细核对，以免影响您的申请");
		setEditTextWatcher();
		setEditOnFocusChange();
		setOnClick();
		setLayoutParams(new LayoutParams(-1, -1));
		this.addView(layout);
	}

	private void setEditTextWatcher() {

		houseAddress.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				String editable = houseAddress.getText().toString();
				String str = stringFilter(editable.toString());
				if (!editable.equals(str)) {
					houseAddress.setText(str);
					houseAddress.setSelection(str.length());// 设置新的光标所在位置
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			};
		});
	}

	private void setEditOnFocusChange() {
		housePrice.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (!hasFocus) {// 没得到焦点时
					housePrice.clearFocus();// 失去焦点
					InputMethodManager mInputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
					mInputMethodManager.hideSoftInputFromWindow(housePrice.getWindowToken(), 0);
				}
			}
		});
		houseArea.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (!hasFocus) {// 没得到焦点时
					houseArea.clearFocus();// 失去焦点
					InputMethodManager mInputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
					mInputMethodManager.hideSoftInputFromWindow(houseArea.getWindowToken(), 0);
				}
			}
		});
		houseAddress.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (!hasFocus) {// 没得到焦点时
					houseAddress.clearFocus();// 失去焦点
					InputMethodManager mInputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
					mInputMethodManager.hideSoftInputFromWindow(houseAddress.getWindowToken(), 0);
				}
			}
		});
	}

	private void setOnClick() {
		houseSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				submitCredential();
			}
		});
		houseCredentialPicAgain.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				bmpFactory.openCamera(YxCommonConstant.ActivityCode.RequestCode.HOUSE_CREDENTIAL);
			}
		});
		houseCredentialPicBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				bmpFactory.openCamera(YxCommonConstant.ActivityCode.RequestCode.HOUSE_CREDENTIAL);
			}
		});
		houseTime_rl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
				if (imm.hideSoftInputFromWindow(housePrice.getWindowToken(), 0)) {// 软键盘已弹出
					imm.showSoftInput(housePrice, 0);
				}
				if (imm.hideSoftInputFromWindow(houseArea.getWindowToken(), 0)) {// 软键盘已弹出
					imm.showSoftInput(houseArea, 0);
				}
				if (imm.hideSoftInputFromWindow(houseAddress.getWindowToken(), 0)) {// 软键盘已弹出
					imm.showSoftInput(houseAddress, 0);
				}
				YxDialogSelectDate mSelectDate = new YxDialogSelectDate(mActivity);
				Calendar c = Calendar.getInstance();
				mSelectDate.setDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
				mSelectDate.show();
				mSelectDate.setSelectDateListener(new OnSelectDateListener() {
					@Override
					public void onClick(String year, String month, String day) {
						// TODO Auto-generated method stub
						houseTime.setText(year + "-" + month + "-" + day);
					}
				});
			}
		});
		houseBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCallBack.removeAssetHouse();
			}
		});
		houseProvinceAndCity_rl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
				if (imm.hideSoftInputFromWindow(housePrice.getWindowToken(), 0)) {// 软键盘已弹出
					imm.showSoftInput(housePrice, 0);
				}
				if (imm.hideSoftInputFromWindow(houseArea.getWindowToken(), 0)) {// 软键盘已弹出
					imm.showSoftInput(houseArea, 0);
				}
				if (imm.hideSoftInputFromWindow(houseAddress.getWindowToken(), 0)) {// 软键盘已弹出
					imm.showSoftInput(houseAddress, 0);
				}
				YxDialogSelectAddress mSelectAddress = new YxDialogSelectAddress(mActivity);
				mSelectAddress.show();
				mSelectAddress.setAddresskListener(new OnAddressCListener() {

					@Override
					public void onClick(String province, String city, String area) {
						// TODO Auto-generated method stub
						houseProvinceAndCity.setText(province + "-" + city + "-" + area);
					}
				});
			}
		});
	}

	public void setHouseCredentialPic(String houseBmpPath) {
		if (houseBmpPath != null) {
			this.houseBmpPath = houseBmpPath;
			this.houseCredentialPic.setImageBitmap(YxPictureUtil.getSmallBitmap(houseBmpPath));
			houseCredentialPicAgain.setText("点击重拍");
			houseCredentialPicFl.setVisibility(View.VISIBLE);
			houseCredentialPicBtn.setVisibility(View.GONE);
		}
		houseCredentialPic.setScaleType(ScaleType.FIT_XY);// 填充图片
	}

	private String stringFilter(String str) throws PatternSyntaxException {
		String regEx = "[^a-zA-Z0-9\u4E00-\u9FA5]";// 只允许字母、数字和汉字
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	private void submitCredential() {
		if (houseProvinceAndCity.getText().toString().equals(houseProvinceAndCity_text)) {
			ToastUtil.showToast(mContext, "请选择" + houseProvinceAndCity_text);
			return;
		}
		if (!YxCommonUtil.isNotBlank(houseAddress.getText().toString())) {
			ToastUtil.showToast(mContext, "请填写购房详细地址");
			return;
		}
		if (!YxCommonUtil.isNotBlank(houseArea.getText().toString())) {
			ToastUtil.showToast(mContext, "请填写购房面积");
			return;
		}
		if (Double.parseDouble(houseArea.getText().toString()) < 1) {
			ToastUtil.showToast(mContext, "请正确填写购房面积");
			return;
		}
		if (YxEmojiFilter.containsEmoji(houseAddress.getText().toString())) {
			ToastUtil.showToast(mContext, "请输入合法数据[包含表情]");
			return;
		}
		if (YxEmojiFilter.containsEmoji(houseArea.getText().toString())) {
			ToastUtil.showToast(mContext, "请输入合法数据[包含表情]");
			return;
		}
		if (YxEmojiFilter.containsEmoji(housePrice.getText().toString())) {
			ToastUtil.showToast(mContext, "请输入合法数据[包含表情]");
			return;
		}
		if (houseTime.getText().toString().equals(houseTime_text)) {
			ToastUtil.showToast(mContext, "请选择" + houseTime_text);
			return;
		}
		if (!YxCommonUtil.isNotBlank(housePrice.getText().toString())) {
			ToastUtil.showToast(mContext, "请输入合法数据[包含表情]");
			return;
		}
		if (Double.parseDouble(housePrice.getText().toString()) < 1) {
			ToastUtil.showToast(mContext, "请正确填写购房价格");
			return;
		}
		if (houseCredentialPicBtn.getVisibility() == View.VISIBLE) {
			ToastUtil.showToast(mContext, "请添加房产证照片");
			return;
		}
		List<Bitmap> bmpList = new ArrayList<Bitmap>();
		if (null != houseBmpPath) {
			bmpList.add(YxPictureUtil.getSmallBitmap(houseBmpPath));
		}

		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File[] files = new File[1];
		try {
			for (int i = 0; i < bmpList.size(); i++) {
				String picName = YxCommonConstant.UploadFileName.IMG_A5 + YxCommonUtil.getCurrentTime2() + ".jpg";
				files[i] = new File(YxPictureUtil.getAlbumDir(), picName);
				fos = new FileOutputStream(files[i]);
				bos = new BufferedOutputStream(fos);
				bmpList.get(i).compress(Bitmap.CompressFormat.JPEG, YxCommonConstant.Params.PICTURE_COMPRESS, bos);// compress是压缩率,50表示压缩50%;如果不压缩是100,表示压缩率为0%
				fos.flush();
				bos.flush();
				YxLog.d("======uploadfiles add file");
				YxLog.d("======uploadfiles Path=" + files[0].getPath());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			YxLog.e("Exception:图片转换错误！");
			ToastUtil.showToast(mContext, "图片转换错误");
			return;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		final File[] uploadFiles = files;
		final DialogLoading dialogLoading = new DialogLoading(mActivity,"上传中...");
		dialogLoading.show();
		new RequestEngine(mContext).upload(certId, uploadFiles, new UploadCallBack() {
			@Override
			public void onSucces(String result) {
				String houseProvinceAndCityValue = houseProvinceAndCity.getText().toString().replace("-", " ");
				String houseAddressValue = houseAddress.getText().toString();
				String houseAreaValue = houseArea.getText().toString();
				String houseTimeValue = houseTime.getText().toString();
				String housePriceValue = housePrice.getText().toString();
				JSONObject params = new JSONObject();
				try {
					params.put("houseDistrict", houseProvinceAndCityValue);
					params.put("houseDetail", houseAddressValue);
					params.put("houseArea", houseAreaValue);
					params.put("houseDate", houseTimeValue);
					params.put("housePrice", housePriceValue);
					params.put("housePhoto", result);
					params.put("categoryCode", categoryCode);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (categoryCode.equals("")) {
					submitHouseAsset(HttpConstant.Request.HOUSE_ASSET_APPROVE, params);
				} else {
					submitHouseAsset(HttpConstant.Request.HOUSE_ASSET_APPROVE_PATCH, params);
				}
				super.onSucces(result);
			}

			@Override
			public void onFailure(String errorCode, String errorMsg) {
				// TODO Auto-generated method stub
				ToastUtil.showToast(mContext, "房产证上传失败！");
				super.onFailure(errorCode, errorMsg);
			}

			@Override
			public void onFinish() {
				for (int i = 0; i < uploadFiles.length; i++) {
					if (uploadFiles[i].getAbsolutePath() != null) {
						YxPictureUtil.deleteTempFile(uploadFiles[i].getAbsolutePath());
					}
				}
				dialogLoading.cancel();
				super.onFinish();
			}

		});
	}

	private void submitHouseAsset(final String serviceId, final JSONObject houseAsset) {
		final DialogLoading dialogLoading = new DialogLoading(mActivity);
		dialogLoading.show();
		new RequestEngine(mContext).execute(serviceId, houseAsset, new RequestCallBack(mContext) {
			@Override
			public void onSucces(String result) {
				dialogLoading.cancel();
				mCallBack.loadUrl(JsConstant.H5_REFRESH, null);
				mCallBack.removeAssetHouse();
				ToastUtil.showToast(mContext, "提交成功");
				YxLog.d("======房产认证成功执行后！");
				// 删除临时原图片
				if (null != houseBmpPath) {
					YxPictureUtil.deleteTempFile(houseBmpPath);
				}
				super.onSucces(result);
			}

			@Override
			public void onFailure(String errorCode, String errorMsg) {
				dialogLoading.cancel();
				super.onFailure(errorCode, errorMsg);
			}
		});
	}
}
