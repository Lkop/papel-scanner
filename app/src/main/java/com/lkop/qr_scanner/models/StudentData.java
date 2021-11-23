package com.lkop.qr_scanner.models;

public class StudentData {

    private String name;
    private String lastname;
    private String am;

    public StudentData(String name, String lastname, String am) {
        setName(name);
        setLastname(lastname);
        setAM(am);
    }

    public StudentData() {

    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setAM(String am) {
        this.am = am;
    }

    public String getAM() {
        return am;
    }

}
