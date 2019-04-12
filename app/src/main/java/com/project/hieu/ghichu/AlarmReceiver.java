package com.project.hieu.ghichu;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.project.hieu.ghichu.DTO.Note;

//import com.project.hieu.ghichu.Service.AlarmService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Admin on 10/26/2016.
 */
public class AlarmReceiver extends BroadcastReceiver {
    DatabaseSQLite db ;
    @Override
    public void onReceive(Context context, Intent intent) {
        db = new DatabaseSQLite(context);
        List<Note> notes = new ArrayList<>();
        List<Note> list = db.getAllNotes();
        notes.addAll(list);

        for(int i = 0; i < notes.size();i++){
              String time = notes.get(i).getAlarmTime();
              String tieude = notes.get(i).getTitle();
              String noidung = notes.get(i).getContent();

                  Date date = null;
                  try {
                      date = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.ENGLISH).parse(time);
                  } catch (ParseException e) {
                      e.printStackTrace();
                  }
                  if (date != null){
                      long milliseconds = date.getTime();
                      long millisecondsFromNow = milliseconds - (new Date()).getTime();

                      if(millisecondsFromNow <= 65000 && millisecondsFromNow >= 0) {
                          Toast.makeText(context, "Có nhắc nhở", Toast.LENGTH_LONG).show();

                          Note selectNote = notes.get(i);

                          Intent intentNoti = new Intent(context, AddNoteActivity.class);
                          intentNoti.putExtra("note", selectNote);
                          TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
                          taskStackBuilder.addParentStack(AddNoteActivity.class);
                          taskStackBuilder.addNextIntent(intentNoti);
                          PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                          Notification notification = new NotificationCompat.Builder(context)
                                  .setContentTitle(tieude)
                                  .setContentText(noidung)
                                  .setSmallIcon(R.drawable.chick)
                                  .setContentIntent(pendingIntent)
                                  .setAutoCancel(true)
                                  .build();
//                notification.sound = Uri.parse("android.resource://"
//                        + context.getPackageName() + "/" + R.raw.dj);
                          notification.defaults |= Notification.DEFAULT_SOUND;
                          NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                          manager.notify(i, notification);
                      }
                  }

        }
    }
}
