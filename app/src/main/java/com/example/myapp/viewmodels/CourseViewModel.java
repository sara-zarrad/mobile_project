package com.example.myapp.viewmodels;

import android.app.Application;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapp.database.AppDatabase;
import com.example.myapp.database.Course;
import com.example.myapp.database.CourseDao;
import com.example.myapp.database.DatabaseHelper;
import com.example.myapp.database.Teacher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CourseViewModel extends AndroidViewModel {
    private static CourseDao courseDao;
    private final DatabaseHelper databaseHelper;
    private static Executor backgroundExecutor;
    private static Handler mainThreadHandler;
    private final Application application;

    private static final MutableLiveData<List<Course>> _allCourses = new MutableLiveData<>();
    public LiveData<List<Course>> allCourses = _allCourses;

    private final MutableLiveData<List<Teacher>> _allTeachers = new MutableLiveData<>();
    public LiveData<List<Teacher>> allTeachers = _allTeachers;

    private static final MutableLiveData<Boolean> _operationStatus = new MutableLiveData<>();
    public LiveData<Boolean> operationStatus = _operationStatus;

    public CourseViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        AppDatabase database = AppDatabase.getDatabase(application);
        courseDao = database.courseDao();
        databaseHelper = new DatabaseHelper(application);
        backgroundExecutor = Executors.newFixedThreadPool(3);
        mainThreadHandler = new Handler(Looper.getMainLooper());

        // Initial data load
        loadCourses();
        loadTeachers();

        // notification channel
        CourseNotificationHelper.createNotificationChannel(application);
    }
    public static void loadCourses() {
        courseDao.getAllCourses().observeForever(courses -> {
            if (courses != null) {
                _allCourses.postValue(courses);
            }
        });
    }
    public void loadTeachers() {
        backgroundExecutor.execute(() -> {
            List<Teacher> teachers = new ArrayList<>();
            Cursor cursor = databaseHelper.getAllTeachers();

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                    teachers.add(new Teacher(id, name, email));
                }
                cursor.close();
            }

            mainThreadHandler.post(() -> _allTeachers.setValue(teachers));
        });
    }

    public void insertCourse(Course course) {
        backgroundExecutor.execute(() -> {
            long result = courseDao.insert(course);
            mainThreadHandler.post(() -> {
                _operationStatus.setValue(result != -1);
                loadCourses(); // Refresh courses after insert

                // Show notification if course was successfully added
                if (result != -1 && CourseNotificationHelper.canSendNotifications(application)) {
                    CourseNotificationHelper.showCourseAddedNotification(
                            application,
                            course.getCourse_name()
                    );
                }
            });
        });
    }
    public void updateCourse(Course course) {
        backgroundExecutor.execute(() -> {
            int result = courseDao.update(course);
            mainThreadHandler.post(() -> {
                _operationStatus.setValue(result > 0);
                loadCourses(); // Refresh courses after update
            });
        });
    }

    public void deleteCourse(Course course) {
        backgroundExecutor.execute(() -> {
            int result = courseDao.delete(course);
            mainThreadHandler.post(() -> {
                _operationStatus.setValue(result > 0);
                loadCourses(); // Refresh courses after delete
            });
        });
    }

    public static void deleteAllCourses() {
        backgroundExecutor.execute(() -> {
            courseDao.deleteAllCourses();
            mainThreadHandler.post(() -> {
                _operationStatus.setValue(true);
                loadCourses();

            });
        });
    }

    public LiveData<List<Course>> getAllCourses() {
        return allCourses;

    }

    public LiveData<List<Teacher>> getAllTeachers() {
        return allTeachers;
    }

    public LiveData<Boolean> getOperationStatus() {
        return operationStatus;
    }
}
