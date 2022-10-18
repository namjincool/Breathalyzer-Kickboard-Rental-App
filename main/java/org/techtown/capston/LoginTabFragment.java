package org.techtown.capston;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginTabFragment extends Fragment {
    private Button login_btn;
    private EditText edit_id, edit_pw;
    double[][] latilong=new double[3][2];
    double []state=new double[3]; // 만약 사용중이면 사용 중 , DB 연결후 재작성
    HashMap<String,String> login_value;
    String key;
    String for_parking;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.login_tab_fragment, container, false);
        login_btn = v.findViewById(R.id.loginBtn);
        edit_id = v.findViewById(R.id.login_ID);
        edit_pw = v.findViewById(R.id.login_PW);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("UserData");//위도 , 경도

        ref.child("model1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Define.lat_lon_first_model=(HashMap)snapshot.getValue();
                latilong[0][0]=Define.lat_lon_first_model.get("lat1");
                latilong[0][1]=Define.lat_lon_first_model.get("lon1");
                state[0]=Define.lat_lon_first_model.get("state");
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
                state[1]=Define.lat_lon_second_model.get("state");
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
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), MainActivity.class);
//                startActivity(intent); // 메인      화면으로 가는거 성공ㅇㅇ

                String userID = edit_id.getText().toString();
                String userPass = edit_pw.getText().toString();
                DatabaseReference loginput=database.getReference(userID);
                loginput.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        login_value=(HashMap)snapshot.getValue();
                        key=login_value.get("isUse");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                DatabaseReference rent_model= database.getReference(userID+"rent");
                rent_model.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        HashMap<String,String> i=(HashMap) snapshot.getValue();
                        for_parking=i.get("isuse");
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                Sleep.Sleep();
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                String userID = jsonObject.getString("userID");
                                String userPass = jsonObject.getString("userPassword");
                                if (key.equals("renting")){
                                    Intent intent = new Intent(getActivity(),Remap.class);
                                    intent.putExtra(Define.retry_latlon[0][0],latilong[0][0]);
                                    intent.putExtra(Define.retry_latlon[0][1],latilong[0][1]);
                                    intent.putExtra(Define.state[0],state[0]);
                                    intent.putExtra(Define.retry_latlon[1][0],latilong[1][0]);
                                    intent.putExtra(Define.retry_latlon[1][1],latilong[1][1]);
                                    intent.putExtra(Define.state[1],state[1]);
                                    intent.putExtra(Define.retry_latlon[2][0],latilong[2][0]);
                                    intent.putExtra(Define.retry_latlon[2][1],latilong[2][1]);
                                    intent.putExtra(Define.state[2],state[2]);
                                    intent.putExtra(Define.renting,key);
                                    intent.putExtra(Define.user, userID);
                                    intent.putExtra(Define.SUFA,for_parking);
                                    startActivity(intent);
                                    getActivity().finish();
                                } else {
                                    loginput.child("isUse").setValue("f_rent");
                                    Toast.makeText(getActivity(), "로그인성공", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    intent.putExtra(Define.inten[0][0], latilong[0][0]);
                                    intent.putExtra(Define.inten[0][1], latilong[0][1]);
                                    intent.putExtra(Define.state[0], state[0]);
                                    intent.putExtra(Define.inten[1][0], latilong[1][0]);
                                    intent.putExtra(Define.inten[1][1], latilong[1][1]);
                                    intent.putExtra(Define.state[1], state[1]);
                                    intent.putExtra(Define.inten[2][0], latilong[2][0]);
                                    intent.putExtra(Define.inten[2][1], latilong[2][1]);
                                    intent.putExtra(Define.state[2], state[2]);
                                    intent.putExtra(Define.user, userID);
                                    //intent.putExtra("userPAss",userPass);
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage("로그인실패").setNegativeButton("확인", null)
                                        .create().show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest(userID,userPass,responseListener);
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                queue.add(loginRequest);
            }
        });
        return v;
    }
}