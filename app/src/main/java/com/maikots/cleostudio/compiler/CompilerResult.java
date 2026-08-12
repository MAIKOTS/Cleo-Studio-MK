package com.maikots.cleostudio.compiler;

public class CompilerResult {
    private final boolean sucesso;
    private final byte[] bytecode;
    private final String mensagemErro;
    private final int linhaErro;
    private final String extensao;

    public CompilerResult(byte[] bytecode, String extensao) {
        this.sucesso = true;
        this.bytecode = bytecode;
        this.mensagemErro = null;
        this.linhaErro = -1;
        this.extensao = extensao;
    }

    public CompilerResult(String mensagemErro, int linhaErro) {
        this.sucesso = false;
        this.bytecode = null;
        this.mensagemErro = mensagemErro;
        this.linhaErro = linhaErro;
        this.extensao = "csa";
    }

    public boolean isSucesso() { return sucesso; }
    public byte[] getBytecode() { return bytecode; }
    public String getMensagemErro() { return mensagemErro; }
    public int getLinhaErro() { return linhaErro; }
    public String getExtensao() { return extensao; }
}
