package org.techtown.capston;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class PopupActivity extends Activity {
    //HashMap<Double,Double> lat_lon_first_model,lat_lon_second_model,lat_lon_third_model;
   // final String [][] inten={{"f_lati","f_logi"},{"s_lati","s_logi"},{"t_lati","t_logi"}};
    double[][] latLon=new double[3][2];
    double [] state = new double[3];
    TextView txtText;
    Button pay;
    long hour,min,second;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_activity);

        //UI 객체생성
        txtText = (TextView)findViewById(R.id.txtText);

        //데이터 가져오기
        Intent intent = getIntent();
        long data = intent.getLongExtra("date",0);
        String check=intent.getStringExtra(Define.check);
        //Remap에서 id값받아와서 setvalue 지정해주기
        hour=data/3600;
        min=data/60-hour*60;
        second=data%60;
        txtText.setText("현재 사용하신 시간은 "+hour+" 시간"+min+" 분"+second+" 초 입니다");
        pay=findViewById(R.id.pay_btn);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("UserData");//위도 , 경도
        DatabaseReference isUse=database.getReference(intent.getStringExtra(Define.user));
        DatabaseReference not_use = database.getReference(intent.getStringExtra(Define.user)+"rent");

        ref.child("model1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Define.lat_lon_first_model = (HashMap)snapshot.getValue();
                latLon[0][0]=Define.lat_lon_first_model.get("lat1");
                latLon[0][1]=Define.lat_lon_first_model.get("lon1");
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
                //Log.v("1번",String.valueOf(lat_lon_first_model));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //DatabaseReference loginout= database.getReference(intent.getStringExtra(Define.user));

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //위도 경도 차이 조건 0.00008,0.00012
                if (check.equals("using1") && Math.abs(Define.latitude-latLon[0][0])<0.00008 && Math.abs(Define.longitude-latLon[0][1])<0.00012) {
                    //현재위치가 주차가능한 구역이면
                    ref.child("model1/state").setValue(0.1);
                    Toast.makeText(getApplicationContext(), "반납이 완료되었습니다!", Toast.LENGTH_LONG).show();
                    Intent intent1=new Intent(PopupActivity.this,MainActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    //위에 줄을 실행하면 이전에 있던 태스크를 모두 지울수 있다 ..
                    intent1.putExtra(Define.inten[0][0],latLon[0][0]);
                    intent1.putExtra(Define.inten[0][1],latLon[0][1]);
                    intent1.putExtra(Define.inten[1][0],latLon[1][0]);
                    intent1.putExtra(Define.inten[1][1],latLon[1][1]);
                    intent1.putExtra(Define.inten[2][0],latLon[2][0]);
                    intent1.putExtra(Define.inten[2][1],latLon[2][1]);
                    isUse.child("isUse").setValue("f_rent");
                    not_use.child("isuse").setValue("not_use");
                    startActivity(intent1);
                } else if (check.equals("using2")  && Math.abs(Define.latitude-latLon[1][0])<0.00008 && Math.abs(Define.longitude-latLon[1][1])<0.00012) {
                    //현재위치가 주차가능한 구역이면
                    ref.child("model2/state").setValue(0.1);
                    Toast.makeText(getApplicationContext(), "반납이 완료되었습니다!", Toast.LENGTH_LONG).show();
                    Intent intent1=new Intent(PopupActivity.this,MainActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    //위에 줄을 실행하면 이전에 있던 태스크를 모두 지울수 있다 ..
                    intent1.putExtra(Define.inten[0][0],latLon[0][0]);
                    intent1.putExtra(Define.inten[0][1],latLon[0][1]);
                    intent1.putExtra(Define.inten[1][0],latLon[1][0]);
                    intent1.putExtra(Define.inten[1][1],latLon[1][1]);
                    intent1.putExtra(Define.inten[2][0],latLon[2][0]);
                    intent1.putExtra(Define.inten[2][1],latLon[2][1]);
                    isUse.child("isUse").setValue("f_rent");
                    not_use.child("isuse").setValue("not_use");
                    startActivity(intent1);
                } else if (check.equals("using3") && Math.abs(Define.latitude-latLon[2][0])<0.00008 && Math.abs(Define.longitude-latLon[2][1])<0.00012) {
                    //현재위치가 주차가능한 구역이면
                    ref.child("model3/state").setValue(0.1);
                    Toast.makeText(getApplicationContext(), "반납이 완료되었습니다!", Toast.LENGTH_LONG).show();
                    Intent intent1=new Intent(PopupActivity.this,MainActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    //위에 줄을 실행하면 이전에 있던 태스크를 모두 지울수 있다 ..
                    intent1.putExtra(Define.inten[0][0],latLon[0][0]);
                    intent1.putExtra(Define.inten[0][1],latLon[0][1]);
                    intent1.putExtra(Define.inten[1][0],latLon[1][0]);
                    intent1.putExtra(Define.inten[1][1],latLon[1][1]);
                    intent1.putExtra(Define.inten[2][0],latLon[2][0]);
                    intent1.putExtra(Define.inten[2][1],latLon[2][1]);
                    isUse.child("isUse").setValue("f_rent");
                    not_use.child("isuse").setValue("not_use");
                    startActivity(intent1);
                } else {
                    Toast.makeText(getApplicationContext(), "현재 위치는 반납이 불가능합니다!", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

}
