package com.maikots.cleostudio.editor;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SnippetManager {

    public static class Snippet {
        public final String titulo;
        public final String atalho;
        public final String descricao;
        public final String conteudo;

        public Snippet(String titulo, String atalho, String descricao, String conteudo) {
            this.titulo = titulo;
            this.atalho = atalho;
            this.descricao = descricao;
            this.conteudo = conteudo;
        }
    }

    private static final List<Snippet> listaSnippets = new ArrayList<>();
    private static boolean carregado = false;

    public static void inicializar(Context context) {
        if (carregado) return;
        try {
            InputStream is = context.getAssets().open("editor/code_snippets.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            JSONArray jsonArray = new JSONArray(new String(buffer, StandardCharsets.UTF_8));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                listaSnippets.add(new Snippet(
                        obj.optString("titulo", ""),
                        obj.optString("atalho", ""),
                        obj.optString("descricao", ""),
                        obj.optString("conteudo", "")
                ));
            }
            carregado = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Snippet> getSnippets() {
        return listaSnippets;
    }
}
