package interfaceTest;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;                                                                                                                                                                             
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import interfaceTest.CheckBoxList.CheckBoxListItem;
import interfaceTest.CheckBoxList.CheckBoxListRenderer;

@SuppressWarnings("serial")
public class PreferenceEditor extends JDialog {
	private JTextField lowerBound;
	private JTextField upperBound;
	private JTextField tfNumLinesBefore;
	private JTextField tfNumLinesAfter;
	protected CheckBoxListItem[] listOfKeyWords;
	protected JPanel listPanel;
	protected DefaultListModel<String> model;
	protected JList<String> list;
	private JComboBox<String> comboBox;
	//Used for deleting elements in the expression buidler
	boolean isAdmin;
	public PreferenceEditor(UserView view, boolean admin) {
		this.isAdmin = admin;
		prepareGUI(view);
	}
	
	void prepareGUI(UserView view){
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
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		JPanel tab1 = new JPanel(false);
		tab1.setLayout(new BoxLayout(tab1, BoxLayout.Y_AXIS));
		tab1.add(Box.createVerticalGlue());
		
		JPanel tab1_upperPanel = new JPanel();
		tab1_upperPanel.setLayout(new FlowLayout());
		JLabel label1 = new JLabel("Restrict the upper and lower bounds for Time Critical DB Calls (seconds)", SwingConstants.CENTER);
		tab1_upperPanel.add(label1);
		
		tab1.add(tab1_upperPanel);
		
		JPanel tab1_lowerPanel = new JPanel();
		tab1_lowerPanel.setLayout(new FlowLayout());
		
		JLabel lower = new JLabel("Lower");
		tab1_lowerPanel.add(lower);
		
		lowerBound = new JTextField(Double.toString(view.lowerBound));
		lowerBound.setPreferredSize(new Dimension(100, 20));
		lowerBound.setHorizontalAlignment(JTextField.CENTER);
		tab1_lowerPanel.add(lowerBound);
		
		JLabel upper = new JLabel("Upper");
		tab1_lowerPanel.add(upper);
		
		if (view.upperBound.equals(Double.MAX_VALUE)){
			upperBound = new JTextField("INF");
		} 
		else {
			upperBound = new JTextField(Double.toString(view.upperBound));
		}
		upperBound.setPreferredSize(new Dimension(100, 20));
		upperBound.setHorizontalAlignment(JTextField.CENTER);
		tab1_lowerPanel.add(upperBound);
		
		tab1.add(tab1_lowerPanel);
		
		tab1.add(Box.createVerticalGlue());
		
		tabbedPane.addTab("Time Critical Bounds", tab1);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		
	
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		UIManager.put("TabbedPane.contentBorderInsets", oldInsets); 
		mainPanel.add(tabbedPane);
		
		JPanel submitPane = new JPanel();
		submitPane.setLayout(new FlowLayout());
		submitPane.setPreferredSize(new Dimension(600, 25));
		
		JButton submitButton = new JButton("Save Changes");
		submitButton.setPreferredSize(new Dimension(125, 20));
		submitButton.addActionListener(e -> {
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
		
		submitPane.add(submitButton);
		
		mainPanel.add(submitPane);
		
		JPanel tab2 = new JPanel(false);
		tab2.setLayout(new BoxLayout(tab2, BoxLayout.Y_AXIS));
		tab2.add(Box.createVerticalGlue());
		
		JPanel tab2_upperPanel = new JPanel();
		tab2_upperPanel.setLayout(new FlowLayout());
		JLabel label2 = new JLabel("How many lines to display before and after the error message (0-10)", SwingConstants.CENTER);
		tab2_upperPanel.add(label2);
		
		tab2.add(tab2_upperPanel);
		
		JPanel tab2_lowerPanel = new JPanel();
		tab2_lowerPanel.setLayout(new FlowLayout());
		
		JLabel lblNumLinesBefore = new JLabel("Before");
		tab2_lowerPanel.add(lblNumLinesBefore);
		//if (view.numLinesBefore.equals("0"dd))
		tfNumLinesBefore = new JTextField("0");
		tfNumLinesBefore.setPreferredSize(new Dimension(100, 20));
		tfNumLinesBefore.setHorizontalAlignment(JTextField.CENTER);
		tab2_lowerPanel.add(tfNumLinesBefore);
		
		JLabel lblNumLinesAfter = new JLabel("After");
		tab2_lowerPanel.add(lblNumLinesBefore);
		
		tfNumLinesAfter = new JTextField("0");
		tfNumLinesAfter.setPreferredSize(new Dimension(100, 20));
		tfNumLinesAfter.setHorizontalAlignment(JTextField.CENTER);
		tab2_lowerPanel.add(tfNumLinesAfter);
		
		tab2.add(tab2_lowerPanel);
		tab2.add(Box.createVerticalGlue());
		
		tabbedPane.addTab("Lines before/after error messages", tab2);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		
		getRootPane().setDefaultButton(submitButton);
		getContentPane().add(mainPanel, BorderLayout.CENTER);
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
	
	private void saveNumLines(UserView view){
		int before = 0, after = 0;
		try {
			before = Integer.parseInt(tfNumLinesBefore.getText());
			if (before < 0){
				JOptionPane.showMessageDialog(this, "Value must not be negative");
				return;
			}
			else if (before > 10){
				JOptionPane.showMessageDialog(this, "Value must not exceed 10");
				return;
			}
		} catch (NumberFormatException e){
				JOptionPane.showMessageDialog(this, "Please enter a valid lower bound");
		}
		try {
			after = Integer.parseInt(tfNumLinesAfter.getText());
			if (after < 0){
				JOptionPane.showMessageDialog(this, "Value must not be negative");
				return;
			}
			else if (after > 10){
				JOptionPane.showMessageDialog(this, "Value must not exceed 10");
				return;
			}
		} catch (NumberFormatException e){
				JOptionPane.showMessageDialog(this, "Please enter a valid lower bound");
		}
		view.numLinesBefore = before;
		view.numLinesAfter = after;
	}
	private void saveTimeBounds(UserView view){
		double low, high;
		try {
			low = Double.parseDouble(lowerBound.getText());
			if (low < 0){
				JOptionPane.showMessageDialog(this, "Invalid lower bound!");
				return;
			}
			//view.lowerBound = low;
			System.out.println("Lower bound: " + low);
		} catch (NumberFormatException e){
			JOptionPane.showMessageDialog(this, "Please enter a valid lower bound");
			return;
		}
		if (upperBound.getText().equals("INF")){
			high = Double.POSITIVE_INFINITY;
		}
		else {
			try{
				high = Double.parseDouble(upperBound.getText());
				if (high < 0){
					JOptionPane.showMessageDialog(this,  "Invalid upper bound!");
					return;
				}
				//view.upperBound = high;
				System.out.println("Higher bound: " + high);
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
	
	
	void populateComboBox(UserView view){
		for (String s : view.originalKeyWords){
			comboBox.addItem(s);
		}
	}
}
