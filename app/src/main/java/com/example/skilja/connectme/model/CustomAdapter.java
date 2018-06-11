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
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.skilja.connectme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;



public class CustomAdapter extends BaseAdapter {

    Context context;
    List<Group> rowItems;


    private ArrayList<String> deletedGroups = new ArrayList<>();

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


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String email = getCurrentUserEmail();
                for (DataSnapshot u : dataSnapshot.getChildren()) {
                    DeleteGroup deleteGroup = u.getValue(DeleteGroup.class);
                    // if (deleteGroup.getEmailUser().equals(email)) {
                    deletedGroups.add(deleteGroup.getGroupId());
                    //}
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        holder.deleteMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                //ovo je lukin kod
                // boolean pera = false;
                String uid = databaseReference.push().getKey();
                String emailUser = getCurrentUserEmail();
                Group grupa = rowItems.get(position);
                String idGrupa = grupa.getId_group();
                String titleGrupa = grupa.getTitle();

                ColorGenerator generator = ColorGenerator.MATERIAL;
                int color1 = generator.getRandomColor();
                while (color1 == 0) {
                    color1 = generator.getRandomColor();
                }


              /*  for (String kljuc : deletedGroups) {

                        if (idGrupa.equals(idGrupa)) {

                          //  Toast.makeText(context, "message has been already deleted by another user", Toast.LENGTH_LONG).show();
                            pera = true;

                            break;

                        }


                }*/

               /*     if (pera == true) {
                        Toast.makeText(context, "message has been already deleted by another user", Toast.LENGTH_LONG).show();


                    } else {*/
                //String uid = databaseReference.push().getKey();
                DeleteGroup delGroup = new DeleteGroup(uid, idGrupa, emailUser, true, titleGrupa, color1);
                databaseReference.child(uid).setValue(delGroup);
                // Toast.makeText(ConversationPageActivity.this, "Poruka obrisana", Toast.LENGTH_LONG).show();
                Toast.makeText(context, "message deleted", Toast.LENGTH_LONG).show();

                notifyDataSetChanged();



            }
        });
        return convertView;
    }

    public String getCurrentUserEmail() {


        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser fireBaseUser = firebaseAuth.getCurrentUser();

        String email = fireBaseUser.getEmail();

        return email;

    }

}
