package com.flyscale.mms.main;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.flyscale.mms.R;
import com.flyscale.mms.bean.SmsInfo;
import com.flyscale.mms.util.SmsUtil;

import java.util.ArrayList;

/**
 * Created by MrBian on 2018/1/11.
 */

public class OutBoxActivity extends Activity {

    private ArrayList<SmsInfo> mOutBoxInfos;
    private ListView mSmsList;
    private boolean markSituation = false;
    private SmsListAdapter smsListAdapter;
    private TextView cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        initData();
        initView();
    }

    private void initData() {
        mOutBoxInfos = SmsUtil.getSmsInfo(this, SmsUtil.SMS_URI_FAILED);
    }

    private void initView() {
        mSmsList = (ListView) findViewById(R.id.main);
        TextView title = (TextView) findViewById(R.id.title);
        cancel = (TextView) findViewById(R.id.back);
        title.setText(getResources().getString(R.string.out_box));
        findViewById(R.id.empty).setVisibility(mOutBoxInfos.size() == 0 ?
                View.VISIBLE : View.GONE);
        mSmsList.setVisibility(mOutBoxInfos.size() == 0 ?
                View.GONE : View.VISIBLE);
        smsListAdapter = new SmsListAdapter();
    }

    private void unMarkAll() {
        markSituation = false;
        for (int i = 0; i < mOutBoxInfos.size(); i++) {
            mOutBoxInfos.get(i).setMark(false);
        }
        smsListAdapter.notifyDataSetChanged();
        cancel.setText(getResources().getString(R.string.back));
    }

    private void markItem(int position, boolean mark) {
        markSituation = true;
        mOutBoxInfos.get(position).setMark(mark);
        smsListAdapter.notifyDataSetChanged();
        cancel.setText(getResources().getString(R.string.cancel));
    }

    private void unMarkItem(int position) {
        markItem(position, false);
        smsListAdapter.notifyDataSetChanged();
    }

    private void markAll() {
        markSituation = true;
        for (int i = 0; i < mOutBoxInfos.size(); i++) {
            mOutBoxInfos.get(i).setMark(true);
        }
        smsListAdapter.notifyDataSetChanged();
        cancel.setText(getResources().getString(R.string.cancel));
    }

    class SmsListAdapter extends BaseAdapter {

        public SmsListAdapter() {
        }

        @Override
        public int getCount() {
            return mOutBoxInfos.size();
        }

        @Override
        public SmsInfo getItem(int position) {
            return mOutBoxInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position % mOutBoxInfos.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            if (convertView != null) {
                view = convertView;
            } else {
                view = getLayoutInflater().inflate(R.layout.item_contacts, parent, false);
            }
            ImageView icon = (ImageView) view.findViewById(R.id.contact_icon);
            TextView tv = (TextView) view.findViewById(R.id.tv);
            CheckBox cb = (CheckBox) view.findViewById(R.id.cb);
            cb.setVisibility(markSituation ? View.VISIBLE : View.GONE);
            SmsInfo smsInfo = mOutBoxInfos.get(position);
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
