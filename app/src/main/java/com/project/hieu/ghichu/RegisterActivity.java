package com.project.hieu.ghichu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

public class RegisterActivity extends AppCompatActivity {

    Button btndangky,btncancel;
    EditText edusernameregister,edpasswordregister,edname;
    final String API_REGISTER = "http://superman-academy.com/dev/SimpleNote/user/register.php";
//    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edusernameregister = (EditText) findViewById(R.id.edusernameregister);
        edpasswordregister = (EditText) findViewById(R.id.edpasswordregister);
        edname = (EditText) findViewById(R.id.edname);
        btndangky = (Button) findViewById(R.id.btndangky);
        btncancel = (Button) findViewById(R.id.btncancel);
//        sharedPreferences = getSharedPreferences("thongtinnguoidung", Context.MODE_PRIVATE);

        btndangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(KiemTraKetNoiMang()){

                    String username = edusernameregister.getText().toString();
                    String password = edpasswordregister.getText().toString();
                    String name = edname.getText().toString();

                    if(username.equals("") || password.equals("") || name.equals("")){
                        Toast.makeText(RegisterActivity.this,"Vui lòng nhập thông tin",Toast.LENGTH_LONG).show();
                    }else {
//                        String username = edusernameregister.getText().toString();
//                        String password = edpasswordregister.getText().toString();
//                        String name = edname.getText().toString();

                        JSONObject jTaiKhoan = new JSONObject();
                        try {
                            jTaiKhoan.put("username",username);
                            jTaiKhoan.put("password",password);
                            jTaiKhoan.put("name",name);

                            String chuoiJsonTaiKhoan = jTaiKhoan.toString();

                            new DangKiTaiKhoan().execute(chuoiJsonTaiKhoan);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    Toast.makeText(RegisterActivity.this,"Bạn đang ngoại tuyến",Toast.LENGTH_LONG).show();
                }
            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private boolean KiemTraKetNoiMang(){
        ConnectivityManager connectivityManager = (ConnectivityManager) RegisterActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            return true;
        }else {
            return false;
        }
    }

    class DangKiTaiKhoan extends AsyncTask<String,Void,Void>{
        ProgressDialog pbLoading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbLoading = new ProgressDialog(RegisterActivity.this);
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
                URL url = new URL(API_REGISTER);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();

                OutputStream os = httpURLConnection.getOutputStream();
                os.write(jsondata.getBytes());

                InputStream is = httpURLConnection.getInputStream();
                String result = "";
                int byteCharacter;
                while ((byteCharacter = is.read()) != -1){
                    result += (char)byteCharacter;
                }

                JSONObject jThongTinNguoiDung = new JSONObject(result);
                int id = jThongTinNguoiDung.getInt("id");
                String username = jThongTinNguoiDung.getString("username");
                String password = jThongTinNguoiDung.getString("password");
                String name = jThongTinNguoiDung.getString("name");
                String token = jThongTinNguoiDung.getString("token");

//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putInt("id",id);
//                editor.putString("username", username);
//                editor.putString("password", password);
//                editor.putString("name", name);
//                editor.putString("token", token);
//                editor.commit();


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
            Toast.makeText(RegisterActivity.this,"Đăng ký thành công",Toast.LENGTH_LONG).show();
        }
    }
}
