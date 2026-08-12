package libmk.icones;

import javax.swing.Icon;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.BasicStroke;

/**
 * Classe base para todos os ícones desenhados em Java puro.
 */
public abstract class IconeBase implements Icon {
    protected int largura;
    protected int altura;
    protected Color cor;
    protected BasicStroke traço; // Define a espessura das linhas

    /**
     * @param tamanho Tamanho quadrado (largura e altura iguais).
     * @param cor Cor do ícone.
     * @param espessuraLinha Espessura para ícones desenhados com contorno.
     */
    public IconeBase(int tamanho, Color cor, float espessuraLinha) {
        this.largura = tamanho;
        this.altura = tamanho;
        this.cor = cor;
        this.traço = new BasicStroke(espessuraLinha, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    }

    /** Construtor padrão: 24x24, Preto, Linha 2.0f. */
    public IconeBase() {
        this(24, Color.BLACK, 2.0f);
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        
        // Ativa suavização (Antialiasing)
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(cor);
        g2.setStroke(traço);
        
        // Move a origem para a posição onde o ícone deve ser desenhado
        g2.translate(x, y);

        // Chama o desenho específico da subclasse
        desenhar(g2, largura, altura);

        g2.dispose();
    }

    /**
     * Método abstrato onde a geometria do ícone é definida.
     * @param g2 O contexto gráfico configurado.
     * @param w Largura disponível.
     * @param h Altura disponível.
     */
    protected abstract void desenhar(Graphics2D g2, int w, int h);

    @Override
    public int getIconWidth() { return largura; }

    @Override
    public int getIconHeight() { return altura; }
}
