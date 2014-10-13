package ip_camara_opencv;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author fabricio
 */
import java.io.File;
import com.sun.imageio.plugins.jpeg.JPEGImageReader;
import net.sourceforge.tess4j.*;

public class Tess4j {

    /**
     * @param args the command line arguments
     */
     public static void main(String[] args) {
        File imageFile = new File("C:\\Users\\fabricio\\Pictures\\placa4.jpg");
        
      // Tesseract instance = Tesseract.getInstance();  // JNA Interface Mapping
        Tesseract1 instance = new Tesseract1(); // JNA Direct Mapping
       try {
           String result = instance.doOCR(imageFile);
           System.out.println(result);
      } catch (TesseractException e) {
            System.err.println(e.getMessage());
      }
    }
    
}
