package com.example.skilja.connectme.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.skilja.connectme.R;

import java.util.List;



public class DeletedGroupAdapter extends ArrayAdapter<DeleteGroup> {
    public DeletedGroupAdapter(Context context, List<DeleteGroup> deleteGroups) {
        super(context, 0, deleteGroups);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DeleteGroup deleteGroup = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_deleted_group, parent, false);
        }
        TextView groupName = convertView.findViewById(R.id.groupName);
        ImageView imageView =  convertView.findViewById(R.id.groupIcon);
        char letter = deleteGroup.getTitle().toCharArray()[0];
        TextDrawable drawable = TextDrawable.builder().buildRound(String.valueOf(letter).toUpperCase(), deleteGroup.getColor());
        groupName.setText(deleteGroup.getTitle());
        imageView.setImageDrawable(drawable);

        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
