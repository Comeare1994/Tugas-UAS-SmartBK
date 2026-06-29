package com.example.smartbk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;
    Spinner spinnerRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        spinnerRole = findViewById(R.id.spinnerRole);
        btnLogin = findViewById(R.id.btnLogin);

        String[] roles = {"Guru BK", "Wali Kelas", "Kepala Sekolah"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                roles
        );
        spinnerRole.setAdapter(adapter);

        btnLogin.setOnClickListener(v -> {
            String user = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            String role = spinnerRole.getSelectedItem().toString();

            if (isValidLogin(user, pass, role)) {

                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                intent.putExtra("role", role);
                startActivity(intent);

            } else {
                Toast.makeText(this, "Login gagal atau role tidak sesuai", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidLogin(String user, String pass, String role) {
        return AccountStore.isValidLogin(this, user, pass, role);
    }
}
