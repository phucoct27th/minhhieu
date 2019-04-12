package com.project.hieu.ghichu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    EditText edusername,edpassword;
    Button btnlogin,btnregister;
    final String API_LOGIN = "http://superman-academy.com/dev/SimpleNote/user/login.php";
    SharedPreferences sharedPreferences;
    SharedPreferences luuTrangThai;
    SharedPreferences trangthaicapnhat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edusername = (EditText) findViewById(R.id.edusername);
        edpassword = (EditText) findViewById(R.id.edpassword);
        btnlogin = (Button) findViewById(R.id.btnlogin);
        btnregister = (Button) findViewById(R.id.btnregister);
        sharedPreferences = getSharedPreferences("thongtinnguoidung", Context.MODE_PRIVATE);
        luuTrangThai = getSharedPreferences("trangthai",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = luuTrangThai.edit();
        editor.putBoolean("dangnhap",false);
        editor.commit();
        trangthaicapnhat = getSharedPreferences("trangthaicapnhat",Context.MODE_PRIVATE);
        SharedPreferences.Editor ghitrangthaicapnhat = trangthaicapnhat.edit();
        ghitrangthaicapnhat.putBoolean("trangthaicapnhat",false);
        ghitrangthaicapnhat.commit();


        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(KiemTraKetNoiMang()){
                    String username = edusername.getText().toString();
                    String password = edpassword.getText().toString();

                    if(username.equals("") || password.equals("")){
                        Toast.makeText(LoginActivity.this,"Vui lòng nhập thông tin",Toast.LENGTH_LONG).show();
                    }else {
//                        String username = edusername.getText().toString();
//                        String password = edpassword.getText().toString();

                        JSONObject jTaiKhoan = new JSONObject();
                        try {
                            jTaiKhoan.put("username", username);
                            jTaiKhoan.put("password", password);

                            String chuoiJsonTaiKhoan = jTaiKhoan.toString();
                            new DangNhapTaiKhoan().execute(chuoiJsonTaiKhoan);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    Toast.makeText(LoginActivity.this,"Vui lòng kiểm tra kết nối mạng của bạn",Toast.LENGTH_LONG).show();
                }


            }
        });

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean KiemTraKetNoiMang(){
        ConnectivityManager connectivityManager = (ConnectivityManager) LoginActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
         return true;
        }else {
            return false;
        }
    }

    class DangNhapTaiKhoan extends AsyncTask<String,Void,Void>{

        ProgressDialog pbLoading;
        int id;
        String token,username,name,password;
        JSONObject jThongTinNguoiDung;
        OutputStream os;
        InputStream is;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbLoading = new ProgressDialog(LoginActivity.this);
            pbLoading.setMessage("Vui lòng đợi....");
            pbLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pbLoading.setCancelable(true);
            pbLoading.setCanceledOnTouchOutside(false);

            pbLoading.show();

        }

        @Override
        protected Void doInBackground(String... params) {
            String jsondata = params[0];

            try {
                URL url = new URL(API_LOGIN);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();

                os = httpURLConnection.getOutputStream();
                os.write(jsondata.getBytes());

                is = httpURLConnection.getInputStream();
                String result = "";
                int byteCharacrer;
                while ((byteCharacrer = is.read()) != -1){
                    result += (char)byteCharacrer;
                }

                jThongTinNguoiDung = new JSONObject(result);

                id = jThongTinNguoiDung.getInt("id");
                username = jThongTinNguoiDung.getString("username");
                password = jThongTinNguoiDung.getString("password");
                name = jThongTinNguoiDung.getString("name");
                token = jThongTinNguoiDung.getString("token");

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("id",id);
                editor.putString("username", username);
                editor.putString("password", password);
                editor.putString("name", name);
                editor.putString("token", token);
                editor.commit();

                is.close();
                os.close();
                httpURLConnection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pbLoading.dismiss();

            try{
                id = jThongTinNguoiDung.getInt("id");
                username = jThongTinNguoiDung.getString("username");
                password = jThongTinNguoiDung.getString("password");
                name = jThongTinNguoiDung.getString("name");
                token = jThongTinNguoiDung.getString("token");
                if(!username.equals("")){
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(getBaseContext(),"Xin Chào " + sharedPreferences.getString("name",""),Toast.LENGTH_LONG).show();
                    finish();
                }
            }catch (NullPointerException e){
                Toast.makeText(getBaseContext(),"Username hoặc Password không đúng",Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                Toast.makeText(getBaseContext(),"Username hoặc Password không đúng",Toast.LENGTH_LONG).show();
            }
        }
    }
}
