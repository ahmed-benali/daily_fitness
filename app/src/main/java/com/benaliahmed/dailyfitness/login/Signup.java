package com.benaliahmed.dailyfitness.login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.benaliahmed.dailyfitness.R;
import com.google.android.material.textfield.TextInputLayout;

public class Signup extends AppCompatActivity {

    Button nextbtn, loginbtn;
    TextView titletext;
    ImageView backbtn;

    //get data from user variables
    TextInputLayout fullname, email, username, password, confirmpass;
    String fullname_back, email_back, username_back, password_back, gender, phone_code, phone_number, country_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup);

        //hooks for animation
        backbtn = findViewById(R.id.signup_back_btn);
        nextbtn = findViewById(R.id.signup_next_btn);
        loginbtn = findViewById(R.id.signup_log_btn);
        titletext = findViewById(R.id.signup_titletext);

        //hooks for getting data from user
        fullname = findViewById(R.id.Signup_fullname);
        email = findViewById(R.id.Signup_email);
        username = findViewById(R.id.Signup_username);
        password = findViewById(R.id.Signup_password);
        confirmpass = findViewById(R.id.Signup_confirm_password);

        try {
            gender = getIntent().getExtras().getString("gender");
            phone_code = getIntent().getExtras().getString("phone_code");
            phone_number = getIntent().getExtras().getString("phone_number");
            country_name = getIntent().getExtras().getString("country_name");
            fullname_back = getIntent().getExtras().getString("fullname");
            username_back = getIntent().getExtras().getString("username");
            email_back = getIntent().getExtras().getString("email");
            password_back = getIntent().getExtras().getString("password");
            fullname.getEditText().setText(fullname_back);
            username.getEditText().setText(username_back);
            email.getEditText().setText(email_back);
            password.getEditText().setText(password_back);
        } catch (Exception e) {
        }


    }

    public void frame(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void SignupBack(View view) {
        Intent intent_main = new Intent(this, MainActivity.class);
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(findViewById(R.id.signup_back_btn), "transition_signup_btn");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Signup.this, pairs);
            startActivity(intent_main, options.toBundle());
        }
    }

    public void SignupNext(View view) {
        if (!validateFullName() | !validateUserName() | !validateEmail() | !validatePassword() | !validatePasswordConfirm()) {
            return;
        }
        Intent intent_signup2 = new Intent(this, Signup2.class);
        Pair[] pairs = new Pair[4];
        pairs[0] = new Pair<View, String>(backbtn, "transition_signup_backbtn");
        pairs[1] = new Pair<View, String>(nextbtn, "transition_signup_nextbtn");
        pairs[2] = new Pair<View, String>(loginbtn, "transition_signup_logbtn");
        pairs[3] = new Pair<View, String>(titletext, "transition_signup_titletext");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            intent_signup2.putExtra("fullname", fullname.getEditText().getText().toString());
            intent_signup2.putExtra("username", username.getEditText().getText().toString());
            intent_signup2.putExtra("email", email.getEditText().getText().toString());
            intent_signup2.putExtra("password", password.getEditText().getText().toString());
            intent_signup2.putExtra("gender", gender);
            intent_signup2.putExtra("phone_code", phone_code);
            intent_signup2.putExtra("phone_number", phone_number);
            intent_signup2.putExtra("country_name", country_name);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Signup.this, pairs);
            startActivity(intent_signup2, options.toBundle());
        } else {
            startActivity(intent_signup2);
        }
    }

    public void SignupLog(View view) {
        Intent log_intnet = new Intent(this, Login.class);
        startActivity(log_intnet);
    }

    //validation functions
    private boolean validateFullName() {
        String val = fullname.getEditText().getText().toString().trim();
        if (val.isEmpty()) {
            fullname.setError("field can not be empty");
            return false;
        } else {
            fullname.setError(null);
            fullname.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateUserName() {
        String val = username.getEditText().getText().toString().trim();
        String checkspaces = "\\A\\w{4,20}\\z";
        if (val.isEmpty()) {
            username.setError("field can not be empty");
            return false;
        } else if (val.length() > 20) {
            username.setError("Username is too large");
            return false;
        } else if (!val.matches(checkspaces)) {
            username.setError("User name should be 4 characters without spaces!");
            return false;
        } else {
            username.setError(null);
            username.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateEmail() {
        String val = email.getEditText().getText().toString().trim();
        String checkemail = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (val.isEmpty()) {
            email.setError("field can not be empty");
            return false;
        } else if (!val.matches(checkemail)) {
            email.setError("Invalid Email!");
            return false;
        } else {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword() {
        String val = password.getEditText().getText().toString().trim();
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
            password.setError("field can not be empty");
            return false;
        } else if (val.length() > 20) {
            password.setError("Password is too large");
            return false;
        } else if (!val.matches(checkpassword)) {
            password.setError("Password should contain 4 charecters without white spaces");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePasswordConfirm() {
        String val = confirmpass.getEditText().getText().toString();
        String pass = password.getEditText().getText().toString();
        if (!val.equals(pass)) {
            confirmpass.setError("password is not matching");
            return false;
        } else {
            confirmpass.setError(null);
            confirmpass.setErrorEnabled(false);
            return true;
        }
    }
}