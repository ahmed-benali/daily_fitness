package com.benaliahmed.dailyfitness.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.benaliahmed.dailyfitness.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

public class ForgotPassword extends AppCompatActivity {

    TextInputLayout et_phone;
    CountryCodePicker code_picker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forgot_password);

        et_phone= findViewById(R.id.forgot_phone);
        code_picker= findViewById(R.id.country_code_picker);
    }

    public void ForgotpassBack(View view) {
        Intent login_intent= new Intent(this, Login.class);
        startActivity(login_intent);
    }

    public void frame(View view){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void ForgotPassNext(View view) {
        final String phone_number = "+" + code_picker.getFullNumber().trim() + et_phone.getEditText().getText().toString().trim();
        final Intent forgotpass2_intent= new Intent(this, ForgotPassword2.class);

        //Database
        Query checkuser = FirebaseDatabase.getInstance().getReference("users").orderByChild("phone").equalTo(phone_number);
        checkuser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String system_user = snapshot.child(phone_number).child("username").getValue(String.class);
                if (snapshot.exists()) {
                    et_phone.setError(null);
                    et_phone.setErrorEnabled(false);
                    forgotpass2_intent.putExtra("phone",phone_number);
                    startActivity(forgotpass2_intent);

                } else {
                    //Toast.makeText(ForgotPassword.this, "phone number not registered", Toast.LENGTH_SHORT).show();
                    et_phone.setError("phone number not registered");
                    et_phone.setErrorEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ForgotPassword.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    }
