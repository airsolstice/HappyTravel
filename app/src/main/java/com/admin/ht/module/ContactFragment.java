package com.admin.ht.module;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.admin.ht.R;
import com.admin.ht.base.Constant;
import com.admin.ht.db.RecentMsgHelper;
import com.jauker.widget.BadgeView;

/**
 * 联系人碎片类
 * <p>
 * Created by Solstice on 3/12/2017.
 */
public class ContactFragment extends Fragment {

    public Fragment mSubContact = null;
    private Fragment mGroup = null;
    private Fragment mMessage = null;
    public BadgeView mBadgeView = null;
    private int msgCount = 0;
    TextView friBtn = null;
    TextView groupBtn = null;
    TextView msgBtn = null;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case Constant.NEW_MDG:
                    msgCount++;
                    mBadgeView.setBadgeCount(msgCount);
                    break;

                case Constant.CHANGE_FOCUS:

                    break;
            }

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mSubContact = new SubContactFragment(mHandler);
        mGroup = new GroupFragment(mHandler);
        mMessage = new MessageFragment(mHandler);

        View v = inflater.inflate(R.layout.fragment_contact, null);
        friBtn = (TextView) v.findViewById(R.id.fri);
        groupBtn = (TextView) v.findViewById(R.id.group);
        msgBtn = (TextView) v.findViewById(R.id.msg);
        mBadgeView = new BadgeView(getContext());
        mBadgeView.setTargetView(msgBtn);
        mBadgeView.setGravity(Gravity.RIGHT);

        friBtn.setOnClickListener(mListener);
        groupBtn.setOnClickListener(mListener);
        msgBtn.setOnClickListener(mListener);

        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment, mSubContact);
        friBtn.setTextColor(Color.parseColor("#0080E0"));
        ft.commit();
        return v;
    }

    View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentManager fm = getChildFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Drawable friDr;
            Drawable groupDr;
            Drawable msgDr;

            switch (v.getId()) {
                case R.id.fri:
                    ft.replace(R.id.fragment, mSubContact);
//                    Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_fri_96);
//                    friDr = new BitmapDrawable(getResources(), bm);
//
//                    friBtn.setCompoundDrawables(null, friDr, null, null);
                    friBtn.setTextColor(Color.parseColor("#0080E0"));

//                    groupDr = ContextCompat.getDrawable(getContext(), R.mipmap.ic_group_gray_96);
//                    groupBtn.setCompoundDrawables(null, groupDr, null, null);
                    groupBtn.setTextColor(Color.parseColor("#bfbfbf"));

//                    msgDr = ContextCompat.getDrawable(getContext(), R.mipmap.ic_msg_gray_96);
//                    msgBtn.setCompoundDrawables(null, msgDr, null, null);
                    msgBtn.setTextColor(Color.parseColor("#bfbfbf"));

                    if (msgCount > 0) {
                        mBadgeView.setVisibility(View.VISIBLE);
                    } else {
                        mBadgeView.setVisibility(View.GONE);
                    }
                    break;

                case R.id.group:
                    ft.replace(R.id.fragment, mGroup);
//                    friDr = ContextCompat.getDrawable(getContext()
//                            .getApplicationContext(), R.mipmap.ic_fri_gray_96);
//                    friBtn.setCompoundDrawables(null, friDr, null, null);
                    friBtn.setTextColor(Color.parseColor("#bfbfbf"));

//                    groupDr = ContextCompat.getDrawable(getContext()
//                            .getApplicationContext(), R.mipmap.ic_group_96);
//                    groupBtn.setCompoundDrawables(null, groupDr, null, null);
                    groupBtn.setTextColor(Color.parseColor("#0080E0"));

//                    msgDr = ContextCompat.getDrawable(getContext()
//                            .getApplicationContext(), R.mipmap.ic_msg_gray_96);
//                    msgBtn.setCompoundDrawables(null, msgDr, null, null);
                    msgBtn.setTextColor(Color.parseColor("#bfbfbf"));

                    if (msgCount > 0) {
                        mBadgeView.setVisibility(View.VISIBLE);
                    } else {
                        mBadgeView.setVisibility(View.GONE);
                    }

                    break;

                case R.id.msg:
                    ft.replace(R.id.fragment, mMessage);
//                    friDr = ContextCompat.getDrawable(getContext()
//                            .getApplicationContext(), R.mipmap.ic_fri_gray_96);
//                    friBtn.setCompoundDrawables(null, friDr, null, null);
                    friBtn.setTextColor(Color.parseColor("#bfbfbf"));

//                    groupDr = ContextCompat.getDrawable(getContext()
//                            .getApplicationContext(), R.mipmap.ic_group_gray_96);
//                    groupBtn.setCompoundDrawables(null, groupDr, null, null);
                    groupBtn.setTextColor(Color.parseColor("#bfbfbf"));

//                    msgDr = ContextCompat.getDrawable(getContext()
//                            .getApplicationContext(), R.mipmap.ic_msg_96);
//                    msgBtn.setCompoundDrawables(null, msgDr, null, null);
                    msgBtn.setTextColor(Color.parseColor("#0080E0"));

                    mBadgeView.setVisibility(View.GONE);
                    msgCount = 0;
                    break;
            }
            ft.commit();
        }
    };
}


