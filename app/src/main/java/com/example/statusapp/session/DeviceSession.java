package com.example.statusapp.session;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.statusapp.repository.DeviceGateway;
import com.example.statusapp.repository.SimulatedDeviceGateway;
import com.example.statusapp.models.BleDevice;
import com.example.statusapp.models.DeviceStatus;

public class DeviceSession {

    public enum State { DISCONNECTED, CONNECTING, CONNECTED }

    private static DeviceSession instance;
    private final DeviceGateway gateway = new SimulatedDeviceGateway();

    private final MutableLiveData<State> state = new MutableLiveData<>(State.DISCONNECTED);
    private final MutableLiveData<DeviceStatus> status = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public LiveData<State> getState() {
        return state;
    }

    public LiveData<String> getError() {
        return error;
    }

    private BleDevice currentDevice;

    public static synchronized DeviceSession getInstance() {
        if (instance == null) instance = new DeviceSession();
        return instance;
    }

    private DeviceSession() {}
    public LiveData<DeviceStatus> getStatus() { return status; }
    public BleDevice getCurrentDevice() { return currentDevice; }

    public void startScan(DeviceGateway.ScanListener listener) {
        gateway.startScan(listener);
    }
    public void stopScan() {
        gateway.stopScan();
    }

    public void connect(BleDevice device) {
        disconnect();
        if (state.getValue() == State.CONNECTING) return;
        currentDevice = device;
        error.setValue(null);
        status.setValue(null);
        state.setValue(State.CONNECTING);

        gateway.connect(device.id, new DeviceGateway.ConnectionListener() {
            @Override public void onConnected() {
                state.setValue(State.CONNECTED);
            }
            @Override public void onStatus(DeviceStatus s) {
                status.setValue(s);
            }
            @Override public void onError(String message) {
                currentDevice = null;
                status.setValue(null);
                state.setValue(State.DISCONNECTED);
                error.setValue(message);
            }
        });

    }


    public void disconnect() {
        gateway.disconnect();
        currentDevice = null;
        status.setValue(null);
        state.setValue(State.DISCONNECTED);
    }

}
