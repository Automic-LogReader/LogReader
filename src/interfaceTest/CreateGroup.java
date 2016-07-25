package interfaceTest;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.EmptyBorder;

import interfaceTest.CheckBoxList.CheckBoxListItem;
import interfaceTest.CheckBoxList.CheckBoxListRenderer;

public class CreateGroup extends JDialog{
	public CreateGroup(PreferenceEditor editor, UserView view){
		prepareGUI(editor, view);
		addEscapeListener(this);
	};
	
	void prepareGUI(PreferenceEditor editor, UserView view){
		try {
		     ClassLoader cl = this.getClass().getClassLoader();
		     ImageIcon programIcon = new ImageIcon(cl.getResource("res/logo.png"));
		     setIconImage(programIcon.getImage());
		  } catch (Exception e) {
		     System.out.println("Could not load program icon.");
		  }
		JPanel mainPanel = new JPanel(false);
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		mainPanel.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
		setBounds(200, 200, 300, 200);
		setTitle("Create Groups");
		setLocationRelativeTo(null);
	
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(Box.createVerticalGlue());
		mainPanel.add(createListDisplay(editor, view));
		
		mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		
		JTextField nameTextField = new JTextField("Enter group name");
		nameTextField.setForeground(Color.gray);
		nameTextField.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
		        nameTextField.setText("");
		        nameTextField.setForeground(Color.black);
		    }

		    public void focusLost(FocusEvent e) {
		        // nothing
		    }
		});
		
		nameTextField.setHorizontalAlignment(JTextField.CENTER);
		mainPanel.add(nameTextField);
		
		mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		
		JButton okButton = new JButton("Create");
		okButton.setPreferredSize(new Dimension(125, 20));
		okButton.setAlignmentX(CENTER_ALIGNMENT);
		okButton.addActionListener(e -> {
			if (noCheckBoxSelected(editor)){
				JOptionPane.showMessageDialog(null, "Please select one or more checkboxes");
			}
			else {
				saveGroups(editor, view);
				editor.updateGroups(view);
				view.createGroupDisplay();
				this.setVisible(false);
			}
		});
		mainPanel.add(okButton);
		mainPanel.add(Box.createVerticalGlue());
		setVisible(true);
	}
	
	JScrollPane createListDisplay(PreferenceEditor editor, UserView view){
		editor.listOfKeyWords = new CheckBoxListItem[view.keyWords.size()];
		int index = 0;
		for (String s : view.keyWords){
			editor.listOfKeyWords[index] = new CheckBoxListItem(s);
			++index;
		}
		JList<CheckBoxListItem> list = new JList<CheckBoxListItem>(editor.listOfKeyWords);
		list.setCellRenderer(new CheckBoxListRenderer());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addMouseListener(new MouseAdapter(){
			 public void mouseClicked(MouseEvent event) {
		            @SuppressWarnings("unchecked")
					JList<CheckBoxListItem> list =
		               (JList<CheckBoxListItem>) event.getSource();
		            // Get index of item clicked
		            int index = list.locationToIndex(event.getPoint());
		            CheckBoxListItem item = (CheckBoxListItem) list.getModel()
		                  .getElementAt(index);
		            // Toggle selected state
		            item.setSelected(!item.isSelected());
		            // Repaint cell
		            list.repaint(list.getCellBounds(index, index));
		         }
		});
		return new JScrollPane(list);
	}
	
	void saveGroups(PreferenceEditor editor, UserView view){
		HashSet<String> group = new HashSet<String>();
		for (int i=0; i<editor.listOfKeyWords.length; i++){
			if (editor.listOfKeyWords[i].isSelected()){
				group.add(editor.listOfKeyWords[i].toString());
				System.out.println(editor.listOfKeyWords[i].toString());
			}
		}
		view.keyWordGroups.add(group);
	}
	
	boolean noCheckBoxSelected(PreferenceEditor editor){
		for (int i = 0; i < editor.listOfKeyWords.length; i++){
			if (editor.listOfKeyWords[i].isSelected()){
				return false;
			}
		}
		return true;
	}
	
	public static void addEscapeListener(final JDialog dialog) {
	    ActionListener escListener = new ActionListener() {

	        @Override
	        public void actionPerformed(ActionEvent e) {
	            dialog.setVisible(false);
	        }
	    };

	    dialog.getRootPane().registerKeyboardAction(escListener,
	            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
	            JComponent.WHEN_IN_FOCUSED_WINDOW);
	}
}
