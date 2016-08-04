package interfaceTest;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class LineDialog extends JDialog {
	private final JPanel pnlMain = new JPanel();
	private JList<?> list;
	private JScrollPane scrollPane;
	public LineDialog(int currentRow, UserView view) {
		prepareGUI(currentRow, view);
		Utility.addEscapeListener(this);
	}

	private void prepareGUI(int currentRow, UserView view){
		try {
		     ClassLoader cl = this.getClass().getClassLoader();
		     ImageIcon programIcon = new ImageIcon(cl.getResource("res/logo.png"));
		     setIconImage(programIcon.getImage());
		  } catch (Exception e) {
		     System.out.println("Could not load program icon.");
		  }
		
		setPreferredSize(new Dimension(400, 300));
		setLocationRelativeTo(null);
		setTitle("Lines Before");
		
		getContentPane().add(pnlMain, BorderLayout.CENTER);
		pnlMain.setBorder(new EmptyBorder(5,5,5,5));
		pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
		System.out.println("Current Row: " + currentRow);
		
		ArrayList<String> listOfLines = view.linesBeforeArrayList.get(currentRow);
		
		JLabel lblTitle = new JLabel("Lines Before");
		lblTitle.setAlignmentX(CENTER_ALIGNMENT);
		pnlMain.add(lblTitle);
		
		list = new JList(listOfLines.toArray());
		
		scrollPane = new JScrollPane(list);
		scrollPane.setBackground(Color.WHITE);
		
		pnlMain.add(scrollPane);
		
		pack();
		setVisible(true);
	}
	

}
