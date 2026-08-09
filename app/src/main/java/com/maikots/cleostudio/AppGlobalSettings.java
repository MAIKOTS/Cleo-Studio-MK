package com.maikots.cleostudio;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Toast;

public class AppGlobalSettings {

    private static long tempoUltimoCliqueVoltar = 0;
    private static final int INTERVALO_PRESSIONAR_VOLTAR = 2000; // 2 segundos

    /**
     * Previne que o app feche acidentalmente ao clicar no botão 'Voltar' do Android.
     * Exige um duplo clique em até 2 segundos para sair.
     */
    public static boolean manipularBotaoVoltar(Activity activity) {
        if (tempoUltimoCliqueVoltar + INTERVALO_PRESSIONAR_VOLTAR > System.currentTimeMillis()) {
            return true; // Permite fechar o app no segundo clique
        } else {
            Toast.makeText(activity, "Pressione 'Voltar' novamente para sair", Toast.LENGTH_SHORT).show();
            tempoUltimoCliqueVoltar = System.currentTimeMillis();
            return false; // Bloqueia o fechamento acidental no primeiro clique
        }
    }

    /**
     * Aplica o modo Imersivo/Tela Cheia padrão do app de forma unificada.
     */
    public static void aplicarModoFullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = activity.getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }
}
