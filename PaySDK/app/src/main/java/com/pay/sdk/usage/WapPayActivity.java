package com.pay.sdk.usage;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.pay.sdk.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import sdk.pay.model.TokenParam;

public class WapPayActivity extends Activity {

    private TokenParam mTokenParam;
    private WebView mWebview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mTokenParam = (TokenParam) intent.getSerializableExtra("payParasmeterModel");
        setContentView(R.layout.activity_wap_pay);
        mWebview = (WebView) findViewById(R.id.webview);
        WebSettings settings = mWebview.getSettings();
//        settings.setJavaScriptEnabled(true);//支持javaScript
//        settings.setDefaultTextEncodingName("utf-8");//设置网页默认编码
//        settings.setJavaScriptCanOpenWindowsAutomatically(true);
//        settings.setJavaScriptEnabled(true);
//// 设置可以访问文件
//        settings.setAllowFileAccess(true);
//// 设置支持缩放
//        settings.setBuiltInZoomControls(true);
//        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//// webSettings.setDatabaseEnabled(true);
//// 使用localStorage则必须打开
//        settings.setDomStorageEnabled(true);
//        settings.setGeolocationEnabled(true);
//        mWebview.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                // TODO Auto-generated method stub
//                super.onPageStarted(view, url, favicon);
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                // TODO Auto-generated method stub
//                super.onPageFinished(view, url);
//            }
//
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                Log.e("WapPayActivity", "访问的url地址：" + url);
//                try {
//                    Uri uri = Uri.parse(url);
//                    Intent intent;
//                    intent = Intent.parseUri(url,
//                            Intent.URI_INTENT_SCHEME);
//                    intent.addCategory("android.intent.category.BROWSABLE");
//                    intent.setComponent(null);
//                    // intent.setSelector(null);
//                    startActivity(intent);
//
//                } catch (Exception e) {
//
//                }
//                return true;
//
//            }
//
//        });

        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        JS接口,跳转Activity
        settings.setJavaScriptEnabled(true);
        mWebview.setWebChromeClient(new WebChromeClient());

        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("WapPayActivity", "105-----shouldOverrideUrlLoading--->" + url);

                if (!TextUtils.isEmpty(url) && !url.startsWith("http:") && !url.startsWith("https:") && !url.startsWith("ftp")) {
                    try {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                if (!TextUtils.isEmpty(url) && url.contains("com.newbear.show.activities")) {
                    Log.e("", url);
                    String substring = url.substring(url.indexOf("com.newbear.show.activities"));
                    Log.e("", "substring---->" + substring);
                    if (!substring.endsWith("Activity")) {
                        return true;
                    }
                    try {
                        Intent intent1 = new Intent();
                        intent1.setClassName("com.newbear.show", substring);
//                        intent1.setClassName(Platform.getInstance().getTopActivity(), substring);
                        startActivity(intent1);
                    } catch (Exception e) {
                        Log.e("", "" + "--------报错--------->" + e.toString());
                        e.printStackTrace();
                    } finally {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
    }

    //   响应webView加载的网页的back操作
    @Override
    public void onBackPressed() {
        if (mWebview.canGoBack()) {
            mWebview.goBack();
            return;
        }
        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        destroy();
        super.onDestroy();
    }


    public void destroy() {
        if (mWebview != null) {
            // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，需要先onDetachedFromWindow()，再
            // destory()
            ViewParent parent = mWebview.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mWebview);
            }

            mWebview.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            mWebview.getSettings().setJavaScriptEnabled(false);
            mWebview.clearHistory();
            mWebview.clearFocus();
            mWebview.clearCache(true);
            mWebview.clearView();
            mWebview.removeAllViews();

            try {
                mWebview.destroy();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void startActivity(Activity activity, TokenParam tokenParam) {
        Intent intent = new Intent(activity, WapPayActivity.class);
        intent.putExtra("payParasmeterModel", tokenParam);
        activity.startActivity(intent);
    }

    public void go1(View view) {
        Toast.makeText(this, "触发go1", Toast.LENGTH_SHORT).show();
//        launchPay("http://pay.paywap.cn/form/pay", "WX");
        launchWebView("http://pay.paywap.cn/form/pay");
    }


    public void go2(View view) {
        Toast.makeText(this, "触发go2", Toast.LENGTH_SHORT).show();
    }


    private void launchPay(String url, String payTyoe) {
        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("p1_usercode", mTokenParam.getP1_usercode())
                .add("p2_order", mTokenParam.getP2_order())
                .add("p3_money", mTokenParam.getP3_money())
                .add("p4_returnurl", mTokenParam.getP4_returnurl())
                .add("p5_notifyurl", mTokenParam.getP5_notifyurl())
                .add("p6_ordertime", mTokenParam.getP6_ordertime())
                .add("p7_sign", mTokenParam.getP7_sign())
//                .add("p8_signtype",mTokenParam.getP8_signtype())
                .add("p9_paymethod", mTokenParam.getP9_paymethod())
//                .add("p10_paychannelnum",mTokenParam.getP10_paychannelnum())
//                .add("p11_cardtype",mTokenParam.getP11_cardtype())
//                .add("p12_channel",mTokenParam.getP12_channel())
//                .add("p13_orderfailertime",mTokenParam.getP13_orderfailertime())
                .add("p14_customname", mTokenParam.getP14_customname())
//                .add("p15_customcontacttype",mTokenParam.getP15_customcontacttype())
//                .add("p16_customcontact",mTokenParam.getP16_customcontact())
//                .add("p17_customip",mTokenParam.getP17_customip())
                .add("p18_product", mTokenParam.getP18_product())
//                .add("p19_productcat",mTokenParam.getP19_productcat())
//                .add("p20_productnum",mTokenParam.getP20_productnum())
//                .add("p21_pdesc",mTokenParam.getP21_pdesc())
//                .add("p22_version",mTokenParam.getP22_version())
//                .add("p23_charset",mTokenParam.getP23_charset())
//                .add("p24_remark",mTokenParam.getP24_remark())
                .add("p25_terminal", mTokenParam.getP25_terminal())
                .add("p26_iswappay", String.valueOf(1))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .method("POST", formBody)
                .build();
        Log.e("WapPayActivity", "86-----launchPay--->" + request.body());

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (e != null) {
                    e.printStackTrace();
                }
                Log.e("WapPayActivity", "66-----onFailure--->" + call);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("WapPayActivity", "73-----onResponse--->" + call);
                Log.e("WapPayActivity", "74-----onResponse--->" + response);
            }
        });
    }

    private void launchWebView(String url) {
        StringBuilder builder1 = new StringBuilder();
        //拼接post提交参数
        builder1.append("p1_usercode=").append(mTokenParam.getP1_usercode()).append("&")
                .append("p2_order=").append(mTokenParam.getP2_order()).append("&")
                .append("p3_money=").append(mTokenParam.getP3_money()).append("&")
                .append("p4_returnurl=").append(mTokenParam.getP4_returnurl()).append("&")
                .append("p5_notifyurl=").append(mTokenParam.getP5_notifyurl()).append("&")
                .append("p6_ordertime=").append(mTokenParam.getP6_ordertime())
                .append("p7_sign=").append(mTokenParam.getP7_sign())
                .append("p9_paymethod=").append(mTokenParam.getP9_paymethod())
                .append("p14_customname=").append(mTokenParam.getP14_customname())
                .append("p18_product=").append(mTokenParam.getP18_product())
                .append("p25_terminal=").append(mTokenParam.getP25_terminal())
                .append("p26_iswappay=").append(String.valueOf(1));
        mWebview.postUrl(url, builder1.toString().getBytes());
    }

}
