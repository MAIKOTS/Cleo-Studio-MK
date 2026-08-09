package com.maikots.cleostudio;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

    private TopActionBar topBar;
    private FindReplaceView painelBusca;
    private CodeEditorView editorTexto;
    private CustomKeyboardView tecladoNativo;
    private ConsoleLogView consoleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🌐 Aplica o modo tela cheia através da classe global
        AppGlobalSettings.aplicarModoFullScreen(this);

        // 1. Layout Raiz (Vertical)
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#121212"));

        // 2. Barra Superior de Ações
        topBar = new TopActionBar(this);
        root.addView(topBar);

        // 3. Editor de Código CLEO
        editorTexto = new CodeEditorView(this);
        editorTexto.desativarTecladoNativoSystema();

        // 🔍 4. Painel de Busca e Substituição (Inserido entre a TopBar e o Editor)
        painelBusca = new FindReplaceView(this, editorTexto);
        root.addView(painelBusca);

        LinearLayout.LayoutParams paramsEditor = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f);
        editorTexto.setLayoutParams(paramsEditor);
        root.addView(editorTexto);

        // 5. Console de Saída / Terminal
        consoleView = new ConsoleLogView(this);
        root.addView(consoleView);

        // 6. Teclado Nativo Customizado
        tecladoNativo = new CustomKeyboardView(this, editorTexto);
        root.addView(tecladoNativo);

        // 🔗 Vincula o Teclado Nativo ao Editor de Código
        editorTexto.setTecladoNativo(tecladoNativo);

        // Ações da Barra Superior
        topBar.getBtnCompilar().setOnClickListener(v -> executarCompilacao());
        
        // Se a sua TopActionBar tiver o botão de busca configurado, vincule assim:
        // topBar.getBtnBuscar().setOnClickListener(v -> alternarPainelBusca());

        setContentView(root);
    }

    // Método utilitário para abrir/fechar o painel de busca
    public void alternarPainelBusca() {
        if (painelBusca.getVisibility() == View.VISIBLE) {
            painelBusca.fechar();
        } else {
            painelBusca.abrir();
        }
    }

    private void executarCompilacao() {
        String codigo = editorTexto.getCodigo();
        if (codigo.isEmpty()) {
            consoleView.logErro("Erro: Editor de código vazio!");
            return;
        }

        consoleView.logSucesso("--- COMPILAÇÃO INICIADA ---\nCódigo processado com sucesso!");
    }

    // 🔒 Intercepta o botão 'Voltar' do Android em hierarquia
    @Override
    public void onBackPressed() {
        // 1º: Se o painel de busca estiver visível, fecha ele primeiro
        if (painelBusca != null && painelBusca.getVisibility() == View.VISIBLE) {
            painelBusca.fechar();
            return;
        }

        // 2º: Se o teclado customizado estiver visível, fecha o teclado
        if (tecladoNativo != null && tecladoNativo.getVisibility() == View.VISIBLE) {
            tecladoNativo.ocultarTeclado();
            return;
        }

        // 3º: Exige confirmação de duplo clique em 2s para sair do app
        if (AppGlobalSettings.manipularBotaoVoltar(this)) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 🌐 Garante que o app continue em tela cheia
        AppGlobalSettings.aplicarModoFullScreen(this);
    }
}
