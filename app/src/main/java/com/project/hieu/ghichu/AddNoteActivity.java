package com.project.hieu.ghichu;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.project.hieu.ghichu.DTO.Note;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddNoteActivity extends AppCompatActivity implements View.OnClickListener{
    public ImageView imhinhdaidien;
    TextView tvtime;
    TextView tvdate;
    EditText edtitle,edcontent;
    Button btngio,btnngay;
    Note note;
    Toolbar toolBarAddNote;
    String time;
    String date;
    String alarm = "";
    private static final int MODE_CREATE = 1;
    private static final int MODE_EDIT = 2;
    private static final int RESQUEST_TAKE_PHOTO = 123;
    private static final int RESQUEST_CHOOSE_PHOTO = 321;
    private int mode;
    private boolean needRefresh;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        edtitle = (EditText) findViewById(R.id.edtitle);
        edcontent = (EditText) findViewById(R.id.edcontent);
        tvtime = (TextView) findViewById(R.id.tvtime);
        tvdate = (TextView) findViewById(R.id.tvdate);
        imhinhdaidien = (ImageView) findViewById(R.id.imanhdaidien);
        btngio = (Button) findViewById(R.id.btngio);
        btnngay = (Button) findViewById(R.id.btnngay);

        toolBarAddNote = (Toolbar) findViewById(R.id.toolBarAddNote);
        setSupportActionBar(toolBarAddNote);
        getSupportActionBar().setTitle("");

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        btngio.setOnClickListener(this);
        btnngay.setOnClickListener(this);

        Intent intent = this.getIntent();
        this.note = (Note) intent.getSerializableExtra("note");
        if(note== null)  {
            this.mode = MODE_CREATE;
        } else  {
            this.mode = MODE_EDIT;
            this.edtitle.setText(note.getTitle());
            this.edcontent.setText(note.getContent());
            this.tvtime.setText(note.getAlarmTime());

            byte[] hinhdaidien = note.getHinhanh();
            if(note.getHinhanh() != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(hinhdaidien, 0, hinhdaidien.length);
                this.imhinhdaidien.setImageBitmap(bitmap);
            }
        }
    }

    public byte[] getByteArrayFromImageView(ImageView imgv){
        BitmapDrawable drawable = (BitmapDrawable) imgv.getDrawable();
        if (drawable!=null){
            Bitmap bmp = drawable.getBitmap();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return  stream.toByteArray();
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btngio:
                Calendar calendarGio = Calendar.getInstance();

                TimePickerDialog timePickerDialog = new TimePickerDialog(AddNoteActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if(minute == 0) {
                            time = hourOfDay + ":" + minute + "0";
                        }else if (minute <= 9){
                            time = hourOfDay + ":" + "0" + minute;
                        }else {
                            time = hourOfDay + ":" + minute ;
                        }
                        tvtime.setText(time);

                    }
                }, calendarGio.get(Calendar.HOUR_OF_DAY),calendarGio.get(Calendar.MINUTE), true);
                timePickerDialog.show();
                break;
            case R.id.btnngay:
                 Calendar calendarNgay = Calendar.getInstance();

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddNoteActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        date = dayOfMonth + "/" + (monthOfYear + 1)  + "/" + year;
                        tvdate.setText(date);
                    }
                }, calendarNgay.get(Calendar.YEAR), calendarNgay.get(Calendar.MONTH), calendarNgay.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                break;
        }
    }
    public void finish() {

        // Chuẩn bị dữ liệu Intent.
        Intent data = new Intent();
        // Yêu cầu MainActivity refresh lại ListView hoặc không.
        data.putExtra("needRefresh", needRefresh);

        // Activity đã hoàn thành OK, trả về dữ liệu.
        this.setResult(Activity.RESULT_OK, data);
        super.finish();
    }

    private void takePicture(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, RESQUEST_TAKE_PHOTO);
    }
    private void choosePicture(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, RESQUEST_CHOOSE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == RESQUEST_CHOOSE_PHOTO){

                try {
                    Uri imageUri = data.getData();
                    InputStream is = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    Bitmap bm = Bitmap.createScaledBitmap(bitmap,500,600,false); // Resize lại ảnh cho không bị lỗi OutOfBound
                    imhinhdaidien.setImageBitmap(bm);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }else if (requestCode == RESQUEST_TAKE_PHOTO){
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//                Bitmap bm1 = Bitmap.createScaledBitmap(bitmap,450,800,false);// Resize lại ảnh cho không bị lỗi OutOfBound
                imhinhdaidien.setImageBitmap(bitmap);

            }
        }
    }

 public String Calender(){
     Calendar calendar = Calendar.getInstance();
     SimpleDateFormat sdf = null;
     String strDateFormat = "dd/MM/yyyy  HH:mm:ss ";
     sdf = new SimpleDateFormat(strDateFormat);

     return sdf.format(calendar.getTime());
 }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.itSave: {
                DatabaseSQLite db = new DatabaseSQLite(this);

                byte[] hinhanh;

                String title = this.edtitle.getText().toString();
                String content = this.edcontent.getText().toString();
                String time = this.tvtime.getText().toString();
                String date = this.tvdate.getText().toString();
                if(time.equals("") && date.equals("")){
                    alarm = "";
                }else {
                    alarm = time + " " + date;
                }


                hinhanh = getByteArrayFromImageView(imhinhdaidien);
                String ngayphathanh = Calender();

                //#c34c33
//                btnsave.setBackgroundColor(Color.parseColor("#c23c33"));

                    if (title.equals("") || content.equals("")) {
                        Toast.makeText(getApplicationContext(),
                                "Bạn chưa nhập nội dung hoặc tiêu đề", Toast.LENGTH_LONG).show();
                    } else {
                        if (mode == MODE_CREATE) {
                            this.note = new Note(title, content, ngayphathanh, hinhanh, alarm);
                            db.insertNote(note);
                        } else {
                            this.note.setTitle(title);
                            this.note.setContent(content);
                            this.note.setHinhanh(hinhanh);
                            this.note.setNgayphathanh(ngayphathanh);
                            this.note.setAlarmTime(alarm);
                            db.updateNote(note);
                        }
                        this.needRefresh = true;
                        // Trở lại MainActivity.
//                this.onBackPressed();
                        Intent intent = new Intent(AddNoteActivity.this, MainActivity.class);
                        startActivity(intent);

                        Toast.makeText(getBaseContext(), "Ghi chú đã được lưu", Toast.LENGTH_LONG).show();
                    }


            }
                break;
            case R.id.itChupHinh:
                takePicture();
                break;
            case R.id.itChonHinh:
                choosePicture();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
