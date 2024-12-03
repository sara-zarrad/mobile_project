package com.example.myapp.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "courses")
public class Course {

     @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "course_name")
    private String course_name;

    private int teacher_id;
    @ColumnInfo(name = "coefficient")
    private double coefficient;

    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    // Constructors, getters, and setters
    public Course(String course_name, int teacher_id, double coefficient) {
        this.course_name = course_name;
        this.teacher_id = teacher_id;
        this.coefficient = coefficient;
    }

    public int getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(int teacher_id) {
        this.teacher_id = teacher_id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getTeacherId() {
        return teacher_id;
    }

    public void setTeacherId(int teacherId) {
        this.teacher_id = teacherId;
    }

    public double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(double coefficient) {
        this.coefficient = coefficient;
    }
}
