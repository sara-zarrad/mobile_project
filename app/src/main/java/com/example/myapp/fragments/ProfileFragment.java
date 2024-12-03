package com.example.myapp.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapp.AuthActivity;
import com.example.myapp.SessionManager;
import com.example.myapp.database.User;
import com.example.myapp.databinding.FragmentProfileBinding;
import com.example.myapp.viewmodels.ProfileViewModel;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;
    private final int userId;
    private final String username, email, password;
    private Bitmap selectedProfilePicture;




    // Permission launcher
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openImagePicker();
                } else {
                    Toast.makeText(getContext(), "Permission denied to read images", Toast.LENGTH_SHORT).show();
                }
            });

    // Image picker launcher
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        Uri imageUri = data.getData();
                        try {
                            InputStream imageStream = requireContext().getContentResolver().openInputStream(imageUri);
                            selectedProfilePicture = BitmapFactory.decodeStream(imageStream);
                            binding.imageViewProfilePic.setImageBitmap(selectedProfilePicture);
                        } catch (FileNotFoundException e) {
                            Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    public ProfileFragment(int userId, String username, String email, String password) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        // Set up profile picture click listener for image selection
        binding.imageViewProfilePic.setOnClickListener(v -> checkAndRequestPermission());
        return binding.getRoot();
    }

    private void checkAndRequestPermission() {
        // Check permission based on Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ uses READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES) ==
                    PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } else {
            // For older versions
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void updateUI(User user) {
        if (user != null) {
            binding.textViewUsername.setText(String.format("Welcome back, : %s", user.getUsername()));
            binding.textViewEmail.setText(String.format("Email: %s", user.getEmail()));
            binding.editTextUsername.setText(user.getUsername());
            binding.editTextPassword.setText(user.getPassword());
            binding.editTextEmail.setText(user.getEmail());
        }
    }

    private void updateProfile() {
        String updatedUsername = Objects.requireNonNull(binding.editTextUsername.getText()).toString().trim();
        String updatedPassword = Objects.requireNonNull(binding.editTextPassword.getText()).toString().trim();
        String updatedEmail = Objects.requireNonNull(binding.editTextEmail.getText()).toString().trim();

        if (updatedUsername.isEmpty() || updatedEmail.isEmpty()) {
            Toast.makeText(getContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        User updatedUser = new User(userId, updatedUsername, updatedEmail, updatedPassword);
        viewModel.updateUserProfile(updatedUser);

        // If a profile picture is selected, you could save it here

        if (selectedProfilePicture != null) {
            // Example: Save or process selectedProfilePicture
            Toast.makeText(getContext(), "Profile picture selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void logout() {
        new SessionManager(requireContext()).clearSession();
        startActivity(new Intent(requireContext(), AuthActivity.class));
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
    private boolean isContextValid() {
        return getContext() != null && isAdded();
    }

}
