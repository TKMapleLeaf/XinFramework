package com.xin.framework.xinframwork.hybrid.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.MutableContextWrapper;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.tencent.sonic.sdk.SonicConfig;
import com.tencent.sonic.sdk.SonicEngine;
import com.tencent.sonic.sdk.SonicSessionConfig;
import com.xin.framework.xinframwork.app.XinApplication;
import com.xin.framework.xinframwork.common.FileConfig;
import com.xin.framework.xinframwork.hybrid.download.WebDownLoadListener;
import com.xin.framework.xinframwork.hybrid.sonic.XinSonicRuntime;
import com.xin.framework.xinframwork.utils.android.SysUtils;
import com.xin.framework.xinframwork.utils.android.logger.Log;
import com.xin.framework.xinframwork.utils.common.assist.Network;
import com.xin.framework.xinframwork.utils.common.utils.FileUtil;

import java.io.File;

/**
 * Description :WebView Config
 * Created by 王照鑫 on 2017/11/1 0001.
 */

public class WebViewConfig implements IWebViewInit {


    private XinWebView mWebView;

    /**
     * 是否允许打电话
     */
    public static final boolean TEL_ENABLE = true;
    /**
     * 是否允许发邮件
     */
    public static final boolean MAIL_ENABLE = true;
    /**
     * 是否允许发短信
     */
    public static final boolean SMS_ENABLE = true;
    /**
     * 是否允许下载
     */
    public static final boolean DOWNLOAD_ENABLE = false;


    private SonicSessionConfig mSessionConfig;


    private WebViewConfig() {

    }

    public static WebViewConfig getInstance() {
        return Holder.sWebViewConfig;
    }


    private static class Holder {
        protected static WebViewConfig sWebViewConfig = new WebViewConfig();
    }

    @Override
    public boolean init() {


        buildSonicEngine();
        createWebView();
        doConfig();


        return true;
    }


    /**
     * 使用webView
     */
    @Override
    public XinWebView useWebView(Context context) {
        mWebView = WebViewCache.getInstance().useWebView(context);
        if (mWebView == null) {

            mWebView= reuseWebView(context);

        }


        return mWebView;
    }

    private XinWebView reuseWebView(Context context) {

        createWebView();
        doConfig();
        if (mWebView != null) {
            ((MutableContextWrapper) mWebView.getContext()).setBaseContext(context);

            mWebView.setIsUsed(true);
        }
        return mWebView;
    }


    /**
     * 不再使用重创建
     */
    @Override
    public void resetWebView() {
        WebViewCache.getInstance().resetWebView();
        mWebView = null;
        createWebView();
        doConfig();
    }


    private void createWebView() {
        if (mWebView == null)
            mWebView = WebViewCache.getInstance().initWebView();
        if (mWebView != null) {
            Log.i("webview 初始化成功");
        }

    }

    private void doConfig() {
        if (mWebView == null)
            return;


        initWebSettings(mWebView);
        CookiesHandler.initCookiesManager(mWebView.getContext());

        if (DOWNLOAD_ENABLE)
            mWebView.setDownloadListener(new WebDownLoadListener(mWebView.getContext()));
    }

    private void buildSonicEngine() {
        // step 1: Initialize sonic engine if necessary, or maybe u can do this when application created
        if (!SonicEngine.isGetInstanceAllowed()) {
            SonicEngine.createInstance(new XinSonicRuntime(XinApplication.getAppContext()), new SonicConfig.Builder().build());
        }

        SonicSessionConfig.Builder sessionConfigBuilder = new SonicSessionConfig.Builder();
      //  sessionConfigBuilder.setSessionMode(SonicConstants.SESSION_MODE_DEFAULT);
         mSessionConfig = sessionConfigBuilder.build();

    }


    @Override
    public WebSettings initWebSettings(WebView view) {

        // 根据需求执行配置

        WebSettings setting = view.getSettings();
        // 适配
        setting.setSupportZoom(true);
        setting.setTextZoom(100);
        setting.setBuiltInZoomControls(true);
        setting.setDisplayZoomControls(false);// 隐藏缩放按钮
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);

