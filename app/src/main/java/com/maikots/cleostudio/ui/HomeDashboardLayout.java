package com.maikots.cleostudio.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.maikots.cleostudio.R;
import com.maikots.cleostudio.adapter.ToolAdapter;
import com.maikots.cleostudio.model.ToolItem;
import com.maikots.cleostudio.ui.menu.PopUpDashboardMenu;

import java.util.ArrayList;
import java.util.List;

public class HomeDashboardLayout extends LinearLayout {

    public interface OnToolSelectedListener {
        void onToolSelected(String toolId);
    }

    public interface OnOptionSelectedListener {
        void onOptionSelected(String opcao);
    }

    private OnOptionSelectedListener optionListener;

    public HomeDashboardLayout(Context context, OnToolSelectedListener toolListener) {
        super(context);
        setOrientation(VERTICAL);
        setBackgroundColor(Color.parseColor("#121212"));
        setPadding(24, 24, 24, 24);

        // 1. Infla o Header XML com o Ícone + Título + Botão
        View headerView = LayoutInflater.from(context).inflate(R.layout.layout_dashboard_header, this, false);
        ImageButton btnOpcoes = headerView.findViewById(R.id.btnOpcoesHeader);

        // ⚡ Chama o Menu PopUp através da classe delegada no pacote ui.menu
        btnOpcoes.setOnClickListener(v -> PopUpDashboardMenu.exibir(context, v, opcao -> {
            if (optionListener != null) {
                optionListener.onOptionSelected(opcao);
            }
        }));

        addView(headerView);

        // 2. Lista de Cards (RecyclerView)
        RecyclerView rvFerramentas = new RecyclerView(context);
        rvFerramentas.setLayoutManager(new LinearLayoutManager(context));
        rvFerramentas.setClipToPadding(false);

        ToolAdapter adapter = new ToolAdapter(obterListaFerramentas(), item -> {
            if (toolListener != null && item != null) {
                toolListener.onToolSelected(item.getId());
            }
        });

        rvFerramentas.setAdapter(adapter);
        addView(rvFerramentas, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public void setOnOptionSelectedListener(OnOptionSelectedListener listener) {
        this.optionListener = listener;
    }

    private List<ToolItem> obterListaFerramentas() {
        List<ToolItem> lista = new ArrayList<>();
        lista.add(new ToolItem("editor", R.drawable.ic_editor, "Editor de Scripts", "Ambiente completo para criação e edição de scripts CLEO com realce de sintaxe.", "CSA/CSI/S"));
        lista.add(new ToolItem("descompilador", R.drawable.ic_decompiler, "Descompilador", "Converta seus arquivos .csa ou .cs compilados de volta para código legível.", "DECOMPILE"));
        lista.add(new ToolItem("opcodes", R.drawable.ic_opcodes, "Tabela de Opcodes", "Consulte a biblioteca completa de opcodes diretamente do catálogo em JSON.", "DOCS"));
        lista.add(new ToolItem("projetos", R.drawable.ic_projects, "Meus Projetos", "Gerencie o armazenamento e a estrutura dos seus scripts e projetos salvos.", "FILES"));
        return lista;
    }
}
