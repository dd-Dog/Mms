package com.flyscale.mms.main.options;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
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
import com.flyscale.mms.main.SendMsgsStatusActivity;

/**
 * Created by MrBian on 2018/1/11.
 */

public class SendMsgOptionsActivity extends Activity {


    private static final int SEND_MSG_EDIT_NUMBER = 1003;
    private static final int GET_CONTACT_NUMBER = 1018;
    private ListView mOptions;
    private String[] mOptionsData;
    private static final String TAG = "SendMsgOptionsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_msg_options);

        initData();
        initView();
    }

    private void initData() {
        mOptionsData = getResources().getStringArray(R.array.sendoptions);
    }

    private void initView() {
        mOptions = (ListView)findViewById(R.id.main);
        mOptions.setDivider(null);
        OptionsAdapter optionsAdapter = new OptionsAdapter();
        mOptions.setAdapter(optionsAdapter);
        mOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleOPtions(position);
            }
        });

    }

    private void handleOPtions(int position) {
        if (position == 0) {
            Intent editnumber = new Intent(SendMsgOptionsActivity.this,
                    SendMsgsStatusActivity.class);
            editnumber.putExtra(Constants.NEW_MSG, getIntent().getStringExtra(Constants.NEW_MSG));
            editnumber.putExtra(Constants.NEW_MSG_NUM, getIntent().getStringExtra(Constants.NEW_MSG_NUM));
            startActivityForResult(editnumber, SEND_MSG_EDIT_NUMBER);
            finish();
        } else if (position == 1) {
            Intent selectContact = new Intent();
            selectContact.putExtra(Constants.ACTION, Constants.PICK_CONTACT);
            ComponentName componentName = new ComponentName("com.flyscale.contacts",
                    "com.flyscale.contacts.main.ContactsListActivity");
            selectContact.setComponent(componentName);
            startActivityForResult(selectContact, GET_CONTACT_NUMBER);
        }
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                int position = mOptions.getSelectedItemPosition();
                handleOPtions(position);
                break;
            case KeyEvent.KEYCODE_BACK:
                finish();
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "requestCode=" + requestCode + ",resultCode=" + resultCode);
        if (requestCode == GET_CONTACT_NUMBER) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK, data);
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
