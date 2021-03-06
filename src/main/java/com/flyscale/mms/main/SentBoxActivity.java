package com.flyscale.mms.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.flyscale.mms.R;
import com.flyscale.mms.bean.SmsInfo;
import com.flyscale.mms.constants.Constants;
import com.flyscale.mms.main.options.MarkOptionsActivity;
import com.flyscale.mms.main.options.SentOptionsActivity;
import com.flyscale.mms.util.SmsUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MrBian on 2018/1/11.
 */

public class SentBoxActivity extends Activity {
    private static final String TAG = "SentBoxActivity";
    private static final int GET_SENDBOX_OPTIONS = 1011;
    private ListView mSmsList;
    private ArrayList<SmsInfo> mSendBoxSmsInfo;
    private SmsListAdapter smsListAdapter;
    private boolean markSituation = false;
    private TextView cancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_inbox);

        initData();
        initView();
    }

    private void initData() {
        mSendBoxSmsInfo = SmsUtil.getSmsInfo(this, SmsUtil.SMS_URI_SEND);
        Log.d(TAG, "inBoxSmsInfo=" + mSendBoxSmsInfo);
    }

    private void initView() {
        mSmsList = (ListView)findViewById(R.id.main);
        findViewById(R.id.empty).setVisibility(mSendBoxSmsInfo.size() == 0 ?
                View.VISIBLE : View.GONE);
        mSmsList.setVisibility(mSendBoxSmsInfo.size() == 0 ?
                View.GONE : View.VISIBLE);
        cancel = (TextView)findViewById(R.id.back);
        smsListAdapter = new SmsListAdapter();
        mSmsList.setAdapter(smsListAdapter);
        TextView title = (TextView)findViewById(R.id.title);
        TextView confirm = (TextView)findViewById(R.id.confirm);
        title.setText(getResources().getString(R.string.sent_box));
        confirm.setText(getResources().getString(R.string.options));
        if (mSendBoxSmsInfo == null || mSendBoxSmsInfo.size() == 0) {
            confirm.setVisibility(View.INVISIBLE);
        }
        mSmsList.setDivider(null);
        mSmsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (markSituation) {
                    SmsInfo smsInfo = mSendBoxSmsInfo.get(position);
                    smsInfo.setMark(!smsInfo.isMark());
                    smsListAdapter.notifyDataSetChanged();
                } else {
                    readDetail(position);
                }
            }
        });
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        int selectedItemPosition = mSmsList.getSelectedItemPosition();
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (markSituation) {
                    SmsInfo smsInfo = mSendBoxSmsInfo.get(selectedItemPosition);
                    smsInfo.setMark(!smsInfo.isMark());
                    smsListAdapter.notifyDataSetChanged();
                } else {
                    readDetail(selectedItemPosition);
                }
                return true;

            case KeyEvent.KEYCODE_MENU:
                if (markSituation) {
                    Intent intent = new Intent(this, MarkOptionsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.MSG_INFO, mSendBoxSmsInfo.get
                            (selectedItemPosition));
                    intent.putExtras(bundle);
                    intent.putExtra(Constants.MARK_OPTIONS, Constants.DELTE_MARK);
                    startActivityForResult(intent, GET_SENDBOX_OPTIONS);
                } else {
                    Intent intent2 = new Intent(this, SentOptionsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.MSG_INFO, mSendBoxSmsInfo.get
                            (selectedItemPosition));
                    intent2.putExtras(bundle);
                    startActivityForResult(intent2, GET_SENDBOX_OPTIONS);
                }
                return true;
            case KeyEvent.KEYCODE_BACK:
                if (markSituation) {
                    unMarkAll();
                    return true;
                }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String action = data.getStringExtra(Constants.ACTION);
            Log.d(TAG, "action=" + action);
            int position = mSmsList.getSelectedItemPosition();
            if (TextUtils.equals(action, Constants.DELETE_MSG)) {
                SmsUtil.delete(this, SmsUtil.SMS_URI_INBOX,
                        mSendBoxSmsInfo.get(position).getId() + "");
                mSendBoxSmsInfo.remove(position);
                smsListAdapter.notifyDataSetChanged();
            } else if ((TextUtils.equals(action, Constants.MARK_OPTTION))) {
                String markOption = data.getStringExtra(Constants.MARK_OPTTION);
                if (TextUtils.equals(markOption, Constants.MARK_CURRENT_ITEM)) {
                    markItem(position, true);
                } else if (TextUtils.equals(markOption, Constants.MARK_ALL)) {
                    markAll();
                } else if (TextUtils.equals(markOption, Constants.CANCEL_CURRENT_MARK)) {
                    unMarkItem(position);
                } else if (TextUtils.equals(markOption, Constants.CANCEL_ALL_MARKS)) {
                    unMarkAll();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void unMarkAll() {
        markSituation = false;
        for (int i = 0; i < mSendBoxSmsInfo.size(); i++) {
            mSendBoxSmsInfo.get(i).setMark(false);
        }
        smsListAdapter.notifyDataSetChanged();
        cancel.setText(getResources().getString(R.string.back));
    }

    private void markItem(int position, boolean mark) {
        markSituation = true;
        mSendBoxSmsInfo.get(position).setMark(mark);
        smsListAdapter.notifyDataSetChanged();
        cancel.setText(getResources().getString(R.string.cancel));
    }

    private void unMarkItem(int position) {
        markItem(position, false);
        smsListAdapter.notifyDataSetChanged();
    }

    private void markAll() {
        markSituation = true;
        for (int i = 0; i < mSendBoxSmsInfo.size(); i++) {
            mSendBoxSmsInfo.get(i).setMark(true);
        }
        smsListAdapter.notifyDataSetChanged();
        cancel.setText(getResources().getString(R.string.cancel));
    }

    private void readDetail(int position) {
        Intent msgDetail = new Intent(this, SentBoxMsgDetailActivity.class);
        Bundle bundle = new Bundle();
        SmsInfo smsInfo = mSendBoxSmsInfo.get(position);
        bundle.putSerializable(Constants.MSG_INFO, smsInfo);
        msgDetail.putExtras(bundle);
        startActivityForResult(msgDetail, GET_SENDBOX_OPTIONS);
    }

    class SmsListAdapter extends BaseAdapter {

        public SmsListAdapter() {
        }

        @Override
        public int getCount() {
            return mSendBoxSmsInfo.size();
        }

        @Override
        public SmsInfo getItem(int position) {
            return mSendBoxSmsInfo.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position % mSendBoxSmsInfo.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            if (convertView != null) {
                view = convertView;
            } else {
                view = getLayoutInflater().inflate(R.layout.item_contacts, parent, false);
            }
            ImageView icon = (ImageView)view.findViewById(R.id.contact_icon);
            TextView tv = (TextView)view.findViewById(R.id.tv);
            CheckBox cb = (CheckBox) view.findViewById(R.id.cb);
            cb.setVisibility(markSituation ? View.VISIBLE : View.GONE);
            SmsInfo smsInfo = mSendBoxSmsInfo.get(position);
            if (markSituation) {
                cb.setChecked(smsInfo.isMark());
            }
            icon.setImageResource(smsInfo.isRead() ? R.drawable.mms_read_icon
                    : R.drawable.mms_not_read_icon);
            if (!TextUtils.isEmpty(smsInfo.getPerson())) {
                tv.setText(smsInfo.getPerson());
            } else {
                tv.setText(smsInfo.getPhoneNumber());
            }

            return view;
        }
    }
}
