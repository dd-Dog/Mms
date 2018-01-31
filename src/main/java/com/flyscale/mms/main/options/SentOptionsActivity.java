package com.flyscale.mms.main.options;

import android.app.Activity;
import android.content.Intent;
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

import com.flyscale.mms.R;
import com.flyscale.mms.constants.Constants;
import com.flyscale.mms.dialog.DeleteConfirmActivity;
import com.flyscale.mms.main.InBoxMsgDetailActivity;
import com.flyscale.mms.main.MarkActivity;
import com.flyscale.mms.main.NewMsgActivity;
import com.flyscale.mms.main.SentBoxMsgDetailActivity;
import com.flyscale.mms.util.SmsUtil;

/**
 * Created by MrBian on 2018/1/11.
 */

public class SentOptionsActivity extends Activity {

    private static final String TAG = "SentOptionsActivity";
    private static final int DELETE_MSG_CONFIRM = 1012;
    private static final int GET_MARK_OPTIONS = 1013;
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
        mOptionsData = getResources().getStringArray(R.array.sentoptions);
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

    private void handleOptions(int position) {
        switch (position) {
            case 0:
                Intent msgDetail = new Intent(this, SentBoxMsgDetailActivity.class);
                msgDetail.putExtras(getIntent().getExtras());
                startActivity(msgDetail);
                finish();
                break;
            case 1:
                Intent delete = new Intent(this, DeleteConfirmActivity.class);
                delete.putExtras(getIntent().getExtras());
                delete.putExtra(Constants.SMS_URI, SmsUtil.SMS_URI_SEND);
                startActivityForResult(delete, DELETE_MSG_CONFIRM);
                break;
            case 2:
                Intent forward = new Intent(this, NewMsgActivity.class);
                forward.putExtras(getIntent().getExtras());
                forward.putExtra(Constants.ACTION, Constants.FORWARD_MSG);
                startActivity(forward);
                finish();
                break;

            case 3:
                Intent markoptions = new Intent(this,
                        MarkActivity.class);
                markoptions.putExtra(Constants.MARK_SITUDATION, false);
                startActivityForResult(markoptions, GET_MARK_OPTIONS);
                break;
        }

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        int position = mOptions.getSelectedItemPosition();
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                handleOptions(position);
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
                Log.d(TAG, "GET_MARK_OPTIONS ok");
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
