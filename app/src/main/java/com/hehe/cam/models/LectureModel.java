package com.hehe.cam.models;

public class LectureModel {
    String end,start,tid,subject,room;

    public LectureModel(String start, String end, String tid, String subject, String room) {
        this.end = end;
        this.start = start;
        this.tid = tid;
        this.subject = subject;
        this.room = room;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getRoom() {
        return room;
    }

    public String getEnd() {
        return end;
    }

    public String getStart() {
        return start;
    }

    public String getTid() {
        return tid;
    }

    public String getSubject() {
        return subject;
    }
}
