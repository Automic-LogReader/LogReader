/**
 * file: MainMenu.java
 * This class brings up a simple frame that allows the user to select if they
 * are either a "User" or an "Admin." If "User" is selected, then a new 
 * User object is created. If "Admin" is selected, a new Login object is created.
 */
package interfaceTest;

import javax.print.DocFlavor.URL;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;


import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.miginfocom.swing.MigLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;

@SuppressWarnings("serial")
public class MainMenu extends JFrame {

	private JPanel contentPane;
	//instantiated if the user selects "Admin"
	private Login loginFrame;
	//instantiated if the user selects "User'
	private User user;
	protected JButton userButton;
	protected JButton adminButton;
	
	private JButton exitButton;
	
	public MainMenu() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		prepareGUI();
	}
	
	
	private void prepareGUI() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		try {
		     ClassLoader cl = this.getClass().getClassLoader();
		     ImageIcon programIcon = new ImageIcon(cl.getResource("res/img.jpg"));
		     setIconImage(programIcon.getImage());
		  } catch (Exception e) {
		     System.out.println("Could not load program icon.");
		  }
		this.setTitle("Project COEUS");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(350, 250));
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		this.add(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
			
		contentPane.add(Box.createVerticalGlue());
		
		JPanel logoPanel = new JPanel();
		logoPanel.setLayout(new BorderLayout());
		
		java.net.URL url = this.getClass().getClassLoader().getResource("res/Automic-Logotype-Black.png");
		ImageIcon banner = new ImageIcon(url);
		
		Image bannerImage = banner.getImage();
		Image resizedBannerImage = bannerImage.getScaledInstance(200, 40, java.awt.Image.SCALE_SMOOTH);
		
		banner = new ImageIcon(resizedBannerImage);
		
		JLabel bannerLabel = new JLabel(banner);
		logoPanel.add(bannerLabel);
		
		contentPane.add(logoPanel);
		
		JPanel topPanel = new JPanel();
		contentPane.add(topPanel);
			
		JLabel label = new JLabel("Please select an option:");
		topPanel.add(label);
			
		Font defaultFont = new JLabel().getFont();
		String fontName = defaultFont.getFontName();
		int fontStyle = defaultFont.getStyle();
		int fontSize = defaultFont.getSize() + 4;
		
		label.setFont(new Font(fontName, fontStyle, fontSize));
		
		JPanel middlePanel = new JPanel();
		contentPane.add(middlePanel);
			
		userButton = new JButton("User");

		userButton.addActionListener(e -> {
			user = new User(this, false);
			this.setVisible(false);
			
		});
		userButton.setAlignmentY(Component.CENTER_ALIGNMENT);
		middlePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		middlePanel.add(userButton);
			
		
		adminButton = new JButton("Admin");
		adminButton.addActionListener(e -> {
			try {
				loginFrame = new Login(this);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			loginFrame.setVisible(true);
			this.setVisible(false);
		});
		adminButton.setAlignmentY(Component.CENTER_ALIGNMENT);
		middlePanel.add(adminButton);
				
		JPanel bottomPanel = new JPanel();
		contentPane.add(bottomPanel);
		
		exitButton = new JButton("Exit");
		exitButton.addActionListener(e ->{
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			System.exit(0);
		});
		
		bottomPanel.add(exitButton);
		contentPane.add(Box.createVerticalGlue());
		
		pack();
		center(this);
	}
	
	public static void center(JFrame frame){
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		
		int w = frame.getSize().width;
		int h = frame.getSize().height;
		
		int x = (dim.width - w) / 2;
		int y = (dim.height - h) / 2;
		
		frame.setLocation(x, y);
	}
}
