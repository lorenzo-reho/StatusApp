package com.example.statusapp.repository;

import android.os.Handler;
import android.os.Looper;

import com.example.statusapp.models.BleDevice;
import com.example.statusapp.models.DeviceStatus;
import java.util.Random;
public class SimulatedDeviceGateway implements DeviceGateway {
    private final Handler scanHandler = new Handler(Looper.getMainLooper());
    private final Handler connHandler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();

    private int battery = 80;
    @Override
    public void startScan(ScanListener listener) {
        stopScan();

        // simulazione latenza per rilevazione dispositivo Bluetooth

        scanHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                listener.onDeviceFound(new BleDevice("TEST-123", "Dispositivo 1", -50));
            }
        }, 800);

        scanHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                listener.onDeviceFound(new BleDevice("TEST-223", "Dispositivo 2", -75));
            }
        }, 1500);

        scanHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                listener.onDeviceFound(new BleDevice("TEST-445", "Dispositivo 3", -85));
            }
        }, 2000);

        scanHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                listener.onScanFinished();
            }
        }, 3000);

    }

    @Override
    public void stopScan() {
        scanHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void connect(final String deviceId, final ConnectionListener listener) {
        disconnect();
        battery = 80;
        connHandler.postDelayed(() -> {
            if ("TEST-223".equals(deviceId)) {
                listener.onError("Connessione fallita: dispositivo fuori portata");
                return;
            }
            listener.onConnected();
            startStatusUpdates(deviceId, listener);
        }, 1500);
    }

    private void startStatusUpdates(final String deviceId, final ConnectionListener listener) {
        connHandler.post(new Runnable() {
            @Override
            public void run() {
                battery = Math.max(0, battery - random.nextInt(2));
                DeviceStatus s = new DeviceStatus();
                s.deviceId = deviceId;
                s.batteryLevel = battery;
                s.firmwareVersion = "1.4.2";
                s.timestamp = System.currentTimeMillis();
                if (battery < 20) s.errors.add("BATTERY_LOW");
                if (random.nextInt(10) == 0) s.errors.add("SENSOR_FAULT");
                listener.onStatus(s);
                connHandler.postDelayed(this, 2000);
            }
        });
    }


    @Override
    public void disconnect() {
        connHandler.removeCallbacksAndMessages(null);
    }
}
