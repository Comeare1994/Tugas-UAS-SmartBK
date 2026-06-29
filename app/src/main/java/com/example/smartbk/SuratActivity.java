package com.example.smartbk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class SuratActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surat);

        LinearLayout menuSuratPemanggilan = findViewById(R.id.menuSuratPemanggilan);
        LinearLayout menuSuratPernyataan = findViewById(R.id.menuSuratPernyataan);

        menuSuratPemanggilan.setOnClickListener(v ->
                startActivity(new Intent(SuratActivity.this, SuratPemanggilanActivity.class))
        );

        menuSuratPernyataan.setOnClickListener(v ->
                startActivity(new Intent(SuratActivity.this, SuratPernyataanActivity.class))
        );
    }
}
