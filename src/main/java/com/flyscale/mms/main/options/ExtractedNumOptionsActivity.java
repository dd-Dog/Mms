package com.flyscale.mms.main.options;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.flyscale.mms.R;
import com.flyscale.mms.bean.SmsInfo;
import com.flyscale.mms.constants.Constants;

public class ExtractedNumOptionsActivity extends Activity {

    private static final String TAG = "MainActivity";
    private ListView mMainTree;
    private String[] mMainData;
    private MainTreeAdapter mMainTreeAdapter;
    private SmsInfo smsInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    private void initData() {
        mMainData = getResources().getStringArray(R.array.extracnumoptions);
        smsInfo = (SmsInfo) getIntent().getSerializableExtra(Constants.MSG_INFO);
    }

    private void initView() {
        mMainTree = (ListView) findViewById(R.id.main);
        mMainTreeAdapter = new MainTreeAdapter();
        mMainTree.setAdapter(mMainTreeAdapter);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(getResources().getString(R.string.extrac_num));

        mMainTree.setDivider(null);
        mMainTree.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handlePosition(position);
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                int position = mMainTree.getSelectedItemPosition();
                handlePosition(position);
                return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void handlePosition(int position) {
        if (position == 0) {
            Intent intent = new Intent();
            intent.setClassName("com.flyscale.contacts",
                    "com.flyscale.contacts.main.options.NewContactOptionsActivity");
            intent.putExtra(Constants.ACTION, Constants.EXTRACT_SAVE);
            intent.putExtra(Constants.CONTACT_NAME, smsInfo.getPerson());
            intent.putExtra(Constants.CONTACT_PHONE, smsInfo.getPhoneNumber());
            startActivity(intent);
            finish();
        }else if (position == 1) {
            Intent call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + smsInfo
                    .getPhoneNumber()));
            startActivity(call);
        }
    }

    class MainTreeAdapter extends BaseAdapter {

        public MainTreeAdapter() {
        }

        @Override
        public int getCount() {
            return mMainData.length;
        }

        @Override
        public String getItem(int position) {
            return mMainData[position % mMainData.length];
        }

        @Override
        public long getItemId(int position) {
            return position % mMainData.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView item = (TextView) getLayoutInflater().inflate(R.layout.item, parent, false);
            item.setText(mMainData[position % mMainData.length]);
            return item;
        }
    }
}
