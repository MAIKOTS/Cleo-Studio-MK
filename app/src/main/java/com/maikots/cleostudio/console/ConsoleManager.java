package com.maikots.cleostudio.console;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

public class ConsoleManager {

    private final FrameLayout containerOverlay;
    private final ConsoleLogView consoleView;
    private boolean visivel = false;

    public ConsoleManager(Context context, FrameLayout rootFrame) {
        // Overlay transparente que cobre a tela toda ao abrir
        containerOverlay = new FrameLayout(context);
        containerOverlay.setBackgroundColor(Color.parseColor("#40000000")); // Sombra leve

        // Se clicar na área sombreada fora do console, fecha o console
        containerOverlay.setOnClickListener(v -> ocultar());

        // Define a largura para apenas 55% da tela no canto direito
        int larguraConsole = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.55);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                larguraConsole, FrameLayout.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.END;

        consoleView = new ConsoleLogView(context);
        consoleView.setLayoutParams(params);
        
        // Impede que cliques DENTRO do console fechem ele
        consoleView.setOnClickListener(v -> {}); 
        consoleView.setOnFecharClickListener(this::ocultar);

        containerOverlay.addView(consoleView);
        containerOverlay.setVisibility(View.GONE);

        rootFrame.addView(containerOverlay);
    }

    public void exibir() {
        containerOverlay.setVisibility(View.VISIBLE);
        containerOverlay.bringToFront();
        visivel = true;
    }

    public void ocultar() {
        containerOverlay.setVisibility(View.GONE);
        visivel = false;
    }

    public void alternar() {
        if (visivel) ocultar(); else exibir();
    }

    public boolean isVisivel() {
        return visivel;
    }

    public void logSucesso(String mensagem) {
        exibir();
        consoleView.logSucesso(mensagem);
    }

    public void logErro(String mensagem) {
        exibir();
        consoleView.logErro(mensagem);
    }

    public void limpar() {
        consoleView.limpar();
    }
}
