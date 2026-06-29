package com.example.smartbk;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AccountActivity extends AppCompatActivity {

    Spinner spinnerRole;
    EditText etUsername, etPassword;
    Button btnSimpanAkun;

    String[] roles = {"Guru BK", "Wali Kelas", "Kepala Sekolah"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        spinnerRole = findViewById(R.id.spinnerRole);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnSimpanAkun = findViewById(R.id.btnSimpanAkun);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                roles
        );
        spinnerRole.setAdapter(adapter);

        spinnerRole.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                loadSelectedAccount();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        btnSimpanAkun.setOnClickListener(v -> {
            String role = spinnerRole.getSelectedItem().toString();
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Username dan password wajib diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            AccountStore.saveAccount(this, role, username, password);
            Toast.makeText(this, "Akun " + role + " berhasil diperbarui", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadSelectedAccount() {
        String role = spinnerRole.getSelectedItem().toString();
        etUsername.setText(AccountStore.getUsername(this, role));
        etPassword.setText(AccountStore.getPassword(this, role));
    }
}
