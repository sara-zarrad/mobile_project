package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapp.database.DatabaseHelper;
import com.example.myapp.database.User;

public class AuthActivity extends AppCompatActivity {
    private EditText usernameField, passwordField;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Initialize database
        dbHelper = new DatabaseHelper(this);

        // Optionally force database creation by getting writable database
        dbHelper.getWritableDatabase();

        // Initialize views and set up listeners
        usernameField = findViewById(R.id.username);
        passwordField = findViewById(R.id.password);

        findViewById(R.id.loginButton).setOnClickListener(v -> handleLogin());
        findViewById(R.id.btnSignUpLink).setOnClickListener(v ->
                startActivity(new Intent(this, SignUpActivity.class)));
    }

    private void handleLogin() {
        dbHelper = new DatabaseHelper(this);
        String username = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        // Use database helper to authenticate
        int userId = dbHelper.authenticateUserAndReturnId(username, password);
        if (userId != -1) {
            User user = dbHelper.findUserById(userId);
            // Start home activity with user data
            startActivity(new Intent(this, HomeActivity.class)
                    .putExtra("userId", userId)
                    .putExtra("username", user.getUsername())
                    .putExtra("password", user.getPassword())
                    .putExtra("email", user.getEmail()));
            finish();
        } else {
            Toast.makeText(this, "Invalid login", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}