package com.maikots.cleostudio.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.maikots.cleostudio.R;

import java.util.List;

public class OpcodeAdapter extends RecyclerView.Adapter<OpcodeAdapter.OpcodeViewHolder> {

    public interface OnOpcodeClickListener {
        void onOpcodeClick(String opcodeCompleto);
    }

    public static class OpcodeItem {
        public String hex;
        public String nome;

        public OpcodeItem(String hex, String nome) {
            this.hex = hex;
            this.nome = nome;
        }
    }

    private final List<OpcodeItem> listaOpcodes;
    private final OnOpcodeClickListener listener;

    public OpcodeAdapter(List<OpcodeItem> listaOpcodes, OnOpcodeClickListener listener) {
        this.listaOpcodes = listaOpcodes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OpcodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_opcode, parent, false);
        return new OpcodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OpcodeViewHolder holder, int position) {
        OpcodeItem item = listaOpcodes.get(position);
        
        holder.txtHex.setText(item.hex);
        holder.txtNome.setText(item.nome);

        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(Color.parseColor("#181818"));
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#222222"));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOpcodeClick(item.hex + ": " + item.nome);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaOpcodes.size();
    }

    static class OpcodeViewHolder extends RecyclerView.ViewHolder {
        TextView txtHex;
        TextView txtNome;

        public OpcodeViewHolder(@NonNull View itemView) {
            super(itemView);
            txtHex = itemView.findViewById(R.id.txtHexOpcode);
            txtNome = itemView.findViewById(R.id.txtNomeComando);
        }
    }
}
