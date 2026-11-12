package com.example.statusapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.statusapp.repository.DeviceGateway;
import com.example.statusapp.adapters.DeviceListAdapter;
import com.example.statusapp.session.DeviceSession;
import com.example.statusapp.R;
import com.example.statusapp.models.BleDevice;
import com.google.firebase.auth.FirebaseAuth;

public class ScanActivity extends AppCompatActivity {

    private final DeviceSession session = DeviceSession.getInstance();
    private Button scanButton;
    private Button detailsButton;

    private ProgressBar scanProgress;
    private TextView infoText;
    private TextView errorText;

    private DeviceListAdapter adapter;
    boolean scanning = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        scanButton = findViewById(R.id.scanButton);
        detailsButton = findViewById(R.id.detailsButton);
        scanProgress = findViewById(R.id.scanProgress);
        infoText = findViewById(R.id.infoText);
        errorText = findViewById(R.id.errorText);
        Button logoutButton = findViewById(R.id.logoutButton);

        RecyclerView list = findViewById(R.id.deviceList);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DeviceListAdapter(new DeviceListAdapter.OnDeviceClickListener() {
            @Override
            public void onDeviceClick(BleDevice device) {
                if (scanning) stopScan();
                hideError();
                session.connect(device);
            }
        });

        detailsButton.setOnClickListener(v ->
                startActivity(new Intent(this, StatusActivity.class)));

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scanning) stopScan();
                else startScan();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });


        list.setAdapter(adapter);

        session.getState().observe(this, this::renderState);

        /*
        session.getStatus().observe(this, s -> {
            BleDevice d = session.getCurrentDevice();
            if (s != null && d != null) {
                infoText.setText("Connesso a " + d.name + "  •  Batteria " + s.batteryLevel + "%");
            }
        });
        */

        session.getError().observe(this, msg -> {
            if (msg != null) showError(msg);
        });

    }


    @Override
    protected void onStart(){
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            goToLogin();
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        goToLogin();
    }

    private void goToLogin() {
        Intent i = new Intent(this, LoginActivity.class);

        // ripulisco lo stack e l'activity di login rimane l'unica aperta.
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    private void startScan() {
        adapter.clear();
        hideError();
        scanning = true;
        scanButton.setText("Interrompi ricerca");
        scanProgress.setVisibility(View.VISIBLE);

        session.startScan(new DeviceGateway.ScanListener() {
            @Override public void onDeviceFound(BleDevice device) {
                adapter.add(device);
            }
            @Override public void onScanFinished() {
                finishScan();
                // if (adapter.getItemCount() == 0) showError("Nessun dispositivo trovato");
            }
        });
    }

    private void stopScan() {
        session.stopScan();
        finishScan();
    }

    private void finishScan() {
        scanning = false;
        scanButton.setText("Cerca dispositivi");
        scanProgress.setVisibility(View.GONE);
    }

    private void renderState(DeviceSession.State state) {
        BleDevice d = session.getCurrentDevice();
        switch (state) {
            case CONNECTING:
                detailsButton.setEnabled(false);
                infoText.setText("Connessione a " + (d != null ? d.name : "") + "…");
                break;
            case CONNECTED:
                detailsButton.setEnabled(true);
                infoText.setText("Connesso a " + (d != null ? d.name : ""));
                break;
            default:
                detailsButton.setEnabled(false);
                infoText.setText("Nessun dispositivo connesso");
        }
    }

    private void showError(String msg){
        errorText.setText(msg);
        errorText.setVisibility(View.VISIBLE);
    }

    private void hideError(){
        errorText.setVisibility(View.GONE);
    }

}
