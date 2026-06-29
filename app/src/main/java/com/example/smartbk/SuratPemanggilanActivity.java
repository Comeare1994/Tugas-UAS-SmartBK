package com.example.smartbk;

import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;

public class SuratPemanggilanActivity extends AppCompatActivity {

    EditText etNamaSurat,
            etKelasSurat,
            etAlasanSurat,
            etTanggalSurat;

    Button btnBuatSurat, btnExportPdf;

    TextView tvHasilSurat;

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surat_pemanggilan);

        // INIT DATABASE
        dbHelper = new DatabaseHelper(this);

        // INIT VIEW
        etNamaSurat = findViewById(R.id.etNamaSurat);
        etKelasSurat = findViewById(R.id.etKelasSurat);
        etAlasanSurat = findViewById(R.id.etAlasanSurat);
        etTanggalSurat = findViewById(R.id.etTanggalSurat);

        btnBuatSurat = findViewById(R.id.btnBuatSurat);
        btnExportPdf = findViewById(R.id.btnExportPdf);

        tvHasilSurat = findViewById(R.id.tvHasilSurat);

        // =========================
        // BUAT SURAT
        // =========================
        btnBuatSurat.setOnClickListener(v -> {

            String nama = etNamaSurat.getText().toString().trim();
            String kelas = etKelasSurat.getText().toString().trim();
            String alasan = etAlasanSurat.getText().toString().trim();
            String tanggal = etTanggalSurat.getText().toString().trim();

            if (nama.isEmpty() || kelas.isEmpty() || alasan.isEmpty() || tanggal.isEmpty()) {
                Toast.makeText(this, "Semua data wajib diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean berhasil = dbHelper.insertSurat(
                    "Pemanggilan",
                    nama,
                    kelas,
                    alasan,
                    tanggal
            );

            String hasil =
                    "SURAT PEMANGGILAN ORANG TUA\n\n" +
                            "Nama: " + nama + "\n" +
                            "Kelas: " + kelas + "\n\n" +
                            "Alasan: " + alasan + "\n\n" +
                            "Tanggal: " + tanggal + "\n";

            tvHasilSurat.setText(hasil);

            if (berhasil) {
                Toast.makeText(this, "Surat berhasil disimpan", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Gagal menyimpan surat", Toast.LENGTH_SHORT).show();
            }
        });

        // =========================
        // EXPORT PDF
        // =========================
        btnExportPdf.setOnClickListener(v -> exportPdf(tvHasilSurat.getText().toString()));
    }

    // =========================
    // FUNGSI EXPORT PDF
    // =========================
    private void exportPdf(String text) {

        PdfDocument document = new PdfDocument();

        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(595, 842, 1).create();

        PdfDocument.Page page = document.startPage(pageInfo);

        android.graphics.Canvas canvas = page.getCanvas();
        android.graphics.Paint paint = new android.graphics.Paint();

        int x = 40;
        int y = 60;

        // =========================
        // KOP SURAT
        // =========================
        paint.setTextSize(14f);
        paint.setFakeBoldText(true);
        canvas.drawText("SMK / SMP NEGERI XXX", x, y, paint);

        y += 25;
        paint.setFakeBoldText(false);
        canvas.drawText("BIMBINGAN KONSELING (BK)", x, y, paint);

        y += 20;
        canvas.drawText("--------------------------------------------", x, y, paint);

        y += 40;

        // =========================
        // JUDUL (TETAP ADA, TAPI SATU SAJA)
        // =========================
        paint.setTextSize(13f);
        paint.setFakeBoldText(true);
        canvas.drawText("SURAT PEMANGGILAN ORANG TUA", x, y, paint);

        y += 30;
        paint.setFakeBoldText(false);

        // =========================
        // AMBIL DATA ASLI (INI PENTING)
        // =========================
        String[] data = text.split("\n");

        String nama = "";
        String kelas = "";
        String alasan = "";
        String tanggal = "";

        for (String line : data) {

            if (line.startsWith("Nama:")) {
                nama = line.replace("Nama:", "").trim();
            }

            if (line.startsWith("Kelas:")) {
                kelas = line.replace("Kelas:", "").trim();
            }

            if (line.startsWith("Alasan:")) {
                alasan = line.replace("Alasan:", "").trim();
            }

            if (line.startsWith("Tanggal:")) {
                tanggal = line.replace("Tanggal:", "").trim();
            }
        }

        // =========================
        // ISI SURAT (BERSIH, TIDAK DOBOEL)
        // =========================
        canvas.drawText("Dengan hormat,", x, y, paint);
        y += 20;

        canvas.drawText("Kami mengharapkan kehadiran orang tua/wali dari:", x, y, paint);
        y += 25;

        canvas.drawText("Nama   : " + nama, x, y, paint);
        y += 20;

        canvas.drawText("Kelas  : " + kelas, x, y, paint);
        y += 20;

        canvas.drawText("Alasan : " + alasan, x, y, paint);
        y += 30;

        canvas.drawText("Untuk membahas permasalahan siswa tersebut.", x, y, paint);
        y += 20;

        canvas.drawText("Demikian surat ini kami sampaikan.", x, y, paint);
        y += 20;

        canvas.drawText("Atas perhatian dan kerja samanya kami ucapkan terima kasih.", x, y, paint);

        // =========================
        // TTD + TANGGAL
        // =========================
        y += 60;

        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("dd MMMM yyyy");

        String tgl = sdf.format(new java.util.Date());

        canvas.drawText("Jepara, " + tgl, x, y, paint);

        y += 40;
        canvas.drawText("Guru BK", x, y, paint);

        y += 60;
        canvas.drawText("(____________________)", x, y, paint);

        document.finishPage(page);

        try {
            String fileName = "Surat_" + System.currentTimeMillis() + ".pdf";
            File file = new File(getExternalFilesDir(null), fileName);

            document.writeTo(new FileOutputStream(file));

            Toast.makeText(this,
                    "PDF tersimpan: " + file.getPath(),
                    Toast.LENGTH_LONG).show();

            android.net.Uri uri = androidx.core.content.FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    file
            );

            android.content.Intent shareIntent = new android.content.Intent();
            shareIntent.setAction(android.content.Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(android.content.Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(android.content.Intent.createChooser(shareIntent, "Share PDF via"));

        } catch (Exception e) {
            Toast.makeText(this,
                    "Gagal export PDF",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        document.close();
    }
}