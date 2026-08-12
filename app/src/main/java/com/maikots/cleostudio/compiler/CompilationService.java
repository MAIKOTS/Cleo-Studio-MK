package com.maikots.cleostudio.compiler;

import android.content.Context;
import com.maikots.cleostudio.console.ConsoleManager;
import com.maikots.cleostudio.utils.FileManager;

public class CompilationService {

    public static void executar(Context context, String codigo, ConsoleManager consoleManager) {
        if (codigo == null || codigo.trim().isEmpty()) {
            consoleManager.logErro("Erro: Editor de código vazio!");
            return;
        }

        consoleManager.logSucesso("--- INICIANDO COMPILAÇÃO CLEO ANDROID ---");

        CompilerResult resultado = CleoCompiler.compilar(codigo);

        if (resultado.isSucesso()) {
            byte[] bytecode = resultado.getBytecode();
            String extensao = resultado.getExtensao() != null ? resultado.getExtensao() : "csa";
            String nomeArquivo = "script_output." + extensao;

            boolean salvoComSucesso = FileManager.salvarBytecode(context, nomeArquivo, bytecode);

            if (salvoComSucesso) {
                String caminhoPasta = FileManager.getCaminhoPastaScripts(context);
                consoleManager.logSucesso("Compilação concluída com sucesso!\n" +
                        "• Tamanho: " + bytecode.length + " bytes\n" +
                        "• Arquivo: " + nomeArquivo + "\n" +
                        "• Local: " + caminhoPasta);
            } else {
                consoleManager.logErro("Compilação finalizada, mas ocorreu uma falha ao gravar o arquivo ." + extensao + " no armazenamento.");
            }
        } else {
            consoleManager.logErro("Falha na linha " + resultado.getLinhaErro() + ":\n" + resultado.getMensagemErro());
        }
    }
}
