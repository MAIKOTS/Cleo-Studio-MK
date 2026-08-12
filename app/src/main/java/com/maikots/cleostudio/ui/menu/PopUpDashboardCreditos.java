package com.maikots.cleostudio.ui.menu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class PopUpDashboardCreditos extends ScrollView {

    public PopUpDashboardCreditos(Context context) {
        super(context);
        init(context);
    }

    public PopUpDashboardCreditos(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setBackgroundColor(Color.parseColor("#121212"));
        setFillViewport(true);

        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(32, 48, 32, 48);

        // 🟢 TÍTULO PRINCIPAL
        TextView txtTitulo = new TextView(context);
        txtTitulo.setText("CRÉDITOS");
        txtTitulo.setTextSize(22);
        txtTitulo.setTextColor(Color.parseColor("#FFB74D"));
        txtTitulo.setTypeface(Typeface.DEFAULT_BOLD);
        txtTitulo.setGravity(Gravity.CENTER);
        txtTitulo.setPadding(0, 0, 0, 32);
        container.addView(txtTitulo);

        // 👤 SEÇÃO 1: DESENVOLVEDOR (MAIKO.T.S)
        LinearLayout cardDev = criarCardBase(context);
        
        TextView lblDevCargo = criarTagCargo(context, "DESENVOLVEDOR PRINCIPAL", "#FFB74D");
        TextView txtDevNome = criarNomePessoa(context, "Maiko.T.S");
        TextView txtDevDesc = criarDescricao(context, "Criador e desenvolvedor do aplicativo Cleo Studio.");

        LinearLayout boxBotoesDev = new LinearLayout(context);
        boxBotoesDev.setOrientation(LinearLayout.VERTICAL);
        boxBotoesDev.setPadding(0, 16, 0, 0);

        View btnYtDev = criarBotaoLink(context, "▶ YouTube", "#FF0000", "https://youtube.com/@maikotss?si=0N9bXEwYBGTAjz61");
        View btnInstaDev = criarBotaoLink(context, "📸 Instagram", "#E1306C", "https://www.instagram.com/maiko.t.s.01?igsh=MzF3dXhqaHJuc3Zo");

        boxBotoesDev.addView(btnYtDev);
        boxBotoesDev.addView(btnInstaDev);

        cardDev.addView(lblDevCargo);
        cardDev.addView(txtDevNome);
        cardDev.addView(txtDevDesc);
        cardDev.addView(boxBotoesDev);

        container.addView(cardDev);

        // 🤝 SEÇÃO 2: PARCEIRO (PL CÉSAR)
        LinearLayout cardParceiro = criarCardBase(context);

        TextView lblParceiroCargo = criarTagCargo(context, "PARCEIRO OFICIAL", "#4FC3F7");
        TextView txtParceiroNome = criarNomePessoa(context, "PL CÉSAR");
        TextView txtParceiroDesc = criarDescricao(context, "Parceiro do projeto e criador de conteúdo.");

        LinearLayout boxBotoesParceiro = new LinearLayout(context);
        boxBotoesParceiro.setOrientation(LinearLayout.VERTICAL);
        boxBotoesParceiro.setPadding(0, 16, 0, 0);

        View btnYtParceiro = criarBotaoLink(context, "▶ YouTube", "#FF0000", "https://youtube.com/@embaixadorgtaoficial?si=IapqIjHylozFCQ7H");
        View btnInstaParceiro = criarBotaoLink(context, "📸 Instagram", "#E1306C", "https://www.instagram.com/pl.visao?igsh=MXN6ZHQyNDE5MnV0eQ==");

        boxBotoesParceiro.addView(btnYtParceiro);
        boxBotoesParceiro.addView(btnInstaParceiro);

        cardParceiro.addView(lblParceiroCargo);
        cardParceiro.addView(txtParceiroNome);
        cardParceiro.addView(txtParceiroDesc);
        cardParceiro.addView(boxBotoesParceiro);

        container.addView(cardParceiro);

        addView(container);
    }

    private LinearLayout criarCardBase(Context context) {
        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(32, 32, 32, 32);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#1E1E1E"));
        bg.setCornerRadius(24f);
        bg.setStroke(2, Color.parseColor("#2A2A2A"));
        card.setBackground(bg);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 24);
        card.setLayoutParams(params);

        return card;
    }

    private TextView criarTagCargo(Context context, String texto, String corHex) {
        TextView tag = new TextView(context);
        tag.setText(texto);
        tag.setTextSize(11);
        tag.setTextColor(Color.parseColor(corHex));
        tag.setTypeface(Typeface.DEFAULT_BOLD);
        tag.setLetterSpacing(0.05f);
        return tag;
    }

    private TextView criarNomePessoa(Context context, String nome) {
        TextView txt = new TextView(context);
        txt.setText(nome);
        txt.setTextSize(18);
        txt.setTextColor(Color.WHITE);
        txt.setTypeface(Typeface.DEFAULT_BOLD);
        txt.setPadding(0, 4, 0, 4);
        return txt;
    }

    private TextView criarDescricao(Context context, String desc) {
        TextView txt = new TextView(context);
        txt.setText(desc);
        txt.setTextSize(13);
        txt.setTextColor(Color.parseColor("#B0B0B0"));
        return txt;
    }

    private View criarBotaoLink(Context context, String texto, String corHex, String url) {
        TextView btn = new TextView(context);
        btn.setText(texto);
        btn.setTextSize(13);
        btn.setTextColor(Color.WHITE);
        btn.setTypeface(Typeface.DEFAULT_BOLD);
        btn.setGravity(Gravity.CENTER);
        btn.setPadding(24, 18, 24, 18);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor(corHex));
        bg.setCornerRadius(16f);
        btn.setBackground(bg);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 12, 0, 0);
        btn.setLayoutParams(params);

        btn.setOnClickListener(v -> abrirUrl(context, url));

        return btn;
    }

    private void abrirUrl(Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
