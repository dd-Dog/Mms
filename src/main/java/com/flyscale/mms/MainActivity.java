package com.flyscale.mms;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.flyscale.mms.main.CommonListEditActivity;
import com.flyscale.mms.main.DraftBoxActivity;
import com.flyscale.mms.main.InBoxActivity;
import com.flyscale.mms.main.MsgCapacityActivity;
import com.flyscale.mms.main.NewMsgActivity;
import com.flyscale.mms.main.OutBoxActivity;
import com.flyscale.mms.main.SentBoxActivity;
import com.flyscale.mms.main.SettingsActivity;
import com.flyscale.mms.util.SmsUtil;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private ListView mMainTree;
    private String[] mMainData;
    private MainTreeAdapter mMainTreeAdapter;
    private Class[] mActivities = new Class[8];
    private int[] inBoxSmsCount;
    private int draftBoxCount;
    private int sendBoxCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();

//        Cursor cursor = getContentResolver().query(Uri.parse(
//                "content://com.flyscale.contacts.provider/speed_dial"),
//                null, null, null, null);
//        if (cursor != null) {
//            cursor.moveToFirst();
//            String name = cursor.getString(cursor.getColumnIndex("name"));
//            String phone = cursor.getString(cursor.getColumnIndex("phone"));
//            Log.d(TAG, "name=" + name + ",phone=" + phone);
//        }
    }

    private void initData() {
        mMainData = getResources().getStringArray(R.array.main);

        mActivities[0] = NewMsgActivity.class;
        mActivities[1] = InBoxActivity.class;
        mActivities[2] = OutBoxActivity.class;
        mActivities[3] = DraftBoxActivity.class;
        mActivities[4] = SentBoxActivity.class;
        mActivities[5] = CommonListEditActivity.class;
        mActivities[6] = SettingsActivity.class;
        mActivities[7] = MsgCapacityActivity.class;

    }

    @Override
    protected void onResume() {
        super.onResume();
        inBoxSmsCount = SmsUtil.getInBoxSmsCount(this, SmsUtil.SMS_URI_INBOX);
        draftBoxCount = SmsUtil.getDraftBoxCount(this, SmsUtil.SMS_URI_DRAFT);
        sendBoxCount = SmsUtil.getSendBoxCount(this, SmsUtil.SMS_URI_SEND);
        mMainTreeAdapter.notifyDataSetChanged();
    }

    private void initView() {
        mMainTree = (ListView) findViewById(R.id.main);
        mMainTreeAdapter = new MainTreeAdapter();
        mMainTree.setAdapter(mMainTreeAdapter);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(getResources().getString(R.string.app_name));

        mMainTree.setDivider(null);
        mMainTree.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, mActivities[position]);
                startActivityForResult(intent, position);
            }
        });
//        mMainTree.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
//                                 int totalItemCount) {
//                Log.d(TAG, "firstVisibleItem=" + firstVisibleItem + ",visibleItemCount=" +
//                        visibleItemCount);
//                if (firstVisibleItem <= 2) {
//                    mMainTree.setSelection(mMainData.length + 2);
//                    Log.d(TAG, "setSelection=" + (mMainData.length + 2));
//                } else if (firstVisibleItem + visibleItemCount > mMainTreeAdapter.getCount() -
// 2) {
//                    mMainTree.setSelection(firstVisibleItem - mMainData.length);
//                }
//            }
//        });
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                int selectedItemPosition = mMainTree.getSelectedItemPosition();
                Intent intent = new Intent(MainActivity.this, mActivities[selectedItemPosition]);
                startActivityForResult(intent, selectedItemPosition);
                return true;
        }
        return super.onKeyUp(keyCode, event);
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
            if (position == 1) {
                item.setText(mMainData[position % mMainData.length]
                        + "(" + inBoxSmsCount[1] + "/" + inBoxSmsCount[0] + ")");
            } else if (position == 2) {
                item.setText(mMainData[position % mMainData.length]
                        + "(" + 0 + ")");
            } else if (position == 3) {
                item.setText(mMainData[position % mMainData.length]
                        + "(" + draftBoxCount + ")");
            } else if (position == 4) {
                item.setText(mMainData[position % mMainData.length]
                        + "(" + sendBoxCount + ")");
            } else {
                item.setText(mMainData[position % mMainData.length]);
            }
            return item;
        }
    }
}
