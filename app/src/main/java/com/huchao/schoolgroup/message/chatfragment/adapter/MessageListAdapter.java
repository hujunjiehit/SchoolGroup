package com.huchao.schoolgroup.message.chatfragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yuntongxun.ecsdk.im.ECGroup;
import com.yuntongxun.schoolgroup.R;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by junjie on 2016/9/27.
 */

public class MessageListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<ECGroup> mGroups;

    public MessageListAdapter(Context context, List<ECGroup> groups) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mGroups = groups;
    }

    @Override
    public int getCount() {
        return mGroups.size();
    }

    @Override
    public Object getItem(int position) {
        return mGroups.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_group_list,null);
            viewHolder.tvGroupName = (TextView) convertView.findViewById(R.id.tv_group_name);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        ECGroup group = mGroups.get(position);
        viewHolder.tvGroupName.setText("Name:"+ group.getName() + "  owner:" + group.getOwner());
        return convertView;
    }

    public final class ViewHolder{
        TextView tvGroupName;
    }
}
