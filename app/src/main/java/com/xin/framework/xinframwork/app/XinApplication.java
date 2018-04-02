package com.xin.framework.xinframwork.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.xin.framework.xinframwork.utils.android.SysUtils;

/**
 * Description :Application
 * Created by xin on 2017/5/16 0016.
 */

public class XinApplication extends Application {
    private static Context sAppContext = null;

    private static AppDelegate mAppDelegate;




    public static Context getAppContext() {
        return sAppContext;
    }






    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);


    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (!getApplicationInfo().packageName.equals(SysUtils.getCurProcessName(this)))
            return;
        sAppContext = getApplicationContext();

        mAppDelegate = new AppDelegate(this);
        mAppDelegate.onCreate();

    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mAppDelegate.onLowMemory();
    }


    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        mAppDelegate.onTrimMemory(level);
    }

    /**
     * 退出应用
     */
    @SuppressWarnings("unused")
    public static void exitApp(){
        mAppDelegate.exit();
    }

}
