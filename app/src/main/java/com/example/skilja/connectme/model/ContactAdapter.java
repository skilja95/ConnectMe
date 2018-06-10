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
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.skilja.connectme.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

/**
 * Created by Lenovo on 6/10/2018.
 */

public class ContactAdapter extends BaseAdapter {




    Context context;
    List<String> users;


    //PRISTUPANJE BAZI
    private FirebaseAuth firebaseAuth;


    public ContactAdapter(Context context, List<String> users) {
        this.context = context;
        this.users = users;
    }

    private class ViewHolder {
        TextView groupTitle;
        ImageView profile_pic;

    }




    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int i) {
        return users.get(i);
    }

    @Override
    public long getItemId(int i) {
        return users.indexOf(getItem(i));
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        holder = new ViewHolder();

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_deleted_group, null);

            holder.groupTitle = convertView.findViewById(R.id.groupName);
            holder.profile_pic =  convertView.findViewById(R.id.groupIcon);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //User us = users.get(position);
        String us = users.get(position);
       // holder.groupTitle.setText(us.getFirst_name()+ " "+ us.getLast_name());
        holder.groupTitle.setText(us);
        //char letter = us.getFirst_name().toCharArray()[0];
        char letter = us.toCharArray()[0];

        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color1 = generator.getRandomColor();
        while (color1 == 0) {
            color1 = generator.getRandomColor();
        }

        TextDrawable drawable = TextDrawable.builder().buildRound(String.valueOf(letter).toUpperCase(), color1);
        holder.profile_pic.setImageDrawable(drawable);


        return convertView;
    }

}
