package com.maikots.cleostudio.cards;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.maikots.cleostudio.adapter.OpcodeAdapter;
import com.maikots.cleostudio.editor.CodeEditorView;

import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OpcodesTableView extends FrameLayout {

    private RecyclerView recyclerView;
    private CodeEditorView editorAlvo;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public OpcodesTableView(Context context) {
        super(context);
        init(context);
    }

    public OpcodesTableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setBackgroundColor(Color.parseColor("#121212"));

        recyclerView = new RecyclerView(context);
        
        // ⚡ OBRIGATÓRIO: Sem isso o RecyclerView causa CRASH instantâneo!
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        
        addView(recyclerView, new LayoutParams(
                LayoutParams.MATCH_PARENT, 
                LayoutParams.MATCH_PARENT
        ));

        carregarDadosAssincrono();
    }

    public void setEditorAlvo(CodeEditorView editor) {
        this.editorAlvo = editor;
    }

    private void carregarDadosAssincrono() {
        new Thread(() -> {
            List<OpcodeAdapter.OpcodeItem> lista = lerOpcodesDoJson(getContext());

            mainHandler.post(() -> {
                OpcodeAdapter adapter = new OpcodeAdapter(lista, opcodeFormatado -> {
                    if (editorAlvo != null) {
                        editorAlvo.inserirTexto(opcodeFormatado);
                    }
                });
                recyclerView.setAdapter(adapter);
            });
        }).start();
    }

    private List<OpcodeAdapter.OpcodeItem> lerOpcodesDoJson(Context context) {
        List<OpcodeAdapter.OpcodeItem> lista = new ArrayList<>();
        try {
            InputStream is = null;
            try {
                is = context.getAssets().open("compilador/opcodes.json");
            } catch (Exception e) {
                try {
                    is = context.getAssets().open("opcodes.json");
                } catch (Exception ignored) {}
            }

            if (is == null) return lista;

            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            String jsonText = new String(buffer, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(jsonText);

            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String nomeComando = keys.next();
                String hexOpcode = jsonObject.getString(nomeComando);
                lista.add(new OpcodeAdapter.OpcodeItem(hexOpcode, nomeComando));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}
