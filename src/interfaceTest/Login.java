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

import java.sql.*;
import java.util.HashMap;
import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JTextField;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;

@SuppressWarnings("serial")
public class Login extends JFrame {
	
	/** Main panel in the Login frame */
	private JPanel pnlMain;
	/** JTextField holding the UserName */
	private JTextField tfUserName;
	/** JTextField holding the PassWord */
	private JPasswordField tfPassWord;
	/** HashMap mapping valid usernames to passwords from the database */
	private HashMap<String, String> loginHashMap = new HashMap<String, String>();
	/**Holds the text from userNameText */
	private String inputUsername;
	/**Holds the text from paswordText */
	private char[] inputPassword;
	/**Instantiated if the info the user entered is valid */
	private JButton btnSubmit;
	/**Returns user back to the main menu */
	private JButton btnBack;
	
	/**
	 * Creates the Login interface.
	 * @param menu The MainMenu that generated the frame
	 * @throws ClassNotFoundException if the classpath is broken
	 * @throws SQLException if there is an error connecting to the SQL Server
	 */
	public Login(MainMenu menu) throws ClassNotFoundException, SQLException {
		fillHashMap();
		
		try {
		     ClassLoader cl = this.getClass().getClassLoader();
		     ImageIcon programIcon = new ImageIcon(cl.getResource("res/logo.png"));
		     setIconImage(programIcon.getImage());
		  } catch (Exception e) {
		     System.out.println("Could not load program icon.");
		  }
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(200, 200, 450, 150);
		setLocationRelativeTo(null);
		
		setTitle("Login");
		
		pnlMain = new JPanel();
		pnlMain.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(pnlMain);
		pnlMain.setLayout(new BorderLayout(0, 0));
		
		JPanel pnlTop = new JPanel();
		pnlMain.add(pnlTop, BorderLayout.NORTH);
		
		JLabel lblInstruction = new JLabel("Please enter your Username and Password");
		pnlTop.add(lblInstruction);
		
		JPanel pnlBottom = new JPanel();
		pnlMain.add(pnlBottom, BorderLayout.SOUTH);
		
		btnSubmit = new JButton("Submit");
		btnSubmit.setPreferredSize(new Dimension(80, 25));
		btnSubmit.addActionListener(e -> {
			inputUsername = tfUserName.getText();
			inputPassword = tfPassWord.getPassword();
			
			try {
				if (checkAdmin(inputUsername, inputPassword)) {
					Admin admin = new Admin(menu);
					this.setVisible(false);
				}
				else {
					JOptionPane.showMessageDialog(null, "The username or password is incorrect");
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		
		btnSubmit.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).
        put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER,0), "Enter pressed");
		btnSubmit.getActionMap().put("Enter pressed", new AbstractAction() {
					public void actionPerformed(ActionEvent e) {
						inputUsername = tfUserName.getText();
						inputPassword = tfPassWord.getPassword();
						
						try {
							if (checkAdmin(inputUsername, inputPassword)) {
								Admin admin = new Admin(menu);
								makeInvis();
							}
							else
								JOptionPane.showMessageDialog(null, "The username or password is incorrect");
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				});
		
		pnlBottom.add(btnSubmit);
		
		btnBack = new JButton("Back");
		btnBack.setPreferredSize(new Dimension(80, 25));
		btnBack.addActionListener(e ->{
			menu.setVisible(true);
			this.setVisible(false);
		});
		pnlBottom.add(btnBack);
		
		JPanel pnlLoginField = new JPanel();
		pnlMain.add(pnlLoginField, BorderLayout.WEST);
		pnlLoginField.setLayout(new BorderLayout(0, 0));
		
		JLabel lblPassword = new JLabel("Password:  ");
		pnlLoginField.add(lblPassword, BorderLayout.SOUTH);
		
		JLabel lblUsername = new JLabel("Username:  ");
		pnlLoginField.add(lblUsername, BorderLayout.NORTH);
		
		JPanel pnlLoginText = new JPanel();
		pnlMain.add(pnlLoginText, BorderLayout.CENTER);
		pnlLoginText.setLayout(new BorderLayout(0, 0));
		
		tfUserName = new JTextField();
		
		pnlLoginText.add(tfUserName, BorderLayout.NORTH);
		tfUserName.setColumns(10);
		
		tfPassWord = new JPasswordField();
		tfPassWord.setEchoChar('*');
		pnlLoginText.add(tfPassWord, BorderLayout.SOUTH);
		tfPassWord.setColumns(10);
		
	}
	
	/**
	 * This function takes in the credentials given by the user
	 * and checks it against the entries within Usernames_Passwords.csv
	 * @param userName - Username given by the user
	 * @param passWord - Password given by the user
	 * @return - returns true if the username and password match an 
	 * 			 entry in Usernames_Passwords.csv, false otherwise
	 */
	boolean checkAdmin(String userName, char[] passWord) {
		//Blocks against no entries
		if (userName.equals("") || passWord.equals(""))
			return false;
		StringBuilder pw = new StringBuilder();
		for(int i = 0; i < passWord.length; i++) {
			pw.append(passWord[i]);
		}
		
		if(!loginHashMap.containsKey(userName)) return false;
		else if(pw.toString().equals(loginHashMap.get(userName))) return true;
		else return false;
	}
	
	/**
	 * Makes the frame invisible
	 */
	private void makeInvis(){
		this.setVisible(false);
	}
	
	/**
	 * Fills loginHashMap with Key-Value pairs from the database.
	 * This HashMap is necessary for the validation of administrator users.
	 * @throws ClassNotFoundException if classpath is broken
	 * @throws SQLException if error with the JDBC connection
	 */
	void fillHashMap() throws ClassNotFoundException, SQLException {
		String driver = "net.sourceforge.jtds.jdbc.Driver";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection("jdbc:jtds:sqlserver://vwaswp02:1433/coeus", "coeus", "C0eus");

		Statement stmt = conn.createStatement();
		String query = "select Username, Password from Usernames_Passwords";
		ResultSet rs = stmt.executeQuery(query);
		while(rs.next()){
			loginHashMap.put(rs.getString("Username"), rs.getString("Password"));
		}
	}
	
}
