package com.example.myapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;
    private final Context context;
    private static final String IS_LOGGED_IN = "isLoggedIn";


    private static final String PREF_NAME = "user_session";
    private static final String KEY_USER_ID = "user_id";

    public SessionManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    // Save user ID
    public void saveUserId(int userId) {
        editor.putInt(KEY_USER_ID, userId);
        editor.apply();
    }

    // Get user ID
    public int getUserId() {
        return preferences.getInt(KEY_USER_ID, -1);  // Default to -1 if not found
    }

    // Clear session (logout)

    public void clearSession() {
        editor.clear();
        editor.apply(); // Make sure changes are saved
    }

    public Context getContext() {
        return context;
    }
}
