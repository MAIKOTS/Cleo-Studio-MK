package com.maikots.cleostudio.globalsettings;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LogSettings {

    private static final String NOME_PASTA_LOGS = "logs";
    private static final String ARQUIVO_LOG_SISTEMA = "app_system.log";
    private static final String ARQUIVO_LOG_COMPILADOR = "compiler_output.log";

    // Pool de thread única para garantir gravação ordenada sem impactar a UI
    private static final ExecutorService logExecutor = Executors.newSingleThreadExecutor();

    public enum TipoLog {
        SISTEMA,
        COMPILADOR
    }

    /**
     * Grava uma nova linha no arquivo de log correspondente.
     */
    public static void registrar(Context context, TipoLog tipo, String tag, String mensagem) {
        if (context == null || mensagem == null) return;

        // Exibe no Logcat do Android Studio em tempo de execução
        Log.d(tag, mensagem);

        logExecutor.execute(() -> {
            try {
                File pastaLogs = new File(context.getExternalFilesDir(null), NOME_PASTA_LOGS);
                if (!pastaLogs.exists()) {
                    pastaLogs.mkdirs();
                }

                String nomeArquivo = (tipo == TipoLog.COMPILADOR) ? ARQUIVO_LOG_COMPILADOR : ARQUIVO_LOG_SISTEMA;
                File arquivoLog = new File(pastaLogs, nomeArquivo);

                String dataHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(new Date());
                String linhaFormatada = String.format("[%s] [%s] %s: %s\n", dataHora, tipo.name(), tag, mensagem);

                FileWriter writer = new FileWriter(arquivoLog, true); // true = append
                writer.append(linhaFormatada);
                writer.flush();
                writer.close();

            } catch (IOException e) {
                Log.e("LogSettings", "Erro ao escrever no arquivo de log: " + e.getMessage());
            }
        });
    }

    /**
     * Limpa o conteúdo de todos os arquivos de log.
     */
    public static void limparLogs(Context context) {
        if (context == null) return;

        logExecutor.execute(() -> {
            File pastaLogs = new File(context.getExternalFilesDir(null), NOME_PASTA_LOGS);
            if (pastaLogs.exists() && pastaLogs.isDirectory()) {
                File[] arquivos = pastaLogs.listFiles();
                if (arquivos != null) {
                    for (File file : arquivos) {
                        file.delete();
                    }
                }
            }
        });
    }
}
