package com.admin.ht.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.admin.ht.R;
import com.admin.ht.model.ChatLog;
import com.admin.ht.utils.ImageUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

/**
 *  简洁聊天记录列表适配器
 *  实现用户和好友记录分左右两边（有bug）
 *
 * Created by Solstice on 4/26/2017.
 */
public class SimpleChatLogAdapter extends BaseAdapter {

    private List<ChatLog> mData;

    private LayoutInflater mInflater;

    public SimpleChatLogAdapter(Context context, List<ChatLog> mData) {
        this.mData = mData;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).getType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;
        ChatLog log = mData.get(position);
        int type = log.getType();
        if (type == 1) {
            if (view == null) {
                view = mInflater.inflate(R.layout.item_chat_right, null);
            }
        } else if (type == 2) {
            if (view == null) {
                view = mInflater.inflate(R.layout.item_chat_left, null);
            }
        }
        TextView name = (TextView) view.findViewById(R.id.name);
        name.setText(log.getName());
        TextView content = (TextView) view.findViewById(R.id.content);
        content.setText(log.getContent());
        final ImageView icon = (ImageView) view.findViewById(R.id.img);

        int rw = icon.getMeasuredWidth();
        int rh = icon.getMeasuredHeight();
        ImageSize targetSize = new ImageSize(rw, rh);
        // result Bitmap will be fit to this size
        ImageLoader.getInstance().loadImage(log.getUrl(), targetSize, ImageUtils.getDisplayImageOptions(),
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        icon.setImageBitmap(loadedImage);
                    }
                });
        return view;
    }
}
