package com.admin.ht.module;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.admin.ht.R;
import com.admin.ht.base.BaseActivity;

public class SingleChatActivity extends BaseActivity {

    @Override
    protected String getTAG() {
        return "Single Chat";
    }

    @Override
    public boolean setTranslucent() {
        return true;
    }

    @Override
    public boolean setDebug() {
        return false;
    }

    @Override
    public int setLayoutId() {
        return R.layout.activity_single_chat;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);




    }
}
