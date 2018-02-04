package com.flyscale.mms.main;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.flyscale.mms.R;
import com.flyscale.mms.constants.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by MrBian on 2018/1/12.
 */

public class SendMsgsStatusActivity extends Activity {

    private static final String TAG = "SendMsgsStatusActivity";
    private TextView status;
    private String msgStr;
    private String msgNum;
    private SmsReceiver mSmsReceiver;
    private ArrayList<String> receivers;
    private boolean sendSuccess = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_msg_status);

        initData();
        initView();

        for (int i = 0; i < receivers.size(); i++) {
            sendSMS(receivers.get(i), msgStr);
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.putExtra(Constants.ACTION, Constants.MSG_SEND_STATUS);
                intent.putExtra(Constants.INTENT_DATA, sendSuccess);
                setResult(RESULT_OK, intent);
                Log.d(TAG, "time up------");
                finish();
            }
        }, 4000);
    }

    private void initData() {
        msgStr = getIntent().getStringExtra(Constants.NEW_MSG);
        receivers = (ArrayList<String>) getIntent().getSerializableExtra(Constants.MSG_RECEIVERS);
        Log.d(TAG, "msgNum=" + msgNum + ",receivers=" + receivers);
    }

    private void initView() {
        status = (TextView) findViewById(R.id.status);
        findViewById(R.id.confirm).setVisibility(View.GONE);
        if (receivers == null || receivers.size() == 0) {
            status.setText(getResources().getString(R.string.empty_phone));
        } else if (readSIMCard()) {
            status.setText(getResources().getString(R.string.msg_sending));
        }
    }

    @Override
    protected void onResume() {
        mSmsReceiver = new SmsReceiver();
        IntentFilter intentFilter = new IntentFilter(SMS_SEND_ACTIOIN);
        intentFilter.addAction(SMS_DELIVERED_ACTION);
        registerReceiver(mSmsReceiver, intentFilter);

        super.onResume();
    }


    /**
     * 直接调用短信接口发短信，不含发送报告和接受报告
     *
     * @param phoneNumber
     * @param message
     */
    public void sendSMS(String phoneNumber, String message) {

         /* 建立自定义Action常数的Intent(给PendingIntent参数之用) */
        Intent itSend = new Intent(SMS_SEND_ACTIOIN);
        Intent itDeliver = new Intent(SMS_DELIVERED_ACTION);

          /* sentIntent参数为传送后接受的广播信息PendingIntent */
        PendingIntent mSendPI = PendingIntent.getBroadcast(getApplicationContext(), 0, itSend, 0);

          /* deliveryIntent参数为送达后接受的广播信息PendingIntent */
        PendingIntent mDeliverPI = PendingIntent.getBroadcast(getApplicationContext(), 0,
                itDeliver, 0);

        // 获取短信管理器
        android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
        // 拆分短信内容（手机短信长度限制）
        List<String> divideContents = smsManager.divideMessage(message);
        for (String text : divideContents) {
            smsManager.sendTextMessage(phoneNumber, null, text, mSendPI, mDeliverPI);
            Log.d(TAG, "send message");
        }

    }


    /* 自定义ACTION常数，作为广播的Intent Filter识别常数 */
    private static String SMS_SEND_ACTIOIN = "SMS_SEND_ACTIOIN";
    private static String SMS_DELIVERED_ACTION = "SMS_DELIVERED_ACTION";

    public class SmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(SMS_SEND_ACTIOIN)) try {
          /* android.content.BroadcastReceiver.getResultCode()方法 */
                Log.d(TAG, "resultcode=" + getResultCode());
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        status.setText(getResources().getString(R.string.send_msg_success));
                        sendSuccess = true;
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        status.setText(getResources().getString(R.string.send_msg_fail));
                        sendSuccess = false;
                        break;
                }
            } catch (Exception e) {
                e.getStackTrace();
            }
            else if (intent.getAction().equals(SMS_DELIVERED_ACTION)) {
                try {
          /* android.content.BroadcastReceiver.getResultCode()方法 */
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            break;
                    }
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
        }
    }


    public boolean readSIMCard() {
        TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);//取得相关系统服务
        boolean simStatus = true;
        switch (tm.getSimState()) { //getSimState()取得sim的状态 有下面6中状态
            case TelephonyManager.SIM_STATE_ABSENT:
            case TelephonyManager.SIM_STATE_UNKNOWN:
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                status.setText(getResources().getString(R.string.insert_sim));
                simStatus = false;
                break;
            case TelephonyManager.SIM_STATE_READY:
                simStatus = true;
                break;
        }
        return simStatus;
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mSmsReceiver);
    }
}
