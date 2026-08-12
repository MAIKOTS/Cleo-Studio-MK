package libmk.icones;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.geom.Path2D;

public class IconeCreditos extends IconeBase {
    public IconeCreditos() { super(); }
    public IconeCreditos(int t, Color c, float e) { super(t, c, e); }

    @Override
    protected void desenhar(Graphics2D g2, int w, int h) {
        // Estrela de 5 pontas
        double cx = w / 2.0;
        double cy = h / 2.0;
        double rExt = w * 0.45;
        double rInt = rExt * 0.45;

        Path2D estrela = new Path2D.Double();
        for (int i = 0; i < 10; i++) {
            double r = (i % 2 == 0) ? rExt : rInt;
            double angulo = Math.toRadians(i * 36 - 90);
            double x = cx + Math.cos(angulo) * r;
            double y = cy + Math.sin(angulo) * r;
            if (i == 0) estrela.moveTo(x, y);
            else estrela.lineTo(x, y);
        }
        estrela.closePath();
        g2.draw(estrela);
    }
}
