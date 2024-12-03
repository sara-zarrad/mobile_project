package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapp.database.User;
import com.example.myapp.databinding.ActivityHomeBinding;
import com.example.myapp.fragments.CoursesFragment;
import com.example.myapp.fragments.ProfileFragment;
import com.example.myapp.fragments.TasksFragment;
import com.example.myapp.fragments.TeachersFragment;
import com.example.myapp.viewmodels.CourseViewModel;

public class HomeActivity extends AppCompatActivity {
    private int userId;
    private String username, email, password;
    private User AuthenticatedUser = new User(-1, null, null, null);
    private CourseViewModel courseViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityHomeBinding binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ViewModel
        courseViewModel = new ViewModelProvider(this).get(CourseViewModel.class);

        // Get user data from intent
        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", -1);
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");
        email = intent.getStringExtra("email");

        AuthenticatedUser.setId(userId);
        AuthenticatedUser.setUsername(username);
        AuthenticatedUser.setPassword(password);
        AuthenticatedUser.setEmail(email);
        SessionManager sm = new SessionManager(this);
        // Set up the toolbar (AppBar)
        setSupportActionBar(binding.toolbar);

        // Set up bottom navigation
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_profile) {
                fragment = new ProfileFragment(userId, username, email, password);
            } else if (itemId == R.id.nav_courses) {
                fragment = new CoursesFragment();
            } else if (itemId == R.id.nav_tasks) {
                fragment = new TasksFragment();
            } else {
                return false;
            }

            loadFragment(fragment);
            return true;
        });

        // Load default fragment (Courses)
        loadFragment(new ProfileFragment(userId,username,email,password));
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    public User getAuthenticatedUser() {
        return AuthenticatedUser;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.buttonAddCourse) {
            loadFragment(new CoursesFragment());
            return true;
        } else if (itemId == R.id.action_clear_courses) {
            new AlertDialog.Builder(this)
                    .setTitle("Clear All Courses")
                    .setMessage("Are you sure you want to delete all courses?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        CourseViewModel.deleteAllCourses();
                        Toast.makeText(this, "All courses deleted", Toast.LENGTH_SHORT).show();
                        loadFragment(new CoursesFragment());
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        } else if (itemId == R.id.action_manage_teachers) {
            loadFragment(new TeachersFragment());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void updateAuthenticatedUser(User user) {
        this.AuthenticatedUser = user;
    }

    public CourseViewModel getCourseViewModel() {
        return courseViewModel;
    }

    public void setCourseViewModel(CourseViewModel courseViewModel) {
        this.courseViewModel = courseViewModel;
    }
}