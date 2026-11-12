package com.example.statusapp.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.statusapp.R;
import com.example.statusapp.models.BleDevice;

import java.util.ArrayList;
import java.util.List;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {

    private List<BleDevice> devices = new ArrayList<>();

    public interface OnDeviceClickListener{
        void onDeviceClick(BleDevice device);
    }

    private OnDeviceClickListener listener;

    public DeviceListAdapter(OnDeviceClickListener listener){
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clear() {
        devices.clear();
        notifyDataSetChanged();
    }

    public void add(BleDevice device) {
        // evito la creazione di doppioni
        for (BleDevice d : devices) {
            if (d.id.equals(device.id)) return;
        }
        devices.add(device);
        notifyItemInserted(devices.size() - 1);
    }

    @NonNull
    @Override
    public DeviceListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_device, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceListAdapter.ViewHolder holder, int position) {
        BleDevice d = devices.get(position);
        holder.name.setText(d.name);
        holder.details.setText(String.format("%s | %d dBm", d.id, d.rssi));
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        final TextView name;
        final TextView details;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.deviceName);
            details = itemView.findViewById(R.id.deviceDetails);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION && listener != null) {
                        listener.onDeviceClick(devices.get(pos));
                    }
                }
            });

        }
    }
}
