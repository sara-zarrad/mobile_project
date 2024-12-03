package com.example.myapp.database;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TeacherDao {

    @Insert
    void insert(Teacher teacher);

    @Update
    void update(Teacher teacher);

    @Delete
    void delete(Teacher teacher);

    @Query("DELETE FROM teachers")
    void deleteAllTeachers();

    @Query("SELECT * FROM teachers ORDER BY name ASC")
    LiveData<List<Teacher>> getAllTeachers();
}
