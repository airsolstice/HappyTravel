package com.admin.ht.module;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.admin.ht.R;
import com.admin.ht.adapter.ChatGroupListAdapter;
import com.admin.ht.adapter.ResultListAdapter;
import com.admin.ht.base.BaseActivity;
import com.admin.ht.base.Constant;
import com.admin.ht.model.ChatMember;
import com.admin.ht.model.Result;
import com.admin.ht.model.User;
import com.admin.ht.retro.ApiClient;
import com.admin.ht.utils.KeyBoardUtils;
import com.admin.ht.utils.LogUtils;
import com.admin.ht.utils.ToastUtils;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GroupAdditionActivity extends BaseActivity {

    @Bind(R.id.key_word_edit)
    EditText mKeyWordEdit;
    @Bind(R.id.list_view)
    ListView mResultList;
    @Bind(R.id.tip)
    TextView mTip;

    private User mUser = null;
    private List<ChatMember> mData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        if (mUser == null) {
            mUser = getUser();
        }
    }

    @OnClick(R.id.create_group)
    public void createGroup() {
        final EditText et = new EditText(mContext);
        new AlertDialog.Builder(mContext).setTitle("输入群名")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            Toast.makeText(getApplicationContext(), "群名不能为空" + input, Toast.LENGTH_LONG).show();
                        }
                        else {
                            createSvc(mUser.getId(), input);
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    public void createSvc(String memberId, String groupName) {

        if (TextUtils.isEmpty(groupName)) {
            return;
        }

        ApiClient.service.create(memberId, groupName, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Result>() {
                    Result result = null;
                    ChatGroupListAdapter adapter = null;
                    @Override
                    public void onCompleted() {
                        String str;
                        if (result == null) {
                            str = "未知异常";
                        } else if (result.getCode() == Constant.SUCCESS) {
                            str = "创建成功";

                        } else if (result.getCode() == Constant.FAIL) {
                            str = "创建失败";
                        } else if (result.getCode() == Constant.EXECUTING) {
                            str = "服务器繁忙";
                        } else {
                            str = "未知异常";
                        }
                        LogUtils.v(TAG, str);
                        ToastUtils.showShort(mContext, str);
                    }

                    @Override
                    public void onNext(Result result) {
                        if (isDebug) {
                            LogUtils.i(TAG, result.toString());
                        }
                        this.result = result;
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastUtils.showShort(mContext, "未知异常");
                    }
                });

    }


    @OnClick(R.id.do_search)
    public void search() {
        searchSvc(mKeyWordEdit.getText().toString());
    }

    public void searchSvc(String key) {

        if (TextUtils.isEmpty(key)) {
            return;
        }

        LogUtils.e(TAG, key);

        ApiClient.service.searchGroup(key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Result>() {
                    Result result = null;
                    ChatGroupListAdapter adapter = null;
                    @Override
                    public void onCompleted() {
                        String str;
                        if (result == null) {
                            str = "未知异常";
                        } else if (result.getCode() == Constant.SUCCESS) {
                            str = "搜索结果";
                            mData.clear();
                            LogUtils.e(TAG, result.getModel().toString());
                            ChatMember list = ApiClient.gson.fromJson(result.getModel().toString(), ChatMember.class);
                            mData.add(list);
                            if (mUser == null) {
                                mUser = getUser();
                            }
                            adapter = new ChatGroupListAdapter(mContext, mData, mUser.getId());
                            mResultList.setAdapter(adapter);
                            mTip.setText("");

                        } else if (result.getCode() == Constant.FAIL) {
                            str = "搜索失败";
                            mTip.setText("未找到相关群");
                            if(adapter != null){
                                mData.clear();
                                adapter.notifyDataSetChanged();
                            }
                        } else if (result.getCode() == Constant.EXECUTING) {
                            str = "服务器繁忙";
                        } else {
                            str = "未知异常";
                        }
                        LogUtils.v(TAG, str);
                    }

                    @Override
                    public void onNext(Result result) {
                        if (isDebug) {
                            LogUtils.i(TAG, result.toString());
                        }
                        this.result = result;
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastUtils.showShort(mContext, "未知异常");
                    }
                });

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (KeyBoardUtils.isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    @Override
    protected String getTAG() {
        return "Personal";
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
        return R.layout.activity_group_addition;
    }
}
