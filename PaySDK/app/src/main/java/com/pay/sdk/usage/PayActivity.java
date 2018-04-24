package com.pay.sdk.usage;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.pay.sdk.R;

import java.util.ArrayList;
import java.util.List;

import sdk.pay.PayUtil;
import sdk.pay.listener.PayGetPayTypeListener;
import sdk.pay.listener.PayUtilCallBack;
import sdk.pay.model.PayTypeModel;
import sdk.pay.model.TokenParam;

public class PayActivity extends Activity implements PayUtilCallBack {
    private static final int FINISH = 0x1;
    protected Dialog mProgressDialog;
    private GridView mGridView;
    private TextView mPayName;
    private TextView mPayPrice;
    private PayTypeGridviewAdapter mTypeGridviewAdapter;
    private int mCheckedId = -1;
    private PayUtil mPayUtil;
    private static Activity mActivity;
    private TokenParam mTokenParam;
    private boolean mBoolPaying;
    /**
     * 建议商户信息由服务器获取,
     * 不推荐商户支付信息存放在客户端
     * 非常不推荐商户支付密钥存放在客户端
     * demo由于展示原因,在手机端进行
     * It is recommended that the business information be obtained by the server,
     * not recommended for merchant payment message stored on the client
     * very not recommended for merchant payment keys stored on the client
     * demo as a result of the show, in the mobile terminal
     **/
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            final List<PayTypeModel> payTypeModelList = mPayUtil.getPayTypeModels();
            if (null == payTypeModelList || 0 == payTypeModelList.size()) {
                return;
            }
            dismiss();
            if (payTypeModelList.size() > 0) {
                mTypeGridviewAdapter.setList(payTypeModelList);
                mTypeGridviewAdapter.Portrait(isPortrait());
                mGridView.setAdapter(mTypeGridviewAdapter);
                if (mCheckedId == -1) {
                    for (PayTypeModel typeModel : payTypeModelList) {
                        if (typeModel.getTypeid() == 4) {
                            mCheckedId = payTypeModelList.indexOf(typeModel);
                            break;
                        } else {
                            mCheckedId = 0;
                        }
                    }
                    mTypeGridviewAdapter.selectItem(mCheckedId);
                }
            } else {
                Toast.makeText(PayActivity.this, "Did not get the payment!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case FINISH:
                    finish();
                    break;
            }
        }
    };

    /**
     * 建议商户信息由服务器获取,
     * 不推荐商户支付信息存放在客户端
     * 非常不推荐商户支付密钥存放在客户端
     * demo由于展示原因,在手机端进行
     * It is recommended that the business information be obtained by the server,
     * not recommended for merchant payment message stored on the client
     * very not recommended for merchant payment keys stored on the client
     * demo as a result of the show, in the mobile terminal
     **/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_layout);
        Intent intent = getIntent();
        mTokenParam = (TokenParam) intent.getSerializableExtra("payParasmeterModel");
        initView();
        mTypeGridviewAdapter = new PayTypeGridviewAdapter(PayActivity.this, new ArrayList<PayTypeModel>());
        mProgressDialog = ProgressDialog.show(PayActivity.this, "", getString(R.string.pay_info), false, true, null);
        mPayUtil = new PayUtil(mActivity, this, true);
        mPayUtil.setPayParam(mTokenParam.getAppId(), mTokenParam.getAesKey(),
                mTokenParam.getAesVector(), mTokenParam.getName().toLowerCase(), mTokenParam.getP1_usercode());
        //动态获取许可支付列表，动态展示支付列表。
        //如果您不需要动态获取许可支付列表可以使用自己既定的列表界面，调用getToken(TokenParam tokenParam,int typeId)
        //Dynamic access license payment list, dynamic display payment lis
        //If you don't need a dynamic access license payment list, call pay (TokenParam TokenParam, int typeId) using your own defined list interface.
        mPayUtil.getPayTypes(new PayGetPayTypeListener() {
            @Override
            public void onPayDataResult() {
                runOnUiThread(mRunnable);
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.pay_layout);
        initView();
        mTypeGridviewAdapter.Portrait(isPortrait());
        mGridView.setAdapter(mTypeGridviewAdapter);
    }

    public static void startActivity(Activity activity, TokenParam tokenParam) {
        Intent intent = new Intent(activity, PayActivity.class);
        mActivity = activity;
        intent.putExtra("payParasmeterModel", tokenParam);
        activity.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.pay.close");
        localBroadcastManager.registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismiss();
        if (mBoolPaying)
            finish();
    }

    private void initView() {
        initWidgets();
        mPayName.setText(mTokenParam.getP18_product());
        mPayPrice.setText(String.format("%s元", mTokenParam.getP3_money()));
    }

    private void initWidgets() {
        mPayName = (TextView) findViewById(R.id.junpay_TextView_name);
        mPayPrice = (TextView) findViewById(R.id.junpay_TextView_price);
        mGridView = (GridView) findViewById(R.id.junpay_listview);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCheckedId = position;
                mTypeGridviewAdapter.selectItem(mCheckedId);
            }
        });

        findViewById(R.id.junpay_button_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheckedId >= 0) {
                    mProgressDialog = ProgressDialog.show(PayActivity.this, "", getString(R.string.pay_info), false, true, null);
                    int typeId = getTypeId(mCheckedId);
                    mPayUtil.pay(getTokenParam(), typeId);
                } else {
                    Toast.makeText(PayActivity.this, "Payment has not yet chosen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 强烈建议p7_sign 数据签名过程在服务器端完成,
     * 非常不推荐商户支付密钥存放在客户端
     * demo由于展示原因,在手机端进行
     * It is highly recommended that the p7_sign data signature process be completed on the server side,
     * very not recommended for merchant payment keys stored on the client
     * demo as a result of the show, in the mobile terminal
     **/
    private TokenParam getTokenParam() {
        return mTokenParam;
    }

    private boolean isPortrait() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    private void dismiss() {
        if (null != mProgressDialog && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private int getTypeId(int index) {
        List<PayTypeModel> list = mPayUtil.getPayTypeModels();
        PayTypeModel model = list.get(index);
        return model.getTypeid();
    }

    @Override
    public void onPayException(String s) {
        dismiss();
        if (mBoolPaying) {
            mBoolPaying = false;
            mHandler.sendEmptyMessageDelayed(FINISH, 2500);
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

}
