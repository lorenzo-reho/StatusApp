package com.example.statusapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.statusapp.R;
import com.example.statusapp.models.Intervention;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InterventionAdapter
        extends RecyclerView.Adapter<InterventionAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Intervention intervention);

    }

    private final List<Intervention> interventions = new ArrayList<>();
    private final OnItemClickListener listener;
    private final SimpleDateFormat format =
            new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ITALY);

    public InterventionAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Intervention> newItems) {
        interventions.clear();
        interventions.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_intervention, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        final Intervention i = interventions.get(position);

        h.title.setText(String.format("Intervento #%s", i.shortId()));
        h.date.setText(i.createdAt != null
                ? format.format(i.createdAt)
                : "In sincronizzazione…");

    }

    @Override
    public int getItemCount() {
        return interventions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView title, date;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleText);
            date = itemView.findViewById(R.id.dateText);
            // note = itemView.findViewById(R.id.noteText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(interventions.get(pos));
                    }
                }
            });
        }
    }
}
