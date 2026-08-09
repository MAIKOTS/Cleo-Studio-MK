package com.maikots.cleostudio;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class CodeEditorView extends LinearLayout {

    private final EditTextCustomizado campoTexto;
    private final LineNumberTextView indicadorLinhas;
    private final ScrollView scrollView;
    private CustomKeyboardView tecladoNativo;

    private class EditTextCustomizado extends EditText {
        public EditTextCustomizado(Context context) {
            super(context);
        }

        @Override
        protected void onSelectionChanged(int selStart, int selEnd) {
            super.onSelectionChanged(selStart, selEnd);
            if (indicadorLinhas != null) {
                indicadorLinhas.invalidate();
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            boolean r = super.onTouchEvent(event);
            if (event.getAction() == MotionEvent.ACTION_UP && tecladoNativo != null) {
                tecladoNativo.exibirTeclado();
            }
            return r;
        }
    }

    public CodeEditorView(Context context) {
        super(context);
        setOrientation(LinearLayout.HORIZONTAL);
        setBackgroundColor(Color.parseColor("#1E1E1E"));

        scrollView = new ScrollView(context);
        scrollView.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        scrollView.setFillViewport(true);

        LinearLayout containerInterno = new LinearLayout(context);
        containerInterno.setOrientation(LinearLayout.HORIZONTAL);

        // 1. Painel de Números das Linhas (Largura Inicial Enxuta)
        indicadorLinhas = new LineNumberTextView(context);
        LayoutParams paramsLinhas = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        indicadorLinhas.setLayoutParams(paramsLinhas);

        // 2. Campo de Texto Principal
        campoTexto = new EditTextCustomizado(context);

        configurarCampoTexto();
        indicadorLinhas.setEditText(campoTexto);

        LayoutParams paramsTexto = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
        campoTexto.setLayoutParams(paramsTexto);

        containerInterno.addView(indicadorLinhas);
        containerInterno.addView(campoTexto);

        scrollView.addView(containerInterno);
        addView(scrollView);

        ouvirMudancasDeTexto();
    }

    private void configurarCampoTexto() {
        campoTexto.setHint("{$CLEO .csi}\n\n:MAIN\n0001: wait 0 ms\n004E: end_thread");
        campoTexto.setHintTextColor(Color.parseColor("#555555"));
        campoTexto.setTextColor(Color.WHITE);
        campoTexto.setBackgroundColor(Color.TRANSPARENT);
        campoTexto.setGravity(Gravity.TOP | Gravity.START);
        campoTexto.setInputType(InputType.TYPE_CLASS_TEXT 
                               | InputType.TYPE_TEXT_FLAG_MULTI_LINE 
                               | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        campoTexto.setTypeface(Typeface.MONOSPACE);
        campoTexto.setTextSize(14);
        
        // Padding esquerdo reduzido para 8px para colar o código bem perto da linha divisória
        int paddingTopBottom = 20;
        campoTexto.setPadding(8, paddingTopBottom, 16, paddingTopBottom);
        campoTexto.setShowSoftInputOnFocus(false);
    }

    private void ouvirMudancasDeTexto() {
        campoTexto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                indicadorLinhas.invalidate();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    public void desativarTecladoNativoSystema() {
        if (campoTexto != null) {
            campoTexto.setShowSoftInputOnFocus(false);
        }
    }

    public void setTecladoNativo(CustomKeyboardView teclado) {
        this.tecladoNativo = teclado;
    }

    public void inserirTexto(String texto) {
        int start = Math.max(campoTexto.getSelectionStart(), 0);
        int end = Math.max(campoTexto.getSelectionEnd(), 0);
        campoTexto.getText().replace(Math.min(start, end), Math.max(start, end), texto, 0, texto.length());
    }

    public String getCodigo() {
        return campoTexto.getText().toString().trim();
    }

    public int getSelectionStart() {
        return campoTexto.getSelectionStart();
    }

    public int getSelectionEnd() {
        return campoTexto.getSelectionEnd();
    }

    public Editable getText() {
        return campoTexto.getText();
    }

    public void setSelection(int index) {
        campoTexto.setSelection(index);
    }
}
