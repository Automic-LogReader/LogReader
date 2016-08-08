/**
 * @file PreferenceEditor.java
 * @authors Leah Talkov, Jerry Tsui
 * @date 8/3/2016
 * Allows the user to make more specifications on their searches by inputing
 * timebounds for Time Critical errors, and how many lines (0-10) the user would
 * like to see before and after an error. The default values for the time bounds 
 * are 0 to infinity, and 5 for before/after lines.
 */

package interfaceTest;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;                                                                                                                                                                             
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class PreferenceEditor extends JDialog {
	/**For lower bound input, value cannot be greater than upper bound*/
	private JTextField tfLowerBound;
	/**For upper bound input, value cannot be less than 0*/
	private JTextField tfUpperBound;
	/**For lines before input, value cannot be less than 0 or greater than 10*/
	private JTextField tfNumLinesBefore;
	/**For lines after input, value cannot be less than 0 or greater than 10*/
	private JTextField tfNumLinesAfter;
	
	public PreferenceEditor(UserView view, boolean admin) {
		prepareGUI(view);
	}
	
	private void prepareGUI(UserView view){
		this.setModal(true);
		setTitle("Preference Editor");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 400, 240);
		setLocationRelativeTo(null);
		setResizable(false);
		try {
		     ClassLoader cl = this.getClass().getClassLoader();
		     ImageIcon programIcon = new ImageIcon(cl.getResource("res/logo.png"));
		     setIconImage(programIcon.getImage());
		  } catch (Exception e) {
		     System.out.println("Could not load program icon.");
		  }
		
		//Gets rid of the ugly tabbed pane border
		Insets oldInsets = UIManager.getInsets("TabbedPane.contentBorderInsets"); 
		UIManager.put("TabbedPane.contentBorderInsets", new Insets(1, 0, 0, 0));
		
		JPanel pnlMain = new JPanel();
		pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		JPanel pnlTabOne = new JPanel(false);
		pnlTabOne.setLayout(new BoxLayout(pnlTabOne, BoxLayout.Y_AXIS));
		pnlTabOne.add(Box.createVerticalGlue());
		
		JPanel pnlTabOneUpper = new JPanel();
		pnlTabOneUpper.setLayout(new FlowLayout());
		JLabel lblBounds = new JLabel("Restrict the upper and lower bounds for Time Critical DB Calls (seconds)", SwingConstants.CENTER);
		pnlTabOneUpper.add(lblBounds);
		
		pnlTabOne.add(pnlTabOneUpper);
		
		JPanel pnlTabOneLower = new JPanel();
		pnlTabOneLower.setLayout(new FlowLayout());
		
		JLabel lblLowerBound = new JLabel("Lower");
		pnlTabOneLower.add(lblLowerBound);
		
		tfLowerBound = new JTextField(Double.toString(view.lowerBound));
		tfLowerBound.setPreferredSize(new Dimension(100, 20));
		tfLowerBound.setHorizontalAlignment(JTextField.CENTER);
		pnlTabOneLower.add(tfLowerBound);
		
		JLabel lblUpperBound = new JLabel("Upper");
		pnlTabOneLower.add(lblUpperBound);
		
		if (view.upperBound.equals(Double.MAX_VALUE)){
			tfUpperBound = new JTextField("INF");
		} 
		else {
			tfUpperBound = new JTextField(Double.toString(view.upperBound));
		}
		tfUpperBound.setPreferredSize(new Dimension(100, 20));
		tfUpperBound.setHorizontalAlignment(JTextField.CENTER);
		pnlTabOneLower.add(tfUpperBound);
		
		pnlTabOne.add(pnlTabOneLower);
		
		pnlTabOne.add(Box.createVerticalGlue());
		
		tabbedPane.addTab("Time Critical Bounds", pnlTabOne);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		
	
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		UIManager.put("TabbedPane.contentBorderInsets", oldInsets); 
		pnlMain.add(tabbedPane);
		
		JPanel pnlSubmit = new JPanel();
		pnlSubmit.setLayout(new FlowLayout());
		pnlSubmit.setPreferredSize(new Dimension(600, 25));
		
		JButton btnSubmit = new JButton("Save Changes");
		btnSubmit.setPreferredSize(new Dimension(125, 20));
		btnSubmit.addActionListener(e -> {
			int result = JOptionPane.showConfirmDialog(this,"Save changes?", null, JOptionPane.YES_NO_OPTION);
            switch(result){
                case JOptionPane.YES_OPTION:
                	System.out.println("changes saved");
                	save(view, tabbedPane.getSelectedIndex());
                    return;
                case JOptionPane.NO_OPTION:
                	System.out.println("changes NOT saved");
                    return;
                case JOptionPane.CLOSED_OPTION:
                    return;
            }
		});
		
		pnlSubmit.add(btnSubmit);
		
		pnlMain.add(pnlSubmit);
		
		JPanel pnlTabTwo = new JPanel(false);
		pnlTabTwo.setLayout(new BoxLayout(pnlTabTwo, BoxLayout.Y_AXIS));
		pnlTabTwo.add(Box.createVerticalGlue());
		
		JPanel pnlTabTwoUpper = new JPanel();
		pnlTabTwoUpper.setLayout(new FlowLayout());
		JLabel lblNumLines = new JLabel("How many lines to display before and after the error message (0-10)", SwingConstants.CENTER);
		pnlTabTwoUpper.add(lblNumLines);
		
		pnlTabTwo.add(pnlTabTwoUpper);
		
		JPanel pnlTabTwoLower = new JPanel();
		pnlTabTwoLower.setLayout(new FlowLayout());
		
		JLabel lblNumLinesBefore = new JLabel("Before");
		pnlTabTwoLower.add(lblNumLinesBefore);

		tfNumLinesBefore = new JTextField(Integer.toString(view.numLinesBefore));
		tfNumLinesBefore.setPreferredSize(new Dimension(100, 20));
		tfNumLinesBefore.setHorizontalAlignment(JTextField.CENTER);
		pnlTabTwoLower.add(tfNumLinesBefore);
		
		JLabel lblNumLinesAfter = new JLabel("After");
		pnlTabTwoLower.add(lblNumLinesAfter);
		
		tfNumLinesAfter = new JTextField(Integer.toString(view.numLinesAfter));
		tfNumLinesAfter.setPreferredSize(new Dimension(100, 20));
		tfNumLinesAfter.setHorizontalAlignment(JTextField.CENTER);
		pnlTabTwoLower.add(tfNumLinesAfter);
		
		pnlTabTwo.add(pnlTabTwoLower);
		pnlTabTwo.add(Box.createVerticalGlue());
		
		tabbedPane.addTab("Lines before/after error messages", pnlTabTwo);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		
		getRootPane().setDefaultButton(btnSubmit);
		getContentPane().add(pnlMain, BorderLayout.CENTER);
		setVisible(true);
	}
	
	private void save(UserView view, int index){
		switch (index){
		case 0:
			saveTimeBounds(view);
			System.out.println("tab index zero");
			return;
		case 1:
			saveNumLines(view);
			System.out.println(view.numLinesBefore + " " + view.numLinesAfter);
			return;
		}
	}
	
	/**
	 * Saves the number of lines the user wishes to see before/after an error,
	 * and ensures that the input is valid.
	 * @param view The UserView associated with this PreferenceEditor
	 */
	private void saveNumLines(UserView view){
		int before = 0;
		int after = 0;
		try {
			before = Integer.parseInt(tfNumLinesBefore.getText());
			after = Integer.parseInt(tfNumLinesAfter.getText());
			if (before < 0 || after < 0){
				JOptionPane.showMessageDialog(this, "Value must not be negative");
				return;
			}
			else if (before > 10 || after > 10){
				JOptionPane.showMessageDialog(this, "Value must not exceed 10");
				return;
			}
		} catch (NumberFormatException e){
				JOptionPane.showMessageDialog(this, "Please enter a valid integer");
		}
		view.numLinesBefore = before;
		view.numLinesAfter = after;
	}
	
	/**
	 * Saves the time bounds that the user has inputed, and ensures
	 * that the time bounds are valid.
	 * @param view The UserView associated with this PreferenceEditor
	 */
	private void saveTimeBounds(UserView view){
		double low, high;
		try {
			low = Double.parseDouble(tfLowerBound.getText());
			if (low < 0){
				JOptionPane.showMessageDialog(this, "Invalid lower bound!");
				return;
			}
		} catch (NumberFormatException e){
			JOptionPane.showMessageDialog(this, "Please enter a valid lower bound");
			return;
		}
		if (tfUpperBound.getText().equals("INF")){
			high = Double.POSITIVE_INFINITY;
		}
		else {
			try{
				high = Double.parseDouble(tfUpperBound.getText());
				if (high < 0){
					JOptionPane.showMessageDialog(this,  "Invalid upper bound!");
					return;
				}
			} catch (NumberFormatException e){
				JOptionPane.showMessageDialog(this, "Please enter a valid upper bound");
				return;
			}
		}
		if (low > high){
			JOptionPane.showMessageDialog(this,  "Upper bound must be greater than lower bound");
			return;
		}
		view.upperBound = high;
		view.lowerBound = low;
	}

}
