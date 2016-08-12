/**
 * @file AddDialog.java
 * @authors Leah Talkov, Jerry Tsui
 * @date 8/3/2016
 * Brings up a Java Dialog when the user wants to add an entry
 * to the database. The dialog has four fields that allow the user to enter
 * in a keyword, the folder the keyword will belong to, an error message,
 * and a solution. The new entry info is then given to the AdminView to be
 * loaded into the table.
 */

package interfaceTest;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.Box;
import java.awt.Dimension;

@SuppressWarnings("serial")
public class AddDialog extends JDialog {

	/**Main panel for the Dialog*/
	private final JPanel pnlMain = new JPanel();
	/**Textfield for the keyword, which cannot be an existing keyword*/
	private JTextField tfKeyword;
	/**Textfield for the error message that corresponds to the given keyword*/
	private JTextField tfError;
	/**Textfield for the solution that corresponds to the given keyword*/
	private JTextField tfSolution;
	/**Textfield for the folder the keyword will be put into, can be a new
	or an existing folder*/
	private JTextField tfFolder;


	public AddDialog(DataController dc) {
		try {
		     ClassLoader cl = this.getClass().getClassLoader();
		     ImageIcon programIcon = new ImageIcon(cl.getResource("res/logo.png"));
		     setIconImage(programIcon.getImage());
		  } catch (Exception e) {
		     System.out.println("Could not load program icon.");
		  }
		
		setBounds(200, 200, 450, 250);
		setMinimumSize(new Dimension(300, 250));
		setMaximumSize(new Dimension(450, 250));
		setLocationRelativeTo(null);
		setTitle("Add Entry");
		
		pnlMain.add(Box.createRigidArea(new Dimension(20, 20)));
		
		pnlMain.setBorder(new EmptyBorder(5, 5, 5, 5));
		add(pnlMain, BorderLayout.CENTER);
		pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
		JPanel pnlFolder = new JPanel();
		pnlMain.add(pnlFolder);
		pnlFolder.setLayout(new BoxLayout(pnlFolder, BoxLayout.X_AXIS));
		
		JLabel lblFolder = new JLabel("Folder:                  ");
		pnlFolder.add(lblFolder);
	
		pnlFolder.add(Box.createRigidArea(new Dimension(20, 20)));
	
		tfFolder = new JTextField();
		pnlFolder.add(tfFolder);
		tfFolder.setColumns(10);
	
		pnlFolder.add(Box.createRigidArea(new Dimension(20, 20)));
		
		pnlMain.add(Box.createRigidArea(new Dimension(20, 20)));
		
		JPanel pnlKeyword = new JPanel();
		pnlMain.add(pnlKeyword);
		pnlKeyword.setLayout(new BoxLayout(pnlKeyword, BoxLayout.X_AXIS));
		
		//Spaces have been added to the labels so that the text boxes line up
		JLabel lblKeyword = new JLabel("Keyword:              ");
		pnlKeyword.add(lblKeyword);

		pnlKeyword.add(Box.createRigidArea(new Dimension(20, 20)));
		
		tfKeyword = new JTextField();

		pnlKeyword.add(tfKeyword);
		tfKeyword.setColumns(10);
	
		pnlKeyword.add(Box.createRigidArea(new Dimension(20, 20)));
		
		pnlMain.add(Box.createRigidArea(new Dimension(20, 20)));
	
		JPanel pnlError = new JPanel();
		pnlMain.add(pnlError);
		pnlError.setLayout(new BoxLayout(pnlError, BoxLayout.X_AXIS));
		
		JLabel lblError = new JLabel("Error Message:     ");
		pnlError.add(lblError);
	
		pnlError.add(Box.createRigidArea(new Dimension(20, 20)));
	
		tfError = new JTextField();
		pnlError.add(tfError);
		tfError.setColumns(10);
	
		pnlError.add(Box.createRigidArea(new Dimension(20, 20)));
		
		pnlMain.add(Box.createRigidArea(new Dimension(20, 20)));
	
		JPanel pnlSolution = new JPanel();
		pnlMain.add(pnlSolution);
		pnlSolution.setLayout(new BoxLayout(pnlSolution, BoxLayout.X_AXIS));
		
		JLabel lblSolution = new JLabel("Solution Message:");
		pnlSolution.add(lblSolution);
	
		pnlSolution.add(Box.createRigidArea(new Dimension(20, 20)));
	
		tfSolution = new JTextField();
		pnlSolution.add(tfSolution);
		tfSolution.setColumns(10);
	
		pnlSolution.add(Box.createRigidArea(new Dimension(20, 20)));
		
		JPanel pnlButton = new JPanel();
		pnlButton.setLayout(new FlowLayout());
		getContentPane().add(pnlButton, BorderLayout.SOUTH);
		
		
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(e -> {
			//When the user presses the admin button, we check if there were any blanks
			if (tfFolder.getText().equals("") || tfKeyword.getText().equals("") ||
				tfError.getText().equals("") || tfSolution.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "All entries must be filled");
			}
			//We then check to see if they user has entered a keyword that already exists
			else if (dc.getAdmin().savedWords.contains(tfKeyword.getText())) {
				JOptionPane.showMessageDialog(null, "No duplicate keywords allowed");
			}
			//Otherwise we call a function from DataController that adds the entry
			else {
				try {
					dc.modifyData(tfFolder.getText(), tfKeyword.getText(), 
								tfError.getText(), tfSolution.getText(), "ADD", 0);
				} catch (Exception e1) {
					e1.printStackTrace();						
					}
					this.setVisible(false);
			}
		});
		
		btnAdd.setActionCommand("OK");
		btnAdd.setToolTipText("Changes will be saved locally");
		pnlButton.add(btnAdd);
		getRootPane().setDefaultButton(btnAdd);
	}
}
	
