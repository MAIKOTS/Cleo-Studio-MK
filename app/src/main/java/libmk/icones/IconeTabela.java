package libmk.icones;

import java.awt.Graphics2D;
import java.awt.Color;

public class IconeTabela extends IconeBase {
    public IconeTabela() { super(); }
    public IconeTabela(int t, Color c, float e) { super(t, c, e); }

    @Override
    protected void desenhar(Graphics2D g2, int w, int h) {
        int m = (int)(w * 0.1); // Margem
        g2.drawRect(m, m, w - 2*m, h - 2*m); // Contorno
        
        // Linhas internas
        g2.drawLine(w/2, m, w/2, h-m); // Vertical
        g2.drawLine(m, h/2, w-m, h/2); // Horizontal
    }
}
