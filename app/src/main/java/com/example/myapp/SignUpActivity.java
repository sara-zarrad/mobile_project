package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapp.database.DatabaseHelper;

public class SignUpActivity extends AppCompatActivity {
    private EditText usernameField, emailField, passwordField, confirmPasswordField;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        usernameField = findViewById(R.id.etUsername);
        emailField = findViewById(R.id.etEmail);
        passwordField = findViewById(R.id.etPassword);
        confirmPasswordField = findViewById(R.id.etConfirmPassword);

        dbHelper = new DatabaseHelper(this);

        Button btnBackToLogin = findViewById(R.id.btnBackToLogin);
        btnBackToLogin.setOnClickListener(view -> {
            // Intent to go back to Login Activity
            Intent intent = new Intent(SignUpActivity.this, AuthActivity.class);
            startActivity(intent);
            finish();
        });


        Button signUpButton = findViewById(R.id.btnSignUp);
        signUpButton.setOnClickListener(view -> {
            String username = usernameField.getText().toString().trim();
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String confirmPassword = confirmPasswordField.getText().toString().trim();

            if (password.equals(confirmPassword)) {
                if (dbHelper.addUser(username, email, password) != 0) {
                    Toast.makeText(SignUpActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, AuthActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SignUpActivity.this, "Error during registration", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
