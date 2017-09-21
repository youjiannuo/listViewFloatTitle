package com.example.youjiannuo.listviewfloattitle;

import android.app.Application;

/**
 * Created by youjiannuo on 17/9/21.
 * Email by 382034324@qq.com
 */

public class YNApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ContextManager.setContext(this);
    }
}
