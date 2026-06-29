package com.example.smartbk;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class RiwayatSuratActivity extends AppCompatActivity {

    ListView listSurat;

    ArrayList<String> data;
    ArrayAdapter<String> adapter;

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_surat);

        listSurat = findViewById(R.id.listSurat);
        dbHelper = new DatabaseHelper(this);

        data = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                data
        );

        listSurat.setAdapter(adapter);

        loadData();

        // =========================
        // CLICK → DETAIL
        // =========================
        listSurat.setOnItemClickListener((parent, view, position, id) -> {

            String selectedData = data.get(position);

            Intent intent = new Intent(
                    RiwayatSuratActivity.this,
                    DetailSuratActivity.class
            );

            intent.putExtra("data", selectedData);
            startActivity(intent);
        });

        // =========================
        // LONG CLICK → HAPUS
        // =========================
        listSurat.setOnItemLongClickListener((parent, view, position, id) -> {

            String selected = data.get(position);

            String[] lines = selected.split("\n");

            if (lines.length >= 5) {

                String jenis = lines[0].replace("Jenis: ", "");
                String nama = lines[1].replace("Nama: ", "");
                String kelas = lines[2].replace("Kelas: ", "");
                String alasan = lines[3].replace("Alasan: ", "");
                String tanggal = lines[4].replace("Tanggal: ", "");

                new android.app.AlertDialog.Builder(this)
                        .setTitle("Hapus Surat")
                        .setMessage("Yakin hapus data ini?")
                        .setPositiveButton("Hapus", (dialog, which) -> {

                            boolean sukses = dbHelper.deleteSurat(
                                    nama,
                                    kelas,
                                    alasan,
                                    tanggal
                            );

                            if (sukses) {
                                data.remove(position);
                                adapter.notifyDataSetChanged();
                            }

                        })
                        .setNegativeButton("Batal", null)
                        .show();
            }

            return true;
        });
    }

    // =========================
    // LOAD DATA
    // =========================
    private void loadData() {

        Cursor cursor = dbHelper.getAllSurat();

        data.clear();

        if (cursor.getCount() == 0) {
            data.add("Belum ada data surat");
        } else {

            while (cursor.moveToNext()) {

                String jenis = cursor.getString(1);
                String nama = cursor.getString(2);
                String kelas = cursor.getString(3);
                String alasan = cursor.getString(4);
                String tanggal = cursor.getString(5);

                String hasil =
                        "Jenis: " + jenis +
                                "\nNama: " + nama +
                                "\nKelas: " + kelas +
                                "\nAlasan: " + alasan +
                                "\nTanggal: " + tanggal;

                data.add(hasil);
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