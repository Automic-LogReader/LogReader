/**
 * @file ModifyDialog.java
 * @authors Leah Talkov, Jerry Tsui
 * @data 8/3/2016
 * Brings up a JDialog when the user wants to modify an 
 * entry. There are four textboxes that are initially filled with
 * the entry's original values. The user can change any of these four
 * (though the keyWord must be unique) and press the modify button to 
 * go through with their changes. 
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
public class ModifyDialog extends JDialog {

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
	/**The row index for the entry the user is modifying*/
	private int selectedRow;

	/**
	 * Creates the dialog for modifying an entry. 
	 * @param folder The previous folder name for the entry
	 * @param keyWord The previous keyword name for the entry
	 * @param errorMessage The previous error message for the entry
	 * @param solutionMessage The previous solution message for the entry
	 * @param dc The DataController that created this object
	 * @param row The row of the entry to be modified
	 */
	public ModifyDialog(String folder, String keyWord, String errorMessage,
						String solutionMessage,  DataController dc, int row) {
		try {
		     ClassLoader cl = this.getClass().getClassLoader();
		     ImageIcon programIcon = new ImageIcon(cl.getResource("res/logo.png"));
		     setIconImage(programIcon.getImage());
		  } catch (Exception e) {
		     System.out.println("Could not load program icon.");
		}
		
		selectedRow = row;
		setBounds(200, 200, 450, 250);
		setMinimumSize(new Dimension(300, 250));
		setMaximumSize(new Dimension(450, 250));
		setLocationRelativeTo(null);
		setTitle("Modify Entry");

		pnlMain.setBorder(new EmptyBorder(5, 5, 5, 5));
		add(pnlMain, BorderLayout.CENTER);
		pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
		
		pnlMain.add(Box.createRigidArea(new Dimension(0, 10)));
		
		JPanel pnlFolder = new JPanel();
		pnlMain.add(pnlFolder);
		pnlFolder.setLayout(new BoxLayout(pnlFolder, BoxLayout.X_AXIS));
		
		JLabel lblFolder = new JLabel("Folder:                  ");
		pnlFolder.add(lblFolder);
	
		pnlFolder.add(Box.createRigidArea(new Dimension(20, 20)));
	
		tfFolder = new JTextField();
		tfFolder.setText(folder);
		pnlFolder.add(tfFolder);
		tfFolder.setColumns(10);
	
		pnlFolder.add(Box.createRigidArea(new Dimension(20, 20)));
		
		pnlMain.add(Box.createRigidArea(new Dimension(20, 20)));
		
		JPanel pnlKeyword = new JPanel();
		pnlMain.add(pnlKeyword);
		pnlKeyword.setLayout(new BoxLayout(pnlKeyword, BoxLayout.X_AXIS));
		
		//Spaces are added so that the textboxes line up correctly
		JLabel lblKeyword = new JLabel("Keyword:              ");
		pnlKeyword.add(lblKeyword);

		pnlKeyword.add(Box.createRigidArea(new Dimension(20, 20)));
		
		tfKeyword = new JTextField();
		tfKeyword.setText(keyWord);
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
		tfError.setText(errorMessage);
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
		tfSolution.setText(solutionMessage);
		pnlSolution.add(tfSolution);
		tfSolution.setColumns(10);
	
		pnlSolution.add(Box.createRigidArea(new Dimension(20, 20)));
		JPanel pnlButton = new JPanel();
		pnlButton.setLayout(new FlowLayout());
		getContentPane().add(pnlButton, BorderLayout.SOUTH);
		JButton btnModify = new JButton("Modify");
		btnModify.addActionListener(e ->{
			//We check to make sure all the textboxes are filled
			if (tfKeyword.getText().equals("") || tfError.getText().equals("") || tfSolution.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "All entries must be filled");
			}
			//We then make sure that the keyword is unique
			else if (dc.getAdmin().savedWords.contains(tfKeyword.getText()) && 
					!keyWord.equals(tfKeyword.getText())) {
					JOptionPane.showMessageDialog(null, "No duplicate keywords allowed");
			}
			else {
				//Otherwise we see if the text is the same by checking it against
				//the original values, and set the booleans accordingly. 
				try {
					dc.setFolderChanged(!(tfFolder.getText().equals(folder)));
					dc.setKeywordChanged(!(tfKeyword.getText().equals(keyWord)));
					dc.setErrorMessageChanged(!(tfError.getText().equals(errorMessage)));
					dc.setSuggestedSolutionChanged(!(tfSolution.getText().equals(solutionMessage)));
					dc.modifyData(tfFolder.getText(), tfKeyword.getText(), tfError.getText(),
							tfSolution.getText(), "MODIFY", selectedRow);
				} catch (Exception e1) {
					e1.printStackTrace();						
				}
				this.setVisible(false);
			}
		});
		btnModify.setAlignmentX(CENTER_ALIGNMENT);
		btnModify.setToolTipText("Changes will be saved locally");
		btnModify.setActionCommand("OK");
		pnlButton.add(btnModify);
		getRootPane().setDefaultButton(btnModify);
	}
	
}
		
