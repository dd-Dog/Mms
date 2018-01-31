package com.flyscale.mms.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import com.flyscale.mms.R;
import com.flyscale.mms.bean.SmsReceiver;
import com.flyscale.mms.constants.Constants;
import com.flyscale.mms.main.options.SendMsgOptionsActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by MrBian on 2018/1/11.
 */

public class EditPhoneNumberActivity extends Activity {
    private static final String TAG = "EditPhoneNumberActivity";
    private static final int INTENT_NEW_MSG_PHONE = 1005;
    private Class[] mActivities = new Class[6];
    private static final int INTENT_NEW_MSG_OPTIONS = 1000;
    private EditText etPhone;
    private TextView cancel;
    private ArrayList<SmsReceiver> mReceivers;
    private ArrayList<SmsReceiver> mNotContactsReceivers;
    private ArrayList<SmsReceiver> mContactsReceivers;
    private boolean deleteChar;
    private HashMap<String, String> selectedContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_phone_activity);
        initView();
        initData();
    }

    private void initData() {
        selectedContacts = new HashMap<String, String>();
        String action = getIntent().getStringExtra(Constants.ACTION);
        String replyNum = getIntent().getStringExtra(Constants.NEW_MSG_NUM);
//        if (TextUtils.equals(action, Constants.REPLY_MSG)) {
        etPhone.setText(replyNum);
        etPhone.setSelection(etPhone.getText().length());
//        }
        mReceivers = new ArrayList<SmsReceiver>();
        mNotContactsReceivers = new ArrayList<SmsReceiver>();
        mContactsReceivers = new ArrayList<SmsReceiver>();
    }

    private void initView() {
        TextView confirm = (TextView) findViewById(R.id.confirm);
        cancel = (TextView) findViewById(R.id.back);
        TextView title = (TextView) findViewById(R.id.title);
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
    private boolean inputChar = false;

    class PhoneNumChangedListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            Log.d(TAG, "s=" + s + ",start=" + start + ",after=" + after + ",count=" + count);
            if (missTextChange) return;

            if (count == 1) {
                inputChar = false;
                deleteChar = true;
                //删除一个字符
                if (s.charAt(start) == ';') {
                    String text = s.toString();
                    int selectionStart = etPhone.getSelectionStart();
                    missTextChange = true;
                    String sbs1 = text.substring(0, selectionStart);
                    String sbs2 = text.substring(selectionStart, text.length());
                    int index = sbs1.substring(0, sbs1.length() - 1).lastIndexOf(';');
                    String sbs3 = sbs1.substring(0, index + 1);
//                    String sbs4 = sbs2.substring(1, sbs2.length());
                    etPhone.setText(sbs3 + sbs2);
                    etPhone.setText(etPhone.getText().length());
                    Log.d(TAG, "sbs1=" + sbs1 + ",sbs2=" + sbs2 + ",sbs3=" + sbs3);
                    missTextChange = false;
                }
            } else if (after == 1) {
                //输入一个字符
                inputChar = true;
                deleteChar = false;
            }
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
                if (inputChar)
                    formatPhoneNums(s.toString());
            }
        }
    }

    private void formatPhoneNums(String s) {
        missTextChange = true;
        String text = etPhone.getText().toString().trim();
        Log.d(TAG, "text=" + etPhone.getText());
        if (!etPhone.getText().toString().trim().endsWith(";")) {
            StringBuilder sb1 = new StringBuilder(text);
            String reverse = sb1.reverse().toString();
            String reverseReplacement = reverse.replaceFirst(";", "\n;");
            StringBuilder sb2 = new StringBuilder(reverseReplacement);
            String replacement = sb2.reverse().toString();
            etPhone.setText(replacement);

            etPhone.getText().insert(etPhone.getText().length(), ";");
            etPhone.setSelection(etPhone.getText().toString().trim().length() - 1);
        } else {
            char c = ' ';
            Log.d(TAG, "text.length()=" + text.length() + ",etPhone.getSelectionStart()=" +
                    etPhone.getSelectionStart());
            if (text.length() == etPhone.getSelectionStart() + 1) {
                missTextChange = false;
                return;
            } else {
                c = text.charAt(etPhone.getSelectionStart());
            }
            String sub1 = text.substring(0, etPhone.getSelectionStart());
            String sub2 = text.substring(etPhone.getSelectionStart(), text.length());
            if (c == '\n') {
                sub2 = ";" + sub2;
                StringBuilder sbSub1 = new StringBuilder(sub1);
                String sub1Rev = sbSub1.reverse().toString();
                String repRevSub1 = sub1Rev.replaceFirst(";", "\n;");
                StringBuilder sbSub2 = new StringBuilder(repRevSub1);
                sub1 = sbSub2.reverse().toString();
                text = sub1 + sub2;
                etPhone.setText(text);
                etPhone.setSelection(sub1.length());
            }
        }
        missTextChange = false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                Intent intent = new Intent(this, SendMsgOptionsActivity.class);
                intent.putExtra(Constants.NEW_MSG, getIntent().getStringExtra(Constants.NEW_MSG));
                Log.d(TAG, "receivers=" + getRecievers());
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.MSG_RECEIVERS, getRecievers());
                intent.putExtras(bundle);
                startActivityForResult(intent, INTENT_NEW_MSG_OPTIONS);
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                getReceivers();
                Intent sendMsg = new Intent(this, SendMsgsStatusActivity.class);
                sendMsg.putExtra(Constants.NEW_MSG, getIntent().getStringExtra(Constants.NEW_MSG));
                Bundle bundle2 = new Bundle();
                bundle2.putSerializable(Constants.MSG_RECEIVERS, getRecievers());
                sendMsg.putExtras(bundle2);
                startActivityForResult(sendMsg, INTENT_NEW_MSG_PHONE);
                break;

            case KeyEvent.KEYCODE_BACK:
                if (!TextUtils.isEmpty(etPhone.getText())) {
                    int selectionStart = etPhone.getSelectionStart();
                    String text = etPhone.getText().toString();
                    char c = text.charAt(selectionStart);
                    if (c == ';') {
                        missTextChange = true;
                        String sbs1 = text.substring(0, selectionStart);
                        String sbs2 = text.substring(selectionStart, text.length());
                        int index = sbs1.lastIndexOf(';');
                        String sbs3 = sbs1.substring(0, index + 1);
                        String sbs4 = sbs2.substring(1, sbs2.length());
                        etPhone.setText(sbs3 + sbs4);
                        Log.d(TAG, "sbs1=" + sbs1 + ",sbs2=" + sbs2 + ",sbs3=" + sbs3 + ",sbs4="
                                + sbs4);
                        missTextChange = false;
                        return true;
                    }
                }
                finish();
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    private ArrayList<String> getRecievers() {
        String str = etPhone.getText().toString();
        String[] phones = str.split(";");
        ArrayList<String> phoneArr = new ArrayList<String>();
        for (int i = 0; i < phones.length; i++) {
            if (!TextUtils.isEmpty(getContact(phones[i]))) {
                phoneArr.add(getContact(phones[i]));
            }
            if (!phoneArr.contains(phones[i]))
                phoneArr.add(phones[i]);
        }
        return phoneArr;
    }

    private String getContact(String key) {
        Set<Map.Entry<String, String>> entries = selectedContacts.entrySet();
        Iterator<Map.Entry<String, String>> iterator = entries.iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            if (TextUtils.equals(next.getKey(), key)) {
                return next.getValue();
            }
        }
        return null;
    }

    private void getReceivers() {
        String text = etPhone.getText().toString();
        String[] split = text.split(";");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "requestCode=" + requestCode + ",resultCode=" + resultCode);
        if (requestCode == INTENT_NEW_MSG_OPTIONS) {
            if (resultCode == RESULT_OK) {
                String action = data.getStringExtra(Constants.ACTION);
                if (TextUtils.equals(action, Constants.GET_CONTACT_BEAN)) {
                    String name = data.getStringExtra(Constants.CONTACT_NAME);
                    String phone = data.getStringExtra(Constants.CONTACT_PHONE);
                    ;
                    selectedContacts.put(name, phone);
                    if (!containsReceiver(mContactsReceivers, phone)) {
                        missTextChange = true;
                        String text = etPhone.getText().toString();
                        text += "\n" + phone + ";";
                        etPhone.setText(text);
                        etPhone.setSelection(etPhone.getText().length());
                        missTextChange = false;
                    }
                }
            }
        }
    }

    private boolean containsReceiver(ArrayList<SmsReceiver> list, String phone) {
        for (int i = 0; i < list.size(); i++) {
            if (TextUtils.equals(list.get(i).phone, phone)) {
                return true;
            }
        }
        return false;
    }

}
