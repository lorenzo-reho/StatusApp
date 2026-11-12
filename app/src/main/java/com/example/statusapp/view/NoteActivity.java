package com.example.statusapp.view;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.statusapp.session.DeviceSession;
import com.example.statusapp.R;
import com.example.statusapp.models.BleDevice;
import com.example.statusapp.models.DeviceStatus;
import com.example.statusapp.repository.InterventionRepository;

public class NoteActivity extends AppCompatActivity {

    private final DeviceSession session = DeviceSession.getInstance();
    private final InterventionRepository repository = new InterventionRepository();

    private EditText noteInput;
    private TextView messageText;
    private Button saveButton;
    private ProgressBar saveProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sessione persa (processo ripristinato): niente da registrare
        if (session.getCurrentDevice() == null) {
            finish();
            return;
        }

        setContentView(R.layout.activity_note);

        TextView title = findViewById(R.id.titleText);
        noteInput = findViewById(R.id.noteInput);
        messageText = findViewById(R.id.messageText);
        saveButton = findViewById(R.id.saveButton);
        saveProgress = findViewById(R.id.saveProgress);

        title.setText("Nota per " + session.getCurrentDevice().name);

        // Se la connessione cade, la schermata si chiude
        session.getState().observe(this, state -> {
            if (state == DeviceSession.State.DISCONNECTED) finish();
        });

        saveButton.setOnClickListener(v -> saveIntervention());

    }

    private void saveIntervention() {
        String note = noteInput.getText().toString().trim();
        DeviceStatus status = session.getStatus().getValue();
        BleDevice device = session.getCurrentDevice();

        if (note.isEmpty()) {
            noteInput.setError("Scrivi una nota");
            return;
        }
        if (device == null || status == null) {
            showMessage("Dispositivo non connesso", true);
            return;
        }

        setSaving(true);
        repository.save(device, status, note, new InterventionRepository.SaveCallback() {
            @Override public void onSaved() {
                setSaving(false);
                noteInput.setText("");
                showMessage("Intervento salvato", false);
            }
            @Override public void onError(String message) {
                setSaving(false);
                showMessage(message, true);
            }
        });
    }

    private void setSaving(boolean saving) {
        saveButton.setEnabled(!saving);
        noteInput.setEnabled(!saving);
        saveProgress.setVisibility(saving ? View.VISIBLE : View.GONE);
        if (saving) messageText.setVisibility(View.GONE);
    }

    private void showMessage(String text, boolean error) {
        messageText.setText(text);
        messageText.setTextColor(error ? 0xFFB00020 : 0xFF2E7D32);
        messageText.setVisibility(View.VISIBLE);
    }
}