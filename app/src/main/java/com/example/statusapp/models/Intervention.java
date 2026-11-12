package com.example.statusapp.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Intervention {
    @DocumentId public String id;
    public String deviceId;
    public String technicianId;
    public String technicianEmail;
    public String note;
    public int battery;
    public String firmware;
    public List<String> errors = new ArrayList<>();
    @ServerTimestamp public Date createdAt;

    public Intervention() {}

    public String shortId(){
        if (id == null) return "…";
        return id.substring(0, Math.min(6, id.length())).toUpperCase(Locale.ROOT);
    }
}