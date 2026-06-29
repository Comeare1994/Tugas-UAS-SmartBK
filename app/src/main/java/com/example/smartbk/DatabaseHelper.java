package com.example.smartbk;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "smartbk.db";
    private static final int DB_VERSION = 12;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // =========================
        // TABEL SISWA
        // =========================
        db.execSQL(
                "CREATE TABLE siswa (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "nama TEXT," +
                        "kelas TEXT," +
                        "jenis_kelamin TEXT)"
        );

        // =========================
        // TABEL ABSENSI
        // =========================
        db.execSQL(
                "CREATE TABLE absensi (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "siswa_id INTEGER," +
                        "tanggal TEXT," +
                        "bulan TEXT," +
                        "status TEXT)"
        );

        // =========================
        // TABEL SURAT
        // =========================
        db.execSQL(
                "CREATE TABLE surat (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "jenis TEXT," +
                        "nama TEXT," +
                        "kelas TEXT," +
                        "alasan TEXT," +
                        "tanggal TEXT)"
        );

        // =========================
        // TABEL KONSELING
        // =========================
        db.execSQL(
                "CREATE TABLE konseling (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "nama TEXT," +
                        "kelas TEXT," +
                        "masalah TEXT," +
                        "solusi TEXT," +
                        "tanggal TEXT)"
        );

        // =========================
        // TABEL PELANGGARAN
        // =========================
        db.execSQL(
                "CREATE TABLE pelanggaran (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "nama TEXT," +
                        "kelas TEXT," +
                        "pelanggaran TEXT," +
                        "poin INTEGER," +
                        "tanggal TEXT)"
        );
        // =========================
// TABEL HOME VISIT
// =========================
        db.execSQL(
                "CREATE TABLE home_visit (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "nama TEXT," +
                        "kelas TEXT," +
                        "hasil TEXT," +
                        "lokasi TEXT," +
                        "foto TEXT," +
                        "tanggal TEXT)"
        );



    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS siswa");
        db.execSQL("DROP TABLE IF EXISTS absensi");
        db.execSQL("DROP TABLE IF EXISTS surat");
        db.execSQL("DROP TABLE IF EXISTS konseling");
        db.execSQL("DROP TABLE IF EXISTS pelanggaran");
        db.execSQL("DROP TABLE IF EXISTS home_visit");

        onCreate(db);
    }

    // =========================
    // SISWA
    // =========================

    public boolean insertSiswa(String nama,
                               String kelas,
                               String jenisKelamin) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("nama", nama);
        cv.put("kelas", kelas);
        cv.put("jenis_kelamin", jenisKelamin);

        return db.insert("siswa", null, cv) != -1;
    }

    public Cursor getAllSiswa() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM siswa ORDER BY nama ASC",
                null
        );
    }

    public boolean deleteSiswa(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(
                "siswa",
                "id=?",
                new String[]{String.valueOf(id)}
        ) > 0;
    }

    // =========================
    // ABSENSI
    // =========================

    public boolean insertAbsensi(int siswaId,
                                 String tanggal,
                                 String bulan,
                                 String status) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("siswa_id", siswaId);
        cv.put("tanggal", tanggal);
        cv.put("bulan", bulan);
        cv.put("status", status);

        return db.insert("absensi", null, cv) != -1;
    }

    public Cursor getAllAbsensi() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT a.id, s.nama, s.kelas, a.tanggal, a.bulan, a.status " +
                        "FROM absensi a " +
                        "INNER JOIN siswa s ON a.siswa_id = s.id " +
                        "ORDER BY a.id DESC",
                null
        );
    }

    // =========================
    // REKAP ABSENSI
    // =========================

    public Cursor getRekapAbsensi() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT s.nama, s.kelas, " +
                        "SUM(CASE WHEN a.status='Hadir' THEN 1 ELSE 0 END) AS hadir, " +
                        "SUM(CASE WHEN a.status='Izin' THEN 1 ELSE 0 END) AS izin, " +
                        "SUM(CASE WHEN a.status='Sakit' THEN 1 ELSE 0 END) AS sakit, " +
                        "SUM(CASE WHEN a.status='Alfa' THEN 1 ELSE 0 END) AS alfa " +
                        "FROM absensi a " +
                        "INNER JOIN siswa s ON a.siswa_id = s.id " +
                        "GROUP BY s.id " +
                        "ORDER BY s.nama ASC",
                null
        );
    }

    public Cursor getRekapAbsensiBulanan(String bulan) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT s.nama, s.kelas, " +
                        "SUM(CASE WHEN a.status='Hadir' THEN 1 ELSE 0 END) AS hadir, " +
                        "SUM(CASE WHEN a.status='Izin' THEN 1 ELSE 0 END) AS izin, " +
                        "SUM(CASE WHEN a.status='Sakit' THEN 1 ELSE 0 END) AS sakit, " +
                        "SUM(CASE WHEN a.status='Alfa' THEN 1 ELSE 0 END) AS alfa " +
                        "FROM absensi a " +
                        "INNER JOIN siswa s ON a.siswa_id = s.id " +
                        "WHERE a.bulan=? " +
                        "GROUP BY s.id " +
                        "ORDER BY s.nama ASC",
                new String[]{bulan}
        );
    }

    // =========================
    // SURAT
    // =========================

    public boolean insertSurat(String jenis,
                               String nama,
                               String kelas,
                               String alasan,
                               String tanggal) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("jenis", jenis);
        cv.put("nama", nama);
        cv.put("kelas", kelas);
        cv.put("alasan", alasan);
        cv.put("tanggal", tanggal);

        return db.insert("surat", null, cv) != -1;
    }

    public Cursor getAllSurat() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM surat ORDER BY id DESC",
                null
        );
    }

    public boolean deleteSurat(String nama,
                               String kelas,
                               String alasan,
                               String tanggal) {

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(
                "surat",
                "nama=? AND kelas=? AND alasan=? AND tanggal=?",
                new String[]{
                        nama,
                        kelas,
                        alasan,
                        tanggal
                }
        ) > 0;
    }

    // =========================
    // KONSELING
    // =========================

    public boolean insertKonseling(String nama,
                                   String kelas,
                                   String masalah,
                                   String solusi,
                                   String tanggal) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("nama", nama);
        cv.put("kelas", kelas);
        cv.put("masalah", masalah);
        cv.put("solusi", solusi);
        cv.put("tanggal", tanggal);

        return db.insert("konseling", null, cv) != -1;
    }

    public Cursor getAllKonseling() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM konseling ORDER BY id DESC",
                null
        );
    }

    // =========================
    // PELANGGARAN
    // =========================

    public boolean insertPelanggaran(String nama,
                                     String kelas,
                                     String pelanggaran,
                                     int poin,
                                     String tanggal) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("nama", nama);
        cv.put("kelas", kelas);
        cv.put("pelanggaran", pelanggaran);
        cv.put("poin", poin);
        cv.put("tanggal", tanggal);

        return db.insert("pelanggaran", null, cv) != -1;
    }

    public Cursor getAllPelanggaran() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM pelanggaran ORDER BY id DESC",
                null
        );
    }

    public boolean deletePelanggaran(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(
                "pelanggaran",
                "id=?",
                new String[]{String.valueOf(id)}
        ) > 0;
    }


    // =========================
