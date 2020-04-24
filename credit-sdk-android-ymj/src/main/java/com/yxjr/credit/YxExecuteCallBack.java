package com.yxjr.credit;

public abstract interface YxExecuteCallBack {

	public abstract void onSuccess();

	public abstract void onFailure(String errorCode, String errorMsg);

}
