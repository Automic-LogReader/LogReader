/**
 * @file LineDialog.java
 * @authors Leah Talkov, Jerry Tsui
 * @date 8/15/2016
 * Brings up a Java Dialog when the user wants view the lines before or after the 
 * specified error selected in the JTable.  The Dialog displays anywhere between 
 * 0-10 lines before and after.  This view is disabled until the log file has been
 * completely parsed through.
 */
package interfaceTest;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;

public class LineDialog extends JDialog {
	/** Main content panel */
	private final JPanel pnlMain = new JPanel();
	/** Holds the contents of the lines to be displayed before */
	private JScrollPane beforeScrollPane;
	/** Holds the list of lines before a given error message */
	private ArrayList<String> listOfLinesBefore;
	/** Holds the list of lines after a given error message */
	private ArrayList<String> listOfLinesAfter;
	/**String that holds line of text where error was found*/
	private String errorLine;
	/**Index for the start of original errorLine*/
	private int startHighlightIndex;
	/**Index for the end of original errorLine*/
	private int endHighlightIndex;
	/**
	 * Constructor
	 * @param currentRow The current selected row within the JTable
	 * @param view The UserView that instantiated the LineDialog 
	 * @param currentError Checks which error the user has selected and 
	 * 					   retrieves the corresponding before/after lines
	 */
	public LineDialog(int currentRow, UserView view, String currentError) {
		startHighlightIndex = 0;
		endHighlightIndex = 0;
		prepareGUI(currentRow, view, currentError);
		Utility.addEscapeListener(this);
	}

	/**
	 * This function prepares and displays the LineDialog GUI.
	 * @param currentRow The current selected row within the JTable
	 * @param view the UserView that instantiated the LineDialog
	 * @param currentError Checks which error the user has selected and 
	 * 					   retrieves the corresponding before/after lines
	 */
	private void prepareGUI(int currentRow, UserView view, String currentError) {
		try {
		     ClassLoader cl = this.getClass().getClassLoader();
		     ImageIcon programIcon = new ImageIcon(cl.getResource("res/logo.png"));
		     setIconImage(programIcon.getImage());
		  } catch (Exception e) {
		     System.out.println("Could not load program icon.");
		  }
		setPreferredSize(new Dimension(800, 300));
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		getContentPane().add(pnlMain, BorderLayout.CENTER);
		setTitle("Lines Before and After " + currentError);
		
		pnlMain.setBorder(new EmptyBorder(5,5,5,5));
		pnlMain.setLayout(new GridLayout(1,2));
		System.out.println("Current Row: " + currentRow);
		
		setTitle("Lines Before and After");
		
		JPanel pnlBefore = new JPanel();
		pnlBefore.setLayout(new BoxLayout(pnlBefore, BoxLayout.Y_AXIS));
		pnlBefore.setBorder(new EmptyBorder(5,5,5,5));
		
		if (!view.linesBeforeArrayList.isEmpty()) {
			listOfLinesBefore = view.linesBeforeArrayList.get(currentRow);
		}
		else {
			listOfLinesBefore = new ArrayList<String>();
		}

		if (!view.linesAfterHashMap.isEmpty()) {
			listOfLinesAfter = view.linesAfterHashMap.get(currentRow + 1);
		}
		else {
			listOfLinesAfter = new ArrayList<String>();
		}
		
		JLabel lblErrorTitle = new JLabel("Lines Before and After Error #" + (currentRow + 1));
		lblErrorTitle.setFont(new Font("Serif", Font.PLAIN, 14));
		lblErrorTitle.setAlignmentX(CENTER_ALIGNMENT);
		pnlBefore.add(lblErrorTitle);
		
		pnlBefore.add(Box.createRigidArea(new Dimension(0,10)));
		JTextArea textLines = new JTextArea();
		textLines.setFont(new Font("Serif", Font.PLAIN, 13));
		textLines.setEditable(false);
		StringBuilder text = new StringBuilder();
		for(int i = 0; i < listOfLinesBefore.size(); i++) {
			text.append(listOfLinesBefore.get(i) + "\n");
		}
		text.append("-------------------------------------------------------"
					+ "-------------------------------------------------------------------"
					+ "-------------------------------------------------------------------\n");
		if(!view.errorLinesArrayList.isEmpty()) { 
			errorLine = view.errorLinesArrayList.get(currentRow) + "\n";
			text.append(errorLine);			
		}
		else
			errorLine = null;
		text.append("-------------------------------------------------------"
				+ "-------------------------------------------------------------------"
				+ "-------------------------------------------------------------------\n");
		for(int i = 0; i < listOfLinesAfter.size(); i++) {
			text.append(listOfLinesAfter.get(i) + "\n");
		}
		String tempText = text.toString();
		if(errorLine != null) {
			startHighlightIndex = text.indexOf(errorLine);
			endHighlightIndex = startHighlightIndex + errorLine.length();
		}
		
		textLines.append(tempText);
		textLines.setCaretPosition(startHighlightIndex);
		DefaultHighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
		try {
			textLines.getHighlighter().addHighlight(startHighlightIndex, endHighlightIndex, painter);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		beforeScrollPane = new JScrollPane(textLines);
		beforeScrollPane.setBackground(Color.WHITE);
		pnlBefore.add(beforeScrollPane);
		//pnlBefore.add(new JLabel(new ImageIcon("C:/Users/let/Pictures/untitled.png")));
		
		pnlMain.add(pnlBefore);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
