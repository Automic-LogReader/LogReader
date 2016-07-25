/**
 * file: Admin.java
 * This class extends the user class and has functions that determine
 * the length of the LogErrors_Suggestions.csv file (how many rows it
 * contains, not including the header) and fills an Object[][] that 
 * prepares the data for the JTable in AdminView.java.
 */

package interfaceTest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Admin extends User {
	
	public Admin(MainMenu menu) throws IOException
	{
		super(menu, true);
	}
}
