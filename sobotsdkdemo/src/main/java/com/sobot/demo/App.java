package com.sobot.demo;

import android.app.Application;

import com.sobot.chat.ZCSobotApi;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ZCSobotApi.setShowDebug(true);
        ZCSobotApi.initSobotSDK(this,"1c1da2c0aad047d7ba1d14ecd18ae4f6","");
        ZCSobotApi.setInternationalLanguage(this,"en",true,true);
    }
}
