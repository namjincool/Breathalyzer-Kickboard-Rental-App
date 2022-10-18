package org.techtown.capston;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {
    /*DB 연결후 주석을 다시 달도록 하겠습니다!*/
    final static private String URL ="http://kappa0908.ivyro.net/Login.php";

    private Map<String , String> map;

    public LoginRequest(String userID, String userPassword, Response.Listener<String> listener)
    {
        super(Method.POST,URL,listener, null);

        map = new HashMap<>();
        map.put("userID",userID);
        map.put("userPassword",userPassword);


    }
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
