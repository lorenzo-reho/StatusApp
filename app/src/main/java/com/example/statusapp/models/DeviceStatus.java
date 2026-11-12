
package com.example.statusapp.models;

import java.util.ArrayList;
import java.util.List;

public class DeviceStatus {
    public String deviceId;
    public int batteryLevel;          // da 0 a 100
    public String firmwareVersion;
    public List<String> errors = new ArrayList<>();
    public long timestamp;
}
