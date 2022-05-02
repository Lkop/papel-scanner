package com.lkop.qr_scanner.models;

public class Classroom {

    private int id;
    private int creator;
    private String token;
    private String description;
    private String subject_name;
    private String subject_professor;
    private String date;
    private String type;
    private int timer;

    public Classroom(int id, int creator, String token, String description, String subject_name, String subject_professor, String date, String type) {
        this.id = id;
        this.creator = creator;
        this.token = token;
        this.description = description;
        this.subject_name = subject_name;
        this.subject_professor = subject_professor;
        this.date = date;
        this.type = type;
        this.timer = 10 * 60 * 1000;
    }

    public int getId() {
        return id;
    }

    public int getCreator() {
        return creator;
    }

    public String getClassroomToken() {
        return token;
    }

    public String getDescription() {
        return description;
    }

    public String getSubjectName() {
        return subject_name;
    }

    public String getSubjectProfessor() {
        return subject_professor;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }
}
