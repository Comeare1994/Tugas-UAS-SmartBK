package com.example.smartbk;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SuratPernyataanActivity extends AppCompatActivity {

    EditText etNama, etKelas, etPernyataan, etTanggal;
    Button btnSimpan, btnExportPdf;
    TextView tvHasil;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surat_pernyataan);

        // INIT DATABASE
        dbHelper = new DatabaseHelper(this);

        // INIT VIEW
        etNama = findViewById(R.id.etNama);
        etKelas = findViewById(R.id.etKelas);
        etPernyataan = findViewById(R.id.etPernyataan);
        etTanggal = findViewById(R.id.etTanggal);

        btnSimpan = findViewById(R.id.btnSimpan);
        btnExportPdf = findViewById(R.id.btnExportPdf);

        tvHasil = findViewById(R.id.tvHasil);

        // =========================
        // SIMPAN DATABASE + PREVIEW
        // =========================
        btnSimpan.setOnClickListener(v -> {

            String nama = etNama.getText().toString().trim();
            String kelas = etKelas.getText().toString().trim();
            String pernyataan = etPernyataan.getText().toString().trim();
            String tanggal = etTanggal.getText().toString().trim();

            if (nama.isEmpty() || kelas.isEmpty() || pernyataan.isEmpty() || tanggal.isEmpty()) {
                Toast.makeText(this, "Semua data wajib diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean berhasil = dbHelper.insertSurat(
                    "Surat Pernyataan",
                    nama,
                    kelas,
                    pernyataan,
                    tanggal
            );

            String hasil =
                    "SURAT PERNYATAAN\n\n" +
                            "Nama   : " + nama + "\n" +
                            "Kelas  : " + kelas + "\n\n" +
                            "Pernyataan:\n" + pernyataan + "\n\n" +
                            "Tanggal : " + tanggal;

            tvHasil.setText(hasil);

            if (berhasil) {
                Toast.makeText(this, "Surat berhasil disimpan", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Gagal menyimpan surat", Toast.LENGTH_SHORT).show();
            }
        });

        // =========================
        // EXPORT PDF
        // =========================
        btnExportPdf.setOnClickListener(v -> {
            exportPdf(
                    etNama.getText().toString(),
                    etKelas.getText().toString(),
                    etPernyataan.getText().toString()
            );
        });
    }

    // =========================
    // FUNGSI EXPORT PDF FINAL
    // =========================
    private void exportPdf(String nama, String kelas, String pernyataan) {

        PdfDocument pdfDocument = new PdfDocument();

        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(595, 842, 1).create();

        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setTextSize(12f);

        int x = 40;
        int y = 60;

        // KOP SURAT
        canvas.drawText("BIMBINGAN KONSELING (BK)", x, y, paint);
        y += 25;
        canvas.drawText("SMK / SMP NEGERI", x, y, paint);
        y += 10;
        canvas.drawText("--------------------------------------", x, y, paint);

        y += 40;

        // JUDUL
        paint.setFakeBoldText(true);
        canvas.drawText("SURAT PERNYATAAN", x + 120, y, paint);
        paint.setFakeBoldText(false);

        y += 50;

        // ISI SURAT
        canvas.drawText("Yang bertanda tangan di bawah ini:", x, y, paint);
        y += 25;

        canvas.drawText("Nama   : " + nama, x, y, paint);
        y += 20;

        canvas.drawText("Kelas  : " + kelas, x, y, paint);
        y += 30;

        canvas.drawText("Dengan ini menyatakan bahwa:", x, y, paint);
        y += 25;

        String[] isi = pernyataan.split("\n");
        for (String line : isi) {
            canvas.drawText(line, x, y, paint);
            y += 20;
        }

        y += 30;

        canvas.drawText("Saya bersedia mematuhi aturan sekolah dan menerima sanksi jika melanggar.", x, y, paint);
        y += 40;

        canvas.drawText("Demikian surat ini dibuat dengan sebenar-benarnya.", x, y, paint);

        y += 60;

        // TANGGAL OTOMATIS
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        String tanggal = sdf.format(new Date());

        canvas.drawText("Jepara, " + tanggal, x + 20, y, paint);

        y += 50;

        canvas.drawText("BK / Wali Kelas", x + 20, y, paint);

        y += 60;

        canvas.drawText("(__________________)", x + 20, y, paint);

        pdfDocument.finishPage(page);

        // SIMPAN FILE
        String fileName = "Surat_Pernyataan_" + System.currentTimeMillis() + ".pdf";
        File file = new File(getExternalFilesDir(null), fileName);

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            pdfDocument.close();

            Toast.makeText(this,
                    "PDF tersimpan: " + file.getPath(),
                    Toast.LENGTH_LONG).show();

            // SHARE (WhatsApp / dll)
            Uri uri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    file
            );

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/pdf");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intent, "Share PDF"));

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal export PDF", Toast.LENGTH_SHORT).show();
        }
    }
}