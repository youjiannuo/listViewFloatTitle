package com.example.youjiannuo.listviewfloattitle;

import android.content.Context;

public class ContextManager {

    private static Context context = null;

    protected static void setContext(Context context) {
        ContextManager.context = context;
    }

    public static Context getContext() {
        return context;
    }

    public static Object getSystemService(String service) {
        return context.getSystemService(service);
    }

    public static void post(Runnable runnable) {

    }

    public static String getString(int stringId) {
        return context.getResources().getString(stringId);
    }

    public static String[] getArrayString(int stringId) {
        return context.getResources().getStringArray(stringId);
    }


}
