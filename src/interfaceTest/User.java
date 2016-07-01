/**
 * file: User.java
 * 
 */

package interfaceTest;

import java.awt.EventQueue;

public class User {

	protected UserView myView;
	
	public User(MainMenu menu)
	{
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					myView = new UserView(menu);
					myView.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
		
}
