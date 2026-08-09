package com.maikots.cleostudio;

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
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import org.json.JSONArray;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class CustomKeyboardView extends LinearLayout {

    private final CodeEditorView editorAlvo;
    private final LinearLayout containerTeclasMid;
    private final LinearLayout barraRodape;
    private final AudioManager audioManager;

    private boolean isShiftActive = false;
    private String mapaAtual = "LETRAS";

    // 4. Cache dos arquivos JSON para carregamento instantâneo
    private final Map<String, JSONArray> cacheLayouts = new HashMap<>();

    // 2. Variáveis para apagar continuamente no DEL
    private final Handler delHandler = new Handler(Looper.getMainLooper());
    private Runnable delRunnable;

    // 3. Variáveis para mover o cursor deslizando no ESPAÇO
    private float espacoXInicial = 0;
    private final float SENSITIVIDADE_CURSOR_PX = 25f; // Sensibilidade do deslize

    public CustomKeyboardView(Context context, CodeEditorView editorAlvo) {
        super(context);
        this.editorAlvo = editorAlvo;
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        setOrientation(LinearLayout.VERTICAL);
        setBackgroundColor(Color.parseColor("#121212"));
        setPadding(8, 8, 8, 8);

        setVisibility(View.GONE);

        // 1. Barra Superior (Fechar)
        adicionarBarraControle(context);

        // 2. Container Central das Teclas
        containerTeclasMid = new LinearLayout(context);
        containerTeclasMid.setOrientation(LinearLayout.VERTICAL);
        addView(containerTeclasMid);

        // 3. Scroll Horizontal do Rodapé
        HorizontalScrollView scrollRodape = new HorizontalScrollView(context);
        scrollRodape.setHorizontalScrollBarEnabled(false);
        scrollRodape.setPadding(0, 8, 0, 0);

        barraRodape = new LinearLayout(context);
        barraRodape.setOrientation(LinearLayout.HORIZONTAL);

        scrollRodape.addView(barraRodape);
        addView(scrollRodape);

        // Prepara os botões do rodapé
        construirBarraRodape(context);

        // ⚡ MELHORIA 4: Pré-carrega todos os arquivos JSON na memória ao iniciar!
        preCarregarLayoutsJSON(context);

        // Carrega o layout inicial diretamente do Cache
        carregarLayoutDoJson(context, "LETRAS");
    }

    // ⚡ MELHORIA 4: Pré-carregamento dos arquivos JSON na inicialização
    private void preCarregarLayoutsJSON(Context context) {
        String[] modos = {"LETRAS", "NUMEROS", "OPCODES", "SIMBOLOS", "ESTRUTURAS", "VARIAVEIS"};
        for (String modo : modos) {
            try {
                InputStream is = context.getAssets().open("TecladoNativo/" + modo + ".json");
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                is.close();

                String jsonText = new String(buffer, StandardCharsets.UTF_8);
                JSONArray jsonArray = new JSONArray(jsonText);
                cacheLayouts.put(modo, jsonArray);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
    }

    public void ocultarTeclado() {
        setVisibility(View.GONE);
    }

    // ⚡ MELHORIA 1: Toca som nativo de clique de tecla
    private void tocarSomClique() {
        if (audioManager != null) {
            audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 0.5f);
        }
    }

    public void carregarLayoutDoJson(Context context, String nomeArquivo) {
        this.mapaAtual = nomeArquivo;
        containerTeclasMid.removeAllViews();

        atualizarEstiloBarraRodape();

        try {
            JSONArray linhasArray = cacheLayouts.get(nomeArquivo);
            if (linhasArray == null) return;

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

        // ⚡ MELHORIA 2: DEL contínuo (apagar sem soltar)
        if (texto.equals("DEL")) {
            configurarAcaoDelContinuo(btn, context);
        } 
        // ⚡ MELHORIA 3: Barra de ESPAÇO como Cursor Deslizante
        else if (texto.equals("ESPAÇO")) {
            configurarEspacoComoCursor(btn, context);
        } 
        else {
            btn.setOnClickListener(v -> {
                tocarSomClique();
                processarAcaoTecla(context, texto);
            });
        }

        return btn;
    }

    // ⚡ MELHORIA 2: Lógica de Apagar Continuamente no DEL
    private void configurarAcaoDelContinuo(Button btn, Context context) {
        delRunnable = new Runnable() {
            @Override
            public void run() {
                tocarSomClique();
                executarBackspace();
                delHandler.postDelayed(this, 50); // Apaga 1 caractere a cada 50ms
            }
        };

        btn.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    tocarSomClique();
                    executarBackspace();
                    delHandler.postDelayed(delRunnable, 400); // Aguarda 400ms antes de iniciar repetição
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

    // ⚡ MELHORIA 3: Lógica de arrastar a barra de ESPAÇO para mover o cursor
    private void configurarEspacoComoCursor(Button btn, Context context) {
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
                            if (editorAlvo != null) {
                                int currentPos = editorAlvo.getSelectionStart();
                                if (deltaX > 0 && currentPos < editorAlvo.getText().length()) {
                                    editorAlvo.setSelection(currentPos + 1); // Move para a Direita
                                } else if (deltaX < 0 && currentPos > 0) {
                                    editorAlvo.setSelection(currentPos - 1); // Move para a Esquerda
                                }
                            }
                            espacoXInicial = event.getRawX(); // Reseta para continuar deslizando suavemente
                            moveuCursor = true;
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.setPressed(false);
                        // Se não deslizou, funciona como inserção normal de espaço ao dar um toque simples
                        if (!moveuCursor && event.getAction() == MotionEvent.ACTION_UP) {
                            tocarSomClique();
                            editorAlvo.inserirTexto(" ");
                        }
                        return true;
                }
                return false;
            }
        });
    }

    private void executarBackspace() {
        if (editorAlvo == null) return;
        int start = editorAlvo.getSelectionStart();
        int end = editorAlvo.getSelectionEnd();
        if (start != end) {
            editorAlvo.getText().delete(start, end);
        } else if (start > 0) {
            editorAlvo.getText().delete(start - 1, start);
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
        if (editorAlvo == null) return;

        switch (tecla) {
            case "SHIFT":
                isShiftActive = !isShiftActive;
                carregarLayoutDoJson(context, mapaAtual);
                break;

            case "TAB":
                editorAlvo.inserirTexto("    ");
                break;

            case "ENTER":
                editorAlvo.inserirTexto("\n");
                break;

            case "←":
                int posEsq = editorAlvo.getSelectionStart();
                if (posEsq > 0) editorAlvo.setSelection(posEsq - 1);
                break;

            case "→":
                int posDir = editorAlvo.getSelectionStart();
                if (posDir < editorAlvo.getText().length()) editorAlvo.setSelection(posDir + 1);
                break;

            default:
                editorAlvo.inserirTexto(tecla);
                if (isShiftActive && tecla.length() == 1) {
                    isShiftActive = false;
                    carregarLayoutDoJson(context, mapaAtual);
                }
                break;
        }
    }
}
