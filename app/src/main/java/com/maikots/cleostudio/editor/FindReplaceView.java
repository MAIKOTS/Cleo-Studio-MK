package com.maikots.cleostudio.editor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FindReplaceView extends LinearLayout {

    private final CodeEditorView editorAlvo;
    private final EditText inputBusca;
    private final EditText inputSubstituir;
    private final TextView textContador;

    private CustomKeyboardView customKeyboard;

    private final List<Integer> posicoesEncontradas = new ArrayList<>();
    private int indiceAtual = -1;

    public FindReplaceView(Context context, CodeEditorView editorAlvo) {
        super(context);
        this.editorAlvo = editorAlvo;

        setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        setOrientation(LinearLayout.VERTICAL);
        setBackgroundColor(Color.parseColor("#181818"));
        setPadding(16, 16, 16, 16);
        setVisibility(View.GONE);

        // Linha 1: Campo de Busca + Contador + Navegação + Fechar
        LinearLayout linha1 = new LinearLayout(context);
        linha1.setOrientation(LinearLayout.HORIZONTAL);
        linha1.setGravity(Gravity.CENTER_VERTICAL);

        inputBusca = criarCampoTexto(context, "Buscar no código...");
        Button btnAnterior = criarBotao(context, "▲");
        Button btnProximo = criarBotao(context, "▼");
        Button btnFechar = criarBotao(context, "✕");

        textContador = new TextView(context);
        textContador.setTextColor(Color.parseColor("#888888"));
        textContador.setTextSize(11);
        textContador.setPadding(8, 0, 8, 0);

        linha1.addView(inputBusca, new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f));
        linha1.addView(textContador);
        linha1.addView(btnAnterior);
        linha1.addView(btnProximo);
        linha1.addView(btnFechar);

        // Linha 2: Campo de Substituição + Botões
        LinearLayout linha2 = new LinearLayout(context);
        linha2.setOrientation(LinearLayout.HORIZONTAL);
        linha2.setGravity(Gravity.CENTER_VERTICAL);
        linha2.setPadding(0, 8, 0, 0);

        inputSubstituir = criarCampoTexto(context, "Substituir por...");
        Button btnSubstituir = criarBotao(context, "Trocar");
        Button btnSubstituirTudo = criarBotao(context, "Tudo");

        linha2.addView(inputSubstituir, new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f));
        linha2.addView(btnSubstituir);
        linha2.addView(btnSubstituirTudo);

        addView(linha1);
        addView(linha2);

        // Listeners
        inputBusca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                realizarBusca();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnProximo.setOnClickListener(v -> irParaProximo());
        btnAnterior.setOnClickListener(v -> irParaAnterior());
        btnSubstituir.setOnClickListener(v -> substituirAtual());
        btnSubstituirTudo.setOnClickListener(v -> substituirTudo());
        btnFechar.setOnClickListener(v -> fechar());
    }

    // Permite conectar o teclado customizado a este layout
    public void setCustomKeyboard(CustomKeyboardView keyboard) {
        this.customKeyboard = keyboard;
    }

    private EditText criarCampoTexto(Context context, String hint) {
        EditText edt = new EditText(context);
        edt.setHint(hint);
        edt.setHintTextColor(Color.parseColor("#555555"));
        edt.setTextColor(Color.WHITE);
        edt.setTextSize(12);
        edt.setTypeface(Typeface.MONOSPACE);
        edt.setBackgroundColor(Color.parseColor("#222222"));
        edt.setPadding(16, 12, 16, 12);

        // Bloqueia o teclado nativo do sistema
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            edt.setShowSoftInputOnFocus(false);
        }

        // Ao tocar no campo, exibe o teclado customizado e oculta o do sistema
        edt.setOnClickListener(v -> conectarTecladoCustomizado(edt));
        edt.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                conectarTecladoCustomizado(edt);
            }
        });

        return edt;
    }

    private void conectarTecladoCustomizado(EditText edt) {
        ocultarTecladoSistema(edt);
        if (customKeyboard != null) {
            customKeyboard.registerTarget(edt);
            customKeyboard.setVisibility(View.VISIBLE);
        }
    }

    private void ocultarTecladoSistema(View view) {
        try {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception ignored) {}
    }

    private Button criarBotao(Context context, String texto) {
        Button btn = new Button(context);
        btn.setText(texto);
        btn.setTextSize(11);
        btn.setTextColor(Color.WHITE);
        btn.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        btn.setBackgroundColor(Color.parseColor("#2A2A2A"));

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(4, 0, 4, 0);
        btn.setLayoutParams(params);
        return btn;
    }

    public void abrir() {
        setVisibility(View.VISIBLE);
        inputBusca.requestFocus();
        conectarTecladoCustomizado(inputBusca);
        realizarBusca();
    }

    public void fechar() {
        removerDestaquesAnteriores();
        setVisibility(View.GONE);
        posicoesEncontradas.clear();
        indiceAtual = -1;

        // Ao fechar a busca, reconecta o teclado customizado de volta ao editor principal
        if (customKeyboard != null && editorAlvo != null) {
            customKeyboard.registerTarget(editorAlvo);
        }
    }

    private void realizarBusca() {
        removerDestaquesAnteriores();
        posicoesEncontradas.clear();
        indiceAtual = -1;

        String termo = inputBusca.getText().toString();
        if (termo.isEmpty() || editorAlvo == null || editorAlvo.getText() == null) {
            textContador.setText("");
            return;
        }

        String textoCompleto = editorAlvo.getText().toString();
        int index = textoCompleto.indexOf(termo);

        while (index >= 0) {
            posicoesEncontradas.add(index);
            index = textoCompleto.indexOf(termo, index + termo.length());
        }

        if (!posicoesEncontradas.isEmpty()) {
            indiceAtual = 0;
            destacarOcorrenciaAtual();
        } else {
            textContador.setText("0/0");
        }
    }

    private void removerDestaquesAnteriores() {
        if (editorAlvo == null || editorAlvo.getText() == null) return;
        Editable editable = editorAlvo.getText();
        BackgroundColorSpan[] spans = editable.getSpans(0, editable.length(), BackgroundColorSpan.class);
        for (BackgroundColorSpan span : spans) {
            editable.removeSpan(span);
        }
    }

    private void destacarOcorrenciaAtual() {
        if (indiceAtual < 0 || indiceAtual >= posicoesEncontradas.size() || editorAlvo == null) return;

        removerDestaquesAnteriores();

        int pos = posicoesEncontradas.get(indiceAtual);
        int tamanhoTermo = inputBusca.getText().length();

        Editable editable = editorAlvo.getText();
        if (editable != null && pos + tamanhoTermo <= editable.length()) {
            editorAlvo.setSelection(pos);

            editable.setSpan(
                    new BackgroundColorSpan(Color.parseColor("#F57C00")),
                    pos, pos + tamanhoTermo,
                    Editable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        textContador.setText((indiceAtual + 1) + "/" + posicoesEncontradas.size());
    }

    private void irParaProximo() {
        if (posicoesEncontradas.isEmpty()) return;
        indiceAtual = (indiceAtual + 1) % posicoesEncontradas.size();
        destacarOcorrenciaAtual();
    }

    private void irParaAnterior() {
        if (posicoesEncontradas.isEmpty()) return;
        indiceAtual = (indiceAtual - 1 + posicoesEncontradas.size()) % posicoesEncontradas.size();
        destacarOcorrenciaAtual();
    }

    private void substituirAtual() {
        if (indiceAtual < 0 || posicoesEncontradas.isEmpty() || editorAlvo == null || editorAlvo.getText() == null) return;

        int pos = posicoesEncontradas.get(indiceAtual);
        String termoOriginal = inputBusca.getText().toString();
        String novoTermo = inputSubstituir.getText().toString();

        editorAlvo.getText().replace(pos, pos + termoOriginal.length(), novoTermo);
        realizarBusca();
    }

    private void substituirTudo() {
        String termoOriginal = inputBusca.getText().toString();
        String novoTermo = inputSubstituir.getText().toString();

        if (termoOriginal.isEmpty() || editorAlvo == null || editorAlvo.getText() == null) return;

        String conteudoAtualizado = editorAlvo.getText().toString().replace(termoOriginal, novoTermo);
        editorAlvo.getText().replace(0, editorAlvo.getText().length(), conteudoAtualizado);
        realizarBusca();
    }
}
