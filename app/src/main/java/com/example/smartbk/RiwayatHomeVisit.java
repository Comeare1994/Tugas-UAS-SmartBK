package com.example.smartbk;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.util.ArrayList;

public class RiwayatHomeVisit extends AppCompatActivity {


    ListView listHomeVisit;
    Button btnExportPdf;

    DatabaseHelper dbHelper;

    ArrayList<String> data;
    ArrayList<String> namaList;
    ArrayList<String> kelasList;
    ArrayList<String> hasilList;
    ArrayList<String> lokasiList;
    ArrayList<String> tanggalList;
    ArrayList<Integer> idList;

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_home_visit);

        listHomeVisit = findViewById(R.id.listHomeVisit);
        btnExportPdf = findViewById(R.id.btnExportPdf);

        dbHelper = new DatabaseHelper(this);

        data = new ArrayList<>();
        namaList = new ArrayList<>();
        kelasList = new ArrayList<>();
        hasilList = new ArrayList<>();
        lokasiList = new ArrayList<>();
        tanggalList = new ArrayList<>();
        idList = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                data
        );

        listHomeVisit.setAdapter(adapter);

        loadData();

        btnExportPdf.setOnClickListener(v -> {
            exportPdf();
        });

        listHomeVisit.setOnItemClickListener((parent, view, position, id) -> {

            Intent intent =
                    new Intent(
                            RiwayatHomeVisit.this,
                            DetailHomeVisitActivity.class
                    );

            intent.putExtra("id", idList.get(position));
            intent.putExtra("nama", namaList.get(position));
            intent.putExtra("kelas", kelasList.get(position));
            intent.putExtra("hasil", hasilList.get(position));
            intent.putExtra("lokasi", lokasiList.get(position));
            intent.putExtra("tanggal", tanggalList.get(position));

            startActivity(intent);
        });

        listHomeVisit.setOnItemLongClickListener((parent, view, position, id) -> {

            new AlertDialog.Builder(this)
                    .setTitle("Hapus Data")
                    .setMessage("Yakin ingin menghapus Home Visit ini?")
                    .setPositiveButton("Ya", (dialog, which) -> {

                        boolean berhasil =
                                dbHelper.deleteHomeVisit(
                                        idList.get(position)
                                );

                        if (berhasil) {

                            Toast.makeText(
                                    this,
                                    "Data berhasil dihapus",
                                    Toast.LENGTH_SHORT
                            ).show();

                            loadData();

                        } else {

                            Toast.makeText(
                                    this,
                                    "Gagal menghapus data",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    })
                    .setNegativeButton("Batal", null)
                    .show();

            return true;
        });
    }

    private void loadData() {

        data.clear();
        namaList.clear();
        kelasList.clear();
        hasilList.clear();
        lokasiList.clear();
        tanggalList.clear();
        idList.clear();

        Cursor cursor = dbHelper.getAllHomeVisit();

        while (cursor.moveToNext()) {

            int id = cursor.getInt(0);

            String nama = cursor.getString(1);
            String kelas = cursor.getString(2);
            String hasil = cursor.getString(3);
            String lokasi = cursor.getString(4);
            String tanggal = cursor.getString(6);

            idList.add(id);

            namaList.add(nama);
            kelasList.add(kelas);
            hasilList.add(hasil);
            lokasiList.add(lokasi);
            tanggalList.add(tanggal);

            String item =
                    "Nama : " + nama +
                            "\nKelas : " + kelas +
                            "\nHasil : " + hasil +
                            "\nLokasi : " + lokasi +
                            "\nTanggal : " + tanggal;

            data.add(item);
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void exportPdf() {

        try {

            File folder = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS
                    ),
                    "SmartBK"
            );

            if (!folder.exists()) {
                folder.mkdirs();
            }

            File file =
                    new File(
                            folder,
                            "Riwayat_Home_Visit.pdf"
                    );

            PdfWriter writer =
                    new PdfWriter(file);

            PdfDocument pdfDocument =
                    new PdfDocument(writer);

            Document document =
                    new Document(pdfDocument);

            document.add(
                    new Paragraph("RIWAYAT HOME VISIT")
            );

            document.add(
                    new Paragraph(" ")
            );

            Cursor cursor =
                    dbHelper.getAllHomeVisit();

            while (cursor.moveToNext()) {

                String isi =
                        "Nama : " + cursor.getString(1) +
                                "\nKelas : " + cursor.getString(2) +
                                "\nHasil : " + cursor.getString(3) +
                                "\nLokasi : " + cursor.getString(4) +
                                "\nTanggal : " + cursor.getString(6) +
                                "\n--------------------------------";

                document.add(
                        new Paragraph(isi)
                );
            }

            cursor.close();
            document.close();

            Toast.makeText(
                    this,
                    "PDF berhasil disimpan",
                    Toast.LENGTH_LONG
            ).show();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Gagal membuat PDF : " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }


}
