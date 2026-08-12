package com.maikots.cleostudio.editor;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.maikots.cleostudio.R;

public class MainEditorLayout extends LinearLayout {

    private final CodeEditorView editorTexto;
    private final TopBarHandler topBar;
    private final FindReplaceView painelBusca;
    private final CustomKeyboardView tecladoNativo;

    public MainEditorLayout(Context context) {
        super(context);
        setOrientation(VERTICAL);
        setBackgroundColor(Color.parseColor("#121212"));

        // 1. Instancia o editor de texto
        editorTexto = new CodeEditorView(context);

        // 2. Instancia o teclado customizado vinculando-o ao editor
        tecladoNativo = new CustomKeyboardView(context, editorTexto);

        // 3. Conecta o teclado ao CodeEditorView
        editorTexto.setTecladoNativo(tecladoNativo);

        // 4. Instancia o painel de busca
        painelBusca = new FindReplaceView(context, editorTexto);

        // 5. Infla a Barra Superior XML
        View topBarView = LayoutInflater.from(context).inflate(R.layout.layout_editor_topbar, this, false);
        topBar = new TopBarHandler(topBarView);

        // 6. Configura os eventos da TopBar
        if (topBar.getBtnConfig() != null) {
            topBar.getBtnConfig().setOnClickListener(v -> 
                Toast.makeText(context, "Configurações do Editor", Toast.LENGTH_SHORT).show());
        }

        if (topBar.getBtnBuscar() != null) {
            topBar.getBtnBuscar().setOnClickListener(v -> {
                if (painelBusca.getVisibility() == View.VISIBLE) {
                    painelBusca.setVisibility(View.GONE);
                } else {
                    painelBusca.setVisibility(View.VISIBLE);
                }
            });
        }

        if (topBar.getBtnAbrir() != null) {
            topBar.getBtnAbrir().setOnClickListener(v -> 
                Toast.makeText(context, "Abrir arquivo", Toast.LENGTH_SHORT).show());
        }

        if (topBar.getBtnSalvar() != null) {
            topBar.getBtnSalvar().setOnClickListener(v -> 
                Toast.makeText(context, "Salvar arquivo", Toast.LENGTH_SHORT).show());
        }

        // 7. Adiciona as Views na ordem vertical correta:
        // TopBar -> Painel de Busca -> Editor (ocupa o resto da tela) -> Teclado Customizado no Rodapé
        addView(topBarView);
        addView(painelBusca);
        addView(editorTexto, new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1.0f));
        addView(tecladoNativo, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    public CodeEditorView getEditorTexto() {
        return editorTexto;
    }

    public TopBarHandler getTopBar() {
        return topBar;
    }

    public FindReplaceView getPainelBusca() {
        return painelBusca;
    }

    public CustomKeyboardView getTecladoNativo() {
        return tecladoNativo;
    }

    public static class TopBarHandler {
        private final ImageButton btnConfig;
        private final ImageButton btnBuscar;
        private final Button btnAbrir;
        private final Button btnSalvar;
        private final Button btnCompilar;
        private final TextView txtTitulo;

        public TopBarHandler(View view) {
            btnConfig = view.findViewById(R.id.btnConfigEditor);
            btnBuscar = view.findViewById(R.id.btnBuscar);
            btnAbrir = view.findViewById(R.id.btnAbrir);
            btnSalvar = view.findViewById(R.id.btnSalvar);
            btnCompilar = view.findViewById(R.id.btnCompilar);
            txtTitulo = view.findViewById(R.id.txtTituloEditor);
        }

        public ImageButton getBtnConfig() { return btnConfig; }
        public ImageButton getBtnBuscar() { return btnBuscar; }
        public Button getBtnAbrir() { return btnAbrir; }
        public Button getBtnSalvar() { return btnSalvar; }
        public Button getBtnCompilar() { return btnCompilar; }
        public TextView getTxtTitulo() { return txtTitulo; }
    }
}
