package com.yxjr.credit.ui.view;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.json.JSONException;
import org.json.JSONObject;

import com.yxjr.credit.constants.HttpConstant;
import com.yxjr.credit.constants.JsConstant;
import com.yxjr.credit.constants.SpConstant;
import com.yxjr.credit.constants.YxCommonConstant;
import com.yxjr.credit.http.manage.RequestEngine;
import com.yxjr.credit.http.manage.RequestCallBack;
import com.yxjr.credit.http.manage.UploadCallBack;
import com.yxjr.credit.log.YxLog;
import com.yxjr.credit.plugin.StatisticalTime;
import com.yxjr.credit.plugin.YxBmpFactory;
import com.yxjr.credit.ui.CameraActivity;
import com.yxjr.credit.ui.YxCallBack;
import com.yxjr.credit.ui.YxEntryActivity;
import com.yxjr.credit.util.DialogUtil;
import com.yxjr.credit.util.ToastUtil;
import com.yxjr.credit.util.YxCommonUtil;
import com.yxjr.credit.util.YxDensityUtil;
import com.yxjr.credit.util.YxPictureUtil;
import com.yxjr.credit.util.YxStoreUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

/**
 * All rights Reserved, Designed By ClareShaw
 * 
 * @公司:益芯金融
 * @作者:xiaochangyou
 * @版本:V1.0
 * @创建时间:2016-7-18 上午10:59:51
 * @描述:TODO[实名认证页]
 */
public class YxViewAutonymCertify extends YxLineraLayout {

	/** 实名认证布局文件(XML) */
	private String autonymID = "yxjr_credit_certify_approve";
	/** 返回按钮(RelativeLayout) */
	private String autonymBackID = "yx_credit_certify_autonym_Back";
	/** 标题(TextView) */
	private String autonymTitleID = "yx_credit_certify_autonym_Title";
	/** 常见问题 */
	private String autonymSubTitleID = "yx_credit_certify_autonym_SublTitle";
	/** 姓名(EditText)[请输入您的姓名] */
	private String autonymNameID = "yx_credit_certify_autonym_Name";
	/** 身份证号(EditText)[请输入您的身份证号] */
	private String autonymIdentityNumID = "yx_credit_certify_autonym_IdentityNum";
	/** 身份证正面拍照按钮(ImageView) */
	private String autonymIdNumFrontPicBtnID = "yx_credit_certify_autonym_IdNumFrontPic_btn";
	/** 身份证正面照片提示文字(TextView)[身份证正面] */
	// private String autonymIdNumFrontPicTxtID =
	// "yx_credit_certify_autonym_IdNumFrontPic_txt";
	/** 身份证反面拍照按钮(ImageView) */
	private String autonymIdNumVersoPicBtnID = "yx_credit_certify_autonym_IdNumVersoPic_btn";
	/** 身份证反面照片提示文字(TextView)[身份证反面] */
	// private String autonymIdNumVersoPicTxtID =
	// "yx_credit_certify_autonym_IdNumVersoPic_txt";
	/** 手持身份证拍照按钮(ImageView) */
	private String autonymIdNumHandPicBtnID = "yx_credit_certify_autonym_IdNumHandPic_btn";
	/** 手持身份证照片提示文字(TextView)[手持身份证] */
	// private String autonymIdNumHandPicTxtID =
	// "yx_credit_certify_autonym_IdNumHandPic_txt";
	/** 提交(Button)[提交] */
	private String autonymSubmitID = "yx_credit_certify_autonym_Submit";
	/** [额度宝会保护您的信息] */
	private String autonymText1ID = "yx_credit_certify_autonym_Text1";
	/** [身份证拍照] */
	private String autonymText2ID = "yx_credit_certify_autonym_Text2";
	/** [拍照上传] */
	// private String autonymText3ID = "yx_credit_certify_autonym_Text3";
	/** [实名影像认证说明：] */
	private String autonymText4ID = "yx_credit_certify_autonym_Text4";
	/** [1、] */
	private String autonymText5ID = "yx_credit_certify_autonym_Text5";
	/** [身份证正面、反面照片须无遮挡、无反光、无修改、字迹、头像均清晰可见;] */
	private String autonymText6ID = "yx_credit_certify_autonym_Text6";
	/** [2、] */
	private String autonymText7ID = "yx_credit_certify_autonym_Text7";
	/** [手持身份证照片须手持身份证，同时露脸并露肘身份证及本人均需无遮挡、无反光、无修改，本人及身份证影像字迹、头像均清晰可见。] */
	private String autonymText8ID = "yx_credit_certify_autonym_Text8";
	/** [3、] */
	private String autonymText9ID = "yx_credit_certify_autonym_Text9";
	/** 查看影像示例(TextView)[查看影像示例] */
	private String autonymText10ID = "yx_credit_certify_autonym_Text10";

