package com.flyscale.mms.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.flyscale.mms.main.options.DraftOptionsActivity;
import com.flyscale.mms.main.options.NewMsgOptionsActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by MrBian on 2018/1/11.
 */

public class CommonActivity extends Activity {

    private static final String TAG = "CommonActivity";
    private String[] mData;
    private ListView mListview;
    private TextView confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commons);
        initData();
        initView();
    }


    private void initData() {
        refreshData();
    }

    private void refreshData() {
        SharedPreferences sp = getSharedPreferences(Constants.SP_COMMONS, Context
                .MODE_PRIVATE);
        HashSet<String> set = null;
        set = (HashSet<String>) sp.getStringSet(Constants.COMMONS_SET, null);
        int length = 0;
        if (null == set) {
            mData = getResources().getStringArray(R.array.commons);
            SharedPreferences.Editor editor = sp.edit();
            set = new HashSet<String>();
            for (int i = 0; i < mData.length; i++) {
                set.add(mData[i]);
            }
            editor.putStringSet(Constants.COMMONS_SET, set);
            editor.commit();
        } else {
            Iterator<String> iterator = set.iterator();
            ArrayList<String> commons = new ArrayList<String>();
            while (iterator.hasNext()) {
                String next = iterator.next();
                commons.add(next);
            }
            mData = new String[commons.size()];
            for (int i=0; i<commons.size(); i++) {
                mData[i] = commons.get(i);
            }
        }
    }

    private void initView() {
        mListview = (ListView)findViewById(R.id.main);
        TextView title = (TextView)findViewById(R.id.title);
        confirm = (TextView)findViewById(R.id.confirm);
        title.setText(getResources().getString(R.string.common_sms));
        mListview.setDivider(null);
        CommonsAdapter adapter = new CommonsAdapter();
        mListview.setAdapter(adapter);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myFinish(Constants.ACTION_APPEND, mData[position]);
            }
        });
    }


    private void myFinish(String action, String data) {
        Intent result = new Intent();
        result.putExtra(Constants.ACTION, action);
        result.putExtra(Constants.INTENT_DATA, data);
        Log.d(TAG, "intent data=" + data);
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                int position = mListview.getSelectedItemPosition();
                myFinish(Constants.ACTION_APPEND, mData[position]);
                return true;
            case KeyEvent.KEYCODE_BACK:
                finish();
                return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    class CommonsAdapter extends BaseAdapter {

        public CommonsAdapter() {
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
            TextView item = (TextView) getLayoutInflater().inflate(R.layout.item, null);
            item.setText(mData[position]);
            return item;
        }
    }
}
