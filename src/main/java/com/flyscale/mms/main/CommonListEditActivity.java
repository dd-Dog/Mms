package com.flyscale.mms.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.flyscale.mms.R;
import com.flyscale.mms.constants.Constants;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by MrBian on 2018/1/11.
 */

public class CommonListEditActivity extends Activity {

    private static final int REQUEST_EDIT_COMMON = 1014;
    private String[] mData;
    private ListView mListview;
    private TextView confirm;
    private CommonsAdapter adapter;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commons);
        initData();
        initView();
    }


    private void initData() {
        mData = new String[Constants.COMMONS_COUNT];
        refreshData();
    }

    private void refreshData() {

        SharedPreferences sp = getSharedPreferences(Constants.SP_COMMONS, Context
                .MODE_PRIVATE);
        HashSet<String> set = null;
        set = (HashSet<String>) sp.getStringSet(Constants.COMMONS_SET, null);
        int length = 0;
        if (null == set) {
            String[] strArr = getResources().getStringArray(R.array.commons);
            for (int k = 0; k < strArr.length; k++) {
                mData[k] = strArr[k];
            }
            SharedPreferences.Editor editor = sp.edit();
            set = new HashSet<String>();
            for (int i = 0; i < mData.length; i++) {
                set.add(mData[i]);
            }
            editor.putStringSet(Constants.COMMONS_SET, set);
            editor.commit();
            length = strArr.length;
        } else {
            Iterator<String> iterator = set.iterator();
            while (iterator.hasNext()) {
                String next = iterator.next();
                mData[length++] = next;
            }
        }
        for (int j = length; j < Constants.COMMONS_COUNT; j++) {
            mData[j] = "<" + getResources().getString(R.string.empty_content) + ">";
        }
    }

    private void initView() {
        mListview = (ListView)findViewById(R.id.main);
        TextView title = (TextView)findViewById(R.id.title);
        confirm = (TextView)findViewById(R.id.confirm);
        title.setText(getResources().getString(R.string.common_sms));
        confirm.setText(getResources().getString(R.string.edit));
        mListview.setDivider(null);
        adapter = new CommonsAdapter();
        mListview.setAdapter(adapter);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mData[position].contains(getResources().getString(R.string.empty_content))) {
                    editItem();
                }else {
                    Intent newMsg = new Intent(CommonListEditActivity.this, NewMsgActivity.class);
                    newMsg.putExtra(Constants.ACTION, Constants.SNED_COMMON_AS_NEWMSG);
                    newMsg.putExtra(Constants.COMMONS_TEXT, mData[position]);
                    startActivity(newMsg);
                }
            }
        });
    }


    private void myFinish(String action, String data) {
        Intent result = new Intent();
        result.putExtra(Constants.ACTION, action);
        result.putExtra(Constants.INTENT_DATA, data);
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String action = data.getStringExtra(Constants.ACTION);
        if (requestCode == REQUEST_EDIT_COMMON) {
            if (resultCode == RESULT_OK) {
                if (TextUtils.equals(action, Constants.SAVE_COMMONT_TEXT)) {
                    String str = data.getStringExtra(Constants.INTENT_DATA);
                    if (TextUtils.isEmpty(str)) {
                        removeCommon(position);
                    } else {
                        editCommon(str);
                    }
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void removeCommon(int position) {
        String str = mData[position];
        SharedPreferences sp = getSharedPreferences(Constants
                .SP_COMMONS, Context.MODE_PRIVATE);
        Set<String> set = sp.getStringSet(Constants.COMMONS_SET, null);
        set.remove(str);
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(Constants.COMMONS_SET, set);
        refreshData();
        adapter.notifyDataSetChanged();
    }

    private void editCommon(String str) {
        SharedPreferences sp = getSharedPreferences(Constants
                .SP_COMMONS, Context.MODE_PRIVATE);
        Set<String> set = sp.getStringSet(Constants.COMMONS_SET, null);
        set.add(str);
        String old = mData[position];
        if (!old.contains(getResources().getString(R.string.empty_content))) {
            set.remove(old);
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(Constants.COMMONS_SET, set);
        refreshData();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                editItem();
                return true;
            case KeyEvent.KEYCODE_BACK:
                finish();
                return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void editItem() {
        position = mListview.getSelectedItemPosition();
        Intent edittext = new Intent(this, EditTextActivity.class);
        String str = mData[position];
        if (!str.contains(getResources().getString(R.string.empty_content))) {
            edittext.putExtra(Constants.COMMONS_TEXT, str);
        }
        startActivityForResult(edittext, REQUEST_EDIT_COMMON);
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
