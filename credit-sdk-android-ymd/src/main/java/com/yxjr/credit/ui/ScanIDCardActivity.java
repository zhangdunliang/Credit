package com.yxjr.credit.ui;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import com.megvii.idcardquality.IDCardQualityAssessment;
import com.megvii.idcardquality.IDCardQualityLicenseManager;
import com.megvii.idcardquality.IDCardQualityResult;
import com.megvii.idcardquality.bean.IDCardAttr;
import com.megvii.licensemanager.Manager;
import com.yxjr.credit.constants.SpConstant;
import com.yxjr.credit.ocr.util.MegviiCamera;
import com.yxjr.credit.ocr.util.MegviiUtil;
import com.yxjr.credit.ocr.util.ScreenUtil;
import com.yxjr.credit.ocr.util.Util;
import com.yxjr.credit.ui.presenter.PScanIDCardActivity;
import com.yxjr.credit.ui.view.ResContainer;
import com.yxjr.credit.util.DialogUtil;
import com.yxjr.credit.util.StringUtil;
import com.yxjr.credit.util.YxDensityUtil;
import com.yxjr.credit.util.YxStoreUtil;
import com.yxjr.credit.widget.IDCardNewIndicator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class ScanIDCardActivity extends YxBaseActivity implements TextureView.SurfaceTextureListener, Camera.PreviewCallback {

	private TextureView mTextureView;// 照相机预览控件
	private MegviiCamera mICamera;// 照相机工具类
	private IDCardQualityAssessment mIdCardQualityAssessment = null;// 身份证质量检测类
	private IDCardNewIndicator mNewIndicatorView;// 自定义的UI-身份证框
	private IDCardAttr.IDCardSide mSide;// 身份证的正反面
	private DecodeThread mDecoder = null;
	// private boolean mIsVertical = false;//弃用，只使用横屏
	private TextView mFrontOrBack;// 正面背面提示
	private TextView mAfterFour;// 身份证号码后四位
	private ImageView mFrontOrBackImg;// 正面背面图片
	private ImageView mExitScan;// "X"退出
	private LinearLayout mWarrantyLin;// 授权UI
	private ProgressBar mWarrantyBar;// 授权进度
	private TextView mWarrantyText;// 授权文字
	private Button mWarrantyAgainBtn;// 重新授权按钮
	private TextView mPrompt;// 错误提示

	// private TextView fps;//调试用

	private DialogUtil mDialogUtil;
	private static int mScanType;
	private static String mCategoryCode;
	private static String mAppNo;

	private final int WARRANTY_SUCCES = 1;
	private final int WARRANTY_FAIL = 2;
	public static final int CANCEL_ANIM = 5;

	private boolean mHasSurface = false;// SurfaceTexture是否有效
	private BlockingQueue<byte[]> mFrameDataQueue;

	private PScanIDCardActivity mPScanIDCardActivity;

	@SuppressLint("HandlerLeak")
	Handler mHandler = new MyHandler();

	@SuppressLint("HandlerLeak")
	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == WARRANTY_SUCCES) {
				mWarrantyLin.setVisibility(View.GONE);
				startScan();
			} else if (msg.what == WARRANTY_FAIL) {
				mWarrantyAgainBtn.setVisibility(View.VISIBLE);
				mWarrantyBar.setVisibility(View.GONE);
				mWarrantyText.setText("联网授权失败，请点击按钮重新授权");
			}
			//			} else if (msg.what == UPLOAD_FRONT) {
			//				mPScanIDCardActivity.uploadP1();
			//			} else if (msg.what == UPLOAD_BACK) {
			//				mPScanIDCardActivity.uploadP2();
			//			} 
			else if (msg.what == CANCEL_ANIM) {
				mNewIndicatorView.stopScanAnimator();
			}
		}
	}

	private ResContainer R;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 无标题
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 横屏
		this.R = ResContainer.get(this);
		setContentView(R.layout("yxjr_credit_scan_idcard"));
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			findView();
			init();
		}
	}

	// findViewId
	private void findView() {
		mWarrantyLin = (LinearLayout) findViewById(R.id("yx_credit_barLinear"));
		mWarrantyBar = (ProgressBar) findViewById(R.id("yx_credit_WarrantyBar"));
		mWarrantyText = (TextView) findViewById(R.id("yx_credit_WarrantyText"));
		mWarrantyAgainBtn = (Button) findViewById(R.id("yx_credit_againWarrantyBtn"));
		mNewIndicatorView = (IDCardNewIndicator) findViewById(R.id("yx_credit_idcardscan_layout_newIndicator"));
		mTextureView = (TextureView) findViewById(R.id("yx_credit_idcardscan_layout_surface"));
		mTextureView.setSurfaceTextureListener(this);
		mFrontOrBack = (TextView) findViewById(R.id("yx_credit_front_or_back"));
		mFrontOrBackImg = (ImageView) findViewById(R.id("yx_credit_front_or_back_img"));
		mAfterFour = (TextView) findViewById(R.id("yx_credit_after_four"));
		mPrompt = (TextView) findViewById(R.id("idcardscan_layout_horizontalTitle"));
		mExitScan = (ImageView) findViewById(R.id("yx_credit_exit"));
		mExitScan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ScanIDCardActivity.this.finish();
			}
		});
		mTextureView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mICamera.autoFocus();
			}
		});
		mWarrantyAgainBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				netWorkWarranty();
			}
		});
		// fps = (TextView) findViewById(R.id.idcardscan_layout_fps);
	}

	// 初始化
	private void init() {
		mICamera = new MegviiCamera();
		mDialogUtil = new DialogUtil(this);
		mFrameDataQueue = new LinkedBlockingDeque<byte[]>(1);
		//
		Intent intent = getIntent();
		mScanType = intent.getIntExtra("scanType", 0);
		if (mScanType == 1) {
			mSide = IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT;
		} else if (mScanType == 2) {
			mSide = IDCardAttr.IDCardSide.IDCARD_SIDE_BACK;
		} else {
			mSide = intent.getIntExtra("side", 0) == 0 ? IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT : IDCardAttr.IDCardSide.IDCARD_SIDE_BACK;
		}
		mAppNo = intent.getStringExtra("appNo");
		mAppNo = mAppNo.equals("empty") ? "" : mAppNo;
		mCategoryCode = intent.getStringExtra("categoryCode");
		mCategoryCode = mCategoryCode.equals("empty") ? "" : mCategoryCode;

		mPScanIDCardActivity = new PScanIDCardActivity(ScanIDCardActivity.this, mScanType, mAppNo, mCategoryCode);

		String idCardNum = YxStoreUtil.get(this, SpConstant.PARTNER_ID_CARD_NUM);
		//
		if (mSide == IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT) {// 拍正面
			mFrontOrBack.setText("的身份证正面");
			mFrontOrBackImg.setImageResource(R.drawable("yxjr_credit_idcard_front"));
		} else if (mSide == IDCardAttr.IDCardSide.IDCARD_SIDE_BACK) {// 拍背面
			mFrontOrBack.setText("的身份证反面");
			mFrontOrBackImg.setImageResource(R.drawable("yxjr_credit_idcard_back"));
		}
		if (!StringUtil.isEmpty(idCardNum)) {
			mAfterFour.setText(StringUtil.laterFour(idCardNum));
		}

		initLayout();
		netWorkWarranty();
	}

	// 初始化 上、下、右的布局位置
	private void initLayout() {
		// 横屏，高度、宽度调换一下
		int heightPixels = ScreenUtil.getHeight(this, false);// 屏幕高度
		int widthPixels = ScreenUtil.getWidth(this, false);// 屏幕宽度
		int right_width = (int) (widthPixels * mNewIndicatorView.RIGHT_RATIO);
		int centerX = (widthPixels - right_width) >> 1;
		int centerY = heightPixels >> 1;
		int idWidth = (int) ((widthPixels - right_width) * mNewIndicatorView.SHOW_CONTENT_RATIO);// 身份证框宽度
		int idHeight = (int) (idWidth / mNewIndicatorView.IDCARD_RATIO);// 身份证框高度
		int left = (int) (centerX - idWidth / 2.0f);
		int top = centerY - idHeight / 2;
		int right = idWidth + left;// 身份证右边X位置
		int bottom = idHeight + top;// 身份证下边Y位置

		// “请保证信息...”布局位置
		int rightSize = widthPixels - right;
		RelativeLayout rightLayout = (RelativeLayout) findViewById(R.id("yx_credit_rl_right_hint"));
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(YxDensityUtil.dipToPx(this, 117), ViewGroup.LayoutParams.WRAP_CONTENT);
		if (rightSize - layoutParams.width > 0) {
			layoutParams.setMargins(right + rightSize / 2 - layoutParams.width / 2, top, 0, 0);// 4个参数按顺序分别是左上右下
		} else {
			layoutParams.setMargins(right, top, 0, 0);// 4个参数按顺序分别是左上右下
		}
		rightLayout.setLayoutParams(layoutParams);
		// “请扫描尾号为.....”布局位置
		LinearLayout bottomLayout = (LinearLayout) findViewById(R.id("yx_credit_ll_bottom"));
		RelativeLayout.LayoutParams bottomLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		bottomLayout.measure(0, 0);
		int bottomLayoutWidth = bottomLayout.getMeasuredWidth();
		// int bottomLayoutHeight = bottomLayout.getMeasuredHeight();
		bottomLayoutParams.setMargins(left + idWidth / 2 - bottomLayoutWidth / 2, bottom + ((heightPixels - bottom) / 2), 0, 0);
		bottomLayout.setLayoutParams(bottomLayoutParams);
		// “完成扫描.....”布局位置
		LinearLayout topLayout = (LinearLayout) findViewById(R.id("yx_credit_ll_top"));
		RelativeLayout.LayoutParams topLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		topLayout.measure(0, 0);
		int topLayoutWidth = topLayout.getMeasuredWidth();
		int topLayoutHeight = topLayout.getMeasuredHeight();
		topLayoutParams.setMargins(left + idWidth / 2 - topLayoutWidth / 2, bottom + ((heightPixels - bottom) / 4) - topLayoutHeight / 2, 0, 0);
		topLayout.setLayoutParams(topLayoutParams);
		// 错误提示 布局位置
		RelativeLayout.LayoutParams promptLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		mPrompt.measure(0, 0);
		int promptWidth = topLayout.getMeasuredWidth();
		int promptHeight = topLayout.getMeasuredHeight();
		promptLayoutParams.setMargins(left + idWidth / 2 - promptWidth / 2, top / 2 - promptHeight / 2, 0, 0);
		mPrompt.setLayoutParams(promptLayoutParams);
	}

	private IDCardQualityLicenseManager idCardLicenseManager;

	/**
	 * 联网授权
	 */
	private void netWorkWarranty() {
		idCardLicenseManager = new IDCardQualityLicenseManager(ScanIDCardActivity.this);

		if (idCardLicenseManager.checkCachedLicense() > 0)// 已授权
			mHandler.sendEmptyMessage(WARRANTY_SUCCES);
		else {
			mDialogUtil.showDialog("是否授权额度宝扫描您的身份证", "取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					ScanIDCardActivity.this.finish();
				}
			}, "授权", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					final String uuid = MegviiUtil.getUUIDString(ScanIDCardActivity.this);
					mWarrantyLin.setVisibility(View.VISIBLE);
					mWarrantyAgainBtn.setVisibility(View.GONE);
					mWarrantyText.setText("正在授权...");
					mWarrantyBar.setVisibility(View.VISIBLE);
					new Thread(new Runnable() {
						@Override
						public void run() {
							Manager manager = new Manager(ScanIDCardActivity.this);
							manager.registerLicenseManager(idCardLicenseManager);
							manager.takeLicenseFromNetwork(uuid);
							if (idCardLicenseManager.checkCachedLicense() > 0)
								mHandler.sendEmptyMessage(WARRANTY_SUCCES);
							else
								mHandler.sendEmptyMessage(WARRANTY_FAIL);
						}
					}).start();
				}
			});
		}
	}

	// 开始扫描
	private void startScan() {
		mNewIndicatorView.startScanAnimator();
		// initData();
		mIdCardQualityAssessment = new IDCardQualityAssessment();
		// mIdCardQualityAssessment.mClear = 0.9f;//0.7
		// mIdCardQualityAssessment.mIsIdcard = 0.9f;//0.5
		// mIdCardQualityAssessment.mInBound = 0.1f;//0.6
		boolean initSuccess = mIdCardQualityAssessment.init(this, MegviiUtil.readModel(this, R.raw("idcardqualitymodel")));
		if (!initSuccess) {
			mDialogUtil.showDialogForActivity("检测器初始化失败");
		}
		mTextureView.setSurfaceTextureListener(this);
		mDecoder = new DecodeThread();
		mDecoder.start();
	}

	// /**
	// * 初始化数据
	// */
	// private void initData() {
	// mIdCardQualityAssessment = new IDCardQualityAssessment();
	// // mIdCardQualityAssessment.mClear = 0.9f;//0.7
	// // mIdCardQualityAssessment.mIsIdcard = 0.9f;//0.5
	// // mIdCardQualityAssessment.mInBound = 0.1f;//0.6
	// boolean initSuccess = mIdCardQualityAssessment.init(this,
	// Util.readIdCardModel(this));
	// if (!initSuccess) {
	// mDialogUtil.showDialog("检测器初始化失败");
	// }
	// }

	@Override
	protected void onResume() {
		super.onResume();
		Camera mCamera = mICamera.openCamera(this, MegviiCamera.REAR_CAMERA);
		if (mCamera != null) {
			RelativeLayout.LayoutParams layout_params = mICamera.getLayoutParam(this);
			mTextureView.setLayoutParams(layout_params);
			mNewIndicatorView.setLayoutParams(layout_params);
		} else {
			mDialogUtil.showDialogForActivity("打开摄像头失败");
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mICamera.closeCamera();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDialogUtil.onDestory();
		if (mDecoder != null) {
			mDecoder.interrupt();
			try {
				mDecoder.join();
				mDecoder = null;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (mIdCardQualityAssessment != null) {
			mIdCardQualityAssessment.release();
			mIdCardQualityAssessment = null;
		}
		mICamera.closeCamera();
	}

	private void doPreview() {
		if (!mHasSurface) {
			return;
		}
		mICamera.startPreview(mTextureView.getSurfaceTexture());
	}

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
		mHasSurface = true;
		doPreview();

		mICamera.actionDetect(this);// 开始检测并setPreviewCallback
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
		mHasSurface = false;
		return false;
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surface) {

	}

	@Override
	public void onPreviewFrame(final byte[] data, Camera camera) {

		mFrameDataQueue.offer(data);
	}

	private class DecodeThread extends Thread {
		boolean mHasSuccess = false;
		// int mCount = 0;
		// int mTimSum = 0;
		private IDCardQualityResult.IDCardFailedType mLstErrType;

		@Override
		public void run() {
			byte[] imgData = null;
			try {
				while ((imgData = mFrameDataQueue.take()) != null) {
					if (mHasSuccess) {
						return;
					}
					int imageWidth = mICamera.mCameraWidth;
					int imageHeight = mICamera.mCameraHeight;
					// if (mIsVertical) {
					// imgData = RotaterUtil.rotate(imgData, imageWidth,
					// imageHeight,
					// mICamera.getCameraAngle(YxScanIDCardActivity.this));
					// imageWidth = mICamera.mCameraHeight;
					// imageHeight = mICamera.mCameraWidth;
					// }
					// long start = System.currentTimeMillis();
					RectF rectF = mNewIndicatorView.getPosition();// 没有考虑竖屏
					Rect roi = new Rect();
					roi.left = (int) (rectF.left * imageWidth);
					roi.top = (int) (rectF.top * imageHeight);
					roi.right = (int) (rectF.right * imageWidth);
					roi.bottom = (int) (rectF.bottom * imageHeight);
					if (!isEven01(roi.left))
						roi.left = roi.left + 1;
					if (!isEven01(roi.top))
						roi.top = roi.top + 1;
					if (!isEven01(roi.right))
						roi.right = roi.right - 1;
					if (!isEven01(roi.bottom))
						roi.bottom = roi.bottom - 1;

					final IDCardQualityResult result = mIdCardQualityAssessment.getQuality(imgData, imageWidth, imageHeight, mSide, roi);
					// long end = System.currentTimeMillis();
					// mCount++;
					// mTimSum += (end - start);
					if (result.isValid()) {// 是否合格
						mHasSuccess = true;
						mHandler.sendEmptyMessage(CANCEL_ANIM);
						handleSuccess(result);
						return;
					} else {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								List<IDCardQualityResult.IDCardFailedType> failTypes = result.fails;
								if (failTypes != null) {
									// StringBuilder stringBuilder = new
									// StringBuilder();
									IDCardQualityResult.IDCardFailedType errType = result.fails.get(0);
									if (errType != mLstErrType) {
										// Util.showToast(IDCardScanActivity.this,
										// Util.errorType2HumanStr(result.fails.get(0),
										// mSide));
										mPrompt.setText(Util.errorType2HumanStr(result.fails.get(0), mSide));

										//										new StatisticalTime.Builder().addContext(ScanIDCardActivity.this)
										//												.addOperPageName(mSide == IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT
										//														? "身份证正面扫描" : "身份证反面扫描")
										//												.addErrorInfo(Util.errorType2HumanStr(result.fails.get(0), mSide))
										//												.addOperElementType("scan").addOperElementName("scan").addSessionId("0")
										//												.build();
										mPScanIDCardActivity.showError(mSide, Util.errorType2HumanStr(result.fails.get(0), mSide), "无");
										mLstErrType = errType;
									}
									// errorType.setText(stringBuilder.toString());
								}
								// if (mCount != 0) {
								// fps.setText((1000 * mCount / mTimSum) + "
								// FPS");
								// }
							}
						});
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private void handleSuccess(IDCardQualityResult result) {
		// intent.putExtra("idcardImg",
		// Util.bmp2byteArr(result.croppedImageOfIDCard()));//扣出图像中身份证的部分。
		// intent.putExtra("portraitImg",
		// Util.bmp2byteArr(result.croppedImageOfPortrait()));//扣出身份证中人脸的部分。
		int side = mSide == IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT ? 0 : 1;
		mPScanIDCardActivity.handleResult(side, mHandler, result);
	}

	public static void startMe(Context context, IDCardAttr.IDCardSide side) {
		if (side == null || context == null)
			return;
		Intent intent = new Intent(context, ScanIDCardActivity.class);
		intent.putExtra("side", side == IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT ? 0 : 1);
		intent.putExtra("scanType", mScanType);
		intent.putExtra("appNo", mAppNo);
		intent.putExtra("categoryCode", mCategoryCode);
		context.startActivity(intent);
	}

	// 用取余运算
	public boolean isEven01(int num) {
		if (num % 2 == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static YxCallBack mYxCallBack;

	public static void setCallback(YxCallBack callBack) {
		mYxCallBack = callBack;
	}
}