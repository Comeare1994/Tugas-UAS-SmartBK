package com.example.smartbk;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetailSuratActivity extends AppCompatActivity {

    TextView tvNama, tvKelas, tvAlasan, tvTanggal;

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_surat);

        tvNama = findViewById(R.id.tvNama);
        tvKelas = findViewById(R.id.tvKelas);
        tvAlasan = findViewById(R.id.tvAlasan);
        tvTanggal = findViewById(R.id.tvTanggal);

        dbHelper = new DatabaseHelper(this);

        int id = getIntent().getIntExtra("id", -1);

        if (id != -1) {

            Cursor cursor = dbHelper.getSuratById(id);

            if (cursor.moveToFirst()) {

                String nama = cursor.getString(
                        cursor.getColumnIndexOrThrow("nama")
                );

                String kelas = cursor.getString(
                        cursor.getColumnIndexOrThrow("kelas")
                );

                String alasan = cursor.getString(
                        cursor.getColumnIndexOrThrow("alasan")
                );

                String tanggal = cursor.getString(
                        cursor.getColumnIndexOrThrow("tanggal")
                );

                tvNama.setText("Nama : " + nama);
                tvKelas.setText("Kelas : " + kelas);
                tvAlasan.setText("Alasan : " + alasan);
                tvTanggal.setText("Tanggal : " + tanggal);
            }

            cursor.close();

        } else {

            tvNama.setText("Data tidak ditemukan");
        }
    }
}