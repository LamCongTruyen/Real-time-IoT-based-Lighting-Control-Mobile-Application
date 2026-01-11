package com.example.light_control;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity2 extends AppCompatActivity {
    private TextView dongdien;
    private DatabaseReference nhietdodata;
    private TextView nangluongtieuthu;
    private DatabaseReference kWhData;
    private TextView dienap;
    private DatabaseReference VoltData;
    private TextView congsuat;
    private DatabaseReference PowerData;
    private TextView phantramden1;
    private TextView phantramden2;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch swden1;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch swden2;
    private SeekBar thanhden1;
    private SeekBar thanhden2;
    boolean sw1data_status   = false;
    boolean sw2data_status   = false;
    boolean isFirebaseUpdate = false;
//    private Button button;
//    private Button button2;
//    private Button button3;

    private DatabaseReference den1_data;
    private DatabaseReference den2_data;
    private DatabaseReference sw1data;
    private DatabaseReference sw2data;
//    private DatabaseReference btn2;
//    private DatabaseReference btn3;
    private int dosang;

    public MainActivity2() {
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Button btnlogout = findViewById(R.id.btnlogout);
        dongdien=findViewById(R.id.AmpeValue);
        nangluongtieuthu=findViewById(R.id.KWH);
        dienap=findViewById(R.id.VOLT);
        congsuat=findViewById(R.id.POWER);
        phantramden1=findViewById(R.id.phantramden1);
        phantramden2=findViewById(R.id.phantramden2);
        swden1=findViewById(R.id.swden1);
        swden2=findViewById(R.id.swden2);
        thanhden1=findViewById(R.id.thanhden1);
        thanhden2=findViewById(R.id.thanhden2);
        ImageButton btnToRGB = findViewById(R.id.btnToRGB);
        ImageButton btnToclock = findViewById(R.id.btnToclock);

        kWhData=FirebaseDatabase.getInstance().getReference().child("kWh");
        sw1data=FirebaseDatabase.getInstance().getReference().child("BT1");
        sw2data=FirebaseDatabase.getInstance().getReference().child("BT2");
        // Xử lý đồng bộ nút switch 1
        sw1data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    boolean value = Boolean.TRUE.equals(snapshot.getValue(boolean.class));
                    boolean switchStatus1 = (true == value);
                    swden1.setOnCheckedChangeListener(null);
                    swden1.setChecked(switchStatus1);
                    sw1data_status = switchStatus1;
                    swden1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                            sw1data_status=!sw1data_status;
                            sw1_onoff();
                        }
                    });
                }
            }
            public void onCancelled(@NonNull DatabaseError error) {
                //NOP
            }
        });
        // Xử lý nút switch 1
        swden1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sw1data_status=!sw1data_status;
                sw1_onoff();
            }
        });
        //Xử lý đồng bộ nút switch 2
        sw2data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    boolean value2 = snapshot.getValue(boolean.class);
                    boolean switchStatus2 = (value2 == true);
                    swden2.setOnCheckedChangeListener(null);
                    swden2.setChecked(switchStatus2);
                    sw2data_status = switchStatus2;
                    swden2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                            sw2data_status=!sw2data_status;
                            sw2_onoff();
                        }
                    });
                }
            }
            public void onCancelled(@NonNull DatabaseError error) {
                //NOP
            }
        });
        //Xử lý nút switch 2
        swden2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sw2data_status=!sw2data_status;
                sw2_onoff();
            }
        });
        //Xử lí nút chuyển sang cài đặt RGB
        btnToRGB.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
            startActivity(intent);
        });
        //Xử lí nt chuyển sang cài đặt hẹn giờ
        btnToclock.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity2.this, MainActivity4.class);
            startActivity(intent);
        });
        // trượt thanh ngang đèn 1
        den1_data=FirebaseDatabase.getInstance().getReference().child("LB1");
        den1_data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    dosang = snapshot.getValue(Integer.class);
                    isFirebaseUpdate = true;
                    thanhden1.setProgress(dosang);
                    phantramden1.setText(dosang + "%");
                }
            }
            public void onCancelled(@NonNull DatabaseError error) {
                //NOP
            }
        });
        thanhden1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(!isFirebaseUpdate){
                    den1_data.setValue(i);
                    phantramden1.setText(i + "%");
                }
                    isFirebaseUpdate = false;
            }
