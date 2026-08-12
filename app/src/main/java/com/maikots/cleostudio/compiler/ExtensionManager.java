package com.maikots.cleostudio.compiler;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ExtensionManager {

    private static final Map<String, Set<String>> extensoesOpcodes = new HashMap<>();
    private static boolean carregado = false;

    public static void inicializar(Context context) {
        if (carregado) return;
        try {
            InputStream is = context.getAssets().open("compilador/extensions_config.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            JSONObject json = new JSONObject(new String(buffer, StandardCharsets.UTF_8));
            JSONObject extensoesObj = json.optJSONObject("extensoes");

            if (extensoesObj != null) {
                Iterator<String> keys = extensoesObj.keys();
                while (keys.hasNext()) {
                    String nomeExtensao = keys.next();
                    JSONObject ext = extensoesObj.getJSONObject(nomeExtensao);
                    JSONArray arrOpcodes = ext.optJSONArray("opcodes");

                    Set<String> opcodesSet = new HashSet<>();
                    if (arrOpcodes != null) {
                        for (int i = 0; i < arrOpcodes.length(); i++) {
                            opcodesSet.add(arrOpcodes.getString(i).toUpperCase());
                        }
                    }
                    extensoesOpcodes.put(nomeExtensao, opcodesSet);
                }
            }
            carregado = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isOpcodeDeExtensao(String opcodeHex) {
        if (opcodeHex == null) return false;
        String op = opcodeHex.toUpperCase();
        for (Set<String> opcodes : extensoesOpcodes.values()) {
            if (opcodes.contains(op)) return true;
        }
        return false;
    }
}
