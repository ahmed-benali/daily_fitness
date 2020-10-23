package com.benaliahmed.dailyfitness.login;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.benaliahmed.dailyfitness.R;
import com.benaliahmed.dailyfitness.database.SessionManager;
import com.benaliahmed.dailyfitness.run.MainMenu;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    TextInputLayout phone_input, password_input;
    CountryCodePicker code_picker;
    CheckBox remember_me;
    RelativeLayout progressbar;

    public static final int DEFAULT_UPDATE_INTERVAL = 3;
    public static final int FAST_UPDATE_INTERVAL = 5;
    private static final int PERMISSION_FINE_LOCATION = 99;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        phone_input = findViewById(R.id.login_phone);
        password_input = findViewById(R.id.login_password);
        code_picker = findViewById(R.id.country_code_picker);
        remember_me = findViewById(R.id.login_checkbox);
        progressbar= findViewById(R.id.progress_bar);

        //check remember me
        SessionManager sessionManager = new SessionManager(Login.this, SessionManager.SESSION_REMEMBERME);
        if (sessionManager.checkRememberme()) {
            HashMap<String, String> remember_data = sessionManager.getRemembermeDetailFromSession();
            password_input.getEditText().setText(remember_data.get(SessionManager.KEY_SESSION_PASSWORD));
            phone_input.getEditText().setText(remember_data.get(SessionManager.KEY_SESSION_PHONE));
            code_picker.setCountryForNameCode(remember_data.get(SessionManager.KEY_SESSION_COUNTRYCODE));
            remember_me.setChecked(true);
        }
    }

    public void loginBack(View view) {
        Intent intent_main = new Intent(this, MainActivity.class);
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(findViewById(R.id.login_back_btn), "transition_login_btn");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login.this, pairs);
            startActivity(intent_main, options.toBundle());
        }
    }

    public void LoginCreateaccount(View view) {
        Intent createaccount_intnet = new Intent(this, Signup.class);
        startActivity(createaccount_intnet);
    }

    public void LogingLog(View view) {
        progressbar.setVisibility(View.VISIBLE);
        if (!validatePassword()) {
            return;
        }
        final String phone_number, password, phone, code, code_number;
        phone = phone_input.getEditText().getText().toString().trim();
        phone_number = "+" + code_picker.getFullNumber().trim() + phone;
        password = password_input.getEditText().getText().toString();
        code = code_picker.getSelectedCountryNameCode();
        code_number = code_picker.getSelectedCountryCode();

        //remember me
        if (remember_me.isChecked()) {
            SessionManager sessionManager = new SessionManager(Login.this, SessionManager.SESSION_REMEMBERME);
            sessionManager.createremembermeSession(true, phone, password, code, code_number);
        } else {
            SessionManager sessionManager = new SessionManager(Login.this, SessionManager.SESSION_REMEMBERME);
            sessionManager.createremembermeSession(false, null, null, null, null);
        }


        //Database
        Query checkuser = FirebaseDatabase.getInstance().getReference("users").orderByChild("phone").equalTo(phone_number);
        checkuser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String system_user = snapshot.child(phone_number).child("username").getValue(String.class);
                if (snapshot.exists()) {
                    phone_input.setError(null);
                    phone_input.setErrorEnabled(false);
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

                        //create session
                        SessionManager sessionManager = new SessionManager(Login.this, SessionManager.SESSION_USERSESSION);
                        sessionManager.createLoginSession(full_name, email, date, gender, user_name, phone, password);
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            progressbar.setVisibility(View.GONE);
                            startActivity(new Intent(getApplicationContext(), MainMenu.class));
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
                            }
                        }

                    } else {
                        progressbar.setVisibility(View.GONE);
                        Toast.makeText(Login.this, "wrong password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressbar.setVisibility(View.GONE);
                    Toast.makeText(Login.this, "phone number not registered", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void loginForgotpass(View view) {
        Intent forgotpass_intent = new Intent(this, ForgotPassword.class);
        startActivity(forgotpass_intent);
    }

    public void frame(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private boolean validatePassword() {
        String val = password_input.getEditText().getText().toString().trim();
        String checkpassword =
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                        // "(?=.*[@#$%^&+=])" +    //at least 1 special character
                        "(?=\\S+$)" +           //no white spaces
                        ".{4,}" +               //at least 4 characters
                        "$";
        ;
        if (val.isEmpty()) {
            password_input.setError("field can not be empty");
            return false;
        } else if (val.length() > 20) {
            password_input.setError("invalid password");
            return false;
        } else if (!val.matches(checkpassword)) {
            password_input.setError("invalid password");
            return false;
        } else {
            password_input.setError(null);
            password_input.setErrorEnabled(false);
            return true;
        }
    }
}