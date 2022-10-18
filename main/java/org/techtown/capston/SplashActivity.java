package org.techtown.capston;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    public static final int TIMER = 3000;
//    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Loadingstart();
    }
    public void Loadingstart() {
        Handler handler=new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){
                Intent intent=new Intent(SplashActivity.this,LoginActivity2.class);

                startActivity(intent);
                finish();
            }
        },TIMER);
    }
}
