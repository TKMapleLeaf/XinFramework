package com.xin.framework.xinframwork.hybrid.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.xin.framework.xinframwork.common.FileConfig;
import com.xin.framework.xinframwork.hybrid.download.WebDownLoadListener;
import com.xin.framework.xinframwork.hybrid.model.WebModel;
import com.xin.framework.xinframwork.utils.android.SysUtils;
import com.xin.framework.xinframwork.utils.android.logger.Log;
import com.xin.framework.xinframwork.utils.common.assist.Network;

import java.io.File;

/**
 * Description :WebView Config
 * Created by 王照鑫 on 2017/11/1 0001.
 */

public class WebViewConfig implements IWebViewInit {


    private XinWebView mWebView;
    private WebSettings mWebSettings;
    private WebModel mWeViewModel;

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


        createWebView();

        doConfig();


        return true;
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

        mWebSettings = initWebSettings(mWebView);
        CookiesHandler.initCookiesManager(mWebView.getContext());

        if (DOWNLOAD_ENABLE)
            mWebView.setDownloadListener(new WebDownLoadListener(mWebView.getContext()));
    }


    public XinWebView getWebView() {

        init();


        return mWebView;
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
        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);// 排版适应屏幕

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
        //设置数据库路径  api19 已经废弃,这里只针对 webkit 起作用
        setting.setGeolocationDatabasePath(dir);
        setting.setDatabasePath(dir);
        //缓存文件最大值
        setting.setAppCacheMaxSize(Long.MAX_VALUE);

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
        view.setKeepScreenOn(true);
        return setting;
    }

    @Override
    public String getCacheDir(Context context) {

        return context.getCacheDir().getAbsolutePath() + File.separator + FileConfig.DIR_WEB_CACHE;
    }

    @SuppressLint("JavascriptInterface")
    @Override
    public void addJavascriptInterface(Object object, String name) {

        if (!mWebSettings.getJavaScriptEnabled()) {
            mWebSettings.setJavaScriptEnabled(true);
        }

        mWebView.addJavascriptInterface(object, name);

    }

    @Override
    public void useWebView(Context context) {
        WebViewCache.getInstance().useWebView(context);
    }
    @Override
    public void resetWebView() {
        WebViewCache.getInstance().resetWebView();
        mWebView = null;
        createWebView();
        doConfig();
    }

    @Override
    public void clearWebCache(boolean mIsWebViewInit) {
        WebViewCache.getInstance().clearWebCache(mIsWebViewInit);
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
