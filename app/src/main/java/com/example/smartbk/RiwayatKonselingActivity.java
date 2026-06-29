package com.example.smartbk;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class RiwayatKonselingActivity extends AppCompatActivity {

    ListView listKonseling;

    DatabaseHelper dbHelper;

    ArrayList<String> dataList;

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_konseling);

        listKonseling = findViewById(R.id.listKonseling);

        dbHelper = new DatabaseHelper(this);

        dataList = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                dataList
        );

        listKonseling.setAdapter(adapter);

        loadData();
    }

    private void loadData() {

        dataList.clear();

        Cursor cursor = dbHelper.getAllKonseling();

        if (cursor.moveToFirst()) {

            do {

                String nama =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("nama"));

                String kelas =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("kelas"));

                String masalah =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("masalah"));

                String tanggal =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("tanggal"));

                dataList.add(
                        "💬 " + nama +
                                "\nKelas : " + kelas +
                                "\nMasalah : " + masalah +
                                "\nTanggal : " + tanggal
                );

            } while (cursor.moveToNext());
        }

        cursor.close();

        adapter.notifyDataSetChanged();
    }
}