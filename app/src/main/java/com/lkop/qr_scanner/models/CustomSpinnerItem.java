package com.lkop.qr_scanner.models;

public class CustomSpinnerItem {

    private int id;
    private String text;

    public CustomSpinnerItem(int id, String text){
        this.id = id;
        this.text = text;
    }

    public int getId(){
        return id;
    }
    public String getText(){
        return text;
    }

}
