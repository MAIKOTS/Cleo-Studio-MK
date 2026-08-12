package com.maikots.cleostudio.editor;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import org.json.JSONArray;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class CustomKeyboardView extends LinearLayout {

    private final CodeEditorView editorAlvo;
    
    // Armazenamos a referência genérica como Object para aceitar tanto CodeEditorView quanto EditText
    private Object targetAtual; 

    private final LinearLayout containerTeclasMid;
    private final LinearLayout barraRodape;
    private final AudioManager audioManager;

    private boolean isShiftActive = false;
    private String mapaAtual = "LETRAS";

    private final Map<String, JSONArray> cacheLayouts = new HashMap<>();

    private final Handler delHandler = new Handler(Looper.getMainLooper());
    private Runnable delRunnable;

    private float espacoXInicial = 0;
    private final float SENSITIVIDADE_CURSOR_PX = 25f;

    public CustomKeyboardView(Context context, CodeEditorView editorAlvo) {
        super(context);
        this.editorAlvo = editorAlvo;
        this.targetAtual = editorAlvo; // Padrão
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        setOrientation(LinearLayout.VERTICAL);
        setBackgroundColor(Color.parseColor("#121212"));
        setPadding(8, 8, 8, 8);

        setVisibility(View.GONE);

        adicionarBarraControle(context);

        containerTeclasMid = new LinearLayout(context);
        containerTeclasMid.setOrientation(LinearLayout.VERTICAL);
        addView(containerTeclasMid);

        HorizontalScrollView scrollRodape = new HorizontalScrollView(context);
        scrollRodape.setHorizontalScrollBarEnabled(false);
        scrollRodape.setPadding(0, 8, 0, 0);

        barraRodape = new LinearLayout(context);
        barraRodape.setOrientation(LinearLayout.HORIZONTAL);

        scrollRodape.addView(barraRodape);
        addView(scrollRodape);

        construirBarraRodape(context);
        preCarregarLayoutsJSON(context);
        carregarLayoutDoJson(context, "LETRAS");
    }

    /**
     * Registra qualquer alvo que vá receber a digitação (EditText ou CodeEditorView)
     */
    public void registerTarget(Object target) {
        if (target != null) {
            this.targetAtual = target;
        } else {
            this.targetAtual = editorAlvo;
        }
    }

    private void preCarregarLayoutsJSON(Context context) {
        String[] modos = {"LETRAS", "NUMEROS", "OPCODES", "SIMBOLOS", "ESTRUTURAS", "VARIAVEIS"};
        for (String modo : modos) {
            InputStream is = null;
            try {
                String modoMin = modo.toLowerCase();
                try {
                    is = context.getAssets().open("editor/keyboard_" + modoMin + ".json");
                } catch (Exception e1) {
                    try {
                        is = context.getAssets().open("editor/" + modoMin + ".json");
                    } catch (Exception e2) {
                        is = context.getAssets().open("TecladoNativo/" + modo + ".json");
                    }
                }

                if (is != null) {
                    byte[] buffer = new byte[is.available()];
                    is.read(buffer);
                    is.close();

                    String jsonText = new String(buffer, StandardCharsets.UTF_8);
                    JSONArray jsonArray = new JSONArray(jsonText);
                    cacheLayouts.put(modo, jsonArray);
                }
            } catch (Exception ignored) {}
        }
    }

    private void adicionarBarraControle(Context context) {
        LinearLayout barraTopo = new LinearLayout(context);
        barraTopo.setOrientation(LinearLayout.HORIZONTAL);
        barraTopo.setGravity(Gravity.END);
        barraTopo.setPadding(0, 0, 8, 8);

        Button btnFechar = new Button(context);
        btnFechar.setText("▼ Fechar");
        btnFechar.setTextSize(11);
        btnFechar.setTextColor(Color.parseColor("#AAAAAA"));
        btnFechar.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);

        aplicarEfeitoEArredondamento(btnFechar, Color.parseColor("#1E1E1E"), Color.parseColor("#333333"));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        btnFechar.setLayoutParams(params);

        btnFechar.setOnClickListener(v -> {
            tocarSomClique();
            ocultarTeclado();
        });
        barraTopo.addView(btnFechar);
        addView(barraTopo);
    }

    private void construirBarraRodape(Context context) {
        String[] modos = {"LETRAS", "NUMEROS", "OPCODES", "SIMBOLOS", "ESTRUTURAS", "VARIAVEIS"};

        for (String modo : modos) {
            Button btnAba = new Button(context);
            btnAba.setText(modo);
            btnAba.setTextSize(11);
            btnAba.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
            btnAba.setPadding(32, 16, 32, 16);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.setMargins(6, 0, 6, 0);
            btnAba.setLayoutParams(params);

            btnAba.setOnClickListener(v -> {
                tocarSomClique();
                carregarLayoutDoJson(context, modo);
            });
            barraRodape.addView(btnAba);
        }
    }

    private void atualizarEstiloBarraRodape() {
        for (int i = 0; i < barraRodape.getChildCount(); i++) {
            View view = barraRodape.getChildAt(i);
            if (view instanceof Button) {
                Button btn = (Button) view;
                String modoBtn = btn.getText().toString();

                if (modoBtn.equalsIgnoreCase(mapaAtual)) {
                    btn.setTextColor(Color.WHITE);
                    aplicarEfeitoEArredondamento(btn, Color.parseColor("#1976D2"), Color.parseColor("#2196F3"));
                } else {
                    btn.setTextColor(Color.parseColor("#888888"));
                    aplicarEfeitoEArredondamento(btn, Color.parseColor("#222222"), Color.parseColor("#444444"));
                }
            }
        }
    }

    public void exibirTeclado() {
        setVisibility(View.VISIBLE);
        if (editorAlvo != null) {
            editorAlvo.permitirTecladoSistema(false);
        }
    }

    public void ocultarTeclado() {
        setVisibility(View.GONE);
    }

    private void tocarSomClique() {
        if (audioManager != null) {
            try {
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 0.5f);
            } catch (Exception ignored) {}
        }
    }

    public void carregarLayoutDoJson(Context context, String nomeArquivo) {
        this.mapaAtual = nomeArquivo;
        containerTeclasMid.removeAllViews();

        atualizarEstiloBarraRodape();

        try {
            JSONArray linhasArray = cacheLayouts.get(nomeArquivo);
            if (linhasArray == null) {
                if (editorAlvo != null) {
                    editorAlvo.permitirTecladoSistema(true);
                }
                return;
            }

            for (int i = 0; i < linhasArray.length(); i++) {
                JSONArray teclasLinha = linhasArray.getJSONArray(i);

                LinearLayout linhaLayout = new LinearLayout(context);
                linhaLayout.setOrientation(LinearLayout.HORIZONTAL);
                linhaLayout.setGravity(Gravity.CENTER);

                LayoutParams paramsLinha = new LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                paramsLinha.setMargins(0, 4, 0, 4);
                linhaLayout.setLayoutParams(paramsLinha);

                for (int j = 0; j < teclasLinha.length(); j++) {
                    String textoTecla = teclasLinha.getString(j);

                    if (isShiftActive && textoTecla.length() == 1 && Character.isLetter(textoTecla.charAt(0))) {
                        textoTecla = textoTecla.toUpperCase();
                    }

                    Button btn = criarBotaoTecla(context, textoTecla);
                    linhaLayout.addView(btn);
                }

                containerTeclasMid.addView(linhaLayout);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Button criarBotaoTecla(final Context context, final String texto) {
        Button btn = new Button(context);
        btn.setText(texto);
        btn.setTextSize(12);
        btn.setTextColor(Color.WHITE);
        btn.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);

        int corNormal;
        int corPressionado;

        if (texto.equals("DEL") || texto.equals("ENTER")) {
            corNormal = Color.parseColor("#B71C1C");
            corPressionado = Color.parseColor("#E53935");
        } else if (texto.equals("SHIFT")) {
            corNormal = Color.parseColor("#333333");
            corPressionado = Color.parseColor("#555555");
        } else if (texto.equals("ESPAÇO") || texto.equals("TAB")) {
            corNormal = Color.parseColor("#1976D2");
            corPressionado = Color.parseColor("#2196F3");
        } else {
            corNormal = Color.parseColor("#212121");
            corPressionado = Color.parseColor("#424242");
        }

        aplicarEfeitoEArredondamento(btn, corNormal, corPressionado);

        float weight = texto.equals("ESPAÇO") ? 3.0f : 1.0f;
        LayoutParams params = new LayoutParams(0, LayoutParams.WRAP_CONTENT, weight);
        params.setMargins(4, 0, 4, 0);
        btn.setLayoutParams(params);

        if (texto.equals("DEL")) {
            configurarAcaoDelContinuo(btn);
        } else if (texto.equals("ESPAÇO")) {
            configurarEspacoComoCursor(btn);
        } else {
            btn.setOnClickListener(v -> {
                tocarSomClique();
                processarAcaoTecla(context, texto);
            });
        }

        return btn;
    }

    private void configurarAcaoDelContinuo(Button btn) {
        delRunnable = new Runnable() {
            @Override
            public void run() {
                tocarSomClique();
                executarBackspace();
                delHandler.postDelayed(this, 50);
            }
        };

        btn.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    tocarSomClique();
                    executarBackspace();
                    delHandler.postDelayed(delRunnable, 400);
                    v.setPressed(true);
                    return true;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    delHandler.removeCallbacks(delRunnable);
                    v.setPressed(false);
                    return true;
            }
            return false;
        });
    }

    private void configurarEspacoComoCursor(Button btn) {
        btn.setOnTouchListener(new OnTouchListener() {
            private boolean moveuCursor = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        espacoXInicial = event.getRawX();
                        moveuCursor = false;
                        v.setPressed(true);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float deltaX = event.getRawX() - espacoXInicial;

                        if (Math.abs(deltaX) > SENSITIVIDADE_CURSOR_PX) {
                            if (targetAtual instanceof EditText) {
                                EditText edt = (EditText) targetAtual;
                                int currentPos = edt.getSelectionStart();
                                if (deltaX > 0 && edt.getText() != null && currentPos < edt.getText().length()) {
                                    edt.setSelection(currentPos + 1);
                                } else if (deltaX < 0 && currentPos > 0) {
                                    edt.setSelection(currentPos - 1);
                                }
                            } else if (targetAtual instanceof CodeEditorView) {
                                CodeEditorView ed = (CodeEditorView) targetAtual;
                                int currentPos = ed.getSelectionStart();
                                if (deltaX > 0 && ed.getText() != null && currentPos < ed.getText().length()) {
                                    ed.setSelection(currentPos + 1);
                                } else if (deltaX < 0 && currentPos > 0) {
                                    ed.setSelection(currentPos - 1);
                                }
                            }
                            espacoXInicial = event.getRawX();
                            moveuCursor = true;
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.setPressed(false);
                        if (!moveuCursor && event.getAction() == MotionEvent.ACTION_UP) {
                            tocarSomClique();
                            inserirTextoNoAlvo(" ");
                        }
                        return true;
                }
                return false;
            }
        });
    }

    private void executarBackspace() {
        if (targetAtual == null) return;

        if (targetAtual instanceof EditText) {
            EditText edt = (EditText) targetAtual;
            if (edt.getText() == null) return;
            Editable editable = edt.getText();
            int start = edt.getSelectionStart();
            int end = edt.getSelectionEnd();
            if (start != end) {
                editable.delete(start, end);
            } else if (start > 0) {
                editable.delete(start - 1, start);
            }
        } else if (targetAtual instanceof CodeEditorView) {
            CodeEditorView ed = (CodeEditorView) targetAtual;
            if (ed.getText() == null) return;
            Editable editable = ed.getText();
            int start = ed.getSelectionStart();
            int end = ed.getSelectionEnd();
            if (start != end) {
                editable.delete(start, end);
            } else if (start > 0) {
                editable.delete(start - 1, start);
            }
        }
    }

    private void inserirTextoNoAlvo(String texto) {
        if (targetAtual == null) return;

        if (targetAtual instanceof CodeEditorView) {
            ((CodeEditorView) targetAtual).inserirTexto(texto);
        } else if (targetAtual instanceof EditText) {
            EditText edt = (EditText) targetAtual;
            if (edt.getText() == null) return;
            int start = Math.max(edt.getSelectionStart(), 0);
            int end = Math.max(edt.getSelectionEnd(), 0);
            int selStart = Math.min(start, end);
            int selEnd = Math.max(start, end);

            edt.getText().replace(selStart, selEnd, texto, 0, texto.length());
        }
    }

    private void aplicarEfeitoEArredondamento(Button btn, int corNormal, int corPressionado) {
        int raioBordaPx = 16;

        GradientDrawable formatoNormal = new GradientDrawable();
        formatoNormal.setColor(corNormal);
        formatoNormal.setCornerRadius(raioBordaPx);

        GradientDrawable formatoPressionado = new GradientDrawable();
        formatoPressionado.setColor(corPressionado);
        formatoPressionado.setCornerRadius(raioBordaPx);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RippleDrawable ripple = new RippleDrawable(
                    ColorStateList.valueOf(corPressionado),
                    formatoNormal,
                    formatoPressionado
            );
            btn.setBackground(ripple);
        } else {
            StateListDrawable estados = new StateListDrawable();
            estados.addState(new int[]{android.R.attr.state_pressed}, formatoPressionado);
            estados.addState(new int[]{}, formatoNormal);
            btn.setBackground(estados);
        }
    }

    private void processarAcaoTecla(Context context, String tecla) {
        if (targetAtual == null) return;

        switch (tecla) {
            case "SHIFT":
                isShiftActive = !isShiftActive;
                carregarLayoutDoJson(context, mapaAtual);
                break;

            case "TAB":
                inserirTextoNoAlvo("    ");
                break;

            case "ENTER":
                inserirTextoNoAlvo("\n");
                break;

            case "←":
                moverCursor(-1);
                break;

            case "→":
                moverCursor(1);
                break;

            default:
                inserirTextoNoAlvo(tecla);
                if (isShiftActive && tecla.length() == 1) {
                    isShiftActive = false;
                    carregarLayoutDoJson(context, mapaAtual);
                }
                break;
        }
    }

    private void moverCursor(int direcao) {
        if (targetAtual instanceof EditText) {
            EditText edt = (EditText) targetAtual;
            int pos = edt.getSelectionStart() + direcao;
            if (edt.getText() != null && pos >= 0 && pos <= edt.getText().length()) {
                edt.setSelection(pos);
            }
        } else if (targetAtual instanceof CodeEditorView) {
            CodeEditorView ed = (CodeEditorView) targetAtual;
            int pos = ed.getSelectionStart() + direcao;
            if (ed.getText() != null && pos >= 0 && pos <= ed.getText().length()) {
                ed.setSelection(pos);
            }
        }
    }
}
