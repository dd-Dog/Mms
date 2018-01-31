package com.flyscale.mms.main.options;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.flyscale.mms.R;
import com.flyscale.mms.constants.Constants;
import com.flyscale.mms.dialog.SavingDialog;
import com.flyscale.mms.main.CommonActivity;
import com.flyscale.mms.main.EditPhoneNumberActivity;
import com.flyscale.mms.main.EmojisActivity;

/**
 * Created by MrBian on 2018/1/11.
 */

public class NewMsgOptionsActivity extends Activity {

    private static final String TAG = "NewMsgOptionsActivity";
    private static final int SEND_MSG_OPTIONS = 1001;
    private static final int QUIT_EDIT_NEW_MSG = 1002;
    private static final int SAVE_DRAFT = 1016;
    private static final int GET_CONTACT = 1019;
    private ListView mOptions;
    private String[] mOptionsData;
    private Class[] mActivities = new Class[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_msg_options);

        initData();
        initView();
    }

    private void initData() {
        mOptionsData = getResources().getStringArray(R.array.newmsgoptions);
        mActivities[0] = EditPhoneNumberActivity.class;
        mActivities[1] = CommonActivity.class;
        mActivities[3] = EmojisActivity.class;
    }

    private void initView() {
        mOptions = (ListView)findViewById(R.id.main);
        mOptions.setDivider(null);
        OptionsAdapter optionsAdapter = new OptionsAdapter();
        mOptions.setAdapter(optionsAdapter);
        mOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleOptions(position);
            }
        });
    }

    private void handleOptions(int position) {
        if (position <= 3) {
            if (position == 2) {
                Intent selectContact = new Intent();
                ComponentName componentName = new ComponentName("com.flyscale.contacts",
                        "com.flyscale.contacts.main.ContactsListActivity");
                selectContact.setComponent(componentName);
                selectContact.putExtra(Constants.ACTION, Constants.PICK_CONTACT);
                startActivityForResult(selectContact, GET_CONTACT);
                return;
            }
            Intent intent = new Intent(NewMsgOptionsActivity.this, mActivities[position]);
            if (position == 0) {
                intent.putExtra(Constants.ACTION,
                        getIntent().getStringExtra(Constants.ACTION));
            }
            intent.putExtra(Constants.NEW_MSG,
                    getIntent().getStringExtra(Constants.NEW_MSG));
            intent.putExtra(Constants.NEW_MSG_NUM,
                    getIntent().getStringExtra(Constants.NEW_MSG_NUM));
            startActivityForResult(intent, position);

        } else if (position == 4) {
            String newmsg = getIntent().getStringExtra(Constants.NEW_MSG);
            if (!TextUtils.isEmpty(newmsg)) {
                Intent intent = new Intent(this, SavingDialog.class);
                intent.putExtra(Constants.NEW_MSG,
                        getIntent().getStringExtra(Constants.NEW_MSG));
                intent.putExtra(Constants.ACTION, Constants.SAVE_DRAFT);
                startActivityForResult(intent, SAVE_DRAFT);
            }

        } else if (position == 5) {
            myFinish();
        }
    }

    private void myFinish() {
        Intent result = new Intent();
        result.putExtra("action", "exit");
        setResult(RESULT_OK, result);
        finish();
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        int position = mOptions.getSelectedItemPosition();
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                handleOptions(position);
                break;
            case KeyEvent.KEYCODE_BACK:
                finish();
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "requestCode=" + requestCode + ",resultCode=" + resultCode);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GET_CONTACT:
//                    ContactBean contactBean = (ContactBean) data.getSerializableExtra(Constants.CONTACT_BEAN);
                case 1:
                case 2:
                case 3:
                    setResult(RESULT_OK, data);
                    Log.d(TAG, "intent data=" + data.getStringExtra(Constants.INTENT_DATA));
                    finish();
                    break;

                case SAVE_DRAFT:
                    setResult(RESULT_OK, data);
                    finish();
                    break;
            }
        }
    }

    private void myFinish(String data) {
        Intent result = new Intent();
        result.putExtra("data", data);
        setResult(RESULT_OK, result);
        finish();
    }

    class OptionsAdapter extends BaseAdapter {

        public OptionsAdapter() {
        }

        @Override
        public int getCount() {
            return mOptionsData.length;
        }

        @Override
        public String getItem(int position) {
            return mOptionsData[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView item = (TextView) getLayoutInflater().inflate(R.layout.item, null);
            item.setText(mOptionsData[position]);
            return item;
        }
    }
}
