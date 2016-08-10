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
import java.awt.GridLayout;
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
	/** Main content panel */
	private final JPanel pnlMain = new JPanel();
	/** JList holding the list of lines before a given error message */
	private JList<Object> jListBefore;
	/** JList holding the list of lines after a given error message */
	private JList<Object> jListAfter;
	/** Holds the contents of the lines to be displayed before */
	private JScrollPane beforeScrollPane;
	/** Holds the contents of the lines to be displayed after */
	private JScrollPane afterScrollPane;
	/** Holds the list of lines before a given error message */
	private ArrayList<String> listOfLinesBefore;
	/** Holds the list of lines after a given error message */
	private ArrayList<String> listOfLinesAfter;
	/**
	 * Constructor
	 * @param currentRow The current selected row within the JTable
	 * @param view The UserView that instantiated the LineDialog 
	 */
	public LineDialog(int currentRow, UserView view, String currentError) {
		prepareGUI(currentRow, view, currentError);
		Utility.addEscapeListener(this);
	}

	/**
	 * This function prepares and displays the LineDialog GUI.
	 * @param currentRow The current selected row within the JTable
	 * @param view the UserView that instantiated the LineDialog
	 */
	private void prepareGUI(int currentRow, UserView view, String currentError){
		try {
		     ClassLoader cl = this.getClass().getClassLoader();
		     ImageIcon programIcon = new ImageIcon(cl.getResource("res/logo.png"));
		     setIconImage(programIcon.getImage());
		  } catch (Exception e) {
		     System.out.println("Could not load program icon.");
		  }
		
		setPreferredSize(new Dimension(800, 300));
		setLocationRelativeTo(null);
		getContentPane().add(pnlMain, BorderLayout.CENTER);
		setTitle("Lines Before and After " + currentError);
		
		pnlMain.setBorder(new EmptyBorder(5,5,5,5));
		pnlMain.setLayout(new GridLayout(1,2));
		System.out.println("Current Row: " + currentRow);
		
		setTitle("Lines Before and After");
		
		JPanel pnlBefore = new JPanel();
		pnlBefore.setLayout(new BoxLayout(pnlBefore, BoxLayout.Y_AXIS));
		pnlBefore.setBorder(new EmptyBorder(5,5,5,5));
		
		if (!view.linesBeforeArrayList.isEmpty()){
			listOfLinesBefore = view.linesBeforeArrayList.get(currentRow);
		}
		else {
			listOfLinesBefore = new ArrayList<String>();
		}
		
		JLabel lblTitleBefore = new JLabel("Lines before error #" + (currentRow + 1));
		lblTitleBefore.setAlignmentX(CENTER_ALIGNMENT);
		pnlBefore.add(lblTitleBefore);
		
		pnlBefore.add(Box.createRigidArea(new Dimension(0,10)));
		
		jListBefore = new JList<Object>(listOfLinesBefore.toArray());
		
		beforeScrollPane = new JScrollPane(jListBefore);
		beforeScrollPane.setBackground(Color.WHITE);
		pnlBefore.add(beforeScrollPane);	
		
		pnlMain.add(pnlBefore);
		

		JPanel pnlAfter = new JPanel();
		pnlAfter.setLayout(new BoxLayout(pnlAfter, BoxLayout.Y_AXIS));
		pnlAfter.setBorder(new EmptyBorder(5,5,5,5));
		
		if (!view.linesAfterHashMap.isEmpty()){
			listOfLinesAfter = view.linesAfterHashMap.get(currentRow + 1);
		}
		else {
			listOfLinesAfter = new ArrayList<String>();
		}
		JLabel lblTitleAfter = new JLabel("Lines after error #" + (currentRow + 1));
		lblTitleAfter.setAlignmentX(CENTER_ALIGNMENT);
		pnlAfter.add(lblTitleAfter);
		
		pnlAfter.add(Box.createRigidArea(new Dimension(0,10)));
		
		jListAfter = new JList<Object>(listOfLinesAfter.toArray());
		
		afterScrollPane = new JScrollPane(jListAfter);
		afterScrollPane.setBackground(Color.WHITE);
		pnlAfter.add(afterScrollPane);
		
		pnlMain.add(pnlAfter);
		
		
		pack();
		setVisible(true);
	}
	

}
