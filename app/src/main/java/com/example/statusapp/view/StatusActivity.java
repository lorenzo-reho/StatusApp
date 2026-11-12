package com.example.statusapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.statusapp.R;
import com.example.statusapp.models.BleDevice;
import com.example.statusapp.models.DeviceStatus;
import com.example.statusapp.session.DeviceSession;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StatusActivity extends AppCompatActivity {

    private final DeviceSession session = DeviceSession.getInstance();
    private final SimpleDateFormat timeFormat =
            new SimpleDateFormat("HH:mm:ss", Locale.ITALY);

    private TextView updatedText, batteryValue, firmwareValue, errorsValue;
    private ProgressBar batteryBar;
    private Button saveButton;
    private Button historyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        TextView titleText = findViewById(R.id.titleText);
        updatedText = findViewById(R.id.updatedText);
        batteryValue = findViewById(R.id.batteryValue);
        batteryBar = findViewById(R.id.batteryBar);
        firmwareValue = findViewById(R.id.firmwareValue);
        errorsValue = findViewById(R.id.errorsValue);
        saveButton = findViewById(R.id.saveButton);
        historyButton = findViewById(R.id.historyButton);

        BleDevice device = session.getCurrentDevice();
        titleText.setText(device != null ? device.name + "  (" + device.id + ")" : "Dispositivo");

        session.getStatus().observe(this, this::render);
        session.getState().observe(this, state -> {
            if (state == DeviceSession.State.DISCONNECTED) finish();
        });

        saveButton.setOnClickListener(v ->
                startActivity(new Intent(this, NoteActivity.class)));

        historyButton.setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));
    }

    private void render(DeviceStatus s) {
        if (s == null) return;

        updatedText.setText("Aggiornato alle " + timeFormat.format(new Date(s.timestamp)));

        batteryValue.setText(s.batteryLevel + "%");
        batteryValue.setTextColor(s.batteryLevel < 20 ? 0xFFB00020 : 0xFF2E7D32);
        batteryBar.setProgress(s.batteryLevel);

        firmwareValue.setText(s.firmwareVersion);

        if (s.errors.isEmpty()) {
            errorsValue.setText("Nessun errore");
            errorsValue.setTextColor(0xFF2E7D32);
        } else {
            StringBuilder sb = new StringBuilder();

            for (String code : s.errors) {
                sb.append(code).append("\n");
            }

            errorsValue.setText(sb.toString().trim());
            errorsValue.setTextColor(0xFFB00020);
        }
    }


}