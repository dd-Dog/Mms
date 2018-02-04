package com.flyscale.mms.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.flyscale.mms.R;
import com.flyscale.mms.bean.SmsInfo;
import com.flyscale.mms.constants.Constants;
import com.flyscale.mms.util.SmsUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by MrBian on 2018/1/13.
 */

public class DeleteConfirmActivity extends Activity {

    private static final String TAG = "DeleteConfirmActivity";
    private SmsInfo smsInfo;
    private TextView confirm;
    private TextView status;
    private TextView cancel;
    private String smsUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_confirm);

        initView();
        initData();

    }

    private void initData() {
        smsInfo = (SmsInfo) getIntent().getSerializableExtra(Constants.MSG_INFO);
        smsUri = getIntent().getStringExtra(Constants.SMS_URI);
        Log.d(TAG, "smsUri=" + smsUri);
    }

    private void initView() {
        confirm = (TextView) findViewById(R.id.confirm);
        status = (TextView) findViewById(R.id.status);
        cancel = (TextView) findViewById(R.id.back);
    }

    public void delayFinish() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.putExtra(Constants.ACTION, Constants.DELETE_DONE);
                setResult(RESULT_OK, intent);
                finish();
            }
        }, 5000);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_MENU:
                status.setText(getResources().getString(R.string.deleting));
                boolean delete = SmsUtil.delete(this, smsUri, smsInfo.getId() + "");
                if (delete) {
                    status.setText(getResources().getString(R.string.delete_success));
                } else {
                    status.setText(getResources().getString(R.string.delete_failed));
                }
                delayFinish();
                return true;

            case KeyEvent.KEYCODE_BACK:
                Intent intent = new Intent();
                intent.putExtra(Constants.ACTION, Constants.DELETE_DONE);
                setResult(RESULT_OK, intent);
                finish();
                return true;

        }
        return super.onKeyUp(keyCode, event);
    }
}
