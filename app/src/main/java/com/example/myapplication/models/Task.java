package com.example.myapplication.models;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Task {
    private int id;

    private int status;

    @JsonProperty("date_created")
    private String dateCreated;

    @JsonProperty("date_updated")
    private String dateUpdated;

    @JsonProperty("date_closed")
    private String dateClosed;

    @JsonProperty("date_expire")
    private String dateExpire;

    @JsonProperty("reporter_login")
    private String reporterLogin;

    @JsonProperty("master_login")
    private String masterLogin;

    @Nullable
    @JsonProperty("assign_login")
    private String assignLogin;

    @JsonProperty("location_title")
    private String locationTitle;

    private int priority;

    private String text;

    private String title;

    public String getStatusLabel() {
        switch (status) {
            case 0: return "New";
            case 1: return "Accepted";
            case 2: return "In progress";
            case 3: return "Done";
        }

        return "N/A";
    }

    public String getPriorityLabel() {
        switch (status) {
            case 0: return "Low";
            case 1: return "Medium";
            case 2: return "High";
            case 3: return "Very high";
        }

        return "N/A";
    }

    public String getDateCreatedString() {
        return new Date(dateCreated).toString();
    }

    public String getDateUpdatedString() {
        return new Date(dateUpdated).toString();
    }

    public String getDateClosedString() {
        return new Date(dateClosed).toString();
    }

    public String getDateExpireString() {
        return new Date(dateExpire).toString();
    }

    public static List<Task> createMocks(int count) {
        List<Task> tasks = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Task task = new Task();
            task.setId(i);
            task.setTitle("Задача " + i);
            task.setText("Описание задачи " + i);
            task.setStatus(0);
            task.setPriority(2);
            task.setAssignLogin("Рабочий " + i);
            task.setMasterLogin("Мастер " + i);
            task.setReporterLogin("Начальник цеха");

            tasks.add(task);
        }

        return tasks;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getDateClosed() {
        return dateClosed;
    }

    public void setDateClosed(String dateClosed) {
        this.dateClosed = dateClosed;
    }

    public String getDateExpire() {
        return dateExpire;
    }

    public void setDateExpire(String dateExpire) {
        this.dateExpire = dateExpire;
    }

    public String getReporterLogin() {
        return reporterLogin;
    }

    public void setReporterLogin(String reporterLogin) {
        this.reporterLogin = reporterLogin;
    }

    public String getMasterLogin() {
        return masterLogin;
    }

    public void setMasterLogin(String masterLogin) {
        this.masterLogin = masterLogin;
    }

    @Nullable
    public String getAssignLogin() {
        return assignLogin;
    }

    public void setAssignLogin(@Nullable String assignLogin) {
        this.assignLogin = assignLogin;
    }

    public String getLocationTitle() {
        return locationTitle;
    }

    public void setLocationTitle(String locationTitle) {
        this.locationTitle = locationTitle;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
