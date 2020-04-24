package com.yxjr.credit.ui;

import java.io.File;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.megvii.licensemanager.Manager;
import com.megvii.livenessdetection.DetectionConfig;
import com.megvii.livenessdetection.DetectionFrame;
import com.megvii.livenessdetection.Detector;
import com.megvii.livenessdetection.LivenessLicenseManager;
import com.megvii.livenessdetection.Detector.DetectionFailedType;
import com.megvii.livenessdetection.Detector.DetectionListener;
import com.megvii.livenessdetection.Detector.DetectionType;
import com.megvii.livenessdetection.FaceQualityManager;
import com.megvii.livenessdetection.FaceQualityManager.FaceQualityErrorType;
import com.megvii.livenessdetection.bean.FaceIDDataStruct;
import com.megvii.livenessdetection.bean.FaceInfo;
import com.yxjr.credit.constants.HttpConstant;
import com.yxjr.credit.constants.JsConstant;
import com.yxjr.credit.constants.SpConstant;
import com.yxjr.credit.http.manage.RequestEngine;
import com.yxjr.credit.http.manage.RequestCallBack;
import com.yxjr.credit.http.manage.UploadCallBack;
import com.yxjr.credit.log.YxLog;
import com.yxjr.credit.ocr.util.IDetection;
import com.yxjr.credit.ocr.util.IMediaPlayer;
import com.yxjr.credit.ocr.util.MegviiCamera;
import com.yxjr.credit.ocr.util.MegviiUtil;
import com.yxjr.credit.ocr.util.SensorUtil;
import com.yxjr.credit.ocr.util.Util;
import com.yxjr.credit.plugin.StatisticalTime;
import com.yxjr.credit.ui.view.DialogLoading;
import com.yxjr.credit.ui.view.ResContainer;
import com.yxjr.credit.util.DialogUtil;
import com.yxjr.credit.util.StringUtil;
import com.yxjr.credit.util.ToastUtil;
import com.yxjr.credit.util.YxCommonUtil;
import com.yxjr.credit.util.YxStoreUtil;
import com.yxjr.credit.widget.AutoRatioImageview;
import com.yxjr.credit.widget.CircleProgressBar;
import com.yxjr.credit.widget.FaceMask;
import com.yxjr.credit.widget.RotaterView;

@SuppressWarnings("deprecation")
public class LivenessActivity extends YxBaseActivity implements PreviewCallback, DetectionListener, TextureView.SurfaceTextureListener {

	private TextureView mCameraPreview;
	private FaceMask mFaceMask;// 画脸位置的类（调试时会用到）
	private ProgressBar mProgressBar;// 网络上传请求验证时出现的ProgressBar
	private LinearLayout mHeadViewLinear;// "请在光线充足的情况下进行检测"这个视图
	private RelativeLayout mRootView;// 根视图
	private TextView mTimeOutText;// 倒计时文字
	private CircleProgressBar mTimeOutBar;// 倒计时控件
	private RelativeLayout mTimeOutRel;// 倒计时部分
	private TextView mPromptText;// 错误提示
	RelativeLayout mRresultLayout;// 返回结果区域

	private Detector mDetector;// 活体检测器
	private MegviiCamera mICamera;// 照相机工具类
	private Handler mainHandler;
	@SuppressWarnings("unused")
	private Handler mHandler;
	private HandlerThread mHandlerThread = new HandlerThread("videoEncoder");
	private IMediaPlayer mIMediaPlayer;// 多媒体工具类
	private IDetection mIDetection;// 实体验证工具类
	private DialogUtil mDialogUtil;

	private boolean isHandleStart;// 是否开始检测
	private FaceQualityManager mFaceQualityManager;
	private SensorUtil mSensorUtil;

	private LinearLayout mWarrantyLin;
	private ProgressBar mWarrantyBar;
	private TextView mWarrantyText;
	private Button mWarrantyAgainBtn;

	private TextView mTitle;

	private TextView mResultTxt;

	private RotaterView mRotaterView;

