package com.project.hieu.ghichu.DTO;

import java.io.Serializable;

/**
 * Created by Admin on 10/11/2016.
 */
public class Note implements Serializable{
    private static int count = 1;
    int id;
    String title;
    String content;
    String ngayphathanh;
    byte[] hinhanh;
    String alarmTime;

    public Note(String title, String content, String ngayphathanh, String alarmTime) {
        this.id = count++;
        this.title = title;
        this.content = content;
        this.ngayphathanh = ngayphathanh;
        this.alarmTime = alarmTime;
    }


    public Note(int id, String title, String content, String ngayphathanh, String alarmTime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.ngayphathanh = ngayphathanh;
        this.alarmTime = alarmTime;
    }


    public Note(String title, String content, String ngayphathanh, byte[] hinhanh, String alarmTime) {
        this.id = count++;
        this.title = title;
        this.content = content;
        this.ngayphathanh = ngayphathanh;
        this.hinhanh = hinhanh;
        this.alarmTime = alarmTime;
    }


    public Note(String alarmTime) {
        this.id = count++;
        this.alarmTime = alarmTime;
    }


    public Note(int id, String title, String content, String ngayphathanh, byte[] hinhanh, String alarmTime) {
        this.id = id;

        this.title = title;
        this.content = content;
        this.ngayphathanh = ngayphathanh;
        this.hinhanh = hinhanh;
        this.alarmTime = alarmTime;
    }


    public Note(String title,String ngayphathanh){
        this.id = count++;
        this.title = title;
        this.ngayphathanh= ngayphathanh;
    }

    public Note(String title, String content, byte[] hinhanh, String ngayphathanh) {
        this.id = count++;
        this.title = title;
        this.content = content;
        this.hinhanh = hinhanh;
        this.ngayphathanh = ngayphathanh;

    }


    public Note(int id, String title, String content, byte[] hinhanh, String ngayphathanh) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.hinhanh = hinhanh;
        this.ngayphathanh = ngayphathanh;
    }


    public Note(String title, String content, byte[] hinhanh) {
        this.id = count++;
        this.title = title;
        this.content = content;
        this.hinhanh = hinhanh;
    }


    public Note(int id, String title, String content, byte[] hinhanh) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.hinhanh = hinhanh;
    }

    public Note(){
//        this.id = count++;
    }

//    public Note(String title, String content) {
//        this.title = title;
//        this.content = content;
//    }



    public Note(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }



    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public String getNgayphathanh() {
        return ngayphathanh;
    }

    public void setNgayphathanh(String ngayphathanh) {
        this.ngayphathanh = ngayphathanh;
    }


    public byte[] getHinhanh() {
        return hinhanh;
    }

    public void setHinhanh(byte[] hinhanh) {
        this.hinhanh = hinhanh;

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }









}
