package com.lkop.qr_scanner.models;

public class ClassroomInfo {

    private String id;
    private String token;
    private String subject_name;
    private String subject_professor;
    private String date;
    private String type;

    public ClassroomInfo(String id, String token, String subject_name, String subject_professor, String date, String type) {

        this.id = id;
        this.token = token;
        this.subject_name = subject_name;
        this.subject_professor = subject_professor;
        this.date = date;
        this.type = type;
    }

    public String getID() {
        return id;
    }
    public String getClassroomToken() {
        return token;
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

}
