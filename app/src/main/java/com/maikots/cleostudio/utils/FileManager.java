package com.maikots.cleostudio.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileManager {

    /**
     * Salva o bytecode compilado (.csa / .csi) no armazenamento do dispositivo.
     */
    public static boolean salvarBytecode(Context context, String nomeArquivo, byte[] bytecode) {
        if (bytecode == null || bytecode.length == 0) return false;

        try {
            // Garante a extensão .csa caso não tenha sido informada
            if (!nomeArquivo.endsWith(".csa") && !nomeArquivo.endsWith(".csi")) {
                nomeArquivo += ".csa";
            }

            // Diretório interno seguro da aplicação ou Downloads/CLEO
            File diretorio = new File(context.getExternalFilesDir(null), "CLEO_Scripts");
            if (!diretorio.exists()) {
                diretorio.mkdirs();
            }

            File arquivoFinal = new File(diretorio, nomeArquivo);
            FileOutputStream fos = new FileOutputStream(arquivoFinal);
            fos.write(bytecode);
            fos.flush();
            fos.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retorna o caminho absoluto do diretório onde os scripts são salvos.
     */
    public static String getCaminhoPastaScripts(Context context) {
        File diretorio = new File(context.getExternalFilesDir(null), "CLEO_Scripts");
        return diretorio.getAbsolutePath();
    }
}
