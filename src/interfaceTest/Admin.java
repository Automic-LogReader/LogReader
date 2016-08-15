/**
 * @file Admin.java
 * @authors Leah Talkov, Jerry Tsui
 * @data 8/15/2016
 * Extends the User class and fills an Object[][] that 
 * prepares the data for the JTable in AdminView.java.
 */
package interfaceTest;

public class Admin extends User {
	
	/**
	 * Creates an Admin object, created by Login if the credentials are correct
	 * @param menu Menu associated with this object
	 */
	public Admin(MainMenu menu) {
		super(menu, true);
	}
}
