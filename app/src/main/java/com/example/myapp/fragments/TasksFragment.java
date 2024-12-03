package com.example.myapp.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapp.MainActivity;
import com.example.myapp.adapters.TaskAdapter;
import com.example.myapp.database.Task;
import com.example.myapp.databinding.FragmentTasksBinding;
import com.example.myapp.viewmodels.TaskNotificationHelper;
import com.example.myapp.viewmodels.TaskViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TasksFragment extends Fragment {

    private FragmentTasksBinding binding;
    private TaskViewModel taskViewModel;
    private TaskAdapter taskAdapter;
    private String selectedTime;
    private String selectedDate;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTasksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize RecyclerView
        binding.recyclerViewTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        taskAdapter = new TaskAdapter();
        binding.recyclerViewTasks.setAdapter(taskAdapter);

        // Initialize ViewModel
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        taskViewModel.getAllTasks().observe(getViewLifecycleOwner(), tasks -> taskAdapter.submitList(tasks));

        // Handle time selection
        binding.textViewTimeLabel.setOnClickListener(v -> showTimePickerDialog());
        binding.textViewDateLabel.setOnClickListener(v -> showDatePicker());

        // Add a task
        binding.buttonAddTask.setOnClickListener(v -> {
            String taskName = binding.editTextTaskName.getText().toString().trim();
            String taskDate = binding.textViewDateLabel.getText().toString().trim();

            if (!taskName.isEmpty() && !taskDate.isEmpty() && selectedTime != null) {
                Task task = new Task(taskName, taskDate, selectedTime);
                taskViewModel.insert(task);
                binding.editTextTaskName.setText("");
                binding.textViewDateLabel.setText("");
                binding.textViewTimeLabel.setText("");
                selectedTime = null;
                selectedDate= null;
                Toast.makeText(getContext(), "Task added", Toast.LENGTH_SHORT).show();

                // Show notification for added task
                TaskNotificationHelper.showTaskAddedNotification(requireContext(), taskName);
            } else {
                Toast.makeText(getContext(), "Task name, date, and time cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        // Delete task
        taskAdapter.setOnTaskDeleteListener(task -> {
            taskViewModel.delete(task);
            Toast.makeText(getContext(), "Task deleted", Toast.LENGTH_SHORT).show();
        });
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                (TimePicker view, int hourOfDay, int minuteOfHour) -> {
                    selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
                    binding.textViewTimeLabel.setText(selectedTime);
                },
                hour,
                minute,
                true
        );
        timePickerDialog.show();
    }
    private void showDatePicker() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH); // Zero-based
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Adjust the month since it's zero-based
                    selectedMonth = selectedMonth + 1;

                    // Format the date as dd/MM/yyyy
                    selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", selectedDay, selectedMonth, selectedYear);
                    binding.textViewDateLabel.setText(selectedDate);
                },
                day, month, year
        );

        datePickerDialog.show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
