package com.flyscale.mms.main.options;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.flyscale.mms.R;
import com.flyscale.mms.bean.SmsInfo;
import com.flyscale.mms.constants.Constants;
import com.flyscale.mms.dialog.DeleteConfirmActivity;
import com.flyscale.mms.main.ExtractedNumberActivity;
import com.flyscale.mms.main.MsgRealDetailActivity;
import com.flyscale.mms.main.NewMsgActivity;
import com.flyscale.mms.util.SmsUtil;

/**
 * Created by MrBian on 2018/1/11.
 */

public class MsgDetailOptionsActivity extends Activity {

    private static final int DELETE_MSG_CONFIRM = 1010;
    private ListView mOptions;
    private String[] mOptionsData;
    private SmsInfo smsInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_msg_options);

        initData();
        initView();
    }

    private void initData() {
        smsInfo = (SmsInfo) getIntent().getSerializableExtra(Constants.MSG_INFO);
        mOptionsData = getResources().getStringArray(R.array.msgdetailoptions);
    }

    private void initView() {
        mOptions = (ListView)findViewById(R.id.main);
        mOptions.setDivider(null);
        OptionsAdapter optionsAdapter = new OptionsAdapter();
        mOptions.setAdapter(optionsAdapter);
        mOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleOptions(position);
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void handleOptions(int position) {
        switch (position) {
            case 0:
                Intent delete = new Intent(MsgDetailOptionsActivity.this, DeleteConfirmActivity.class);
                delete.putExtras(getIntent().getExtras());
                delete.putExtra(Constants.SMS_URI, SmsUtil.SMS_URI_INBOX);
                startActivityForResult(delete, DELETE_MSG_CONFIRM);
                break;
            case 1:
                Intent reply = new Intent(MsgDetailOptionsActivity.this, NewMsgActivity.class);
                reply.putExtras(getIntent().getExtras());
                reply.putExtra(Constants.ACTION, Constants.REPLY_MSG);
                startActivity(reply);
                finish();
                break;
            case 2:
                Intent forward = new Intent(MsgDetailOptionsActivity.this, NewMsgActivity.class);
                forward.putExtras(getIntent().getExtras());
                forward.putExtra(Constants.ACTION, Constants.FORWARD_MSG);
                startActivity(forward);
                finish();
                break;
            case 3:
                Intent call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + smsInfo
                        .getPhoneNumber()));
                startActivity(call);
                break;
            case 4:
                Intent realDetail = new Intent(MsgDetailOptionsActivity.this, MsgRealDetailActivity.class);
                realDetail.putExtras(getIntent().getExtras());
                realDetail.putExtra(Constants.BOX_TYPE, Constants.INBOX);
                startActivity(realDetail);
                finish();
                break;
            case 5:
                Intent option = new Intent(this, ExtractedNumberActivity.class);
                option.putExtras(getIntent().getExtras());
                startActivity(option);
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent intent = new Intent();
        if (requestCode == DELETE_MSG_CONFIRM) {
            if (resultCode == RESULT_OK) {
                intent.putExtra(Constants.ACTION, Constants.DELETE_MSG);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        int position = mOptions.getSelectedItemPosition();
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                handleOptions(position);
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
