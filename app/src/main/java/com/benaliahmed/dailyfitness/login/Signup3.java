package com.benaliahmed.dailyfitness.login;

import androidx.annotation.NonNull;
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
import android.widget.Toast;

import com.benaliahmed.dailyfitness.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

public class Signup3 extends AppCompatActivity {


    Button nextbtn, loginbtn;
    TextView titletext;
    ImageView backbtn;

    TextInputLayout phone_et;

    String fullname, email, username, password, date, gender, phone_number_back, phone_code_back, country_name_back;
    CountryCodePicker codePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup3);

        backbtn = findViewById(R.id.signup_back_btn);
        nextbtn = findViewById(R.id.signup_next_btn);
        loginbtn = findViewById(R.id.signup_log_btn);
        titletext = findViewById(R.id.signup_titletext);
        codePicker = findViewById(R.id.country_code_picker);

        phone_et = findViewById(R.id.Signup_phone);


        fullname = getIntent().getExtras().getString("fullname");
        username = getIntent().getExtras().getString("username");
        email = getIntent().getExtras().getString("email");
        password = getIntent().getExtras().getString("password");
        date = getIntent().getExtras().getString("date");
        gender = getIntent().getExtras().getString("gender");
        try {
            phone_code_back = getIntent().getExtras().getString("phone_code");
            phone_number_back = getIntent().getExtras().getString("phone_number");
            country_name_back = getIntent().getExtras().getString("country_name");
            phone_et.getEditText().setText(phone_number_back);
            codePicker.setCountryForNameCode(country_name_back);
        } catch (Exception e) {
        }

    }

    public void frame(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void Signup3Back(View view) {
        Intent intent_signup2 = new Intent(this, Signup2.class);
        Pair[] pairs = new Pair[4];
        pairs[0] = new Pair<View, String>(backbtn, "transition_signup_backbtn");
        pairs[1] = new Pair<View, String>(nextbtn, "transition_signup_nextbtn");
        pairs[2] = new Pair<View, String>(loginbtn, "transition_signup_logbtn");
        pairs[3] = new Pair<View, String>(titletext, "transition_signup_titletext");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            intent_signup2.putExtra("fullname", fullname);
            intent_signup2.putExtra("username", username);
            intent_signup2.putExtra("email", email);
            intent_signup2.putExtra("password", password);
            intent_signup2.putExtra("date", date);
            intent_signup2.putExtra("gender", gender);
            intent_signup2.putExtra("phone_code", codePicker.getSelectedCountryCode());
            intent_signup2.putExtra("phone_number", phone_et.getEditText().getText().toString());
            intent_signup2.putExtra("country_name", codePicker.getSelectedCountryNameCode());
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Signup3.this, pairs);
            startActivity(intent_signup2, options.toBundle());
        } else {
            startActivity(intent_signup2);
        }
    }

    public void Signup3Next(View view) {
/*        if (!validatePhone()) {
            return;
        }*/

        final Intent intnent_verification = new Intent(this, CodeVerification.class);
        intnent_verification.putExtra("fullname", fullname);
        intnent_verification.putExtra("username", username);
        intnent_verification.putExtra("email", email);
        intnent_verification.putExtra("password", password);
        intnent_verification.putExtra("date", date);
        intnent_verification.putExtra("gender", gender);
        intnent_verification.putExtra("phone_code", codePicker.getSelectedCountryCode());
        intnent_verification.putExtra("phone_number", phone_et.getEditText().getText().toString());
        intnent_verification.putExtra("country_name", codePicker.getSelectedCountryNameCode());

        final String phone_number = "+"+codePicker.getFullNumber()+phone_et.getEditText().getText().toString();
        Query checkuser = FirebaseDatabase.getInstance().getReference("users").orderByChild("phone").equalTo(phone_number);
        checkuser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    phone_et.setError(null);
                    phone_et.setErrorEnabled(false);
                    Toast.makeText(Signup3.this, "phone number already registered", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(intnent_verification);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Signup3.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }

        public void Signup3Log (View view){
            Intent log_intnet = new Intent(this, Login.class);
            startActivity(log_intnet);
        }

/*    private boolean validatePhone() {
        if (Integer.parseInt(phone_number) / 10000000 < 1) {
            return false;
        } else {
            return true;
        }
    }*/
    }