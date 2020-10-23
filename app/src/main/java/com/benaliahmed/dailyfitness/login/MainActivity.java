package com.benaliahmed.dailyfitness.login;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.benaliahmed.dailyfitness.R;

public class MainActivity extends AppCompatActivity {

    //animation
    Animation top_anim, bot_anim;
    ImageView logo;
    TextView slogan;
    LinearLayout btns_lay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        logo = findViewById(R.id.logo_image);
        slogan = findViewById(R.id.slogan_tv);
        btns_lay = findViewById(R.id.btns_lay);

        top_anim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bot_anim = AnimationUtils.loadAnimation(this, R.anim.bot_animation);

        logo.setAnimation(top_anim);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                slogan.setVisibility(View.VISIBLE);
                btns_lay.setVisibility(View.VISIBLE);
                slogan.setAnimation(bot_anim);
                btns_lay.setAnimation(bot_anim);
            }
        },2000);
    }


    public void login(View view) {

        if (!isConnected(this)) {
            showDialog();
        } else {
            Intent intent_login = new Intent(this, Login.class);
            Pair[] pairs = new Pair[1];
            pairs[0] = new Pair<View, String>(findViewById(R.id.login_btn), "transition_login_btn");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
                startActivity(intent_login, options.toBundle());
            }
        }
    }


    public void Signup(View view) {
        if (!isConnected(this)) {
            showDialog();
        } else {
            Intent intent_signup = new Intent(this, Signup.class);
            Pair[] pairs = new Pair[1];
            pairs[0] = new Pair<View, String>(findViewById(R.id.signup_btn), "transition_signup_btn");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
                startActivity(intent_signup, options.toBundle());
            }
        }
    }

    private boolean isConnected(MainActivity mainActivity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wificon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobilecon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wificon != null && wificon.isConnected()) || (mobilecon != null && mobilecon.isConnected())) {
            return true;
        } else {
            return false;
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You need internet connection to use this app")
                .setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }
}