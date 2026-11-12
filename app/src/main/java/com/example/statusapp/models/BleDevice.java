package com.example.statusapp.models;

public class BleDevice {
    public final String id;
    public final String name;
    public final int rssi;

    public BleDevice(String id, String name, int rssi) {
        this.id = id;
        this.name = name;
        this.rssi = rssi;
    }
}