package com.admin.ht.module;

import android.os.Bundle;

import com.admin.ht.R;
import com.admin.ht.base.BaseActivity;

import butterknife.OnClick;

public class GroupChatActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @OnClick(R.id.back)
    public void back() {
        finish();
    }

    @OnClick(R.id.send)
    public void sendMsg() {

    }

    @Override
    protected String getTAG() {
        return "Group Chat";
    }

    @Override
    public boolean setTranslucent() {
        return true;
    }

    @Override
    public boolean setDebug() {
        return true;
    }

    @Override
    public int setLayoutId() {
        return R.layout.activity_group_chat;
    }


}
