package com.example.ahmedessam.moviedbflow;

import android.app.Application;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by ahmed essam on 23/05/2017.
 */

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(this);
        Downloader downloader = new OkHttp3Downloader(getCacheDir(), 100 * 1024 * 1024);
        Picasso picasso = new Picasso.Builder(this).downloader(downloader).build();
Picasso.setSingletonInstance(picasso);
    }
}
