package com.example.smartbk;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class RekapPelanggaranActivity extends AppCompatActivity {

    ListView listRekap;
    Button btnExport;

    ArrayList<String> data;
    ArrayAdapter<String> adapter;

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekap_pelanggaran);

        listRekap = findViewById(R.id.listRekap);
        btnExport = findViewById(R.id.btnExport);

        dbHelper = new DatabaseHelper(this);

        data = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                data
        );

        listRekap.setAdapter(adapter);

        loadData();

        btnExport.setOnClickListener(v -> exportCSV());
    }

    private void loadData() {

        data.clear();

        Cursor cursor = dbHelper.getRekapPelanggaran();

        while (cursor.moveToNext()) {

            String nama = cursor.getString(0);
            String kelas = cursor.getString(1);
            int totalPoin = cursor.getInt(2);

            data.add(
                    "Nama : " + nama +
                            "\nKelas : " + kelas +
                            "\nTotal Poin : " + totalPoin
            );
        }

        cursor.close();

        adapter.notifyDataSetChanged();
    }

    private void exportCSV() {

        try {

            File folder = getExternalFilesDir(null);

            if (folder == null) return;

            File file = new File(
                    folder,
                    "RekapPelanggaran.csv"
            );

            FileWriter writer = new FileWriter(file);

            writer.append("Nama,Kelas,Total Poin\n");

            Cursor cursor = dbHelper.getRekapPelanggaran();

            while (cursor.moveToNext()) {

                writer.append(cursor.getString(0)).append(",");
                writer.append(cursor.getString(1)).append(",");
                writer.append(String.valueOf(cursor.getInt(2))).append("\n");
            }

            cursor.close();

            writer.flush();
            writer.close();

            Uri uri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    file
            );

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/csv");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(
                    intent,
                    "Bagikan Rekap Pelanggaran"
            ));

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}