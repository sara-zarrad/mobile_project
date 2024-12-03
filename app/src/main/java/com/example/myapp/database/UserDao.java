package com.example.myapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

@Dao
public interface UserDao {

    @Insert
    void insert(User user);

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    User authenticate(String username, String password);

    @Update
    void update(User user);

    @Query("UPDATE users SET username = :username, email = :email, password = :password WHERE id = :userId")
    void updateUserById(int userId, String username, String email, String password);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM users WHERE id = :userId  LIMIT 1")
    User getUserById(int userId);

    @Update
    void updateUser(User user);

    @Query("DELETE FROM users WHERE id = :userId")
    void deleteUserById(int userId);

    @Query("SELECT * FROM users")
    List<User> getAllUsers();
}
