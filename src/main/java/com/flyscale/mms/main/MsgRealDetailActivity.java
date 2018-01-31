package com.flyscale.mms.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.flyscale.mms.R;
import com.flyscale.mms.bean.SmsInfo;
import com.flyscale.mms.constants.Constants;
import com.flyscale.mms.main.options.MsgDetailOptionsActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by MrBian on 2018/1/13.
 */

public class MsgRealDetailActivity extends Activity {

    private static final String TAG = "InBoxMsgDetailActivity";
    private static final int REPLY_MSG = 1006;
    private TextView title;
    private TextView detail;
    private SmsInfo smsInfo;
    private String boxtype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_detail);

        initView();
        initData();
    }

    private void initData() {
        smsInfo = (SmsInfo) getIntent().getSerializableExtra(Constants.MSG_INFO);
        boxtype = getIntent().getStringExtra(Constants.BOX_TYPE);
        title.setText(getResources().getString(R.string.detail));
        String type = null;
        if (TextUtils.equals(boxtype, Constants.INBOX)) {
            type = getResources().getString(R.string.sender);
        }else if (TextUtils.equals(boxtype, Constants.SENTBOX)) {
            type = getResources().getString(R.string.receiver);
        }
        String content = type + ":"
                        + smsInfo.getPerson() + "\n"
                        + getResources().getString(R.string.phonenumber) + ":"
                        + smsInfo.getPhoneNumber() + "\n"
                        + getResources().getString(R.string.date) +":"
                        + formatData(smsInfo.getDate()) + "\n"
                        + getResources().getString(R.string.service_center) + ":"
                        + smsInfo.getService_center();
        Log.d(TAG, "content=" + content);
        detail.setText(content);
    }

    private String formatData(long ms) {
        Date date = new Date(ms);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String format = sdf.format(date);
        return format;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.MSG_INFO, smsInfo);
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finish();
                return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void initView() {
        title = (TextView)findViewById(R.id.title);
        detail = (TextView)findViewById(R.id.msg_detail);
        TextView confirm = (TextView)findViewById(R.id.confirm);
        confirm.setVisibility(View.INVISIBLE);
    }
}
