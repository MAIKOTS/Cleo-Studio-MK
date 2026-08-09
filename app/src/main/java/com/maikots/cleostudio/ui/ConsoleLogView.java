package com.maikots.cleostudio;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ConsoleLogView extends LinearLayout {

    private final TextView painelTexto;

    public ConsoleLogView(Context context) {
        super(context);
        setOrientation(LinearLayout.VERTICAL);

        TextView label = new TextView(context);
        label.setText("Console de Saída:");
        label.setTextColor(Color.LTGRAY);
        label.setTextSize(12);
        label.setPadding(8, 8, 8, 4);
        addView(label);

        ScrollView scroll = new ScrollView(context);
        LayoutParams paramsScroll = new LayoutParams(LayoutParams.MATCH_PARENT, 260);
        scroll.setLayoutParams(paramsScroll);
        scroll.setBackgroundColor(Color.parseColor("#0A0A0A"));

        painelTexto = new TextView(context);
        painelTexto.setTextColor(Color.parseColor("#00FF00"));
        painelTexto.setTypeface(Typeface.MONOSPACE);
        painelTexto.setTextSize(12);
        painelConsole();
        painelTexto.setPadding(16, 16, 16, 16);
        painelTexto.setText("Pronto.");

        scroll.addView(painelTexto);
        addView(scroll);
    }

    private void painelConsole() {}

    public void logSucesso(String msg) {
        painelTexto.setTextColor(Color.parseColor("#00FF00"));
        painelTexto.setText(msg);
    }

    public void logErro(String msg) {
        painelTexto.setTextColor(Color.parseColor("#FF5252"));
        painelTexto.setText(msg);
    }
}
