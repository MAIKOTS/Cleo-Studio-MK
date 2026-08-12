package com.maikots.cleostudio.compiler;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class CompilerConfig {

    private static final Set<String> PALAVRAS_IGNORADAS = new HashSet<>();
    private static final Set<Integer> OPCODES_FORMATADOS = new HashSet<>();
    private static final Set<Integer> OPCODES_INT32 = new HashSet<>();
    private static boolean carregado = false;

    public static void inicializar(Context context) {
        if (carregado) return;
        try {
            InputStream is = context.getAssets().open("compilador/compiler_config.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            JSONObject json = new JSONObject(new String(buffer, StandardCharsets.UTF_8));

            // 1. Carrega Palavras Ignoradas (Sintaxe Cosmética)
            JSONArray arrayIgnoradas = json.optJSONArray("palavras_ignoradas");
            if (arrayIgnoradas != null) {
                for (int i = 0; i < arrayIgnoradas.length(); i++) {
                    PALAVRAS_IGNORADAS.add(arrayIgnoradas.getString(i).toUpperCase());
                }
            }

            // 2. Carrega Opcodes Formatados
            JSONArray arrayFormatados = json.optJSONArray("opcodes_formatados_null_terminated");
            if (arrayFormatados != null) {
                for (int i = 0; i < arrayFormatados.length(); i++) {
                    int op = Integer.parseInt(arrayFormatados.getString(i), 16);
                    OPCODES_FORMATADOS.add(op);
                }
            }

            // 3. Carrega Opcodes que exigem Int32
            JSONArray arrayInt32 = json.optJSONArray("opcodes_int32_obrigatorio");
            if (arrayInt32 != null) {
                for (int i = 0; i < arrayInt32.length(); i++) {
                    int op = Integer.parseInt(arrayInt32.getString(i), 16);
                    OPCODES_INT32.add(op);
                }
            }

            carregado = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isPalavraIgnorada(String palavra) {
        return PALAVRAS_IGNORADAS.contains(palavra.toUpperCase());
    }

    public static boolean isOpcodeFormatado(int opcode) {
        return OPCODES_FORMATADOS.contains(opcode);
    }

    public static boolean isOpcodeInt32Obrigatorio(int opcode) {
        return OPCODES_INT32.contains(opcode);
    }
}
