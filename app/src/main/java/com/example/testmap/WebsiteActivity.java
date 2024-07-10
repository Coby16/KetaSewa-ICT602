package com.example.testmap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class WebsiteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_website);


        Button buttonsc = findViewById(R.id.buttonsc); // ID of your ABOUT button
        buttonsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WebsiteActivity.this, Socar.class); // Ensure About is the correct Activity class name
                startActivity(intent);
            }
        });

        Button buttongc = findViewById(R.id.buttongc); // ID of your ABOUT button
        buttongc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WebsiteActivity.this, Gocar.class); // Ensure About is the correct Activity class name
                startActivity(intent);
            }
        });

        Button buttonw = findViewById(R.id.buttonw); // ID of your ABOUT button
        buttonw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WebsiteActivity.this, Wahdah.class); // Ensure About is the correct Activity class name
                startActivity(intent);
            }
        });

        Button buttonez = findViewById(R.id.buttonez); // ID of your ABOUT button
        buttonez.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WebsiteActivity.this, Easybook.class); // Ensure About is the correct Activity class name
                startActivity(intent);
            }
        });
    }
}

