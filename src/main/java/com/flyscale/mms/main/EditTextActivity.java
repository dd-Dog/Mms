package com.flyscale.mms.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import com.flyscale.mms.R;
import com.flyscale.mms.constants.Constants;

/**
 * Created by MrBian on 2018/1/15.
 */

public class EditTextActivity extends Activity{

    private EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text);
        et = (EditText) findViewById(R.id.et);
        String str = getIntent().getStringExtra(Constants.COMMONS_TEXT);
        et.setText(str);
        et.setSelection(et.getText().length());
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                Intent result = new Intent();
                result.putExtra(Constants.ACTION, Constants.SAVE_COMMONT_TEXT);
                String text = et.getText().toString().trim();
                result.putExtra(Constants.INTENT_DATA, text);
                setResult(RESULT_OK, result);
                finish();
                break;

            case KeyEvent.KEYCODE_BACK:
                finish();
                break;
        }
        return super.onKeyUp(keyCode, event);
    }
}
