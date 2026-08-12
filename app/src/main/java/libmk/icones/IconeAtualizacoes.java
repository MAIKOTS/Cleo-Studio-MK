package libmk.icones;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;

public class IconeAtualizacoes extends IconeBase {
    public IconeAtualizacoes() { super(); }
    public IconeAtualizacoes(int t, Color c, float e) { super(t, c, e); }

    @Override
    protected void desenhar(Graphics2D g2, int w, int h) {
        // Arco de círculo
        g2.draw(new Arc2D.Double(w*0.1, h*0.1, w*0.8, h*0.8, 40, 280, Arc2D.OPEN));
        
        // Cabeça da seta
        Path2D seta = new Path2D.Double();
        seta.moveTo(w * 0.8, h * 0.2);
        seta.lineTo(w * 0.95, h * 0.4);
        seta.lineTo(w * 0.65, h * 0.4);
        seta.closePath();
        
        g2.fill(seta); // Preenche a cabeça da seta
    }
}
