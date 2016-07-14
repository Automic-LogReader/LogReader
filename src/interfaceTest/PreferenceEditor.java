package interfaceTest;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.util.HashSet;

import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import interfaceTest.CheckBoxList.CheckBoxListItem;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

@SuppressWarnings("serial")
public class PreferenceEditor extends JFrame {
	private JTextField lowerBound;
	private JTextField upperBound;
	protected CheckBoxListItem[] listOfKeyWords;
	protected JPanel listPanel;
	protected DefaultListModel<String> model;
	protected JList<String> list;
	
	public PreferenceEditor(UserView view) {
		prepareGUI(view);

	}
	
	void prepareGUI(UserView view){
		setTitle("Preference Editor");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 600, 240);
		setLocationRelativeTo(null);
		
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
		
		JPanel tab2 = new JPanel(false);
		tabbedPane.addTab("Error Groups", tab2);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		
		tab2.setLayout(new BoxLayout(tab2, BoxLayout.Y_AXIS));
		tab2.add(Box.createVerticalGlue());
		
		listPanel = displayGroups(view);
		tab2.add(listPanel);
		
		JPanel buttonPanel = new JPanel();
		tab2.add(buttonPanel);
		
		JButton createGroup = new JButton("Create Group");
		createGroup.setPreferredSize(new Dimension(125, 20));
		createGroup.setAlignmentX(CENTER_ALIGNMENT);
		createGroup.addActionListener(e -> {
			CreateGroup groupView = new CreateGroup(this, view);
		});
		buttonPanel.add(createGroup);
		
		JButton deleteGroup = new JButton("Delete Group");
		deleteGroup.setPreferredSize(new Dimension(125, 20));
		deleteGroup.setAlignmentX(CENTER_ALIGNMENT);
		deleteGroup.addActionListener(e -> {
			removeGroup(view);
		});
		buttonPanel.add(deleteGroup);
		
		tab2.add(Box.createVerticalGlue());
		
		JPanel tab3 = new JPanel(false);
		tabbedPane.addTab("AND/OR/NOT", tab3);
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
		
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
                	save(view);
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
		
		getRootPane().setDefaultButton(submitButton);
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		setVisible(true);
	}
	
	JPanel displayGroups(UserView view){
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
	    panel.add(Box.createHorizontalGlue());

	    model = new DefaultListModel<String>();
	    updateGroups(view);
	    list = new JList<String>(model);
	    JScrollPane scrollPane = new JScrollPane(list);
	    panel.add(scrollPane);
	    return panel;
	}
	
	void updateGroups(UserView view){
		model.clear();
		for (HashSet<String> list : view.keyWordGroups){
	    	StringBuilder listItem = new StringBuilder();
	    	listItem.setLength(0);
	    	for (String s : list){
	    		listItem.append(s + " ");
	    	}
	    	model.addElement(listItem.toString());
	    }
	}
	
	void removeGroup(UserView view){
		String groupToRemove = list.getSelectedValue();
		if (groupToRemove == null){
			JOptionPane.showMessageDialog(this, "No group selected");
			return;
		}
		System.out.println(groupToRemove);
		String[] array = groupToRemove.split(" ");
		HashSet<String> tempSet = new HashSet<String>();
		for (int i=0; i<array.length; i++){
			tempSet.add(array[i]);
		}
		for (HashSet<String> list : view.keyWordGroups){
	    	if (tempSet.equals(list)){
	    		if (view.keyWordGroups.remove(list)){
	    			System.out.println("removal successful");
	    		}
	    		break;
	    	}
	    }
		updateGroups(view);
	}
	
	void save(UserView view){
		saveTimeBounds(view);
	}
	
	void saveTimeBounds(UserView view){
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
	
	
	boolean noCheckBoxSelected(){
		for (int i = 0; i < listOfKeyWords.length; i++){
			if (listOfKeyWords[i].isSelected()){
				return false;
			}
		}
		JOptionPane.showMessageDialog(null, "Please select one or more checkboxes");
		return true;
	}
}
