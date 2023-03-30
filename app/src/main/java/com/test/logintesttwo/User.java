package com.test.logintesttwo;

public class User {
    String user, date_time, task;

    public User() {
    }

    public User(String user, String date_time, String task) {
        this.user = user;
        this.date_time = date_time;
        this.task = task;
    }

    public String getUser() {
        return user;
    }

    public String getDate_time() {
        return date_time;
    }

    public String getTask() {
        return task;
    }
}
