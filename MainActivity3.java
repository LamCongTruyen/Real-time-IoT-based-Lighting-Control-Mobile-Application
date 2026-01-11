package com.example.light_control;

import android.graphics.Color;
import android.util.Log;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity3 extends AppCompatActivity {
    private ImageButton btnBack;
    private Button btnwarmwhite;
    private Button btncoldwhite;
    private Button btndaylightwhite;
    private SeekBar seekBarRed, seekBarGreen, seekBarBlue;
    private View colorDisplay;
    boolean isFirebaseUpdate = false;
    DatabaseReference rgbdata;
    int red = 0;
    int green = 0;
    int blue = 0;
    int warmwhitedate;
    int coldwhite;
    int daylightwhite;
    int color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        seekBarRed = findViewById(R.id.seekBarRed);
        seekBarGreen = findViewById(R.id.seekBarGreen);
        seekBarBlue = findViewById(R.id.seekBarBlue);
        colorDisplay = findViewById(R.id.colorDisplay);
        btnBack = findViewById(R.id.btnBack);
        btnwarmwhite = findViewById(R.id.btnwarmwhite);
        btncoldwhite = findViewById(R.id.btncoldwhite);
        btndaylightwhite = findViewById(R.id.btndaylightwhite);

        rgbdata = FirebaseDatabase.getInstance().getReference().child("LEDRGB");

        //xử lí nút quay lại trang
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //nút nhan 3 chế độ đèn
        btnwarmwhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                warmwhitedate = Color.rgb(255,218,185);
                warmwhitedate = warmwhitedate & 0xFFFFFF;
                rgbdata.setValue(warmwhitedate);
            }
        });
        btncoldwhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coldwhite = Color.rgb(240,248,255);
                coldwhite = coldwhite & 0xFFFFFF;
                rgbdata.setValue(coldwhite);
            }
        });
        btndaylightwhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                daylightwhite = Color.rgb(255,255,255);
                daylightwhite = daylightwhite & 0xFFFFFF;
                rgbdata.setValue(daylightwhite);
            }
        });
        rgbdata.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && !isFirebaseUpdate) {
                    int colorhex = snapshot.getValue(Integer.class);
                    int red = (colorhex >> 16) & 0xFF;
                    int green = (colorhex >> 8) & 0xFF;
                    int blue = colorhex & 0xFF;
                    isFirebaseUpdate = true;
                    seekBarRed.setProgress(red);
                    seekBarGreen.setProgress(green);
                    seekBarBlue.setProgress(blue);
                    isFirebaseUpdate = false;
                }
            }
            public void onCancelled (@NonNull DatabaseError error){
                //NOP
            }
        });
        //thanh điều chỉnh màu RGB
        SeekBar.OnSeekBarChangeListener colorChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateColorDisplay();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        };
        seekBarRed.setOnSeekBarChangeListener(colorChangeListener);
        seekBarGreen.setOnSeekBarChangeListener(colorChangeListener);
        seekBarBlue.setOnSeekBarChangeListener(colorChangeListener);
    }
    // Cập nhật màu hiển thị dựa trên giá trị của các SeekBar
    private void updateColorDisplay() {
        red = seekBarRed.getProgress();
        green = seekBarGreen.getProgress();
        blue = seekBarBlue.getProgress();

        color = Color.rgb(red, green, blue);
        colorDisplay.setBackgroundColor(color);
////      color = Math.abs(color);
////      int colors = 16777216 + color;
//        color = color & 0xFFFFFF;//giữ lại 24bit màu RGB
//        rgbdata.setValue(color);
        if (!isFirebaseUpdate) {
            color = color & 0xFFFFFF;
            isFirebaseUpdate = true;
            rgbdata.setValue(color);
            isFirebaseUpdate = false;
        }
    }
}

