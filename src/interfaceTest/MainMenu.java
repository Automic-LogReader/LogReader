/**
 * file: MainMenu.java
 * This class brings up a simple frame that allows the user to select if they
 * are either a "User" or an "Admin." If "User" is selected, then a new 
 * User object is created. If "Admin" is selected, a new Login object is created.
 */
package interfaceTest;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.Component;

import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import java.awt.FlowLayout;

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
		
	}
	
	
	private void prepareGUI(){
		try {
		     ClassLoader cl = this.getClass().getClassLoader();
		     ImageIcon programIcon = new ImageIcon(cl.getResource("res/img.jpg"));
		     setIconImage(programIcon.getImage());
		  } catch (Exception e) {
		     System.out.println("Could not load program icon.");
		  }
		this.setTitle("Project COEUS");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(400, 200, 300, 200);
		setLocationRelativeTo(null);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[260px]", "[]15[]"));
			
		JPanel topPanel = new JPanel();
		contentPane.add(topPanel, "cell 0 0,growx,aligny top");
			
		JLabel lblNewLabel = new JLabel("Please select an option:");
		topPanel.add(lblNewLabel);
			
		
		JPanel middlePanel = new JPanel();
		contentPane.add(middlePanel, "cell 0 1,grow");
			
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
		contentPane.add(bottomPanel, "cell 0 2,growx,aligny top");
		
		exitButton = new JButton("Exit");
		exitButton.addActionListener(e ->{
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			System.exit(0);
		});
		bottomPanel.add(exitButton);
	}
	
	
}
