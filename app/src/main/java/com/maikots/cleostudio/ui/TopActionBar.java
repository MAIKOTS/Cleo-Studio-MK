package com.maikots.cleostudio.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.maikots.cleostudio.ui.menu.PopUpTopActionBarMenu;

public class TopActionBar extends LinearLayout {

    private final Button btnOpcoes;
    private final Button btnBuscar;
    private final Button btnConsole;
    private final Button btnAbrir;
    private final Button btnSalvar;
    private final Button btnCompilar;
    private final TextView txtTitulo;

    public interface OnOptionSelectedListener {
        void onOptionSelected(String opcao);
    }

    private OnOptionSelectedListener optionListener;

    public TopActionBar(Context context) {
        super(context);

        setOrientation(LinearLayout.HORIZONTAL);
        setBackgroundColor(Color.parseColor("#181818"));
        setPadding(8, 10, 8, 10);
        setGravity(Gravity.CENTER_VERTICAL);

        // ⚙️ Botão de Opções/Menu (Canto Superior Esquerdo)
        btnOpcoes = criarBotao(context, "⚙️", "#2D2D2D");
        addView(btnOpcoes);

        // 1. Título / Nome do Arquivo
        txtTitulo = new TextView(context);
        txtTitulo.setText("CLEO Studio MK");
        txtTitulo.setTextSize(13);
        txtTitulo.setTextColor(Color.parseColor("#4CAF50"));
        txtTitulo.setTypeface(null, Typeface.BOLD);
        txtTitulo.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        txtTitulo.setSingleLine(true);
        txtTitulo.setEllipsize(TextUtils.TruncateAt.END);

        LayoutParams paramsTitulo = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
        paramsTitulo.setMargins(8, 0, 8, 0);
        txtTitulo.setLayoutParams(paramsTitulo);
        addView(txtTitulo);

        // 2. Botões de Ação
        btnBuscar = criarBotao(context, "🔍", "#2D2D2D");
        btnConsole = criarBotao(context, "📟", "#2D2D2D");
        btnAbrir = criarBotao(context, "ABRIR", "#2D2D2D");
        btnSalvar = criarBotao(context, "SALVAR", "#2D2D2D");
        btnCompilar = criarBotao(context, "COMPILAR", "#2196F3");

        // Adiciona os botões à direita
        addView(btnBuscar);
        addView(btnConsole);
        addView(btnAbrir);
        addView(btnSalvar);
        addView(btnCompilar);

        // ⚡ Exibe o Menu via PopUpTopActionBarMenu repassando as notificações
        btnOpcoes.setOnClickListener(v -> PopUpTopActionBarMenu.exibir(context, v, opcao -> {
            notificarOpcao(opcao);
        }));
    }

    private void notificarOpcao(String opcao) {
        if (optionListener != null) {
            optionListener.onOptionSelected(opcao);
        } else {
            Toast.makeText(getContext(), "Opção: " + opcao, Toast.LENGTH_SHORT).show();
        }
    }

    private Button criarBotao(Context context, String texto, String corHex) {
        Button btn = new Button(context);
        btn.setText(texto);
        btn.setTextSize(10);
        btn.setTextColor(Color.WHITE);
        btn.setBackgroundColor(Color.parseColor(corHex));
        btn.setTypeface(null, Typeface.BOLD);
        btn.setPadding(10, 0, 10, 0);

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(2, 0, 2, 0);
        btn.setLayoutParams(params);
        return btn;
    }

    // Listener para escutar as ações do menu externas
    public void setOnOptionSelectedListener(OnOptionSelectedListener listener) {
        this.optionListener = listener;
    }

    public Button getBtnOpcoes() { return btnOpcoes; }
    public Button getBtnBuscar() { return btnBuscar; }
    public Button getBtnConsole() { return btnConsole; }
    public Button getBtnAbrir() { return btnAbrir; }
    public Button getBtnSalvar() { return btnSalvar; }
    public Button getBtnCompilar() { return btnCompilar; }
    
    public void setTituloArquivo(String nome) { txtTitulo.setText(nome); }
    public String getTituloArquivo() { return txtTitulo.getText().toString(); }
}
