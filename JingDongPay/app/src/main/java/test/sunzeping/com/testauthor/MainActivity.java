package test.sunzeping.com.testauthor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.jdpaysdk.author.Constants;
import com.jdpaysdk.author.JDPayAuthor;

public class MainActivity extends Activity {

  private EditText mTv;
  private Button mBtn;
  private EditText mEdt_orderId;
  private EditText mEdt_merchant;
  private EditText mEdt_appKey;
  private EditText mEdt_signData;

  private MyClick myClick = new MyClick();

  private boolean mFlag = false;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    if (savedInstanceState != null) {
      mFlag = true;
      Log.e("JDPay", "system has recovered MainActivity");
    } else {
      mFlag = false;
      Log.e("JDPay", "system has not recovered MainActivity");
    }
    mTv = (EditText) findViewById(R.id.tv_show);
    mBtn = (Button) findViewById(R.id.btn_send);
    mBtn.setOnClickListener(myClick);
    mEdt_orderId = (EditText) findViewById(R.id.edt_orderId);
    mEdt_merchant = (EditText) findViewById(R.id.edt_merchant);
    mEdt_appKey = (EditText) findViewById(R.id.edt_appKey);
    mEdt_signData = (EditText) findViewById(R.id.edt_signData);
  }

  class MyClick implements View.OnClickListener {

    @Override public void onClick(View v) {
      JDPayAuthor jdPayAuthor = new JDPayAuthor();
      String orderId = mEdt_orderId.getText().toString();
      String merchant = mEdt_merchant.getText().toString();
      String appId = mEdt_appKey.getText().toString();
      String signData = mEdt_signData.getText().toString();
      jdPayAuthor.author(MainActivity.this, orderId, merchant, appId, signData,null);
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (data != null) {
      if (Constants.PAY_RESPONSE_CODE == resultCode) {//返回信息接收
        String result = data.getStringExtra(JDPayAuthor.JDPAY_RESULT);
        mTv.setText(result);
      }
    } else {
      Toast.makeText(MainActivity.this, "返回为NULL", Toast.LENGTH_SHORT).show();
    }
  }
}
