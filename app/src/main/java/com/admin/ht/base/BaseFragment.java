package com.admin.ht.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.admin.ht.model.User;
import com.admin.ht.utils.LogUtils;

/**
 * Fragment基类
 *
 * Created by Solstice on 4/15/2017.
 */
public abstract class BaseFragment extends Fragment {

    protected String TAG = getTAG();
    protected abstract String getTAG();
    public abstract boolean setDebug();
    protected boolean isDebug = setDebug();
    protected SharedPreferences mPreferences = null;
    protected User mUser = null;
    private Context mContext = null;


    public boolean putUser(String id, String name, String email, String url, int chatId){
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(Constant.USER_ID, id);
        editor.putString(Constant.USER_HEAD_URL, url);
        editor.putString(Constant.USER_EMAIL, email);
        editor.putString(Constant.USER_NAME, name);
        editor.putInt(Constant.USER_CHAT_ID, chatId);
        return editor.commit();
    }

    public User getUser(){
        String id = mPreferences.getString(Constant.USER_ID,"");
        String name = mPreferences.getString(Constant.USER_NAME,"");
        String url = mPreferences.getString(Constant.USER_HEAD_URL,Constant.USER_DEFAULT_HEAD_URL);
        String email = mPreferences.getString(Constant.USER_EMAIL, "");
        int chatId = mPreferences.getInt(Constant.USER_CHAT_ID, 0);
        User user = new User(id,name, email, url, chatId);
        return user;
    }
    @Override
    public Context getContext(){
        if(mContext == null){
            return BaseApplication.getInstance();
        }
        return mContext;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (isDebug) LogUtils.v(TAG, "onAttach");
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isDebug) LogUtils.v(TAG, "onCreate");
        mPreferences = getActivity().getSharedPreferences(Constant.FILE_NAME, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (isDebug) LogUtils.v(TAG, "onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isDebug) LogUtils.v(TAG, "onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isDebug) LogUtils.v(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isDebug) LogUtils.v(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isDebug) LogUtils.v(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isDebug) LogUtils.v(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isDebug) LogUtils.v(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isDebug) LogUtils.v(TAG, "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (isDebug) LogUtils.v(TAG, "onDetach");
    }
}
