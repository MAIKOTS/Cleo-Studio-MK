package com.maikots.cleostudio.globalsettings;

import android.content.Context;
import android.content.SharedPreferences;

import com.maikots.cleostudio.editor.CodeEditorView;

public class AppStateSettings {

    private static final String PREF_NAME = "cleo_studio_state";
    private static final String KEY_CODIGO = "ultimo_codigo";
    private static final String KEY_CURSOR_POS = "posicao_cursor";

    public static void salvarEstado(Context context, CodeEditorView editorTexto) {
        if (context == null || editorTexto == null) return;

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_CODIGO, editorTexto.getCodigo())
                .putInt(KEY_CURSOR_POS, editorTexto.getSelectionStart())
                .apply();
    }

    public static void restaurarEstado(Context context, CodeEditorView editorTexto) {
        if (context == null || editorTexto == null) return;

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String codigoSalvo = prefs.getString(KEY_CODIGO, "");
        int cursorSalvo = prefs.getInt(KEY_CURSOR_POS, 0);

        if (!codigoSalvo.isEmpty()) {
            editorTexto.inserirTexto(codigoSalvo);
            if (cursorSalvo <= codigoSalvo.length()) {
                editorTexto.setSelection(cursorSalvo);
            }
        }
    }
}
