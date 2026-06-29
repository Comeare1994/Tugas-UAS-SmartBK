package com.example.smartbk;

import android.content.Context;
import android.content.SharedPreferences;

public class AccountStore {
    private static final String PREF_NAME = "smartbk_accounts";

    public static boolean isValidLogin(Context context, String user, String pass, String role) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if (role.equals("Guru BK")
                && !prefs.contains(key(role, "username"))
                && user.equals("gurubk")
                && pass.equals("123")) {
            return true;
        }
        return user.equals(getUsername(prefs, role)) && pass.equals(getPassword(prefs, role));
    }

    public static String getUsername(Context context, String role) {
        return getUsername(context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE), role);
    }

    public static String getPassword(Context context, String role) {
        return getPassword(context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE), role);
    }

    public static void saveAccount(Context context, String role, String username, String password) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(key(role, "username"), username)
                .putString(key(role, "password"), password)
                .apply();
    }

    private static String getUsername(SharedPreferences prefs, String role) {
        return prefs.getString(key(role, "username"), defaultUsername(role));
    }

    private static String getPassword(SharedPreferences prefs, String role) {
        return prefs.getString(key(role, "password"), "123");
    }

    private static String key(String role, String field) {
        return role.replace(" ", "_").toLowerCase() + "_" + field;
    }

    private static String defaultUsername(String role) {
        if (role.equals("Wali Kelas")) {
            return "wali";
        }
        if (role.equals("Kepala Sekolah")) {
            return "kepsek";
        }
        return "admin";
    }
}
