package com.example.myapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "my_app_database";
    private static final int DATABASE_VERSION = 4 ; // Increment version to support migration

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_TEACHERS = "teachers";
    private static final String TABLE_COURSES = "courses";
    private static final String TABLE_TASKS = "tasks";  // New User table

    // Common Columns
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_COEFFICIENT = "coefficient";
    // Course Specific Columns
    private static final String COLUMN_COURSE_NAME = "course_name";
    public static final String COLUMN_TEACHER_ID = "teacher_id";
    // Task-Specific Columns
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TIME = "time";
    // Create Tables SQL
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USERNAME + " TEXT NOT NULL UNIQUE,"
            + COLUMN_EMAIL + " TEXT NOT NULL UNIQUE,"
            + COLUMN_PASSWORD + " TEXT NOT NULL"
            + ")";

    private static final String CREATE_TABLE_TEACHERS = "CREATE TABLE " + TABLE_TEACHERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME + " TEXT NOT NULL,"
            + COLUMN_EMAIL + " TEXT NOT NULL UNIQUE"
            + ")";

    private static final String CREATE_TABLE_COURSES = "CREATE TABLE " + TABLE_COURSES + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_COURSE_NAME + " TEXT NOT NULL, "
            + COLUMN_TEACHER_ID + " INTEGER, "
            + COLUMN_COEFFICIENT + " REAL, "
            + "FOREIGN KEY(" + COLUMN_TEACHER_ID + ") REFERENCES " + TABLE_TEACHERS + "(" + COLUMN_ID + ")"
            + ")";
    private static final String CREATE_TABLE_TASKS = "CREATE TABLE " + TABLE_TASKS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " TEXT NOT NULL, "
            + COLUMN_DATE + " TEXT NOT NULL, "
            + COLUMN_TIME + " TEXT NOT NULL"
            + ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_TEACHERS);
        db.execSQL(CREATE_TABLE_COURSES);
        db.execSQL(CREATE_TABLE_TASKS);
        ContentValues values = new ContentValues();

        // Insert Teacher 1
        values.put(COLUMN_NAME, "Hend ben ayed");
        values.put(COLUMN_EMAIL, "hend.benayed@gmail.com");
        db.insert(TABLE_TEACHERS, null, values);

        // Add default admin user
        ContentValues adminValues = new ContentValues();
        adminValues.put(COLUMN_USERNAME, "sara");
        adminValues.put(COLUMN_EMAIL, "sarra.zarrad23@gmail.com");
        adminValues.put(COLUMN_PASSWORD, "sara");
        db.insert(TABLE_USERS, null, adminValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop tables in reverse order of creation to respect foreign key constraints
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEACHERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    // User CRUD Operations
    public long addUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);

        return db.insert(TABLE_USERS, null, values);
    }

    public int authenticateUserAndReturnId(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        int userId = -1;

        try (Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ID},
                COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{username, password},
                null, null, null)) {

            if (cursor != null && cursor.moveToFirst()) {
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            }
        } catch (Exception e) {
            Log.e("DatabaseError", "Error authenticating user with username: " + username, e);
        }

        return userId;
    }


    public User findUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;

        try {
            Cursor cursor = db.query(TABLE_USERS,
                    null,
                    COLUMN_ID + "=?",
                    new String[]{String.valueOf(userId)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                user = new User(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
                );
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("DatabaseError", "Error while fetching user with ID: " + userId, e);
        }

        return user;
    }

    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASSWORD, user.getPassword());


        return db.update(TABLE_USERS,
                values,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(user.getId())});
    }

    public int deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_USERS,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(userId)});
        db.close();
        return result;
    }

    public boolean clearAllUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_USERS, null, null);

        return result > 0;
    }

    // For checking if a username already exists
    public boolean isUsernameTaken(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ID},
                COLUMN_USERNAME + "=?",
                new String[]{username},
                null, null, null);

        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        return exists;
    }

    // For checking if an email already exists
    public boolean isEmailTaken(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ID},
                COLUMN_EMAIL + "=?",
                new String[]{email},
                null, null, null);

        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        return exists;
    }

    // Add a new teacher
    public long addTeacher(String name, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);

        return db.insert(TABLE_TEACHERS, null, values);
    }

    // Get a teacher by ID
    public Cursor findTeacherById(int teacherId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_TEACHERS,
                null,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(teacherId)},
                null, null, null);
    }

    // Update a teacher's details
    public int updateTeacher(int teacherId, String name, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);

        return db.update(TABLE_TEACHERS,
                values,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(teacherId)});
    }

    // Delete a teacher
    public int deleteTeacher(int teacherId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_TEACHERS,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(teacherId)});
    }


    public Cursor getAllTeachers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM teachers", null);  // Adjust this SQL query based on your table structure
    }
    // Add a new course
    public long addCourse(String courseName, String type, int teacherId, double coefficient) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_COURSE_NAME, courseName);
        values.put(COLUMN_TEACHER_ID, teacherId);
        values.put(COLUMN_COEFFICIENT, coefficient);

        return db.insert(TABLE_COURSES, null, values);
    }

    // Get a course by ID
    public Cursor findCourseById(int courseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_COURSES,
                null,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(courseId)},
                null, null, null);
    }

    // Update a course's details
    public int updateCourse(int courseId, String courseName, String type, int teacherId, double coefficient) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_COURSE_NAME, courseName);
        values.put(COLUMN_TEACHER_ID, teacherId);
        values.put(COLUMN_COEFFICIENT, coefficient);

        return db.update(TABLE_COURSES,
                values,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(courseId)});
    }

    // Delete a course
    public int deleteCourse(int courseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_COURSES,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(courseId)});
    }

    // Get all courses
    public Cursor getAllCourses() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_COURSES,
                null,
                null, null,
                null, null, null);
    }

    // Get courses by teacher ID
    public Cursor getCoursesByTeacherId(int teacherId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_COURSES,
                null,
                COLUMN_TEACHER_ID + "=?",
                new String[]{String.valueOf(teacherId)},
                null, null, null);
    }
    // Task CRUD Operations
    public long addTask(String name, String date, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, name);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_TIME, time);

        return db.insert(TABLE_TASKS, null, values);
    }

    public Cursor getAllTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_TASKS, null, null, null, null, null, null);
    }

    public int updateTask(int taskId, String name, String date, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, name);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_TIME, time);

        return db.update(TABLE_TASKS, values, COLUMN_ID + "=?", new String[]{String.valueOf(taskId)});
    }

    public int deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_TASKS, COLUMN_ID + "=?", new String[]{String.valueOf(taskId)});
    }

    public Cursor findTaskById(int taskId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_TASKS, null, COLUMN_ID + "=?", new String[]{String.valueOf(taskId)}, null, null, null);
    }
}
