package com.uakgul.moviedb.moviet.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Crew {

    @SerializedName("credit_id")
    @Expose
    private String creditId;

    @SerializedName("department")
    @Expose
    private String department;

    @SerializedName("gender")
    @Expose
    private Integer gender;

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("job")
    @Expose
    private String job;

    @SerializedName("name")
    @Expose
    private String name;

    @Override
    public String toString() {
        return "Crew{" +
                "creditId='" + creditId + '\'' +
                ", department='" + department + '\'' +
                ", gender=" + gender +
                ", id=" + id +
                ", job='" + job + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public String getCreditId() {
        return creditId;
    }

    public void setCreditId(String creditId) {
        this.creditId = creditId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
