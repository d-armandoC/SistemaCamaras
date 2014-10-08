package ip_camara_opencv;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.ImageIcon;

public class jpFondoVisor extends javax.swing.JPanel {

    ImageIcon imagen;

    public jpFondoVisor(ImageIcon imagen, int ancho, int alto) {
        this.imagen = imagen;
        this.setSize(ancho, alto);
    }

    @Override
    public void paint(Graphics g) {
        Dimension tamanio = getSize();
        ImageIcon imagenFondo = imagen;
        g.drawImage(imagenFondo.getImage(), 0, 0, tamanio.width, tamanio.height, null);
        setOpaque(false);
        super.paintComponent(g);
    }

}
