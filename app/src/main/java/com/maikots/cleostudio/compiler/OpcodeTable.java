package com.maikots.cleostudio.compiler;

import android.content.Context;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class OpcodeTable {

    private static final Map<String, Integer> OPCODES = new HashMap<>();
    private static boolean carregado = false;

    public static void inicializar(Context context) {
        if (carregado) return;
        try {
            InputStream is = context.getAssets().open("compilador/opcodes.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            JSONObject jsonObject = new JSONObject(new String(buffer, StandardCharsets.UTF_8));
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String comando = keys.next();
                int opcodeInt = Integer.parseInt(jsonObject.getString(comando), 16);
                OPCODES.put(comando.toUpperCase(), opcodeInt);
            }
            carregado = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Integer obterOpcode(String comando) {
        if (comando == null) return null;
        return OPCODES.get(comando.toUpperCase());
    }
}
