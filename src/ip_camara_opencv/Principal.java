package ip_camara_opencv;

import static com.googlecode.javacv.cpp.opencv_objdetect.*;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;
import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core;
import static com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvInRangeS;
import com.googlecode.javacv.cpp.opencv_highgui;
import static com.googlecode.javacv.cpp.opencv_core.cvScalar;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_MEDIAN;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvSmooth;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_GAUSSIAN;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_core.cvScalar;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_GAUSSIAN;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_MEDIAN;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvSmooth;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_THRESH_BINARY_INV;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_ADAPTIVE_THRESH_MEAN_C;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RETR_CCOMP;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_CHAIN_APPROX_NONE;
//import static com.googlecode.javacv.cpp.opencv_imgproc.CV_FILLED;
import java.awt.Color;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvBoundingRect;
import java.awt.Canvas;
import java.awt.Image;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;
import org.omg.CORBA.Environment;

public class Principal extends javax.swing.JFrame {

    DefaultListModel listModel;
    GregorianCalendar gcHora1;
    IplImage grabbedImage;
    Thread hilo_initCamara;
    Thread hilo_initVisualizacion;
    boolean guardar;
    CvFont font;

    public Principal() {
        initComponents();
        initRecursos();
        initCamara();
    }

    private void initRecursos() {
        
       //jbtncapturar.setEnabled(false);
        this.setLocationRelativeTo(null);
        this.listModel = new DefaultListModel();
        this.gcHora1 = new GregorianCalendar();
        this.grabbedImage = null;
        this.guardar = false;
        this.font = null;
        this.jtxttiempo.setText("" + 5);
    }

    private void initCamara() {
        hilo_initCamara = new Thread() {
            public void run() {
                CvCapture capture = opencv_highgui.cvCreateCameraCapture(0);
                //CvCapture capture = opencv_highgui.cvCreateFileCapture("rtsp://admin:12345@192.168.10.150:554//Streaming/Channels/1");
                //  opencv_highgui.cvSetCaptureProperty(capture, opencv_highgui.CV_CAP_PROP_FRAME_HEIGHT, 300);
                // opencv_highgui.cvSetCaptureProperty(capture, opencv_highgui.CV_CAP_PROP_FRAME_WIDTH, 200);
                grabbedImage = opencv_highgui.cvQueryFrame(capture);
                while ((grabbedImage = opencv_highgui.cvQueryFrame(capture)) != null) {
                }
            }
        };
        hilo_initCamara.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
           // Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
      //  jbtncapturar.setEnabled(true);
    }

    private void salir() {
        System.exit(0);
    }

    private void iniciarVisualizacion() {
        hilo_initVisualizacion = new Thread() {

            public void run() {
                int tiempo = 0;
                int numero_capturas = 0;
                String hora, fecha;
                while (grabbedImage != null) {
                    GregorianCalendar gcHora1 = new GregorianCalendar();
                    IplImage img_temporal = grabbedImage;
                    hora = gcHora1.get(Calendar.HOUR) + ":" + gcHora1.get(Calendar.MINUTE) + ":" + gcHora1.get(Calendar.SECOND) + ":" + gcHora1.get(Calendar.MILLISECOND);
                    fecha = gcHora1.get(Calendar.YEAR) + "/" + gcHora1.get(Calendar.MONTH) + "/" + gcHora1.get(Calendar.DATE);
                    if (gcHora1.get(Calendar.AM_PM) == 0) {                                                                                      //retorno de calendar.AM_PM
                        hora = hora + " am";
                    } else {
                        hora = hora + " pm";
                    }

                    CvFont font1 = new CvFont(5);
                    cvInitFont(font1, 1, 6.9, 1.0, 1.3, 5, 5);
                    cvPutText(img_temporal, retornaHora(), cvPoint(10, 70), font1, CvScalar.WHITE);
                    cvPutText(img_temporal, retornaFecha(), cvPoint(10, 145), font1, CvScalar.WHITE);
                    ImageIcon imagen = new ImageIcon(img_temporal.getBufferedImage());
                    ImageIcon fot = new ImageIcon(img_temporal.getBufferedImage());
                    Icon icono = new ImageIcon(fot.getImage().getScaledInstance(jlimg.getWidth(), jlimg.getHeight(), Image.SCALE_DEFAULT));
                    jlimg.setIcon(icono);
                    String nombre_img;
                    if (guardar == true) {
                        int tiempo_ingresado = (Integer.parseInt(jtxttiempo.getText())) * 1000;
                        if (tiempo < tiempo_ingresado) {
                            nombre_img = asignaNombreImg();
                            cvSaveImage("C:\\Users\\fabricio\\Desktop\\frames\\" + nombre_img, grabbedImage);
                            listModel.addElement(nombre_img);
                            jlistanombres.setModel(listModel);
                            tiempo = tiempo + 25;
                            numero_capturas++;
                            jlnumero_capturas.setText("Número de capturas:" + numero_capturas);
                        } else {
                            tiempo = 0;
                            guardar = false;
                            jbtncapturar.setEnabled(true);

                            numero_capturas = 0;
                        }
                    }
                    try {
                        Thread.sleep(25);
                        //  jtxttiempo.setText("");
                    } catch (InterruptedException ex) {

                    }

                }
            }
        };
        hilo_initVisualizacion.start();
    }

