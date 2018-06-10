package com.example.skilja.connectme.model;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.skilja.connectme.R;

import java.util.List;

/**
 * Created by Lenovo on 6/10/2018.
 */

public class DeleteAdapter  extends BaseAdapter {

    Context context;
    List<DeleteGroup> list;


    public DeleteAdapter(Context context, List<DeleteGroup> list) {
        this.context = context;
        this.list = list;
    }

    private class ViewHolder {

        TextView groupTitle;
        ImageView profilePic;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }



    @Override
    public long getItemId(int i) {
        return list.indexOf(getItem(i));
    }



    @Override
    public View getView(final int i, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        holder = new ViewHolder();

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_deleted_group, null);

            holder.groupTitle = convertView.findViewById(R.id.groupName);
            holder.profilePic = convertView.findViewById(R.id.groupIcon);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DeleteGroup gr = list.get(i);
        char letter = gr.getTitle().toCharArray()[0];
        holder.groupTitle.setText(gr.getTitle());

        TextDrawable drawable = TextDrawable.builder().buildRound(String.valueOf(letter).toUpperCase(), gr.getColor());
        holder.profilePic.setImageDrawable(drawable);




        return convertView;
    }
}
