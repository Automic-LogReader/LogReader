/**
 * @file LineDialog.java
 * @authors Leah Talkov, Jerry Tsui
 * @date 8/3/2016
 * Brings up a Java Dialog when the user wants view the lines before or after the 
 * specified error selected in the JTable.  The Dialog displays anywhere between 
 * 0-10 lines before and after.  This view is disabled until the log file has been
 * completely parsed through.
 */
package interfaceTest;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

public class LineDialog extends JDialog {
	private final JPanel pnlMain = new JPanel();
	private JList<?> list;
	private JScrollPane scrollPane;
	/**
	 * Constructor
	 * @param currentRow The current selected row within the JTable
	 * @param view The UserView that instantiated the LineDialog 
	 */
	public LineDialog(int currentRow, UserView view, String type) {
		prepareGUI(currentRow, view, type);
		Utility.addEscapeListener(this);
	}

	/**
	 * This function prepares and displays the LineDialog GUI.
	 * @param currentRow THe current selected row within the JTable
	 * @param view the UserView that instantiated the LineDialog
	 */
	private void prepareGUI(int currentRow, UserView view, String type){
		try {
		     ClassLoader cl = this.getClass().getClassLoader();
		     ImageIcon programIcon = new ImageIcon(cl.getResource("res/logo.png"));
		     setIconImage(programIcon.getImage());
		  } catch (Exception e) {
		     System.out.println("Could not load program icon.");
		  }
		
		setPreferredSize(new Dimension(400, 300));
		setLocationRelativeTo(null);
		if (type.equals("BEFORE")){
			setTitle("Lines Before");
			
			getContentPane().add(pnlMain, BorderLayout.CENTER);
			pnlMain.setBorder(new EmptyBorder(5,5,5,5));
			pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
			System.out.println("Current Row: " + currentRow);
			
			ArrayList<String> listOfLines = view.linesBeforeArrayList.get(currentRow);
			
			JLabel lblTitle = new JLabel("Lines before error #" + currentRow);
			lblTitle.setAlignmentX(CENTER_ALIGNMENT);
			pnlMain.add(lblTitle);
			
			pnlMain.add(Box.createRigidArea(new Dimension(0,10)));
			
			list = new JList(listOfLines.toArray());
			
			scrollPane = new JScrollPane(list);
			scrollPane.setBackground(Color.WHITE);
			
			pnlMain.add(scrollPane);	
		} else if (type.equals("AFTER")){
			
		}
		
		pack();
		setVisible(true);
	}
	

}
