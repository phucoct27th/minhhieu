package com.project.hieu.ghichu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences luuTrangThai = getSharedPreferences("trangthai", Context.MODE_PRIVATE);
                boolean trangthai = luuTrangThai.getBoolean("dangnhap",false);
                if(trangthai == true){
                    Intent intent = new Intent(ScreenActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(ScreenActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        },3000);


    }
}
