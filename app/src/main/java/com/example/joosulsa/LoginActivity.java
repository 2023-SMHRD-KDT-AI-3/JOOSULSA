package com.example.joosulsa;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.joosulsa.databinding.ActivityLoginBinding;
import com.example.joosulsa.fragment.HomeFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

    // 서버에 요청 보내려면 필요함
    private RequestQueue queue;

    private String springUrl = "http://192.168.219.62:8089/login";

    int postMethod = Request.Method.POST;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);

        if (queue == null) {
            queue = Volley.newRequestQueue(this);
        }

        // 로그인 버튼 클릭시 발생 이벤트
        binding.btnLogin.setOnClickListener(v -> {

            // 유저 입력 아이디 패스워드 받아오기
            String userID = binding.loginId.getText().toString();
            String userPw = binding.loginPw.getText().toString();

            StringRequest request = new StringRequest(
                    postMethod,
                    springUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("LoginActivity", response);
                            handleLoginResponse(response);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            binding.errorMsg.setText("아이디 또는 비밀번호가 틀렸습니다.");
                        }
                    }
            ){
                @Nullable
                @Override
                // Map<String(key), String(value)>를
                // getParams를 사용해서 key와 value를 login?key=value 형태로 바꿔준다.
                protected Map<String, String> getParams() throws AuthFailureError {
                    // 전송방식을 POST로 지정했을 때 사용하는 메소드
                    // 데이터를 전송할 때 Map 형태로 구성하여 리턴해줘야 한다.
                    Map<String, String> params = new HashMap<>();
                    params.put("userId", userID);
                    params.put("userPw", userPw);
                    Log.d("LoginParams", params.toString());
                    return params;
                }
            };

            queue.add(request);

        });

        
        //회원가입 버튼 클릭시 회원가입 버튼으로 이동.
        binding.loginJoin.setOnClickListener(view->{
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
        // 뒤로가기 버튼 클릭시 홈 화면으로 이동
        binding.loginBack.setOnClickListener(v->{
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
        });
    }

    private void handleLoginResponse(String response) {
        // 서버에서 받아온 응답을 처리하는 로직을 작성
        // response는 서버에서 보낸 JSON 형태의 데이터일 것이므로, 필요에 따라 파싱하여 사용
        try {
            JSONObject jsonResponse = new JSONObject(response);
            // json 파싱하는 부분
            String loginName = jsonResponse.getString("userName");
            String loginId = jsonResponse.getString("userId");
            String loginPw = jsonResponse.getString("userPw");
            String loginAddr = jsonResponse.getString("userAddr");
            String loginNick = jsonResponse.getString("userNick");
            boolean checkBoolean = jsonResponse.getBoolean("attendance");
            boolean quizBoolean = jsonResponse.getBoolean("quizParticipation");
            Log.d("LogInDataCheck", "닉네임: "+loginName + "Id: "+loginId + "Pw : "+ loginPw);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            // intent에 값 집어넣음
            intent.putExtra("userName", loginName);
            intent.putExtra("userId", loginId);
            intent.putExtra("userPw", loginPw);
            intent.putExtra("userAddr", loginAddr);
            intent.putExtra("userNick", loginNick);
            Log.d("오토로그인", "잘가니?");
            // 자동 로그인용 값
            // SharedPreferences에 사용자 정보 저장
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("autoId", loginId);
            editor.putString("autoPw", loginPw);
            editor.putString("autoName", loginName);
            editor.putString("autoAddr", loginAddr);
            editor.putString("autoNick", loginNick);
            editor.putBoolean("checkBoolean", checkBoolean);
            editor.putBoolean("quizBoolean", quizBoolean);
            Log.d("checkAfterInput", loginPw);
            editor.apply();

            startActivity(intent);
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}