package cn.dreamchase.android.six.webview;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import cn.dreamchase.android.six.R;

/**
 * -WebView 加载网页的四种方式
 * --可以在线加载url，也能加载本地html
 * ---webView.loadUrl("http://139.196.35.30:8080/OkHttpTest/apppackage/test.html");
 * ---webView.loadUrl("file:///android_asset/test.html"); // 加载asset文件夹下的html
 * ---webView.loadUrl("content://com.ansen.webview/sdcard/test.html");// 加载手机sd卡上的html页面
 * ---webView.loadDataWithBaseURL(null,"<html><html/>","test.html","utf-8",null);
 *
 *
 * ----------------------------------------------------------------------------------
 *
 * -WebViewClient 与 WebChromeClient
 * --WebViewClient主要帮助WebView处理各种通知，请求事件，有以下几个常用方法：
 * ---onPageFinished:页面请求完成
 * ---onPageStarted：页面开始加载
 * ---shouldOverrideUrlLoading: 拦截url
 * ---onReceivedError:访问错误时回调，例如访问网页时报错404，在这个方法回调的时候可以加载错误页面
 *
 * ----------------------------------------------------------------------------------
 * -WebChromeClient 主要辅助WebView 处理Javascript 的对话框，网站图标、网站title、加载进度等，有以下几个常用方法：
 * ---onJsAlert: WebView 不支持JS的 alert 弹窗，需要自己监听后通过 dialog 弹窗
 * ---onReceivedTitle ： 获取网页标题
 * ---onReceivedIcon : 获取网页icon
 * ---onProgressChanged : 加载进度回调
 */
public class MainActivity_WebView extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_webview);


        webView = findViewById(R.id.web_view);
        progressBar = findViewById(R.id.progressbar);

        webView.loadUrl("https://www.baidu.com");

        /*
            addJavascriptInterface有很大的安全隐患（通过js获取高权限，盗取用户信息、运行病毒代码等等），
            特别是4.2之前，4.2及以上版本（API >= 17）新增了@JavascriptInterface注解来提高安全等级，没有注解的方法，
            js无法调用，并在4.4版本增加了带回调的方法
            webview.evaluateJavascript(s,valuecallback)，但仍有更安全的交互方式；
        */

        webView.addJavascriptInterface(this,"android");
        // 添加js监听，这样html就能调用客户端
        webView.setWebChromeClient(webChromeClient);

        webView.setWebViewClient(webViewClient);

        WebSettings webSettings = webView.getSettings();
        // 允许使用JS
        webSettings.setJavaScriptEnabled(true);

        /**
         * LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
         * LOAD_DEFAULT: （默认）根据cache-control 决定是否从网络上获取数据
         * LOAD_NO_CACHE: 不使用缓存，只从网络获取数据
         * LOAD_CACHE_ELSE_NETWORK: 只要本地存，无论是否过期，或者no-cache，都使用缓存中的数据
         */
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAllowFileAccess(true);
        webSettings.setSupportZoom(true); // 支持屏幕缩放
        webSettings.setBuiltInZoomControls(true);
        webSettings.setBlockNetworkLoads(false);

        // 不显示webView缩放按钮
        // webSettings.setDisplayZoomControls(false);
    }


    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) { // 页面加载完成
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // 页面开始加载
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i("ansen","拦截url：" + url);
            if (url.equals("http://www.google.com/")) {
                Toast.makeText(MainActivity_WebView.this,"国内不能访问google，拦截该url",Toast.LENGTH_SHORT).show();
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    };

    /**
     * -主要辅助webView处理JavaScript的对话框，网站图标，网站title、加载进度等
     */
    private WebChromeClient webChromeClient = new WebChromeClient() {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            // 不支持 js的alert弹窗，需要自己监听，然后通过dialog弹窗
            AlertDialog.Builder aBuilder = new AlertDialog.Builder(view.getContext());
            aBuilder.setMessage(message).setPositiveButton("确定",null);
            aBuilder.setCancelable(false);
            aBuilder.create().show();


            // 注意：
            // 必须有result.confirm()
            // 表示处理结果为确定状态同时唤醒webcore线程
            // 否则不能继续点击按钮
            result.confirm();
            return true;
        }

        /**
         * -获取网页标题
         * @param view
         * @param title
         */
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            Log.i("ansen","网页标题：" + title);
        }

        /**
         * -加载进度回调
         * @param view
         * @param newProgress
         */
        @Override
        public void onProgressChanged(WebView view, int newProgress) {

            progressBar.setProgress(newProgress);
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("ansen","是否有上一个页面：" + webView.canGoBack());
        if(webView.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {
            // 点击返回按钮的时候判断有没有上一页
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * JS调用Android的方法
     * @param str
     */
    @JavascriptInterface
    public void getClient(String str) {
        Log.i("ansen","html调用客户端:" + str);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.destroy();
        webView = null;
    }
}