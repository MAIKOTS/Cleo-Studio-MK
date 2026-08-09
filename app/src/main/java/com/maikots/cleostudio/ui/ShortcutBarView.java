package com.maikots.cleostudio;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import org.json.JSONArray;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ShortcutBarView extends LinearLayout {

    private final LinearLayout layoutAbas;
    private final LinearLayout containerBotoes;
    private final CodeEditorView editorAlvo;

    // Lista de arquivos JSON na pasta assets/ShortcutBar/
    private final String[] categorias = {"NAV", "SIMBOLOS", "OPCODES", "VARS"};

    public ShortcutBarView(Context context, CodeEditorView editorAlvo) {
        super(context);
        this.editorAlvo = editorAlvo;

        setOrientation(LinearLayout.VERTICAL);
        setBackgroundColor(Color.parseColor("#181818"));

        // 1. Barra de Abas (Categorias)
        HorizontalScrollView scrollAbas = new HorizontalScrollView(context);
        scrollAbas.setHorizontalScrollBarEnabled(false);
        
        layoutAbas = new LinearLayout(context);
        layoutAbas.setOrientation(LinearLayout.HORIZONTAL);
        layoutAbas.setPadding(4, 4, 4, 0);
        scrollAbas.addView(layoutAbas);
        addView(scrollAbas);

        // 2. Barra Scrollable de Botões da Categoria Selecionada
        HorizontalScrollView scrollBotoes = new HorizontalScrollView(context);
        scrollBotoes.setHorizontalScrollBarEnabled(false);

        containerBotoes = new LinearLayout(context);
        containerBotoes.setOrientation(LinearLayout.HORIZONTAL);
        containerBotoes.setPadding(8, 8, 8, 8);
        scrollBotoes.addView(containerBotoes);
        addView(scrollBotoes);

        // Inicializa as abas e carrega a primeira por padrão
        construirAbas(context);
        carregarCategoria(context, "NAV");
    }

    private void construirAbas(final Context context) {
        for (final String cat : categorias) {
            Button btnAba = new Button(context);
            btnAba.setText(cat);
            btnAba.setTextSize(10);
            btnAba.setTextColor(Color.LTGRAY);
            btnAba.setBackgroundColor(Color.parseColor("#222222"));
            btnAba.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(4, 0, 4, 0);
            btnAba.setLayoutParams(params);

            btnAba.setOnClickListener(v -> carregarCategoria(context, cat));
            layoutAbas.addView(btnAba);
        }
    }

    private void carregarCategoria(Context context, String nomeCategoria) {
        containerBotoes.removeAllViews();

        try {
            InputStream is = context.getAssets().open("ShortcutBar/" + nomeCategoria + ".json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            String jsonText = new String(buffer, StandardCharsets.UTF_8);
            JSONArray array = new JSONArray(jsonText);

            for (int i = 0; i < array.length(); i++) {
                final String item = array.getString(i);
                criarBotaoAtalho(context, item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void criarBotaoAtalho(Context context, final String texto) {
        Button btn = new Button(context);
        btn.setText(texto);
        btn.setTextSize(12);
        btn.setTextColor(Color.WHITE);
        btn.setBackgroundColor(Color.parseColor("#2D2D2D"));
        btn.setTypeface(Typeface.MONOSPACE);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(6, 0, 6, 0);
        btn.setLayoutParams(params);

        btn.setOnClickListener(v -> processarAcaoAtalho(texto));
        containerBotoes.addView(btn);
    }

    // Processa tanto textos comuns quanto ações de navegação (NAV)
    private void processarAcaoAtalho(String acao) {
        if (editorAlvo == null) return;

        switch (acao) {
            case "←":
                int posEsq = editorAlvo.getSelectionStart();
                if (posEsq > 0) editorAlvo.setSelection(posEsq - 1);
                break;

            case "→":
                int posDir = editorAlvo.getSelectionStart();
                if (posDir < editorAlvo.getText().length()) editorAlvo.setSelection(posDir + 1);
                break;

            case "[TAB]":
                editorAlvo.inserirTexto("    ");
                break;

            case "[ENTER]":
                editorAlvo.inserirTexto("\n");
                break;

            default:
                editorAlvo.inserirTexto(acao);
                break;
        }
    }
}
