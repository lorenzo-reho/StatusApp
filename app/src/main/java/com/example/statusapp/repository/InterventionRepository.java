package com.example.statusapp.repository;

import android.os.Handler;
import android.os.Looper;

import com.example.statusapp.models.BleDevice;
import com.example.statusapp.models.DeviceStatus;
import com.example.statusapp.models.Intervention;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class InterventionRepository {

    public interface SaveCallback {
        void onSaved();

        void onError(String message);
    }

    public interface HistoryCallback {
        void onData(List<Intervention> items);

        void onError(String message);
    }

    public interface DetailCallback{
        void onData(Intervention item);
        void onNotFound();
    }

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collection(String deviceId) {
        return db.collection("devices").document(deviceId).collection("interventions");
    }

    public void save(BleDevice device, DeviceStatus status, String note,
                     final SaveCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onError("Sessione scaduta devi rifare l'accesso");
            return;
        }

        Intervention i = new Intervention();
        i.deviceId = device.id;
        i.technicianId = user.getUid();
        i.technicianEmail = user.getEmail();
        i.note = note;
        i.battery = status.batteryLevel;
        i.firmware = status.firmwareVersion;
        i.errors = new ArrayList<>(status.errors);

        collection(device.id).document().set(i)
                .addOnSuccessListener(v -> {
                    callback.onSaved();
                })
                .addOnFailureListener(e -> {
                    // TODO
                });
    }

    public ListenerRegistration listen(String deviceId, final HistoryCallback callback) {
        return collection(deviceId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(50)
                .addSnapshotListener((snap, e) -> {
                    if (e != null) {
                        return;
                    }
                    List<Intervention> items = snap != null ? snap.toObjects(Intervention.class) : null;
                    if(items != null && !items.isEmpty())
                        callback.onData(snap.toObjects(Intervention.class));
                    else
                        callback.onError("Interventi non rilevati");
                });
    }

    public ListenerRegistration listenOne(String deviceId, String interventionId,
                                          final DetailCallback callback) {
        return collection(deviceId).document(interventionId)
                .addSnapshotListener((snap, e) -> {
                    if (e != null) {
                        return;
                    }

                    Intervention i = snap != null ? snap.toObject(Intervention.class) : null;
                    if (i == null) callback.onNotFound();
                    else callback.onData(i);
                });
    }
}