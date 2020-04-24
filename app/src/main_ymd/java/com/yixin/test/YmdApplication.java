package com.yixin.test;

import android.app.Application;

import com.moxie.client.manager.MoxieSDK;


/**
 * Created by xiaochangyou on 2017/9/20.
 */

public class YmdApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        //初始化MoxieSDK
        MoxieSDK.init(this);
    }
}
