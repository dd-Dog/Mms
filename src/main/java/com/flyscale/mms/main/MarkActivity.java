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

public class MarkActivity extends Activity {

    private ListView mOptions;
    private String[] mOptionsData;
    private String[] markOptions = new String[4];
    private boolean markSitudation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_msg_options);

        initData();
        initView();
    }

    private void initData() {
        markSitudation = getIntent().getBooleanExtra(Constants.MARK_SITUDATION, false);
        mOptionsData = getResources().getStringArray(R.array.mark);
        markOptions[0] = Constants.MARK_CURRENT_ITEM;
        markOptions[1] = Constants.CANCEL_CURRENT_MARK;
        markOptions[2] = Constants.MARK_ALL;
        markOptions[3] = Constants.CANCEL_ALL_MARKS;
    }

    private void initView() {
        mOptions = (ListView) findViewById(R.id.main);
        mOptions.setDivider(null);
        OptionsAdapter optionsAdapter = new OptionsAdapter();
        mOptions.setAdapter(optionsAdapter);
        mOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra(Constants.ACTION, Constants.MARK_OPTTION);
                if (!markSitudation && (position == 0 || position == 2)) {
                    intent.putExtra(Constants.MARK_OPTTION, markOptions[position]);
                    setResult(RESULT_OK, intent);
                    finish();
                } else if (markSitudation) {
                    intent.putExtra(Constants.MARK_OPTTION, markOptions[position]);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        int position = mOptions.getSelectedItemPosition();
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                Intent intent = new Intent();
                intent.putExtra(Constants.ACTION, Constants.MARK_OPTTION);
                intent.putExtra(Constants.MARK_OPTTION, markOptions[position]);
                setResult(RESULT_OK, intent);
                finish();
                return true;
            case KeyEvent.KEYCODE_BACK:
                finish();
                return true;

        }
        return super.onKeyUp(keyCode, event);
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
