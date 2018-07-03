package com.teamdoor.android.door.Entity;

public class Report {
    private String contents;
    private long date;

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public Report() {
    }

    public Report(String contents, long date) {
        this.contents = contents;
        this.date = date;
    }
}
