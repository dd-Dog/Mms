package com.flyscale.mms.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyscale.mms.R;
import com.flyscale.mms.constants.Constants;
import com.flyscale.mms.util.SmsUtil;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by MrBian on 2018/1/15.
 */

public class SavingDialog extends Activity {

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saving_dialog);
        tv = (TextView) findViewById(R.id.tv);

        boolean b = false;
        String msg = getIntent().getStringExtra(Constants.NEW_MSG);
        String action = getIntent().getStringExtra(Constants.ACTION);
        if (TextUtils.equals(action, Constants.SAVE_DRAFT)) {
            b = SmsUtil.addDraft(this, SmsUtil.SMS_URI_DRAFT, msg);
        }else {

        }
        if (b) {
            tv.setText(getResources().getString(R.string.save_success));
        } else {
            tv.setText(getResources().getString(R.string.save_failed));
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.putExtra(Constants.ACTION, Constants.SAVE_DRAFT_DONE);
                setResult(RESULT_OK, intent);
                finish();
            }
        }, 2000);
    }
}
