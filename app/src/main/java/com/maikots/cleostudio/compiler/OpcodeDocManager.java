package com.maikots.cleostudio.compiler;

import android.content.Context;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class OpcodeDocManager {

    public static class OpcodeDoc {
        public final String nome;
        public final String sintaxe;
        public final String descricao;

        public OpcodeDoc(String nome, String sintaxe, String descricao) {
            this.nome = nome;
            this.sintaxe = sintaxe;
            this.descricao = descricao;
        }
    }

    private static final Map<String, OpcodeDoc> mapaDocs = new HashMap<>();
    private static boolean carregado = false;

    public static void inicializar(Context context) {
        if (carregado) return;
        try {
            InputStream is = context.getAssets().open("compilador/opcodes_docs.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            JSONObject json = new JSONObject(new String(buffer, StandardCharsets.UTF_8));
            Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                String opcodeHex = keys.next().toUpperCase();
                JSONObject obj = json.getJSONObject(opcodeHex);

                mapaDocs.put(opcodeHex, new OpcodeDoc(
                        obj.optString("nome", ""),
                        obj.optString("sintaxe", ""),
                        obj.optString("descricao", "")
                ));
            }
            carregado = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static OpcodeDoc obterDoc(String opcodeHex) {
        if (opcodeHex == null) return null;
        return mapaDocs.get(opcodeHex.toUpperCase());
    }
}
