package com.project.hieu.ghichu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;


import com.project.hieu.ghichu.Adapter.CustomArrayAdapter;
import com.project.hieu.ghichu.DTO.Note;
import com.project.hieu.ghichu.Service.AlarmService;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final int MENU_ITEM_EDIT = 222;
    private static final int MENU_ITEM_DELETE = 444;

    private static final int MY_REQUEST_CODE = 1000;

    final String API_GET = "http://superman-academy.com/dev/SimpleNote/note/get.php";
    final String API_DELETE = "http://superman-academy.com/dev/SimpleNote/note/delete.php";
    final String API_SYNC = "http://superman-academy.com/dev/SimpleNote/note/create.php";


    Button btnthemghichu;
    ListView listView;
    List<Note> danhsachnote;
    CustomArrayAdapter adapter;
    DatabaseSQLite db = new DatabaseSQLite(this);
    Toolbar toolbar;
    SharedPreferences luuTrangThai;
    String tokenCode;
    List<Integer> idNote = new ArrayList<>();
    Note note = new Note();
    List<Note> list;
    JSONArray jNotes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sp = getSharedPreferences("thongtinnguoidung", Context.MODE_PRIVATE);
        tokenCode = sp.getString("token", "");
        SharedPreferences trangthaicapnhat = getSharedPreferences("trangthaicapnhat", Context.MODE_PRIVATE);
        boolean capnhat = trangthaicapnhat.getBoolean("trangthaicapnhat", false);
        if (capnhat == false) {
            new LayDuLieuTuServer().execute();

            SharedPreferences trangthaicapnhat1 = getSharedPreferences("trangthaicapnhat", Context.MODE_PRIVATE);
            SharedPreferences.Editor ghi = trangthaicapnhat1.edit();
            ghi.putBoolean("trangthaicapnhat", true);
            ghi.commit();
        }

        btnthemghichu = (Button) findViewById(R.id.btnthemghichu);
        listView = (ListView) findViewById(R.id.listview);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        luuTrangThai = getSharedPreferences("trangthai", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = luuTrangThai.edit();
        editor.putBoolean("dangnhap", true);
        editor.commit();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tất cả ghi chú");//xét tiêu đề cho actionbar

        
        db.createDefaultNotesIfNeed();

        danhsachnote = new ArrayList<>();
        list = db.getAllNotes();
        this.danhsachnote.addAll(list);
        setAdapterListView(danhsachnote);
        adapter.notifyDataSetChanged();
        registerForContextMenu(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Note selectNote = danhsachnote.get(position);
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                intent.putExtra("note", selectNote);
                startActivityForResult(intent, MY_REQUEST_CODE);

            }
        });

        btnthemghichu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popupMenu = new PopupMenu(getSupportActionBar().getThemedContext(), btnthemghichu);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.ghichu:
                                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                                // Start AddEditNoteActivity, có phản hồi.
                                startActivityForResult(intent, MY_REQUEST_CODE);
                                break;
