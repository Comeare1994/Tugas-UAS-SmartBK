package com.example.smartbk;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class RekapAbsensiActivity extends AppCompatActivity {

    ListView listRekap;

    DatabaseHelper dbHelper;

    ArrayList<String> data;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekap_absensi);

        listRekap = findViewById(R.id.listRekap);

        dbHelper = new DatabaseHelper(this);

        data = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                data
        );

        listRekap.setAdapter(adapter);

        loadData();
    }

    private void loadData() {

        data.clear();

        Cursor cursor = dbHelper.getRekapAbsensi();

        while (cursor.moveToNext()) {

            String nama = cursor.getString(0);
            String kelas = cursor.getString(1);

            int hadir = cursor.getInt(2);
            int izin = cursor.getInt(3);
            int sakit = cursor.getInt(4);
            int alfa = cursor.getInt(5);

            data.add(
                    "Nama : " + nama +
                            "\nKelas : " + kelas +
                            "\nHadir : " + hadir +
                            "\nIzin : " + izin +
                            "\nSakit : " + sakit +
                            "\nAlfa : " + alfa
            );
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