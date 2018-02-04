package com.flyscale.mms.main;

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
import com.flyscale.mms.main.options.MsgDetailOptionsActivity;
import com.flyscale.mms.main.options.SentBoxMsgDetailOptionsActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by MrBian on 2018/1/13.
 */

public class SentBoxMsgDetailActivity extends Activity{

    private static final String TAG = "SentBoxMsgDetailActivity";
    private static final int REPLY_MSG = 1006;
    private TextView title;
    private TextView detail;
    private SmsInfo smsInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_detail);

        initView();
        initData();
    }

    private void initData() {
        smsInfo = (SmsInfo) getIntent().getSerializableExtra(Constants.MSG_INFO);
        Log.d(TAG, "smsInfo=" + smsInfo);
        title.setText(getResources().getString(R.string.read_msg));
        String content = (TextUtils.isEmpty(smsInfo.getPerson())?
                smsInfo.getPhoneNumber(): smsInfo.getPerson()) + "\n"
                + formatData(smsInfo.getDate()) + "\n"
                + smsInfo.getSmsbody();
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
            case KeyEvent.KEYCODE_DPAD_CENTER:
                Intent reply = new Intent(this, NewMsgActivity.class);
                reply.putExtra(Constants.ACTION, Constants.REPLY_MSG);
                reply.putExtras(bundle);
                startActivityForResult(reply, REPLY_MSG);
                return true;
            case KeyEvent.KEYCODE_MENU:
                Intent msgdetailoptions = new Intent(this, SentBoxMsgDetailOptionsActivity.class);
                msgdetailoptions.putExtras(bundle);
                startActivity(msgdetailoptions);
                return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void initView() {
        title = (TextView)findViewById(R.id.title);
        detail = (TextView)findViewById(R.id.msg_detail);
        TextView confirm = (TextView)findViewById(R.id.confirm);
        confirm.setText(getResources().getString(R.string.options));
    }
}
