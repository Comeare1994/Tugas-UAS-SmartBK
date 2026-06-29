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

public class PelanggaranActivity extends AppCompatActivity {

    EditText etNama, etKelas, etPelanggaran, etPoin;
    Button btnSimpan;
    ListView listPelanggaran;

    DatabaseHelper dbHelper;

    ArrayList<String> data;
    ArrayList<Integer> idList;

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelanggaran);

        etNama = findViewById(R.id.etNama);
        etKelas = findViewById(R.id.etKelas);
        etPelanggaran = findViewById(R.id.etPelanggaran);
        etPoin = findViewById(R.id.etPoin);
        btnSimpan = findViewById(R.id.btnSimpan);
        listPelanggaran = findViewById(R.id.listPelanggaran);

        dbHelper = new DatabaseHelper(this);

        data = new ArrayList<>();
        idList = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                R.layout.item_modern_text,
                data
        );

        listPelanggaran.setAdapter(adapter);

        loadData();

        btnSimpan.setOnClickListener(v -> simpanData());

        listPelanggaran.setOnItemLongClickListener((parent, view, position, id) -> {
            if (position >= idList.size()) {
                return true;
            }

            int pelanggaranId = idList.get(position);

            if (dbHelper.deletePelanggaran(pelanggaranId)) {

                Toast.makeText(
                        this,
                        "Data berhasil dihapus",
                        Toast.LENGTH_SHORT
                ).show();

                loadData();
            }

            return true;
        });
    }

    private void simpanData() {

        String nama = etNama.getText().toString().trim();
        String kelas = etKelas.getText().toString().trim();
        String pelanggaran = etPelanggaran.getText().toString().trim();
        String poinText = etPoin.getText().toString().trim();

        if (nama.isEmpty() ||
                kelas.isEmpty() ||
                pelanggaran.isEmpty() ||
                poinText.isEmpty()) {

            Toast.makeText(
                    this,
                    "Lengkapi semua data",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        int poin = Integer.parseInt(poinText);

        String tanggal = new SimpleDateFormat(
                "dd-MM-yyyy",
                Locale.getDefault()
        ).format(new Date());

        boolean hasil = dbHelper.insertPelanggaran(
                nama,
                kelas,
                pelanggaran,
                poin,
                tanggal
        );

        if (hasil) {

            Toast.makeText(
                    this,
                    "Data pelanggaran tersimpan",
                    Toast.LENGTH_SHORT
            ).show();

            etNama.setText("");
            etKelas.setText("");
            etPelanggaran.setText("");
            etPoin.setText("");

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
        idList.clear();

        Cursor cursor = dbHelper.getAllPelanggaran();

        while (cursor.moveToNext()) {

            int id = cursor.getInt(0);
            String nama = cursor.getString(1);
            String kelas = cursor.getString(2);
            String pelanggaran = cursor.getString(3);
            int poin = cursor.getInt(4);
            String tanggal = cursor.getString(5);

            idList.add(id);

            data.add(
                    "Nama : " + nama +
                            "\nKelas : " + kelas +
                            "\nPelanggaran : " + pelanggaran +
                            "\nPoin : " + poin +
                            "\nTanggal : " + tanggal
            );
        }

        cursor.close();

        if (data.isEmpty()) {
            data.add("Belum ada data pelanggaran. Data yang disimpan akan tampil di sini.");
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}
