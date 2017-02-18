package com.admin.ht.module;

import android.os.Bundle;

import com.admin.ht.R;
import com.admin.ht.base.BaseActivity;

public class GroupActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected String getTAG() {
        return "Group";
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
        return R.layout.activity_group;
    }


}
