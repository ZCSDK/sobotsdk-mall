package com.sobot.demo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.sobot.chat.ZCSobotApi;
import com.sobot.chat.api.model.Information;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Information info=new Information();
        info.setApp_key("1c1da2c0aad047d7ba1d14ecd18ae4f6");
        ZCSobotApi.openZCChat(MainActivity.this,info);
    }
}