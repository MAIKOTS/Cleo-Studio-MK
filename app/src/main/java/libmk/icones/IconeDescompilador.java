package libmk.icones;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.geom.Path2D;

public class IconeDescompilador extends IconeBase {
    public IconeDescompilador() { super(); }
    public IconeDescompilador(int t, Color c, float e) { super(t, c, e); }

    @Override
    protected void desenhar(Graphics2D g2, int w, int h) {
        // Tag Aberta '<'
        Path2D tagAbre = new Path2D.Double();
        tagAbre.moveTo(w * 0.35, h * 0.2);
        tagAbre.lineTo(w * 0.10, h * 0.5);
        tagAbre.lineTo(w * 0.35, h * 0.8);
        g2.draw(tagAbre);

        // Barra '/'
        g2.drawLine((int)(w * 0.6), (int)(h * 0.1), (int)(w * 0.4), (int)(h * 0.9));

        // Tag Fecha '>'
        Path2D tagFecha = new Path2D.Double();
        tagFecha.moveTo(w * 0.65, h * 0.2);
        tagFecha.lineTo(w * 0.90, h * 0.5);
        tagFecha.lineTo(w * 0.65, h * 0.8);
        g2.draw(tagFecha);
    }
}
