package com.example.skilja.connectme.model;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.skilja.connectme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;



public class CustomAdapter extends BaseAdapter {

    Context context;
    List<Group> rowItems;


    //PRISTUPANJE BAZI
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("deletedGroup");


    public CustomAdapter(Context context, List<Group> rowItems) {
        this.context = context;
        this.rowItems = rowItems;
    }

    private class ViewHolder {
        TextView groupTitle;
        ImageView profile_pic;
        ImageButton deleteMessage;
    }

    @Override
    public int getCount() {
        return rowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowItems.indexOf(getItem(position));
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        holder = new ViewHolder();
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_conversations, null);

            holder.groupTitle = convertView.findViewById(R.id.conversation_title);
            holder.profile_pic =  convertView.findViewById(R.id.profile_pic);
            holder.deleteMessage = convertView.findViewById(R.id.deleteImageButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Group row_pos = rowItems.get(position);
        char letter = row_pos.getTitle().toCharArray()[0];
        holder.groupTitle.setText(row_pos.getTitle());


        TextDrawable drawable = TextDrawable.builder().buildRound(String.valueOf(letter).toUpperCase(), row_pos.getColor());
        holder.profile_pic.setImageDrawable(drawable);


        holder.deleteMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        return convertView;
    }



}
