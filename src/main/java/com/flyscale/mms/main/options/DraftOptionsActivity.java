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
import com.flyscale.mms.bean.SmsInfo;
import com.flyscale.mms.constants.Constants;
import com.flyscale.mms.dialog.DeleteConfirmActivity;
import com.flyscale.mms.main.DraftBoxActivity;
import com.flyscale.mms.main.NewMsgActivity;
import com.flyscale.mms.util.SmsUtil;

/**
 * Created by MrBian on 2018/1/11.
 */

public class DraftOptionsActivity extends Activity {

    private static final int DELTE_DRAFT = 1017;
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
        mOptions = (ListView) findViewById(R.id.main);
        mOptions.setDivider(null);
        OptionsAdapter optionsAdapter = new OptionsAdapter();
        mOptions.setAdapter(optionsAdapter);
        mOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handlOptions(position);
            }
        });
    }

    private void handlOptions(int position) {
        switch (position) {
            case 0:
                Intent msgDetail = new Intent(this, NewMsgActivity.class);
                msgDetail.putExtras(getIntent().getExtras());
                msgDetail.putExtra(Constants.ACTION, Constants.EDIT_DRAFT);
                startActivity(msgDetail);
                finish();
                break;
            case 1:
                Intent intent = new Intent(this, DeleteConfirmActivity.class);
                intent.putExtras(getIntent().getExtras());
                intent.putExtra(Constants.SMS_URI, SmsUtil.SMS_URI_DRAFT);
                startActivityForResult(intent, DELTE_DRAFT);
                break;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DELTE_DRAFT) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK, data);
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:

                break;
            case KeyEvent.KEYCODE_BACK:
                finish();
                break;

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
