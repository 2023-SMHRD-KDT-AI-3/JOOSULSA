package com.example.joosulsa;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.joosulsa.databinding.ActivitySearchBinding;
import com.example.joosulsa.databinding.ActivitySearchDetailBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    private ActivitySearchBinding binding;
    // url 주소
    private String springUrl = "http://192.168.219.62:8089/search";
    private String popSearchUrl = "http://192.168.219.62:8089/popData";
    private String popRecyUrl = "http://192.168.219.62:8089/popRecy";

    // post
    int postMethod = Request.Method.POST;

    private RequestQueue requestQueue;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(SearchActivity.this);
        }

        // 인기검색어 가져오기
        popSearch();

        // 버튼 이벤트 처리

        // 뒤로가기
        binding.searchBack.setOnClickListener(v -> {
            finish();
        });
        // 검색 버튼 클릭 시 이벤트
        binding.searchImg.setOnClickListener(v -> {

        });
        // 엔터 이벤트
        binding.textSearch.setOnEditorActionListener((textView, actionId, keyEvent) -> {

            if (actionId == EditorInfo.IME_ACTION_SEARCH || (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                // 검색 이벤트 처리
                Log.d("엔터", "된다");
                performSearch();
                return true; // 이벤트 처리 완료
            }
            return false; // 이벤트 처리하지 않음
        });

        binding.popSearch1.setOnClickListener(v -> {
            String data = binding.popSearch1.getText().toString();
            Log.d("acvpsozxckjvandpc", data);
            popDataRecySend(data);
        });

        binding.popSearch2.setOnClickListener(v -> {
            String data = binding.popSearch2.getText().toString();
            Log.d("acvpsozxckjvandpc", data);
            popDataRecySend(data);
        });

        binding.popSearch3.setOnClickListener(v -> {
            String data = binding.popSearch3.getText().toString();
            Log.d("acvpsozxckjvandpc", data);
            popDataRecySend(data);
        });
        binding.popSearch4.setOnClickListener(v -> {
            String data = binding.popSearch4.getText().toString();
            Log.d("acvpsozxckjvandpc", data);
            popDataRecySend(data);
        });
        binding.popSearch5.setOnClickListener(v -> {
            String data = binding.popSearch5.getText().toString();
            Log.d("acvpsozxckjvandpc", data);
            popDataRecySend(data);
        });
        binding.popSearch6.setOnClickListener(v -> {
            String data = binding.popSearch6.getText().toString();
            Log.d("acvpsozxckjvandpc", data);
            popDataRecySend(data);
        });
        binding.popSearch7.setOnClickListener(v -> {
            String data = binding.popSearch7.getText().toString();
            Log.d("acvpsozxckjvandpc", data);
            popDataRecySend(data);
        });

    }

    // 인기검색어 가져오기
    private void popSearch(){

        StringRequest request = new StringRequest(
                postMethod,
                popSearchUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("popSearchData", response);
                        handlePopData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }
        };
        requestQueue.add(request);

    }

    // 인기검색어 추출해서 화면에 뿌려주기
    private void handlePopData(String response){

        try {
            JSONArray jsonArray = new JSONArray(response);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONArray itemArray = jsonArray.getJSONArray(i);
                String trashName = itemArray.getString(0);
                String recycleViews = itemArray.getString(1);

                String textViewId = "popSearch" + (i + 1);

                int textViewResourceId = getResources().getIdentifier(textViewId, "id", getPackageName());
                TextView textView = findViewById(textViewResourceId);

                if (textView != null) {
                    // TextView가 존재하면 trashName을 설정
                    textView.setText(trashName);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // 검색 기능 로직 처리
    private void performSearch() {
        // 여기에 실제 검색에 대한 로직을 추가하세요
        // 예를 들어, 검색어를 가져와서 검색 결과 화면으로 이동하는 등의 동작을 수행합니다.

        String search = binding.textSearch.getText().toString();
        // 검색 방법
        String searchMethod = "text";
        preferences = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);

        String autoId = preferences.getString("autoId", "1");
        if (autoId.equals("1")){
            searchMethod = "etc";
            Log.d("확인1", search + searchMethod);
            searchRequest(search, searchMethod, autoId);
        }else {
            Log.d("확인1", search + searchMethod);
            searchRequest(search, searchMethod, autoId);
        }


        // 검색어를 이용한 추가 동작 수행

    }

    // 서버 통신
    private void searchRequest(String search, String searchMethod, String autoId) {
        StringRequest request = new StringRequest(
                postMethod,
                springUrl,
                response -> {
                    // 서버통신 성공시
                    Log.d("searchCheck", response); // 로그

                    handSearch(response);

                    // 키워드에 따른 페이지 이동 이벤트
                    if(response.equals("")){
                        // DB에 없는 키워드 일때
                        Intent intent = new Intent(SearchActivity.this, SearchDetailActivity.class);
                        startActivity(intent);
                    }else {
                        // DB에 있는 키워드가 있을때.
                        Intent intent = new Intent(SearchActivity.this, RecycleDetailActivity.class);
                        startActivity(intent);
                    }
                },
                error -> {
                    // 서버통신 실패시
                    Log.d("qwedaqszxc", "????");
                }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("search",search);
                params.put("method", searchMethod);
                params.put("user", autoId);
                long now =System.currentTimeMillis();
                Date today =new Date(now);
                SimpleDateFormat format =new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
                String time = format.format(today);
                params.put("earnTime", time);
                Log.d("가냐..?",search);
                return params;
            }
        };
        // Request를 RequestQueue에 추가
        requestQueue.add(request);
    }

    // Spring 에서 받아온 재활용 데이터 처리 메소드
    private void handSearch(String response) {
//                sepaMethod = 분리수거 방법
//                sepaCaution = 분리수거 주의사항
//                sepaImg = 분리수거 이미지
//                sepaVideo = 분리수거 영상
//                recycleVideo = 업사이클 비디오
//                recycleImg = 업사이클 이미지
        try {
            // 데이터 파싱 작업
            // 데이터 파싱 작업
            JSONObject jsonResponse = new JSONObject(response);

            // searchCheck JSON 객체 가져오기
            JSONObject searchCheckObject = jsonResponse.getJSONObject("searchCheck");

            // searchCheck의 속성들 가져오기
            String trashName = searchCheckObject.getString("trashName");
            String sepaMethod = searchCheckObject.getString("sepaMethod");
            String sepaCaution = searchCheckObject.getString("sepaCaution");
            String sepaImg = searchCheckObject.getString("sepaImg");
            String sepaVideo = searchCheckObject.getString("sepaVideo");
            String recycleVideo = searchCheckObject.getString("recycleVideo");
            String recycleImg = searchCheckObject.getString("recycleImg");
            String recycleNum = searchCheckObject.getString("recycleNum");

            // totalPoints 가져오기
            int totalPoints = jsonResponse.getInt("totalPoints");

            // 확인
            Log.d("데이터 처리", "방법: " + sepaMethod + " 주의사항: " + sepaCaution +
                    " 이미지: " + sepaImg + " 분리수거 영상: " + sepaVideo + " 업사이클 영상: " + recycleVideo +
                    " 업사이클 이미지 : " + recycleImg);

            Log.d("데이터 처리", "Total Points: " + totalPoints);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("userPoint", totalPoints);
            editor.apply();
            // Intent에 값 넣어주기
            Intent textSearchIntent = new Intent(SearchActivity.this, RecycleDetailActivity.class);
            textSearchIntent.putExtra("trashName", trashName);
            textSearchIntent.putExtra("sepaMethod",sepaMethod);
            textSearchIntent.putExtra("sepaCaution",sepaCaution);
            textSearchIntent.putExtra("sepaImg",sepaImg);
            textSearchIntent.putExtra("sepaVideo",sepaVideo);
            textSearchIntent.putExtra("recycleVideo",recycleVideo);
            textSearchIntent.putExtra("recycleImg",recycleImg);
            textSearchIntent.putExtra("searchmethod", "text");
            textSearchIntent.putExtra("recyNum", recycleNum);
            Log.d("searchWhy", "방법: " + sepaMethod + " 주의사항: " + sepaCaution +
                    " 이미지: " + sepaImg + " 분리수거 영상: " + sepaVideo + " 업사이클 영상: " + recycleVideo +
                    " 업사이클 이미지 : " + recycleImg);
            startActivity(textSearchIntent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 인기 검색어 클릭하면 해당 데이터 가져와서 재활용 페이지 보내기
    private void popDataRecySend(String data){
        StringRequest request = new StringRequest(
                postMethod,
                popRecyUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("handlePopRecy", response);
                        handlePopRecy(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        ){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String method = "etc";
                params.put("data", data);
                params.put("method", method);
                Log.d("popopopParams", data + method);
                return params;
            }
        };
        requestQueue.add(request);
    }

    private void handlePopRecy(String response){


        try {
            JSONObject jsonResponse = new JSONObject(response);

            // searchCheck의 속성들 가져오기
            String trashName = jsonResponse.getString("trashName");
            String sepaMethod = jsonResponse.getString("sepaMethod");
            String sepaCaution = jsonResponse.getString("sepaCaution");
            String sepaImg = jsonResponse.getString("sepaImg");
            String sepaVideo = jsonResponse.getString("sepaVideo");
            String recycleVideo = jsonResponse.getString("recycleVideo");
            String recycleImg = jsonResponse.getString("recycleImg");
            String recycleNum = jsonResponse.getString("recycleNum");

            Log.d("popWhy", "방법: " + sepaMethod + " 주의사항: " + sepaCaution +
                    " 이미지: " + sepaImg + " 분리수거 영상: " + sepaVideo + " 업사이클 영상: " + recycleVideo +
                    " 업사이클 이미지 : " + recycleImg);

            Intent textSearchIntent = new Intent(SearchActivity.this, RecycleDetailActivity.class);
            textSearchIntent.putExtra("trashName", trashName);
            textSearchIntent.putExtra("sepaMethod",sepaMethod);
            textSearchIntent.putExtra("sepaCaution",sepaCaution);
            textSearchIntent.putExtra("sepaImg",sepaImg);
            textSearchIntent.putExtra("sepaVideo",sepaVideo);
            textSearchIntent.putExtra("recycleVideo",recycleVideo);
            textSearchIntent.putExtra("recycleImg",recycleImg);
            textSearchIntent.putExtra("searchmethod", "etc");
            textSearchIntent.putExtra("recyNum", recycleNum);

            startActivity(textSearchIntent);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

}