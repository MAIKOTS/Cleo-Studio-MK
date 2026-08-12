package com.maikots.cleostudio.ui;

import android.app.Activity;
import android.view.View;
import com.maikots.cleostudio.globalsettings.AppGlobalSettings;
import com.maikots.cleostudio.console.ConsoleManager;
import com.maikots.cleostudio.editor.MainEditorLayout;

public class NavigationHandler {

    public static boolean tratarBotaoVoltar(Activity activity, ConsoleManager consoleManager, MainEditorLayout editorLayout) {
        // 1º: Console Flutuante
        if (consoleManager != null && consoleManager.isVisivel()) {
            consoleManager.ocultar();
            return true;
        }

        if (editorLayout != null) {
            // 2º: Painel de Busca
            if (editorLayout.getPainelBusca().getVisibility() == View.VISIBLE) {
                editorLayout.getPainelBusca().fechar();
                return true;
            }

            // 3º: Teclado Customizado
            if (editorLayout.getTecladoNativo().getVisibility() == View.VISIBLE) {
                editorLayout.getTecladoNativo().ocultarTeclado();
                return true;
            }
        }

        // 4º: Confirmação de saída do aplicativo
        return !AppGlobalSettings.manipularBotaoVoltar(activity);
    }
}
