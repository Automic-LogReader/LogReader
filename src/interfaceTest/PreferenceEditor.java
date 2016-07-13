package interfaceTest;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;

import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

@SuppressWarnings("serial")
public class PreferenceEditor extends JFrame {
	private JTextField lowerBound;
	private JTextField upperBound;
	
	public PreferenceEditor(UserView view) {
		prepareGUI(view);

	}
	
	void prepareGUI(UserView view){
		setTitle("Preference Editor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
		
		lowerBound = new JTextField("0");
		lowerBound.setPreferredSize(new Dimension(100, 20));
		lowerBound.setHorizontalAlignment(JTextField.CENTER);
		tab1_lowerPanel.add(lowerBound);
		
		JLabel upper = new JLabel("Upper");
		tab1_lowerPanel.add(upper);
		
		upperBound = new JTextField("INF");
		upperBound.setPreferredSize(new Dimension(100, 20));
		upperBound.setHorizontalAlignment(JTextField.CENTER);
		tab1_lowerPanel.add(upperBound);
		
		tab1.add(tab1_lowerPanel);
		
		tab1.add(Box.createVerticalGlue());
		
		tabbedPane.addTab("Groups", tab1);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		
		JPanel tab2 = new JPanel(false);
		tabbedPane.addTab("Time Critical Bounds", tab2);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_2);
		
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		UIManager.put("TabbedPane.contentBorderInsets", oldInsets); 
		mainPanel.add(tabbedPane);
		
		JPanel submitPane = new JPanel();
		submitPane.setLayout(new FlowLayout());
		submitPane.setPreferredSize(new Dimension(600, 25));
		
		JButton submitButton = new JButton("Submit");
		submitButton.setPreferredSize(new Dimension(80, 20));
		submitButton.addActionListener(e -> {
			int result = JOptionPane.showConfirmDialog(this,"Save changes?","",JOptionPane.YES_NO_OPTION);
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
		
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		setVisible(true);
	}
	
	void save(UserView view){
		saveTimeBounds(view);
	}
	
	void saveTimeBounds(UserView view){
		try {
			double low = Double.parseDouble(lowerBound.getText());
			view.lowerBound = low;
			System.out.println("Lower bound: " + low);
		} catch (NumberFormatException e){
			JOptionPane.showMessageDialog(this, "Please enter a valid lower bound");
			return;
		}
		if (upperBound.getText().equals("INF")){
			view.upperBound = Double.POSITIVE_INFINITY;
		}
		else {
			try{
				double high = Double.parseDouble(upperBound.getText());
				view.upperBound = high;
				System.out.println("Higher bound: " + high);
			} catch (NumberFormatException e){
				JOptionPane.showMessageDialog(this, "Please enter a valid upper bound");
				return;
			}
		}
	}
}
