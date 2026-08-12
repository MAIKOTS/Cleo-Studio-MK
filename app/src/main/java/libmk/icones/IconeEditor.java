package libmk.icones;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.geom.Path2D;

public class IconeEditor extends IconeBase {
    public IconeEditor() { super(); }
    public IconeEditor(int t, Color c, float e) { super(t, c, e); }

    @Override
    protected void desenhar(Graphics2D g2, int w, int h) {
        Path2D lapis = new Path2D.Double();
        lapis.moveTo(w * 0.7, h * 0.1);  // Topo/Borracha
        lapis.lineTo(w * 0.9, h * 0.3);
        lapis.lineTo(w * 0.3, h * 0.9);  // Corpo
        lapis.lineTo(w * 0.1, h * 0.9);  // Ponta
        lapis.lineTo(w * 0.1, h * 0.7);
        lapis.closePath();
        g2.draw(lapis);
        
        // Linha perto da ponta
        g2.drawLine((int)(w*0.1), (int)(h*0.7), (int)(w*0.3), (int)(h*0.9));
    }
}
