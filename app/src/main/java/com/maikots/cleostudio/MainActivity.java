package com.maikots.cleostudio;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.maikots.cleostudio.cards.OpcodesTableView;
import com.maikots.cleostudio.compiler.CompilationService;
import com.maikots.cleostudio.console.ConsoleManager;
import com.maikots.cleostudio.editor.MainEditorLayout;
import com.maikots.cleostudio.globalsettings.AppGlobalSettings;
import com.maikots.cleostudio.ui.HomeDashboardLayout;
import com.maikots.cleostudio.ui.NavigationHandler;

public class MainActivity extends Activity {

    private FrameLayout containerRaiz;
    private HomeDashboardLayout dashboardLayout;
    private MainEditorLayout editorLayout;
    private OpcodesTableView opcodesTableView;
    private ConsoleManager consoleManager;

    private String telaAtual = "dashboard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Container Principal
        containerRaiz = new FrameLayout(this);
        containerRaiz.setBackgroundColor(Color.parseColor("#121212"));

        // 2. Instancia a Dashboard (Menu com Cards)
        dashboardLayout = new HomeDashboardLayout(this, this::navegarParaFerramenta);
        
        // ⚙️ Trata as escolhas do Menu de Opções da Dashboard
        dashboardLayout.setOnOptionSelectedListener(opcao -> {
            switch (opcao) {
                case "configuracoes":
                    // TODO: Abrir janela/painel de Configurações
                    break;
                case "atualizar":
                    // TODO: Checar atualizações do app
                    break;
                case "sobre":
                    // TODO: Mostrar diálogo "Sobre"
                    break;
                case "creditos":
                    // TODO: Mostrar créditos
                    break;
            }
        });

        containerRaiz.addView(dashboardLayout, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        // 🌐 3. Inicialização dos Sistemas Globais
        AppGlobalSettings.inicializar(this, null);

        setContentView(containerRaiz);
    }

    /**
     * Alterna entre as telas sem fechar a MainActivity
     */
    private void navegarParaFerramenta(String toolId) {
        AppGlobalSettings.logSistema(this, "MainActivity", "Navegando para: " + toolId);

        switch (toolId) {
            case "editor":
                abrirEditor();
                break;
            case "opcodes":
                abrirTabelaOpcodes();
                break;
            // TODO: Adicionar descompilador, projetos etc. conforme criados
        }
    }
    
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            AppGlobalSettings.aplicarModoFullScreen(this);
        }
    }

    private void abrirEditor() {
        if (editorLayout == null) {
            // Instancia o layout do Editor sob demanda
            editorLayout = new MainEditorLayout(this);
            consoleManager = new ConsoleManager(this, containerRaiz);

            // Restaura o texto salvo
            AppGlobalSettings.salvarEstado(this, editorLayout.getEditorTexto());

            // Vincula o botão compilar do Editor
            if (editorLayout.getTopBar() != null && editorLayout.getTopBar().getBtnCompilar() != null) {
                editorLayout.getTopBar().getBtnCompilar().setOnClickListener(v ->
                    CompilationService.executar(this, editorLayout.getEditorTexto().getCodigo(), consoleManager)
                );
            }

            // Monitor de Memória
            AppGlobalSettings.registrarMonitorDeMemoria(this, () -> {
                if (consoleManager != null) consoleManager.limpar();
            });
        }

        // Se a tabela de opcodes estiver visível, esconde
        if (opcodesTableView != null) {
            opcodesTableView.setVisibility(View.GONE);
        }

        // Esconde a Dashboard e exibe o Editor
        dashboardLayout.setVisibility(View.GONE);
        if (editorLayout.getParent() == null) {
            containerRaiz.addView(editorLayout, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        }
        editorLayout.setVisibility(View.VISIBLE);
        telaAtual = "editor";
    }

    private void abrirTabelaOpcodes() {
        if (opcodesTableView == null) {
            // Instancia o card/tabela do pacote cards
            opcodesTableView = new OpcodesTableView(this);

            // Se o editor já foi criado, vincula ele para poder inserir o opcode clicado
            if (editorLayout != null && editorLayout.getEditorTexto() != null) {
                opcodesTableView.setEditorAlvo(editorLayout.getEditorTexto());
            }
        }

        // Esconde o editor se estiver visível
        if (editorLayout != null) {
            editorLayout.setVisibility(View.GONE);
        }

        // Esconde a Dashboard e exibe a Tabela
        dashboardLayout.setVisibility(View.GONE);

        if (opcodesTableView.getParent() == null) {
            containerRaiz.addView(opcodesTableView, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        }
        opcodesTableView.setVisibility(View.VISIBLE);
        telaAtual = "opcodes";
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBackPressed() {
        // Se a Tabela de Opcodes estiver aberta, volta para a Dashboard
        if ("opcodes".equals(telaAtual)) {
            if (opcodesTableView != null) {
                opcodesTableView.setVisibility(View.GONE);
            }
            dashboardLayout.setVisibility(View.VISIBLE);
            telaAtual = "dashboard";
            return;
        }

        // Se o editor estiver aberto
        if ("editor".equals(telaAtual)) {
            if (NavigationHandler.tratarBotaoVoltar(this, consoleManager, editorLayout)) {
                return;
            }
            // Volta para a Dashboard de Cards
            if (editorLayout != null) {
                editorLayout.setVisibility(View.GONE);
            }
            dashboardLayout.setVisibility(View.VISIBLE);
            telaAtual = "dashboard";
            return;
        }

        // Se estiver na Dashboard, gerencia o duplo toque para sair
        if (!AppGlobalSettings.manipularBotaoVoltar(this)) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (editorLayout != null && editorLayout.getEditorTexto() != null) {
            AppGlobalSettings.salvarEstado(this, editorLayout.getEditorTexto());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppGlobalSettings.aplicarModoFullScreen(this);
    }
}
