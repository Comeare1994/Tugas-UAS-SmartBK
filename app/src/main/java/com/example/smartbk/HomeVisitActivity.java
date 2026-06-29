package com.example.smartbk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeVisitActivity extends AppCompatActivity {

    EditText etNama, etKelas, etHasil;
    Button btnFoto, btnLokasi, btnSimpan;
    ImageView imgFoto;
    TextView txtLokasi;

    DatabaseHelper dbHelper;

    String fotoPath = "";

    ActivityResultLauncher<Intent> cameraLauncher;

    FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_visit);

        dbHelper = new DatabaseHelper(this);

        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);

        etNama = findViewById(R.id.etNama);
        etKelas = findViewById(R.id.etKelas);
        etHasil = findViewById(R.id.etHasil);

        btnFoto = findViewById(R.id.btnFoto);
        btnLokasi = findViewById(R.id.btnLokasi);
        btnSimpan = findViewById(R.id.btnSimpan);

        imgFoto = findViewById(R.id.imgFoto);
        txtLokasi = findViewById(R.id.txtLokasi);

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == RESULT_OK
                            && result.getData() != null) {

                        Bundle extras =
                                result.getData().getExtras();

                        if (extras != null) {

                            Object obj = extras.get("data");

                            if (obj instanceof Bitmap) {

                                Bitmap imageBitmap =
                                        (Bitmap) obj;

                                imgFoto.setImageBitmap(
                                        imageBitmap
                                );

                                fotoPath =
                                        "Foto berhasil diambil";

                                Toast.makeText(
                                        HomeVisitActivity.this,
                                        "Foto berhasil diambil",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                    }
                });

        btnFoto.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE
                    );

            if (intent.resolveActivity(
                    getPackageManager()) != null) {

                cameraLauncher.launch(intent);

            } else {

                Toast.makeText(
                        HomeVisitActivity.this,
                        "Aplikasi kamera tidak ditemukan",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        btnLokasi.setOnClickListener(v -> {
            ambilLokasi();
        });

        btnSimpan.setOnClickListener(v -> {

            String nama =
                    etNama.getText().toString().trim();

            String kelas =
                    etKelas.getText().toString().trim();

            String hasil =
                    etHasil.getText().toString().trim();

            String lokasi =
                    txtLokasi.getText().toString();

            String tanggal =
                    new SimpleDateFormat(
                            "dd-MM-yyyy",
                            Locale.getDefault())
                            .format(new Date());

            if (nama.isEmpty()
                    || kelas.isEmpty()
                    || hasil.isEmpty()) {

                Toast.makeText(
                        HomeVisitActivity.this,
                        "Lengkapi semua data terlebih dahulu",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            boolean berhasil =
                    dbHelper.insertHomeVisit(
                            nama,
                            kelas,
                            hasil,
                            lokasi,
                            fotoPath,
                            tanggal
                    );

            if (berhasil) {

                Toast.makeText(
                        HomeVisitActivity.this,
                        "Data Home Visit berhasil disimpan",
                        Toast.LENGTH_SHORT
                ).show();

                etNama.setText("");
                etKelas.setText("");
                etHasil.setText("");

                txtLokasi.setText(
                        "Lokasi belum diambil"
                );

                imgFoto.setImageDrawable(null);

                fotoPath = "";

            } else {

                Toast.makeText(
                        HomeVisitActivity.this,
                        "Gagal menyimpan data",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void ambilLokasi() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    100
            );

            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {

                    if (location != null) {

                        try {

                            Geocoder geocoder =
                                    new Geocoder(
                                            HomeVisitActivity.this,
                                            Locale.getDefault()
                                    );

                            List<Address> addresses =
                                    geocoder.getFromLocation(
                                            location.getLatitude(),
                                            location.getLongitude(),
                                            1
                                    );

                            if (addresses != null
                                    && !addresses.isEmpty()) {

                                Address address =
                                        addresses.get(0);

                                txtLokasi.setText(
                                        address.getAddressLine(0)
                                );

                            } else {

                                txtLokasi.setText(
                                        "Alamat tidak ditemukan"
                                );
                            }

                        } catch (IOException e) {

                            e.printStackTrace();

                            txtLokasi.setText(
                                    "Gagal mengambil alamat"
                            );
                        }

                    } else {

                        txtLokasi.setText(
                                "Lokasi tidak ditemukan"
                        );
                    }
                });
    }
}