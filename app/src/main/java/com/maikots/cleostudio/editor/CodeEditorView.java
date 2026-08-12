package com.maikots.cleostudio.editor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class CodeEditorView extends LinearLayout {

    private final EditTextCustomizado campoTexto;
    private final LineNumberTextView indicadorLinhas;
    private final ScrollView scrollView;
    private CustomKeyboardView tecladoNativo;

    private final Set<String> palavrasChave = new HashSet<>();

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
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (tecladoNativo != null) {
                    setShowSoftInputOnFocus(false);
                    ocultarTecladoSistema();
                    tecladoNativo.exibirTeclado();
                }
            }
            return r;
        }

        private void ocultarTecladoSistema() {
            try {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindowToken(), 0);
                }
            } catch (Exception ignored) {}
        }
    }

    public CodeEditorView(Context context) {
        super(context);
        setOrientation(LinearLayout.HORIZONTAL);

        indicadorLinhas = new LineNumberTextView(context);
        campoTexto = new EditTextCustomizado(context);

        indicadorLinhas.setEditText(campoTexto);

        campoTexto.setBackgroundColor(Color.TRANSPARENT);
        campoTexto.setTextColor(Color.parseColor("#F8F8F2"));
        campoTexto.setTextSize(14);
        campoTexto.setTypeface(Typeface.MONOSPACE);
        campoTexto.setGravity(Gravity.TOP | Gravity.START);
        campoTexto.setInputType(InputType.TYPE_CLASS_TEXT 
                | InputType.TYPE_TEXT_FLAG_MULTI_LINE 
                | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        campoTexto.setHorizontallyScrolling(true);

        permitirTecladoSistema(true);

        carregarTema(context);
        carregarKeywords(context);

        scrollView = new ScrollView(context);
        scrollView.setFillViewport(true);
        scrollView.addView(campoTexto, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, 
                LayoutParams.MATCH_PARENT));

        LayoutParams paramsLinhas = new LayoutParams(
                LayoutParams.WRAP_CONTENT, 
                LayoutParams.MATCH_PARENT);

        LayoutParams paramsScroll = new LayoutParams(
                0, 
                LayoutParams.MATCH_PARENT, 
                1.0f);

        addView(indicadorLinhas, paramsLinhas);
        addView(scrollView, paramsScroll);

        ouvirMudancasDeTexto();
    }

    // ⚡ MÉTODOS PÚBLICOS NECESSÁRIOS QUE ESTAVAM FALTANDO:
    public void permitirTecladoSistema(boolean permitir) {
        if (campoTexto != null) {
            campoTexto.setShowSoftInputOnFocus(permitir);
        }
    }

    public void setTecladoNativo(CustomKeyboardView teclado) {
        this.tecladoNativo = teclado;
        if (teclado != null) {
            permitirTecladoSistema(false);
        } else {
            permitirTecladoSistema(true);
        }
    }

    private void carregarTema(Context context) {
        try {
            InputStream is = context.getAssets().open("editor/theme_colors.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            JSONObject json = new JSONObject(new String(buffer, StandardCharsets.UTF_8));
            JSONArray temas = json.optJSONArray("temas");
            if (temas != null && temas.length() > 0) {
                JSONObject temaPadrao = temas.getJSONObject(0);
                String corFundo = temaPadrao.optString("background", "#121212");
                String corTexto = temaPadrao.optString("texto_padrao", "#F8F8F2");
                setBackgroundColor(Color.parseColor(corFundo));
                campoTexto.setTextColor(Color.parseColor(corTexto));
            }
        } catch (Exception e) {
            setBackgroundColor(Color.parseColor("#121212"));
        }
    }

    private void carregarKeywords(Context context) {
        try {
            InputStream is = context.getAssets().open("editor/keywords.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            JSONArray array = new JSONArray(new String(buffer, StandardCharsets.UTF_8));
            for (int i = 0; i < array.length(); i++) {
                palavrasChave.add(array.getString(i));
            }
        } catch (Exception e) {
            // Ignora se não houver arquivo
        }
    }

    private void ouvirMudancasDeTexto() {
        campoTexto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (indicadorLinhas != null) {
                    indicadorLinhas.invalidate();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
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

    public Set<String> getPalavrasChave() {
        return palavrasChave;
    }
}
