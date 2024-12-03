package com.example.myapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import androidx.room.Update;
import java.util.List;

@Dao
public interface CourseDao {

    @Insert
    long insert(Course course);

    @Update
    int update(Course course);

    @Delete
    int delete(Course course);

    @Query("SELECT * FROM courses ORDER BY id DESC")
    LiveData<List<Course>> getAllCourses();

    @Query("DELETE FROM courses")
    void deleteAllCourses();
}