        if (SysUtils.hasKitKat()) {

            setting.setLayoutAlgorithm(android.webkit.WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        } else {
            setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }

        // 存储
        // setting.setSavePassword(true);
        setting.setSaveFormData(true);
        setting.setAllowFileAccess(true);//允许加载本地文件html  file协议
        setting.setDefaultTextEncodingName("UTF-8");
        setting.setDomStorageEnabled(true);
        setting.setDatabaseEnabled(true);
        setting.setAppCacheEnabled(true);
        // 定位
        setting.setGeolocationEnabled(true);// 启用地理定位
        String dir = getCacheDir(view.getContext());
        // 设置H5缓存
        setting.setAppCachePath(dir);

        if (!SysUtils.hasKitKat()) {
            //设置数据库路径  api19 已经废弃,这里只针对 webkit 起作用
            setting.setGeolocationDatabasePath(dir);
            setting.setDatabasePath(dir);
            //缓存文件最大值
            setting.setAppCacheMaxSize(Long.MAX_VALUE);
        }

        if (Network.isAvailable(view.getContext())) {
            //根据cache-control获取数据。
            setting.setCacheMode(android.webkit.WebSettings.LOAD_DEFAULT);
        } else {
            //没网，则从本地获取，即离线加载
            setting.setCacheMode(android.webkit.WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        if (SysUtils.hasKitKat()) {
            setting.setLoadsImagesAutomatically(true);//图片自动缩放 打开
        } else {
            setting.setLoadsImagesAutomatically(false);//图片自动缩放 关闭
        }

        setting.setSupportMultipleWindows(false);
        setting.setBlockNetworkImage(false);//是否阻塞加载网络图片  协议http or https

        setting.setNeedInitialFocus(true);
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
        setting.setDefaultFontSize(16);
        setting.setMinimumFontSize(12);//设置 WebView 支持的最小字体大小，默认为 8
        setting.setJavaScriptEnabled(true);

        if (SysUtils.hasLollipop()) {
            //适配5.0不允许http和https混合使用情况
            setting.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (SysUtils.hasKitKat()) {
            view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (!SysUtils.hasKitKat()) {
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        view.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_INSET);
        view.setSaveEnabled(true);
        //  view.setKeepScreenOn(true);




     /* TODO 还没有使用sonic的接口   // add java script interface
         // sonic 使用的
        view.removeJavascriptInterface("searchBoxJavaBridge_");
        intent.putExtra(SonicJavaScriptInterface.PARAM_LOAD_URL_TIME, System.currentTimeMillis());
        view.addJavascriptInterface(new SonicJavaScriptInterface(sonicSessionClient, intent), "sonic");*/


        return setting;
    }

    @Override
    public String getCacheDir(Context context) {

        return context.getCacheDir().getAbsolutePath() + File.separator + FileConfig.DIR_WEB_CACHE;
    }

    @Override
    public File getSonicCacheDir(Context context) {
        String dir = context.getFilesDir().getAbsolutePath() + File.separator + FileConfig.DIR_WEB_SONIC_CACHE;
        File file = new File(dir);


        FileUtil.createDir(file);

        return file;
    }


    @SuppressLint("JavascriptInterface")
    @Override
    public void addJavascriptInterface(Object object, String name) {


        mWebView.addJavascriptInterface(object, name);

    }


    @Override
    public void clearWebCache(boolean mIsWebViewInit) {
        WebViewCache.getInstance().clearWebCache(mIsWebViewInit);
    }


    public SonicSessionConfig getSessionConfig() {
        return mSessionConfig;
    }



    /**
     * cookies处理
     */
    public static class CookiesHandler {

        private static boolean isInit = false;

        static synchronized void initCookiesManager(Context context) {
            if (!isInit) {
                createCookiesSyncInstance(context);
                isInit = true;
            }
        }

        //获取Cookie
        public static String getCookiesByUrl(String url) {
            return CookieManager.getInstance() == null ? null : CookieManager.getInstance().getCookie(url);
        }


        /**
         * 清除过期的cookie
         */
        public static void removeExpiredCookies() {
            CookieManager mCookieManager = null;
            if ((mCookieManager = CookieManager.getInstance()) != null) { //同步清除
                mCookieManager.removeExpiredCookie();
                toSyncCookies();
            }
        }

        public static void removeAllCookies() {
            removeAllCookies(null);

        }


        // 解决兼容 Android 4.4 java.lang.NoSuchMethodError: android.webkit.CookieManager.removeSessionCookies
        public static void removeSessionCookies() {
            removeSessionCookies(null);
        }

        public static void removeSessionCookies(ValueCallback<Boolean> callback) {

            if (callback == null)
                callback = getDefaultIgnoreCallback();
            if (CookieManager.getInstance() == null) {
                callback.onReceiveValue(new Boolean(false));
                return;
            }
            if (!SysUtils.hasLollipop()) {
                CookieManager.getInstance().removeSessionCookie();
                toSyncCookies();
                callback.onReceiveValue(new Boolean(true));
                return;
            } else {

                CookieManager.getInstance().removeSessionCookies(callback);
            }
            toSyncCookies();

        }


        //Android  4.4  NoSuchMethodError: android.webkit.CookieManager.removeAllCookies
        public static void removeAllCookies(@Nullable ValueCallback<Boolean> callback) {

            if (callback == null)
                callback = getDefaultIgnoreCallback();
            if (!SysUtils.hasLollipop()) {
                CookieManager.getInstance().removeAllCookie();
                toSyncCookies();
                callback.onReceiveValue(!CookieManager.getInstance().hasCookies());
                return;
            } else {

                CookieManager.getInstance().removeAllCookies(callback);
            }
            toSyncCookies();
        }


        private static ValueCallback<Boolean> getDefaultIgnoreCallback() {

            return new ValueCallback<Boolean>() {
                @Override
                public void onReceiveValue(Boolean ignore) {
                    Log.i("removeExpiredCookies:" + ignore);
                }
            };
        }


        public static void syncCookie(String url, String cookies) {

            CookieManager mCookieManager = CookieManager.getInstance();
            if (mCookieManager != null) {
                mCookieManager.setCookie(url, cookies);
                toSyncCookies();
            }
        }


        private static void createCookiesSyncInstance(Context context) {


            if (!SysUtils.hasLollipop()) {
                CookieSyncManager.createInstance(context);
            }
        }


        private static void toSyncCookies() {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                CookieSyncManager.getInstance().sync();
                return;
            }

            AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    if (SysUtils.hasLollipop())
                        CookieManager.getInstance().flush();

                }
            });
        }

    }



}
