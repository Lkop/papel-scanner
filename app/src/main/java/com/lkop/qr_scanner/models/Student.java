package com.lkop.qr_scanner.models;

public class Student {

    private int id;
    private String name;
    private String lastname;
    private long am;
    private long pass_id;

    public Student(int id, String name, String lastname, long am, long pass_id) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.am = am;
        this.pass_id = pass_id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public long getAM() {
        return am;
    }

    public long getPassId() {
        return pass_id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        Student other = (Student) obj;
        if (this.id != other.id) {
            return false;
        }

        return true;
    }
}
