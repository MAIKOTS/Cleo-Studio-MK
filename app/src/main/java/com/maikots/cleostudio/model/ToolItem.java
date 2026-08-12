package com.maikots.cleostudio.model;

public class ToolItem {
    private String id;
    private int iconeResId; // Guarda R.drawable.ic_...
    private String nome;
    private String descricao;
    private String tag;

    public ToolItem(String id, int iconeResId, String nome, String descricao, String tag) {
        this.id = id;
        this.iconeResId = iconeResId;
        this.nome = nome;
        this.descricao = descricao;
        this.tag = tag;
    }

    public String getId() { return id; }
    public int getIconeResId() { return iconeResId; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public String getTag() { return tag; }
}
