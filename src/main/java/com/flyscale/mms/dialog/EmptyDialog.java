package com.flyscale.mms.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.flyscale.mms.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by MrBian on 2018/2/1.
 */

public class EmptyDialog extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_empty);
        findViewById(R.id.confirm).setVisibility(View.INVISIBLE);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                finish();
            }
        }, 3000);
    }
}
