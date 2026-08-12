package libmk.icones;

import java.awt.Graphics2D;
import java.awt.Color;

public class IconeMenu extends IconeBase {
    public IconeMenu() { super(); }
    public IconeMenu(int t, Color c, float e) { super(t, c, e); }

    @Override
    protected void desenhar(Graphics2D g2, int w, int h) {
        // Três linhas horizontais
        int margem = (int)(w * 0.15);
        int largLinha = w - (margem * 2);
        
        g2.drawLine(margem, (int)(h * 0.25), margem + largLinha, (int)(h * 0.25));
        g2.drawLine(margem, (int)(h * 0.50), margem + largLinha, (int)(h * 0.50));
        g2.drawLine(margem, (int)(h * 0.75), margem + largLinha, (int)(h * 0.75));
    }
}
