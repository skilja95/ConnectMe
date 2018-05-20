package com.example.skilja.connectme.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.skilja.connectme.R;


public class MessagesViewHolder extends RecyclerView.ViewHolder {
    public TextView messageText;
    public TextView sender;

    public TextView messageTextLeft;
    public TextView senderLeft;


    public MessagesViewHolder(View itemView) {
        super(itemView);

        messageText = (TextView) itemView.findViewById(R.id.message);
        sender = (TextView) itemView.findViewById(R.id.txtSender);

        messageTextLeft = (TextView) itemView.findViewById(R.id.messageLeft);
        senderLeft = (TextView) itemView.findViewById(R.id.txtSenderLeft);
    }
}