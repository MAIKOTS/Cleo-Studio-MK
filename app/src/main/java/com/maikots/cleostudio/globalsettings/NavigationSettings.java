package com.maikots.cleostudio.globalsettings;

import android.app.Activity;
import android.widget.Toast;

public class NavigationSettings {

    private static long tempoUltimoCliqueVoltar = 0;
    private static final int INTERVALO_PRESSIONAR_VOLTAR = 2000;

    public static boolean manipularBotaoVoltar(Activity activity) {
        if (tempoUltimoCliqueVoltar + INTERVALO_PRESSIONAR_VOLTAR > System.currentTimeMillis()) {
            return true;
        } else {
            Toast.makeText(activity, "Pressione 'Voltar' novamente para sair", Toast.LENGTH_SHORT).show();
            tempoUltimoCliqueVoltar = System.currentTimeMillis();
            return false;
        }
    }
}
