package com.yixin.test;

import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;

import com.yixin.test.util.DataInfoCache;
import com.yixin.test.util.TestUtil;
import com.yixin.test.util.YxjrUtil;

import java.util.ArrayList;

public class MyTestActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private Spinner mPartnerId;
    private AutoCompleteTextView mChannel, mName, mIdCard, mPhoneNum;
    private Button mIntoSDK;

    private String partnerId, key;
    private String[] codes;
    private String[] keys;

    private final String POSITION = "partnerId_position";
    private final String CHANNEL = "channel";//
    private final String NAME = "name";//
    private final String IDCARD = "idCard";
    private final String PHONENUM = "phoneNum";
    private final String AUTOCHANNEL = "autoChannel";
    private final String AUTONAME = "autoName";
    private final String AUTOIDCARD = "autoIdCard";
    private final String AUTOPHONENUM = "autoPhoneNum";

    private ArrayList<Object> channelList;
    private ArrayList<Object> nameList;
    private ArrayList<Object> idcardList;
    private ArrayList<Object> phoneNumList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//默认不弹出软键盘
        setContentView(R.layout.activity_test_main);
        this.getSupportActionBar().setTitle(this.getApplication().getPackageName());
        init();
    }

    private void init() {
        findView();
        //
        getAutoComplete();
        //
        codes = getResources().getStringArray(R.array.codes);
        keys = getResources().getStringArray(R.array.keys);
        //
        int pos = TestUtil.getInt(this, POSITION);
        mPartnerId.setSelection(pos);//设置默认值
        partnerId = codes[pos];
        key = keys[pos];
        //
        mChannel.setText(TestUtil.getString(this, CHANNEL));
        mName.setText(TestUtil.getString(this, NAME));
        mIdCard.setText(TestUtil.getString(this, IDCARD));
        mPhoneNum.setText(TestUtil.getString(this, PHONENUM));
    }

    private void findView() {
        mPartnerId = (Spinner) findViewById(R.id.partnerId);
        mName = (AutoCompleteTextView) findViewById(R.id.name);
        mChannel = (AutoCompleteTextView) findViewById(R.id.channel);
        mIdCard = (AutoCompleteTextView) findViewById(R.id.idCard);
        mPhoneNum = (AutoCompleteTextView) findViewById(R.id.phoneNum);
        mIntoSDK = (Button) findViewById(R.id.intoSDK);
        mIntoSDK.setOnClickListener(this);
        mPartnerId.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        partnerId = codes[position];
        key = keys[position];
        TestUtil.saveInt(MyTestActivity.this, POSITION, position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.intoSDK:
                YxjrUtil.startCredit(this, getParam());
                saveData();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.get_user:
                YxjrUtil.getUser(this, getParam());
                return true;
            case R.id.get_account:
                YxjrUtil.getAccount(this, getParam());
                return true;
            case R.id.get_contract:
                YxjrUtil.getContract(this, getParam());
                return true;
            case R.id.get_param:
                YxjrUtil.getParam(this, getParam());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private Bundle getParam() {
        return YxjrUtil.getBundle(partnerId, mChannel.getText().toString(), mName.getText().toString(), mIdCard.getText().toString(), mPhoneNum.getText().toString(), key);
    }

    private void saveData() {
        TestUtil.saveString(this, CHANNEL, mChannel.getText().toString());
        TestUtil.saveString(this, NAME, mName.getText().toString());
        TestUtil.saveString(this, IDCARD, mIdCard.getText().toString());
        TestUtil.saveString(this, PHONENUM, mPhoneNum.getText().toString());

        putAutoComplete();
    }

    private void setAutoComplete() {
        ArrayAdapter<Object> adapterChannel =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_dropdown_item_1line, channelList);
        ArrayAdapter<Object> adapterName =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_dropdown_item_1line, nameList);
        ArrayAdapter<Object> adapterIdCard =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_dropdown_item_1line, idcardList);
        ArrayAdapter<Object> adapterPhoneNum =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_dropdown_item_1line, phoneNumList);
        mChannel.setAdapter(adapterChannel);
        mName.setAdapter(adapterName);
        mIdCard.setAdapter(adapterIdCard);
        mPhoneNum.setAdapter(adapterPhoneNum);
    }

    private void getAutoComplete() {

        channelList = DataInfoCache.loadListCache(this, AUTOCHANNEL);
        if (null == channelList) {
            channelList = new ArrayList<>();
        }
        nameList = DataInfoCache.loadListCache(this, AUTONAME);
        if (null == nameList) {
            nameList = new ArrayList<>();
        }
        idcardList = DataInfoCache.loadListCache(this, AUTOIDCARD);
        if (null == idcardList) {
            idcardList = new ArrayList<>();
        }
        phoneNumList = DataInfoCache.loadListCache(this, AUTOPHONENUM);
        if (null == phoneNumList) {
            phoneNumList = new ArrayList<>();
        }

        setAutoComplete();
    }

    private void putAutoComplete() {

        if (!channelList.contains(mChannel.getText().toString())) {
            channelList.add(mChannel.getText().toString());
        }
        if (!nameList.contains(mName.getText().toString())) {
            nameList.add(mName.getText().toString());
        }
        if (!idcardList.contains(mIdCard.getText().toString())) {
            idcardList.add(mIdCard.getText().toString());
        }
        if (!phoneNumList.contains(mPhoneNum.getText().toString())) {
            phoneNumList.add(mPhoneNum.getText().toString());
        }

        DataInfoCache.saveListCache(this, channelList, AUTOCHANNEL);
        DataInfoCache.saveListCache(this, nameList, AUTONAME);
        DataInfoCache.saveListCache(this, idcardList, AUTOIDCARD);
        DataInfoCache.saveListCache(this, phoneNumList, AUTOPHONENUM);

        setAutoComplete();
    }

}

