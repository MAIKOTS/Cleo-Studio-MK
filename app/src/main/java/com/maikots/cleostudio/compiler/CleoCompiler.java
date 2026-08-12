package com.maikots.cleostudio.compiler;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CleoCompiler {

    public static CompilerResult compilar(String codigoFonte) {
        if (codigoFonte == null || codigoFonte.trim().isEmpty()) {
            return new CompilerResult("O código fonte está vazio.", 0);
        }

        String[] linhasBrutas = codigoFonte.split("\n");
        List<LinhaCodigo> linhasLimpas = new ArrayList<>();
        Map<String, Integer> tabelaLabels = new HashMap<>();
        String extensaoSaida = "csa";

        int offsetAtual = 0;

        // ==========================================
        // PASSAGEM 1: Mapeamento de Labels e Limpeza
        // ==========================================
        for (int i = 0; i < linhasBrutas.length; i++) {
            String linha = linhasBrutas[i].trim();

            if (linha.isEmpty() || linha.startsWith("//") || linha.startsWith(";")) {
                continue;
            }
            if (linha.contains("//")) {
                linha = linha.substring(0, linha.indexOf("//")).trim();
            }

            if (linha.startsWith("{$") && linha.endsWith("}")) {
                String diretiva = linha.substring(2, linha.length() - 1).trim().toLowerCase();
                if (diretiva.contains(".csi")) extensaoSaida = "csi";
                else if (diretiva.contains(".csa")) extensaoSaida = "csa";
                continue;
            }

            // Definição de Label (ex: :LOOP_PRINCIPAL)
            if (linha.startsWith(":") && !linha.contains(" ")) {
                String nomeLabel = linha.substring(1).toUpperCase();
                tabelaLabels.put(nomeLabel, offsetAtual);
                continue;
            }

            int tamanhoEstimado = estimarTamanhoLinha(linha);
            linhasLimpas.add(new LinhaCodigo(linha, i + 1, offsetAtual));
            offsetAtual += tamanhoEstimado;
        }

        // ==========================================
        // PASSAGEM 2: Geração do Bytecode Binário
        // ==========================================
        ByteArrayOutputStream bufferSaida = new ByteArrayOutputStream();

        for (LinhaCodigo item : linhasLimpas) {
            String linha = item.conteudo;
            int numLinhaOriginal = item.numeroLinha;
            int offsetLinhaAtual = item.offset;

            // Divide comando e argumentos
            String[] partes = linha.split("\\s+", 2);
            String comando = partes[0].toUpperCase().replace(":", "");

            Integer opcode = OpcodeTable.obterOpcode(comando);
            if (opcode == null) {
                if (comando.matches("^[0-9A-FA-f]{4}$")) {
                    opcode = Integer.parseInt(comando, 16);
                } else {
                    return new CompilerResult("Comando ou Opcode inválido: " + comando, numLinhaOriginal);
                }
            }

            // 1. Escreve Opcode (2 bytes Little-Endian)
            bufferSaida.write(opcode & 0xFF);
            bufferSaida.write((opcode >> 8) & 0xFF);

            // 2. Processa Argumentos
            if (partes.length > 1) {
                String strArgs = partes[1].trim();
                
                Pattern regex = Pattern.compile("\"[^\"]*\"|'[^']*'|\\S+");
                Matcher matcher = regex.matcher(strArgs);

                while (matcher.find()) {
                    String arg = matcher.group().trim();
                    if (arg.endsWith(",")) {
                        arg = arg.substring(0, arg.length() - 1).trim();
                    }

                    // Consulta o JSON para verificar palavras cosméticas
                    if (CompilerConfig.isPalavraIgnorada(arg)) {
                        continue;
                    }

                    if (arg.isEmpty()) continue;

                    processarArgumento(bufferSaida, arg, tabelaLabels, offsetLinhaAtual, opcode);
                }
            }
        }

        return new CompilerResult(bufferSaida.toByteArray(), extensaoSaida);
    }

    private static void processarArgumento(ByteArrayOutputStream out, String arg, Map<String, Integer> tabelaLabels, int offsetAtual, int opcode) {
        // A. Pulo para Labels
        if (arg.startsWith(":") || arg.startsWith("@")) {
            String nomeLabel = arg.substring(1).toUpperCase();
            int destino = tabelaLabels.containsKey(nomeLabel) ? tabelaLabels.get(nomeLabel) : 0;
            int offsetRelativo = destino - (offsetAtual + 2);
            out.write(0x01); // DataType: Int32 Offset
            escreverIntLittleEndian(out, offsetRelativo);
            return;
        }

        // B. Variáveis Locais (0@, 1@) e Locais Float (0@f)
        if (arg.matches("^\\d+@[fF]?$")) {
            boolean isFloat = arg.toLowerCase().endsWith("f");
            String numStr = arg.replaceAll("(?i)@[f]?", "");
            int numVar = Integer.parseInt(numStr);
            out.write(isFloat ? 0x08 : 0x07);
            escreverShortLittleEndian(out, (short) numVar);
            return;
        }

        // C. Variáveis Globais ($PLAYER_ACTOR, $100)
        if (arg.startsWith("$")) {
            String varGlobal = arg.substring(1);
            int offsetGlobal = 2;
            if (varGlobal.matches("^\\d+$")) {
                offsetGlobal = Integer.parseInt(varGlobal);
            }
            out.write(0x02);
            escreverShortLittleEndian(out, (short) offsetGlobal);
            return;
        }

        // D. Strings entre Aspas ("TEXTO")
        if (arg.startsWith("\"") && arg.endsWith("\"")) {
            String texto = arg.substring(1, arg.length() - 1);
            byte[] bytesTexto = texto.getBytes(StandardCharsets.UTF_8);

            out.write(0x0E); // DataType: String Formatada/GXT
            out.write(bytesTexto.length + 1); // Tamanho + Terminação NUL
            try {
                out.write(bytesTexto);
                out.write(0x00); // NUL terminator
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Consulta o JSON para saber se o opcode exige o terminador DT_END
            if (CompilerConfig.isOpcodeFormatado(opcode)) {
                out.write(0x00);
            }
            return;
        }

        // E. Números Floats (100.0, 0.5)
        if (arg.matches("^-?\\d+\\.\\d+f?$")) {
            float valFloat = Float.parseFloat(arg.replace("f", "").replace("F", ""));
            out.write(0x06);
            escreverFloatLittleEndian(out, valFloat);
            return;
        }

        // F. Números Inteiros
        if (arg.matches("^-?\\d+$")) {
            long valor = Long.parseLong(arg);
            // Consulta o JSON se exige Int32 obrigatoriamente
            if (CompilerConfig.isOpcodeInt32Obrigatorio(opcode) || valor > 32767 || valor < -32768) {
                out.write(0x01); // Int32
                escreverIntLittleEndian(out, (int) valor);
            } else if (valor >= -128 && valor <= 127) {
                out.write(0x04); // Int8
                out.write((byte) valor);
            } else {
                out.write(0x05); // Int16
                escreverShortLittleEndian(out, (short) valor);
            }
        }
    }

    private static int estimarTamanhoLinha(String linha) {
        int tamanho = 2;
        String[] partes = linha.split("\\s+", 2);
        if (partes.length > 1) {
            Pattern regex = Pattern.compile("\"[^\"]*\"|'[^']*'|\\S+");
            Matcher matcher = regex.matcher(partes[1]);
            while (matcher.find()) {
                String arg = matcher.group().trim();
                if (CompilerConfig.isPalavraIgnorada(arg)) continue;

                if (arg.startsWith(":") || arg.startsWith("@")) tamanho += 5;
                else if (arg.matches("^\\d+@[fF]?$")) tamanho += 3;
                else if (arg.startsWith("$")) tamanho += 3;
                else if (arg.matches("^-?\\d+\\.\\d+f?$")) tamanho += 5;
                else if (arg.startsWith("\"")) tamanho += 3 + (arg.length() - 2);
                else tamanho += 5;
            }
        }
        return tamanho;
    }

    private static void escreverShortLittleEndian(ByteArrayOutputStream out, short valor) {
        ByteBuffer bb = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN);
        bb.putShort(valor);
        out.write(bb.array(), 0, 2);
    }

    private static void escreverIntLittleEndian(ByteArrayOutputStream out, int valor) {
        ByteBuffer bb = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt(valor);
        out.write(bb.array(), 0, 4);
    }

    private static void escreverFloatLittleEndian(ByteArrayOutputStream out, float valor) {
        ByteBuffer bb = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
        bb.putFloat(valor);
        out.write(bb.array(), 0, 4);
    }

    private static class LinhaCodigo {
        final String conteudo;
        final int numeroLinha;
        final int offset;

        LinhaCodigo(String conteudo, int numeroLinha, int offset) {
            this.conteudo = conteudo;
            this.numeroLinha = numeroLinha;
            this.offset = offset;
        }
    }
}
