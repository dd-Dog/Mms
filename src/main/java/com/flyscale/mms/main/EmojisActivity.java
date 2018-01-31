package com.flyscale.mms.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.flyscale.mms.R;
import com.flyscale.mms.constants.Constants;

/**
 * Created by MrBian on 2018/1/11.
 */

public class EmojisActivity extends Activity {

    private String[] mData;
    private ListView mListview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commons);
        initData();
        initView();
    }

    private void initData() {
        mData = getResources().getStringArray(R.array.emojis);
    }

    private void initView() {
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(getResources().getString(R.string.emojis));
        mListview = (ListView) findViewById(R.id.main);
        mListview.setDivider(null);
        SettingsAdapter adapter = new SettingsAdapter();
        mListview.setAdapter(adapter);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myFinish(mData[position]);
            }
        });
    }

    private void myFinish(String mDatum) {
        Intent intent = new Intent();
        intent.putExtra(Constants.ACTION, Constants.ACTION_GET_EMOJIS);
        intent.putExtra(Constants.INTENT_DATA, mDatum);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                myFinish(mData[mListview.getSelectedItemPosition()]);
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
            TextView item = (TextView) getLayoutInflater().inflate(R.layout.item, null);
            item.setText(mData[position]);
            return item;
        }
    }
}
