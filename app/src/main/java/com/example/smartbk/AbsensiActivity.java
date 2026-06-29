package com.example.smartbk;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AbsensiActivity extends AppCompatActivity {

    Spinner spinnerSiswa, spinnerStatus;
    Button btnSimpan;

    DatabaseHelper dbHelper;

    ArrayList<String> siswaList = new ArrayList<>();
    ArrayList<Integer> siswaIdList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absensi);

        dbHelper = new DatabaseHelper(this);

        spinnerSiswa = findViewById(R.id.spinnerSiswa);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnSimpan = findViewById(R.id.btnSimpanAbsensi);

        loadSiswa();
        loadStatus();

        btnSimpan.setOnClickListener(v -> simpanAbsensi());
    }

    private void loadSiswa() {

        Cursor cursor = dbHelper.getAllSiswa();

        siswaList.clear();
        siswaIdList.clear();

        while (cursor.moveToNext()) {
            siswaIdList.add(cursor.getInt(0));
            siswaList.add(cursor.getString(1) + " - " + cursor.getString(2));
        }

        spinnerSiswa.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                siswaList
        ));
    }

    private void loadStatus() {

        String[] status = {"Hadir", "Izin", "Sakit", "Alfa"};

        spinnerStatus.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                status
        ));
    }

    private void simpanAbsensi() {

        int index = spinnerSiswa.getSelectedItemPosition();
        int siswaId = siswaIdList.get(index);

        String status = spinnerStatus.getSelectedItem().toString();

        String tanggal = new SimpleDateFormat(
                "dd-MM-yyyy",
                Locale.getDefault()
        ).format(new Date());

        String bulan = new SimpleDateFormat(
                "yyyy-MM",
                Locale.getDefault()
        ).format(new Date());

        boolean hasil = dbHelper.insertAbsensi(
                siswaId,
                tanggal,
                bulan,
                status
        );

        Toast.makeText(this,
                hasil ? "Absensi tersimpan" : "Gagal simpan absensi",
                Toast.LENGTH_SHORT).show();
    }
}