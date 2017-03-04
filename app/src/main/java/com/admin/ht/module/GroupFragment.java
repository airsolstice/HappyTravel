package com.admin.ht.module;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.admin.ht.R;

/**
 * Created by Spec_Inc on 3/4/2017.
 */

public class GroupFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.layout_group,null);
        return v;
    }

}
