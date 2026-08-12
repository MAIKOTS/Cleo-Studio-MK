package com.maikots.cleostudio.console;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ConsoleLogView extends LinearLayout {

    private final TextView logText;
    private Runnable onFecharClickListener;

    public ConsoleLogView(Context context) {
        super(context);
        setOrientation(LinearLayout.VERTICAL);
        setBackgroundColor(Color.parseColor("#121212"));

        // 1. Barra de Cabeçalho do Console
        LinearLayout header = new LinearLayout(context);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setBackgroundColor(Color.parseColor("#1E1E1E"));
        header.setPadding(16, 12, 16, 12);
        header.setGravity(Gravity.CENTER_VERTICAL);

        TextView titulo = new TextView(context);
        titulo.setText("📟 Console Output");
        titulo.setTextColor(Color.parseColor("#AAAAAA"));
        titulo.setTextSize(12);
        titulo.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);

        LayoutParams paramsTitulo = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
        titulo.setLayoutParams(paramsTitulo);

        Button btnLimpar = criarBotaoHeader(context, "🗑");
        Button btnFechar = criarBotaoHeader(context, "✕");

        btnLimpar.setOnClickListener(v -> limpar());
        btnFechar.setOnClickListener(v -> {
            if (onFecharClickListener != null) {
                onFecharClickListener.run();
            }
        });

        header.addView(titulo);
        header.addView(btnLimpar);
        header.addView(btnFechar);
        addView(header);

        // Divisória sutil
        LinearLayout divisor = new LinearLayout(context);
        divisor.setBackgroundColor(Color.parseColor("#2A2A2A"));
        addView(divisor, new LayoutParams(LayoutParams.MATCH_PARENT, 2));

        // 2. Área de Texto com Rolagem
        ScrollView scrollView = new ScrollView(context);
        scrollView.setPadding(16, 16, 16, 16);

        logText = new TextView(context);
        logText.setTextColor(Color.parseColor("#CCCCCC"));
        logText.setTextSize(11);
        logText.setTypeface(Typeface.MONOSPACE);

        scrollView.addView(logText);
        addView(scrollView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    private Button criarBotaoHeader(Context context, String texto) {
        Button btn = new Button(context);
        btn.setText(texto);
        btn.setTextSize(12);
        btn.setTextColor(Color.WHITE);
        btn.setBackgroundColor(Color.TRANSPARENT);
        
        LayoutParams params = new LayoutParams(80, 80);
        params.setMargins(4, 0, 4, 0);
        btn.setLayoutParams(params);
        return btn;
    }

    public void setOnFecharClickListener(Runnable listener) {
        this.onFecharClickListener = listener;
    }

    public void logSucesso(String mensagem) {
        String logAntigo = logText.getText().toString();
        logText.setText((logAntigo.isEmpty() ? "" : logAntigo + "\n") + "[OK] " + mensagem);
        logText.setTextColor(Color.parseColor("#00E676"));
    }

    public void logErro(String mensagem) {
        String logAntigo = logText.getText().toString();
        logText.setText((logAntigo.isEmpty() ? "" : logAntigo + "\n") + "[ERRO] " + mensagem);
        logText.setTextColor(Color.parseColor("#FF5252"));
    }

    public void limpar() {
        logText.setText("");
    }
}
