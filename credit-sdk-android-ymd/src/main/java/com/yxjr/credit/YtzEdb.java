package com.yxjr.credit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import com.yxjr.credit.constants.HttpConstant;
import com.yxjr.credit.constants.SpConstant;
import com.yxjr.credit.constants.YxConstant;
import com.yxjr.credit.http.manage.RequestEngine;
import com.yxjr.credit.http.manage.RequestCallBack;
import com.yxjr.credit.log.YxLog;
import com.yxjr.credit.ui.YxActivityManager;
import com.yxjr.credit.util.YxCommonUtil;
import com.yxjr.credit.util.YxStoreUtil;

public class YtzEdb {

    private Context mContext;
    private Bundle mBundle = null;
    private YtzCallBack mCallBack = null;
    private String mRealName = null;
    private String mIdCardNum = null;
    private String mPhoneNum = null;

    @SuppressLint("DefaultLocale")
    protected YtzEdb(Context context, Bundle bundle, YtzCallBack callBack) {
        this.mContext = context;
        this.mBundle = bundle;
        this.mCallBack = callBack;
        YxCommonUtil.processParam(mContext, mBundle);
        mRealName = mBundle.getString(YxConstant.PARTNER_REAL_NAME).trim();
        mIdCardNum = mBundle.getString(YxConstant.PARTNER_ID_CARD_NUM).toUpperCase().trim();//将身份证号里的所有小写转成大写
        mPhoneNum = mBundle.getString(YxConstant.PARTNER_PHONE_NUMBER).trim();
    }

    public void startUser() {
        initServer(mContext);
    }

    public void initServer(final Context context) {
        new RequestEngine(context).executeInitToken(new RequestCallBack(context) {
            @Override
            public void onSucces(String result) {
                product(context);
                super.onSucces(result);
            }

            @Override
            public void onFailure(String errorCode, String errorMsg) {
                mCallBack.onFailure(errorMsg);
                YxActivityManager.finishAllActivity();
                super.onFailure(errorCode, errorMsg);
            }
        });
    }

    private void product(final Context context) {
        new RequestEngine(context).execute(HttpConstant.Request.GET_PRODUCT, new JSONObject(), new RequestCallBack(context) {
            @Override
            public void onSucces(String result) {
                String productType = null;//产品类型
                String productId = null;//产品ID
                String productIdChild = null;//产品子ID
                try {
                    JSONObject obj = new JSONObject(result);
                    String serviceBody = obj.getString("serviceBody");
                    JSONArray arr = new JSONArray(serviceBody);
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject temp = (JSONObject) arr.get(i);
                        String isDefault = temp.getString("isDefault");
                        if (isDefault.equals("1")) {
                            productType = temp.getString("productType");
                            productId = temp.getString("productId");
                            productIdChild = temp.getString("productIdChild");
                            break;
                        }
                    }
                    if (null != productId && null != productIdChild && null != productType) {
                        YxStoreUtil.save(mContext, SpConstant.PRODUCT_TYPE, productType);
                        YxStoreUtil.save(mContext, SpConstant.PRODUCT_ID, productId);
                        YxStoreUtil.save(mContext, SpConstant.PRODUCT_ID_CHILD, productIdChild);
                        //
                        JSONObject json = new JSONObject();
                        try {
                            json.put("name", mRealName);
                            json.put("cert", mIdCardNum);
                            json.put("mobileNo", mPhoneNum);
                        } catch (Exception e) {
                            e.printStackTrace();
                            mCallBack.onFailure("构建失败1");
                            return;
                        }
                        user(context, json);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mCallBack.onFailure("构建失败2");
                    YxLog.d("edb======product onAfter error" + e.toString());
                }
                super.onSucces(result);
            }

            @Override
            public void onFailure(String errorCode, String errorMsg) {
                // TODO Auto-generated method stub
                mCallBack.onFailure(errorMsg);
                super.onFailure(errorCode, errorMsg);
            }
        });
    }

    private void user(final Context context, JSONObject json) {
        new RequestEngine(context).execute(HttpConstant.Request.GET_USER_STATE, json, new RequestCallBack(context) {
            @Override
            public void onSucces(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    obj.put("productType", YxStoreUtil.get(context, SpConstant.PRODUCT_TYPE));
                    obj.put("productId", YxStoreUtil.get(context, SpConstant.PRODUCT_ID));
                    obj.put("productIdChild", YxStoreUtil.get(context, SpConstant.PRODUCT_ID_CHILD));
                    mCallBack.onSuccess(obj.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    mCallBack.onFailure("构建失败2.1");
                    YxLog.d("edb======user onAfter error" + e.toString());
                }
            }

            public void onFailure(String errorCode, String errorMsg) {
                mCallBack.onFailure(errorMsg);
            }
        });
    }

    public void startAccount() {
        String productType = YxStoreUtil.get(mContext, SpConstant.PRODUCT_TYPE);
        if (productType != null) {
            JSONObject json = new JSONObject();
            try {
                json.put("productType", productType);
                json.put("cert", mIdCardNum);
            } catch (Exception e) {
                e.printStackTrace();
                mCallBack.onFailure("构建失败3");
                return;
            }
            account(mContext, json);
        } else {
            mCallBack.onFailure("查询失败！类型空");
        }
    }

    private void account(final Context context, final JSONObject json) {
        new RequestEngine(context).execute(HttpConstant.Request.GET_ACCOUNT_STATE, json, new RequestCallBack(context) {
            @Override
            public void onSucces(String result) {
                mCallBack.onSuccess(result);
            }

            public void onFailure(String errorCode, String errorMsg) {
                mCallBack.onFailure(errorMsg);
            }
        });
    }

    public void startContract() {
        try {
            contract(mContext, new JSONObject().put("cert", mIdCardNum));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void contract(final Context context, final JSONObject json) {
        new RequestEngine(context).execute(HttpConstant.Request.GET_CONTRACT_LIST, json, new RequestCallBack(context) {
            @Override
            public void onSucces(String result) {
                mCallBack.onSuccess(result);
            }

            public void onFailure(String errorCode, String errorMsg) {
                mCallBack.onFailure(errorMsg);
            }
        });
    }


    public void startGetParam() {
        JSONObject json = new JSONObject();
        try {
            json.put("name", mRealName);
            json.put("cert", mIdCardNum);
            json.put("mobileNo", mPhoneNum);
        } catch (Exception e) {
            e.printStackTrace();
            mCallBack.onFailure("构建失败3");
            return;
        }
        getParam(mContext, json);
    }

    private void getParam(final Context context, final JSONObject json) {
        new RequestEngine(context).execute(HttpConstant.Request.GET_PARAM, json, new RequestCallBack(context) {
            @Override
            public void onSucces(String result) {
                mCallBack.onSuccess(result);
            }

            public void onFailure(String errorCode, String errorMsg) {
                mCallBack.onFailure(errorMsg);
            }
        });
    }

}