    public String retornaHora() {
        String hora;
        GregorianCalendar gcHora1 = new GregorianCalendar();
        IplImage img_temporal = grabbedImage;
        hora = gcHora1.get(Calendar.HOUR) + ":" + gcHora1.get(Calendar.MINUTE) + ":" + gcHora1.get(Calendar.SECOND) + ":" + gcHora1.get(Calendar.MILLISECOND);
        if (gcHora1.get(Calendar.AM_PM) == 0) {                                                                                      //retorno de calendar.AM_PM
            hora = hora + " am";
        } else {
            hora = hora + " pm";
        }
        return hora;
    }

    public String retornaFecha() {
        String fecha;
        GregorianCalendar gcHora1 = new GregorianCalendar();
        fecha = gcHora1.get(Calendar.YEAR) + "/" + gcHora1.get(Calendar.MONTH) + "/" + gcHora1.get(Calendar.DATE);
        return fecha;
    }

    public String asignaNombreImg() {
        String nombre_img;
        GregorianCalendar gcHora = new GregorianCalendar();
        nombre_img = gcHora.get(Calendar.YEAR) + "_"
                + gcHora.get(Calendar.MONTH) + "_"
                + gcHora.get(Calendar.DATE) + "_"
                + gcHora.get(Calendar.HOUR) + "_"
                + gcHora.get(Calendar.MINUTE) + "_"
                + gcHora.get(Calendar.SECOND) + "_"
                + gcHora.get(Calendar.MILLISECOND) + "_";
        if (gcHora.get(Calendar.AM_PM) == 0) {                                                                                      //retorno de calendar.AM_PM
            nombre_img = nombre_img + "am";
        } else {
            nombre_img = nombre_img + "pm";
        }
        nombre_img = nombre_img + ".jpg";
        return nombre_img;
    }

    @SuppressWarnings("unchecked")

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btniniciar = new javax.swing.JButton();
        jbtncapturar = new javax.swing.JButton();
        jlimg = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jlistanombres = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jtxttiempo = new javax.swing.JTextField();
        btnsalir = new javax.swing.JButton();
        jlnumero_capturas = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btniniciar.setText("INICIAR");
        btniniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btniniciarActionPerformed(evt);
            }
        });

        jbtncapturar.setText("CAPTURAR");
        jbtncapturar.setEnabled(false);
        jbtncapturar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtncapturarActionPerformed(evt);
            }
        });

        jlistanombres.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jlistanombresMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jlistanombres);

        jLabel1.setText("tiempo de captura:");

        jtxttiempo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxttiempoActionPerformed(evt);
            }
        });

        btnsalir.setText("SALIR");
        btnsalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnsalirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jtxttiempo, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnsalir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addComponent(btniniciar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jbtncapturar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlnumero_capturas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(66, 66, 66)
                .addComponent(jlimg, javax.swing.GroupLayout.PREFERRED_SIZE, 597, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(btniniciar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtncapturar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnsalir)
                .addGap(1, 1, 1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jtxttiempo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlnumero_capturas, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
            .addComponent(jlimg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

   // static CvScalar min = cvScalar(0, 15, 5, 0);//BGR-A
    //static CvScalar max = cvScalar(137, 22, 10, 0);//BGR-A

    private void btniniciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btniniciarActionPerformed
        this.iniciarVisualizacion();
        btniniciar.setEnabled(false);
        jbtncapturar.setEnabled(true);
    }//GEN-LAST:event_btniniciarActionPerformed
    private void jbtncapturarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtncapturarActionPerformed
        listModel.removeAllElements();
        jlistanombres.setModel(listModel);
        if (this.guardar) {
            this.guardar = false;
            jbtncapturar.setText("CAPTURAR");
        } else {
            this.guardar = true;
            jbtncapturar.setEnabled(false);
        }
    }//GEN-LAST:event_jbtncapturarActionPerformed

    private void jtxttiempoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxttiempoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxttiempoActionPerformed

    private void btnsalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsalirActionPerformed
        this.salir();
    }//GEN-LAST:event_btnsalirActionPerformed

    private void jlistanombresMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlistanombresMouseClicked
        if (evt.getClickCount() == 2) {
            new Visor((String) jlistanombres.getSelectedValue()).setVisible(true);
        }
    }//GEN-LAST:event_jlistanombresMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btniniciar;
    private javax.swing.JButton btnsalir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbtncapturar;
    private javax.swing.JLabel jlimg;
    private javax.swing.JList jlistanombres;
    private javax.swing.JLabel jlnumero_capturas;
    private javax.swing.JTextField jtxttiempo;
    // End of variables declaration//GEN-END:variables
}
