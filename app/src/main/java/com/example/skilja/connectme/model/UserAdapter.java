package com.example.skilja.connectme.model;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.skilja.connectme.R;


import java.util.List;

public class UserAdapter extends ArrayAdapter<User>{

    private Context context;
    private List<User> users;

    public UserAdapter (Context context, int resourceId, List<User> users) {
        super(context, resourceId, users);
        this.context = context;
        this.users = users;
    }

    private class  ViewHolder {
        TextView result;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Nullable
    @Override
    public User getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return users.indexOf(users.get(position));
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        holder = new ViewHolder();
        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.list_item, null);
            holder.result = (TextView) convertView.findViewById(R.id.groupTitle);
            holder.result.setText(getItem(position).getFirst_name());
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        User row_pos =users.get(position);
        String email = row_pos.getEmail();
        Log.d("UserAdapter", "email: "+email);

        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //return super.getDropDownView(position, convertView, parent);
        if(convertView == null)
            return super.getView(position,convertView,parent);
        TextView textView = (TextView) convertView.findViewById(R.id.groupTitle);
        textView.setText(getItem(position).getFirst_name());
        return  convertView;
    }
}
