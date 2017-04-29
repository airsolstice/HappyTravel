package com.admin.ht.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.admin.ht.R;
import com.admin.ht.base.Constant;
import com.admin.ht.model.Item;
import com.admin.ht.model.Result;
import com.admin.ht.model.User;
import com.admin.ht.retro.ApiClient;
import com.admin.ht.utils.ImageUtils;
import com.admin.ht.utils.LogUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 好友列表适配器
 *
 * Created by Solstice on 4/22/2017.
 */
public class ExpandAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private LayoutInflater mInflater = null;
    private List<String> mGroups = null;
    private List<List<Item>> mData = null;
    private Map<String, User> mUserData = new HashMap<>();

    public ExpandAdapter(Context context, List<String> mGroups, List<List<Item>> list) {
        mContext = context;
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mGroups = mGroups;
        mData = list;
    }


    @Override
    public int getGroupCount() {
        return mData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mData.get(groupPosition).size();
    }

    @Override
    public List<Item> getGroup(int groupPosition) {
        return mData.get(groupPosition);
    }

    @Override
    public Item getChild(int groupPosition, int childPosition) {
        return mData.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        /* 实现ChildView点击事件，必须返回true */
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_group, null);
        }
        GroupViewHolder holder = new GroupViewHolder();
        holder.mGroupName = (TextView) convertView
                .findViewById(R.id.group_name);
        holder.mGroupName.setText(mGroups.get(groupPosition));
        holder.mGroupCount = (TextView) convertView
                .findViewById(R.id.group_count);
        holder.mGroupCount.setText("[" + mData.get(groupPosition).size() + "]");
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_child, null);
        }
        ChildViewHolder holder = new ChildViewHolder();
        holder.mIcon = (ImageView) convertView.findViewById(R.id.img);
        //设置默认头像
        holder.mChildName = (TextView) convertView.findViewById(R.id.item_name);
        //设置默认用户信息
        holder.mChildName.setText("user" + "(" + getChild(groupPosition, childPosition).getId() + ")");
        holder.mDetail = (TextView) convertView.findViewById(R.id.item_detail);
        holder.mDetail.setText("desc");
        getTargetUserInfo(getChild(groupPosition, childPosition).getId(),holder);

        return convertView;
    }

    /**
     * 获取目标用户信息
     *
     * @param id
     * @param holder
     */
    public void getTargetUserInfo(String id, final ChildViewHolder holder) {
        ApiClient.service.getUserInfo(id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Result>() {
                    Result result = null;
                    @Override
                    public void onCompleted() {
                        String str;
                        if (result == null) {
                            str = "未知异常";
                        } else if (result.getCode() == Constant.SUCCESS) {
                            str = "获取群组列表";
                            User user = ApiClient.gson.fromJson(result.getModel().toString().trim(), User.class);
                            mUserData.put(user.getId(), user);
                            if(user == null){
                                return;
                            }
                            int rw = holder.mIcon.getMeasuredWidth();
                            int rh = holder.mIcon.getMeasuredHeight();
                            ImageSize targetSize = new ImageSize(rw, rh); // result Bitmap will be fit to this size
                            ImageLoader.getInstance().loadImage(user.getUrl(), targetSize, ImageUtils.getDisplayImageOptions(), new SimpleImageLoadingListener() {
                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    holder.mIcon.setImageBitmap(loadedImage);
                                }
                            });
                            holder.mChildName.setText(user.getName() + "(" + user.getId() + ")");
                            holder.mDetail.setText(user.getNote());
                        } else if (result.getCode() == Constant.FAIL) {
                            str = "更新失败";
                        } else if (result.getCode() == Constant.EXECUTING) {
                            str = "服务器繁忙";
                        } else {
                            str = "未知异常";
                        }
                        LogUtils.d("Adapter", str);
                    }

                    @Override
                    public void onNext(Result result) {
                        this.result = result;
                        LogUtils.e("Adapter", result.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });

    }

    private Drawable getRoundCornerDrawable(int resId, float roundPX /* 圆角的半径 */) {
        Drawable drawable = mContext.getResources().getDrawable(resId);
        int w = mContext.getResources().getDimensionPixelSize(R.dimen.image_width);
        int h = w;

        Bitmap bitmap = Bitmap
                .createBitmap(w, h,
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap retBmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas can = new Canvas(retBmp);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        paint.setColor(color);
        paint.setAntiAlias(true);
        can.drawARGB(0, 0, 0, 0);
        can.drawRoundRect(rectF, roundPX, roundPX, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        can.drawBitmap(bitmap, rect, rect, paint);
        return new BitmapDrawable(retBmp);
    }

    public Map<String, User> getUserData(){
        return mUserData;
    }

    public void setData(List<List<Item>> list) {
        mData = list;
    }


    private class GroupViewHolder {
        TextView mGroupName;
        TextView mGroupCount;
    }

    private class ChildViewHolder {
        ImageView mIcon;
        TextView mChildName;
        TextView mDetail;
    }

}