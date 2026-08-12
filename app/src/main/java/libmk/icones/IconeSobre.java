package libmk.icones;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.geom.Ellipse2D;

public class IconeSobre extends IconeBase {
    public IconeSobre() { super(); }
    public IconeSobre(int t, Color c, float e) { super(t, c, e); }

    @Override
    protected void desenhar(Graphics2D g2, int w, int h) {
        // Círculo externo
        g2.draw(new Ellipse2D.Double(w*0.1, h*0.1, w*0.8, h*0.8));
        
        // Ponto do 'i' (Círculo preenchido)
        g2.fillOval((int)(w*0.45), (int)(h*0.25), (int)(w*0.1), (int)(h*0.1));
        
        // Corpo do 'i' (Linha grossa)
        g2.drawLine((int)(w*0.5), (int)(h*0.45), (int)(w*0.5), (int)(h*0.7));
    }
}
