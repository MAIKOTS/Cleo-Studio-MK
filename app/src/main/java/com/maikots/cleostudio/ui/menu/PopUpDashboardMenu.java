package com.maikots.cleostudio.ui.menu;

import android.app.Activity;
import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupMenu;

import com.maikots.cleostudio.R;
import com.maikots.cleostudio.globalsettings.AppGlobalSettings;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PopUpDashboardMenu {

    public interface OnOptionSelectedListener {
        void onOptionSelected(String opcao);
    }

    /**
     * Exibe o menu pop-up do Header do Dashboard.
     */
    public static void exibir(Context context, View anchorView, OnOptionSelectedListener customListener) {
        if (context == null || anchorView == null) return;

        PopupMenu popup = new PopupMenu(context, anchorView);

        MenuItem item1 = popup.getMenu().add(0, 1, 0, "Configurações");
        item1.setIcon(R.drawable.ic_settings);

        MenuItem item2 = popup.getMenu().add(0, 2, 1, "Ver Atualizações");
        item2.setIcon(R.drawable.ic_update);

        MenuItem item3 = popup.getMenu().add(0, 3, 2, "Sobre o CLEO Studio");
        item3.setIcon(R.drawable.ic_about);

        MenuItem item4 = popup.getMenu().add(0, 4, 3, "Créditos");
        item4.setIcon(R.drawable.ic_credits);

        forcarExibicaoIconesMenu(popup);

        popup.setOnMenuItemClickListener(item -> {
            String opcao = "";
            switch (item.getItemId()) {
                case 1: opcao = "configuracoes"; break;
                case 2: opcao = "atualizar"; break;
                case 3: opcao = "sobre"; break;
                case 4: 
                    opcao = "creditos"; 
                    // ⚡ CONEXÃO AUTOMÁTICA: Abre diretamente a tela PopUpDashboardCreditos
                    abrirTelaCreditos(context);
                    break;
            }

            if (customListener != null && !opcao.isEmpty()) {
                customListener.onOptionSelected(opcao);
            }
            return true;
        });

        // ⚡ Garante que o app continue em modo imersivo/tela cheia após fechar o menu
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
     * Instancia e exibe a tela PopUpDashboardCreditos na raiz da Activity
     */
    private static void abrirTelaCreditos(Context context) {
        if (!(context instanceof Activity)) return;

        Activity activity = (Activity) context;
        ViewGroup rootContainer = activity.findViewById(android.R.id.content);

        if (rootContainer != null) {
            PopUpDashboardCreditos telaCreditos = new PopUpDashboardCreditos(context);

            rootContainer.addView(telaCreditos, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            ));

            AppGlobalSettings.aplicarModoFullScreen(activity);
        }
    }

    /**
     * Força o sistema do Android a exibir os ícones vetorizados no PopupMenu
     */
    private static void forcarExibicaoIconesMenu(PopupMenu popup) {
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    if (menuPopupHelper != null) {
                        Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                        Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                        setForceIcons.invoke(menuPopupHelper, true);
                    }
                    break;
                }
            }
        } catch (Exception ignored) {
        }
    }
}