// REKAP PELANGGARAN
// =========================

    public Cursor getRekapPelanggaran() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT nama, kelas, SUM(poin) as total_poin " +
                        "FROM pelanggaran " +
                        "GROUP BY nama, kelas " +
                        "ORDER BY total_poin DESC",
                null
        );
    }

    // =========================
// HOME VISIT
// =========================

    public boolean insertHomeVisit(
            String nama,
            String kelas,
            String hasil,
            String lokasi,
            String foto,
            String tanggal) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("nama", nama);
        cv.put("kelas", kelas);
        cv.put("hasil", hasil);
        cv.put("lokasi", lokasi);
        cv.put("foto", foto);
        cv.put("tanggal", tanggal);

        return db.insert("home_visit", null, cv) != -1;
    }

    public Cursor getAllHomeVisit() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM home_visit ORDER BY id DESC",
                null
        );
    }

    public Cursor getHomeVisitById(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM home_visit WHERE id=?",
                new String[]{String.valueOf(id)}
        );
    }

    public boolean updateHomeVisit(
            int id,
            String nama,
            String kelas,
            String hasil,
            String lokasi,
            String tanggal) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("nama", nama);
        cv.put("kelas", kelas);
        cv.put("hasil", hasil);
        cv.put("lokasi", lokasi);
        cv.put("tanggal", tanggal);

        return db.update(
                "home_visit",
                cv,
                "id=?",
                new String[]{String.valueOf(id)}
        ) > 0;
    }

    public boolean deleteHomeVisit(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(
                "home_visit",
                "id=?",
                new String[]{String.valueOf(id)}
        ) > 0;
    }
    public int getJumlahSiswa() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT COUNT(*) FROM siswa",
                        null
                );

        cursor.moveToFirst();

        int jumlah = cursor.getInt(0);

        cursor.close();

        return jumlah;
    }

    public int getJumlahKonseling() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT COUNT(*) FROM konseling",
                        null
                );

        cursor.moveToFirst();

        int jumlah = cursor.getInt(0);

        cursor.close();

        return jumlah;
    }

    public int getJumlahPelanggaran() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT COUNT(*) FROM pelanggaran",
                        null
                );

        cursor.moveToFirst();

        int jumlah = cursor.getInt(0);

        cursor.close();

        return jumlah;
    }

    public int getJumlahHomeVisit() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT COUNT(*) FROM home_visit",
                        null
                );

        cursor.moveToFirst();

        int jumlah = cursor.getInt(0);

        cursor.close();

        return jumlah;
    }
    public Cursor cariSiswa(String keyword) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM siswa WHERE nama LIKE ? ORDER BY nama ASC",
                new String[]{"%" + keyword + "%"}
        );
    }
    public Cursor cariGlobal(String keyword) {

        SQLiteDatabase db = this.getReadableDatabase();
        String searchKeyword = "%" + keyword + "%";

        return db.rawQuery(

                "SELECT id, nama, kelas, 'Siswa' as kategori, " +
                        "jenis_kelamin as deskripsi, '' as tanggal, '' as lokasi, '' as hasil " +
                        "FROM siswa " +
                        "WHERE nama LIKE ? OR kelas LIKE ? OR jenis_kelamin LIKE ? " +

                        "UNION ALL " +

                        "SELECT a.id, s.nama, s.kelas, 'Absensi' as kategori, " +
                        "a.status as deskripsi, a.tanggal as tanggal, '' as lokasi, '' as hasil " +
                        "FROM absensi a " +
                        "INNER JOIN siswa s ON a.siswa_id = s.id " +
                        "WHERE s.nama LIKE ? OR s.kelas LIKE ? OR a.tanggal LIKE ? OR a.bulan LIKE ? OR a.status LIKE ? " +

                        "UNION ALL " +

                        "SELECT id, nama, kelas, 'Surat' as kategori, " +
                        "jenis || ' - ' || alasan as deskripsi, tanggal, '' as lokasi, '' as hasil " +
                        "FROM surat " +
                        "WHERE nama LIKE ? OR kelas LIKE ? OR jenis LIKE ? OR alasan LIKE ? OR tanggal LIKE ? " +

                        "UNION ALL " +

                        "SELECT id, nama, kelas, 'Konseling' as kategori, " +
                        "masalah as deskripsi, tanggal, '' as lokasi, solusi as hasil " +
                        "FROM konseling " +
                        "WHERE nama LIKE ? OR kelas LIKE ? OR masalah LIKE ? OR solusi LIKE ? OR tanggal LIKE ? " +

                        "UNION ALL " +

                        "SELECT id, nama, kelas, 'Pelanggaran' as kategori, " +
                        "pelanggaran || ' - Poin: ' || poin as deskripsi, tanggal, '' as lokasi, '' as hasil " +
                        "FROM pelanggaran " +
                        "WHERE nama LIKE ? OR kelas LIKE ? OR pelanggaran LIKE ? OR tanggal LIKE ? OR CAST(poin AS TEXT) LIKE ? " +

                        "UNION ALL " +

                        "SELECT id, nama, kelas, 'Home Visit' as kategori, " +
                        "hasil as deskripsi, tanggal, lokasi, hasil " +
                        "FROM home_visit " +
                        "WHERE nama LIKE ? OR kelas LIKE ? OR hasil LIKE ? OR lokasi LIKE ? OR tanggal LIKE ? " +

                        "ORDER BY nama COLLATE NOCASE ASC, kategori ASC",

                new String[]{
                        searchKeyword, searchKeyword, searchKeyword,
                        searchKeyword, searchKeyword, searchKeyword, searchKeyword, searchKeyword,
                        searchKeyword, searchKeyword, searchKeyword, searchKeyword, searchKeyword,
                        searchKeyword, searchKeyword, searchKeyword, searchKeyword, searchKeyword,
                        searchKeyword, searchKeyword, searchKeyword, searchKeyword, searchKeyword,
                        searchKeyword, searchKeyword, searchKeyword, searchKeyword, searchKeyword
                }
        );
    }
    public Cursor getSuratById(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM surat WHERE id=?",
                new String[]{String.valueOf(id)}
        );
    }
}
