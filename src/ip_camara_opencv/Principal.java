package ip_camara_opencv;

import java.io.File;
import com.sun.imageio.plugins.jpeg.JPEGImageReader;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.JDialog;
//import javax.sound.midi.SysexMessage;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.SwingConstants;
//import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.FileFilter;
import javaanpr.Main;
import javaanpr.gui.tools.FileListModel;
import javaanpr.gui.tools.ImageFileFilter;
import javaanpr.imageanalysis.CarSnapshot;
import javaanpr.imageanalysis.Photo;
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
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Image;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaanpr.intelligence.Intelligence;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.omg.CORBA.Environment;

public class Principal extends javax.swing.JFrame {

    DefaultListModel listModel;
    GregorianCalendar gcHora1;
    IplImage grabbedImage;
    Thread hilo_initCamara;
    Thread hilo_initVisualizacion;
    boolean guardar;
    CvFont font;
    Tesseract instance = Tesseract.getInstance();

    public class RecognizeThread extends Thread {

        Principal parentFrame = null;

        public RecognizeThread(Principal parentFrame) {
          
            this.parentFrame = parentFrame;
        }

        public void run() {
           String recognizedText = "";
            this.parentFrame.recognitionLabel.setText("processing ...");
           // int index = this.parentFrame.selectedIndex;
            try {
              recognizedText = Main.systemLogic.recognize(this.parentFrame.car);
            } catch (Exception ex) {
                this.parentFrame.recognitionLabel.setText("error");
                return;
            }
            this.parentFrame.recognitionLabel.setText(recognizedText);
            //this.parentFrame.fileListModel.fileList.elementAt(index).recognizedPlate = recognizedText;
        }
    }

    public class LoadImageThread extends Thread {

        Principal parentFrame = null;
        String url = null;

        public LoadImageThread(Principal parentFrame, String url) {
            this.parentFrame = parentFrame;
            this.url = url;
        }

        public void run() {
//            try {
//                this.parentFrame.car = new CarSnapshot(url);
//                this.parentFrame.panelCarContent = this.parentFrame.car.duplicate().getBi();
//                this.parentFrame.panelCarContent = Photo.linearResizeBi(this.parentFrame.panelCarContent,
//                        this.parentFrame.panelCar.getWidth(),
//                        this.parentFrame.panelCar.getHeight());
//                this.parentFrame.panelCar.paint(this.parentFrame.panelCar.getGraphics());
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
        }
    }

    CarSnapshot car;
    BufferedImage panelCarContent;

    JFileChooser fileChooser;
    private FileListModel fileListModel;
    int selectedIndex = -1;

