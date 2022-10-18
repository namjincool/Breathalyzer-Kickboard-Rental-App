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
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;


public class Remap extends AppCompatActivity implements OnMapReadyCallback {
    //메인 뷰와 같지만 버튼이 다른 뷰
    Timer timer = new Timer();
    double []state=new double[3];
    String []current_state=new String[3];
    private double lat;
    private double lon;
    private GoogleMap mMap;
    int count;
    Button Return;
    final int CAMERAZOOM = 19;
    //String state; // 만약 사용중이면 사용 중 ,
    private long backpressedTime = 0;
    double[][] latLon=new double[3][2];
    String renting;
    Loding loding;
    final LatLng Parking= new LatLng(Define.latitude,Define.longitude);
    //final String retry_latlon [][]={{"back_lat1","back_lon1"},{"back_lat2","back_lon2"},{"back_lat3","back_lon3"}};
    //위에 상수 배열은 메인뷰와 다르게 인텐트로 넘기기위해 변수로 지정을 한 것
    Date current_Time,next_Time;
    SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
    long riding;
    String ID;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    HashMap<String,String> value;
    double [][]new_latLon=new double [3][2];
    Button refresh2;
    String current_Rent, check;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remap);
        //시간 계산은 프로그램의 지속된 실행을 방지하고자 빌린 시점 과 반납시점을 1변수로 저장해서 계산해서 구현을 하였습니다
        Intent intent=getIntent();
        ID=intent.getStringExtra(Define.user);

        if (intent.getStringExtra(Define.SUFA).equals("using1")){
            current_Rent="using1";
        } else if (intent.getStringExtra(Define.SUFA).equals("using2")){
            current_Rent="using2";
        } else if (intent.getStringExtra(Define.SUFA).equals("using3")){
            current_Rent="using3";
        }
        DatabaseReference First_Use=database.getReference(ID);
        try {
            current_Time=format.parse(getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        renting=(intent.getStringExtra(Define.renting));
        if (renting.equals("renting")){
            Toast.makeText(getApplicationContext(),"현재 대여중입니다!",Toast.LENGTH_LONG).show();
            First_Use.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    value=(HashMap) snapshot.getValue();
                    try {
                        current_Time=format.parse(value.get("time"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //Log.v("dd",String.valueOf(current_Time));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else if(renting.equals("f_rent")){
            Toast.makeText(getApplicationContext(),"대여가 성공하였습니다!",Toast.LENGTH_LONG).show();
            First_Use.child("time").setValue(getTime());
        }
        latLon[0][0]=intent.getDoubleExtra(Define.retry_latlon[0][0],0);
        latLon[0][1]=intent.getDoubleExtra(Define.retry_latlon[0][1],0);
        state[0]=intent.getDoubleExtra(Define.state[0],0);
        latLon[1][0]=intent.getDoubleExtra(Define.retry_latlon[1][0],0);
        latLon[1][1]=intent.getDoubleExtra(Define.retry_latlon[1][1],0);
        state[1]=intent.getDoubleExtra(Define.state[1],0);
        latLon[2][0]=intent.getDoubleExtra(Define.retry_latlon[2][0],0);
        latLon[2][1]=intent.getDoubleExtra(Define.retry_latlon[2][1],0);
        state[2]=intent.getDoubleExtra(Define.state[2],0);
        current_state[0]=state[0]==0.1?"대여가 가능합니다":"현재 사용 중 입니다";
        current_state[1]=state[1]==0.1?"대여가 가능합니다":"현재 사용 중 입니다";
        current_state[2]=state[2]==0.1?"대여가 가능합니다":"현재 사용 중 입니다";
        //나머지 모델도 다 받아야 합니다!!
        loding = new Loding(this);
        loding.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Return=findViewById(R.id.Return);// 반납하기
        Return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(Remap.this,PopupActivity.class);
                try {
                    next_Time = format.parse(getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                riding=(next_Time.getTime()-current_Time.getTime())/1000;//초 단위로 측정가능해짐
                intent1.putExtra("date", riding);
                intent1.putExtra(Define.user,ID);
                intent1.putExtra(Define.check,current_Rent);
                startActivity(intent1);
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        refresh2=findViewById(R.id.refresh2);
        refresh2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                DatabaseReference ref = database.getReference("UserData");//위도 , 경도
                ref.child("model1").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Define.lat_lon_first_model = (HashMap)snapshot.getValue();
                        latLon[0][0]=Define.lat_lon_first_model.get("lat1");
                        latLon[0][1]=Define.lat_lon_first_model.get("lon1");
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
                        latLon[1][0]=Define.lat_lon_second_model.get("lat2");
                        latLon[1][1]=Define.lat_lon_second_model.get("lon2");
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
                        latLon[2][0]=Define.lat_lon_third_model.get("lat3");
                        latLon[2][1]=Define.lat_lon_third_model.get("lon3");
                        state[2]=Define.lat_lon_third_model.get("state");
                        //Log.v("1번",String.valueOf(lat_lon_first_model));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
                LatLng Locate1 = new LatLng(latLon[0][0],latLon[0][1]);
                LatLng Locate2 = new LatLng(latLon[1][0],latLon[1][1]);
                LatLng Locate3 = new LatLng(latLon[2][0],latLon[2][1]);
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
                if (current_Rent.equals("using1")) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(Locate1)); //카메라 이동
                } else if (current_Rent.equals("using2")){
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(Locate2)); //카메라 이동
                } else if (current_Rent.equals("using3")){
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(Locate3)); //카메라 이동
                }
                mMap.moveCamera(CameraUpdateFactory.zoomTo(CAMERAZOOM)); // 카메라 이동 후 줌인
            }
        });
    }
    //아래 코드는 마커만 표시를 하게 하자 현재 위치
    public void onMapReady (final GoogleMap googleMap){
        mMap = googleMap;
        //아래 위치는 DB에서 가지고와야함

        LatLng Locate1 = new LatLng(latLon[0][0],latLon[0][1]);
        LatLng Locate2 = new LatLng(latLon[1][0],latLon[1][1]);
        LatLng Locate3 = new LatLng(latLon[2][0],latLon[2][1]);
        BitmapDrawable drawable=(BitmapDrawable)getResources().getDrawable(R.drawable.parking);
        Bitmap park=drawable.getBitmap();
        Bitmap park_mark=Bitmap.createScaledBitmap(park,100,100,false);
        mMap.addMarker(new MarkerOptions().position(Parking).title("주차 공간").snippet("주차공간입니다").icon(BitmapDescriptorFactory.fromBitmap(park_mark)));
        mMap.addCircle(new CircleOptions().center(Parking).radius(11).strokeColor(Color.parseColor("#ffffff")).fillColor(Color.parseColor("#5587cefa")));
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
        if (current_Rent.equals("using1")) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(Locate1)); //카메라 이동
        } else if (current_Rent.equals("using2")){
            mMap.moveCamera(CameraUpdateFactory.newLatLng(Locate2)); //카메라 이동
        } else if (current_Rent.equals("using3")){
            mMap.moveCamera(CameraUpdateFactory.newLatLng(Locate3)); //카메라 이동
        }
        mMap.moveCamera(CameraUpdateFactory.zoomTo(CAMERAZOOM)); // 카메라 이동 후 줌인
        //mMap.addMarker(new MarkerOptions().position(Locate2).title(lat_lon.model[1]).snippet(state).icon(BitmapDescriptorFactory.fromBitmap(marker)));
        //mMap.addMarker(new MarkerOptions().position(Locate3).title(lat_lon.model[2]).snippet(state).icon(BitmapDescriptorFactory.fromBitmap(marker)));
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
                                ActivityCompat.requestPermissions(Remap.this, new String[]{
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
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backpressedTime + 2000) {
            backpressedTime = System.currentTimeMillis();
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        } else if (System.currentTimeMillis() <= backpressedTime + 2000) {
            finish();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getTime(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime localTime = LocalTime.now();
        return dtf.format(localTime);
    }

}
