package com.example.smartbk;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class RiwayatAbsensiActivity extends AppCompatActivity {

    ListView listAbsensi;
    EditText etFilterNama;
    Spinner spinnerBulan;

    ArrayList<String> data;
    ArrayAdapter<String> adapter;

    DatabaseHelper dbHelper;

    String bulanTerpilih = "Semua";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_absensi);

        listAbsensi = findViewById(R.id.listAbsensi);
        etFilterNama = findViewById(R.id.etFilterNama);
        spinnerBulan = findViewById(R.id.spinnerBulan);

        dbHelper = new DatabaseHelper(this);

        data = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                data
        );

        listAbsensi.setAdapter(adapter);

        // =========================
        // SPINNER BULAN
        // =========================
        String[] bulan = {
                "Semua", "Januari", "Februari", "Maret", "April",
                "Mei", "Juni", "Juli", "Agustus", "September",
                "Oktober", "November", "Desember"
        };

        spinnerBulan.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                bulan
        ));

        spinnerBulan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                bulanTerpilih = bulan[position];
                loadData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // =========================
        // FILTER NAMA
        // =========================
        etFilterNama.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadData();
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        loadData();
    }

    // =========================
    // LOAD DATA + FILTER
    // =========================
    private void loadData() {

        data.clear();

        Cursor cursor = dbHelper.getAllAbsensi();

        String filterNama = etFilterNama.getText().toString().toLowerCase();

        if (cursor.getCount() == 0) {
            data.add("Belum ada data absensi");
        } else {

            while (cursor.moveToNext()) {

                String nama = cursor.getString(1);
                String kelas = cursor.getString(2);
                String tanggal = cursor.getString(3);
                String bulan = cursor.getString(4);
                String status = cursor.getString(5);

                // =========================
                // FILTER LOGIC
                // =========================
                boolean cocokNama = nama.toLowerCase().contains(filterNama);

                boolean cocokBulan = bulanTerpilih.equals("Semua")
                        || bulan.equalsIgnoreCase(bulanTerpilih);

                if (cocokNama && cocokBulan) {

                    data.add(
                            "Nama : " + nama +
                                    "\nKelas : " + kelas +
                                    "\nTanggal : " + tanggal +
                                    "\nBulan : " + bulan +
                                    "\nStatus : " + status
                    );
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}