package libmk.icones;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

public class IconeConfiguracoes extends IconeBase {
    public IconeConfiguracoes() { super(); }
    public IconeConfiguracoes(int t, Color c, float e) { super(t, c, e); }

    @Override
    protected void desenhar(Graphics2D g2, int w, int h) {
        double cx = w / 2.0;
        double cy = h / 2.0;
        double rExt = w * 0.4;
        double rInt = w * 0.2;

        // Desenha o círculo interno
        g2.draw(new Ellipse2D.Double(cx - rInt, cy - rInt, rInt * 2, rInt * 2));

        // Desenha os dentes da engrenagem
        Path2D dentes = new Path2D.Double();
        int numDentes = 8;
        for (int i = 0; i < numDentes; i++) {
            double angulo = Math.toRadians(i * (360.0 / numDentes));
            double xd = cx + Math.cos(angulo) * rExt;
            double yd = cy + Math.sin(angulo) * rExt;
            
            if (i == 0) dentes.moveTo(xd, yd);
            else dentes.lineTo(xd, yd);
        }
        dentes.closePath();
        g2.draw(dentes);
    }
}
