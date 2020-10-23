package com.benaliahmed.dailyfitness.run;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.benaliahmed.dailyfitness.R;
import com.benaliahmed.dailyfitness.database.LocalDbManager;
import com.benaliahmed.dailyfitness.database.SessionManager;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.HashMap;

public class MainMenu extends AppCompatActivity {
    ChipNavigationBar chipNavigationBar;
    String age, weight, height, gender;
    int int_duration;
    float flt_distance, flt_speed, flt_age, flt_weight, flt_height;
    double dbl_calories;
    AlertDialog dialog;
    AlertDialog.Builder dialog_builder;
    Button yes_btn, no_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        chipNavigationBar = findViewById(R.id.chipnav);
        chipNavigationBar.setItemSelected(R.id.navigation_run, true);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new runFragment()).commit();
        bottomMenu();

        try {
            final SessionManager sessionManagerStats = new SessionManager(this, SessionManager.SESSION_STATS);
            HashMap<String, String> stats_data = sessionManagerStats.getStatsFromSession();
            age = stats_data.get(SessionManager.KEY_AGE);
            weight = stats_data.get(SessionManager.KEY_WEIGHT);
            height = stats_data.get(SessionManager.KEY_HEIGHT);
            flt_age = Float.parseFloat(age);
            flt_weight = Float.parseFloat(weight);
            flt_height = Float.parseFloat(height);
/*            final SessionManager sessionManagerUser = new SessionManager(this, SessionManager.SESSION_USERSESSION);
            HashMap<String, String> user_data = sessionManagerUser.getUsersDetailFromSession();
            gender = user_data.get(SessionManager.KEY_GENDER);*/
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (age == null || height == null || weight == null) {
            chipNavigationBar.setItemSelected(R.id.navigation_profile, true);
            Toast.makeText(this, "please fill up missing information!", Toast.LENGTH_SHORT).show();
        }


        Cursor cursor = null;
        String id = "0";
        int myid = 0;
        try {
            LocalDbManager dbManager = new LocalDbManager(this);
            SQLiteDatabase sdb = dbManager.getReadableDatabase();
            cursor = sdb.rawQuery("select id from stats_table", null);
            while (cursor.moveToNext()) {
                id = cursor.getString(0);
            }
            myid = Integer.parseInt(id) + 1;
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            boolean stop_clicked = getIntent().getExtras().getBoolean("stop_btn");
            String date = getIntent().getExtras().getString("date");
            String distance = getIntent().getExtras().getString("distance");
            String duration = getIntent().getExtras().getString("duration");
            flt_distance = Float.parseFloat(distance);
            int_duration = Integer.parseInt(duration);
            String speed = "0";
            if (int_duration > 0) {
                flt_speed = flt_distance / int_duration;
                speed = String.valueOf(flt_speed);
            }
            String calories = "0";
            dbl_calories = (flt_distance) * flt_weight * 1.036;
            calories = String.format("%.2f",dbl_calories);

            //Toast.makeText(this, "stats="+date+"////"+distance+"////"+duration, Toast.LENGTH_SHORT).show();

            if (stop_clicked) {
                if (age == null || height == null || weight == null) {
                    chipNavigationBar.setItemSelected(R.id.navigation_profile, true);
                    Toast.makeText(this, "please fill up missing information!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        LocalDbManager dbManager = new LocalDbManager(this);
                        SQLiteDatabase sdb = dbManager.getWritableDatabase();
                        sdb.execSQL("insert into stats_table values('" + myid + "','" + date + "','" + distance + "','" + duration + "','" + speed + "','" + calories + "')");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    chipNavigationBar.setItemSelected(R.id.navigation_progress, true);
                    //getSupportFragmentManager().beginTransaction().detach(getSupportFragmentManager().findFragmentById(R.id.fragment_container)).commit();
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void bottomMenu() {
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                switch (i) {
                    case R.id.navigation_run:
                        fragment = new runFragment();
                        break;
                }

                switch (i) {
                    case R.id.navigation_progress:
                        fragment = new progressFragment();
                        break;

                }
                switch (i) {
                    case R.id.navigation_profile:
                        fragment = new profileFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
    public void onBackPressed(){
        dialog_builder = new AlertDialog.Builder(this);
        final View popup_view = getLayoutInflater().inflate(R.layout.logout_popup, null);
        yes_btn = popup_view.findViewById(R.id.yes_btn);
        no_btn = popup_view.findViewById(R.id.no_btn);
        dialog_builder.setView(popup_view);
        dialog = dialog_builder.create();
        dialog.show();
        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        no_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}