	private final int TOP_ID = 1;

	private final int WARRANTY_SUCCES = 1;
	private final int WARRANTY_FAIL = 2;

	@SuppressLint("HandlerLeak")
	Handler mWarrantyHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WARRANTY_SUCCES:
				if (mWarrantyLin.getVisibility() == View.VISIBLE) {
					mWarrantyLin.setVisibility(View.GONE);
				}
				mCameraPreview.setSurfaceTextureListener(LivenessActivity.this);
				break;
			case WARRANTY_FAIL:
				mWarrantyAgainBtn.setVisibility(View.VISIBLE);
				mWarrantyBar.setVisibility(View.GONE);
				mWarrantyText.setText("联网授权失败，请点击按钮重新授权");
				break;
			}
		}
	};

	private ResContainer R;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 无标题
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏锁定
		this.R = ResContainer.get(this);
		setContentView(R.layout("yxjr_credit_liveness"));
		init();
		netWorkWarranty();
	}

	private void findView() {
		//
		mRootView = (RelativeLayout) findViewById(R.id("liveness_layout_rootRel"));
		mFaceMask = (FaceMask) findViewById(R.id("liveness_layout_facemask"));
		mFaceMask.setVisibility(View.GONE);// 仅调试时用
		mPromptText = (TextView) findViewById(R.id("liveness_layout_promptText"));
		mCameraPreview = (TextureView) findViewById(R.id("liveness_layout_textureview"));
		// mCameraPreview.setSurfaceTextureListener(this);
		mProgressBar = (ProgressBar) findViewById(R.id("liveness_layout_progressbar"));
		mProgressBar.setVisibility(View.INVISIBLE);
		mHeadViewLinear = (LinearLayout) findViewById(R.id("liveness_layout_bottom_tips_head"));
		mHeadViewLinear.setVisibility(View.VISIBLE);
		mTimeOutRel = (RelativeLayout) findViewById(R.id("detection_step_timeoutRel"));
		mTimeOutText = (TextView) findViewById(R.id("detection_step_timeout_garden"));
		mTimeOutBar = (CircleProgressBar) findViewById(R.id("detection_step_timeout_progressBar"));
		mRresultLayout = (RelativeLayout) findViewById(R.id("liveness_layout_result"));

		LinearLayout back = (LinearLayout) findViewById(R.id("yx_credit_liveness_back"));
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mTitle = (TextView) findViewById(R.id("yx_credit_liveness_title"));
		mResultTxt = (TextView) findViewById(R.id("liveness_layout_result_str"));
		mRotaterView = (RotaterView) findViewById(R.id("liveness_layout_result_rotater"));
	}

	private void init() {
		findView();
		mSensorUtil = new SensorUtil(this);
		mainHandler = new Handler();
		mHandlerThread.start();
		mHandler = new Handler(mHandlerThread.getLooper());
		mIMediaPlayer = new IMediaPlayer(this);
		mDialogUtil = new DialogUtil(this);
		mIDetection = new IDetection(this, mRootView);
		mICamera = new MegviiCamera();
		// mIDetection.viewsInit();
		initLayout();
		initData();
		// netWorkWarranty();
	}

	private String mAppNo = "";
	private String mCategoryCode = "";

	/**
	 * 初始化数据
	 */
	private void initData() {

		Intent intent = getIntent();
		mAppNo = intent.getStringExtra("appNo");
		mAppNo = mAppNo.equals("empty") ? "" : mAppNo;
		mCategoryCode = intent.getStringExtra("categoryCode");
		mCategoryCode = mCategoryCode.equals("empty") ? "" : mCategoryCode;

		// 初始化活体检测器
		DetectionConfig config = new DetectionConfig.Builder().build();
		// DetectionConfig config = new DetectionConfig.Builder().setBlur(arg0,
		// arg1).build();//配置人脸识别数据地方
		mDetector = new Detector(this, config);

		boolean initSuccess = mDetector.init(LivenessActivity.this, MegviiUtil.readModel(LivenessActivity.this, R.raw("meglivemodel")), "");
		if (!initSuccess) {
			mDialogUtil.showDialogForActivity("检测器初始化失败");
		}
	}

	private LivenessLicenseManager licenseManager;

	/**
	 * 联网授权
	 */
	private void netWorkWarranty() {
		// String uuid = MegviiUtil.getUUIDString(this);
		mWarrantyLin = (LinearLayout) findViewById(R.id("yx_credit_live_barLinear"));
		mWarrantyBar = (ProgressBar) findViewById(R.id("yx_credit_live_WarrantyBar"));
		mWarrantyText = (TextView) findViewById(R.id("yx_credit_live_WarrantyText"));
		mWarrantyAgainBtn = (Button) findViewById(R.id("yx_credit_live_againWarrantyBtn"));
		licenseManager = new LivenessLicenseManager(LivenessActivity.this);
		if (licenseManager.checkCachedLicense() > 0)// 已授权
			mWarrantyHandler.sendEmptyMessage(WARRANTY_SUCCES);
		else {
			mDialogUtil.showDialog("是否授权额度宝对您进行人脸识别", "取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					LivenessActivity.this.finish();
				}
			}, "授权", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mWarrantyAgainBtn.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							netWorkWarranty();
						}
					});
					mWarrantyLin.setVisibility(View.VISIBLE);
					mWarrantyAgainBtn.setVisibility(View.GONE);
					mWarrantyText.setText("正在授权...");
					mWarrantyBar.setVisibility(View.VISIBLE);

					new Thread(new Runnable() {
						@Override
						public void run() {
							Manager manager = new Manager(LivenessActivity.this);

							manager.registerLicenseManager(licenseManager);
							manager.takeLicenseFromNetwork(MegviiUtil.getUUIDString(LivenessActivity.this));
							if (licenseManager.checkCachedLicense() > 0)
								mWarrantyHandler.sendEmptyMessage(WARRANTY_SUCCES);
							else
								mWarrantyHandler.sendEmptyMessage(WARRANTY_FAIL);
						}
					}).start();
				}
			});
		}
	}

	// 布局初始化
	private void initLayout() {
		findViewById(R.id("liveness_layout_topBar")).setId(TOP_ID);// 顶部导航栏设置ID

		LayoutParams layout_params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		layout_params.addRule(RelativeLayout.BELOW, TOP_ID);
		mFaceMask.setLayoutParams(layout_params);// 调试时UI
		findViewById(R.id("liveness_layout_result")).setLayoutParams(layout_params);// 返回结果
		// 顶部文字
		LayoutParams hint_layout_params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		hint_layout_params.addRule(RelativeLayout.BELOW, TOP_ID);
		findViewById(R.id("liveness_layout_hint")).setLayoutParams(hint_layout_params);// 顶部文字
		// 人头引导图
		LayoutParams mask_layout_params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		mask_layout_params.addRule(RelativeLayout.BELOW, TOP_ID);
		AutoRatioImageview mask = (AutoRatioImageview) findViewById(R.id("liveness_layout_head_mask"));
		mask.setScaleType(ImageView.ScaleType.FIT_XY);
		mask.setLayoutParams(mask_layout_params);// 人头引导图
	}

	@Override
	protected void onResume() {
		super.onResume();
		isHandleStart = false;
		// 打开照相机
		Camera mCamera = mICamera.openCamera(this, MegviiCamera.FRONT_CAMERA);
		if (mCamera != null) {
			CameraInfo cameraInfo = new CameraInfo();
			Camera.getCameraInfo(MegviiCamera.FRONT_CAMERA, cameraInfo);
			mFaceMask.setFrontal(cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT);
			// 获取到相机分辨率对应的显示大小，并把这个值复制给camerapreview
			// 这版，没有使用相应比例，因为设计为全屏，但是魅族等其他手机不可能铺满，因此不是用比例
			// 如果碰到 人脸变形、拉伸，说明此原因，并重新设计，不能使用全屏。
			// 建议正方形、或圆形，
			// RelativeLayout.LayoutParams camera_layout_params =
			// mICamera.getLayoutParam(this);
			LayoutParams camera_layout_params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			camera_layout_params.addRule(RelativeLayout.BELOW, TOP_ID);
			camera_layout_params.addRule(RelativeLayout.CENTER_HORIZONTAL);
			mCameraPreview.setLayoutParams(camera_layout_params);// 相机预览
			mCameraPreview.setSurfaceTextureListener(this);
			// 初始化人脸质量检测管理类
			mFaceQualityManager = new FaceQualityManager(1 - 0.5f, 0.5f);
			mIDetection.mCurShowIndex = -1;
		} else {
			mDialogUtil.showDialogForActivity("打开前置摄像头失败");
		}
	}

	/**
	 * 开始检测
	 */
	private void handleStart() {
		if (isHandleStart)
			return;
		isHandleStart = true;
		// 开始动画
		// Animation animationIN =
		// AnimationUtils.loadAnimation(LivenessActivity.this,
		// R.anim.yx_credit_anim_rightin);
		Animation animationOut = AnimationUtils.loadAnimation(LivenessActivity.this, R.anim("yx_credit_anim_leftout"));
		mHeadViewLinear.startAnimation(animationOut);
		// mIDetection.mAnimViews[0].setVisibility(View.VISIBLE);
		// mIDetection.mAnimViews[0].startAnimation(animationIN);
		animationOut.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mTimeOutRel.setVisibility(View.VISIBLE);
			}
		});
		// 开始活体检测
		mainHandler.post(mTimeoutRunnable);

	}

	private Runnable mTimeoutRunnable = new Runnable() {
		@Override
		public void run() {
			// 倒计时开始
			initDetecteSession();
			if (mIDetection.mDetectionSteps != null)
				changeType(mIDetection.mDetectionSteps.get(0), 10);
		}
	};

	private void initDetecteSession() {
		if (mICamera.mCamera == null)
			return;

		mProgressBar.setVisibility(View.INVISIBLE);
		mIDetection.detectionTypeInit();// 初始化动作集

		mCurStep = 0;
		mDetector.reset();
		mDetector.changeDetectionType(mIDetection.mDetectionSteps.get(0));//
	}

	/**
	 * 照相机预览数据回调 （PreviewCallback的接口回调方法）
	 */
	@Override
	public void onPreviewFrame(final byte[] data, Camera camera) {
		Size previewsize = camera.getParameters().getPreviewSize();
		// 活体检测器检测
		mDetector.doDetection(data, previewsize.width, previewsize.height, 360 - mICamera.getCameraAngle(this));
	}

	/**
	 * 活体验证成功 （DetectionListener的接口回调方法）
	 */
	@Override
	public DetectionType onDetectionSuccess(final DetectionFrame validFrame) {
		mIMediaPlayer.reset();
		mCurStep++;
		mFaceMask.setFaceInfo(null);

		if (mCurStep == mIDetection.mDetectionSteps.size()) {
			// mProgressBar.setVisibility(View.VISIBLE);
			// getLivenessData();
			handleResult(R.string("verify_success"));
		} else {
			changeType(mIDetection.mDetectionSteps.get(mCurStep), 10);
		}

		// 检测器返回值：如果不希望检测器检测则返回DetectionType.DONE，如果希望检测器检测动作则返回要检测的动作
		return mCurStep >= mIDetection.mDetectionSteps.size() ? DetectionType.DONE : mIDetection.mDetectionSteps.get(mCurStep);
	}

	// private void getLivenessData() {
	// new Thread(new Runnable() {
	// @Override
	// public void run() {
	// final FaceIDDataStruct idDataStruct = mDetector.getFaceIDDataStruct();
	// String delta = idDataStruct.delta;
	// for (String key : idDataStruct.images.keySet()) {
	// byte[] data = idDataStruct.images.get(key);
	// if (key.equals("image_best")) {
	// byte[] imageBestData = data;// 这是最好的一张图片
	// String picName = "P3_" + YxCommonUtil.getCurrentTime2() + ".jpg";
	// files[0] = Util.byte2File(imageBestData, picName);
	// } else if (key.equals("image_env")) {
	// byte[] imageEnvData = data;// 这是一张全景图
	// } else {
	// // 其余为其他图片，根据需求自取
	// }
	// }
	// }
	// }).start();
	// }

	/**
	 * 活体检测失败 （DetectionListener的接口回调方法）
	 */
	@Override
	public void onDetectionFailed(final DetectionFailedType type) {
		int resourceID = R.string("liveness_detection_failed");
		switch (type) {
		case ACTIONBLEND:// 混入了其他不应该出现的动作
			resourceID = R.string("liveness_detection_failed_action_blend");
			break;
		case NOTVIDEO:// 使用非连续的图像进行活体攻击
			resourceID = R.string("liveness_detection_failed_not_video");
			break;
		case TIMEOUT:// 检测超时
			resourceID = R.string("liveness_detection_failed_timeout");
			break;
		case FACELOSTNOTCONTINUOUS:// 人脸时不时的丢失，被算法判定为非连续|不适合提示用户

			break;
		case FACENOTCONTINUOUS:// 由于人脸动作过快导致的非连续|不适合提示用户
			break;
		case MASK:// 面具攻击 |不适合提示用户
			break;
		case TOOMANYFACELOST:// 人脸从拍摄区域消失时间过长|不适合提示用户
			break;
		}
		handleResult(resourceID);
	}

	/**
	 * 活体验证中（这个方法会持续不断的回调，返回照片detection信息） （DetectionListener的接口回调方法）
	 */
	@Override
	public void onFrameDetected(long timeout, DetectionFrame detectionFrame) {
		if (mSensorUtil.isVertical()) {
			faceOcclusion(detectionFrame);
			handleNotPass(timeout);
			mFaceMask.setFaceInfo(detectionFrame);
		} else {
			if (mSensorUtil.Y == 0) {
				showErrorTxt("请打开手机读取运动数据权限", "无");
			} else {
				showErrorTxt("请竖直握紧手机", "无");
			}
		}
	}

	/**
	 * 照镜子环节
	 * 流程：1,先从返回的DetectionFrame中获取FaceInfo。在FaceInfo中可以先判断这张照片上的人脸是否有被遮挡的状况
	 * ,入股有直接return 2,
	 * 如果没有遮挡就把SDK返回的DetectionFramed传入人脸质量检测管理类mFaceQualityManager中获取FaceQualityErrorType的list
	 * 3.通过返回的list来判断这张照片上的人脸是否合格。
	 * 如果返回list为空或list中FaceQualityErrorType的对象数量为0则表示这张照片合格开始进行活体检测
	 */
	private void faceOcclusion(DetectionFrame detectionFrame) {
		mFailFrame++;
		if (detectionFrame != null) {
			FaceInfo faceInfo = detectionFrame.getFaceInfo();
			if (faceInfo != null) {
				if (faceInfo.eyeLeftOcclusion > 0.5 || faceInfo.eyeRightOcclusion > 0.5) {
					if (mFailFrame > 10) {
						mFailFrame = 0;
						showErrorTxt("请勿用手遮挡眼睛", "无");
					}
					return;
				}
				if (faceInfo.mouthOcclusion > 0.5) {
					if (mFailFrame > 10) {
						mFailFrame = 0;
						showErrorTxt("请勿用手遮挡嘴巴", "无");
					}
					return;
				}
				boolean faceTooLarge = faceInfo.faceTooLarge;
				mIDetection.checkFaceTooLarge(faceTooLarge);
			}
		}
		// 从人脸质量检测管理类中获取错误类型list
		faceInfoChecker(mFaceQualityManager.feedFrame(detectionFrame));
	}

	private int mFailFrame = 0;

	public void faceInfoChecker(List<FaceQualityErrorType> errorTypeList) {
		if (errorTypeList == null || errorTypeList.size() == 0)
			handleStart();
		else {
			String infoStr = "";
			FaceQualityErrorType errorType = errorTypeList.get(0);
			if (errorType == FaceQualityErrorType.FACE_NOT_FOUND) {
				infoStr = "请让我看到您的正脸";
			} else if (errorType == FaceQualityErrorType.FACE_POS_DEVIATED) {
				infoStr = "请让我看到您的正脸";
			} else if (errorType == FaceQualityErrorType.FACE_NONINTEGRITY) {
				infoStr = "请让我看到您的正脸";
			} else if (errorType == FaceQualityErrorType.FACE_TOO_DARK) {
				infoStr = "请让光线再亮点";
			} else if (errorType == FaceQualityErrorType.FACE_TOO_BRIGHT) {
				infoStr = "请让光线再暗点";
			} else if (errorType == FaceQualityErrorType.FACE_TOO_SMALL) {
				infoStr = "请再靠近一些";
			} else if (errorType == FaceQualityErrorType.FACE_TOO_LARGE) {
				infoStr = "请再离远一些";
			} else if (errorType == FaceQualityErrorType.FACE_TOO_BLURRY) {
				infoStr = "请避免侧光和背光";
			} else if (errorType == FaceQualityErrorType.FACE_OUT_OF_RECT) {
				infoStr = "请保持脸在人脸框中";
			}

			if (mFailFrame > 10) {
				mFailFrame = 0;
				if (!infoStr.isEmpty()) {
					showErrorTxt(infoStr, "无");
				}
			}
		}
	}

	/**
	 * 跳转Activity传递信息
	 */
	private void handleResult(final int resID) {
		String resultString = getResources().getString(resID);
		boolean isSuccess = resultString.equals(getResources().getString(R.string("verify_success")));
		if (isSuccess) {
			final FaceIDDataStruct idDataStruct = mDetector.getFaceIDDataStruct();
			delta = idDataStruct.delta;
			for (String key : idDataStruct.images.keySet()) {
				byte[] data = idDataStruct.images.get(key);
				if (key.equals("image_best")) {
					byte[] imageBestData = data;// 这是最好的一张图片
					String picName = "P3_" + YxCommonUtil.getCurrentTime2() + ".jpg";
					String filePath = Util.saveJPGFile(this, imageBestData, picName);
					files[0] = new File(filePath);
				} else if (key.equals("image_env")) {
					byte[] imageEnvData = data;// 这是一张全景图
					String picName = "P4_" + YxCommonUtil.getCurrentTime2() + ".jpg";
					String filePath = Util.saveJPGFile(this, imageEnvData, picName);
					files[1] = new File(filePath);
				} else {
					// 其余为其他图片，根据需求自取
				}
			}
		}
		doRotate(isSuccess, resID);
	}

	private void doRotate(final boolean isSuccess, int resID) {
		release();
		if (resID == R.string("verify_success")) {
			mIMediaPlayer.doPlay(R.raw("meglive_success"));
		} else if (resID == R.string("liveness_detection_failed_not_video")) {
			mIMediaPlayer.doPlay(R.raw("meglive_failed"));
		} else if (resID == R.string("liveness_detection_failed_timeout")) {
			mIMediaPlayer.doPlay(R.raw("meglive_failed"));
		} else if (resID == R.string("liveness_detection_failed")) {
			mIMediaPlayer.doPlay(R.raw("meglive_failed"));
		} else {
			mIMediaPlayer.doPlay(R.raw("meglive_failed"));
		}

		mResultTxt.setText(isSuccess ? "识别成功!" : "识别失败!");
		mResultTxt.setTextColor(isSuccess ? 0xff1ab4fb : 0xfff45555);
		mTitle.setText("面部验证结果");
		if (!isSuccess) {
			TextView suggestTxt = (TextView) findViewById(R.id("liveness_layout_suggest"));
			suggestTxt.setVisibility(View.VISIBLE);
			suggestTxt.setText(resID);
		}
		Animation animationIN = AnimationUtils.loadAnimation(this, R.anim("yx_credit_anim_rightin"));

		mRresultLayout.setVisibility(View.VISIBLE);
		mRresultLayout.setAnimation(animationIN);

		final Button resultOkBtn = (Button) findViewById(R.id("liveness_layout_ok"));
		resultOkBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isSuccess) {
					LivenessActivity.this.finish();
				} else {
					if (delta != null) {
						upload(files, delta);
					} else {
						LivenessActivity.this.finish();
					}
				}
			}
		});

		mRotaterView.setColour(isSuccess ? 0xff1ab4fb : 0xfff45555);
		final ImageView statusView = (ImageView) findViewById(R.id("liveness_layout_result_status"));
		statusView.setVisibility(View.INVISIBLE);
		statusView.setImageResource(isSuccess ? R.drawable("yxjr_credit_liveness_result_success") : R.drawable("yxjr_credit_liveness_result_failded"));

		ObjectAnimator objectAnimator = ObjectAnimator.ofInt(mRotaterView, "progress", 0, 100);
		objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
		objectAnimator.setDuration(600);
		objectAnimator.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				Animation scaleanimation = AnimationUtils.loadAnimation(LivenessActivity.this, R.anim("yx_credit_anim_scaleoutin"));
				statusView.startAnimation(scaleanimation);
				statusView.setVisibility(View.VISIBLE);
				mResultTxt.startAnimation(scaleanimation);
				mResultTxt.setVisibility(View.VISIBLE);
				resultOkBtn.startAnimation(scaleanimation);
				resultOkBtn.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}
		});
		objectAnimator.start();
	}

	private int mCurStep = 0;// 检测动作的次数

	public void changeType(final DetectionType detectiontype, long timeout) {
		// 动画切换
		mIDetection.changeType(detectiontype, timeout);
		mFaceMask.setFaceInfo(null);
		// 语音播放
		if (mCurStep == 0) {
			mIMediaPlayer.doPlay(mIMediaPlayer.getSoundRes(detectiontype));
		} else {
			mIMediaPlayer.doPlay(R.raw("meglive_well_done"));
			mIMediaPlayer.setOnCompletionListener(detectiontype);
		}
	}

	public void handleNotPass(final long remainTime) {
		if (remainTime > 0) {
			mainHandler.post(new Runnable() {
				@Override
				public void run() {
					mTimeOutText.setText(remainTime / 1000 + "");
					mTimeOutBar.setProgress((int) (remainTime / 100));
				}
			});
		}
	}

	private boolean mHasSurface = false;

	/**
	 * TextureView启动成功后 启动相机预览和添加活体检测回调
	 * （TextureView.SurfaceTextureListener的接口回调方法）
	 */
	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
		mHasSurface = true;
		doPreview();

		// 添加活体检测回调 （本Activity继承了DetectionListener）
		mDetector.setDetectionListener(this);
		// 添加相机预览回调（本Activity继承了PreviewCallback）
		mICamera.actionDetect(this);
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
	}

	/**
	 * TextureView销毁后 （TextureView.SurfaceTextureListener的接口回调方法）
	 */
	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
		mHasSurface = false;
		return false;
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surface) {
	}

	private void doPreview() {
		if (!mHasSurface)
			return;

		mICamera.startPreview(mCameraPreview.getSurfaceTexture());
	}

	@Override
	protected void onPause() {
		super.onPause();
		mainHandler.removeCallbacksAndMessages(null);
		mICamera.closeCamera();
		mIMediaPlayer.close();

		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mDetector != null)
			mDetector.release();
		mDialogUtil.onDestory();
		mIDetection.onDestroy();
		mIMediaPlayer.close();
		release();
	}

	private void release() {
		// if (mDetector != null)//跳转至结果页时，释放此处报空指针异常
		// mDetector.release();
		mSensorUtil.release();
		mICamera.closeCamera();
	}

	private void showErrorTxt(String error, String sessionId) {
		new StatisticalTime.Builder().addContext(LivenessActivity.this).addErrorInfo(error).addOperPageName("Face++人脸识别").addOperElementType("ocr").addOperElementName("活体扫描").addSessionId(sessionId).build();
		mPromptText.setText(error);
	}

	private static YxCallBack mYxCallBack;

	public static void setCallback(YxCallBack callBack) {
		mYxCallBack = callBack;
	}

	private File[] files = new File[2];
	private String delta = null;

	private void upload(final File[] files, final String delta) {
		final DialogLoading dialogLoading = new DialogLoading(LivenessActivity.this, "上传中...");
		dialogLoading.show();
		new RequestEngine(LivenessActivity.this).upload(mAppNo, files, new UploadCallBack() {
			@Override
			public void onSucces(String result) {
				showErrorTxt("人脸识别图片上传成功", mAppNo);
				String[] filePathAfterSplit = new String[2];
				filePathAfterSplit = result.split(","); // 以“,”作为分隔符来分割date字符串，并把结果放入3个字符串中。
				final JSONObject json = new JSONObject();
				try {
					json.put("mobileNo", YxStoreUtil.get(LivenessActivity.this, SpConstant.PARTNER_PHONENUMBER));// 手机号
					json.put("name", YxStoreUtil.get(LivenessActivity.this, SpConstant.PARTNER_REAL_NAME));// 姓名
					json.put("cert", YxStoreUtil.get(LivenessActivity.this, SpConstant.PARTNER_ID_CARD_NUM));// 身份证
					json.put("userheadPortrait", filePathAfterSplit[1].contains("P3") ? filePathAfterSplit[1] : filePathAfterSplit[0]);//
					json.put("picType", "P3");
					json.put("imageEnv", filePathAfterSplit[0].contains("P4") ? filePathAfterSplit[0] : filePathAfterSplit[1]);
					json.put("delta", delta);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				send(json);
				super.onSucces(result);
			}

			@Override
			public void onFailure(String errorCode, String errorMsg) {
				showErrorTxt("人脸识别图片上传失败" + errorMsg, mAppNo);
				ToastUtil.showToast(LivenessActivity.this, "上传失败！");
				super.onFailure(errorCode, errorMsg);
			}

			@Override
			public void onFinish() {
				dialogLoading.cancel();
				super.onFinish();
			}
		});
	}

	public void send(final JSONObject json) {
		final String mServiceId = StringUtil.isEmpty(mCategoryCode) ? HttpConstant.Request.FACE_INFO : HttpConstant.Request.PATCH_FACE_INFO;
		final DialogLoading dialogLoading = new DialogLoading(LivenessActivity.this);
		dialogLoading.show();
		new RequestEngine(LivenessActivity.this).execute(mServiceId, json, new RequestCallBack(LivenessActivity.this) {
			@Override
			public void onSucces(String result) {
				String responseCode = null;
				String responseMsg = null;
				try {
					JSONObject jsonObject = new JSONObject(result).getJSONObject("serviceHeader");
					responseCode = jsonObject.getString("responseCode");
					responseMsg = jsonObject.getString("responseMsg");
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				if (responseCode != null && responseCode.equals(HttpConstant.Response.SUCCEED)) {
					dialogLoading.cancel();
					mYxCallBack.loadUrl(JsConstant.LIVENESS, null);
					ToastUtil.showToast(LivenessActivity.this, "提交成功");
					YxLog.d("======活体成功执行后！");
					showErrorTxt("人脸识别验证成功", mServiceId);
					LivenessActivity.this.finish();
				} else {
					mYxCallBack.loadUrl(JsConstant.LIVENESS, null);
					dialogLoading.cancel();
					if (responseMsg != null) {
						ToastUtil.showToast(LivenessActivity.this, responseMsg);
					}
					showErrorTxt("人脸识别验证失败，" + responseMsg, mServiceId);
				}
				super.onSucces(result);
			}

			@Override
			public void onFailure(String errorCode, String errorMsg) {
				showErrorTxt("人脸识别验证失败，" + errorMsg, mServiceId);
				dialogLoading.cancel();
				super.onFailure(errorCode, errorMsg);
			}
		});
	}
}