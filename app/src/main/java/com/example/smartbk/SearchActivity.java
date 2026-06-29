package com.example.smartbk;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    SearchView searchView;
    ListView listHasil;
    TextView txtInfoHasil, txtEmptyTitle, txtEmptyMessage;
    LinearLayout emptyState;

    DatabaseHelper dbHelper;

    ArrayList<SearchResult> dataList;
    SearchResultAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = findViewById(R.id.searchView);
        listHasil = findViewById(R.id.listHasil);
        txtInfoHasil = findViewById(R.id.txtInfoHasil);
        txtEmptyTitle = findViewById(R.id.txtEmptyTitle);
        txtEmptyMessage = findViewById(R.id.txtEmptyMessage);
        emptyState = findViewById(R.id.emptyState);

        dbHelper = new DatabaseHelper(this);

        dataList = new ArrayList<>();

        adapter = new SearchResultAdapter();
        listHasil.setAdapter(adapter);

        tampilkanStateAwal();

        listHasil.setOnItemClickListener((parent, view, position, id) -> {

            SearchResult result = dataList.get(position);

            try {

                if (result.kategori.equals("Siswa")) {

                    startActivity(
                            new Intent(
                                    SearchActivity.this,
                                    DataSiswaActivity.class
                            )
                    );

                } else if (result.kategori.equals("Absensi")) {

                    startActivity(
                            new Intent(
                                    SearchActivity.this,
                                    RiwayatAbsensiActivity.class
                            )
                    );

                } else if (result.kategori.equals("Surat")) {

                    Intent intent =
                            new Intent(
                                    SearchActivity.this,
                                    DetailSuratActivity.class
                            );

                    intent.putExtra("id", result.id);
                    startActivity(intent);

                } else if (result.kategori.equals("Konseling")) {

                    startActivity(
                            new Intent(
                                    SearchActivity.this,
                                    RiwayatKonselingActivity.class
                            )
                    );

                } else if (result.kategori.equals("Pelanggaran")) {

                    startActivity(
                            new Intent(
                                    SearchActivity.this,
                                    PelanggaranActivity.class
                            )
                    );

                } else if (result.kategori.equals("Home Visit")) {

                    bukaDetailHomeVisit(result.id);
                }

            } catch (Exception e) {

                Toast.makeText(
                        SearchActivity.this,
                        "Gagal membuka data: " + e.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                cariData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                cariData(newText);
                return true;
            }
        });
    }

    private void cariData(String keyword) {

        String cleanKeyword = keyword.trim();

        dataList.clear();

        if (cleanKeyword.isEmpty()) {
            adapter.notifyDataSetChanged();
            tampilkanStateAwal();
            return;
        }

        Cursor cursor = dbHelper.cariGlobal(cleanKeyword);

        if (cursor != null && cursor.moveToFirst()) {

            do {

                int id =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow("id")
                        );

                String nama =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("nama")
                        );

                String kelas =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("kelas")
                        );

                String kategori =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("kategori")
                        );

                String deskripsi =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("deskripsi")
                        );

                String tanggal =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("tanggal")
                        );

                dataList.add(
                        new SearchResult(
                                id,
                                nama,
                                kelas,
                                kategori,
                                deskripsi,
                                tanggal
                        )
                );

            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        adapter.notifyDataSetChanged();
        tampilkanHasil(cleanKeyword);
    }

    private void tampilkanStateAwal() {

        listHasil.setVisibility(View.GONE);
        emptyState.setVisibility(View.VISIBLE);
        txtInfoHasil.setText("Masukkan kata kunci untuk mulai mencari");
        txtEmptyTitle.setText("Belum ada pencarian");
        txtEmptyMessage.setText("Ketik nama, kelas, tanggal, status, atau keterangan data.");
    }

    private void tampilkanHasil(String keyword) {

        if (dataList.isEmpty()) {

            listHasil.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
            txtInfoHasil.setText("Tidak ada hasil untuk \"" + keyword + "\"");
            txtEmptyTitle.setText("Data tidak ditemukan");
            txtEmptyMessage.setText("Coba gunakan kata kunci lain yang lebih umum.");

        } else {

            emptyState.setVisibility(View.GONE);
            listHasil.setVisibility(View.VISIBLE);
            txtInfoHasil.setText(dataList.size() + " hasil ditemukan untuk \"" + keyword + "\"");
        }
    }

    private void bukaDetailHomeVisit(int dataId) {

        Cursor cursor = dbHelper.getHomeVisitById(dataId);

        if (cursor != null && cursor.moveToFirst()) {

            Intent intent =
                    new Intent(
                            SearchActivity.this,
                            DetailHomeVisitActivity.class
                    );

            intent.putExtra("id", dataId);
            intent.putExtra(
                    "nama",
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("nama")
                    )
            );
            intent.putExtra(
                    "kelas",
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("kelas")
                    )
            );
            intent.putExtra(
                    "hasil",
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("hasil")
                    )
            );
            intent.putExtra(
                    "lokasi",
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("lokasi")
                    )
            );
            intent.putExtra(
                    "tanggal",
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("tanggal")
                    )
            );

            cursor.close();
            startActivity(intent);

        } else {

            if (cursor != null) {
                cursor.close();
            }

            Toast.makeText(
                    this,
                    "Data home visit tidak ditemukan",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private class SearchResultAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return dataList.get(position).id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {

                convertView =
                        LayoutInflater.from(SearchActivity.this).inflate(
                                R.layout.item_search_result,
                                parent,
                                false
                        );

                holder = new ViewHolder();
                holder.txtNama = convertView.findViewById(R.id.txtNama);
                holder.txtKategori = convertView.findViewById(R.id.txtKategori);
                holder.txtKelas = convertView.findViewById(R.id.txtKelas);
                holder.txtDeskripsi = convertView.findViewById(R.id.txtDeskripsi);
                holder.txtTanggal = convertView.findViewById(R.id.txtTanggal);

                convertView.setTag(holder);

            } else {

                holder = (ViewHolder) convertView.getTag();
            }

            SearchResult result = dataList.get(position);

            holder.txtNama.setText(result.nama);
            holder.txtKategori.setText(result.kategori);
            holder.txtKelas.setText("Kelas : " + result.kelas);

            if (result.deskripsi == null || result.deskripsi.trim().isEmpty()) {
                holder.txtDeskripsi.setVisibility(View.GONE);
            } else {
                holder.txtDeskripsi.setVisibility(View.VISIBLE);
                holder.txtDeskripsi.setText(result.deskripsi);
            }

            if (result.tanggal == null || result.tanggal.trim().isEmpty()) {
                holder.txtTanggal.setVisibility(View.GONE);
            } else {
                holder.txtTanggal.setVisibility(View.VISIBLE);
                holder.txtTanggal.setText("Tanggal : " + result.tanggal);
            }

            return convertView;
        }
    }

    private static class ViewHolder {
        TextView txtNama, txtKategori, txtKelas, txtDeskripsi, txtTanggal;
    }

    private static class SearchResult {

        int id;
        String nama;
        String kelas;
        String kategori;
        String deskripsi;
        String tanggal;

        SearchResult(
                int id,
                String nama,
                String kelas,
                String kategori,
                String deskripsi,
                String tanggal) {

            this.id = id;
            this.nama = nama;
            this.kelas = kelas;
            this.kategori = kategori;
            this.deskripsi = deskripsi;
            this.tanggal = tanggal;
        }
    }
}
