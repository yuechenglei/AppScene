package com.example.wn.myapplication;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by wn on 2015/9/15.
 */
public class ListAdapter extends BaseAdapter{

    List<ListBean> list;
    LayoutInflater inflater;
    Activity context;

    public ListAdapter(List<ListBean> list, Activity context){
        this.list = list;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            convertView = inflater.inflate(R.layout.simple_items, null);
            viewHolder = new ViewHolder();
            viewHolder.content = (TextView) convertView.findViewById(R.id.tv_content);
            viewHolder.title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.choose = (Button) convertView.findViewById(R.id.choose_photo);

            convertView.setTag(viewHolder);
        }

        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.content.setText(list.get(position).content);
        viewHolder.title.setText(list.get(position).title);
        viewHolder.choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpLoadPhoto upLoadPhoto = new UpLoadPhoto(context);
                upLoadPhoto.chooseAlbum();
            }
        });

        return convertView;
    }

    class ViewHolder{
        TextView content;
        TextView title;
        Button choose;
    }
}
