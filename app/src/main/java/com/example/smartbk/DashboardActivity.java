package com.example.smartbk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private static final int FULL_ACCESS = 1;
    private static final int LIMITED_ACCESS = 2;
    private static final int VIEW_ACCESS = 3;

    LinearLayout menuAbsensi,
            menuKonseling,
            menuPelanggaran,
            menuHomeVisit,
            menuSurat,
            menuRiwayatSurat,
            menuDataSiswa,
            menuRiwayatAbsensi,
            menuRekapAbsensi,
            menuRekapPelanggaran,
            menuRiwayatHomeVisit,
            menuAkun,
            menuSearch;

    TextView txtJumlahSiswa,
            txtJumlahKonseling,
            txtJumlahPelanggaran,
            txtJumlahHomeVisit,
            txtRoleLabel;

    DatabaseHelper dbHelper;
    String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dbHelper = new DatabaseHelper(this);
        role = getIntent().getStringExtra("role");
        if (role == null || role.trim().isEmpty()) {
            role = "Guru BK";
        }

        txtRoleLabel = findViewById(R.id.txtRoleLabel);
        txtJumlahSiswa = findViewById(R.id.txtJumlahSiswa);
        txtJumlahKonseling = findViewById(R.id.txtJumlahKonseling);
        txtJumlahPelanggaran = findViewById(R.id.txtJumlahPelanggaran);
        txtJumlahHomeVisit = findViewById(R.id.txtJumlahHomeVisit);

        menuAbsensi = findViewById(R.id.menuAbsensi);
        menuKonseling = findViewById(R.id.menuKonseling);
        menuPelanggaran = findViewById(R.id.menuPelanggaran);
        menuHomeVisit = findViewById(R.id.menuHomeVisit);
        menuSurat = findViewById(R.id.menuSurat);
        menuRiwayatSurat = findViewById(R.id.menuRiwayatSurat);
        menuDataSiswa = findViewById(R.id.menuDataSiswa);
        menuRiwayatAbsensi = findViewById(R.id.menuRiwayatAbsensi);
        menuRekapAbsensi = findViewById(R.id.menuRekapAbsensi);
        menuRekapPelanggaran = findViewById(R.id.menuRekapPelanggaran);
        menuRiwayatHomeVisit = findViewById(R.id.menuRiwayatHomeVisit);
        menuAkun = findViewById(R.id.menuAkun);
        menuSearch = findViewById(R.id.menuSearch);

        loadStatistik();
        applyRoleAccess();

        menuAbsensi.setOnClickListener(v ->
                openIfAllowed(menuAbsensi, AbsensiActivity.class)
        );

        menuKonseling.setOnClickListener(v ->
                openIfAllowed(menuKonseling, KonselingActivity.class)
        );

        menuPelanggaran.setOnClickListener(v ->
                openIfAllowed(menuPelanggaran, PelanggaranActivity.class)
        );

        menuHomeVisit.setOnClickListener(v ->
                openIfAllowed(menuHomeVisit, HomeVisitActivity.class)
        );

        menuRiwayatHomeVisit.setOnClickListener(v ->
                openIfAllowed(menuRiwayatHomeVisit, RiwayatHomeVisit.class)
        );

        menuDataSiswa.setOnClickListener(v ->
                openIfAllowed(menuDataSiswa, DataSiswaActivity.class)
        );

        menuRiwayatAbsensi.setOnClickListener(v ->
                openIfAllowed(menuRiwayatAbsensi, RiwayatAbsensiActivity.class)
        );

        menuRekapAbsensi.setOnClickListener(v ->
                openIfAllowed(menuRekapAbsensi, RekapAbsensiActivity.class)
        );

        menuRekapPelanggaran.setOnClickListener(v ->
                openIfAllowed(menuRekapPelanggaran, RekapPelanggaranActivity.class)
        );

        menuSurat.setOnClickListener(v ->
                openIfAllowed(menuSurat, SuratActivity.class)
        );

        menuRiwayatSurat.setOnClickListener(v ->
                openIfAllowed(menuRiwayatSurat, RiwayatSuratActivity.class)
        );

        menuSearch.setOnClickListener(v ->
                openIfAllowed(menuSearch, SearchActivity.class)
        );

        menuAkun.setOnClickListener(v ->
                openIfAllowed(menuAkun, SettingActivity.class)
        );
    }

    private void loadStatistik() {

        txtJumlahSiswa.setText(
                "Siswa\n" +
                        dbHelper.getJumlahSiswa()
        );

        txtJumlahKonseling.setText(
                "Konseling\n" +
                        dbHelper.getJumlahKonseling()
        );

        txtJumlahPelanggaran.setText(
                "Pelanggaran\n" +
                        dbHelper.getJumlahPelanggaran()
        );

        txtJumlahHomeVisit.setText(
                "Home Visit\n" +
                        dbHelper.getJumlahHomeVisit()
        );
    }

    private void applyRoleAccess() {
        txtRoleLabel.setText(role);

        setMenuAccess(menuSearch, FULL_ACCESS);
        setMenuAccess(menuDataSiswa, FULL_ACCESS);

        if (role.equals("Guru BK")) {
            setMenuAccess(menuAbsensi, FULL_ACCESS);
            setMenuAccess(menuKonseling, FULL_ACCESS);
            setMenuAccess(menuPelanggaran, FULL_ACCESS);
            setMenuAccess(menuHomeVisit, FULL_ACCESS);
            setMenuAccess(menuSurat, FULL_ACCESS);
            setMenuAccess(menuRiwayatSurat, FULL_ACCESS);
            setMenuAccess(menuRiwayatAbsensi, FULL_ACCESS);
            setMenuAccess(menuRekapAbsensi, FULL_ACCESS);
            setMenuAccess(menuRekapPelanggaran, FULL_ACCESS);
            setMenuAccess(menuRiwayatHomeVisit, FULL_ACCESS);
            setMenuAccess(menuAkun, FULL_ACCESS);
            return;
        }

        if (role.equals("Wali Kelas")) {
            setMenuAccess(menuAbsensi, LIMITED_ACCESS);
            setMenuAccess(menuKonseling, LIMITED_ACCESS);
            setMenuAccess(menuPelanggaran, LIMITED_ACCESS);
            setMenuAccess(menuHomeVisit, LIMITED_ACCESS);
            setMenuAccess(menuSurat, LIMITED_ACCESS);
            setMenuAccess(menuRiwayatSurat, LIMITED_ACCESS);
            setMenuAccess(menuRiwayatAbsensi, FULL_ACCESS);
            setMenuAccess(menuRekapAbsensi, FULL_ACCESS);
            setMenuAccess(menuRekapPelanggaran, FULL_ACCESS);
            setMenuAccess(menuRiwayatHomeVisit, FULL_ACCESS);
            setMenuAccess(menuAkun, LIMITED_ACCESS);
            return;
        }

        setMenuAccess(menuAbsensi, VIEW_ACCESS);
        setMenuAccess(menuKonseling, VIEW_ACCESS);
        setMenuAccess(menuPelanggaran, VIEW_ACCESS);
        setMenuAccess(menuHomeVisit, VIEW_ACCESS);
        setMenuAccess(menuSurat, VIEW_ACCESS);
        setMenuAccess(menuRiwayatSurat, FULL_ACCESS);
        setMenuAccess(menuRiwayatAbsensi, FULL_ACCESS);
        setMenuAccess(menuRekapAbsensi, FULL_ACCESS);
        setMenuAccess(menuRekapPelanggaran, FULL_ACCESS);
        setMenuAccess(menuRiwayatHomeVisit, FULL_ACCESS);
        setMenuAccess(menuAkun, VIEW_ACCESS);
    }

    private void setMenuAccess(View view, int access) {
        view.setTag(access);
        view.setAlpha(access == FULL_ACCESS ? 1f : 0.45f);
    }

    private boolean isMenuAllowed(View view) {
        Object tag = view.getTag();
        return tag instanceof Integer && (Integer) tag == FULL_ACCESS;
    }

    private void openIfAllowed(View view, Class<?> destination) {
        if (isMenuAllowed(view)) {
            startActivity(new Intent(DashboardActivity.this, destination));
        } else {
            showLimitedAccessMessage();
        }
    }

    private void showLimitedAccessMessage() {
        Toast.makeText(
                this,
                "Akses menu ini dibatasi untuk " + role,
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStatistik();
    }
}
