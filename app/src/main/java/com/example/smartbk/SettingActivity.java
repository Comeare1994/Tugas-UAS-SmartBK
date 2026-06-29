package com.example.smartbk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {

    private static final int REQUEST_PICK_PROFILE_PHOTO = 10;
    private static final String PREF_NAME = "smartbk_setting";
    private static final String KEY_PHOTO_URI = "photo_uri";
    private static final String KEY_NAMA_GURU = "nama_guru";
    private static final String KEY_JABATAN_GURU = "jabatan_guru";

    ImageView imgAdminProfile;
    TextView txtGuruNamePreview, txtGuruRolePreview;
    EditText etNamaGuru;
    Spinner spinnerJabatanGuru;
    Button btnPilihFoto, btnSimpanBiodata;
    LinearLayout menuKelolaAkun, menuLogout;
    SharedPreferences preferences;

    String[] jabatanOptions = {
            "Wali Kelas 7A",
            "Wali Kelas 7B",
            "Wali Kelas 7C",
            "Wali Kelas 8A",
            "Wali Kelas 8B",
            "Wali Kelas 8C",
            "Wali Kelas 9A",
            "Wali Kelas 9B",
            "Wali Kelas 9C",
            "Guru BK Kelas 7",
            "Guru BK Kelas 8",
            "Guru BK Kelas 9",
            "Kepala Sekolah"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        imgAdminProfile = findViewById(R.id.imgAdminProfile);
        txtGuruNamePreview = findViewById(R.id.txtGuruNamePreview);
        txtGuruRolePreview = findViewById(R.id.txtGuruRolePreview);
        etNamaGuru = findViewById(R.id.etNamaGuru);
        spinnerJabatanGuru = findViewById(R.id.spinnerJabatanGuru);
        btnPilihFoto = findViewById(R.id.btnPilihFoto);
        btnSimpanBiodata = findViewById(R.id.btnSimpanBiodata);
        menuKelolaAkun = findViewById(R.id.menuKelolaAkun);
        menuLogout = findViewById(R.id.menuLogout);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                jabatanOptions
        );
        spinnerJabatanGuru.setAdapter(adapter);

        loadSettings();

        btnPilihFoto.setOnClickListener(v -> pickProfilePhoto());
        btnSimpanBiodata.setOnClickListener(v -> saveBiodata());
        menuKelolaAkun.setOnClickListener(v ->
                startActivity(new Intent(SettingActivity.this, AccountActivity.class))
        );
        menuLogout.setOnClickListener(v -> logout());
    }

    private void pickProfilePhoto() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_PICK_PROFILE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_PICK_PROFILE_PHOTO || resultCode != RESULT_OK || data == null) {
            return;
        }

        Uri uri = data.getData();
        if (uri == null) {
            return;
        }

        final int flags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
        try {
            getContentResolver().takePersistableUriPermission(uri, flags);
        } catch (SecurityException ignored) {
        }

        preferences.edit().putString(KEY_PHOTO_URI, uri.toString()).apply();
        imgAdminProfile.setImageURI(uri);
        Toast.makeText(this, "Foto profil diperbarui", Toast.LENGTH_SHORT).show();
    }

    private void saveBiodata() {
        String namaGuru = etNamaGuru.getText().toString().trim();
        String jabatan = spinnerJabatanGuru.getSelectedItem().toString();

        if (namaGuru.isEmpty()) {
            Toast.makeText(this, "Nama guru wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        preferences.edit()
                .putString(KEY_NAMA_GURU, namaGuru)
                .putString(KEY_JABATAN_GURU, jabatan)
                .apply();

        updatePreview(namaGuru, jabatan);
        Toast.makeText(this, "Biodata guru tersimpan", Toast.LENGTH_SHORT).show();
    }

    private void loadSettings() {
        String photoUri = preferences.getString(KEY_PHOTO_URI, "");
        if (!photoUri.isEmpty()) {
            imgAdminProfile.setImageURI(Uri.parse(photoUri));
        }

        String namaGuru = preferences.getString(KEY_NAMA_GURU, "");
        String jabatan = preferences.getString(KEY_JABATAN_GURU, jabatanOptions[0]);
        etNamaGuru.setText(namaGuru);

        for (int i = 0; i < jabatanOptions.length; i++) {
            if (jabatanOptions[i].equals(jabatan)) {
                spinnerJabatanGuru.setSelection(i);
                break;
            }
        }

        updatePreview(namaGuru, jabatan);
    }

    private void updatePreview(String namaGuru, String jabatan) {
        txtGuruNamePreview.setText(namaGuru.isEmpty() ? "Setting Admin" : namaGuru);
        txtGuruRolePreview.setText(jabatan);
    }

    private void logout() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
