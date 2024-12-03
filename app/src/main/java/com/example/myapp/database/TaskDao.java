package com.example.myapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("SELECT * FROM tasks ORDER BY id DESC")
    LiveData<List<Task>> getAllTasks();

    @Query("SELECT * FROM tasks WHERE date = :date")
    LiveData<List<Task>> getTasksByDate(String date); // Filter tasks by date

    @Query("SELECT * FROM tasks WHERE time = :time")
    LiveData<List<Task>> getTasksByTime(String time); // Filter tasks by time

    @Query("SELECT * FROM tasks WHERE date = :date AND time = :time")
    LiveData<List<Task>> getTasksByDateAndTime(String date, String time); // Filter tasks by both date and time
}
