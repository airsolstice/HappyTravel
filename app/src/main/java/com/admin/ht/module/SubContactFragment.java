package com.admin.ht.module;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.admin.ht.R;
import com.admin.ht.adapter.GroupViewAdapter;
import com.admin.ht.model.Group;
import com.admin.ht.model.GroupItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Spec_Inc on 3/4/2017.
 */

public class SubContactFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.layout_contact, null);
        ListView list = (ListView) v.findViewById(R.id.fri_list);
        GroupViewAdapter adapter = new GroupViewAdapter(getContext(), getGroupData());
        list.setAdapter(adapter);
        getGroupData();

        return v;
    }

    private List<Group> getGroupData() {

        List<Group> groups = new ArrayList<>();
        List<GroupItem> items1 = new ArrayList<>();

        String url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=" +
                "1488723551780&di=9ac9726620e9f72d6e473ab97e847be0&imgtype=0" +
                "&src=http%3A%2F%2Fimg.qqai.net%2Fuploads%2Fi_1_3783854548x2374077175_21.jpg";

        for (int i = 0; i < 5; i++) {
            GroupItem item = new GroupItem("100" + i, "friend" + i, url, 1, "note" + i);
            items1.add(item);
        }
        Group group1 = new Group("好友", items1);
        groups.add(group1);

        List<GroupItem> items2 = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            GroupItem item = new GroupItem("100" + i, "family" + i, url, 1, "note" + i);
            items2.add(item);
        }
        Group group2 = new Group("家人", items2);
        groups.add(group2);


        List<GroupItem> items3 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            GroupItem item = new GroupItem("100" + i, "workmate" + i, url, 1, "note" + i);
            items3.add(item);
        }
        Group group3 = new Group("同事", items3);
        groups.add(group3);

        return groups;

    }
}


