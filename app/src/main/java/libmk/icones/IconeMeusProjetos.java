package libmk.icones;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.geom.Path2D;

public class IconeMeusProjetos extends IconeBase {
    public IconeMeusProjetos() { super(); }
    public IconeMeusProjetos(int t, Color c, float e) { super(t, c, e); }

    @Override
    protected void desenhar(Graphics2D g2, int w, int h) {
        // Desenho da Pasta
        Path2D pasta = new Path2D.Double();
        pasta.moveTo(w * 0.1, h * 0.2);
        pasta.lineTo(w * 0.4, h * 0.2); // Aba
        pasta.lineTo(w * 0.5, h * 0.3);
        pasta.lineTo(w * 0.9, h * 0.3); // Topo
        pasta.lineTo(w * 0.9, h * 0.8);
        pasta.lineTo(w * 0.1, h * 0.8);
        pasta.closePath();
        g2.draw(pasta);
    }
}
