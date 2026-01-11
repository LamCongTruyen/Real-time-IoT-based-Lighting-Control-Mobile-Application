package com.example.light_control;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.EditText;
import android.widget.Toast;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity4 extends AppCompatActivity {
    private EditText editTextNote;
    private TimePicker timePicker;
    private TimerData timerData;
    private Button btnSetTimer;
    private Button btnCancelTimer;
    private ImageButton btnBack;
    private int hours;
    private int minute;
    private String note;
    private String timerId;
    private boolean isFirebaseUpdate = false;
    private DatabaseReference realtime;
    private FirebaseDatabase database;
    private DatabaseReference timerRef;
    private List<String> timerIdList;
    private List<TimerData> timerDataList;
    private ArrayAdapter<String> adapter;
    private List<String> displayList;
    private ListView listViewTimers;
    private ArrayList<String> timerList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        // Khởi tạo UI
        timePicker = findViewById(R.id.timePicker);
        btnSetTimer = findViewById(R.id.btnSetTimer);
        btnCancelTimer = findViewById(R.id.btnCancelTimer);
        editTextNote = findViewById(R.id.editTextNote);
        btnBack = findViewById(R.id.btnBack);
        listViewTimers = findViewById(R.id.listViewTimers);

        realtime = FirebaseDatabase.getInstance().getReference("TIME");
        database = FirebaseDatabase.getInstance();
//        timerRef = database.getReference("TIME");

        timerIdList = new ArrayList<>();
        timerDataList = new ArrayList<>();
        displayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        listViewTimers.setAdapter(adapter);

        timerData = new TimerData(hours, minute, note);
        timerId = realtime.push().getKey();
//        syncDataFromFirebase();


        //// Lắng nghe sự kiện đồng bộ
//        realtime.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                // Xóa dữ liệu cũ trước khi cập nhật danh sách mới
//                timerIdList.clear();
//                timerDataList.clear();
//                displayList.clear();
//                // Duyệt qua từng mục trong cơ sở dữ liệu Firebase
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    // Lấy timerId và TimerData từ Firebase
//                    timerId = snapshot.getKey();
//                    timerData = snapshot.getValue(TimerData.class);
//
//                    // Kiểm tra nếu dữ liệu không null
//                    if (timerId != null && timerData != null) {
//                        timerIdList.add(timerId);
//                        timerDataList.add(timerData);
//                        // Thêm dữ liệu vào displayList để hiển thị trên ListView
//                        displayList.add("Hẹn giờ: " + timerData.getHour() + " : " + timerData.getMinute() + "     Ghi chú: " + timerData.getNote());
//                    }
//                }
//                // Thông báo cho adapter để cập nhật ListView
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý lỗi khi lắng nghe Firebase bị hủy
//                Toast.makeText(MainActivity4.this, "Lỗi khi đồng bộ dữ liệu: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });

        // Lắng nghe sự kiện nút Set Timer
        btnSetTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hours = timePicker.getHour();
                minute = timePicker.getMinute();
                note = editTextNote.getText().toString();
                if (timerId != null) {
                    timerData = new TimerData(hours, minute, note);
                    timerIdList.add(timerId);
                    timerDataList.add(timerData);
                    displayList.add("Hẹn giờ: " + hours + " : " + minute + "     Ghi chú: " + note);
                    adapter.notifyDataSetChanged();

                    realtime.child(timerId).setValue(timerData).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity4.this, "Đã đặt hẹn giờ", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity4.this, "Lỗi: Không thể đặt hẹn giờ", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Khởi tạo lắng nghe dữ liệu từ Firebase cho timerId vừa tạo
//                    syncDataFromFirebase(timerId);
                } else {
                    Toast.makeText(MainActivity4.this, "Hẹn giờ vào: " + hours + ":" + minute + "\nGhi chú: " + note, Toast.LENGTH_LONG).show();
                }
            }
        });

        // Thiết lập sự kiện click cho ListView
        listViewTimers.setOnItemClickListener((parent, view, position, id) -> {
            // Hiển thị hộp thoại xác nhận để xóa hẹn giờ
            new AlertDialog.Builder(MainActivity4.this)
                    .setTitle("Xóa Hẹn Giờ")
                    .setMessage("Bạn có muốn xóa hẹn giờ này không?")
                    .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Xóa hẹn giờ đã chọn
                            String timerIdToDelete = timerIdList.get(position);
                            realtime.child(timerIdToDelete).removeValue().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity4.this, "Đã hủy hẹn giờ", Toast.LENGTH_SHORT).show();
                                    // Xóa thông tin khỏi danh sách hiển thị và cập nhật
                                    timerIdList.remove(position);
                                    timerDataList.remove(position);
                                    displayList.remove(position);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(MainActivity4.this, "Lỗi: Không thể hủy hẹn giờ", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


//    private void syncDataFromFirebase() {
//        // Lắng nghe dữ liệu thay đổi từ Firebase
//        timerRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                // Xóa các danh sách hiện có để cập nhật với dữ liệu mới từ Firebase
////                    timerIdList.clear();
////                    timerDataList.clear();
////                    displayList.clear();
//                timerList.clear();
//
//                // Duyệt qua tất cả các timerData trong Firebase
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    // Lấy key của từng timer (timerId)
//                    isFirebaseUpdate = true;
//                    Integer time  = snapshot.child("TIME").getValue(Integer.class);
//                    // Lấy dữ liệu TimerData từ Firebase
//                    Integer note  = snapshot.child("TIME").getValue(Integer.class);
//                    // Thêm timerId và timerData vào danh sách
////                    if (timerId != null && timerData != null) {
////                        timerIdList.add(timerId);
////                        timerDataList.add(timerData);
////                        // Cập nhật danh sách hiển thị với dữ liệu mới
////                        displayList.add("Hẹn giờ: " + timerData.getHour() + ":" + timerData.getMinute() + " - " + timerData.getNote());
////                    }
//                    if (time != null && note != null) {
//                        timerList.add("Time: " + time + " | Note: " + note);
//                    }
//                }
//                // Thông báo cho adapter cập nhật giao diện
//                adapter.notifyDataSetChanged();
////                isFirebaseUpdate = fasle;
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý lỗi nếu có vấn đề với Firebase
//                Toast.makeText(MainActivity4.this, "Lỗi khi đồng bộ dữ liệu: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
    private void initializeUI() {
        listViewTimers = findViewById(R.id.listViewTimers);
//        realtime = FirebaseDatabase.getInstance().getReference("TIME");
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        listViewTimers.setAdapter(adapter);
    }
    public class TimerData {
        private int hour;
        private int minute;
        private String note;
//
        public TimerData() {
            // Constructor mặc định cho Firebase
        }
        public TimerData(int hour, int minute, String note) {
            this.hour = hour;
            this.minute = minute;
            this.note = note;
        }
        public int getHour() {
            return hour;
        }
        public void setHour(int hour) {
            this.hour = hour;
        }
        public int getMinute() {
            return minute;
        }
        public void setMinute(int minute ) {
            this.minute = minute ;
        }
        public String getNote() {
            return note;
        }
        public void setNote(String note ) {
            this.note = note ;
        }
    }
}
