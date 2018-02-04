package com.flyscale.mms.main;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.flyscale.mms.R;
import com.flyscale.mms.bean.SmsReceiver;
import com.flyscale.mms.constants.Constants;
import com.flyscale.mms.dialog.EmptyDialog;
import com.flyscale.mms.main.options.SendMsgOptionsActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by MrBian on 2018/1/11.
 */

public class SmsCenterActivity extends Activity {
    private static final String TAG = "SmsCenterActivity";
    private EditText etPhone;
    private TextView cancel;
    private SMSReceiver sendReceiver;
    private SMSReceiver deliveryReceiver;
    private SMSReceiver smsReceiver;
    private static final String ACTION_SMS_SEND = "lab.sodino.sms.send";
    private static final String ACTION_SMS_DELIVERY = "lab.sodino.sms.delivery";
    private static final String ACTION_SMS_RECEIVER = "android.provider.Telephony.SMS_RECEIVED";
    private TextView center;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_smscenter_activity);
        initView();
        initData();
    }

    private void initData() {
        String action = getIntent().getStringExtra(Constants.ACTION);
        String replyNum = getIntent().getStringExtra(Constants.NEW_MSG_NUM);
//        if (TextUtils.equals(action, Constants.REPLY_MSG)) {
        etPhone.setText(replyNum);
        etPhone.setSelection(etPhone.getText().length());
//        }

        // 注册send
        sendReceiver = new SMSReceiver();
        IntentFilter sendFilter = new IntentFilter(ACTION_SMS_SEND);
        registerReceiver(sendReceiver, sendFilter);
        // 注册delivery
        deliveryReceiver = new SMSReceiver();
        IntentFilter deliveryFilter = new IntentFilter(ACTION_SMS_DELIVERY);
        registerReceiver(deliveryReceiver, deliveryFilter);
        // 注册接收下行receiver
        smsReceiver = new SMSReceiver();
        IntentFilter receiverFilter = new IntentFilter(ACTION_SMS_RECEIVER);
        registerReceiver(smsReceiver, receiverFilter);

        sendSms();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(sendReceiver);
        unregisterReceiver(deliveryReceiver);
        unregisterReceiver(smsReceiver);
        super.onDestroy();
    }

    private void initView() {
        TextView confirm = (TextView) findViewById(R.id.confirm);
        cancel = (TextView) findViewById(R.id.back);
        TextView title = (TextView) findViewById(R.id.title);
        center = (TextView) findViewById(R.id.tv);
        title.setText(getResources().getString(R.string.phonenumber));
        confirm.setText(getResources().getString(R.string.options));
        etPhone = (EditText) findViewById(R.id.et_new_msg);
        //设置EditText的显示方式为多行文本输入
//        etPhone.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        //文本显示的位置在EditText的最上方
        etPhone.setGravity(Gravity.TOP);
        //改变默认的单行模式
        etPhone.setSingleLine(false);
        //水平滚动设置为False
        etPhone.setHorizontallyScrolling(false);
        etPhone.addTextChangedListener(new PhoneNumChangedListener());

    }

    private boolean missTextChange = false;

    private void sendSms() {
        String smsBody = "lab.sodino.sms.test";
        String smsAddress = "10086";
        SmsManager smsMag = SmsManager.getDefault();
        Intent sendIntent = new Intent(ACTION_SMS_SEND);
        PendingIntent sendPI = PendingIntent.getBroadcast(this, 0, sendIntent,
                0);
        Intent deliveryIntent = new Intent(ACTION_SMS_DELIVERY);
        PendingIntent deliveryPI = PendingIntent.getBroadcast(this, 0,
                deliveryIntent, 0);
        smsMag.sendTextMessage(smsAddress, null, smsBody, sendPI, deliveryPI);
    }
    public class SMSReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String actionName = intent.getAction();
            int resultCode = getResultCode();
            if (actionName.equals(ACTION_SMS_SEND)) {
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.d(TAG, "/n[Send]SMS Send:Successed!");
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Log.d(TAG, "/n[Send]SMS Send:RESULT_ERROR_GENERIC_FAILURE!");
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Log.d(TAG, "/n[Send]SMS Send:RESULT_ERROR_NO_SERVICE!");
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Log.d(TAG, "/n[Send]SMS Send:RESULT_ERROR_NULL_PDU!");
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        break;
                }
            } else if (actionName.equals(ACTION_SMS_DELIVERY)) {
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.d(TAG, "/n[Delivery]SMS Delivery:Successed!");
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Log.d(TAG, "/n[Delivery]SMS Delivery:RESULT_ERROR_GENERIC_FAILURE!");
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Log.d(TAG, "/n[Delivery]SMS Delivery:RESULT_ERROR_NO_SERVICE!");
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Log.d(TAG, "/n[Delivery]SMS Delivery:RESULT_ERROR_NULL_PDU!");
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Log.d(TAG, "/n[Delivery]SMS Delivery:RESULT_ERROR_RADIO_OFF!");
                        break;
                }
                Log.d(TAG, "/n正在等待下行短信...");
            } else if (actionName.equals(ACTION_SMS_RECEIVER)) {
                System.out.println("[Sodino]result = " + resultCode);
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Object[] myOBJpdus = (Object[]) bundle.get("pdus");
                    SmsMessage[] messages = new SmsMessage[myOBJpdus.length];
                    for (int i = 0; i < myOBJpdus.length; i++) {
                        messages[i] = SmsMessage
                                .createFromPdu((byte[]) myOBJpdus[i]);
                    }
                    SmsMessage message = messages[0];
                    Log.d(TAG, "/n短信服务中心号码为：" + message.getServiceCenterAddress());
                    etPhone.setVisibility(View.VISIBLE);
                    center.setVisibility(View.GONE);
                    etPhone.setText(message.getServiceCenterAddress());
                }
            }
        }
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (TextUtils.isEmpty(etPhone.getText().toString())){
                    startActivity(new Intent(this, EmptyDialog.class));
                    return true;
                }
                if (etPhone.getVisibility() == View.VISIBLE) {
                    SharedPreferences sp = getSharedPreferences(Constants
                            .SP_COMMONS, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(Constants.SMS_CENTER, etPhone.getText().toString());
                    editor.commit();
                }
            case KeyEvent.KEYCODE_BACK:
                break;
        }
        return super.onKeyUp(keyCode, event);
    }
    class PhoneNumChangedListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            Log.d(TAG, "s=" + s + ",start=" + start + ",after=" + after + ",count=" + count);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (missTextChange) return;
            if (TextUtils.isEmpty(s.toString())) {
                cancel.setText(getResources().getString(R.string.back));
            } else {
                cancel.setText(getResources().getString(R.string.clear));
            }
        }
    }
}
