package sampler;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

import sampler.Instrument;

public class Sampler {
	
	public static String BACKGROUND_PATH = "res/images/background.jpg";
	public static String DEFAULT_INSTRUMENT = "res/instruments/fender-rhodes";
	public static String TITLE = "SAMPLER ";
	public static int NUMBER_OF_INSTRUMENTS = 2;
	public static int MARGIN = 20;
	public static int LABEL_TEXT_SIZE = 20;
	public static int TITLE_TEXT_SIZE = 40;
	public static int TITLE_HEIGHT = 40;
	public static int BUTTON_SIZE = 50;
	public static int WINDOW_WIDTH = 400;
	public static int WINDOW_HEIGHT = 250;
	public enum NOTE{C ,Db ,D ,E ,Eb ,F ,Gb ,G ,A , Ab, B, Bb};
	private HashMap<NOTE,Clip> samples = new HashMap<>();
	public static final int NUM_NOTES = 12;
	public JFrame window;
	public JLabel currentInstrument;
	
	public static void main(String[] args) {
		
		Sampler sampler = new Sampler();
	}
	
	public Sampler() {
		
		makeWindow();
		initialiseSamples(DEFAULT_INSTRUMENT);
		
	}
	
	public void makeWindow() {
		window = new JFrame();
	

			try {
				window.setContentPane(new ImagePanel(ImageIO.read(new File(BACKGROUND_PATH))));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		
		window.addKeyListener(new KeyListener(){
			@Override
			public void keyTyped(KeyEvent e) {
				Clip clip = samples.get(charToNote(e.getKeyChar())); 
				if(!clip.isActive()) {
					playClip(charToNote(e.getKeyChar()));
				}
			}
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub	
			}
			@Override
			public void keyReleased(KeyEvent e) {
				Clip clip = samples.get(charToNote(e.getKeyChar()));
				//fadeOutClip(clip);
				clip.stop();
				clip.setFramePosition(0);
			}
			
		});
		window.addWindowListener(new WindowListener() {
			
			public void windowOpened(WindowEvent e) {}
			@Override
			public void windowClosing(WindowEvent e) {
				for(NOTE note: NOTE.values()) {
					try {
						samples.get(note).close();
					}
					catch(Exception ex) {
						
					}
				}
				
			}
			public void windowClosed(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
		});
		currentInstrument = new JLabel("Currently playing a : rhodes",SwingConstants.CENTER);
		currentInstrument.setBounds(MARGIN, MARGIN*3+TITLE_HEIGHT+BUTTON_SIZE+LABEL_TEXT_SIZE,WINDOW_WIDTH-2*MARGIN, LABEL_TEXT_SIZE);
		currentInstrument.setForeground(Color.LIGHT_GRAY);
		window.add(currentInstrument);
		Instrument[] instruments = new Instrument[NUMBER_OF_INSTRUMENTS];
		instruments[0] = new Instrument("rhodes","res/instruments/fender-rhodes","res/images/rhodes_icon.jpg");
		instruments[1] = new Instrument("piano","res/instruments/piano","res/images/piano_icon.jpg");
		
		window.setTitle(TITLE);
		JLabel title = new JLabel(TITLE,SwingConstants.CENTER);
		title.setBounds(MARGIN,MARGIN,WINDOW_WIDTH-2*MARGIN,TITLE_HEIGHT);
		title.setForeground(Color.LIGHT_GRAY);
		title.setFont(new Font(title.getName(), Font.PLAIN, TITLE_TEXT_SIZE));
		window.add(title);
		makeButtons(instruments);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		window.setBounds((int)(dim.width*0.5-WINDOW_WIDTH*0.5), (int)(dim.height*0.5-WINDOW_HEIGHT*0.5),WINDOW_WIDTH,WINDOW_HEIGHT);
		window.setLayout(null); 
		window.setVisible(true);
	}
	
	private void makeButtons(Instrument[] instruments) {
		
		int spacer = (int)(WINDOW_WIDTH-instruments.length*BUTTON_SIZE)/(instruments.length+1);
		for(int i=0;i<instruments.length;i++) {
			JButton button = new JButton();
			JLabel label = new JLabel(instruments[i].getName(),SwingConstants.CENTER);
			Instrument inst = instruments[i];
			try {
				BufferedImage img = ImageIO.read(new File(inst.getIconImagePath()));
				button.setIcon(new ImageIcon(getScaledImage(img,BUTTON_SIZE,BUTTON_SIZE)));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			button.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					initialiseSamples(inst.getNotesPath());
					currentInstrument.setText("Currently playing a : " + inst.getName());
					//to do set label for instrument
				}
			});
			button.setFocusable(false);
			button.setBounds((i+1)*spacer+MARGIN,MARGIN*2+TITLE_HEIGHT,BUTTON_SIZE,BUTTON_SIZE);
			label.setBounds((i+1)*spacer+MARGIN,MARGIN*2+TITLE_HEIGHT+BUTTON_SIZE,BUTTON_SIZE,LABEL_TEXT_SIZE);
			label.setForeground(Color.LIGHT_GRAY);
			window.add(button);
			window.add(label);
		}
	}
	/**
	 * changes the notes to the current instrument
	 * @param dirPath, directory of the notes for this instrument
	 */
	
	private void initialiseSamples(String dirPath) {
		
		for(NOTE note : NOTE.values()) {
			try {
				Clip clip= AudioSystem.getClip();
				clip.open(AudioSystem.getAudioInputStream(
						 new File(dirPath+"/"+note.toString().toLowerCase()+"2.wav"))); //edit to reflect the standard
				samples.put(note, clip);
			} catch (LineUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedAudioFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
	private void playClip(NOTE note) {
		Clip clip = samples.get(note);
		clip.start();
		
	}
	private NOTE charToNote(char c) {
		
		switch(c) {
		case 'a' :
			return NOTE.C;
		case 's' :
			return NOTE.D;
		case 'd':
			return NOTE.E;
		case 'f':
			return NOTE.F;
		case 'g':
			return NOTE.G;
		case 'h': 
			return NOTE.A;
		case 'j':
			return NOTE.B;
		case 'w':
			return NOTE.Db;
		case 'e':
			return NOTE.Eb;
		case 't':
			return NOTE.Gb;
		case 'y':
			return NOTE.Ab;
		case 'u':
			return NOTE.Bb;
		default:
			return null;
		}		
	}
	public void fadeOutClip(Clip clip) {
		
		FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		float i = (float)(Math.pow(Math.E,gainControl.getValue()*0.05));
		while(20*Math.log(i)>gainControl.getMinimum()){
			 
			
			   try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			   gainControl.setValue((float)(20*Math.log(i)));
			   
			   i-=0.1f;
		}
		gainControl.setValue(gainControl.getMinimum());
		clip.stop();
		gainControl.setValue(0);
		
	}
	
	private Image getScaledImage(Image srcImg, int w, int h){
	    BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = resizedImg.createGraphics();

	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(srcImg, 0, 0, w, h, null);
	    g2.dispose();

	    return resizedImg;
	}
	
	class ImagePanel extends JComponent {
	    private Image image;
	    public ImagePanel(Image image) {
	        this.image = image;
	    }
	    @Override
	    protected void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        g.drawImage(image, 0, 0, this);
	    }
	}
}
