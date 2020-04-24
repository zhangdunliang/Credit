package com.yxjr.credit.ocr.util;

import java.util.ArrayList;
import java.util.Collections;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.megvii.livenessdetection.Detector;
import com.megvii.livenessdetection.Detector.DetectionType;
import com.yxjr.credit.plugin.StatisticalTime;
import com.yxjr.credit.ui.view.ResContainer;

/**
 * 实体验证工具类
 */
public class IDetection {

	private View rootView;
	private Context mContext;
	private ResContainer R;

	private int mStepsnum = 3;// 动作数量
	public int mCurShowIndex = -1;// 现在底部展示试图的索引值
	public ArrayList<DetectionType> mDetectionSteps;// 活体检测动作列表

	private TextView detectionNameText;
	private String detectionNameStr;

	public IDetection(Context context, View view) {
		this.mContext = context;
		this.rootView = view;
		this.R = ResContainer.get(mContext);
	}

	public void changeType(final DetectionType detectiontype, long timeout) {
		mCurShowIndex = mCurShowIndex == -1 ? 0 : (mCurShowIndex == 0 ? 1 : 0);
		initAnim(detectiontype, null);
	}

	private void initAnim(DetectionType detectiontype, View layoutView) {
		rootView.findViewById(R.id("liveness_layout_promptText")).setVisibility(View.GONE);
		detectionNameText = (TextView) rootView.findViewById(R.id("detection_step_name"));
		detectionNameText.setVisibility(View.VISIBLE);
		Animation animationIN = AnimationUtils.loadAnimation(mContext, R.anim("yx_credit_anim_rightin"));
		detectionNameText.setAnimation(animationIN);
		detectionNameStr = getDetectionName(detectiontype);
		showTxt(detectionNameStr);

	}

	public void checkFaceTooLarge(boolean isLarge) {
		if (detectionNameStr != null && detectionNameText != null) {
			if (isLarge && !detectionNameText.getText().toString().equals("请再离远一些")) {
				showTxt("请再离远一些");
			} else if (!isLarge && detectionNameText.getText().toString().equals("请再离远一些")) {
				showTxt(detectionNameStr);
			}
		}
	}

	private void showTxt(String str) {
		new StatisticalTime.Builder().addContext(mContext).addErrorInfo(str).addOperPageName("Face++人脸识别")
				.addOperElementType("ocr").addOperElementName("活体扫描").addSessionId("无").build();
		detectionNameText.setText(str);
	}

	// 无动画，弃用
	// private Drawable getDrawRes(Detector.DetectionType detectionType) {
	// int resID = -1;
	// switch (detectionType) {
	// case POS_PITCH://上下点头
	// case POS_PITCH_UP://向上抬头
	// case POS_PITCH_DOWN://向下点头
	// resID = R.drawable.liveness_head_pitch;
	// break;
	// case POS_YAW_LEFT://向左摇头
	// case POS_YAW_RIGHT://向右摇头
	// case POS_YAW://左右摇头
	// resID = R.drawable.liveness_head_yaw;
	// break;
	// case MOUTH://MOUTH
	// resID = R.drawable.liveness_mouth_open_closed;
	// break;
	// case BLINK://眨眼
	// resID = R.drawable.liveness_eye_open_closed;
	// break;
	// }
	// Drawable cachedDrawAble = mDrawableCache.get(resID);
	// if (cachedDrawAble != null)
	// return cachedDrawAble;
	// else {
	// Drawable drawable = mContext.getResources().getDrawable(resID);
	// mDrawableCache.put(resID, (drawable));
	// return null;
	// }

	// 返回检测的动作名字
	@SuppressWarnings("incomplete-switch")
	private String getDetectionName(DetectionType detectionType) {
		String detectionName = null;
		switch (detectionType) {
		case POS_PITCH:
			detectionName = "缓慢点头";
			break;
		case POS_PITCH_UP:
			detectionName = "向上抬头";
			break;
		case POS_PITCH_DOWN:
			detectionName = " 向下点头";
			break;
		case POS_YAW:
			detectionName = "左右摇头";
			break;
		case POS_YAW_LEFT:
			detectionName = "向左摇头";
			break;
		case POS_YAW_RIGHT:
			detectionName = "向右摇头";
			break;
		case MOUTH:
			detectionName = "张嘴";
			break;
		case BLINK:
			detectionName = "眨眼";
			break;
		}
		return detectionName;
	}

	/**
	 * 初始化检测动作
	 */
	public void detectionTypeInit() {
		ArrayList<DetectionType> tmpTypes = new ArrayList<DetectionType>();
		tmpTypes.add(DetectionType.BLINK);// 眨眼
		tmpTypes.add(DetectionType.MOUTH);// 张嘴
		tmpTypes.add(DetectionType.POS_PITCH);// 缓慢点头/上下点头
		// tmpTypes.add(Detector.DetectionType.POS_PITCH_UP);// 向上抬头
		// tmpTypes.add(Detector.DetectionType.POS_PITCH_DOWN);// 向下点头
		tmpTypes.add(DetectionType.POS_YAW);// 左右摇头
		// tmpTypes.add(Detector.DetectionType.POS_YAW_LEFT);// 向左摇头
		// tmpTypes.add(Detector.DetectionType.POS_YAW_RIGHT);// 向右摇头
		Collections.shuffle(tmpTypes);// 打乱顺序

		mDetectionSteps = new ArrayList<DetectionType>(mStepsnum);
		for (int i = 0; i < mStepsnum; i++) {
			mDetectionSteps.add(tmpTypes.get(i));
		}
	}

	public void onDestroy() {
		rootView = null;
		mContext = null;
		// if (mDrawableCache != null) {
		// mDrawableCache.clear();
		// }
	}
}
