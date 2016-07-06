/**
 * file: MainMenu.java
 * This class brings up a simple frame that allows the user to select if they
 * are either a "User" or an "Admin." If "User" is selected, then a new 
 * User object is created. If "Admin" is selected, a new Login object is created.
 */
package interfaceTest;

import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
	
	public MainMenu() {
		prepareGUI();
		/*
		setTitle("Log Reader");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(400, 200, 300, 200);
		setLocationRelativeTo(null);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
			
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
			
		JLabel lblNewLabel = new JLabel("Please select an option:");
		panel.add(lblNewLabel);
			
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.CENTER);
			
		userButton = new JButton("User");

		userButton.addActionListener(e -> {
			user = new User(this, false);
			this.setVisible(false);
			
		});
		panel_1.add(userButton);
			
		
		adminButton = new JButton("Admin");
		adminButton.addActionListener(e -> {
			loginFrame = new Login(this);
			loginFrame.setVisible(true);
			this.setVisible(false);
		});
		panel_1.add(adminButton);
				
		exitButton = new JButton("Exit");*/
		
	}
	
	
	private void prepareGUI(){
		this.setTitle("Log Reader");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(400, 200, 300, 200);
		setLocationRelativeTo(null);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
			
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
			
		JLabel lblNewLabel = new JLabel("Please select an option:");
		panel.add(lblNewLabel);
			
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.CENTER);
			
		userButton = new JButton("User");

		userButton.addActionListener(e -> {
			user = new User(this, false);
			this.setVisible(false);
			
		});
		panel_1.add(userButton);
			
		
		adminButton = new JButton("Admin");
		adminButton.addActionListener(e -> {
			loginFrame = new Login(this);
			loginFrame.setVisible(true);
			this.setVisible(false);
		});
		panel_1.add(adminButton);
				
		JPanel panel_2 = new JPanel();
		contentPane.add(panel_2, BorderLayout.SOUTH);
		
		exitButton = new JButton("Exit");
		exitButton.addActionListener(e ->{
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			System.exit(0);
		});
		panel_2.add(exitButton);
	}
	
	
}
