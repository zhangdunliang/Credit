package com.yxjr.credit.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

public class ItemLongClickedPopWindow extends PopupWindow {
	public static final int IMAGE_VIEW_POPUPWINDOW = 5;//图片项目弹出菜单
	private LayoutInflater mItemLongClickedPopWindowInflater;
	private View mItemLongClickedPopWindowView;
	private Context mContext;
	private int type;

	/**
	 * 构造函数 * @param context 上下文 * @param width 宽度 * @param height 高度 *
	 */
	public ItemLongClickedPopWindow(Context context, int type, int width, int height) {
		super(context);
		this.mContext = context;
		this.type = type;
		//创建
		this.initTab();
		//设置默认选项
		setWidth(width);
		setHeight(height);
		setContentView(this.mItemLongClickedPopWindowView);
		setOutsideTouchable(true);
		setFocusable(true);
	}

	//实例化
	private void initTab() {
		this.mItemLongClickedPopWindowInflater = LayoutInflater.from(mContext);
		switch (type) {
		case IMAGE_VIEW_POPUPWINDOW: //图片
			this.mItemLongClickedPopWindowView = this.mItemLongClickedPopWindowInflater.inflate(ResContainer.get(mContext).layout("list_item_longclicked_img"), null);
			break;
		default:
			break;
		}
	}

	public View getView(int id) {
		return this.mItemLongClickedPopWindowView.findViewById(id);
	}
}