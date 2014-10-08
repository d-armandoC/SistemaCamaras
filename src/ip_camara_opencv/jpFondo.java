package ip_camara_opencv;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.ImageIcon;

public class jpFondo extends javax.swing.JPanel {

    String imagen;

    public jpFondo(String imagen, int ancho, int alto) {
        this.imagen = imagen;
        this.setSize(ancho, alto);
    }

    @Override
    public void paint(Graphics g) {
        Dimension tamanio = getSize();
        ImageIcon imagenFondo = new ImageIcon("C:\\Users\\fabricio\\Desktop\\frames\\" + imagen);
        g.drawImage(imagenFondo.getImage(), 0, 0, tamanio.width, tamanio.height, null);
        setOpaque(false);
        super.paintComponent(g);
    }

}
