package com.project.hieu.ghichu;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by Admin on 10/11/2016.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
