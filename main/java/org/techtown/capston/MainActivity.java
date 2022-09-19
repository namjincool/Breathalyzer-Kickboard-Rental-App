package org.techtown.capston;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    Timer timer = new Timer();
    private double lat;
    private double lon;
    private GoogleMap mMap;
    Button refreshbtn;
    int count;
    Button button;
    String []model=new String[3];
    final int CAMERAZOOM = 19;
    String state="사용가능"; // 만약 사용중이면 사용 중 ,
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //위 3줄 데이터베이스 값 저장 아래 는 데이터베이스 값 읽기
        Intent intent=getIntent();
        lat=intent.getDoubleExtra("lat_f",0);
        lon=intent.getDoubleExtra("lon_f",0);
        model[0]=intent.getStringExtra("f_m");
        button = findViewById(R.id.btn_draw_State);
        //큐알로 이동을 위한 새로운 액티비티를 Intent로 호출 51~57
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        finish();
                    }
                };
                Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
                startActivity(intent);
                timer.schedule(task,2000);
            }
        });
        refreshbtn=findViewById(R.id.refresh_btn);
        refreshbtn.setOnClickListener(new View.OnClickListener() {
            //2개더 받아야함 효율 강구 배열;;
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref1 = database.getReference("UsersData");//위도 , 경도
                ref1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        HashMap<Double,Double>lat_lon_first_model = (HashMap)snapshot.getValue();
                        lat=lat_lon_first_model.get("one");
                        lon=lat_lon_first_model.get("two");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    //아래 코드는 마커만 표시를 하게 하자 현재 위치
        public void onMapReady (final GoogleMap googleMap){
            mMap = googleMap;
            //아래 위치는 DB에서 가지고와야함
            LatLng Locate = new LatLng(lat, lon);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(Locate)); //카메라 이동
            mMap.moveCamera(CameraUpdateFactory.zoomTo(CAMERAZOOM)); // 카메라 이동 후 줌인
            BitmapDrawable bitmapDrawable=(BitmapDrawable)getResources().getDrawable(R.drawable.kick);
            Bitmap b= bitmapDrawable.getBitmap();
            Bitmap marker = Bitmap.createScaledBitmap(b,100, 100,false);
            //mMap.addMarker(new MarkerOptions().position(Locate).title("서울").icon(BitmapDescriptorFactory.fromBitmap(marker)));
            //Locate = new LatLng(lat,lon);
            mMap.addMarker(new MarkerOptions().position(Locate).title("인앤아웃1").snippet(state).icon(BitmapDescriptorFactory.fromBitmap(marker)));
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
            } else {
                checkLocationPermissionWithRationale();
            }
        }

        //상위 if else 권한지정 코드
        public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
        // 프로젝트 시작시 권한 설정 코드
        private void checkLocationPermissionWithRationale () {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    new AlertDialog.Builder(this)
                            .setTitle("위치정보")
                            .setMessage("이 앱을 사용하기 위해서는 위치정보에 접근이 필요합니다. 위치정보 접근을 허용하여 주세요.")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                            Manifest.permission.ACCESS_FINE_LOCATION
                                    }, MY_PERMISSIONS_REQUEST_LOCATION);
                                }
                            }).create().show();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, MY_PERMISSIONS_REQUEST_LOCATION);
                }
            }
        }
        @Override
        public void onRequestPermissionsResult ( int requestCode, String permissions[], int[] grantResults){
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            switch (requestCode) {
                case MY_PERMISSIONS_REQUEST_LOCATION: {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            mMap.setMyLocationEnabled(true);
                        }
                    } else {
                        Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                    }
                    return;
                }
            }
        }
        public void onLocationChanged (Location location){
            lat = location.getLatitude();
            lon = location.getLongitude();
            Log.i("MyLocTest", "onLocationChanged() 호출되었습니다");
            Log.i("MyLocTest", "내 위치는 Latitude :" + lat + " Longtitude : " + lon);
            updateUserLocation(lat, lon);
            count++;
        }
        private void updateUserLocation ( double lat, double lon){

        }
}
