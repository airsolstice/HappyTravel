package com.admin.ht.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


/**
 * Created by Holy-Spirit on 2015/12/29.
 */
public abstract  class BaseAdapter<T> extends android.widget.BaseAdapter
{

    protected Context mContext;
    protected List<T> mData;
    protected LayoutInflater mInflater;

    public BaseAdapter(Context context, List<T> datas){
        this.mContext = context;
        this.mData = datas;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount()
    {
        return mData.size();
    }

    @Override
    public T getItem(int position)
    {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }


    @Override
    public  View getView(int position, View convertView, ViewGroup parent){

        int layoutId = getLayoutId();

        ViewHolder mHolder = ViewHolder.get(convertView,mContext,layoutId,parent,position);

        convert(mHolder,getItem(position));

        return mHolder.getConvertView();
    }


    public abstract void convert(ViewHolder holder,T t);

    public abstract int getLayoutId();

}
