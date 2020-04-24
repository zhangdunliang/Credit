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

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.InputFilter;
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
import com.yxjr.credit.ui.view.YxDialogSelectDate.OnSelectDateListener;
import com.yxjr.credit.util.ToastUtil;
import com.yxjr.credit.util.YxCommonUtil;
import com.yxjr.credit.util.YxEmojiFilter;
import com.yxjr.credit.util.YxPictureUtil;

/**
 * All rights Reserved, Designed By ClareShaw
 * 
 * @公司:益芯金融
 * @作者:xiaochangyou
 * @版本:V1.0
 * @创建时间:2016-7-18 上午10:58:39
 * @描述:TODO[添加车产页]
 */
public class YxViewAddAssetCar extends RelativeLayout {

	/** 车产布局文件(XML) */
	private String addAssetCarID = "yxjr_credit_add_asset_car";
	/** 返回按钮(RelativeLayout) */
	private String carBackID = "yx_credit_asset_car_Back";
	/** 标题(TextView) */
	private String carTitleID = "yx_credit_asset_car_Title";
	/** 车辆型号(EditText)[输入车辆型号] */
	private String carTypeID = "yx_credit_asset_car_Type";
	/** 车牌号(EditText)[输入车牌号] */
	private String carPlateID = "yx_credit_asset_car_Plate";
	/** 发动机编号(EditText)[输入发动机编号] */
	private String carEngineNumID = "yx_credit_asset_car_EngineNum";
	/** 添加行驶证注册日期(RelativeLayout) */
	private String carDLRegisterDate_rlID = "yx_credit_asset_car_DLRegisterDate_rl";
	/** 行驶证注册日期(TextView)[行驶证注册日期] */
	private String carDLRegisterDateID = "yx_credit_asset_car_DLRegisterDate";
	/** 添加行驶证发证日期(RelativeLayout) */
	private String carDLSendDate_rlID = "yx_credit_asset_car_DLSendDate_rl";
	/** 行驶证发证日期(TextView)[行驶证发证日期] */
	private String carDLSendDateID = "yx_credit_asset_car_DLSendDate";
	/** 添加行驶证照片按钮(ImageView) */
	private String carCredentialPic_btnID = "yx_credit_asset_car_CredentialPic_btn";
	/** 显示行驶证照片和重拍整块(FrameLayout) */
	private String carCredentialPic_flID = "yx_credit_asset_car_CredentialPic_fl";
	/** 显示行驶证照片(ImageView) */
	private String carCredentialPicID = "yx_credit_asset_car_CredentialPic";
	/** 重拍行驶证照片(TextView)[点击重拍] */
	private String carCredentialPic_AgainID = "yx_credit_asset_car_CredentialPic_Again";
	/** 提交车产信息(Button)[提交] */
	private String carSubmitID = "yx_credit_asset_car_Submit";
	/** [个人车辆信息] */
	private String carText1ID = "yx_credit_asset_car_Text1";
	/** [行驶证照片] */
	private String carText2ID = "yx_credit_asset_car_Text2";
	/** [*信息一经提交则无法更改，请仔细核对，以免影响您的申请] */
	private String carText3ID = "yx_credit_asset_car_Text3";

	/** 布局文件 */
	private View layout;
	/** 返回按钮 */
	private RelativeLayout carBack;
	/** 标题 */
	private TextView carTitle;
	/** 车辆型号 */
	private EditText carType;
	/** 车牌号 */
	private EditText carPlate;
	/** 发动机编号 */
	private EditText carEngineNum;
	/** 添加行驶证注册日期 */
	private RelativeLayout carDLRegisterDateRl;
	/** 行驶证注册日期 */
	private TextView carDLRegisterDate;
	/** 添加行驶证发证日期 */
	private RelativeLayout carDLSendDateRl;
	/** 行驶证发证日期 */
	private TextView carDLSendDate;
	/** 添加行驶证照片按钮 */
	private ImageView carCredentialPicBtn;
	/** 显示行驶证照片和重拍整块 */
	private FrameLayout carCredentialPicFl;
	/** 显示行驶证照片 */
	private ImageView carCredentialPic;
	/** 重拍行驶证照片 */
	private TextView carCredentialPic_Again;
	/** 提交车产信息 */
	private Button carSubmit;
	/** 个人车辆信息 */
	private TextView carText1;
	/** 行驶证照片 */
	private TextView carText2;
	/** 信息一经提交则无法更改，请仔细核对，以免影响您的申请 */
	private TextView carText3;

