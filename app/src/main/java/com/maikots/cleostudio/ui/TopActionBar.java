package com.maikots.cleostudio;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TopActionBar extends LinearLayout {

    private final Button btnBuscar;
    private final Button btnAbrir;
    private final Button btnSalvar;
    private final Button btnCompilar;
    private final TextView txtTitulo;

    public TopActionBar(Context context) {
        super(context);

        setOrientation(LinearLayout.HORIZONTAL);
        setBackgroundColor(Color.parseColor("#181818"));
        setPadding(16, 12, 16, 12);
        setGravity(Gravity.CENTER_VERTICAL);

        // Título / Nome do Arquivo
        txtTitulo = new TextView(context);
        txtTitulo.setText("CLEO Studio MK");
        txtTitulo.setTextSize(15);
        txtTitulo.setTextColor(Color.parseColor("#4CAF50"));
        txtTitulo.setTypeface(null, Typeface.BOLD);

        LayoutParams paramsTitulo = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
        txtTitulo.setLayoutParams(paramsTitulo);
        addView(txtTitulo);

        // Botões
        btnBuscar = criarBotao(context, "🔍", "#2D2D2D");
        btnAbrir = criarBotao(context, "ABRIR", "#2D2D2D");
        btnSalvar = criarBotao(context, "SALVAR", "#2D2D2D");
        btnCompilar = criarBotao(context, "COMPILAR", "#2196F3");

        // Ação para abrir/fechar a barra de busca ao clicar na lupa
        btnBuscar.setOnClickListener(v -> {
            if (getContext() instanceof MainActivity) {
                ((MainActivity) getContext()).alternarPainelBusca();
            }
        });

        addView(btnBuscar);
        addView(btnAbrir);
        addView(btnSalvar);
        addView(btnCompilar);
    }

    private Button criarBotao(Context context, String texto, String corHex) {
        Button btn = new Button(context);
        btn.setText(texto);
        btn.setTextSize(11);
        btn.setTextColor(Color.WHITE);
        btn.setBackgroundColor(Color.parseColor(corHex));
        btn.setTypeface(null, Typeface.BOLD);

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(4, 0, 4, 0);
        btn.setLayoutParams(params);
        return btn;
    }

    public Button getBtnBuscar() { return btnBuscar; }
    public Button getBtnAbrir() { return btnAbrir; }
    public Button getBtnSalvar() { return btnSalvar; }
    public Button getBtnCompilar() { return btnCompilar; }
    public void setTituloArquivo(String nome) { txtTitulo.setText(nome); }
}
