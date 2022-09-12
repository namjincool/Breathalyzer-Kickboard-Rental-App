package org.techtown.capston;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SplashActivity extends AppCompatActivity {
    HashMap<Double,Double> lat_lon_first_model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref1 = database.getReference("UsersData");//위도 , 경도
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lat_lon_first_model = (HashMap)snapshot.getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //Log.v("여기요1",String.valueOf(lat_lon.getLatitude()));//00
        //Log.v("여기요2",String.valueOf(lat_lon.getLongitude()));//00
        Loadingstart();
    }
    private void Loadingstart(){
        Handler handler=new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("lat_f", lat_lon_first_model.get("one"));
                intent.putExtra("lon_f", lat_lon_first_model.get("two"));
                intent.putExtra("f_m","인앤아웃 1");
                startActivity(intent);
                finish();
            }
            },1500);
    }
}

