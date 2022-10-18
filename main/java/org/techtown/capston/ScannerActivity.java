package org.techtown.capston;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


public class ScannerActivity extends AppCompatActivity {
    private IntentIntegrator qrScan;
    Timer timer = new Timer();
    Loding loding;
    double []alcoal=new double[3];
    double [][]latilong=new double [3][2];
    HashMap<Double,Double> lat_lon_first_model,lat_lon_second_model,lat_lon_third_model;
    final String [] QR_URL = {"https://www.InOut1.com","https://www.InOut2.com","https://www.InOut3.com"};
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("UserData");//위도 , 경도
    DatabaseReference loginout,rent_model;//rent_model DB생성으로 현재 어떤 킥보드를 빌렷는지 구현
    String ID;
    double state[]=new double[3];
    //final String [][] retry_latlon={{"back_lat1","back_lon1"},{"back_lat2","back_lon2"},{"back_lat3","back_lon3"}};
    //final String [][] inten={{"f_lati","f_logi"},{"s_lati","s_logi"},{"t_lati","t_logi"}};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.loading);
        Intent intent = getIntent();
        ID=intent.getStringExtra(Define.user);

        loginout=database.getReference(ID);
        //loginout=database.getReference(ID);
        //intent.getStringExtra(Define.user);
        Scanning();
        loding = new Loding(this);
        loding.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        /*Back키를 눌렀을때 새로고침을 다시 뷰로 가려면 사용해야함 다른 모델도 필요*/
        ref.child("model1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Define.lat_lon_first_model=(HashMap)snapshot.getValue();
                latilong[0][0]=Define.lat_lon_first_model.get("lat1");
                latilong[0][1]=Define.lat_lon_first_model.get("lon1");
                state[0]=Define.lat_lon_third_model.get("state");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ref.child("model2").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Define.lat_lon_second_model=(HashMap)snapshot.getValue();
                latilong[1][0]=Define.lat_lon_second_model.get("lat2");
                latilong[1][1]=Define.lat_lon_second_model.get("lon2");
                state[1]=Define.lat_lon_third_model.get("state");

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ref.child("model3").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Define.lat_lon_third_model=(HashMap)snapshot.getValue();
                latilong[2][0]=Define.lat_lon_third_model.get("lat3");
                latilong[2][1]=Define.lat_lon_third_model.get("lon3");
                state[2]=Define.lat_lon_third_model.get("state");

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Intent intent =new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra(Define.inten[0][0],latilong[0][0]);
                intent.putExtra(Define.inten[0][1],latilong[0][1]);
                intent.putExtra(Define.inten[1][0],latilong[1][0]);
                intent.putExtra(Define.inten[1][1],latilong[1][1]);
                intent.putExtra(Define.inten[2][0],latilong[2][0]);
                intent.putExtra(Define.inten[2][1],latilong[2][1]);
                intent.putExtra(Define.state[0],state[0]);
                intent.putExtra(Define.state[1],state[1]);
                intent.putExtra(Define.state[2],state[2]);
                intent.putExtra(Define.user,ID);
                startActivity(intent);
                finish();
            } else {
                //https://www.InOut3.com 1 2
                //1번모델을 대여했을때
                if (result.getContents().equals(QR_URL[0])) { //1
                    loding.show();
                    loding.setCanceledOnTouchOutside(false);//주변터치 방지
                    loding.setCancelable(false);//뒤로가기 방지Toast.makeText(this, "대여성공", Toast.LENGTH_LONG).show();
                    ref.child("model1").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Define.lat_lon_first_model=(HashMap)snapshot.getValue();
                            latilong[0][0]=Define.lat_lon_first_model.get("lat1");
                            latilong[0][1]=Define.lat_lon_first_model.get("lon1");
                            alcoal[0]=Define.lat_lon_first_model.get("alco1");
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    TimerTask Do = new TimerTask() {
                        @Override
                        public void run() {
                            if(alcoal[0]<110){
                                loding.dismiss();
                                finish();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra(Define.inten[0][0],latilong[0][0]);
                                intent.putExtra(Define.inten[0][1],latilong[0][1]);
                                intent.putExtra(Define.inten[1][0],latilong[1][0]);
                                intent.putExtra(Define.inten[1][1],latilong[1][1]);
                                intent.putExtra(Define.inten[2][0],latilong[2][0]);
                                intent.putExtra(Define.inten[2][1],latilong[2][1]);
                                intent.putExtra(Define.state[0],state[0]);
                                intent.putExtra(Define.state[1],state[1]);
                                intent.putExtra(Define.state[2],state[2]);
                                intent.putExtra(Define.error,1);
                                intent.putExtra(Define.user,ID);
                                //나머지 두개의 모델도 넣어줘야함
                                startActivity(intent);
                            } else if(110<=alcoal[0]&&alcoal[0]<400){
                                ref.child("model1/state").setValue(0.5);
                                rent_model= database.getReference(ID+"rent");
                                rent_model.child("isuse").setValue("using1");
                                //Toast.makeText(getApplicationContext(), Successs[0]+"대여성공", Toast.LENGTH_LONG).show();
                                loding.dismiss();
                                finish();
                                Intent intent = new Intent(getApplicationContext(), Remap.class);
                                intent.putExtra(Define.retry_latlon[0][0],latilong[0][0]);
                                intent.putExtra(Define.retry_latlon[0][1],latilong[0][1]);
                                intent.putExtra(Define.retry_latlon[1][0],latilong[1][0]);
                                intent.putExtra(Define.retry_latlon[1][1],latilong[1][1]);
                                intent.putExtra(Define.retry_latlon[2][0],latilong[2][0]);
                                intent.putExtra(Define.retry_latlon[2][1],latilong[2][1]);
                                intent.putExtra(Define.state[0],0.5); //0.5로 어차피 받아짐
                                intent.putExtra(Define.state[1],state[1]);
                                intent.putExtra(Define.state[2],state[2]);
                                intent.putExtra(Define.SUFA,"using1");
                                //intent.putExtra(Define.renting,0);
                                intent.putExtra(Define.renting,"f_rent");
                                intent.putExtra(Define.user,ID);
                                //나머지 두개의 모델도 넣어줘야함
                                loginout.child("isUse").setValue("renting");
                                startActivity(intent);
                                //timer.schedule(Succ, 3000);
                            } else {
                                //Toast.makeText(getApplicationContext(), "한 번만 다시 불어주세요!", Toast.LENGTH_LONG).show();
                                loding.dismiss();
                                finish();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra(Define.inten[0][0],latilong[0][0]);
                                intent.putExtra(Define.inten[0][1],latilong[0][1]);
                                intent.putExtra(Define.inten[1][0],latilong[1][0]);
                                intent.putExtra(Define.inten[1][1],latilong[1][1]);
                                intent.putExtra(Define.inten[2][0],latilong[2][0]);
                                intent.putExtra(Define.inten[2][1],latilong[2][1]);
                                intent.putExtra(Define.state[0],state[0]);
                                intent.putExtra(Define.state[1],state[1]);
                                intent.putExtra(Define.state[2],state[2]);
                                intent.putExtra(Define.error,2);
                                intent.putExtra(Define.user,ID);
                                //나머지 두개의 모델도 넣어줘야함
                                startActivity(intent);
                            }
                        }
                    };
                    timer.schedule(Do,5000);
                    // 110보다 작고 , 110보다 크면서 400보다 작고, 400보다 크면
                } else if (result.getContents().equals(QR_URL[1])) { //1
                    loding.show();
                    loding.setCanceledOnTouchOutside(false);//주변터치 방지
                    loding.setCancelable(false);//뒤로가기 방지Toast.makeText(this, "대여성공", Toast.LENGTH_LONG).show();
                    ref.child("model2").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Define.lat_lon_second_model=(HashMap)snapshot.getValue();
                            latilong[1][0]=Define.lat_lon_second_model.get("lat2");
                            latilong[1][1]=Define.lat_lon_second_model.get("lon2");
                            alcoal[1]=Define.lat_lon_second_model.get("alco2");
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    TimerTask Do = new TimerTask() {
                        @Override
                        public void run() {
                            if(alcoal[1]<110){
                                loding.dismiss();
                                finish();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra(Define.inten[0][0],latilong[0][0]);
                                intent.putExtra(Define.inten[0][1],latilong[0][1]);
                                intent.putExtra(Define.inten[1][0],latilong[1][0]);
                                intent.putExtra(Define.inten[1][1],latilong[1][1]);
                                intent.putExtra(Define.inten[2][0],latilong[2][0]);
                                intent.putExtra(Define.inten[2][1],latilong[2][1]);
                                intent.putExtra(Define.state[0],state[0]);
                                intent.putExtra(Define.state[1],state[1]);
                                intent.putExtra(Define.state[2],state[2]);
                                intent.putExtra(Define.error,1);
                                intent.putExtra(Define.user,ID);
                                //나머지 두개의 모델도 넣어줘야함
                                startActivity(intent);
                            } else if(110<=alcoal[1]&&alcoal[1]<400){
                                ref.child("model2/state").setValue(0.5);
                                rent_model= database.getReference(ID+"rent");
                                rent_model.child("isuse").setValue("using2");
                                //Toast.makeText(getApplicationContext(), Successs[0]+"대여성공", Toast.LENGTH_LONG).show();
                                loding.dismiss();
                                finish();
                                Intent intent = new Intent(getApplicationContext(), Remap.class);
                                intent.putExtra(Define.retry_latlon[0][0],latilong[0][0]);
                                intent.putExtra(Define.retry_latlon[0][1],latilong[0][1]);
                                intent.putExtra(Define.retry_latlon[1][0],latilong[1][0]);
                                intent.putExtra(Define.retry_latlon[1][1],latilong[1][1]);
                                intent.putExtra(Define.retry_latlon[2][0],latilong[2][0]);
                                intent.putExtra(Define.retry_latlon[2][1],latilong[2][1]);
                                intent.putExtra(Define.state[1],0.5); //0.5로 어차피 받아짐
                                intent.putExtra(Define.state[0],state[0]);
                                intent.putExtra(Define.state[2],state[2]);
                                intent.putExtra(Define.SUFA,"using2");
                                //intent.putExtra(Define.renting,0);
                                intent.putExtra(Define.renting,"f_rent");
                                intent.putExtra(Define.user,ID);
                                //나머지 두개의 모델도 넣어줘야함
                                loginout.child("isUse").setValue("renting");
                                startActivity(intent);
                                //timer.schedule(Succ, 3000);
                            } else {
                                loding.dismiss();
                                finish();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra(Define.inten[0][0],latilong[0][0]);
                                intent.putExtra(Define.inten[0][1],latilong[0][1]);
                                intent.putExtra(Define.inten[1][0],latilong[1][0]);
                                intent.putExtra(Define.inten[1][1],latilong[1][1]);
                                intent.putExtra(Define.inten[2][0],latilong[2][0]);
                                intent.putExtra(Define.inten[2][1],latilong[2][1]);
                                intent.putExtra(Define.state[0],state[0]);
                                intent.putExtra(Define.state[1],state[1]);
                                intent.putExtra(Define.state[2],state[2]);
                                intent.putExtra(Define.error,2);
                                intent.putExtra(Define.user,ID);
                                //나머지 두개의 모델도 넣어줘야함
                                startActivity(intent);
                            }
                        }
                    };
                    timer.schedule(Do,5000);
                    // 110보다 작고 , 110보다 크면서 400보다 작고, 400보다 크면ㄴ
                } else if (result.getContents().equals(QR_URL[2])) { //1
                    loding.show();
                    loding.setCanceledOnTouchOutside(false);//주변터치 방지
                    loding.setCancelable(false);//뒤로가기 방지Toast.makeText(this, "대여성공", Toast.LENGTH_LONG).show();
                    ref.child("model3").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Define.lat_lon_third_model=(HashMap)snapshot.getValue();
                            latilong[2][0]=Define.lat_lon_third_model.get("lat3");
                            latilong[2][1]=Define.lat_lon_third_model.get("lon3");
                            alcoal[2]=Define.lat_lon_third_model.get("alco3");
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    TimerTask Do = new TimerTask() {
                        @Override
                        public void run() {
                            if(alcoal[2]<110){
                                loding.dismiss();
                                finish();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra(Define.inten[0][0],latilong[0][0]);
                                intent.putExtra(Define.inten[0][1],latilong[0][1]);
                                intent.putExtra(Define.inten[1][0],latilong[1][0]);
                                intent.putExtra(Define.inten[1][1],latilong[1][1]);
                                intent.putExtra(Define.inten[2][0],latilong[2][0]);
                                intent.putExtra(Define.inten[2][1],latilong[2][1]);
                                intent.putExtra(Define.error,1);
                                intent.putExtra(Define.user,ID);
                                intent.putExtra(Define.state[0],state[0]);
                                intent.putExtra(Define.state[1],state[1]);
                                intent.putExtra(Define.state[2],state[2]);
                                //나머지 두개의 모델도 넣어줘야함
                                startActivity(intent);
                            } else if(110<=alcoal[2]&&alcoal[0]<400){
                                ref.child("model3/state").setValue(0.5);
                                rent_model= database.getReference(ID+"rent");
                                rent_model.child("isuse").setValue("using3");
                                loding.dismiss();
                                finish();
                                Intent intent = new Intent(getApplicationContext(), Remap.class);
                                intent.putExtra(Define.retry_latlon[0][0],latilong[0][0]);
                                intent.putExtra(Define.retry_latlon[0][1],latilong[0][1]);
                                intent.putExtra(Define.retry_latlon[1][0],latilong[1][0]);
                                intent.putExtra(Define.retry_latlon[1][1],latilong[1][1]);
                                intent.putExtra(Define.retry_latlon[2][0],latilong[2][0]);
                                intent.putExtra(Define.retry_latlon[2][1],latilong[2][1]);
                                intent.putExtra(Define.state[2],0.5); //0.5로 어차피 받아짐
                                intent.putExtra(Define.SUFA,"using3");
                                intent.putExtra(Define.renting,"f_rent");
                                intent.putExtra(Define.user,ID);
                                intent.putExtra(Define.state[0],state[0]);
                                intent.putExtra(Define.state[1],state[1]);
                                //나머지 두개의 모델도 넣어줘야함
                                loginout.child("isUse").setValue("renting");
                                startActivity(intent);
                                //timer.schedule(Succ, 3000);
                            } else {
                                //Toast.makeText(getApplicationContext(), "한 번만 다시 불어주세요!", Toast.LENGTH_LONG).show();
                                loding.dismiss();
                                finish();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra(Define.inten[0][0],latilong[0][0]);
                                intent.putExtra(Define.inten[0][1],latilong[0][1]);
                                intent.putExtra(Define.inten[1][0],latilong[1][0]);
                                intent.putExtra(Define.inten[1][1],latilong[1][1]);
                                intent.putExtra(Define.inten[2][0],latilong[2][0]);
                                intent.putExtra(Define.inten[2][1],latilong[2][1]);
                                intent.putExtra(Define.state[0],state[0]);
                                intent.putExtra(Define.state[1],state[1]);
                                intent.putExtra(Define.state[2],state[2]);
                                intent.putExtra(Define.error,2);
                                intent.putExtra(Define.user,ID);
                                //나머지 두개의 모델도 넣어줘야함
                                startActivity(intent);
                            }
                        }
                    };
                    timer.schedule(Do,5000);
                    // 110보다 작고 , 110보다 크면서 400보다 작고, 400보다 크면
                }else { // x
                    //super.onActivityResult(requestCode, resultCode, data);
                    Scanning();
                    Toast.makeText(this, "없는 킥보드 모델 입니다 다시 QR을 스캔해 주세요!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
            public void Scanning(){
                qrScan = new IntentIntegrator(this);
                qrScan.setCaptureActivity(CaptureForm.class);
                qrScan.setOrientationLocked(false); // default가 세로모드인데 휴대폰 방향에 따라 가로, 세로로 자동 변경됩니다.
                qrScan.initiateScan();
            }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}