	/** 布局文件 */
	private View layout;
	/** 返回按钮 */
	private RelativeLayout autonymBack;
	/** 标题 */
	private TextView autonymTitle;
	/** 常见问题 */
	private TextView autoymSubTitle;
	/** 姓名 */
	private EditText autonymName;
	/** 身份证号 */
	private EditText autonymIdentityNum;
	/** 身份证正面拍照按钮 */
	private ImageView autonymIdNumFrontPicBtn;
	/** 身份证正面照片提示文字 */
	// private TextView autonymIdNumFrontPicTxt;
	/** 身份证反面拍照按钮 */
	private ImageView autonymIdNumVersoPicBtn;
	/** 身份证反面照片提示文字 */
	// private TextView autonymIdNumVersoPicTxt;
	/** 手持身份证拍照按钮 */
	private ImageView autonymIdNumHandPicBtn;
	/** 手持身份证照片提示文字 */
	// private TextView autonymIdNumHandPicTxt;
	/** 提交 */
	private Button autonymSubmitBtn;
	/** 额度宝会保护您的信息 */
	private TextView autonymText1;
	/** 身份证拍照 */
	private TextView autonymText2;
	/** 拍照上传 */
	// private TextView autonymText3;
	/** 实名影像认证说明： */
	private TextView autonymText4;
	/** 1、 */
	private TextView autonymText5;
	/** 身份证正面、反面照片须无遮挡、无反光、无修改、字迹、头像均清晰可见; */
	private TextView autonymText6;
	/** 2、 */
	private TextView autonymText7;
	/** 手持身份证照片须手持身份证，同时露脸并露肘身份证及本人均需无遮挡、无反光、无修改，本人及身份证影像字迹、头像均清晰可见。 */
	private TextView autonymText8;
	/** 3、 */
	private TextView autonymText9;
	/** 查看影像示例 */
	private TextView autonymText10;

	private Context mContext;
	private YxEntryActivity mActivity;
	private YxCallBack mCallBack;
	private ResContainer R;
	private String certId;
	private String categoryCode;
	public YxBmpFactory bmpFactory;
	/** 身份证正面原照片路径 */
	private String frontBmpPath = null;
	/** 身份证反面原照片路径 */
	private String versoBmpPath = null;
	/** 手持身份证原照片路径 */
	private String handBmpPath = null;

	private DialogUtil mDialogUtil;
	private int screenWidth;

	public YxViewAutonymCertify(Activity mActivity, YxCallBack callBack, String certId, String categoryCode) {
		// TODO Auto-generated constructor stub
		super(mActivity);
		mDialogUtil = new DialogUtil(mActivity);
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
		layout = inflate(mContext, R.layout(autonymID), null);
		layout.setLayoutParams(new LayoutParams(-1, -1));
		autonymBack = (RelativeLayout) layout.findViewById(R.id(autonymBackID));
		autonymTitle = (TextView) layout.findViewById(R.id(autonymTitleID));
		autoymSubTitle = (TextView) layout.findViewById(R.id(autonymSubTitleID));
		autonymName = (EditText) layout.findViewById(R.id(autonymNameID));
		autonymIdentityNum = (EditText) layout.findViewById(R.id(autonymIdentityNumID));
		autonymIdNumFrontPicBtn = (ImageView) layout.findViewById(R.id(autonymIdNumFrontPicBtnID));
		// autonymIdNumFrontPicTxt = (TextView)
		// layout.findViewById(R.id(autonymIdNumFrontPicTxtID));
		autonymIdNumVersoPicBtn = (ImageView) layout.findViewById(R.id(autonymIdNumVersoPicBtnID));
		// autonymIdNumVersoPicTxt = (TextView)
		// layout.findViewById(R.id(autonymIdNumVersoPicTxtID));
		autonymIdNumHandPicBtn = (ImageView) layout.findViewById(R.id(autonymIdNumHandPicBtnID));
		// autonymIdNumHandPicTxt = (TextView)
		// layout.findViewById(R.id(autonymIdNumHandPicTxtID));
		autonymSubmitBtn = (Button) layout.findViewById(R.id(autonymSubmitID));
		autonymText1 = (TextView) layout.findViewById(R.id(autonymText1ID));
		autonymText2 = (TextView) layout.findViewById(R.id(autonymText2ID));
		// autonymText3 = (TextView) layout.findViewById(R.id(autonymText3ID));
		autonymText4 = (TextView) layout.findViewById(R.id(autonymText4ID));
		autonymText5 = (TextView) layout.findViewById(R.id(autonymText5ID));
		autonymText6 = (TextView) layout.findViewById(R.id(autonymText6ID));
		autonymText7 = (TextView) layout.findViewById(R.id(autonymText7ID));
		autonymText8 = (TextView) layout.findViewById(R.id(autonymText8ID));
		autonymText9 = (TextView) layout.findViewById(R.id(autonymText9ID));
		autonymText10 = (TextView) layout.findViewById(R.id(autonymText10ID));
	}

