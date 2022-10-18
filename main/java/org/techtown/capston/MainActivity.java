package org.techtown.capston;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
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
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private double lat; //받은 코드
    private double lon;// 받은 코드
    private GoogleMap mMap; //구글 맵 이용 코드 (필수)
    int count; //받은 코드
    Button rent_btn; // 대여하기 버튼
    final int CAMERAZOOM = 19; // 카메라 줌 설정
    Button refresh1;
    String []current_state=new String[3];
    private long backpressedTime = 0; // 뒤로가기 클릭시 구현
    double[][] latLon=new double[3][2]; // 각 모델을  double로 위도와 경도 측정후 값을 저장하는 공간
    //Sleep lat_lon=new Sleep();
    final String [][] inten={{"f_lati","f_logi"},{"s_lati","s_logi"},{"t_lati","t_logi"}};
    Loding loding;
    final String retry_latlon [][]={{"back_lat1","back_lon1"},{"back_lat2","back_lon2"},{"back_lat3","back_lon3"}};
    //HashMap<Double,Double> lat_lon_first_model,lat_lon_second_model,lat_lon_third_model;

    Intent intent;
    double[][] new_latLon=new double[3][2];
    double []state=new double[3]; // 만약 사용중이면 사용 중 , DB 연결후 재작성
    final LatLng Parking= new LatLng(Define.latitude,Define.longitude);
    String isuseingkick_ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent=getIntent();
        setContentView(R.layout.activity_main);
        /*새로고침 구현 */
        refresh1=findViewById(R.id.refresh1);
        refresh1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference("UserData");//위도 , 경도
                ref.child("model1").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Define.lat_lon_first_model = (HashMap)snapshot.getValue();
                        new_latLon[0][0]=Define.lat_lon_first_model.get("lat1");
                        new_latLon[0][1]=Define.lat_lon_first_model.get("lon1");
                        state[0]=Define.lat_lon_first_model.get("state");
                        //Log.v("1번",String.valueOf(lat_lon_first_model));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                ref.child("model2").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Define.lat_lon_second_model = (HashMap)snapshot.getValue();
                        new_latLon[1][0]=Define.lat_lon_second_model.get("lat2");
                        new_latLon[1][1]=Define.lat_lon_second_model.get("lon2");
                        state[1]=Define.lat_lon_second_model.get("state");
                        //Log.v("1번",String.valueOf(lat_lon_first_model));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                ref.child("model3").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Define.lat_lon_third_model = (HashMap)snapshot.getValue();
                        new_latLon[2][0]=Define.lat_lon_third_model.get("lat3");
                        new_latLon[2][1]=Define.lat_lon_third_model.get("lon3");
                        state[2]=Define.lat_lon_third_model.get("state");
                        //Log.v("1번",String.valueOf(lat_lon_first_model));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                mMap.clear();
                new_latLon[0][0]=new_latLon[0][0]==0?latLon[0][0]:new_latLon[0][0];
                new_latLon[0][1]=new_latLon[0][1]==0?latLon[0][1]:new_latLon[0][1];
                new_latLon[1][0]=new_latLon[1][0]==0?latLon[1][0]:new_latLon[1][0];
                new_latLon[1][1]=new_latLon[1][1]==0?latLon[1][1]:new_latLon[1][1];
                new_latLon[2][0]=new_latLon[2][0]==0?latLon[2][0]:new_latLon[2][0];
                new_latLon[2][1]=new_latLon[2][1]==0?latLon[2][1]:new_latLon[2][1];
                current_state[0]=state[0]==0.1?"대여가 가능합니다":"현재 사용 중 입니다";
                current_state[1]=state[1]==0.1?"대여가 가능합니다":"현재 사용 중 입니다";
                current_state[2]=state[2]==0.1?"대여가 가능합니다":"현재 사용 중 입니다";
                BitmapDrawable drawable=(BitmapDrawable)getResources().getDrawable(R.drawable.parking);
                Bitmap park=drawable.getBitmap();
                Bitmap park_mark=Bitmap.createScaledBitmap(park,100,100,false);
                mMap.addMarker(new MarkerOptions().position(Parking).title("주차 공간").snippet("주차공간입니다").icon(BitmapDescriptorFactory.fromBitmap(park_mark)));
                mMap.addCircle(new CircleOptions().center(Parking).radius(11).strokeColor(Color.parseColor("#ffffff")).fillColor(Color.parseColor("#5587cefa")));
                LatLng Locate1 = new LatLng(new_latLon[0][0],new_latLon[0][1]);
                LatLng Locate2 = new LatLng(new_latLon[1][0],new_latLon[1][1]);
                LatLng Locate3 = new LatLng(new_latLon[2][0],new_latLon[2][1]);
                BitmapDrawable bitmapDrawable=(BitmapDrawable)getResources().getDrawable(R.drawable.kick);
                Bitmap b= bitmapDrawable.getBitmap();
                Bitmap marker = Bitmap.createScaledBitmap(b,100, 100,false);
                if (state[0]==0.1) {
                    mMap.addMarker(new MarkerOptions().position(Locate1).title(Define.SsingSsing[0]).snippet(current_state[0]).icon(BitmapDescriptorFactory.fromBitmap(marker)));
                }
                if (state[1]==0.1) {
                    mMap.addMarker(new MarkerOptions().position(Locate2).title(Define.SsingSsing[1]).snippet(current_state[1]).icon(BitmapDescriptorFactory.fromBitmap(marker)));
                }
                if (state[2]==0.1) {
                    mMap.addMarker(new MarkerOptions().position(Locate3).title(Define.SsingSsing[2]).snippet(current_state[2]).icon(BitmapDescriptorFactory.fromBitmap(marker)));
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLng(Locate1)); //카메라 이동
                mMap.moveCamera(CameraUpdateFactory.zoomTo(CAMERAZOOM)); // 카메라 이동 후 줌인
            }
        });
        latLon[0][0]=intent.getDoubleExtra(Define.inten[0][0],0);
        Log.v("abcd",""+latLon[0][0]);
        latLon[0][1]=intent.getDoubleExtra(Define.inten[0][1],0);
        Log.v("abc",""+latLon[0][1]);
        state[0]=intent.getDoubleExtra(Define.state[0],0);
        latLon[1][0]=intent.getDoubleExtra(Define.inten[1][0],0);
        latLon[1][1]=intent.getDoubleExtra(Define.inten[1][1],0);
        state[1]=intent.getDoubleExtra(Define.state[1],0);
        latLon[2][0]=intent.getDoubleExtra(Define.inten[2][0],0);
        latLon[2][1]=intent.getDoubleExtra(Define.inten[2][1],0);
        state[2]=intent.getDoubleExtra(Define.state[2],0);
        isuseingkick_ID=intent.getStringExtra(Define.user);
        current_state[0]=state[0]==0.1?"대여가 가능합니다":"현재 사용 중 입니다";
        current_state[1]=state[1]==0.1?"대여가 가능합니다":"현재 사용 중 입니다";
        current_state[2]=state[2]==0.1?"대여가 가능합니다":"현재 사용 중 입니다";
        loding = new Loding(this);
        loding.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (intent.getIntExtra(Define.error,0)==1){
            Toast.makeText(getApplicationContext(),"측정이 아쉽게 안됐습니다 ㅠㅠ",Toast.LENGTH_LONG).show();
        } else if (intent.getIntExtra(Define.error,0)==2){
            double value=Math.random()*0.1;
            Toast.makeText(getApplicationContext(),"현재 혈중 알코올 농도가"+String.format("%.2f",value) +"입니다. 다시측정해주세요.",Toast.LENGTH_LONG).show();
        }
        /*대여하기 기능 구현 */
        rent_btn=findViewById(R.id.btn_draw_State);
        rent_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(MainActivity.this,ScannerActivity.class);
                intent1.putExtra(Define.user,isuseingkick_ID); // DB요소 추가를 위해서 일부러 넘김

                startActivity(intent1);
                finish();
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    //아래 코드는 마커만 표시를 하게 하자 현재 위치
        public void onMapReady (final GoogleMap googleMap){
            mMap = googleMap;
            /*Locate 변수들은 킥보드의 모델을 지정 후에 많을시 List로 선언을 해줘야 함*/
            LatLng Locate1 = new LatLng(latLon[0][0],latLon[0][1]);
            LatLng Locate2 = new LatLng(latLon[1][0],latLon[1][1]);
            LatLng Locate3 = new LatLng(latLon[2][0],latLon[2][1]);
            /*카메라 이동 및 이동 후 줌인 */
            mMap.moveCamera(CameraUpdateFactory.newLatLng(Locate1)); //카메라 이동
            mMap.moveCamera(CameraUpdateFactory.zoomTo(CAMERAZOOM)); // 카메라 이동 후 줌인
            BitmapDrawable drawable=(BitmapDrawable)getResources().getDrawable(R.drawable.parking);
            Bitmap park=drawable.getBitmap();
            Bitmap park_mark=Bitmap.createScaledBitmap(park,100,100,false);
            mMap.addMarker(new MarkerOptions().position(Parking).title("주차 공간").snippet("주차공간입니다").icon(BitmapDescriptorFactory.fromBitmap(park_mark)));
            mMap.addCircle(new CircleOptions().center(Parking).radius(11).strokeColor(Color.parseColor("#ffffff")).fillColor(Color.parseColor("#5587cefa")));
            /*Bitmap은 마커를 꾸미기위해 위해 사용하는 코드, 킥보드*/
            BitmapDrawable bitmapDrawable=(BitmapDrawable)getResources().getDrawable(R.drawable.kick);
            Bitmap b= bitmapDrawable.getBitmap();
            Bitmap marker = Bitmap.createScaledBitmap(b,100, 100,false);
            /*맵에 킥보드 위치정보를 넣기*/
            if (state[0]==0.1) {
                    mMap.addMarker(new MarkerOptions().position(Locate1).title(Define.SsingSsing[0]).snippet(current_state[0]).icon(BitmapDescriptorFactory.fromBitmap(marker)));
            }
            if (state[1]==0.1) {
                mMap.addMarker(new MarkerOptions().position(Locate2).title(Define.SsingSsing[1]).snippet(current_state[1]).icon(BitmapDescriptorFactory.fromBitmap(marker)));
            }
            if (state[2]==0.1) {
                mMap.addMarker(new MarkerOptions().position(Locate3).title(Define.SsingSsing[2]).snippet(current_state[2]).icon(BitmapDescriptorFactory.fromBitmap(marker)));
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
            } else {
                checkLocationPermissionWithRationale();
            }
        }
        /*바로 위 if else 문 포함 아래까지 현재 위치 권한 및 앱 처음 사용시 권한 지정을 해주는 코드*/
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
        /*뒤로가기 누를시 동작 --> 한번 누른 후 2초만에 한번 더 누를 시 종료하게 동작 */
        @Override
        public void onBackPressed() {
            if (System.currentTimeMillis() > backpressedTime + 2000) {
            backpressedTime = System.currentTimeMillis();
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        } else if (System.currentTimeMillis() <= backpressedTime + 2000) {
            finish();
        }
    }
}
