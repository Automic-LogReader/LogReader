/**
 * @file User.java
 * @authors Leah Talkov, Jerry Tsui
 * @date 8/4/2016
 * Creates a User Object. If the boolean isAdmin is true,
 * then in the user interface there will be an additional
 * button that allows the user to access admin features. 
 */

package interfaceTest;

import java.awt.EventQueue;

public class User {

	protected UserView myView;
	
	/**
	 * Creates a User Object
	 * @param menu MainMenu frame that instantiated the User object
	 * @param isAdmin Boolean value representing administrator status
	 */
	public User(MainMenu menu, boolean isAdmin) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					myView = new UserView(menu, isAdmin);
					myView.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
		
}
