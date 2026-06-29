package com.example.smartbk;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;


public class DataSiswaActivity extends AppCompatActivity {

    EditText etNama, etKelas;
    Spinner spinnerJK;
    Button btnSimpanSiswa, btnImportCSV, btnExportCSV;
    ListView listSiswa;
    SearchView searchSiswa;

    DatabaseHelper dbHelper;

    ArrayList<String> data = new ArrayList<>();
    ArrayList<Integer> idList = new ArrayList<>();

    ArrayAdapter<String> adapter;

    private static final int REQUEST_CODE_IMPORT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_siswa);

        etNama = findViewById(R.id.etNama);
        etKelas = findViewById(R.id.etKelas);
        spinnerJK = findViewById(R.id.spinnerJK);

        btnSimpanSiswa = findViewById(R.id.btnSimpanSiswa);
        btnImportCSV = findViewById(R.id.btnImportExcel);
        btnExportCSV = findViewById(R.id.btnExportCSV);

        listSiswa = findViewById(R.id.listSiswa);
        searchSiswa = findViewById(R.id.searchSiswa);

        dbHelper = new DatabaseHelper(this);

        String[] jk = {
                "Laki-laki",
                "Perempuan"
        };

        spinnerJK.setAdapter(
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        jk
                )
        );

        adapter = new ArrayAdapter<>(
                this,
                R.layout.item_modern_text,
                data
        );

        listSiswa.setAdapter(adapter);
        searchSiswa.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        adapter.getFilter().filter(query);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        adapter.getFilter().filter(newText);
                        return false;
                    }
                }
        );

        loadData();

        // SIMPAN SISWA
        btnSimpanSiswa.setOnClickListener(v -> {

            String nama = etNama.getText().toString().trim();
            String kelas = etKelas.getText().toString().trim();
            String jkValue = spinnerJK.getSelectedItem().toString();

            if (nama.isEmpty() || kelas.isEmpty()) {

                Toast.makeText(
                        this,
                        "Lengkapi data",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            boolean berhasil = dbHelper.insertSiswa(
                    nama,
                    kelas,
                    jkValue
            );

            if (berhasil) {

                Toast.makeText(
                        this,
                        "Data tersimpan",
                        Toast.LENGTH_SHORT
                ).show();

                etNama.setText("");
                etKelas.setText("");

                loadData();

            } else {

                Toast.makeText(
                        this,
                        "Gagal menyimpan",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        // IMPORT CSV
        btnImportCSV.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            startActivityForResult(
                    intent,
                    REQUEST_CODE_IMPORT
            );
        });

        // EXPORT CSV
        btnExportCSV.setOnClickListener(v -> exportCSV());

        // HAPUS DATA
        listSiswa.setOnItemLongClickListener((parent, view, position, id) -> {
            if (position >= idList.size()) {
                return true;
            }

            int siswaId = idList.get(position);

            boolean berhasil = dbHelper.deleteSiswa(siswaId);

            if (berhasil) {

                Toast.makeText(
                        this,
                        "Data dihapus",
                        Toast.LENGTH_SHORT
                ).show();

                loadData();
            }

            return true;
        });
    }

    // =========================
    // IMPORT CSV
    // =========================
    private void importCSV(Uri uri) {

        try {

            InputStream inputStream =
                    getContentResolver().openInputStream(uri);

            Scanner scanner = new Scanner(inputStream);

            boolean firstLine = true;
            int count = 0;

            while (scanner.hasNextLine()) {

                String line = scanner.nextLine();

                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] split = line.split(",");

                if (split.length >= 3) {

                    String nama = split[0].trim();
                    String kelas = split[1].trim();
                    String jk = split[2].trim();

                    if (dbHelper.insertSiswa(
                            nama,
                            kelas,
                            jk
                    )) {
                        count++;
                    }
                }
            }

            scanner.close();
            inputStream.close();

            Toast.makeText(
                    this,
                    "Import berhasil : " + count + " data",
                    Toast.LENGTH_LONG
            ).show();

            loadData();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Error import : " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    // =========================
    // EXPORT CSV
    // =========================
    private void exportCSV() {

        try {

            Cursor cursor = dbHelper.getAllSiswa();

            File folder = getExternalFilesDir(null);

            if (folder == null) {

                Toast.makeText(
                        this,
                        "Folder tidak ditemukan",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

            File file = new File(
                    folder,
                    "data_siswa.csv"
            );

            FileWriter writer = new FileWriter(file);

            writer.append("Nama,Kelas,Jenis Kelamin\n");

            while (cursor.moveToNext()) {

                writer.append(cursor.getString(1)).append(",");
                writer.append(cursor.getString(2)).append(",");
                writer.append(cursor.getString(3)).append("\n");
            }

            cursor.close();

            writer.flush();
            writer.close();

            Toast.makeText(
                    this,
                    "Export berhasil\n" + file.getAbsolutePath(),
                    Toast.LENGTH_LONG
            ).show();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Error export : " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

            e.printStackTrace();
        }
    }

    // =========================
    // HASIL FILE PICKER
    // =========================
    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent dataIntent) {

        super.onActivityResult(
                requestCode,
                resultCode,
                dataIntent
        );

        if (requestCode == REQUEST_CODE_IMPORT
                && resultCode == RESULT_OK
                && dataIntent != null) {

            Uri uri = dataIntent.getData();

            if (uri != null) {
                importCSV(uri);
            }
        }
    }

    // =========================
    // LOAD DATA SISWA
    // =========================
    private void loadData() {

        data.clear();
        idList.clear();

        Cursor cursor = dbHelper.getAllSiswa();

        while (cursor.moveToNext()) {

            idList.add(cursor.getInt(0));

            data.add(
                    cursor.getString(1)
                            + " | "
                            + cursor.getString(2)
                            + " | "
                            + cursor.getString(3)
            );
        }

        cursor.close();

        if (data.isEmpty()) {
            data.add("Belum ada data siswa. Tambahkan siswa baru melalui form di atas.");
        }

        adapter.notifyDataSetChanged();
    }
}
