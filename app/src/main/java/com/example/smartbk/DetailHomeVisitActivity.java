package com.example.smartbk;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetailHomeVisitActivity extends AppCompatActivity {

    TextView txtNama,
            txtKelas,
            txtHasil,
            txtLokasi,
            txtTanggal;

    Button btnEdit;

    int id;

    String nama;
    String kelas;
    String hasil;
    String lokasi;
    String tanggal;

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_home_visit);

        txtNama = findViewById(R.id.txtNama);
        txtKelas = findViewById(R.id.txtKelas);
        txtHasil = findViewById(R.id.txtHasil);
        txtLokasi = findViewById(R.id.txtLokasi);
        txtTanggal = findViewById(R.id.txtTanggal);

        btnEdit = findViewById(R.id.btnEdit);

        dbHelper = new DatabaseHelper(this);

        id = getIntent().getIntExtra("id", 0);

        nama = getIntent().getStringExtra("nama");
        kelas = getIntent().getStringExtra("kelas");
        hasil = getIntent().getStringExtra("hasil");
        lokasi = getIntent().getStringExtra("lokasi");
        tanggal = getIntent().getStringExtra("tanggal");

        if (id != 0 && nama == null) {
            loadDataById(id);
        }

        txtNama.setText("Nama : " + nama);
        txtKelas.setText("Kelas : " + kelas);
        txtHasil.setText("Hasil Kunjungan :\n" + hasil);
        txtLokasi.setText("Lokasi : " + lokasi);
        txtTanggal.setText("Tanggal : " + tanggal);

        btnEdit.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            DetailHomeVisitActivity.this,
                            EditHomeVisitActivity.class
                    );

            intent.putExtra("id", id);
            intent.putExtra("nama", nama);
            intent.putExtra("kelas", kelas);
            intent.putExtra("hasil", hasil);

            startActivity(intent);
        });
    }

    private void loadDataById(int id) {

        Cursor cursor = dbHelper.getHomeVisitById(id);

        if (cursor.moveToFirst()) {

            nama = cursor.getString(
                    cursor.getColumnIndexOrThrow("nama")
            );
            kelas = cursor.getString(
                    cursor.getColumnIndexOrThrow("kelas")
            );
            hasil = cursor.getString(
                    cursor.getColumnIndexOrThrow("hasil")
            );
            lokasi = cursor.getString(
                    cursor.getColumnIndexOrThrow("lokasi")
            );
            tanggal = cursor.getString(
                    cursor.getColumnIndexOrThrow("tanggal")
            );
        }

        cursor.close();
    }
}
