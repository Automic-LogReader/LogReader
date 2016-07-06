/**
 * file: Login.java
 * This class brings up a frame that allows the user to enter in a 
 * username and password. If the info given is within the file
 * Usernames_Passwords.csv, then a new Admin is created. Otherwise,
 * a popup shows that the username or password is incorrect. 
 */

package interfaceTest;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JTextField;
import javax.swing.AbstractAction;
import javax.swing.JButton;

@SuppressWarnings("serial")
public class Login extends JFrame {

	private JPanel contentPane;
	private JTextField userNameText;
	private JPasswordField passwordText;
	
	//Holds the text from userNameText
	private String inputUsername;
	//Holds the text from paswordText
	private char[] inputPassword;
	//Instantiated if the info the user entered is valid
	private JButton submitButton;
	//Returns user back to the main menu
	private JButton backButton;
	
	private Admin admin;

	private boolean confirmed;
	//Used as a holder for usernames from Usernames_Passwords.csv to 
	//compare against the inputUsername
	String userCheck;
	//Used as a holder for passwords from Usernames_Passwords.csv to
	//compare against the inputPassword
	String[] userPwLine;
	
	
	/**
	 * Create the frame.
	 */
	public Login(MainMenu menu) {
		
		confirmed = false;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(200, 200, 450, 150);
		setLocationRelativeTo(null);
		
		setTitle("Login");
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		
		JLabel lblNewLabel = new JLabel("Please enter your Username and Password");
		panel.add(lblNewLabel);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.SOUTH);
		
		submitButton = new JButton("Submit");
		submitButton.setPreferredSize(new Dimension(80, 30));
		submitButton.addActionListener(e -> {
			inputUsername = userNameText.getText();
			inputPassword = passwordText.getPassword();
			
			try {
				if (checkAdmin(inputUsername, inputPassword))
				{
					admin = new Admin(menu);
					this.setVisible(false);
				}
				else
					JOptionPane.showMessageDialog(null, "The username or password is incorrect");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		
		submitButton.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).
        put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER,0), "Enter pressed");
		submitButton.getActionMap().put("Enter pressed", new AbstractAction()
				{
					public void actionPerformed(ActionEvent e)
					{
						inputUsername = userNameText.getText();
						inputPassword = passwordText.getPassword();
						
						try {
							if (checkAdmin(inputUsername, inputPassword))
							{
								admin = new Admin(menu);
								confirmed = true;
							}
							else
								JOptionPane.showMessageDialog(null, "The username or password is incorrect");
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				});
		panel_1.add(submitButton);
		
		backButton = new JButton("Back");
		backButton.setPreferredSize(new Dimension(80, 30));
		backButton.addActionListener(e ->{
			menu.setVisible(true);
			this.setVisible(false);
		});
		panel_1.add(backButton);
		
		JPanel panel_2 = new JPanel();
		contentPane.add(panel_2, BorderLayout.WEST);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel_2 = new JLabel("Password:");
		panel_2.add(lblNewLabel_2, BorderLayout.SOUTH);
		
		JLabel lblNewLabel_3 = new JLabel("Username:");
		panel_2.add(lblNewLabel_3, BorderLayout.NORTH);
		
		JPanel panel_3 = new JPanel();
		contentPane.add(panel_3, BorderLayout.CENTER);
		panel_3.setLayout(new BorderLayout(0, 0));
		
		userNameText = new JTextField();
		panel_3.add(userNameText, BorderLayout.NORTH);
		userNameText.setColumns(10);
		
		passwordText = new JPasswordField();
		passwordText.setEchoChar('*');
		panel_3.add(passwordText, BorderLayout.SOUTH);
		passwordText.setColumns(10);
		
		if (confirmed)
			this.setVisible(false);
	}

	
	/**
	 * This function takes in the credentials given by the user
	 * and checks it against the entries within Usernames_Passwords.csv
	 * @param userName - Username given by the user
	 * @param passWord - Password given by the user
	 * @return - returns true if the username and password match an 
	 * 			 entry in Usernames_Passwords.csv, false otherwise
	 * @throws IOException
	 */
	boolean checkAdmin(String userName, char[] passWord) throws IOException
	{
		//Blocks against no entries
		if (userName.equals("") || passWord.equals(""))
			return false;
		InputStream userPwReader = getClass().getResourceAsStream(MainController.userPwFile);
		BufferedReader userPwbf = new BufferedReader(new InputStreamReader(userPwReader));
		
		userCheck = userPwbf.readLine();
		userCheck = userPwbf.readLine();
		
		while(userCheck != null)
		{
			userPwLine = userCheck.split(",(?=([^\"]|\"[^\"]*\")*$)");
			
			//Both the username and password match so we return true
			if(userName.equals(userPwLine[0]) && checkPassword(passWord, userPwLine[1]))
					return true;
			userCheck = userPwbf.readLine();	
			
		}
		userPwbf.close();	
		return false;
	}
	
	/**
	 * This function takes in a password from the user and 
	 * compares it against a password in the file. 
	 * @param pw - The given password from the user
	 * @param pwCheck - A password from Usernames_Passwords.csv
	 * @return - returns true if the params match, false otherwise
	 */
	boolean checkPassword(char [] pw, String pwCheck)
	{
		if(pw.length != pwCheck.length())
			return false;
		for(int i = 0; i < pw.length; i++)
		{
			if (pwCheck.charAt(i) != pw[i])
				return false;
		}
		return true;
	}
}
