/**
 * file: MainController.java
 * Contains the main function for the program, and brings up the main menu. 
 * This program allows users to input log files, and finds errors within
 * the files for the user. The timestamp, Ucode, the error message, and 
 * a suggested solution are displayed on the screen. If the user is an
 * admin, then the user can also modify, delete, or add entries into the
 * file that contains information on the various errors that the program
 * searches for. 
 */

package interfaceTest;

import java.io.IOException;
import java.awt.EventQueue;

public class MainController {

	public static final String errorFile = "resources/LogErrors_Suggestions.csv";
	public static final String oldFile = "src/interfaceTest/resources/LogErrors_Suggestions.csv";
	public static final String userPwFile = "resources/Usernames_Passwords.csv";
	
	public static void main(String[] args) throws IOException
	{
	
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try 
				{
					MainMenu mainMenu = new MainMenu();
					mainMenu.setVisible(true);   
				} 
				catch (Exception e) {
					e.printStackTrace();
					}
			}
		}
		);	
		
	}
	
}
