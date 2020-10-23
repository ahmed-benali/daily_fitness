package com.benaliahmed.dailyfitness.login;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.autofill.AutofillValue;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.benaliahmed.dailyfitness.R;

import java.util.Calendar;

public class Signup2 extends AppCompatActivity {

    Button nextbtn, loginbtn;
    TextView titletext;
    ImageView backbtn;

    RadioGroup radioGroup;
    RadioButton selectedgender, rb1, rb2, rb3;
    DatePicker datePicker;

    //get data from user variables
    String fullname, email, username, password, gender, gender_back, date_back, phone_number, phone_code, country_name;
    AutofillValue datepicker_view_back;
    String datepicker_view_string;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup2);

        backbtn = findViewById(R.id.signup_back_btn);
        nextbtn = findViewById(R.id.signup_next_btn);
        loginbtn = findViewById(R.id.signup_log_btn);
        titletext = findViewById(R.id.signup_titletext);

        radioGroup = findViewById(R.id.radiogroup);
        datePicker = findViewById(R.id.datepicker);
        rb1 = findViewById(R.id.radioButton1);
        rb2 = findViewById(R.id.radioButton2);
        rb3 = findViewById(R.id.radioButton3);

        fullname = getIntent().getExtras().getString("fullname");
        username = getIntent().getExtras().getString("username");
        email = getIntent().getExtras().getString("email");
        password = getIntent().getExtras().getString("password");


        try {
            gender_back = getIntent().getExtras().getString("gender");
            date_back = getIntent().getExtras().getString("date");
            phone_code = getIntent().getExtras().getString("phone_code");
            phone_number = getIntent().getExtras().getString("phone_number");
            country_name = getIntent().getExtras().getString("country_name");
        } catch (Exception e) {
        }
        if (rb3.getText().equals(gender_back))
            rb3.setChecked(true);
        else if (rb2.getText().equals(gender_back)) {
            rb2.setChecked(true);
        } else if (rb1.getText().equals(gender_back)) {
            rb1.setChecked(true);
        }
    }

    public void frame(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void Signup2Log(View view) {
        Intent log_intnet = new Intent(this, Login.class);
        startActivity(log_intnet);
    }


    @SuppressLint("ResourceType")
    public void Signup2Next(View view) {
        if (!validateAge() | !validateGender()) {
            return;
        }

        selectedgender = findViewById(radioGroup.getCheckedRadioButtonId());
        gender = selectedgender.getText().toString();

        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        String date = day + "/" + month + "/" + year;


        Intent intent_signup3 = new Intent(this, Signup3.class);
        Pair[] pairs = new Pair[4];
        pairs[0] = new Pair<View, String>(backbtn, "transition_signup_backbtn");
        pairs[1] = new Pair<View, String>(nextbtn, "transition_signup_nextbtn");
        pairs[2] = new Pair<View, String>(loginbtn, "transition_signup_logbtn");
        pairs[3] = new Pair<View, String>(titletext, "transition_signup_titletext");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            intent_signup3.putExtra("fullname", fullname);
            intent_signup3.putExtra("username", username);
            intent_signup3.putExtra("email", email);
            intent_signup3.putExtra("password", password);
            intent_signup3.putExtra("gender", gender);
            intent_signup3.putExtra("date", date);
            intent_signup3.putExtra("phone_code", phone_code);
            intent_signup3.putExtra("phone_number", phone_number);
            intent_signup3.putExtra("country_name", country_name);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Signup2.this, pairs);
            startActivity(intent_signup3, options.toBundle());
        } else {
            startActivity(intent_signup3);
        }
    }

    public void Signup2Back(View view) {
        try {
            selectedgender = findViewById(radioGroup.getCheckedRadioButtonId());
            gender = selectedgender.getText().toString();
        } catch (Exception e) {
        }

        Intent intent_signup = new Intent(this, Signup.class);
        Pair[] pairs = new Pair[4];
        pairs[0] = new Pair<View, String>(backbtn, "transition_signup_backbtn");
        pairs[1] = new Pair<View, String>(nextbtn, "transition_signup_nextbtn");
        pairs[2] = new Pair<View, String>(loginbtn, "transition_signup_logbtn");
        pairs[3] = new Pair<View, String>(titletext, "transition_signup_titletext");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            intent_signup.putExtra("fullname", fullname);
            intent_signup.putExtra("username", username);
            intent_signup.putExtra("email", email);
            intent_signup.putExtra("password", password);
            intent_signup.putExtra("gender", gender);
            intent_signup.putExtra("phone_code", phone_code);
            intent_signup.putExtra("phone_number", phone_number);
            intent_signup.putExtra("country_name", country_name);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Signup2.this, pairs);
            startActivity(intent_signup, options.toBundle());
        } else {
            startActivity(intent_signup);
        }
    }

    private boolean validateGender() {
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateAge() {
        int currentyear = Calendar.getInstance().get(Calendar.YEAR);
        int userage = datePicker.getYear();
        if (currentyear - userage < 8) {
            Toast.makeText(this, "Age is not eligible", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}