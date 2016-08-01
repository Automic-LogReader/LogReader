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

	private final JPanel pnlMain = new JPanel();
	private JTextField tfKeyword;
	private JTextField tfError;
	private JTextField tfSolution;
	private JTextField tfFolder;
	private int selectedRow;

	/**
	 * Create the dialog.
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
			if (tfKeyword.getText().equals("") || tfError.getText().equals("") || tfSolution.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "All entries must be filled");
			}
			else if (dc.getAdmin().savedWords.contains(tfKeyword.getText()) && 
					!keyWord.equals(tfKeyword.getText())) {
					JOptionPane.showMessageDialog(null, "No duplicate keywords allowed");
			}
			else{
				try {
					dc.setFolderChanged(!(tfFolder.getText().equals(folder)));
					dc.setKeywordChanged(!(tfKeyword.getText().equals(keyWord)));
					dc.setErrorMessageChanged(!(tfError.getText().equals(errorMessage)));
					dc.setSuggestedSolutionChanged(!(tfSolution.getText().equals(solutionMessage)));
					dc.modifyData(tfFolder.getText(), tfKeyword.getText(), tfError.getText(),
							tfSolution.getText(), "MODIFY", selectedRow);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
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
		
