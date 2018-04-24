package com.pay.sdk.usage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pay.sdk.R;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import sdk.pay.model.TokenParam;

import static sdk.pay.constant.PayExternalConstant.PAY_STATUS;
import static sdk.pay.constant.PayExternalConstant.REQUEST_CODE;
import static sdk.pay.constant.PayExternalConstant.RESULT_CODE;
import static sdk.pay.constant.PayExternalConstant.ZERO_CODE;
import static sdk.pay.listener.PayGetPayStatusListener.SUCCESS;


public class ProductActivity extends Activity {
    private final String SYTEM_NAME = "WSF";
    private LinearLayout mLayout;
    private TextView mProductPrice, mProductName;
    private Toast mToast;
    private PayParameter payParameter;
    protected Dialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        initViews();
        initData();
    }

    private void showToast(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mToast.setText(str);
                mToast.show();
            }
        });
    }

    private void initViews() {
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        mLayout = (LinearLayout) findViewById(R.id.layout);
        mProductPrice = (TextView) findViewById(R.id.product_price);
        mProductName = (TextView) findViewById(R.id.product_name);
    }

    private void initData() {
        payParameter = new PayParameter();
        mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog = ProgressDialog.show(ProductActivity.this, "",
                        getString(R.string.init_tips), false, false, null);
                payParameter.setPayParameterListener(new PayParameter.PayParametercCllback() {
                    @Override
                    public void payParameter(final TokenParam tokenParam) {
                        PayActivity.startActivity(ProductActivity.this, tokenParam);
                        dismiss();
                    }

                    @Override
                    public void failureParameter(String str) {
                        Toast.makeText(ProductActivity.this, str, Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });
                payParameter.getPayParameter(ProductActivity.this, mProductName.getText().toString(),
                        mProductPrice.getText().toString(), SYTEM_NAME);
            }
        });
        mLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInputDialog(mProductPrice);
                return true;
            }
        });
    }

    private void dismiss() {
        if (null != mProgressDialog && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void showInputDialog(final TextView textView) {
        /*@setView add EditView  */
        final EditText editText = new EditText(ProductActivity.this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editText.setText(textView.getText().toString());
        editText.addTextChangedListener(new SearchWather(editText));
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(ProductActivity.this);
        inputDialog.setTitle(R.string.please_input_price).setView(editText);
        inputDialog.setPositiveButton(R.string.confirm,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if (!TextUtils.isEmpty(editText.getText().toString())) {
                                DecimalFormat df = new DecimalFormat("0.00");
                                String price = df.format(Double.parseDouble(editText.getText().toString()));
                                textView.setText(price);
                                mProductPrice.setText(price);
                            } else {
                                Toast.makeText(ProductActivity.this, R.string.data_cannot_empty, Toast.LENGTH_SHORT).show();
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            textView.setText(mProductPrice.getText().toString());
                            Toast.makeText(ProductActivity.this, R.string.data_format_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).show();
    }

    /**
     * edittext Filter only number and .
     */
    private String stringFilterText(String str) throws PatternSyntaxException {
        String regEx = "[^0-9.]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * Text input box special character filtering
     */
    private class SearchWather implements TextWatcher {
        //Listen to changes the text box
        private EditText editText;

        SearchWather(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void onTextChanged(CharSequence ss, int start, int before, int count) {
            String editable = editText.getText().toString();
            String str = stringFilterText(editable);
            if (str.contains(".")) {
                String strDecimal = str.substring(str.indexOf("."));
                if (strDecimal.length() > 3) {
                    str = str.substring(0, str.indexOf(".") + 3);
                }
            }
            if (!editable.equals(str)) {
                editText.setText(str);
                //new selection position
                editText.setSelection(str.length());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_CODE == requestCode) {
            if (RESULT_CODE == resultCode) {
                int payStatus = data.getIntExtra(PAY_STATUS, ZERO_CODE);
                if (payStatus == SUCCESS) {
                    showToast(getString(R.string.pay_success));
                } else {
                    showToast(getString(R.string.pay_failure));
                }
            }

        }
    }
}
