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
import com.admin.ht.adapter.ResultListAdapter;
import com.admin.ht.base.BaseActivity;
import com.admin.ht.base.Constant;
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

public class PersonalAdditionActivity extends BaseActivity {

    @Bind(R.id.key_word_edit)
    EditText mKeyWordEdit;
    @Bind(R.id.list_view)
    ListView mResultList;
    @Bind(R.id.tip)
    TextView mTip;

    private User mHolderUser = null;
    private List<User> mData = new ArrayList<>();
    private List<String> mGroups = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        mGroups.clear();
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        List<String> list = ApiClient.gson.fromJson(mPreferences.getString(Constant.GROUP_NAME_LIST, "{}"), type);
        mGroups.addAll(list);

    }

    @OnClick(R.id.list_group)
    public void listGroup() {
        String[] groups = mGroups.toArray(new String[mGroups.size()]);
        new  AlertDialog.Builder(mContext)
                .setTitle("分组列表" )
                .setItems(groups,  null )
                .setNegativeButton("返回" ,  null )
                .show();
    }


    @OnClick(R.id.manage_group)
    public void addGroup(){
        final EditText et = new EditText(mContext);

        new AlertDialog.Builder(mContext).setTitle("输入分组名")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            Toast.makeText(getApplicationContext(), "分组名不能为空" + input, Toast.LENGTH_LONG).show();
                        }
                        else {
                            mGroups.add(input);
                            Toast.makeText(getApplicationContext(), "新增分组:" + input
                                    +"\n如不添加用户，则不分组不保存", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();

    }

    @OnClick(R.id.do_search)
    public void search() {
        searchSvc(mKeyWordEdit.getText().toString());
    }

    public void searchSvc(String key) {
        if(isDebug){
            LogUtils.e(TAG, key);
        }

        if (TextUtils.isEmpty(key)) {
            return;
        }

        ApiClient.service.searchUser(key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Result>() {
                    Result result = null;
                    ResultListAdapter adapter = null;
                    @Override
                    public void onCompleted() {
                        String str;
                        if (result == null) {
                            str = "未知异常";
                        } else if (result.getCode() == Constant.SUCCESS) {
                            str = "搜索结果";
                            mData.clear();
                            LogUtils.e(TAG, result.getModel().toString());
                            Type type = new TypeToken<ArrayList<User>>() {}.getType();
                            List<User> list = ApiClient.gson.fromJson(result.getModel().toString(), type);
                            if (mHolderUser == null) {
                                mHolderUser = getUser();
                            }

                            for(int i = 0; i < list.size(); i++){
                                if(list.get(i).getId().equals(mHolderUser.getId())){
                                    list.remove(i);
                                }
                            }
                            mData.addAll(list);
                            String[] groups = mGroups.toArray(new String[mGroups.size()]);
                            adapter = new ResultListAdapter(mContext, mData, mHolderUser.getId(), groups);
                            mResultList.setAdapter(adapter);

                            if (mHolderUser == null) {
                                mHolderUser = getUser();
                            }
                            mTip.setText(String.format("搜索到%s个结果", mData.size()));
                        } else if (result.getCode() == Constant.FAIL) {
                            str = "搜索失败";
                            mTip.setText("搜索不到结果，请输入有效手机号");
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

    public void add(String id, String groupName) {
        ApiClient.service.add(id, id, groupName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Result>() {
                    Result result = null;
                    @Override
                    public void onCompleted() {
                        String str;
                        if (result == null) {
                            str = "未知异常";
                        } else if (result.getCode() == Constant.SUCCESS) {
                            str = "成功添加分组";
                        } else if (result.getCode() == Constant.FAIL) {
                            str = result.getModel().toString();
                        } else if (result.getCode() == Constant.EXECUTING) {
                            str = "服务器繁忙";
                        } else {
                            str = "未知异常";
                        }
                        ToastUtils.showShort(mContext, str);
                    }

                    @Override
                    public void onNext(Result result) {
                        this.result = result;
                        LogUtils.e("ResultList", result.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
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
        return true;
    }

    @Override
    public int setLayoutId() {
        return R.layout.activity_personal_addition;
    }
}
