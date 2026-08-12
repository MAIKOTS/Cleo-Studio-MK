package com.maikots.cleostudio.ui.menu;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupMenu;

import com.maikots.cleostudio.globalsettings.AppGlobalSettings;

public class PopUpTopActionBarMenu {

    public interface OnOptionSelectedListener {
        void onOptionSelected(String opcao);
    }

    /**
     * Exibe o menu popup e já conecta internamente as ações automáticas (como abrir Créditos).
     */
    public static void exibir(Context context, View anchorView, OnOptionSelectedListener customListener) {
        if (context == null || anchorView == null) return;

        PopupMenu popup = new PopupMenu(context, anchorView);

        // Adiciona as opções ao menu
        popup.getMenu().add(0, 1, 0, "⚙️ Configurações");
        popup.getMenu().add(0, 2, 1, "🔄 Ver Atualizações");
        popup.getMenu().add(0, 3, 2, "ℹ️ Sobre o CLEO Studio");
        popup.getMenu().add(0, 4, 3, "👨‍💻 Créditos");

        popup.setOnMenuItemClickListener(item -> {
            String opcaoSelecionada = "";

            switch (item.getItemId()) {
                case 1:
                    opcaoSelecionada = "configuracoes";
                    break;
                case 2:
                    opcaoSelecionada = "atualizar";
                    break;
                case 3:
                    opcaoSelecionada = "sobre";
                    break;
                case 4:
                    opcaoSelecionada = "creditos";
                    // ⚡ CONEXÃO AUTOMÁTICA: Abre a tela PopUpDashboardCreditos diretamente
                    abrirTelaCreditos(context);
                    break;
            }

            // Notifica o listener externo (se houver)
            if (customListener != null && !opcaoSelecionada.isEmpty()) {
                customListener.onOptionSelected(opcaoSelecionada);
            }

            return true;
        });

        // ⚡ Restaura o modo tela cheia ao fechar o menu
        popup.setOnDismissListener(menu -> {
            if (context instanceof Activity) {
                AppGlobalSettings.aplicarModoFullScreen((Activity) context);
            }
        });

        popup.show();

        if (context instanceof Activity) {
            AppGlobalSettings.aplicarModoFullScreen((Activity) context);
        }
    }

    /**
     * Instancia e adiciona a view de Créditos diretamente na raiz da Activity
     */
    private static void abrirTelaCreditos(Context context) {
        if (!(context instanceof Activity)) return;

        Activity activity = (Activity) context;
        ViewGroup rootContainer = activity.findViewById(android.R.id.content);

        if (rootContainer != null) {
            // Cria a view de créditos conectada
            PopUpDashboardCreditos telaCreditos = new PopUpDashboardCreditos(context);

            // Adiciona a view ocupando toda a tela
            rootContainer.addView(telaCreditos, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            ));

            AppGlobalSettings.aplicarModoFullScreen(activity);
        }
    }
}
