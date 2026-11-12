package com.example.statusapp.repository;

import com.example.statusapp.models.BleDevice;
import com.example.statusapp.models.DeviceStatus;

public interface DeviceGateway {

    interface ScanListener {
        void onDeviceFound(BleDevice device);
        void onScanFinished();
    }

    interface ConnectionListener {
        void onConnected();
        void onStatus(DeviceStatus status);
        void onError(String message);   // fallimento o perdita di connessione
    }

    void startScan(ScanListener listener);
    void stopScan();
    void connect(String deviceId, ConnectionListener listener);
    void disconnect();
}
