package com.example.skilja.connectme.activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.example.skilja.connectme.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Lenovo on 6/11/2018.
 */

public class UserDialog extends DialogFragment {





    private ArrayList<User> korisnici;
    private ArrayList<String> korisnici3;

    private ArrayList korisnici2;
    public ArrayList<User> temp;

    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
    private FirebaseAuth firebaseAuth;




    public interface DialogUserCall {

        public void onDialogPositiveClick(DialogFragment dialog, ArrayList<User> lista);
        public void onDialogNegativeClick (DialogFragment dialog);

    }

    DialogUserCall DListener;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        korisnici = new ArrayList<>();
       korisnici2 = new ArrayList<>();
        temp = new ArrayList<>();


       /* ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String email = getCurrentUserEmail();

                for (DataSnapshot u : dataSnapshot.getChildren()) {

                    User user = u.getValue(User.class);

                   // if (!user.getEmail().equalsIgnoreCase(email)) {

                            korisnici.add(user);


                    //}

                    //  temp.add(user);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/






        String [] arr = new String[korisnici.size()];
        String [] pera = new String[10];
        pera[0] = "jedan";
        pera[1] = "dva";
        pera[2] = "tri";
        pera[3] = "jedan";
        pera[4] = "dva";
        pera[5] = "tri";
        pera[6] = "jedan";
        pera[7] = "dva";
        pera[8] = "tri";
        pera[9] = "tri";

        for(int i = 0; i < korisnici.size(); i++) {

            arr[i] = korisnici.get(i).getFirst_name();

        }

        AlertDialog.Builder builder  = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose users")
        .setMultiChoiceItems(pera, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {

                if (b) {

                    korisnici2.add(i);
                    temp.add(korisnici.get(i));

                }
                else if (korisnici2.contains(i)) {

                        korisnici2.remove(Integer.valueOf(i));
                        temp.remove(korisnici.get(i));

                }

            }
        })
           .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialogInterface, int i) {

                           DListener.onDialogPositiveClick(UserDialog.this, temp);

                       }
                   }


           )

            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                        DListener.onDialogNegativeClick(UserDialog.this);

                }
            });


            return builder.create();


    }


    @Override
    public  void onAttach(Activity activity) {

            super.onAttach(activity);

            try {
                DListener = (DialogUserCall) activity;
            }
            catch(ClassCastException e) {

                    throw new ClassCastException(activity.toString()+ "must implement DialogUserCall");

            }


    }


    public String getCurrentUserEmail() {

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser fireBaseUser = (FirebaseUser) firebaseAuth.getCurrentUser();
        String email = fireBaseUser.getEmail();
        return email;

    }

    public ArrayList<User> getTemp() {
        return temp;
    }

    public void setTemp(ArrayList<User> temp) {
        this.temp = temp;
    }
}
