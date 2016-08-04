/**
 * @file MainMenu.java
 * @authors Leah Talkov, Jerry Tsui
 * @date 8/4/2016
 * Brings up a simple frame that allows the user to select if they
 * are either a "User" or an "Admin." If "User" is selected, then a new 
 * User object is created. If "Admin" is selected, a new Login object is created.
 */
package interfaceTest;

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

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;

@SuppressWarnings("serial")
public class MainMenu extends JFrame {

	/**Main panel for login that has the Automic logo and the User/Admin buttons*/
	private JPanel contentPane;
	/**instantiated if the user selects "Admin"*/
	private Login loginFrame;
	/**instantiated if the user selects "User"*/
	private User user;
	protected JButton btn_user;
	protected JButton btn_admin;
	private JButton btn_exit;
	
	public MainMenu() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		prepareGUI();
	}
	
	/**
	 * Creates the GUI for MainMenu
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws UnsupportedLookAndFeelException
	 */
	private void prepareGUI() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		try {
		     ClassLoader cl = this.getClass().getClassLoader();
		     ImageIcon programIcon = new ImageIcon(cl.getResource("res/logo.png"));
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
		
		contentPane.add(Box.createRigidArea(new Dimension(0, 10)));
		
		JPanel middlePanel = new JPanel();
		contentPane.add(middlePanel);
			
		btn_user = new JButton("User");
		btn_user.addActionListener(e -> {
			user = new User(this, false);
			this.setVisible(false);
			
		});
		btn_user.setPreferredSize(new Dimension(80, 25));
		btn_user.setAlignmentY(Component.CENTER_ALIGNMENT);
		
		middlePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		middlePanel.add(btn_user);
			
		
		btn_admin = new JButton("Admin");
		btn_admin.addActionListener(e -> {
			try {
				loginFrame = new Login(this);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			loginFrame.setVisible(true);
			this.setVisible(false);
		});
		btn_admin.setPreferredSize(new Dimension(80, 25));
		btn_admin.setAlignmentY(Component.CENTER_ALIGNMENT);
		middlePanel.add(btn_admin);
				
		contentPane.add(Box.createRigidArea(new Dimension(0, 10)));
		
		JPanel bottomPanel = new JPanel();
		contentPane.add(bottomPanel);
		
		btn_exit = new JButton("Exit");
		btn_exit.addActionListener(e ->{
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			System.exit(0);
		});
		btn_exit.setPreferredSize(new Dimension(60, 25));
		
		bottomPanel.add(btn_exit);
		contentPane.add(Box.createVerticalGlue());
		
		pack();
		center(this);
	}
	
	/**
	 * Centers the MainMenu frame on the screen
	 * @param frame Frame for class MainMenu
	 */
	public static void center(JFrame frame){
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		
		int w = frame.getSize().width;
		int h = frame.getSize().height;
		
		int x = (dim.width - w) / 2;
		int y = (dim.height - h) / 2;
		
		frame.setLocation(x, y);
	}
}
