package com.yxjr.credit.ocr.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.yxjr.credit.log.YxLog;
import com.yxjr.credit.util.YxDensityUtil;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.view.Surface;
import android.widget.RelativeLayout;

@SuppressWarnings("deprecation")
public class MegviiCamera {

	private int mCameraId = 0;// 0=后置摄像头1=前置摄像头
	//	private boolean mIsVertical = false;

	public Camera mCamera = null;
	public int mCameraWidth = 0;
	public int mCameraHeight = 0;

	private int screenWidth;
	private int screenHeight;

	public final static int FRONT_CAMERA = 1;//前置摄像头
	public final static int REAR_CAMERA = 0;//后置摄像头

	//	public MegviiCamera(boolean isVertical) {
	//		this.mIsVertical = isVertical;
	//		//不允许更改横竖屏
	//	}

	/**
	 * 打开相机
	 */
	public Camera openCamera(Activity activity, int cameraId) {
		this.mCameraId = cameraId;
		if (mCameraId == FRONT_CAMERA) {//前置-活体验证
			try {
				mCamera = Camera.open(mCameraId);
				CameraInfo cameraInfo = new CameraInfo();
				Camera.getCameraInfo(mCameraId, cameraInfo);
				Parameters params = mCamera.getParameters();
				int width = ScreenUtil.getWidth(activity, true);
				int height = ScreenUtil.getHeight(activity, true) - YxDensityUtil.dipToPx(activity, 45);
				Camera.Size bestPreviewSize = calBestPreviewSize(mCamera.getParameters(), width, height);
				mCameraWidth = bestPreviewSize.width;
				mCameraHeight = bestPreviewSize.height;
				params.setPreviewSize(mCameraWidth, mCameraHeight);
				mCamera.setDisplayOrientation(getCameraAngle(activity));
				mCamera.setParameters(params);
				return mCamera;
			} catch (Exception e) {
				return null;
			}
		} else if (mCameraId == REAR_CAMERA) {//后置-身份证
			try {
				screenWidth = activity.getWindowManager().getDefaultDisplay().getWidth();
				screenHeight = activity.getWindowManager().getDefaultDisplay().getHeight();
				mCamera = Camera.open(mCameraId);
				CameraInfo cameraInfo = new CameraInfo();
				Camera.getCameraInfo(mCameraId, cameraInfo);
				Parameters params = mCamera.getParameters();
				Camera.Size bestPreviewSize = getNearestRatioSize(mCamera.getParameters(), screenWidth, screenHeight);
				mCameraWidth = bestPreviewSize.width;
				mCameraHeight = bestPreviewSize.height;
				params.setPreviewSize(mCameraWidth, mCameraHeight);
				List<String> focusModes = params.getSupportedFocusModes();
				if (focusModes.contains(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
					params.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
				}
				mCamera.setDisplayOrientation(getCameraAngle(activity));
				mCamera.setParameters(params);
				return mCamera;
			} catch (Exception e) {
				return null;
			}
		} else {
			return null;
		}
	}

	// 通过屏幕参数、相机预览尺寸计算布局参数
	public RelativeLayout.LayoutParams getLayoutParam(Context context) {
		if (mCameraId == FRONT_CAMERA) {//前置-活体验证
			Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
			float scale = Math.min(ScreenUtil.getWidth(context, true) * 1.0f / previewSize.height, (ScreenUtil.getHeight(context, true) - YxDensityUtil.dipToPx(context, 45)) * 1.0f / previewSize.width);//高度要减顶部导航栏高度
			int layout_width = (int) previewSize.height;
			int layout_height = (int) (scale * previewSize.width);
			RelativeLayout.LayoutParams layout_params = new RelativeLayout.LayoutParams(layout_width, layout_height);
			return layout_params;
		} else if (mCameraId == REAR_CAMERA) {//后置-身份证
			float scale = mCameraWidth * 1.0f / mCameraHeight;
			//			Screen.initialize(context);
			int layout_height = ScreenUtil.getWidth(context, true);
			int layout_width = (int) (layout_height * 1.0f * scale);
			//			if (!mIsVertical) {
			//				layout_height = Screen.mWidth;
			//				layout_width = (int) (layout_height * 1.0f * scale);
			//			}
			RelativeLayout.LayoutParams layout_params = new RelativeLayout.LayoutParams(layout_width, layout_height);
			return layout_params;
		} else {
			YxLog.e("is error==============================");
			return new RelativeLayout.LayoutParams(mCameraWidth, mCameraHeight);
		}
	}

	public void autoFocus() {
		if (mCameraId == REAR_CAMERA) {//后置-身份证
			try {
				if (mCamera != null) {
					Parameters parameters = mCamera.getParameters();
					List<String> focusModes = parameters.getSupportedFocusModes();
					if (focusModes.contains(Parameters.FOCUS_MODE_AUTO)) {
						parameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
						mCamera.cancelAutoFocus();
						parameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
						mCamera.setParameters(parameters);
						mCamera.autoFocus(null);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void startPreview(SurfaceTexture surfaceTexture) {
		if (mCamera != null) {
			try {
				mCamera.setPreviewTexture(surfaceTexture);
				mCamera.startPreview();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 开始检测脸
	 */
	public void actionDetect(Camera.PreviewCallback mActivity) {
		if (mCamera != null) {
			mCamera.setPreviewCallback(mActivity);
		}
	}

	public void closeCamera() {
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
		}
	}

	//	public RelativeLayout.LayoutParams getParams(Camera camera) {
	//		Camera.Parameters camPara = camera.getParameters();
	//		// 注意Screen是否初始化
	//		Camera.Size bestPreviewSize = calBestPreviewSize(camPara, Screen.mWidth, Screen.mHeight);
	//		mCameraWidth = bestPreviewSize.width;
	//		mCameraHeight = bestPreviewSize.height;
	//		camPara.setPreviewSize(mCameraWidth, mCameraHeight);
	//		camera.setParameters(camPara);
	//
	//		float scale = bestPreviewSize.width / bestPreviewSize.height;
	//
	//		RelativeLayout.LayoutParams layoutPara = new RelativeLayout.LayoutParams((int) (bestPreviewSize.width), (int) (bestPreviewSize.width / scale));
	//
	//		layoutPara.addRule(RelativeLayout.CENTER_HORIZONTAL);// 设置照相机水平居中
	//		return layoutPara;
	//	}

	/**
	 * 通过传入的宽高算出最接近于宽高值的相机大小
	 */
	private Camera.Size calBestPreviewSize(Parameters camPara, final int width, final int height) {
		List<Camera.Size> allSupportedSize = camPara.getSupportedPreviewSizes();
		ArrayList<Camera.Size> widthLargerSize = new ArrayList<Camera.Size>();
		for (Camera.Size tmpSize : allSupportedSize) {
			if (tmpSize.width > tmpSize.height) {
				widthLargerSize.add(tmpSize);
			}
		}

		Collections.sort(widthLargerSize, new Comparator<Camera.Size>() {
			@Override
			public int compare(Camera.Size lhs, Camera.Size rhs) {
				int off_one = Math.abs(lhs.width * lhs.height - width * height);
				int off_two = Math.abs(rhs.width * rhs.height - width * height);
				return off_one - off_two;
			}
		});

		return widthLargerSize.get(0);
	}

	public static Camera.Size getNearestRatioSize(Parameters para, final int screenWidth, final int screenHeight) {
		List<Camera.Size> supportedSize = para.getSupportedPreviewSizes();
		for (Camera.Size tmp : supportedSize) {
			if (tmp.width == 1280 && tmp.height == 720) {
				return tmp;
			}
		}
		Collections.sort(supportedSize, new Comparator<Camera.Size>() {
			@Override
			public int compare(Camera.Size lhs, Camera.Size rhs) {
				int diff1 = (((int) ((1000 * (Math.abs(lhs.width / (float) lhs.height - screenWidth / (float) screenHeight))))) << 16) - lhs.width;
				int diff2 = (((int) (1000 * (Math.abs(rhs.width / (float) rhs.height - screenWidth / (float) screenHeight)))) << 16) - rhs.width;
				return diff1 - diff2;
			}
		});
		return supportedSize.get(0);
	}

	public Bitmap getBitMap(byte[] data, Camera camera, boolean mIsFrontalCamera) {
		int width = camera.getParameters().getPreviewSize().width;
		int height = camera.getParameters().getPreviewSize().height;
		YuvImage yuvImage = new YuvImage(data, camera.getParameters().getPreviewFormat(), width, height, null);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		yuvImage.compressToJpeg(new Rect(0, 0, width, height), 80, byteArrayOutputStream);
		byte[] jpegData = byteArrayOutputStream.toByteArray();
		// 获取照相后的bitmap
		Bitmap tmpBitmap = BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length);
		Matrix matrix = new Matrix();
		matrix.reset();
		if (mIsFrontalCamera) {
			matrix.setRotate(-90);
		} else {
			matrix.setRotate(90);
		}
		tmpBitmap = Bitmap.createBitmap(tmpBitmap, 0, 0, tmpBitmap.getWidth(), tmpBitmap.getHeight(), matrix, true);
		tmpBitmap = tmpBitmap.copy(Bitmap.Config.ARGB_8888, true);

		int hight = tmpBitmap.getHeight() > tmpBitmap.getWidth() ? tmpBitmap.getHeight() : tmpBitmap.getWidth();

		float scale = hight / 800.0f;

		if (scale > 1) {
			tmpBitmap = Bitmap.createScaledBitmap(tmpBitmap, (int) (tmpBitmap.getWidth() / scale), (int) (tmpBitmap.getHeight() / scale), false);
		}
		return tmpBitmap;
	}

	//	/**
	//	 * 打开前置或后置摄像头
	//	 */
	//	public Camera getCameraSafely(int cameraId) {
	//		Camera camera = null;
	//		try {
	//			camera = Camera.open(cameraId);
	//		} catch (Exception e) {
	//			camera = null;
	//		}
	//		return camera;
	//	}

	/**
	 * 获取照相机旋转角度
	 */
	public int getCameraAngle(Activity activity) {
		int rotateAngle = 90;
		CameraInfo info = new CameraInfo();
		Camera.getCameraInfo(mCameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
			rotateAngle = (info.orientation + degrees) % 360;
			rotateAngle = (360 - rotateAngle) % 360; // compensate the mirror
		} else { // back-facing
			rotateAngle = (info.orientation - degrees + 360) % 360;
		}
		return rotateAngle;
	}

}