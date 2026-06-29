package com.example.smartbk;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class KonselingActivity extends AppCompatActivity {

    EditText etNama, etKelas, etMasalah, etSolusi;
    Button btnSimpan;
    ListView listKonseling;

    DatabaseHelper dbHelper;

    ArrayList<String> data;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konseling);

        etNama = findViewById(R.id.etNama);
        etKelas = findViewById(R.id.etKelas);
        etMasalah = findViewById(R.id.etMasalah);
        etSolusi = findViewById(R.id.etSolusi);

        btnSimpan = findViewById(R.id.btnSimpan);
        listKonseling = findViewById(R.id.listKonseling);

        dbHelper = new DatabaseHelper(this);

        data = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                R.layout.item_modern_text,
                data
        );

        listKonseling.setAdapter(adapter);

        loadData();

        btnSimpan.setOnClickListener(v -> simpanKonseling());
    }

    private void simpanKonseling() {

        String nama = etNama.getText().toString().trim();
        String kelas = etKelas.getText().toString().trim();
        String masalah = etMasalah.getText().toString().trim();
        String solusi = etSolusi.getText().toString().trim();

        if (nama.isEmpty() ||
                kelas.isEmpty() ||
                masalah.isEmpty() ||
                solusi.isEmpty()) {

            Toast.makeText(
                    this,
                    "Lengkapi semua data",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String tanggal = new SimpleDateFormat(
                "dd-MM-yyyy",
                Locale.getDefault()
        ).format(new Date());

        boolean berhasil = dbHelper.insertKonseling(
                nama,
                kelas,
                masalah,
                solusi,
                tanggal
        );

        if (berhasil) {

            Toast.makeText(
                    this,
                    "Data konseling tersimpan",
                    Toast.LENGTH_SHORT
            ).show();

            etNama.setText("");
            etKelas.setText("");
            etMasalah.setText("");
            etSolusi.setText("");

            loadData();

        } else {

            Toast.makeText(
                    this,
                    "Gagal menyimpan",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void loadData() {

        data.clear();

        Cursor cursor = dbHelper.getAllKonseling();

        if (cursor.getCount() == 0) {

            data.add("Belum ada data konseling. Catatan baru akan tampil di sini setelah disimpan.");

        } else {

            while (cursor.moveToNext()) {

                int idHomeVisit = cursor.getInt(0);

                String nama = cursor.getString(1);
                String kelas = cursor.getString(2);
                String masalah = cursor.getString(3);
                String solusi = cursor.getString(4);
                String tanggal = cursor.getString(5);

                data.add(
                        "Nama : " + nama +
                                "\nKelas : " + kelas +
                                "\nMasalah : " + masalah +
                                "\nSolusi : " + solusi +
                                "\nTanggal : " + tanggal
                );
            }
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}
