package com.flyscale.mms.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import com.flyscale.mms.R;
import com.flyscale.mms.bean.ContactBean;
import com.flyscale.mms.bean.SmsInfo;
import com.flyscale.mms.constants.Constants;
import com.flyscale.mms.main.options.NewMsgOptionsActivity;

/**
 * Created by MrBian on 2018/1/11.
 */

public class NewMsgActivity extends Activity {
    private static final String TAG = "NewMsgActivity";
    private static final int INTENT_NEW_MSG_OPTIONS = 1000;
    private EditText etNewMsg;
    private TextView confirm;
    private TextView cancel;
    private TextView title;
    private SmsInfo smsInfo;
    private String action;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_msg_activity);
        initView();
        initData();
    }

    private void initData() {
        smsInfo = (SmsInfo) getIntent().getSerializableExtra(Constants.MSG_INFO);
        action = getIntent().getStringExtra(Constants.ACTION);
        if (TextUtils.equals(action, Constants.FORWARD_MSG)
                || TextUtils.equals(action, Constants.EDIT_DRAFT)) {
            if (smsInfo != null) {
                etNewMsg.setText(smsInfo.getSmsbody());
                etNewMsg.setSelection(etNewMsg.getText().length());
            }
        } else if (TextUtils.equals(action, Constants.SNED_COMMON_AS_NEWMSG)) {
            String msgStr = getIntent().getStringExtra(Constants.COMMONS_TEXT);
            etNewMsg.setText(msgStr);
            etNewMsg.setSelection(etNewMsg.getText().length());
        } else if (TextUtils.equals(action, Constants.ACTION_SENDMSG_FROM_CONTACTS)) {
            phoneNumber = getIntent().getStringExtra(Constants.NEW_MSG_NUM);
            Log.d(TAG, "phoneNumber=" + phoneNumber);
        }
        Log.d(TAG, "smsInfo=" + smsInfo);
    }

    private void initView() {
        confirm = (TextView) findViewById(R.id.confirm);
        cancel = (TextView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        etNewMsg = (EditText) findViewById(R.id.et_new_msg);
        etNewMsg.addTextChangedListener(new NewMsgChangedListener());
        title.setText(getResources().getString(R.string.edit_msg));
        confirm.setText(getResources().getString(R.string.options));
    }

    class NewMsgChangedListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            Log.d(TAG, "s=" + s + ",start=" + start + ",after=" + after + ",count=" + count);
//            Log.d(TAG, "改变的字符==" + s.charAt(start));
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d(TAG, "s=" + s + ",start=" + start + ",before=" + before + ",count=" + count);
//            Log.d(TAG, "改变的字符==" + s.charAt(start));
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (TextUtils.isEmpty(s.toString())) {
                cancel.setText(getResources().getString(R.string.back));
            } else {
                cancel.setText(getResources().getString(R.string.clear));
            }
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        String msgStr = etNewMsg.getText().toString();
        Log.e(TAG, "msgStr=" + msgStr);
        if (smsInfo != null)
            phoneNumber = smsInfo.getPhoneNumber();
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                Intent intent = new Intent(this, NewMsgOptionsActivity.class);
                intent.putExtra(Constants.NEW_MSG, msgStr);
                intent.putExtra(Constants.NEW_MSG_NUM, phoneNumber);
                intent.putExtra(Constants.ACTION, action);
                startActivityForResult(intent, INTENT_NEW_MSG_OPTIONS);
                return true;

            case KeyEvent.KEYCODE_DPAD_CENTER:
                Intent phoneNum = new Intent(this, EditPhoneNumberActivity.class);
                phoneNum.putExtra(Constants.NEW_MSG, msgStr);
                phoneNum.putExtra(Constants.ACTION, action);
                phoneNum.putExtra(Constants.NEW_MSG_NUM, phoneNumber);
                startActivity(phoneNum);
                break;
            case KeyEvent.KEYCODE_BACK:
                finish();
                return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == INTENT_NEW_MSG_OPTIONS) {
                String action = data.getStringExtra(Constants.ACTION);
                if (TextUtils.equals("exit", action)) {
                    finish();
                } else if (TextUtils.equals(Constants.ACTION_APPEND, action)) {
                    String dataStr = data.getStringExtra(Constants.INTENT_DATA);
                    etNewMsg.append(dataStr);
                } else if (TextUtils.equals(Constants.SAVE_DRAFT_DONE, action)) {
                    finish();
                }else if (TextUtils.equals(Constants.ACTION_GET_EMOJIS, action)) {
                    etNewMsg.append(data.getStringExtra(Constants.INTENT_DATA));
                }else if (TextUtils.equals(Constants.GET_CONTACT_BEAN, action)) {
                    etNewMsg.append(getResources().getString(R.string.name) + ":" +
                            data.getStringExtra(Constants.CONTACT_NAME) + ","+
                            getResources().getString(R.string.mobile_phone) + ":"
                            + data.getStringExtra(Constants.CONTACT_PHONE));
                }
            }
        }
    }
}
