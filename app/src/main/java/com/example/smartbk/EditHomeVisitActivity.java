package com.example.smartbk;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditHomeVisitActivity extends AppCompatActivity {

    EditText etNama, etKelas, etHasil;
    Button btnUpdate;

    DatabaseHelper dbHelper;

    int id;

    String lokasi;
    String tanggal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_home_visit);

        etNama = findViewById(R.id.etNama);
        etKelas = findViewById(R.id.etKelas);
        etHasil = findViewById(R.id.etHasil);
        btnUpdate = findViewById(R.id.btnUpdate);

        dbHelper = new DatabaseHelper(this);

        id = getIntent().getIntExtra("id", 0);

        String nama = getIntent().getStringExtra("nama");
        String kelas = getIntent().getStringExtra("kelas");
        String hasil = getIntent().getStringExtra("hasil");

        lokasi = getIntent().getStringExtra("lokasi");
        tanggal = getIntent().getStringExtra("tanggal");

        etNama.setText(nama);
        etKelas.setText(kelas);
        etHasil.setText(hasil);

        btnUpdate.setOnClickListener(v -> {

            String namaBaru =
                    etNama.getText().toString().trim();

            String kelasBaru =
                    etKelas.getText().toString().trim();

            String hasilBaru =
                    etHasil.getText().toString().trim();

            if (namaBaru.isEmpty()
                    || kelasBaru.isEmpty()
                    || hasilBaru.isEmpty()) {

                Toast.makeText(
                        this,
                        "Semua data harus diisi",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            boolean berhasil =
                    dbHelper.updateHomeVisit(
                            id,
                            namaBaru,
                            kelasBaru,
                            hasilBaru,
                            lokasi,
                            tanggal
                    );

            if (berhasil) {

                Toast.makeText(
                        this,
                        "Data berhasil diperbarui",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

            } else {

                Toast.makeText(
                        this,
                        "Gagal memperbarui data",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}