//                            case R.id.nhacnho:
//                                Intent intent1 = new Intent(MainActivity.this, AddNoteActivity.class);
//                                startActivity(intent1);
//                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        Intent intentService = new Intent(MainActivity.this, AlarmService.class);
        startService(intentService);

    }

    private boolean KiemTraKetNoiMang(){
        ConnectivityManager connectivityManager = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.my_menu, menu);
        MenuItem searchview = menu.findItem(R.id.searchView);
        SearchView searchView = (SearchView) searchview.getActionView();
        searchView.setOnQueryTextListener(MainActivity.this);


        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        danhsachnote = db.getAllNotesTheoMa(newText);
        setAdapterListView(danhsachnote);
        adapter.notifyDataSetChanged();
        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.searchView:

                break;
            case R.id.timhieuthem:

                break;
            case R.id.dongbo:
                if(KiemTraKetNoiMang()){
                    new LayLaiId().execute();
                    new XoaTatCaDuLieuCuaServer().execute();
                    ChuyenDuLieuTuLocalSangServer();
                    Toast.makeText(MainActivity.this, "Đồng bộ thành công", Toast.LENGTH_LONG).show();
                    new LayLaiId().execute();
                }else {
                    Toast.makeText(MainActivity.this,"Vui lòng kết nối mạng",Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.dangxuat:
                db = new DatabaseSQLite(this);
                int length = (danhsachnote.size() - 1);
                for (int i = 0; i < length; i++) {
                    final Note selectNote = danhsachnote.get(i);
                     db.deleteNote(selectNote);
                }
                for (int i = length - 1; i >= 0; i--) {
                    danhsachnote.remove(i);
                }
                Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intentLogin);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select The Action");

        menu.add(0, MENU_ITEM_EDIT, 2, "Xem và sửa ghi chú");
        menu.add(0, MENU_ITEM_DELETE, 4, "Xóa ghi chú");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        final Note selectedNote = (Note) this.listView.getItemAtPosition(info.position);

        if (item.getItemId() == MENU_ITEM_EDIT) {
            Intent intent = new Intent(this, AddNoteActivity.class);
            intent.putExtra("note", selectedNote);

            // Start AddEditNoteActivity, có phản hồi.
            this.startActivityForResult(intent, MY_REQUEST_CODE);
        } else if (item.getItemId() == MENU_ITEM_DELETE) {
            // Hỏi trước khi xóa.
            new AlertDialog.Builder(this)
                    .setMessage(selectedNote.getTitle() + ".Bạn có muốn xóa ghi chú này?")
                    .setCancelable(false)
                    .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            deleteNote(selectedNote);
                        }
                    })
                    .setNegativeButton("Không,Tôi không muốn", null)
                    .show();
        } else {
            return false;
        }
        return true;
    }

    private void deleteNote(Note note) {
        DatabaseSQLite db = new DatabaseSQLite(this);
        db.deleteNote(note);
        this.danhsachnote.remove(note);
        // Refresh ListView.
        this.adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == MY_REQUEST_CODE) {
            boolean needRefresh = data.getBooleanExtra("needRefresh", true);
            // Refresh ListView
            if (needRefresh) {
                this.danhsachnote.clear();
                DatabaseSQLite db = new DatabaseSQLite(this);
                List<Note> list = db.getAllNotes();
                this.danhsachnote.addAll(list);
                // Thông báo dữ liệu thay đổi (Để refresh ListView).
                this.adapter.notifyDataSetChanged();
//        super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void setAdapterListView(List<Note> list) {
        adapter = new CustomArrayAdapter(this, R.layout.custom_layout_listview, this.danhsachnote);
        listView.setAdapter(adapter);
    }

    public void ChuyenDuLieuTuLocalSangServer() {

        for (int i = 0; i < danhsachnote.size(); i++) {
            note = danhsachnote.get(i);

            JSONObject jNote = new JSONObject();
            try {
                jNote.put("local_id",note.getId());
                jNote.put("title", note.getTitle());
                jNote.put("content", note.getContent());
                String ngayphathanh = note.getNgayphathanh();
                SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss ");
                Date date = sf.parse(ngayphathanh);
                long millisecondsDate = date.getTime();
                jNote.put("created_date", millisecondsDate);
                String baothuc = note.getAlarmTime();
                SimpleDateFormat sfa = new SimpleDateFormat("HH:mm dd/MM/yyyy");
                if (!baothuc.equals("")) {
                    Date alarm = sfa.parse(baothuc);
                    long millisecondsAlarm = alarm.getTime();
                    jNote.put("alarm", millisecondsAlarm);
                } else {
                    jNote.put("alarm",0);
                }
                String jsonNote = jNote.toString();
                new DongBoDuLieuTuLocalLenServer().execute(jsonNote);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    class DongBoDuLieuTuLocalLenServer extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String jsonData = params[0];
            try {
                URL url = new URL(API_SYNC);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("token", tokenCode);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();

                OutputStream os = httpURLConnection.getOutputStream();
                os.write(jsonData.getBytes());

                InputStream is = httpURLConnection.getInputStream();
                String result = "";
                int byteCharacter;
                while ((byteCharacter = is.read()) != -1) {
                    result += (char) byteCharacter;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class XoaTatCaDuLieuCuaServer extends AsyncTask<Void, Void, Void> {
        String result = "";

        @Override
        protected Void doInBackground(Void... params) {
            SharedPreferences luu = getSharedPreferences("idnote", Context.MODE_PRIVATE);
            int idnote = luu.getInt("id", 0);
            SharedPreferences luuJnote = getSharedPreferences("jnotes", Context.MODE_PRIVATE);
            int lengJnote = luuJnote.getInt("lengthJnotes", 0);
            int length = idnote - lengJnote;

//            for (int i = idnote; i > length ; i--) {
//                int id = i;
            for (int i = 0; i < idNote.size() ; i++) {
                int id = idNote.get(i);
                try {
                    URL url = new URL(String.format("%s/%d", API_DELETE, id));
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("DELETE");
                    httpURLConnection.setRequestProperty("token", tokenCode);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.connect();

                    InputStream is = httpURLConnection.getInputStream();
                    int byteCharacter;

                    while ((byteCharacter = is.read()) != -1) {
                        result += (char) byteCharacter;
                    }
                    Log.d("API Result", "doInBackground: " + "Note bị xoa ở ví trí có id là: " + result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

    }
    class LayLaiId extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(API_GET);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("token", tokenCode);
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                InputStream is = httpURLConnection.getInputStream();
                String result = "";
                int byteCharacter;
                while ((byteCharacter = is.read()) != -1) {
                    result += (char) byteCharacter;
                }

                jNotes = new JSONArray(result);

                SharedPreferences luuJnote = getSharedPreferences("jnotes",Context.MODE_PRIVATE);
                SharedPreferences.Editor edt = luuJnote.edit();
                edt.putInt("lengthJnotes",jNotes.length());
                edt.commit();

                for (int i = 0; i < jNotes.length(); i++) {
                    JSONObject jNote = jNotes.getJSONObject(i);
                    int id = jNote.getInt("id");
                    String title = jNote.getString("title");
                    String content = jNote.getString("content");
                    long created_date = jNote.getLong("created_date");
                    long alarm = jNote.getLong("alarm");
                    int user_id = jNote.getInt("user_id");

                    SharedPreferences luu = getSharedPreferences("idnote", Context.MODE_PRIVATE);
                    SharedPreferences.Editor ed = luu.edit();
                    ed.putInt("id",id);
                    ed.commit();

                    idNote.add(id);
                    Log.d("API ID", "doInBackground: " + id);

                    Date date = new Date(created_date);
                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss ");
                    String dateText = df.format(date);
                    Date baothuc = new Date(alarm);
                    SimpleDateFormat dfBaoThuc = new SimpleDateFormat("HH:mm dd/MM/yyyy");
                    String alarmText = dfBaoThuc.format(baothuc);

//                    note = new Note(id, title, content, dateText, alarmText);
//                    DatabaseSQLite dt = new DatabaseSQLite(MainActivity.this);
//                    dt.insertNote(note);
//                    danhsachnote.add(0, note);
                }
                is.close();
                httpURLConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

    }


    class LayDuLieuTuServer extends AsyncTask<Void, Void, Void> {
        Note note;
        ProgressDialog pbLoading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbLoading = new ProgressDialog(MainActivity.this);
            pbLoading.setMessage("Đang cập nhật dữ liệu....");
            pbLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pbLoading.setCancelable(true);
            pbLoading.setCanceledOnTouchOutside(false);

            pbLoading.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
//            MenuItem item = MenuItem.OnMenuItemClickListener;
            int id = 0;
            String title = "";
            String content = "";
            String dateText = "";
            String alarmText = "";
            long alarm = 0;
            int local_id = 0;

            try {
                URL url = new URL(API_GET);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("token", tokenCode);
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                InputStream is = httpURLConnection.getInputStream();
                String result = "";
                int byteCharacter;
                while ((byteCharacter = is.read()) != -1) {
                    result += (char) byteCharacter;
                }

                jNotes = new JSONArray(result);

                SharedPreferences luuJnote = getSharedPreferences("jnotes",Context.MODE_PRIVATE);
                SharedPreferences.Editor edt = luuJnote.edit();
                edt.putInt("lengthJnotes",jNotes.length());
                edt.commit();

                for (int i = 0; i < jNotes.length(); i++) {
                    JSONObject jNote = jNotes.getJSONObject(i);
                    id = jNote.getInt("id");
                    title = jNote.getString("title");
                    content = jNote.getString("content");
                    long created_date = jNote.getLong("created_date");
                    Date date = new Date(created_date);
                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss ");
                    dateText = df.format(date);

                    alarm = jNote.getLong("alarm");

                    if(alarm > 0){
                        Date baothuc = new Date(alarm);
                        SimpleDateFormat dfBaoThuc = new SimpleDateFormat("HH:mm dd/MM/yyyy");
                        alarmText = dfBaoThuc.format(baothuc);
                    }else {
                        alarmText = "";
                    }
                    int user_id = jNote.getInt("user_id");

                    SharedPreferences luu = getSharedPreferences("idnote", Context.MODE_PRIVATE);
                    SharedPreferences.Editor ed = luu.edit();
                    ed.putInt("id", id);
                    ed.commit();

                    idNote.add(id);
                    Log.d("API ID", "doInBackground: " + id);



//                    note = new Note(local_id, title, content, dateText, alarmText);
                    note = new Note(title,content,dateText,alarmText);
                    DatabaseSQLite dt = new DatabaseSQLite(MainActivity.this);
                    dt.insertNote(note);
                    danhsachnote.add(0, note);
                }
                is.close();
                httpURLConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pbLoading.dismiss();
            setAdapterListView(danhsachnote);
//            adapter.notifyDataSetChanged();
//            registerForContextMenu(listView);

        }
    }
}


