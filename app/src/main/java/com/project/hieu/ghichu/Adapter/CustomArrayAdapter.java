package com.project.hieu.ghichu.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.hieu.ghichu.DTO.Note;
import com.project.hieu.ghichu.R;

import java.util.List;

/**
 * Created by Admin on 10/11/2016.
 */
public class CustomArrayAdapter extends ArrayAdapter<Note> {
    Context context;
    int resource;
    List<Note> objects;

    public CustomArrayAdapter(Context context, int resource, List<Note> objects) {
        super(context, resource, objects);

        this.context = context;
        this.resource = resource;
        this.objects = objects;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(resource,parent,false);



        TextView tvtitle = (TextView) view.findViewById(R.id.tvtitle);
        TextView tvcontent = (TextView) view.findViewById(R.id.tvcontent);
        ImageView imhinhanh = (ImageView) view.findViewById(R.id.imv);
        TextView tvngayphathanh = (TextView) view.findViewById(R.id.tvngayphathanh);
        TextView tvhengio = (TextView) view.findViewById(R.id.tvhengio);



        tvtitle.setText(objects.get(position).getTitle());
        tvcontent.setText(objects.get(position).getContent());

        if(objects.get(position).getHinhanh() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(objects.get(position).getHinhanh(), 0, objects.get(position).getHinhanh().length);
            imhinhanh.setImageBitmap(bitmap);
        }
        tvngayphathanh.setText(objects.get(position).getNgayphathanh());
        tvhengio.setText(objects.get(position).getAlarmTime());
        return view;
    }
}
