package org.techtown.capston;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpTabFragment extends Fragment{
    private EditText edit_suID,edit_suPW,edit_suName,edit_suAge;
    private Button signup_Btn;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.signup_fragment, container, false);

        edit_suID = v.findViewById(R.id.signup_ID);
        edit_suPW = v.findViewById(R.id.signup_PW);
        edit_suName = v.findViewById(R.id.signup_NAME);
        edit_suAge = v.findViewById(R.id.signup_AGE);

        signup_Btn = v.findViewById(R.id.signupBtn);
        signup_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = edit_suID.getText().toString();
                String userPass = edit_suPW.getText().toString();
                String userName = edit_suName.getText().toString();
                String userAge = edit_suAge.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if(success)
                            {
                                DatabaseReference ref = database.getReference(userID);//위도 , 경도
                                DatabaseReference ref2 = database.getReference(userID+"rent");
                                ref.child("isUse").setValue("f_rent");
                                ref2.child("isuse").setValue("not_use");
                                Toast.makeText(getActivity(),"회원등록완료",Toast.LENGTH_SHORT).show();
                                //getActivity().finish();
                            } else {
                                Toast.makeText(getActivity(),"회원등록 실패",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                RegisterRequest registerRequest = new RegisterRequest(userID,userPass,userName,userAge,responseListener);
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                queue.add(registerRequest);
            }
        });

        return v;
    }
}
