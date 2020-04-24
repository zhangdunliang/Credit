package com.yxjr.credit;

public abstract interface YtzCallBack {

	public abstract void onStart();

	public abstract void onSuccess(String data);

	public abstract void onFailure(String errorMsg);
}
