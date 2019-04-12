package com.project.hieu.ghichu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.project.hieu.ghichu.DTO.Note;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Admin on 10/7/2016.
 */
public class DatabaseSQLite extends SQLiteOpenHelper {

    private static final String NAME_DATABASE = "ghichu";
    public static final String TABLE_NOTE = "Note";
    private static final String ID_NOTE = "_id";
    private static final String TITLE_NOTE = "title";
    private static final String CONTENT_NOTE = "content";
    private static final String NGAY_PHAT_HANH = "ngayphathanh";
    private static final String HINH_ANH = "hinhanh";
    private static final String ALARM = "baothuc";

    private static final String querrytable = "create table " + TABLE_NOTE + "(" + ID_NOTE + " integer primary key autoincrement,"
            + TITLE_NOTE + " text," + CONTENT_NOTE + " text," + HINH_ANH + " blob," + NGAY_PHAT_HANH + " text," + ALARM + " text);";

    public DatabaseSQLite(Context context) {
        super(context, NAME_DATABASE, null, 10);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(querrytable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion){
            db.execSQL("drop table if exists " + TABLE_NOTE);
            onCreate(db);
        }
    }

    public void insertNote(Note note){
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TITLE_NOTE,note.getTitle());
        values.put(CONTENT_NOTE,note.getContent());
        values.put(HINH_ANH, note.getHinhanh());
        values.put(NGAY_PHAT_HANH,note.getNgayphathanh());
        values.put(ALARM,note.getAlarmTime());

        database.insert(TABLE_NOTE, null, values);
        database.close();
    }

    public  Note getNote(int id){
        SQLiteDatabase database = getWritableDatabase();
        Cursor cursor = database.query(TABLE_NOTE, new String[]{ID_NOTE, TITLE_NOTE, CONTENT_NOTE, HINH_ANH, NGAY_PHAT_HANH}, ID_NOTE + "= ?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if(cursor != null)
            cursor.moveToFirst();
        Note note = new Note(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2),cursor.getBlob(3),cursor.getString(4));
        return note;
    }

    public List<Note> getAllNotes() {
        List<Note> noteList = new ArrayList<Note>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NOTE;
        Bitmap bitmap;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Duyệt trên con trỏ, và thêm vào danh sách.
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(Integer.parseInt(cursor.getString(0)));
                note.setTitle(cursor.getString(1));
                note.setContent(cursor.getString(2));
                if(cursor.getBlob(3) != null) {
                    note.setHinhanh(cursor.getBlob(3));
                    bitmap = BitmapFactory.decodeByteArray(cursor.getBlob(3), 0, cursor.getBlob(3).length);
                }else {

                }
                note.setNgayphathanh(cursor.getString(4));
                note.setAlarmTime(cursor.getString(5));
                // Thêm vào danh sách.
                noteList.add(0,note);
            } while (cursor.moveToNext());
        }
        // return note list
        return noteList;
    }

    public int getNotesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NOTE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public int updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TITLE_NOTE, note.getTitle());
        values.put(CONTENT_NOTE, note.getContent());
        values.put(HINH_ANH,note.getHinhanh());
        values.put(NGAY_PHAT_HANH, note.getNgayphathanh());
        values.put(ALARM, note.getAlarmTime());

        // updating row
        return db.update(TABLE_NOTE, values, ID_NOTE + " = ?",new String[]{String.valueOf(note.getId())});
    }

    public void deleteNote(Note note){
        SQLiteDatabase database = getWritableDatabase();
        database.delete(TABLE_NOTE, ID_NOTE + "=?", new String[]{String.valueOf(note.getId())});

    }

    public List<Note> getAllNotesTheoMa(String tenghichu) {
        List<Note> noteList = new ArrayList<Note>();
        String[] column = {ID_NOTE,TITLE_NOTE,CONTENT_NOTE,HINH_ANH,NGAY_PHAT_HANH,ALARM};
        // Select All Query
        String selectQuery = "Select " + column[0] + " , " + column[1] + " , "
                + column[2] + " , " + column[3] + " , " + column[4] + " ," + column[5] + " From "
                + TABLE_NOTE + " Where " + TITLE_NOTE + " LIKE '%" + tenghichu.toLowerCase() + "%'";
        Bitmap bitmap;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Duyệt trên con trỏ, và thêm vào danh sách.
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(Integer.parseInt(cursor.getString(0)));
                note.setTitle(cursor.getString(1));
                note.setContent(cursor.getString(2));
                if(cursor.getBlob(3) != null) {
                    note.setHinhanh(cursor.getBlob(3));
                    bitmap = BitmapFactory.decodeByteArray(cursor.getBlob(3), 0, cursor.getBlob(3).length);
                }else {
                }
                note.setNgayphathanh(cursor.getString(4));
                note.setAlarmTime(cursor.getString(5));
                // Thêm vào danh sách.
                noteList.add(0,note);
            } while (cursor.moveToNext());
        }
        // return note list
        return noteList;
    }

    public void createDefaultNotesIfNeed()  {
        int count = this.getNotesCount();
        if(count ==0 ) {
            Note note1 = new Note("Welcom to ChickNote",null,"26/11/2016  00:00:00",null,Calender());
            this.insertNote(note1);
        }
    }

    public String Calender(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = null;
        String strDateFormat = " HH:mm dd/MM/yyyy ";
        sdf = new SimpleDateFormat(strDateFormat);
        return sdf.format(calendar.getTime());
    }
}
