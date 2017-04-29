package com.admin.ht.module;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.admin.ht.R;

/**
 * 联系人碎片类
 * <p>
 * Created by Solstice on 3/12/2017.
 */
public class ContactFragment extends Fragment {

    private Fragment mSubContact = new SubContactFragment();
    private Fragment mGroup = new GroupFragment();
    private Fragment mMessage = new MessageFragment();

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_contact, null);
        TextView friBtn = (TextView) v.findViewById(R.id.fri);
        TextView groupBtn = (TextView) v.findViewById(R.id.group);
        TextView msgBtn = (TextView) v.findViewById(R.id.msg);
        friBtn.setOnClickListener(mListener);
        groupBtn.setOnClickListener(mListener);
        msgBtn.setOnClickListener(mListener);

        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment, mSubContact);
        ft.commit();
        return v;
    }

    View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentManager fm = getChildFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            switch (v.getId()) {
                case R.id.fri:
                    ft.replace(R.id.fragment, mSubContact);
                    break;

                case R.id.group:
                    ft.replace(R.id.fragment, mGroup);
                    break;

                case R.id.msg:
                    ft.replace(R.id.fragment, mMessage);
                    break;
            }
            ft.commit();
        }
    };
}


