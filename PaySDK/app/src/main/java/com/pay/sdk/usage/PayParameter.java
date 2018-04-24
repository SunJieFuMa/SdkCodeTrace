package com.pay.sdk.usage;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.pay.sdk.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import sdk.pay.model.TokenParam;
import sdk.pay.utils.PayDESUtil;
import sdk.pay.utils.PayMD5Util;


/**
 * Created by lj on 2017/6/28 0028.
 */

public class PayParameter {
    public final static long TIME_OUT = 30L;
    private static final String JFT_PAYPARAM = "http://api.jtpay.com/AccessAccountInfo/getBusinessInformation";
    public PayParametercCllback mPayParametercCllback;
    public static final String TAG = "junfutong";

    public PayParameter() {
    }

    public void getPayParameter(final Context context, String goodsName, String goodsPrice, String systemName) {
        JSONObject object = new JSONObject();
        try {
            object.put("SysFlag", systemName.toUpperCase());
            object.put("money", goodsPrice);
            object.put("product", goodsName);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "Merchants configuration errors" + e.toString());
            if (mPayParametercCllback != null) {
                mPayParametercCllback.failureParameter(context.getString(R.string.merchants_configuration_errors));
            }
        }
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String key = PayMD5Util.getMD5(dateFormat.format(date));
        String encode = PayDESUtil.encode("abcdefg" + key, object.toString());
        String url = JFT_PAYPARAM + "?" + String.format("info=%s", encode) + String.format("&nonce=%s", key);
        Log.d(TAG, "Merchants configuration  url = " + url);
        OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(TIME_OUT, TimeUnit.SECONDS).readTimeout(TIME_OUT, TimeUnit.SECONDS).build();
        Request request = new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (null != mPayParametercCllback) {
                    mPayParametercCllback.failureParameter(context.getString(R.string.merchants_configuration_errors));
                }
                Log.d(TAG, "Merchants configuration errors");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (null != response.body().toString()) {
                    String result = response.body().string();
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String flag = jsonObject.getString("flag");
                            if ("1".equals(flag)) {
                                TokenParam tokenParam = new TokenParam();
                                String nonce = jsonObject.getString("nonce");
                                String msg = jsonObject.getString("msg");
                                String decode = PayDESUtil.decode("abcdefg" + nonce, msg);
                                Log.d(TAG, "Merchants configuration decode" + decode);
                                JSONObject jsonObject1 = new JSONObject(decode);
                                String name = jsonObject1.getString("name");
                                tokenParam.setName(name);
                                String str = jsonObject1.getString("businessInfo");
                                JSONObject jsonObject2 = new JSONObject(str);
                                if (!TextUtils.isEmpty(str)) {
                                    tokenParam.setP1_usercode(jsonObject2.getString("p1_usercode"));
                                    tokenParam.setP2_order(jsonObject2.getString("p2_order"));
                                    tokenParam.setP3_money(jsonObject2.getString("p3_money"));
                                    tokenParam.setP4_returnurl(jsonObject2.getString("p4_returnurl"));
                                    tokenParam.setP5_notifyurl(jsonObject2.getString("p5_notifyurl"));
                                    tokenParam.setP6_ordertime(jsonObject2.getString("p6_ordertime"));
                                    tokenParam.setP7_sign(jsonObject2.getString("p7_sign"));
                                    tokenParam.setAppId(jsonObject2.getString("appId"));
                                    tokenParam.setAesKey(jsonObject2.getString("aesKey"));
                                    tokenParam.setAesVector(jsonObject2.getString("aesVector"));
                                    String parametermDic = jsonObject2.getString("parametermDic");
                                    JSONObject jsonObject3 = new JSONObject(parametermDic);
                                    tokenParam.setP14_customname(jsonObject3.getString("p14_customname"));
                                    tokenParam.setP18_product(jsonObject3.getString("p18_product"));
                                    if (null != mPayParametercCllback) {
                                        mPayParametercCllback.payParameter(tokenParam);
                                    }
                                }
                            } else {
                                Log.d(TAG, "Merchants configuration errors" + jsonObject.toString());
                                if (null != mPayParametercCllback) {
                                    mPayParametercCllback.failureParameter(context.getString(R.string.merchants_configuration_errors));
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "get Merchants configuration errors" + e.toString());
                            if (null != mPayParametercCllback) {
                                mPayParametercCllback.failureParameter(context.getString(R.string.gain_merchants_configuration_errors));
                            }
                        }
                    }
                }
            }
        });
    }

    public void setPayParameterListener(PayParametercCllback payParametercCllback) {
        mPayParametercCllback = payParametercCllback;
    }

    public interface PayParametercCllback {
        void payParameter(TokenParam payParasmeterModel);

        void failureParameter(String str);
    }


}

