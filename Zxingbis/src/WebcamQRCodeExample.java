/*Library of sarxos*/
import com.github.sarxos.webcam.*;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.BasicStroke;

/*Library of java*/
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.swing.JComponent;
import javax.swing.JButton;
import javax.swing.BorderFactory;
/*Library of java*/
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.google.zxing.FormatException;
import com.google.zxing.DecodeHintType;

/*Library of zxing*/
import com.google.zxing.MultiFormatReader;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.oned.Code128Reader;


/*
 * Classe permettant de délimiter la zone à scanner 
 */


class MyCanvas3 extends JPanel {
	
	private static final long serialVersionUID = 1L;

		public JPanel affiche(){
			this.setPreferredSize(new Dimension(640,480));
	        //this.setBackground(new Color(0,0,0,130));
			this.setOpaque(false);
	        return this;
	    }
	 
	    public  void paintComponent(Graphics g) {
	        super.paintComponent(g);      
	        doDrawing(g);
	    }
	//*    
	    private void doDrawing(Graphics g) {
	    
	     Graphics2D g2d = (Graphics2D) g;
	    
	     //Configuration rectangle 
	     Stroke bs1 = new BasicStroke(5.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 20.0f);
	     g2d.setStroke(bs1);
	     g2d.drawRect(210,180,200,100);
	    
	     //Configuration ligne
	     
	     Stroke bs2 = new BasicStroke(1.5f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 20.0f);
	     g2d.setStroke(bs2);
	     g2d.setColor(Color.RED);
	     g2d.drawLine(210,230,410,230);
	     g2d.dispose();
	        
	}
	   //*/	    
}


public class WebcamQRCodeExample extends JFrame implements Runnable, ThreadFactory {
	
	//attributs
		private static final long serialVersionUID = 6441489157408381878L;

		private Executor executor = Executors.newSingleThreadExecutor(this);

		private Webcam webcam = null;
		private WebcamPanel panel = null;
		private JTextArea textarea = null;
		
		//Constructeur de WebcamQRCodeExample 
			/**
			 * 
			 */
			public WebcamQRCodeExample() {
			super();

			setLayout(new FlowLayout());
			setTitle("Read QR / Bar Code With Webcam");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			Dimension size = WebcamResolution.VGA.getSize();

			webcam = Webcam.getWebcams().get(0);
			webcam.setViewSize(size);
			
			// permet d'afficher le flux vidéo dans la fenêtre 
			panel = new WebcamPanel(webcam);
			//panel.setFPSDisplayed(true);
			//panel.setDisplayDebugInfo(true);
			panel.setImageSizeDisplayed(true);
			panel.setMirrored(true);
			//System.out.println(" test 3 : zxing");
			
			//Création zone de texte
			textarea = new JTextArea();
			textarea.setEditable(false);
			textarea.setPreferredSize(size);
	
			// Création de la zone à délimiter  
			panel.add(new MyCanvas3().affiche(),BorderLayout.CENTER);
			
			//Ajout des composants à la fenêtre 
			add(panel);
			add(textarea);
			//add(panel2);
			
			pack();
			this.repaint();
			
			setVisible(true);
			executor.execute(this);

			//panel.add(texture.getImage());
			//,BorderLayout.CENTER);
			//panel.add(rect,BorderLayout.CENTER);
			
			// 
		
			
		}

		@Override
		public void run() {

			do {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Result result = null;
				BufferedImage image = null;
				//BufferedImage image2 = null;
				

				if (webcam.isOpen()) {

					if ((image = webcam.getImage()) == null) {
						continue;
					}
					
					//image = webcam.getImage();

					LuminanceSource source = new BufferedImageLuminanceSource(image);
					BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
				    Hashtable<DecodeHintType,Integer> hints = new Hashtable<DecodeHintType,Integer>();
					
					try {
					
							result = new MultiFormatReader().decode(bitmap);				
		
					} 
					catch (NotFoundException e) {
						// fall thru, it means there is no QR code in image)
					}
				}
		
				
				if (result != null) {
					
					//Fichier son 
					File son = new File("1023.wav");
				
					try{
						AudioClip clip = Applet.newAudioClip(son.toURL());
						clip.play();
						
						}
					
					catch (MalformedURLException e){
							System.out.println(e.getMessage());
					}
					
					//Affichage du texte 
					textarea.setText("Format:"+ result.getBarcodeFormat().getName() +"\n" + "Code: " + result.getText());
				}
				
			} while (true);
		}

		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r, "example-runner");
			t.setDaemon(true);
			return t;
		}

		public static void main(String[] args) {
			new WebcamQRCodeExample();
		}

}
