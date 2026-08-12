package com.maikots.cleostudio.globalsettings;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

public class UISettings {

    /**
     * Aplica o modo tela cheia imersivo permanente e impede o aparecimento das barras
     * ao clicar em menus, cards, popups ou rotacionar a tela.
     */
    public static void aplicarModoFullScreen(Activity activity) {
        if (activity == null || activity.getWindow() == null) return;

        Window window = activity.getWindow();
        View decorView = window.getDecorView();

        // 1. Aplica o modo tela cheia de imediato
        esconderBarrasSistema(activity, decorView);

        // 2. Garante re-aplicação contínua quando qualquer elemento (Popup, Card, Menu) ganha/perde foco
        decorView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                esconderBarrasSistema(activity, decorView);
            }
        });

        // 3. Ouve mudanças de visibilidade do sistema (em versões do Android < 11 / API 30)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    esconderBarrasSistema(activity, decorView);
                }
            });
        }
    }

    /**
     * Executa a ocultação das barras de status e navegação respeitando a versão do Android
     */
    private static void esconderBarrasSistema(Activity activity, View decorView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = activity.getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }

    public static void manterTelaLigada(Activity activity, boolean manter) {
        if (activity == null || activity.getWindow() == null) return;

        if (manter) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    public static void otimizarRenderizacaoHardware(Activity activity) {
        if (activity == null || activity.getWindow() == null) return;
        
        activity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        );
    }
}
