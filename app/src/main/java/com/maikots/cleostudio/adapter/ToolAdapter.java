package com.maikots.cleostudio.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.maikots.cleostudio.R;
import com.maikots.cleostudio.model.ToolItem;

import java.util.List;

public class ToolAdapter extends RecyclerView.Adapter<ToolAdapter.ToolViewHolder> {

    public interface OnToolClickListener {
        void onToolClick(ToolItem tool);
    }

    private final List<ToolItem> ferramentasList;
    private final OnToolClickListener listener;

    public ToolAdapter(List<ToolItem> ferramentasList, OnToolClickListener listener) {
        this.ferramentasList = ferramentasList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ToolViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tool_card, parent, false);
        return new ToolViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToolViewHolder holder, int position) {
        ToolItem item = ferramentasList.get(position);

        holder.imgIcone.setImageResource(item.getIconeResId());
        holder.txtNome.setText(item.getNome());
        holder.txtDescricao.setText(item.getDescricao());
        holder.txtTag.setText(item.getTag());

        // ⚡ Dispara a ação após priorizar o desenho da animação Ripple na UI Thread
        holder.itemView.setOnClickListener(v -> {
            v.post(() -> {
                if (listener != null) {
                    listener.onToolClick(item);
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return ferramentasList.size();
    }

    static class ToolViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcone;
        TextView txtNome, txtDescricao, txtTag;

        public ToolViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcone = itemView.findViewById(R.id.imgIconeFerramenta);
            txtNome = itemView.findViewById(R.id.txtNomeFerramenta);
            txtDescricao = itemView.findViewById(R.id.txtDescricaoFerramenta);
            txtTag = itemView.findViewById(R.id.txtTagFerramenta);

            // ⚡ Otimização: Aplicado uma única vez na criação do ViewHolder
            configurarBackgrounds();
        }

        private void configurarBackgrounds() {
            // Fundo Padrão do Card
            GradientDrawable backgroundCard = new GradientDrawable();
            backgroundCard.setColor(Color.parseColor("#1A1A1A"));
            backgroundCard.setCornerRadius(24f);
            backgroundCard.setStroke(2, Color.parseColor("#2A2A2A"));

            // Efeito Ripple com cor translúcida
            int colorRipple = Color.parseColor("#33FFFFFF");

            RippleDrawable rippleDrawable = new RippleDrawable(
                    ColorStateList.valueOf(colorRipple),
                    backgroundCard,
                    backgroundCard
            );

            itemView.setBackground(rippleDrawable);

            // Arredonda a caixa do ícone
            GradientDrawable iconBg = new GradientDrawable();
            iconBg.setColor(Color.parseColor("#262626"));
            iconBg.setCornerRadius(16f);
            imgIcone.setBackground(iconBg);
        }
    }
}