//                if(i<10){
//                    den1_data.child("MUC SANG DEN 1").setValue(0);
//                    dosang=0;
//                    phantramden1.setText(dosang+"%");
//                }else if(i>=10 && i<20){
//                    den1_data.child("MUC SANG DEN 1").setValue(10);
//                    dosang=10;
//                    phantramden1.setText(dosang+"%");
//                }else if(i>10 && i<20){
//                    den1_data.child("MUC SANG DEN 1").setValue(10);
//                    dosang=10;
//                    phantramden1.setText(dosang+"%");
//                }else if(i>=20 && i<30){
//                    den1_data.child("MUC SANG DEN 1").setValue(20);
//                    dosang=20;
//                    phantramden1.setText(dosang+"%");
//                }else if(i>=30 && i<40){
//                    den1_data.child("MUC SANG DEN 1").setValue(30);
//                    dosang=30;
//                    phantramden1.setText(dosang+"%");
//                }else if(i>=40 && i<50){
//                    den1_data.child("MUC SANG DEN 1").setValue(40);
//                    dosang=40;
//                    phantramden1.setText(dosang+"%");
//                }else if(i>=50 && i<60){
//                    den1_data.child("MUC SANG DEN 1").setValue(50);
//                    dosang=50;
//                    phantramden1.setText(dosang+"%");
//                }else if(i>=60 && i<70){
//                    den1_data.child("MUC SANG DEN 1").setValue(60);
//                    dosang=60;
//                    phantramden1.setText(dosang+"%");
//                }else if(i>=70 && i<80){
//                    den1_data.child("MUC SANG DEN 1").setValue(70);
//                    dosang=70;
//                    phantramden1.setText(dosang+"%");
//                }else if(i>=80 && i<90){
//                    den1_data.child("MUC SANG DEN 1").setValue(80);
//                    dosang=80;
//                    phantramden1.setText(dosang+"%");
//                }else if(i>=90 && i<100){
//                    den1_data.child("MUC SANG DEN 1").setValue(90);
//                    dosang=90;
//                    phantramden1.setText(dosang+"%");
//                }else {
//                    den1_data.child("MUC SANG DEN 1").setValue(100);
//                    dosang=100;
//                    phantramden1.setText(dosang+"%");
//                }///////////
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        // trượt thanh ngang đèn 2
        den2_data=FirebaseDatabase.getInstance().getReference().child("LB2");
        den2_data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    dosang = snapshot.getValue(Integer.class);
                    isFirebaseUpdate = true;
                    thanhden2.setProgress(dosang);
                    phantramden2.setText(dosang + "%");
                }
            }
            public void onCancelled(@NonNull DatabaseError error) {
                //NOP
            }
        });

        thanhden2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b){
                if (!isFirebaseUpdate) {
                    den2_data.setValue(i);
                    phantramden2.setText(i + "%");
                }
                isFirebaseUpdate = false;
                }
//                if(i<10){
//                    den2_data.child("MUC SANG DEN 2").setValue(0);
//                    dosang=0;
//                    phantramden2.setText(dosang+"%");
//                }else if(i>=10 && i<20){
//                    den2_data.child("MUC SANG DEN 2").setValue(10);
//                    dosang=10;
//                    phantramden2.setText(dosang+"%");
//                }else if(i>10 && i<20){
//                    den2_data.child("MUC SANG DEN 2").setValue(10);
//                    dosang=10;
//                    phantramden2.setText(dosang+"%");
//                }else if(i>=20 && i<30){
//                    den2_data.child("MUC SANG DEN 2").setValue(20);
//                    dosang=20;
//                    phantramden2.setText(dosang+"%");
//                }else if(i>=30 && i<40){
//                    den2_data.child("MUC SANG DEN 2").setValue(30);
//                    dosang=30;
//                    phantramden2.setText(dosang+"%");
//                }else if(i>=40 && i<50){
//                    den2_data.child("MUC SANG DEN 2").setValue(40);
//                    dosang=40;
//                    phantramden2.setText(dosang+"%");
//                }else if(i>=50 && i<60){
//                    den2_data.child("MUC SANG DEN 2").setValue(50);
//                    dosang=50;
//                    phantramden2.setText(dosang+"%");
//                }else if(i>=60 && i<70){
//                    den2_data.child("MUC SANG DEN 2").setValue(60);
//                    dosang=60;
//                    phantramden2.setText(dosang+"%");
//                }else if(i>=70 && i<80){
//                    den2_data.child("MUC SANG DEN 2").setValue(70);
//                    dosang=70;
//                    phantramden2.setText(dosang+"%");
//                }else if(i>=80 && i<90){
//                    den2_data.child("MUC SANG DEN 2").setValue(80);
//                    dosang=80;
//                    phantramden2.setText(dosang+"%");
//                }else if(i>=90 && i<100){
//                    den2_data.child("MUC SANG DEN 2").setValue(90);
//                    dosang=90;
//                    phantramden2.setText(dosang+"%");
//                }else {
//                    den2_data.child("MUC SANG DEN 2").setValue(100);
//                    dosang=100;
//                    phantramden2.setText(dosang+"%");
//                }////////////////
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        nhietdodata = FirebaseDatabase.getInstance().getReference();
        final ValueEventListener nd_dht11 = nhietdodata.child("AMPE").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dongdien.setText(Objects.requireNonNull(snapshot.getValue()).toString() + " A");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        kWhData = FirebaseDatabase.getInstance().getReference();
        final ValueEventListener watt = kWhData.child("kWh").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nangluongtieuthu.setText(Objects.requireNonNull(snapshot.getValue()).toString() + " kWh");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        VoltData = FirebaseDatabase.getInstance().getReference();
        final ValueEventListener volt = VoltData.child("VOLT").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dienap.setText(Objects.requireNonNull(snapshot.getValue()).toString() + " V");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        PowerData = FirebaseDatabase.getInstance().getReference();
        final ValueEventListener P = PowerData.child("WATT").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                congsuat.setText(Objects.requireNonNull(snapshot.getValue()).toString() + " W");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // xu ly nut logout
        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void sw2_onoff() {
        if(sw2data_status){
            sw2data.setValue(true);
        }else {
            sw2data.setValue(false);
        }
    }
    private void sw1_onoff() {
        if(sw1data_status){//sw1data_status==true
            sw1data.setValue(true);
        }else {
            sw1data.setValue(false);
        }
    }
}