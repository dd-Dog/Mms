package com.flyscale.mms.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.flyscale.mms.R;
import com.flyscale.mms.constants.Constants;

/**
 * Created by MrBian on 2018/1/11.
 */

public class SmsStorageActivity extends Activity {

    private static final String TAG = "SettingsActivity";
    private String[] mData;
    private ListView mListview;
    private SettingsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    private void initData() {
        mData = getResources().getStringArray(R.array.smsstorage);
        String smsStorage = getMsgStorageSeting();

    }

    private void initView() {
        mListview = (ListView) findViewById(R.id.main);
        TextView title = (TextView)findViewById(R.id.title);
        title.setText(getResources().getString(R.string.sms_storage));
        mListview.setDivider(null);
        adapter = new SettingsAdapter();
        mListview.setAdapter(adapter);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleOptions(position);
            }
        });
    }

    private void handleOptions(int position) {

        switch (position) {
            case 0:
            case 1:
                toggleMsgStorage();
                finish();
                break;
        }

    }

    private void toggleMsgStorage() {
        Log.d(TAG, "toggleSendMsgStatus");
        SharedPreferences sp = getSharedPreferences(Constants.SMS_SETTINGS,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String string = sp.getString(Constants.MSG_STORAGE, Constants.SMS_STORAGE_LOCAL);
        if (TextUtils.equals(string, Constants.SMS_STORAGE_LOCAL)) {
            editor.putString(Constants.MSG_STORAGE, Constants.SMS_STORAGE_SIM);
        } else {
            editor.putString(Constants.MSG_STORAGE, Constants.SMS_STORAGE_LOCAL);
        }
        editor.commit();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                handleOptions(mListview.getSelectedItemPosition());
                break;
            case KeyEvent.KEYCODE_BACK:
                finish();
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    class SettingsAdapter extends BaseAdapter {

        public SettingsAdapter() {

        }

        @Override
        public int getCount() {
            return mData.length;
        }

        @Override
        public String getItem(int position) {
            return mData[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            if (convertView != null) {
                view = convertView;
            } else {
                view = getLayoutInflater().inflate(R.layout.item_contacts, parent, false);
            }
            view.findViewById(R.id.contact_icon).setVisibility(View.GONE);
            TextView tv = (TextView)view.findViewById(R.id.tv);
            CheckBox cb = (CheckBox) view.findViewById(R.id.cb);
            cb.setButtonDrawable(R.drawable.cb_style_oval);
            cb.setVisibility(View.VISIBLE);
            tv.setText(mData[position]);
            boolean equals = TextUtils.equals(getMsgStorageSeting(), Constants.SMS_STORAGE_LOCAL);

            if (position == 0) {
                cb.setChecked(equals ? false : true);
            }else if (position == 1) {
                cb.setChecked(equals ? true : false);
            }
            return view;
        }
    }

    private String getMsgStorageSeting() {
        SharedPreferences sp = getSharedPreferences(Constants.SMS_SETTINGS,
                Context.MODE_PRIVATE);
        return sp.getString(Constants.MSG_STORAGE, Constants.SMS_STORAGE_LOCAL);
    }
}