	private String carDLRegisterDate_text = "行驶证注册日期";
	private String carDLSendDate_text = "行驶证发证日期";

	private Context mContext;
	private YxEntryActivity mActivity;
	private YxCallBack mCallBack;
	private ResContainer R;
	private String certId;
	private String categoryCode;
	public YxBmpFactory bmpFactory;
	private String carBmpPath = null;

	public YxViewAddAssetCar(Activity mActivity, YxCallBack callBack, String certId, String categoryCode) {
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

	private String stringFilter(String str) throws PatternSyntaxException {
		String regEx = "[^a-zA-Z0-9\u4E00-\u9FA5]";// 只允许字母、数字和汉字
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	private void findView() {
		// TODO Auto-generated method stub
		layout = inflate(mContext, R.layout(addAssetCarID), null);
		layout.setLayoutParams(new LayoutParams(-1, -1));
		carBack = (RelativeLayout) layout.findViewById(R.id(carBackID));
		carTitle = (TextView) layout.findViewById(R.id(carTitleID));
		carType = (EditText) layout.findViewById(R.id(carTypeID));
		carPlate = (EditText) layout.findViewById(R.id(carPlateID));
		carEngineNum = (EditText) layout.findViewById(R.id(carEngineNumID));
		carDLRegisterDateRl = (RelativeLayout) layout.findViewById(R.id(carDLRegisterDate_rlID));
		carDLRegisterDate = (TextView) layout.findViewById(R.id(carDLRegisterDateID));
		carDLSendDateRl = (RelativeLayout) layout.findViewById(R.id(carDLSendDate_rlID));
		carDLSendDate = (TextView) layout.findViewById(R.id(carDLSendDateID));
		carCredentialPicBtn = (ImageView) layout.findViewById(R.id(carCredentialPic_btnID));
		carCredentialPicFl = (FrameLayout) layout.findViewById(R.id(carCredentialPic_flID));
		carCredentialPic = (ImageView) layout.findViewById(R.id(carCredentialPicID));
		carCredentialPic_Again = (TextView) layout.findViewById(R.id(carCredentialPic_AgainID));
		carSubmit = (Button) layout.findViewById(R.id(carSubmitID));
		carText1 = (TextView) layout.findViewById(R.id(carText1ID));
		carText2 = (TextView) layout.findViewById(R.id(carText2ID));
		carText3 = (TextView) layout.findViewById(R.id(carText3ID));
	}

	private void init() {
		carTitle.setText("添加车产");
		carType.setHint("输入车辆型号");
		carType.setFilters(new InputFilter[] { new InputFilter.LengthFilter(40) });
		carPlate.setHint("输入车牌号");
		carPlate.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });
		carEngineNum.setHint("输入发动机编号");
		carEngineNum.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });
		carDLRegisterDate.setText(carDLRegisterDate_text);
		carDLSendDate.setText(carDLSendDate_text);
		carCredentialPicBtn.setImageResource(R.drawable("yx_credit_photograph_btn"));
		carCredentialPicFl.setVisibility(View.GONE);
		carSubmit.setText("提交");
		carText1.setText("个人车辆信息");
		carText2.setText("行驶证照片");
		carText3.setText("*信息一经提交则无法更改，请仔细核对，以免影响您的申请");
		setEditTextWatcher();
		setEditOnFocusChange();
		setOnClick();
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		this.addView(layout);
	}

	private void setEditTextWatcher() {
		carEngineNum.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				String editableCarEngineNum = carEngineNum.getText().toString();
				String strCarEngineNum = stringFilter(editableCarEngineNum.toString());
				if (!editableCarEngineNum.equals(strCarEngineNum)) {
					carEngineNum.setText(strCarEngineNum);
					carEngineNum.setSelection(strCarEngineNum.length());// 设置新的光标所在位置
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
		carPlate.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				String editableCarPlate = carPlate.getText().toString();
				String strCarPlate = stringFilter(editableCarPlate.toString());
				if (!editableCarPlate.equals(strCarPlate)) {
					carPlate.setText(strCarPlate);
					carPlate.setSelection(strCarPlate.length());// 设置新的光标所在位置
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
		carType.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				String editableCarType = carType.getText().toString();
				String strCarType = stringFilter(editableCarType.toString());
				if (!editableCarType.equals(strCarType)) {
					carType.setText(strCarType);
					carType.setSelection(strCarType.length());// 设置新的光标所在位置
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
		carEngineNum.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (!hasFocus) {// 没得到焦点时
					carEngineNum.clearFocus();// 失去焦点
					InputMethodManager mInputMethodManager = (InputMethodManager) mContext
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					mInputMethodManager.hideSoftInputFromWindow(carEngineNum.getWindowToken(), 0);
				}
			}
		});
		carPlate.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (!hasFocus) {// 没得到焦点时
					carPlate.clearFocus();// 失去焦点
					InputMethodManager mInputMethodManager = (InputMethodManager) mContext
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					mInputMethodManager.hideSoftInputFromWindow(carPlate.getWindowToken(), 0);
				}
			}
		});
		carType.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (!hasFocus) {// 没得到焦点时
					carType.clearFocus();// 失去焦点
					InputMethodManager mInputMethodManager = (InputMethodManager) mContext
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					mInputMethodManager.hideSoftInputFromWindow(carType.getWindowToken(), 0);
				}
			}
		});
	}

	private void setOnClick() {
		carBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCallBack.removeAssetCar();
			}
		});
		carDLRegisterDateRl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
				if (imm.hideSoftInputFromWindow(carType.getWindowToken(), 0)) {// 软键盘已弹出
					imm.showSoftInput(carType, 0);
				}
				if (imm.hideSoftInputFromWindow(carEngineNum.getWindowToken(), 0)) {// 软键盘已弹出
					imm.showSoftInput(carEngineNum, 0);
				}
				if (imm.hideSoftInputFromWindow(carPlate.getWindowToken(), 0)) {// 软键盘已弹出
					imm.showSoftInput(carPlate, 0);
				}
				YxDialogSelectDate mChangeBirthDialog = new YxDialogSelectDate(mActivity);
				Calendar c = Calendar.getInstance();
				mChangeBirthDialog.setDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1,
						c.get(Calendar.DAY_OF_MONTH));
				mChangeBirthDialog.show();
				mChangeBirthDialog.setSelectDateListener(new OnSelectDateListener() {
					@Override
					public void onClick(String year, String month, String day) {
						// TODO Auto-generated method stub
						carDLRegisterDate.setText(year + "-" + month + "-" + day);
					}
				});
			}
		});
		carDLSendDateRl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
				if (imm.hideSoftInputFromWindow(carType.getWindowToken(), 0)) {// 软键盘已弹出
					imm.showSoftInput(carType, 0);
				}
				if (imm.hideSoftInputFromWindow(carEngineNum.getWindowToken(), 0)) {// 软键盘已弹出
					imm.showSoftInput(carEngineNum, 0);
				}
				if (imm.hideSoftInputFromWindow(carPlate.getWindowToken(), 0)) {// 软键盘已弹出
					imm.showSoftInput(carPlate, 0);
				}
				YxDialogSelectDate mChangeBirthDialog = new YxDialogSelectDate(mActivity);
				Calendar c = Calendar.getInstance();
				mChangeBirthDialog.setDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1,
						c.get(Calendar.DAY_OF_MONTH));
				mChangeBirthDialog.show();
				mChangeBirthDialog.setSelectDateListener(new OnSelectDateListener() {

					@Override
					public void onClick(String year, String month, String day) {
						// TODO Auto-generated method stub
						carDLSendDate.setText(year + "-" + month + "-" + day);
					}
				});
			}
		});
		carCredentialPicBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				bmpFactory.openCamera(YxCommonConstant.ActivityCode.RequestCode.CAR_CREDENTIAL);
			}
		});
		carCredentialPic_Again.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				bmpFactory.openCamera(YxCommonConstant.ActivityCode.RequestCode.CAR_CREDENTIAL);
			}
		});
		carSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				submitCredential();
			}
		});
	}

	public void setCarCredentialPic(String carBmpPath) {
		if (carBmpPath != null) {
			this.carBmpPath = carBmpPath;
			carCredentialPic.setImageBitmap(YxPictureUtil.getSmallBitmap(carBmpPath));
			carCredentialPic_Again.setText("点击重拍");
			carCredentialPicBtn.setVisibility(View.GONE);
			carCredentialPicFl.setVisibility(View.VISIBLE);
		}
		carCredentialPic.setScaleType(ScaleType.FIT_XY);// 填充图片
	}

	private void submitCredential() {

		if (!YxCommonUtil.isNotBlank(carType.getText().toString())) {
			ToastUtil.showToast(mContext, "请填写车辆型号");
			return;
		}
		if (!YxCommonUtil.isNotBlank(carPlate.getText().toString())) {
			ToastUtil.showToast(mContext, "请填写车牌号");
			return;
		}
		if (!YxCommonUtil.isNotBlank(carEngineNum.getText().toString())) {
			ToastUtil.showToast(mContext, "请填写发动机编号");
			return;
		}
		if (YxEmojiFilter.containsEmoji(carType.getText().toString())) {
			ToastUtil.showToast(mContext, "请输入合法数据[包含表情]");
			return;
		}
		if (YxEmojiFilter.containsEmoji(carPlate.getText().toString())) {
			ToastUtil.showToast(mContext, "请输入合法数据[包含表情]");
			return;
		}
		if (YxEmojiFilter.containsEmoji(carEngineNum.getText().toString())) {
			ToastUtil.showToast(mContext, "请输入合法数据[包含表情]");
			return;
		}
		if (carDLRegisterDate.getText().toString().equals(carDLRegisterDate_text)) {
			ToastUtil.showToast(mContext, "请选择" + carDLRegisterDate_text);
			return;
		}
		if (carDLSendDate.getText().toString().equals(carDLSendDate_text)) {
			ToastUtil.showToast(mContext, "请选择" + carDLSendDate_text);
			return;
		}
		if (carCredentialPicBtn.getVisibility() == View.VISIBLE) {
			ToastUtil.showToast(mContext, "请添加行驶证照片");
			return;
		}
		List<Bitmap> bmpList = new ArrayList<Bitmap>();
		if (null != carBmpPath) {
			bmpList.add(YxPictureUtil.getSmallBitmap(carBmpPath));
		}

		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File[] files = new File[1];
		try {
			for (int i = 0; i < bmpList.size(); i++) {
				String picName = YxCommonConstant.UploadFileName.IMG_A4 + YxCommonUtil.getCurrentTime2() + ".jpg";
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
			YxLog.e("Exception:图片转换错误");
			e.printStackTrace();
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
		final DialogLoading dialogLoading = new DialogLoading(mContext, "上传中...");
		dialogLoading.show();
		new RequestEngine(mContext).upload(certId, uploadFiles, new UploadCallBack() {
			@Override
			public void onSucces(String result) {
				String carTypeValue = carType.getText().toString();
				String carPlateValue = carPlate.getText().toString();
				String carEngineNumValue = carEngineNum.getText().toString();
				String carDLRegisterDateValue = carDLRegisterDate.getText().toString();
				String carDLSendDateValue = carDLSendDate.getText().toString();
				JSONObject params = new JSONObject();
				try {
					params.put("carModel", carTypeValue);
					params.put("carNo", carPlateValue);
					params.put("engineNo", carEngineNumValue);
					params.put("vehicleRegisterDate", carDLRegisterDateValue);
					params.put("vehicleGrantDate", carDLSendDateValue);
					params.put("vehiclePhoto", result);
					params.put("categoryCode", categoryCode);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (categoryCode.equals("")) {
					submitCarAsset(HttpConstant.Request.CAR_ASSET_APPROVE, params);
				} else {
					submitCarAsset(HttpConstant.Request.CAR_ASSET_APPROVE_PATCH, params);
				}
				super.onSucces(result);
			}

			@Override
			public void onFailure(String errorCode, String errorMsg) {
				// TODO Auto-generated method stub
				ToastUtil.showToast(mContext, "行驶证上传失败！");
				super.onFailure(errorCode, errorMsg);
			}

			@Override
			public void onFinish() {
				dialogLoading.cancel();
				for (int i = 0; i < uploadFiles.length; i++) {
					if (uploadFiles[i].getAbsolutePath() != null) {
						YxPictureUtil.deleteTempFile(uploadFiles[i].getAbsolutePath());
					}
				}
				super.onFinish();
			}

			@Override
			public void onProgress(long currentLength, long totalLength, int index) {
				super.onProgress(currentLength, totalLength, index);
			}
		});
	}

	private void submitCarAsset(String serviceId, JSONObject carAsset) {
		final DialogLoading dialogLoading = new DialogLoading(mContext);
		dialogLoading.show();
		new RequestEngine(mContext).execute(serviceId, carAsset, new RequestCallBack(mContext) {
			@Override
			public void onSucces(String result) {
				dialogLoading.cancel();
				mCallBack.loadUrl(JsConstant.H5_REFRESH, null);
				mCallBack.removeAssetCar();
				ToastUtil.showToast(mContext, "提交成功！");
				YxLog.d("车产认证成功执行后！");
				// 删除临时原图片
				if (null != carBmpPath) {
					YxPictureUtil.deleteTempFile(carBmpPath);
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