    public Principal() {
        initComponents();
        try {
            Main.systemLogic = new Intelligence(false);
        } catch (Exception ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.fileChooser = new JFileChooser();
        this.fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        this.fileChooser.setFileFilter(new ImageFileFilter());

        // init : window dimensions and visibility
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = this.getWidth();
        int height = this.getHeight();
        this.setLocation((screenSize.width - width) / 2, (screenSize.height - height) / 2);
        this.setVisible(true);

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

                // CvCapture capture = opencv_highgui.cvCreateCameraCapture(0);
                CvCapture capture = opencv_highgui.cvCreateFileCapture("rtsp://admin:12345@192.168.10.150:554//Streaming/Channels/1");
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

                    CvFont font1 = new CvFont(20);
                    cvInitFont(font1, 2, 6.9, 1.0, 1.3, 5, 5);
                    //   cvPutText(img_temporal, retornaHora(), cvPoint(10, 70), font1, CvScalar.WHITE);
                    cvPutText(img_temporal, retornaFecha(), cvPoint(10, 145), font1, CvScalar.RED);
                    ImageIcon imagen = new ImageIcon(img_temporal.getBufferedImage());
                    ImageIcon fot = new ImageIcon(img_temporal.getBufferedImage());
                    Icon icono = new ImageIcon(fot.getImage().getScaledInstance(jlimg.getWidth(), jlimg.getHeight(), Image.SCALE_DEFAULT));
                    jlimg.setIcon(icono);

                    try {
                        String result = instance.doOCR(img_temporal.getBufferedImage());
                        jlplaca.setText(result);
                        System.out.println(result);
                    } catch (TesseractException e) {
                        System.err.println(e.getMessage());
                    }

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
        jScrollPane1 = new javax.swing.JScrollPane();
        jlistanombres = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jtxttiempo = new javax.swing.JTextField();
        btnsalir = new javax.swing.JButton();
        jlnumero_capturas = new javax.swing.JLabel();
        jlplaca = new javax.swing.JLabel();
        recognizeButton = new javax.swing.JButton();
        recognitionLabel = new javax.swing.JLabel();
        jlimg = new javax.swing.JLabel();
        jlimgselect = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        imageMenu = new javax.swing.JMenu();
        openDirectoryItem = new javax.swing.JMenuItem();
        exitItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        aboutItem = new javax.swing.JMenuItem();
        helpItem = new javax.swing.JMenuItem();

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
        jlistanombres.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jlistanombresValueChanged(evt);
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

        jlplaca.setText("placa");

        recognizeButton.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        recognizeButton.setText("DETECTAR PLACA");
        recognizeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recognizeButtonActionPerformed(evt);
            }
        });

        recognitionLabel.setBackground(new java.awt.Color(0, 0, 0));
        recognitionLabel.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        recognitionLabel.setForeground(new java.awt.Color(255, 204, 51));
        recognitionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        recognitionLabel.setText("null");
        recognitionLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        recognitionLabel.setOpaque(true);

        jLabel2.setText("VIDEO ACTUAL");

        jLabel3.setText("CAPTURA SELECCIONADA");

        menuBar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        imageMenu.setText("Image");
        imageMenu.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        openDirectoryItem.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        openDirectoryItem.setText("Load snapshots from directory");
        openDirectoryItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openDirectoryItemActionPerformed(evt);
            }
        });
        imageMenu.add(openDirectoryItem);

        exitItem.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        exitItem.setText("Exit");
        exitItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitItemActionPerformed(evt);
            }
        });
        imageMenu.add(exitItem);

        menuBar.add(imageMenu);

        helpMenu.setText("Help");
        helpMenu.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        helpMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpMenuActionPerformed(evt);
            }
        });

        aboutItem.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        aboutItem.setText("About");
        aboutItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutItem);

        helpItem.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        helpItem.setText("Help");
        helpItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpItemActionPerformed(evt);
            }
        });
        helpMenu.add(helpItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

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
                    .addComponent(jlnumero_capturas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlplaca, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jlimg, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jlimgselect, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(recognitionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 257, Short.MAX_VALUE)
                        .addComponent(jLabel3)
                        .addGap(338, 338, 338))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(recognizeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jlimg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jlimgselect, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(recognitionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(recognizeButton))
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jlplaca, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlnumero_capturas, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        } else {
            if (evt.getClickCount() == 1) {
                String nameimg=(String) jlistanombres.getSelectedValue();
                System.out.println(nameimg);
              ImageIcon fot = new ImageIcon("C:\\Users\\fabricio\\Desktop\\frames\\" +nameimg );
                    Icon icono = new ImageIcon(fot.getImage().getScaledInstance(jlimg.getWidth(), jlimg.getHeight(), Image.SCALE_DEFAULT));
                    jlimgselect.setIcon(icono);
                try {
                    //       ImageIcon imagen = new ImageIcon("C:\\Users\\fabricio\\Desktop\\frames\\" + (String) jlistanombres.getSelectedValue());
                   
                    this.car = new CarSnapshot("C:\\Users\\fabricio\\Desktop\\frames\\" +nameimg);
                } catch (IOException ex) {
                    System.out.println("ERROR AL CARGAR IMAGEN");
                    Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }//GEN-LAST:event_jlistanombresMouseClicked

    private void recognizeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recognizeButtonActionPerformed
        String plate = null;

        // namiesto tohto urobime thread plate = Main.systemLogic.recognize(this.car);
        // thread code start
        new RecognizeThread(this).start();
        // thread code end

        //            this.fileListModel.fileList.elementAt(this.selectedIndex).recognizedPlate = plate;
        //            this.label.setText(plate);

    }//GEN-LAST:event_recognizeButtonActionPerformed

    private void openDirectoryItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openDirectoryItemActionPerformed
        int returnValue;
        String fileURL;

        this.fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        this.fileChooser.setDialogTitle("Load snapshots from directory");
        returnValue = this.fileChooser.showOpenDialog((Component) evt.getSource());

        if (returnValue != this.fileChooser.APPROVE_OPTION) {
            return;
        }

        fileURL = this.fileChooser.getSelectedFile().getAbsolutePath();
        File selectedFile = new File(fileURL);

        this.fileListModel = new FileListModel();
        for (String fileName : selectedFile.list()) {
            if (!ImageFileFilter.accept(fileName)) {
                continue; // not a image
            }
            this.fileListModel.addFileListModelEntry(fileName, selectedFile + File.separator + fileName);
        }
        this.jlistanombres.setModel(fileListModel);
      //  this.fileList.setModel(fileListModel);

    }//GEN-LAST:event_openDirectoryItemActionPerformed

    private void exitItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitItemActionPerformed

    private void aboutItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutItemActionPerformed
        //   new FrameHelp(FrameHelp.SHOW_ABOUT);
    }//GEN-LAST:event_aboutItemActionPerformed

    private void helpItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpItemActionPerformed
        // new FrameHelp(FrameHelp.SHOW_HELP);
    }//GEN-LAST:event_helpItemActionPerformed

    private void helpMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpMenuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_helpMenuActionPerformed

    private void jlistanombresValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jlistanombresValueChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_jlistanombresValueChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutItem;
    private javax.swing.JButton btniniciar;
    private javax.swing.JButton btnsalir;
    private javax.swing.JMenuItem exitItem;
    private javax.swing.JMenuItem helpItem;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenu imageMenu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbtncapturar;
    private javax.swing.JLabel jlimg;
    private javax.swing.JLabel jlimgselect;
    private javax.swing.JList jlistanombres;
    private javax.swing.JLabel jlnumero_capturas;
    private javax.swing.JLabel jlplaca;
    private javax.swing.JTextField jtxttiempo;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem openDirectoryItem;
    private javax.swing.JLabel recognitionLabel;
    private javax.swing.JButton recognizeButton;
    // End of variables declaration//GEN-END:variables
}
