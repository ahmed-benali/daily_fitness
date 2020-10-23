package com.benaliahmed.dailyfitness.database;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences users_session;
    SharedPreferences.Editor editor;
    Context context;

    public static final String SESSION_USERSESSION = "userLoginSession";
    public static final String SESSION_REMEMBERME = "rememberme";
    public static final String SESSION_STATS = "stats";

    //usersession variables
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_FULLNAME = "fullname";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_DATE = "date";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_PASSWORD = "password";
    //remember me variables
    private static final String IS_REMEMBER = "IsRemember";
    public static final String KEY_SESSION_PHONE = "phone";
    public static final String KEY_SESSION_PASSWORD = "password";
    public static final String KEY_SESSION_COUNTRYCODE = "code";
    public static final String KEY_SESSION_COUNTRYCODENUMBER = "code_number";
    //stats variables
    public static final String KEY_AGE ="age";
    public static final String KEY_WEIGHT ="weight";
    public static final String KEY_HEIGHT ="height";

    public SessionManager(Context context, String session_name) {
        this.context = context;
        users_session = this.context.getSharedPreferences(session_name, Context.MODE_PRIVATE);
        editor = users_session.edit();
    }


    public void createLoginSession(String fullname, String email, String date, String gender, String username, String phone, String password) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_FULLNAME, fullname);
        editor.putString(KEY_DATE, date);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_GENDER, gender);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_PASSWORD, password);
        editor.commit();
    }

    public HashMap<String, String> getUsersDetailFromSession() {
        HashMap<String, String> userData = new HashMap<String, String>();
        userData.put(KEY_FULLNAME, users_session.getString(KEY_FULLNAME, null));
        userData.put(KEY_PASSWORD, users_session.getString(KEY_PASSWORD, null));
        userData.put(KEY_PHONE, users_session.getString(KEY_PHONE, null));
        userData.put(KEY_USERNAME, users_session.getString(KEY_USERNAME, null));
        userData.put(KEY_EMAIL, users_session.getString(KEY_EMAIL, null));
        userData.put(KEY_GENDER, users_session.getString(KEY_GENDER, null));
        userData.put(KEY_DATE, users_session.getString(KEY_DATE, null));

        return userData;
    }


    public boolean checkLogin() {
        if (users_session.getBoolean(IS_LOGIN, true)) {
            return true;
        } else {
            return false;
        }
    }

    public void logoutUserFromFromSession() {
        editor.clear();
        editor.commit();
    }

    public void createremembermeSession(boolean status,String phone, String password, String code, String code_number) {
        editor.putBoolean(IS_REMEMBER, status);
        editor.putString(KEY_SESSION_PHONE, phone);
        editor.putString(KEY_SESSION_PASSWORD, password);
        editor.putString(KEY_SESSION_COUNTRYCODE, code);
        editor.putString(KEY_SESSION_COUNTRYCODENUMBER, code_number);
        editor.commit();
    }

    public HashMap<String, String> getRemembermeDetailFromSession() {
        HashMap<String, String> userData = new HashMap<String, String>();
        userData.put(KEY_SESSION_PHONE, users_session.getString(KEY_SESSION_PHONE, null));
        userData.put(KEY_SESSION_PASSWORD, users_session.getString(KEY_SESSION_PASSWORD , null));
        userData.put(KEY_SESSION_COUNTRYCODE, users_session.getString(KEY_SESSION_COUNTRYCODE, null));
        userData.put(KEY_SESSION_COUNTRYCODENUMBER, users_session.getString(KEY_SESSION_COUNTRYCODENUMBER, null));
        return userData;
    }

    public boolean checkRememberme() {
        if (users_session.getBoolean(IS_REMEMBER, false)) {
            return true;
        } else {
            return false;
        }
    }

    public void createStatsSession(String age,String weight,String height){
        editor.putString(KEY_AGE,age);
        editor.putString(KEY_WEIGHT,weight);
        editor.putString(KEY_HEIGHT,height);
        editor.commit();
    }
    public HashMap<String, String> getStatsFromSession() {
        HashMap<String, String> userData = new HashMap<String, String>();
        userData.put(KEY_AGE, users_session.getString(KEY_AGE, null));
        userData.put(KEY_WEIGHT, users_session.getString(KEY_WEIGHT, null));
        userData.put(KEY_HEIGHT, users_session.getString(KEY_HEIGHT, null));
        return userData;
    }
}
