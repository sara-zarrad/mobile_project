package com.example.myapp.fragments;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapp.R;
import com.example.myapp.adapters.TeacherAdapter;
import com.example.myapp.database.DatabaseHelper;
import com.example.myapp.database.Teacher;
import com.example.myapp.databinding.FragmentTeacherBinding;

import java.util.ArrayList;
import java.util.List;

public class TeachersFragment extends Fragment {

    private FragmentTeacherBinding binding;
    private DatabaseHelper dbHelper;
    private TeacherAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTeacherBinding.inflate(inflater, container, false);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(requireContext());

        setupRecyclerView();
        setupAddTeacherButton();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.recyclerViewTeachers.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new TeacherAdapter();

        // Set email click listener
        adapter.setOnEmailClickListener(this::sendEmailToTeacher);

        binding.recyclerViewTeachers.setAdapter(adapter);

        loadTeachers();
    }

    private void sendEmailToTeacher(Teacher teacher) {
        // Prepare email intent
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + teacher.getEmail()));

        // Set subject with teacher's name
        String emailSubject = String.format("Email pour %s", teacher.getName());
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);

        // Check if there's an email client available
        try {
            startActivity(Intent.createChooser(emailIntent, "Envoyer un email..."));
        } catch (ActivityNotFoundException e) {
            // Handle case where no email app is installed
            Toast.makeText(requireContext(),
                    "Aucune application email n'est installée",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void loadTeachers() {
        List<Teacher> teachers = new ArrayList<>();
        Cursor cursor = dbHelper.getAllTeachers();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                teachers.add(new Teacher(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("email"))
                ));
            }
            cursor.close();
        }

        adapter.submitList(teachers);
    }

    private void setupAddTeacherButton() {
        binding.buttonAddTeacher.setOnClickListener(v -> showAddTeacherDialog());
    }

    private void showAddTeacherDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_teacher, null);
        EditText editTextName = dialogView.findViewById(R.id.editTextTeacherName);
        EditText editTextEmail = dialogView.findViewById(R.id.editTextTeacherEmail);

        new AlertDialog.Builder(requireContext())
                .setTitle("Ajouter un enseignant")
                .setView(dialogView)
                .setPositiveButton("Enregistrer", (dialog, which) -> {
                    String name = editTextName.getText().toString().trim();
                    String email = editTextEmail.getText().toString().trim();

                    if (name.isEmpty() || email.isEmpty()) {
                        Toast.makeText(requireContext(), "Tous les champs sont requis", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    long result = dbHelper.addTeacher(name, email);
                    if (result != -1) {
                        Toast.makeText(requireContext(), "Enseignant ajouté avec succès", Toast.LENGTH_SHORT).show();
                        loadTeachers();
                    } else {
                        Toast.makeText(requireContext(), "Échec de l'ajout de l'enseignant", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}