package com.flyscale.mms.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import com.flyscale.mms.R;
import com.flyscale.mms.bean.SmsInfo;
import com.flyscale.mms.constants.Constants;
import com.flyscale.mms.main.options.ExtractedNumOptionsActivity;

import java.io.Serializable;

/**
 * Created by MrBian on 2018/1/15.
 */

public class ExtractedNumberActivity extends Activity {

    private EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extract_number);

        et = (EditText) findViewById(R.id.et);
        TextView title = (TextView) findViewById(R.id.title);
        TextView confirm = (TextView) findViewById(R.id.confirm);
        confirm.setText(getResources().getString(R.string.options));
        title.setText(getResources().getString(R.string.extrac_num));

        SmsInfo smsInfo = (SmsInfo) getIntent().getSerializableExtra(Constants.MSG_INFO);
        if (smsInfo != null) {
            et.setText(smsInfo.getPhoneNumber());
            et.setSelection(et.getText().length());
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                Intent extractNum = new Intent(this, ExtractedNumOptionsActivity.class);
                extractNum.putExtras(getIntent().getExtras());
                startActivity(extractNum);
                finish();
                break;
            case KeyEvent.KEYCODE_BACK:
                finish();
                break;
        }
        return super.onKeyUp(keyCode, event);
    }
}
