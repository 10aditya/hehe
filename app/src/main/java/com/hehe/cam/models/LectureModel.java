package com.hehe.cam.models;

public class LectureModel {
    String end,start,tid,subject;

    public LectureModel(String start, String end, String tid, String subject) {
        this.end = end;
        this.start = start;
        this.tid = tid;
        this.subject = subject;
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
