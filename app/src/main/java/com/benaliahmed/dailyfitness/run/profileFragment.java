package com.benaliahmed.dailyfitness.run;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.benaliahmed.dailyfitness.R;
import com.benaliahmed.dailyfitness.database.SessionManager;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class profileFragment extends Fragment {
    TextInputLayout fullname_tv, username_tv, phone_tv, gender_tv,date_tv,email_tv,age_tv,weight_tv,height_tv;
    CardView save_btn,more_btn;

    @Override
    public void onStart() {
        super.onStart();

        fullname_tv = getView().findViewById(R.id.fullname);
        username_tv = getView().findViewById(R.id.username);
        phone_tv= getView().findViewById(R.id.phone);
        gender_tv = getView().findViewById(R.id.gender);
        date_tv = getView().findViewById(R.id.date);
        email_tv = getView().findViewById(R.id.email);
        weight_tv = getView().findViewById(R.id.weight);
        age_tv = getView().findViewById(R.id.age);
        height_tv = getView().findViewById(R.id.height);

        final SessionManager sessionManagerStats = new SessionManager(getContext(), SessionManager.SESSION_STATS);
        HashMap<String, String> stats_data = sessionManagerStats.getStatsFromSession();
        age_tv.getEditText().setText(stats_data.get(SessionManager.KEY_AGE));
        weight_tv.getEditText().setText(stats_data.get(SessionManager.KEY_WEIGHT));
        height_tv.getEditText().setText(stats_data.get(SessionManager.KEY_HEIGHT));

        fullname_tv.setEnabled(false);
        username_tv.setEnabled(false);
        phone_tv.setEnabled(false);
        email_tv.setEnabled(false);
        gender_tv.setEnabled(false);
        date_tv.setEnabled(false);

        save_btn=getView().findViewById(R.id.save_btn);
        more_btn=getView().findViewById(R.id.more_btn);

        more_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("This app is created by Ahmed BenAli \n date:2020 \n Developed with android studio as a personal project")
                            .setCancelable(false)
                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.show();
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validateAge() || !validateHeight() || !validateWeight()){
                    return;
                }
                sessionManagerStats.createStatsSession(age_tv.getEditText().getText().toString(),weight_tv.getEditText().getText().toString(),height_tv.getEditText().getText().toString());
                Toast.makeText(getContext(), "saved", Toast.LENGTH_SHORT).show();
            }
        });


        //get phone number from session
        SessionManager sessionManagerPhone = new SessionManager(getContext(), SessionManager.SESSION_REMEMBERME);
        HashMap<String, String> remember_data = sessionManagerPhone.getRemembermeDetailFromSession();
        final String password = remember_data.get(sessionManagerPhone.KEY_SESSION_PASSWORD);
        final String phone = remember_data.get(sessionManagerPhone.KEY_SESSION_PHONE);
        String code_picker = remember_data.get(sessionManagerPhone.KEY_SESSION_COUNTRYCODE);
        String code_number= remember_data.get(sessionManagerPhone.KEY_SESSION_COUNTRYCODENUMBER);

        final String phone_number = "+" + code_number + phone;

        //Database
        Query checkuser = FirebaseDatabase.getInstance().getReference("users").orderByChild("phone").equalTo(phone_number);
        checkuser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String system_user = snapshot.child(phone_number).child("username").getValue(String.class);
                if (snapshot.exists()) {

                    String system_password = snapshot.child(phone_number).child("password").getValue(String.class);
                    if (system_password.equals(password)) {
                        //ToDo
                        String full_name = snapshot.child(phone_number).child("fullname").getValue(String.class);
                        String phone = snapshot.child(phone_number).child("phone").getValue(String.class);
                        String user_name = snapshot.child(phone_number).child("username").getValue(String.class);
                        String email = snapshot.child(phone_number).child("email").getValue(String.class);
                        String password = snapshot.child(phone_number).child("password").getValue(String.class);
                        String gender = snapshot.child(phone_number).child("gender").getValue(String.class);
                        String date = snapshot.child(phone_number).child("date").getValue(String.class);

                        fullname_tv.getEditText().setText(full_name);
                        username_tv.getEditText().setText(full_name);
                        email_tv.getEditText().setText(email);
                        gender_tv.getEditText().setText(gender);
                        date_tv.getEditText().setText(date);
                        phone_tv.getEditText().setText(phone);

                    } else {
                        Toast.makeText(getContext(), "wrong password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "phone number not registered", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    private boolean validateAge() {
        int userage = 0;
        try {
            userage = Integer.parseInt(age_tv.getEditText().getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (userage<8) {
            age_tv.setError("Age is not eligible");
            return false;
        } else {
            age_tv.setError(null);
            age_tv.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateHeight() {
        int userheight = 0;
        try {
            userheight = Integer.parseInt(height_tv.getEditText().getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (userheight==0) {
            height_tv.setError("insert valid height");
            return false;
        } else {
            height_tv.setError(null);
            height_tv.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateWeight() {
        int userweight = 0;
        try {
            userweight = Integer.parseInt(weight_tv.getEditText().getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (userweight==0) {
            weight_tv.setError("insert valid weight");
            return false;
        } else {
            weight_tv.setError(null);
            weight_tv.setErrorEnabled(false);
            return true;
        }
    }

}