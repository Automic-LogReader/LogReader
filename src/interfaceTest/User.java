/**
 * file: User.java
 * 
 */

package interfaceTest;

import java.awt.EventQueue;

public class User {

	protected UserView myView;
	
	/**
	 * @param menu MainMenu frame that instantiated the User
	 * @param isAdmin Administrator status
	 */
	public User(MainMenu menu, boolean isAdmin){
		
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
