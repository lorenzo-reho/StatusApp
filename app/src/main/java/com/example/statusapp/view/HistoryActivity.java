package com.example.statusapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.statusapp.session.DeviceSession;
import com.example.statusapp.adapters.InterventionAdapter;
import com.example.statusapp.R;
import com.example.statusapp.models.BleDevice;
import com.example.statusapp.models.Intervention;
import com.example.statusapp.repository.InterventionRepository;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private final DeviceSession session = DeviceSession.getInstance();
    private final InterventionRepository repository = new InterventionRepository();
    private InterventionAdapter adapter;
    private String deviceId;
    private TextView errorText;
    private ProgressBar progress;
    private ListenerRegistration registration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BleDevice device  = session.getCurrentDevice();

        if(device  == null){
            finish();
            return;
        }

        deviceId = device.id;

        setContentView(R.layout.activity_history);
        setTitle("Storico " + device.name);

        progress = findViewById(R.id.historyProgress);
        errorText = findViewById(R.id.errorText);
        adapter = new InterventionAdapter(new InterventionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Intervention intervention) {
               openDetail(intervention);
            }
        });

        RecyclerView list = findViewById(R.id.historyList);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (deviceId == null) return;

        progress.setVisibility(View.VISIBLE);
        registration = repository.listen(deviceId,
                new InterventionRepository.HistoryCallback() {
                    @Override public void onData(List<Intervention> items) {
                        progress.setVisibility(View.GONE);
                        errorText.setVisibility(View.GONE);
                        adapter.setItems(items);

                    }
                    @Override public void onError(String message) {
                        progress.setVisibility(View.GONE);
                        errorText.setText(message);
                        errorText.setVisibility(View.VISIBLE);
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
    private void openDetail(Intervention intervention) {
        if (intervention.id == null) return;
        Intent i = new Intent(this, InterventionDetailActivity.class);
        i.putExtra(InterventionDetailActivity.INTERVENTION_ID, intervention.id);
        startActivity(i);
    }
}
