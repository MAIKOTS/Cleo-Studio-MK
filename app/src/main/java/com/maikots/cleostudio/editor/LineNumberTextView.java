package com.maikots.cleostudio.editor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

public class LineNumberTextView extends View {

    private EditText editText;
    private final Paint paintNumero;
    private final Paint paintLinhaDivisoria;
    private int ultimaLarguraCalculada = -1;

    public LineNumberTextView(Context context) {
        super(context);

        // Fundo com leve variação de tom (#181818)
        setBackgroundColor(Color.parseColor("#181818"));

        // Pincel para os números das linhas
        paintNumero = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintNumero.setColor(Color.parseColor("#555555"));
        paintNumero.setTypeface(Typeface.MONOSPACE);

        // Pincel para a linha vertical divisória
        paintLinhaDivisoria = new Paint();
        paintLinhaDivisoria.setColor(Color.parseColor("#2A2A2A"));
        paintLinhaDivisoria.setStrokeWidth(2f);
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
        if (editText != null) {
            paintNumero.setTextSize(editText.getTextSize());
        }
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (editText == null || editText.getLayout() == null) return;

        int totalLinhas = editText.getLineCount();
        
        // Adjusta a largura da barra dinamicamente para não sobrar nem faltar espaço
        ajustarLarguraDinamica(totalLinhas);

        // Desenha a linha vertical divisória rente à direita
        canvas.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight(), paintLinhaDivisoria);

        int linhaAtualCursor = obterLinhaAtualDoCursor();

        for (int i = 0; i < totalLinhas; i++) {
            int baseline = editText.getLayout().getLineBaseline(i);

            if (i == linhaAtualCursor) {
                paintNumero.setColor(Color.parseColor("#00E676")); // Verde destaque
                paintNumero.setFakeBoldText(true);
            } else {
                paintNumero.setColor(Color.parseColor("#4A4A4A")); // Cinza discreto
                paintNumero.setFakeBoldText(false);
            }

            String numeroStr = String.valueOf(i + 1);
            
            // Alinha o número com apenas 6px de folga da linha divisória
            float xPos = getWidth() - paintNumero.measureText(numeroStr) - 6;
            canvas.drawText(numeroStr, xPos, baseline + editText.getPaddingTop(), paintNumero);
        }
    }

    private void ajustarLarguraDinamica(int totalLinhas) {
        // Pega a quantidade de dígitos (ex: "99" = 2, "100" = 3)
        String maiorNumero = String.valueOf(Math.max(totalLinhas, 9)); 
        float larguraTexto = paintNumero.measureText(maiorNumero);
        
        // Largura exata = Tamanho do texto + margem mínima (12px total)
        int larguraDesejada = (int) (larguraTexto + 14);

        if (larguraDesejada != ultimaLarguraCalculada) {
            ultimaLarguraCalculada = larguraDesejada;
            post(() -> {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
                if (params != null) {
                    params.width = larguraDesejada;
                    setLayoutParams(params);
                }
            });
        }
    }

    private int obterLinhaAtualDoCursor() {
        int posSelection = editText.getSelectionStart();
        if (posSelection < 0) return 0;
        return editText.getLayout().getLineForOffset(posSelection);
    }
}
