package com.example.joosulsa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.joosulsa.databinding.ActivityMyPageEditBinding;

import java.util.HashMap;
import java.util.Map;

public class MyPageEditActivity extends AppCompatActivity {

    private ActivityMyPageEditBinding binding;

    // SharedPreferences(셰어드 프레퍼런시즈) 객체 생성
    // SharedPreferences(세어드 프레퍼런시즈) : 안드로이드 내에 저장하는 세션이라고 생각하셈
    private SharedPreferences spf;

    private RequestQueue queue;

    // url 주소
    private String springUrl = "http://192.168.219.62:8089/myChange";


    // post
    int postMethod = Request.Method.POST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyPageEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());




        // 서버통신할거면 무조건 있어야댐
        if (queue == null) {
            queue = Volley.newRequestQueue(this);
        }

        // 페이지 이동

        // 뒤로가기 이벤트
        binding.myBack.setOnClickListener(v -> {
            finish();
        });

        // 변경완료 이벤트
        binding.myChangeOK.setOnClickListener(v -> {
            // 비밀번호, 닉네임, 주소 가져오기
            String myId = binding.myId.getText().toString();
            String newPw = binding.newPwCheck.getText().toString();
            String newNick = binding.myNick.getText().toString();
            String newAddr = binding.myAddress.getText().toString();
            Log.d("가냐?", myId + newPw + newNick + newAddr);
            // 수정할 갑 설정
            String checkPw = binding.newPwCheck.getText().toString();
            String checkNick = binding.myNick.getText().toString();
            String checkAddr = binding.myAddress.getText().toString();

            // SharedPreferences.Editor 객체 초기화
            // SharedPreferences.Editor(세어드 프레퍼런시즈 에데터) 객체 생성
            // Editor(세어드 프레퍼런시즈 에데터) : 저장된거 수정하게 해주는거
            SharedPreferences.Editor editor = spf.edit();

            // 변경사항 저장
            editor.putString("autoPw", checkPw);
            editor.putString("autoNick", checkNick);
            editor.putString("autoAddr", checkAddr);
            Log.d("확인해볼까?",checkPw);
            editor.apply();


            if (newPw == null) {
                Log.d("editerror", "adsdasd");
            } else {
                StringRequest request = new StringRequest(
                        postMethod,
                        springUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // 서버 응답 처리
                                Log.d("서버", response);
                                // 서버로부터 응당 처리 메소드 호출

                            }
                        },
                        // 에러
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("오류", "안간다.");
                            }
                        }
                ) {
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        // 전송할 데이터 설정
                        Map<String, String> params = new HashMap<>();
                        params.put("id", myId);
                        params.put("newPw", newPw);
                        params.put("newNick", newNick);
                        params.put("newAddr", newAddr);
                        Log.d("MyPageEditActivity", params.toString());
                        // 조회수 추가
                        // 포인트 추가
                        return params;
                    }
                };
                // 해줘야댐.
                queue.add(request);

                // 마이페이지로 이동
                finish();
            }
            // 데이터 보내기

        });


        //현재 비밀번호 일치여부 확인하기
        binding.myPwCheck.addTextChangedListener(new TextWatcher() {
            // int start = 시작전
            // int count = 과정
            // int after = 결과
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 변경 시작점, 변경 과정, 변경 결과
                // 텍스트가 변경되기 전 이벤트
                binding.pwNm.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 변경 시작점, 변경 과정, 변경 결과
                // 텍스트 변화가 있을 경우
                checkPasswordMatch(); // 현재 비밀번호 일치 여부 메소드 호출
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 텍스트 변경후 이벤트
            }
        });

        // 새로운 비밀번호 일치 여부 확인
        binding.newPwCheck.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 시작전
                binding.newPwNm.setVisibility(View.GONE); // 텍스트 숨김
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 과정
                newPwCheck(); // 새로운 비밀번호 일치여부 메소드 호출
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        // 수정하기 버튼 클릭 이벤트
        binding.btnMyPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnMyPw.setVisibility(View.GONE);
                // 수정하기 버튼 안보이게 비활성화
                binding.btnMyPw2.setVisibility(View.VISIBLE);
                // 숨김 버튼 보이게 활성화
                binding.pwChange.setVisibility(View.VISIBLE);
                // 수정하는 레이아웃 활성화
                binding.newPw.setText(null);
                binding.newPwCheck.setText(null);
                // 새로운 비밀번호 재입력 비우기

            }
        });
        // 숨김 버튼 클릭 이벤트
        binding.btnMyPw2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnMyPw.setVisibility(View.VISIBLE);
                // 수정하기 버튼 보이게 활성화
                binding.btnMyPw2.setVisibility(View.GONE);
                // 숨김 버튼 안보이게 비활성화
                binding.pwChange.setVisibility(View.GONE);
                // 수정하는 레이아웃 비활성화
                binding.newPw.setText(binding.myPw.getText().toString());
                binding.newPwCheck.setText(binding.myPw.getText().toString());
                // 새로운 비밀번호입력에 기존 비밀번호 채워주기
            }
        });

        // 데이터 가져오기
        // autoLogin 변수에 접근 하기
        spf = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
        // 데이터 불러오기

        String myId = spf.getString("autoId", null);
        String myPw = spf.getString("autoPw", null);
        String myName = spf.getString("autoName", null);
        String myAddr = spf.getString("autoAddr", null);
        String myNick = spf.getString("autoNick", null);
        Log.d("확인id",myId);
        Log.d("확인pw",myPw);
        Log.d("확인name",myName);
        Log.d("확인nick",myNick);

        // 불러온 데이터 삽입.
        binding.myId.setText(myId);
        binding.myPw.setText(myPw);
        binding.myName.setText(myName);
        binding.myNick.setText(myNick);
        binding.myAddress.setText(myAddr);

        // 새로운 비밀번호 입력에 기존 비밀번호 채워주기
        binding.newPw.setText(myPw);
        binding.newPwCheck.setText(myPw);


    }

    // 현재 비밀번호 확인여부 메소드
    private void checkPasswordMatch() {
        String currentPassword = binding.myPw.getText().toString();
        String enteredPassword = binding.myPwCheck.getText().toString();

        if (currentPassword.equals(enteredPassword)) {
            binding.pwNm.setVisibility(View.INVISIBLE); // 텍스트 숨김
            binding.myChangeOK.setVisibility(View.VISIBLE); // 완료버튼 출력
            binding.myChangeNo.setVisibility(View.GONE);    // 비활성화 숨김
        } else {
            binding.pwNm.setVisibility(View.VISIBLE); // 텍스트 출력
            binding.myChangeOK.setVisibility(View.GONE); // 완료버튼 숨김
            binding.myChangeNo.setVisibility(View.VISIBLE);// 비활성화 출력
        }
    }

    // 새로운 비밀번호 일치 여부 확인 메소드
    private void newPwCheck() {
        String newpw = binding.newPw.getText().toString();
        String newpwcheck = binding.newPwCheck.getText().toString();

        if (newpw.equals(newpwcheck)) {
            binding.newPwNm.setVisibility(View.INVISIBLE); // 텍스트 숨김
            binding.myChangeOK.setVisibility(View.VISIBLE); // 완료버튼 출력
            binding.myChangeNo.setVisibility(View.GONE);    // 비활성화 숨김

        } else {
            binding.newPwNm.setVisibility(View.VISIBLE); // 텍스트 출력
            binding.myChangeOK.setVisibility(View.GONE); // 완료버튼 숨김
            binding.myChangeNo.setVisibility(View.VISIBLE);// 비활성화 출력
        }
    }
}