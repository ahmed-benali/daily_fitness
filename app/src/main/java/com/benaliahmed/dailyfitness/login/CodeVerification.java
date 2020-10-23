package com.benaliahmed.dailyfitness.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.benaliahmed.dailyfitness.R;
import com.benaliahmed.dailyfitness.database.DatabaseHelper;
import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class CodeVerification extends AppCompatActivity {

    String fullname, email, username, password,date,gender,phone_number,phone_code, phoneNumber,country_name;
    TextView tv_phone;
    PinView pinView;
    String code_by_system;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_code_verification);

        fullname= getIntent().getExtras().getString("fullname");
        username= getIntent().getExtras().getString("username");
        email= getIntent().getExtras().getString("email");
        password= getIntent().getExtras().getString("password");
        date= getIntent().getExtras().getString("date");
        gender= getIntent().getExtras().getString("gender");
        phone_code= getIntent().getExtras().getString("phone_code");
        phone_number= getIntent().getExtras().getString("phone_number");
        country_name= getIntent().getExtras().getString("country_name");

        tv_phone=findViewById(R.id.phone);
        phoneNumber ="+"+phone_code+phone_number;
        tv_phone.setText(phoneNumber);

        pinView= findViewById(R.id.pin_view);
        
        sendVerificationCodeToUser(phoneNumber);
    }

    private void sendVerificationCodeToUser(String phone) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    code_by_system= s;
                }

                @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                    String code= phoneAuthCredential.getSmsCode();
                    if(code!=null){
                    pinView.setText(code);
                    //verifycode(code);
                    }
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    Toast.makeText(CodeVerification.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            };

    private void verifycode(String code) {
        PhoneAuthCredential credential= PhoneAuthProvider.getCredential(code_by_system,code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final Intent intent= new Intent(this, Login.class);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CodeVerification.this, "verification completed", Toast.LENGTH_SHORT).show();
                            storeNewUserData();
                            startActivity(intent);
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(CodeVerification.this, "verification error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void storeNewUserData() {
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("users");
        DatabaseHelper addNewUser = new DatabaseHelper(fullname,email,username,password,date,gender,phoneNumber);
        reference.child(phoneNumber).setValue(addNewUser);
    }

    public void VerifyCodeBtn(View view) {
        String code= pinView.getText().toString();
        if(!code.isEmpty()){
            verifycode(code);
        }
        //Toast.makeText(this,fullname+"\n"+ email+"\n" + username+"\n" + password+"\n" + date+"\n" + gender+"\n" + phoneNumber, Toast.LENGTH_SHORT).show();
    }

    public void frame(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void VerificationExit(View view) {
        Intent intent_signup3= new Intent(this, Signup3.class);
        intent_signup3.putExtra("fullname",fullname);
        intent_signup3.putExtra("username",username);
        intent_signup3.putExtra("email",email);
        intent_signup3.putExtra("password",password);
        intent_signup3.putExtra("date",date);
        intent_signup3.putExtra("gender",gender);
        intent_signup3.putExtra("phone_code",phone_code);
        intent_signup3.putExtra("phone_number",phone_number);
        intent_signup3.putExtra("country_name",country_name);
        startActivity(intent_signup3);
    }
}