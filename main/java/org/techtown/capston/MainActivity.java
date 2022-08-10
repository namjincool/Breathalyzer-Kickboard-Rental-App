package org.techtown.capston;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private double lat,lon;
    int count;
    Button button;
    String latitude,longitude;
    //데이터베이스 예시를 위해 사용한 코드  41 42
    TextView readText;
    EditText writeText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        button = findViewById(R.id.btn_draw_State);
        //큐알로 이동을 위한 새로운 액티비티를 Intent로 호출 51~57
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
                startActivity(intent);
            }
        });
        //임의로 확인만 해보는 코드  ex 58~82
        writeText=findViewById(R.id.write_text); // editText
        readText=findViewById(R.id.read_text); // Textview
        Button readBtn=findViewById(R.id.read_btn);
        readBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database =FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference("latlong");//위도 , 경도
                //DatabaseReference ref2 = database.getReference("longitude");//경도
                ref.setValue(writeText.getText().toString());
                //ref2.setValue(writeText.getText().toString().split(" ")[1]);
                //위 3줄 데이터베이스 값 저장 아래 는 데이터베이스 값 읽기
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String value = snapshot.getValue(String.class);
                        latitude=value.split(" ")[0];
                        longitude=value.split(" ")[1];
                        readText.setText("위도 : "+ latitude + "경도 : "+ longitude);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
    //아래 코드는 마커만 표시를 하게 하자 현재 위치
    public void onMapReady(final GoogleMap googleMap){
        mMap=googleMap;
        //아래 위치는 DB에서 가지고와야함

        LatLng Locate =new LatLng(37.56,126.97);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(Locate)); //카메라 이동
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15)); // 카메라 이동 후 줌인
        MarkerOptions markerOption=new MarkerOptions();
        markerOption.position(Locate).title("서울");
        mMap.addMarker(markerOption);
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Locate,15)); 위 구글맨 줌인 코드와 겹칠것 같아 주석 처리
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            checkLocationPermissionWithRationale();
        }
    }
        //상위 if else 권한지정 코드
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    // 프로젝트 시작시 권한 설정 코드
    private void checkLocationPermissionWithRationale() {
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
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

    public void onLocationChanged(Location location){
        lat = location.getLatitude();
        lon = location.getLongitude();
        Log.i("MyLocTest", "onLocationChanged() 호출되었습니다");
        Log.i("MyLocTest","내 위치는 Latitude :" + lat + " Longtitude : " + lon);
        updateUserLocation(lat,lon);
        count++;
    }

    private void updateUserLocation(double lat, double lon) {

    }
}
