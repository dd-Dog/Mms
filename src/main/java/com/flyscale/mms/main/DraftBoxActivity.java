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
import com.flyscale.mms.main.options.DraftOptionsActivity;
import com.flyscale.mms.main.options.InBoxOptionsActivity;
import com.flyscale.mms.main.options.MarkOptionsActivity;
import com.flyscale.mms.util.SmsUtil;

import java.util.ArrayList;

/**
 * Created by MrBian on 2018/1/11.
 */

public class DraftBoxActivity extends Activity {
    private static final String TAG = "DraftBoxActivity";
    private static final int GET_DRAFTBOX_OPTIONS = 1015;
    private ListView mSmsList;
    private ArrayList<SmsInfo> mDraftBoxSmsInfo;
    private SmsListAdapter draftListAdapter;
    private boolean markSituation = false;
    private TextView cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        SmsUtil.getSmsInfo(this, SmsUtil.SMS_URI_INBOX);
        SmsUtil.addDraft(this, SmsUtil.SMS_URI_DRAFT, "保存到草稿箱2222");
        initData();
        initView();
    }

    private void initData() {
        mDraftBoxSmsInfo = SmsUtil.getSmsInfo(this, SmsUtil.SMS_URI_DRAFT);
        Log.d(TAG, "inBoxSmsInfo=" + mDraftBoxSmsInfo);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDraftBoxSmsInfo = SmsUtil.getSmsInfo(this, SmsUtil.SMS_URI_DRAFT);
        draftListAdapter.notifyDataSetChanged();
    }

    private void initView() {
        mSmsList = (ListView) findViewById(R.id.main);
        refreshView();
        cancel = (TextView) findViewById(R.id.back);
        draftListAdapter = new SmsListAdapter();
        mSmsList.setAdapter(draftListAdapter);
        TextView title = (TextView) findViewById(R.id.title);
        TextView confirm = (TextView) findViewById(R.id.confirm);
        if (mDraftBoxSmsInfo.size() == 0) {
            confirm.setVisibility(View.GONE);
        } else {
            confirm.setVisibility(View.VISIBLE);
            confirm.setText(getResources().getString(R.string.options));
        }
        title.setText(getResources().getString(R.string.draft_box));

        mSmsList.setDivider(null);
        mSmsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (markSituation) {
                    SmsInfo smsInfo = mDraftBoxSmsInfo.get(position);
                    smsInfo.setMark(!smsInfo.isMark());
                    draftListAdapter.notifyDataSetChanged();
                } else {
                    editDraft(position);
                }
            }
        });
    }

    private void editDraft(int position) {
        Intent msgDetail = new Intent(DraftBoxActivity.this, NewMsgActivity.class);
        Bundle bundle = new Bundle();
        SmsInfo smsInfo = mDraftBoxSmsInfo.get(position);
        bundle.putSerializable(Constants.MSG_INFO, smsInfo);
        msgDetail.putExtras(bundle);
        msgDetail.putExtra(Constants.ACTION, Constants.EDIT_DRAFT);
        startActivityForResult(msgDetail, GET_DRAFTBOX_OPTIONS);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        int selectedItemPosition = mSmsList.getSelectedItemPosition();
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (mDraftBoxSmsInfo == null || (mDraftBoxSmsInfo.size() == 0)) {
                    return true;
                }
                if (markSituation) {
                    SmsInfo smsInfo = mDraftBoxSmsInfo.get(selectedItemPosition);
                    smsInfo.setMark(!smsInfo.isMark());
                    draftListAdapter.notifyDataSetChanged();
                } else {
                    editDraft(selectedItemPosition);
                }
                return true;

            case KeyEvent.KEYCODE_MENU:
                if (mDraftBoxSmsInfo == null || (mDraftBoxSmsInfo.size() == 0)) {
                    return true;
                }
                Intent intent = new Intent(this, DraftOptionsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.MSG_INFO, mDraftBoxSmsInfo.get
                        (selectedItemPosition));
                intent.putExtra(Constants.MARK_OPTIONS, Constants.REPLY_DELTE_MARK);
                intent.putExtras(bundle);
                startActivityForResult(intent, GET_DRAFTBOX_OPTIONS);
                return true;
            case KeyEvent.KEYCODE_BACK:
                if (markSituation) {
                    unMarkAll();
                    return true;
                }
        }
        return super.onKeyUp(keyCode, event);
    }

    private void unMarkAll() {
        markSituation = false;
        for (int i = 0; i < mDraftBoxSmsInfo.size(); i++) {
            mDraftBoxSmsInfo.get(i).setMark(false);
        }
        draftListAdapter.notifyDataSetChanged();
        cancel.setText(getResources().getString(R.string.back));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String action = data.getStringExtra(Constants.ACTION);
            Log.d(TAG, "action=" + action);
            int position = mSmsList.getSelectedItemPosition();
            if (TextUtils.equals(action, Constants.DELETE_MSG)) {
                SmsUtil.delete(this, SmsUtil.SMS_URI_INBOX,
                        mDraftBoxSmsInfo.get(position).getId() + "");
                mDraftBoxSmsInfo.remove(position);
                draftListAdapter.notifyDataSetChanged();
                refreshView();
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

    private void refreshView() {
        findViewById(R.id.empty).setVisibility(mDraftBoxSmsInfo.size() == 0 ?
                View.VISIBLE : View.GONE);
        mSmsList.setVisibility(mDraftBoxSmsInfo.size() == 0 ?
                View.GONE : View.VISIBLE);
    }

    private void markItem(int position, boolean mark) {
        markSituation = true;
        mDraftBoxSmsInfo.get(position).setMark(mark);
        draftListAdapter.notifyDataSetChanged();
        cancel.setText(getResources().getString(R.string.cancel));
    }

    private void unMarkItem(int position) {
        markItem(position, false);
        draftListAdapter.notifyDataSetChanged();
    }

    private void markAll() {
        markSituation = true;
        for (int i = 0; i < mDraftBoxSmsInfo.size(); i++) {
            mDraftBoxSmsInfo.get(i).setMark(true);
        }
        draftListAdapter.notifyDataSetChanged();
        cancel.setText(getResources().getString(R.string.cancel));
    }

    class SmsListAdapter extends BaseAdapter {

        public SmsListAdapter() {
        }

        @Override
        public int getCount() {
            return mDraftBoxSmsInfo.size();
        }

        @Override
        public SmsInfo getItem(int position) {
            return mDraftBoxSmsInfo.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position % mDraftBoxSmsInfo.size();
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
            SmsInfo smsInfo = mDraftBoxSmsInfo.get(position);
            if (markSituation) {
                cb.setChecked(smsInfo.isMark());
            }
            icon.setImageResource(R.drawable.sms_draft);
            tv.setText(smsInfo.getSmsbody());
            return view;
        }
    }

}
