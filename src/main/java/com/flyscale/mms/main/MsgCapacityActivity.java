package com.flyscale.mms.main;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.flyscale.mms.R;
import com.flyscale.mms.main.options.MarkOptionsActivity;
import com.flyscale.mms.util.SmsUtil;

import java.util.ArrayList;

/**
 * Created by MrBian on 2018/1/11.
 */

public class MsgCapacityActivity extends Activity {

    private static final String TAG = "MsgCapacityActivity";
    private ListView mOptions;
    private String[] mOptionsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capacity_activity);

        initData();
        initView();
    }

    private void initData() {
        mOptionsData = getResources().getStringArray(R.array.capacity);
        ArrayList<SmsMessage> smsList = new ArrayList<SmsMessage>();
        Log.d(TAG, "smsList=" + smsList);
    }

    private void initView() {
        TextView title = (TextView)findViewById(R.id.title);
        title.setText(getResources().getString(R.string.capacity));
        findViewById(R.id.confirm).setVisibility(View.INVISIBLE);
        mOptions = (ListView) findViewById(R.id.main);
        mOptions.setDivider(null);
        CapacityAdapter adapter = new CapacityAdapter();
        mOptions.setAdapter(adapter);
    }

    class CapacityAdapter extends BaseAdapter {

        public CapacityAdapter() {
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
