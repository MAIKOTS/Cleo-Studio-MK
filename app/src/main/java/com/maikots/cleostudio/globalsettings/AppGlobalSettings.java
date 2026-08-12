package com.maikots.cleostudio.globalsettings;

import android.app.Activity;
import android.content.Context;

import com.maikots.cleostudio.compiler.CompilerConfig;
import com.maikots.cleostudio.compiler.ExtensionManager;
import com.maikots.cleostudio.compiler.OpcodeDocManager;
import com.maikots.cleostudio.compiler.OpcodeTable;
import com.maikots.cleostudio.editor.CodeEditorView;
import com.maikots.cleostudio.editor.SnippetManager;

public class AppGlobalSettings {

    /**
     * 🚀 INICIALIZAÇÃO GLOBAL DO APLICATIVO
     */
    public static void inicializar(Activity activity, CodeEditorView editorTexto) {
        if (activity == null) return;

        // 1. Log de Inicialização do App
        logSistema(activity, "AppGlobalSettings", "Iniciando subsistemas do Cleo Studio...");

        // 2. Configurações de UI e Hardware
        UISettings.aplicarModoFullScreen(activity);
        UISettings.manterTelaLigada(activity, true);
        UISettings.otimizarRenderizacaoHardware(activity);

        // 3. Carregamento das Tabelas e Recursos do Compilador
        OpcodeTable.inicializar(activity);
        CompilerConfig.inicializar(activity);
        OpcodeDocManager.inicializar(activity);
        ExtensionManager.inicializar(activity);
        SnippetManager.inicializar(activity);

        // 4. Restauração de Estado
        if (editorTexto != null) {
            AppStateSettings.restaurarEstado(activity, editorTexto);
        }

        logSistema(activity, "AppGlobalSettings", "Todos os subsistemas foram carregados com sucesso.");
    }

    // =======================================================
    // 🪵 MÉTODOS PÚBLICOS DE LOG
    // =======================================================

    /**
     * Registra logs do sistema/app no arquivo 'app_system.log'
     */
    public static void logSistema(Context context, String tag, String mensagem) {
        LogSettings.registrar(context, LogSettings.TipoLog.SISTEMA, tag, mensagem);
    }

    /**
     * Registra logs da compilação no arquivo 'compiler_output.log'
     */
    public static void logCompilador(Context context, String tag, String mensagem) {
        LogSettings.registrar(context, LogSettings.TipoLog.COMPILADOR, tag, mensagem);
    }

    /**
     * Apaga os arquivos de log
     */
    public static void limparLogs(Context context) {
        LogSettings.limparLogs(context);
    }

    // Métodos utilitários repassados
    public static boolean manipularBotaoVoltar(Activity activity) {
        return NavigationSettings.manipularBotaoVoltar(activity);
    }

    public static void registrarMonitorDeMemoria(Context context, Runnable callbackLiberarMemoria) {
        MemorySettings.registrarMonitorDeMemoria(context, callbackLiberarMemoria);
    }

    public static void salvarEstado(Context context, CodeEditorView editorTexto) {
        AppStateSettings.salvarEstado(context, editorTexto);
    }

    public static void aplicarModoFullScreen(Activity activity) {
        UISettings.aplicarModoFullScreen(activity);
    }
}
