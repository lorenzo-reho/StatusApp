package com.example.statusapp.view;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.statusapp.R;
import com.example.statusapp.models.BleDevice;
import com.example.statusapp.models.Intervention;
import com.example.statusapp.repository.InterventionRepository;
import com.example.statusapp.session.DeviceSession;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class InterventionDetailActivity extends AppCompatActivity {
    public static final String INTERVENTION_ID = "intervention_id";
    private final InterventionRepository repository = new InterventionRepository();
    private final SimpleDateFormat format =
            new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ITALY);

    private ListenerRegistration registration;
    private String deviceId;
    private String interventionId;

    private TextView titleText, dateText, technicianText, noteText,
            batteryText, firmwareText, errorsText, errorMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BleDevice device = DeviceSession.getInstance().getCurrentDevice();
        interventionId = getIntent().getStringExtra(INTERVENTION_ID);

        if (device == null || interventionId == null) {
            finish();
            return;
        }

        deviceId = device.id;

        setContentView(R.layout.activity_intervention_detail);
        setTitle("Dettaglio intervento");

        titleText = findViewById(R.id.titleText);
        dateText = findViewById(R.id.dateText);
        technicianText = findViewById(R.id.technicianText);
        noteText = findViewById(R.id.noteText);
        batteryText = findViewById(R.id.batteryText);
        firmwareText = findViewById(R.id.firmwareText);
        errorsText = findViewById(R.id.errorsText);
        errorMessage = findViewById(R.id.errorMessage);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (deviceId == null) return;

        registration = repository.listenOne(deviceId, interventionId,
                new InterventionRepository.DetailCallback() {
                    @Override public void onData(Intervention i) {
                        errorMessage.setVisibility(View.GONE);
                        render(i);
                    }
                    @Override public void onNotFound() {
                        showError("Intervento non trovato");
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (registration != null) {
            registration.remove();
            registration = null;
        }
    }

    private void render(Intervention i) {
        titleText.setText("Intervento #" + i.shortId());
        dateText.setText(i.createdAt != null
                ? format.format(i.createdAt)
                : "In sincronizzazione…");
        technicianText.setText("Tecnico: " + i.technicianEmail);
        noteText.setText(i.note);

        batteryText.setText("Batteria: " + i.battery + "%");
        firmwareText.setText("Firmware: " + i.firmware);

        List<String> errors = i.errors;
        if (errors == null || errors.isEmpty()) {
            errorsText.setText("Errori: nessuno");
        } else {
            StringBuilder sb = new StringBuilder("Errori: ");
            for (int k = 0; k < errors.size(); k++) {
                if (k > 0) sb.append(", ");
                sb.append(errors.get(k).toLowerCase());
            }
            errorsText.setText(sb.toString());
        }
    }

    private void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setVisibility(View.VISIBLE);
    }

}
