/**
 * @file Admin.java
 * @authors Leah Talkov, Jerry Tsui
 * @data 8/3/2016
 * This class extends the user class that fills an Object[][] that 
 * prepares the data for the JTable in AdminView.java.
 */

package interfaceTest;

import java.io.IOException;

public class Admin extends User {
	
	public Admin(MainMenu menu) throws IOException{
		super(menu, true);
	}
}
