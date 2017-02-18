package com.admin.ht.module;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import com.admin.ht.R;
import com.admin.ht.base.BaseActivity;
import butterknife.Bind;
import butterknife.OnClick;

public class HomeActivity extends BaseActivity {


    @Bind(R.id.container)
    RelativeLayout mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

    }

    @OnClick(R.id.switch_group)
    public void switchGroup() {
        Snackbar.make(mContainer, "好友组",
                Snackbar.LENGTH_SHORT)
                .setAction("查看分组", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(HomeActivity.this,GroupActivity.class));
                    }
                })
                .show();
    }


    @Override
    protected String getTAG() {
        return "Home";
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
        return R.layout.activity_home;
    }
}