	@SuppressWarnings("deprecation")
	private void init() {
		screenWidth = mActivity.getWindowManager().getDefaultDisplay().getWidth();
		autonymTitle.setText("实名认证");
		autonymName.setText(YxStoreUtil.get(mContext, SpConstant.PARTNER_REAL_NAME));
		autonymName.setFocusable(false);
		autonymName.setFocusableInTouchMode(false);
		autonymIdentityNum.setText(YxStoreUtil.get(mContext, SpConstant.PARTNER_ID_CARD_NUM));
		autonymIdentityNum.setFocusable(false);
		autonymIdentityNum.setFocusableInTouchMode(false);
		autonymIdNumFrontPicBtn.setImageResource(R.drawable("yx_credit_identity_card_front"));
		ViewGroup.LayoutParams frontLayoutParams = autonymIdNumFrontPicBtn.getLayoutParams();
		frontLayoutParams.height = screenWidth / 3 - YxDensityUtil.dipToPx(mContext, 15);// 高度=控件宽度(屏幕总宽/3-多余的15dp)
		autonymIdNumFrontPicBtn.setLayoutParams(frontLayoutParams);
		// autonymIdNumFrontPicTxt.setText("身份证正面");
		autonymIdNumVersoPicBtn.setImageResource(R.drawable("yx_credit_identity_card_verso"));
		ViewGroup.LayoutParams versoLayoutParams = autonymIdNumVersoPicBtn.getLayoutParams();
		versoLayoutParams.height = screenWidth / 3 - YxDensityUtil.dipToPx(mContext, 15);
		autonymIdNumVersoPicBtn.setLayoutParams(versoLayoutParams);
		// autonymIdNumVersoPicTxt.setText("身份证反面");
		autonymIdNumHandPicBtn.setImageResource(R.drawable("yx_credit_identity_card_in_hand"));
		ViewGroup.LayoutParams handLayoutParams = autonymIdNumHandPicBtn.getLayoutParams();
		handLayoutParams.height = screenWidth / 3 - YxDensityUtil.dipToPx(mContext, 15);
		autonymIdNumHandPicBtn.setLayoutParams(handLayoutParams);

		// autonymIdNumHandPicTxt.setText("手持身份证");
		autonymSubmitBtn.setText("提交");
		autonymText1.setText("额度宝会保护您的信息");
		autonymText2.setText("身份证拍照");
		// autonymText3.setText("拍照上传");
		autonymText4.setText("实名影像认证说明：");
		autonymText5.setText("1、");
		autonymText6.setText("身份证正面、反面照片须无遮挡、无反光、无修改、字迹、头像均清晰可见;");
		autonymText7.setText("2、");
		autonymText8.setText("手持身份证照片须手持身份证，同时露脸并露肘身份证及本人均需无遮挡、无反光、无修改，本人及身份证影像字迹、头像均清晰可见。");
		autonymText9.setText("3、");
		autonymText10.setText("查看影像示例");
		autonymBack.setOnClickListener(clickListener);
		autoymSubTitle.setOnClickListener(clickListener);
		autonymIdNumFrontPicBtn.setScaleType(ScaleType.FIT_XY);
		autonymIdNumFrontPicBtn.setOnClickListener(clickListener);
		autonymIdNumVersoPicBtn.setScaleType(ScaleType.FIT_XY);
		autonymIdNumVersoPicBtn.setOnClickListener(clickListener);
		autonymIdNumHandPicBtn.setScaleType(ScaleType.FIT_XY);
		autonymIdNumHandPicBtn.setOnClickListener(clickListener);
		autonymSubmitBtn.setOnClickListener(clickListener);
		autonymText10.setOnClickListener(clickListener);
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		this.addView(layout);
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();

			int autonymBack = R.id(autonymBackID);
			int autonmySubTitleInt = R.id(autonymSubTitleID);
			int autonymIdNumFrontPicBtn = R.id(autonymIdNumFrontPicBtnID);
			int autonymIdNumVersoPicBtn = R.id(autonymIdNumVersoPicBtnID);
			int autonymIdNumHandPicBtn = R.id(autonymIdNumHandPicBtnID);
			int autonymSubmitBtn = R.id(autonymSubmitID);
			int autonymText10 = R.id(autonymText10ID);

			if (id == autonymBack) {// 返回
				mCallBack.removeAutonymCertify();
			} else if (id == autonmySubTitleInt) {
				mCallBack.addQuestion();
			} else if (id == autonymIdNumFrontPicBtn) {// 身份证正面
				if (!isSelectFront) {
					mActivity
							.startActivityForResult(
									new Intent(mContext, CameraActivity.class).putExtra("picName",
											YxCommonConstant.UploadFileName.IMG_A1 + YxCommonUtil.getCurrentTime2()
													+ ".jpg"),
									YxCommonConstant.ActivityCode.RequestCode.AUTONYM_CERTIFY_ID_CARD_FRONT);
				} else {
					mDialogUtil.showDialog(frontBmpPath,
							YxCommonConstant.ActivityCode.RequestCode.AUTONYM_CERTIFY_ID_CARD_FRONT,
							YxCommonConstant.UploadFileName.IMG_A1 + YxCommonUtil.getCurrentTime2() + ".jpg");
				}
			} else if (id == autonymIdNumVersoPicBtn) {// 身份证反面
				if (!isSelectVerso) {
					mActivity
							.startActivityForResult(
									new Intent(mContext, CameraActivity.class).putExtra("picName",
											YxCommonConstant.UploadFileName.IMG_A2 + YxCommonUtil.getCurrentTime2()
													+ ".jpg"),
									YxCommonConstant.ActivityCode.RequestCode.AUTONYM_CERTIFY_ID_CARD_VERSO);
				} else {
					mDialogUtil.showDialog(versoBmpPath,
							YxCommonConstant.ActivityCode.RequestCode.AUTONYM_CERTIFY_ID_CARD_VERSO,
							YxCommonConstant.UploadFileName.IMG_A2 + YxCommonUtil.getCurrentTime2() + "jpg");
				}
			} else if (id == autonymIdNumHandPicBtn) {// 手持身份证
				if (!isSelectHand) {
					mActivity
							.startActivityForResult(
									new Intent(mContext, CameraActivity.class)
											.putExtra("picName",
													YxCommonConstant.UploadFileName.IMG_A3
															+ YxCommonUtil.getCurrentTime2() + ".jpg")
											.putExtra("isvertical", true).putExtra("isChange", true),
									YxCommonConstant.ActivityCode.RequestCode.AUTONYM_CERTIFY_ID_CARD_HAND);
				} else {
					mDialogUtil.showDialog(handBmpPath,
							YxCommonConstant.ActivityCode.RequestCode.AUTONYM_CERTIFY_ID_CARD_HAND,
							YxCommonConstant.UploadFileName.IMG_A3 + YxCommonUtil.getCurrentTime2() + ".jpg");
				}
			} else if (id == autonymSubmitBtn) {// 提交
				submitCredential();
			} else if (id == autonymText10) {// 影像示例
				mCallBack.addExample();
			}
		}
	};

	private boolean isSelectFront = false;// 是否添加身份证正面照
	private boolean isSelectVerso = false;// 是否添加身份证反面照
	private boolean isSelectHand = false;// 是否添加身份证手持照

	/***/
	public void setAutonymIdNumFrontPic(String frontBmpPath) {
		if (frontBmpPath != null) {
			if (this.frontBmpPath != null) {
				YxPictureUtil.deleteTempFile(this.frontBmpPath);
			}
			this.frontBmpPath = frontBmpPath;
			this.autonymIdNumFrontPicBtn.setImageBitmap(YxPictureUtil.getSmallBitmap(frontBmpPath));
			isSelectFront = true;
		}
	}

	/***/
	public void setAutonymIdNumVersoPic(String versoBmpPath) {
		if (versoBmpPath != null) {
			if (this.versoBmpPath != null) {
				YxPictureUtil.deleteTempFile(this.versoBmpPath);
			}
			this.versoBmpPath = versoBmpPath;
			this.autonymIdNumVersoPicBtn.setImageBitmap(YxPictureUtil.getSmallBitmap(versoBmpPath));
			isSelectVerso = true;
		}
	}

	/***/
	public void setAutonymIdNumHandPic(String handBmpPath) {
		if (handBmpPath != null) {
			if (this.handBmpPath != null) {
				YxPictureUtil.deleteTempFile(this.handBmpPath);
			}
			this.handBmpPath = handBmpPath;
			this.autonymIdNumHandPicBtn.setImageBitmap(YxPictureUtil.getSmallBitmap(handBmpPath));
			isSelectHand = true;
		}
	}

	@SuppressWarnings("resource")
	private void submitCredential() {

		if (!YxCommonUtil.isNotBlank(autonymName.getText().toString())) {
			ToastUtil.showToast(mContext, "请填写姓名");
			new StatisticalTime.Builder().addContext(mActivity).addOperPageName("实名认证").addErrorInfo("请填写姓名")
					.addOperElementType("button").addOperElementName("提交").addSessionId(certId).build();
			return;
		}
		if (!YxCommonUtil.isNotBlank(autonymIdentityNum.getText().toString())) {
			ToastUtil.showToast(mContext, "请填写身份证号");
			new StatisticalTime.Builder().addContext(mActivity).addOperPageName("请填写身份证号").addErrorInfo("请填写姓名")
					.addOperElementType("button").addOperElementName("提交").addSessionId(certId).build();
			return;
		}
		if (!isSelectFront) {
			ToastUtil.showToast(mContext, "请添加身份证正面照");
			new StatisticalTime.Builder().addContext(mActivity).addOperPageName("请添加身份证正面照").addErrorInfo("请填写姓名")
					.addOperElementType("button").addOperElementName("提交").addSessionId(certId).build();
			return;
		}
		if (!isSelectVerso) {
			ToastUtil.showToast(mContext, "请添加身份证反面照");
			new StatisticalTime.Builder().addContext(mActivity).addOperPageName("请添加身份证反面照").addErrorInfo("请填写姓名")
					.addOperElementType("button").addOperElementName("提交").addSessionId(certId).build();
			return;
		}
		if (!isSelectHand) {
			ToastUtil.showToast(mContext, "请添加手持身份证照");
			new StatisticalTime.Builder().addContext(mActivity).addOperPageName("请添加手持身份证照").addErrorInfo("请填写姓名")
					.addOperElementType("button").addOperElementName("提交").addSessionId(certId).build();
			return;
		}
		List<Bitmap> bmpList = new ArrayList<Bitmap>();
		if (null != frontBmpPath) {
			bmpList.add(YxPictureUtil.getSmallBitmap(frontBmpPath));
		}
		if (null != versoBmpPath) {
			bmpList.add(YxPictureUtil.getSmallBitmap(versoBmpPath));
		}
		if (null != handBmpPath) {
			bmpList.add(YxPictureUtil.getSmallBitmap(handBmpPath));
		}
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		File[] files = new File[bmpList.size()];
		try {
			for (int i = 0; i < bmpList.size(); i++) {
				String picName = "A" + (i + 1) + "_" + YxCommonUtil.getCurrentTime2() + ".jpg";
				files[i] = new File(YxPictureUtil.getAlbumDir(), picName);
				fos = new FileOutputStream(files[i]);
				bos = new BufferedOutputStream(fos);
				bmpList.get(i).compress(Bitmap.CompressFormat.JPEG, YxCommonConstant.Params.PICTURE_COMPRESS, bos);// compress是压缩率,50表示压缩50%;如果不压缩是100,表示压缩率为0%
				fos.flush();
				bos.flush();
				YxLog.d("======uploadfiles add file" + new FileInputStream(files[i]).available());
				YxLog.d("======uploadfiles Path " + files[i].getPath());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			YxLog.e("Exception:图片转换错误!");
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
		final DialogLoading dialogLoading = new DialogLoading(mActivity,"上传中...");
		dialogLoading.show();
		new RequestEngine(mContext).upload(certId, uploadFiles, new UploadCallBack() {
			@Override
			public void onSucces(String result) {
				// TODO Auto-generated method stub
				YxLog.d("======文件上传成功：" + result);
				String[] filePathAfterSplit = new String[3];
				// filePathAfterSplit = result.split(","); //
				// 以“,”作为分隔符来分割date字符串，并把结果放入3个字符串中。
				StringTokenizer token = new StringTokenizer(result, ",");
				while (token.hasMoreTokens()) {
					String path = token.nextToken();
					if (path.contains("A1")) {
						filePathAfterSplit[0] = path;
					} else if (path.contains("A2")) {
						filePathAfterSplit[1] = path;
					} else if (path.contains("A3")) {
						filePathAfterSplit[2] = path;
					}
				}

				String houseProvinceAndCityValueValue = autonymIdentityNum.getText().toString();
				String autonymNameValue = autonymName.getText().toString();
				JSONObject params = new JSONObject();
				try {
					params.put("cert", houseProvinceAndCityValueValue);// 身份证号
					params.put("name", autonymNameValue);// 姓名
					params.put("certFront", filePathAfterSplit[0]);// 身份证正面
					params.put("certBack", filePathAfterSplit[1]);// 身份证反面
					params.put("certHandheld", filePathAfterSplit[2]);// 手持身份证
					params.put("categoryCode", categoryCode);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (categoryCode.equals("")) {
					submitName(HttpConstant.Request.NAME_ASSET_APPROVE, params);
				} else {
					submitName(HttpConstant.Request.NAME_ASSET_APPROVE_PATCH, params);
				}
				super.onSucces(result);
			}

			@Override
			public void onFailure(String errorCode, String errorMsg) {
				// TODO Auto-generated method stub
				YxLog.d("======文件上传失败：" + errorCode + "==" + errorMsg);
				ToastUtil.showToast(mContext, "身份证上传失败！");
				new StatisticalTime.Builder().addContext(mActivity).addOperPageName("实名认证").addErrorInfo(errorMsg)
						.addOperElementType("button").addOperElementName("提交").addSessionId(certId).build();
				super.onFailure(errorCode, errorMsg);
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				for (int i = 0; i < uploadFiles.length; i++) {
					if (null != uploadFiles[i].getAbsolutePath()) {
						YxPictureUtil.deleteTempFile(uploadFiles[i].getAbsolutePath());
					}
				}
				dialogLoading.cancel();
				super.onFinish();
			}

			@Override
			public void onProgress(long currentLength, long totalLength, int index) {
				super.onProgress(currentLength, totalLength, index);
			}
		});
	}

	// 实名认证
	private void submitName(final String serviceId, final JSONObject name) {
		final DialogLoading dialogLoading = new DialogLoading(mActivity);
		dialogLoading.show();
		new RequestEngine(mContext).execute(serviceId, name, new RequestCallBack(mContext) {
			@Override
			public void onSucces(String result) {
				dialogLoading.cancel();
				ToastUtil.showToast(mContext, "提交成功！");
				// 统计提交
				new StatisticalTime.Builder().addContext(mActivity).addOperPageName("实名认证").addErrorInfo("")
						.addOperElementType("button").addOperElementName("提交").addSessionId(serviceId).build();
				mCallBack.removeAutonymCertify();
				mCallBack.loadUrl(JsConstant.H5_REFRESH, null);
				// 删除临时原图片
				if (null != frontBmpPath) {
					YxPictureUtil.deleteTempFile(frontBmpPath);
				}
				if (null != versoBmpPath) {
					YxPictureUtil.deleteTempFile(versoBmpPath);
				}
				if (null != handBmpPath) {
					YxPictureUtil.deleteTempFile(handBmpPath);
				}
				super.onSucces(result);
			}

			@Override
			public void onFailure(String errorCode, String errorMsg) {
				dialogLoading.cancel();
				new StatisticalTime.Builder().addContext(mActivity).addOperPageName("实名认证").addErrorInfo(errorMsg)
						.addOperElementType("button").addOperElementName("提交").addSessionId(serviceId).build();
				super.onFailure(errorCode, errorMsg);
			}
		});
	}
}
