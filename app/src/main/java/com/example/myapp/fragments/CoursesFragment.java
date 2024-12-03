package com.example.myapp.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapp.R;
import com.example.myapp.adapters.CourseAdapter;
import com.example.myapp.database.Course;
import com.example.myapp.database.DatabaseHelper;
import com.example.myapp.database.Teacher;
import com.example.myapp.databinding.FragmentCoursesBinding;
import com.example.myapp.viewmodels.CourseViewModel;

import java.util.ArrayList;
import java.util.List;

public class CoursesFragment extends Fragment {

    private FragmentCoursesBinding binding;
    private CourseViewModel courseViewModel;
    private CourseAdapter courseAdapter;
    private List<Teacher> teacherList = new ArrayList<>();
    private final List<Course> allCourses = new ArrayList<>();
    private final List<Course> filteredCourses = new ArrayList<>();
    private DatabaseHelper dbHelper;

    // Notification permission launcher
    private final ActivityResultLauncher<String> requestNotificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(requireContext(), "Notification permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Notifications are disabled", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCoursesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(requireContext());

        // Check and request notification permission
        checkNotificationPermission();

        // Initialize ViewModel
        courseViewModel = new ViewModelProvider(this).get(CourseViewModel.class);

        // Setup RecyclerView
        setupRecyclerView();

        // Setup Add Course Button
        setupAddCourseButton();

        // Observe Courses and Teachers
        observeCourses();
        observeTeachers();

        // Setup Search Functionality
        setupSearchFunctionality();
    }

    private void checkNotificationPermission() {
        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void setupRecyclerView() {
        // Initialize CourseAdapter with DatabaseHelper
        courseAdapter = new CourseAdapter(dbHelper);
        binding.recyclerViewCourses.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewCourses.setAdapter(courseAdapter);

        // Setup item click listeners
        courseAdapter.setOnItemClickListener(new CourseAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Course course) {
                showAddEditCourseDialog(course);
            }

            @Override
            public void onDeleteClick(Course course) {
                courseViewModel.deleteCourse(course);
            }
        });
    }

    private void setupAddCourseButton() {
        binding.buttonAddCourse.setOnClickListener(v -> showAddEditCourseDialog(null));
    }

    private void observeCourses() {
        courseViewModel.getAllCourses().observe(getViewLifecycleOwner(), courses -> {
            if (courses != null && !courses.isEmpty()) {
                // Log the courses
                for (Course course : courses) {
                    Log.d("CoursesFragment", "Course: " + course.getCourse_name());
                }

                // Update course lists
                allCourses.clear();
                allCourses.addAll(courses);
                filteredCourses.clear();
                filteredCourses.addAll(courses);

                // Update adapter
                courseAdapter.submitList(filteredCourses);

                // Show/hide empty state
                binding.textViewNoCourses.setVisibility(
                        courses.isEmpty() ? View.VISIBLE : View.GONE
                );
            } else {
                Log.d("CoursesFragment", "No courses available.");
                binding.textViewNoCourses.setVisibility(View.VISIBLE);
            }
        });
    }

    private void observeTeachers() {
        courseViewModel.getAllTeachers().observe(getViewLifecycleOwner(), teachers -> teacherList = teachers);
    }

    private void showAddEditCourseDialog(Course existingCourse) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_edit_course, null);

        EditText courseNameEditText = dialogView.findViewById(R.id.editTextCourseName);
        EditText coefficientEditText = dialogView.findViewById(R.id.editTextCoefficient);
        Spinner teacherSpinner = dialogView.findViewById(R.id.spinnerTeachers);

        // Populate teacher spinner
        List<String> teacherNames = new ArrayList<>();
        for (Teacher teacher : teacherList) {
            teacherNames.add(teacher.getName());
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                teacherNames
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teacherSpinner.setAdapter(spinnerAdapter);

        // If editing an existing course, pre-populate fields
        if (existingCourse != null) {
            courseNameEditText.setText(existingCourse.getCourse_name());
            coefficientEditText.setText(String.valueOf(existingCourse.getCoefficient()));

            // Set selected teacher
            for (int i = 0; i < teacherList.size(); i++) {
                if (teacherList.get(i).getId() == existingCourse.getTeacherId()) {
                    teacherSpinner.setSelection(i);
                    break;
                }
            }
        }

        builder.setTitle(existingCourse == null ? "Add Course" : "Edit Course")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String courseName = courseNameEditText.getText().toString().trim();
                    String coefficientStr = coefficientEditText.getText().toString().trim();
                    int selectedTeacherIndex = teacherSpinner.getSelectedItemPosition();

                    if (courseName.isEmpty() || coefficientStr.isEmpty()) {
                        Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        double coefficient = Double.parseDouble(coefficientStr);
                        int teacherId = teacherList.get(selectedTeacherIndex).getId();

                        Course course = existingCourse == null
                                ? new Course(courseName, teacherId, coefficient)
                                : existingCourse;

                        course.setCourse_name(courseName);
                        course.setTeacherId(teacherId);
                        course.setCoefficient(coefficient);

                        if (existingCourse == null) {
                            courseViewModel.insertCourse(course);
                        } else {
                            courseViewModel.updateCourse(course);
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(requireContext(), "Invalid coefficient", Toast.LENGTH_SHORT).show();
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(requireContext(), "Please select a teacher", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupSearchFunctionality() {
        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                filterCourses(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // No action needed
            }
        });
    }

    private void filterCourses(String query) {
        filteredCourses.clear();
        if (query.isEmpty()) {
            // Show all courses if the query is empty
            filteredCourses.addAll(allCourses);
        } else {
            // Filter the courses based on the query (by course_name)
            for (Course course : allCourses) {
                if (course.getCourse_name().toLowerCase().contains(query.toLowerCase())) {
                    filteredCourses.add(course);
                }
            }
        }
        // Submit the filtered list to the adapter
        courseAdapter.submitList(new ArrayList<>(filteredCourses));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        // Close the database helper to prevent resource leaks
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
