package com.flyscale.mms.main.options;

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
import com.flyscale.mms.dialog.DeleteConfirmActivity;
import com.flyscale.mms.main.MarkActivity;
import com.flyscale.mms.main.NewMsgActivity;
import com.flyscale.mms.util.SmsUtil;

/**
 * Created by MrBian on 2018/1/11.
 */

public class DrafBoxOptionsActivity extends Activity {

    private static final int DELETE_MSG_CONFIRM = 1007;
    private static final int GET_MARK_OPTIONS = 1008;
    private ListView mOptions;
    private String[] mOptionsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_msg_options);

        initData();
        initView();
    }

    private void initData() {
        mOptionsData = getResources().getStringArray(R.array.draftboxoptions);
    }

    private void initView() {
        mOptions = (ListView)findViewById(R.id.main);
        mOptions.setDivider(null);
        OptionsAdapter optionsAdapter = new OptionsAdapter();
        mOptions.setAdapter(optionsAdapter);
        mOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleOption(position);
            }
        });
    }

    private void handleOption(int position) {
        switch (position) {
            case 0:
                Intent reply = new Intent(DrafBoxOptionsActivity.this, NewMsgActivity.class);
                reply.putExtras(getIntent().getExtras());
                reply.putExtra(Constants.ACTION, Constants.REPLY_MSG);
                startActivity(reply);
                finish();
                break;
            case 1:
                Intent delete = new Intent(DrafBoxOptionsActivity.this, DeleteConfirmActivity.class);
                delete.putExtras(getIntent().getExtras());
                delete.putExtra(Constants.SMS_URI, SmsUtil.SMS_URI_INBOX);
                startActivityForResult(delete, DELETE_MSG_CONFIRM);
                break;
            case 2:
                Intent markoptions = new Intent(DrafBoxOptionsActivity.this, MarkActivity
                        .class);
                markoptions.putExtra(Constants.MARK_SITUDATION, true);
                startActivityForResult(markoptions, GET_MARK_OPTIONS);
                break;
        }
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                int position = mOptions.getSelectedItemPosition();
                handleOption(position);
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent intent = new Intent();
        if (requestCode == DELETE_MSG_CONFIRM) {
            if (resultCode == RESULT_OK) {
                intent.putExtra(Constants.ACTION, Constants.DELETE_MSG);
                setResult(RESULT_OK, intent);
            }
            finish();
        } else if (requestCode == GET_MARK_OPTIONS) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK, data);
            }
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
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
