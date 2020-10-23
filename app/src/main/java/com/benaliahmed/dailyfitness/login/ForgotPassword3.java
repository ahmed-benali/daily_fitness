package com.benaliahmed.dailyfitness.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.benaliahmed.dailyfitness.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ForgotPassword3 extends AppCompatActivity {
    TextInputLayout password_input, confirm_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forgot_password3);
        password_input =findViewById(R.id.new_pass);
        confirm_input =findViewById(R.id.confirm_pass);
    }
    public void frame(View view){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public void Forgotpass3Back(View view) {
        Intent forgotpass_intent= new Intent(this, ForgotPassword.class);
        startActivity(forgotpass_intent);
    }

    public void Forgotpass3Ok(View view) {
        Intent forgotpass4_intent= new Intent(this, ForgotPassword4.class);

        //validate pass
        if(!validatePassword() | !validatePasswordConfirm()){
            return;
        }

        String newpass= password_input.getEditText().getText().toString();
        String phone_number=getIntent().getExtras().getString("phone");

        //update database
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users");
        reference.child(phone_number).child("password").setValue(newpass);
        startActivity(forgotpass4_intent);
        finish();
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
    private boolean validatePasswordConfirm() {
        String val = confirm_input.getEditText().getText().toString();
        String pass = password_input.getEditText().getText().toString();
        if (!val.equals(pass)) {
            confirm_input.setError("password is not matching");
            return false;
        } else {
            confirm_input.setError(null);
            confirm_input.setErrorEnabled(false);
            return true;
        }
    }
}