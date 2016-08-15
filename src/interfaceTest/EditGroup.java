/**
 * @file EditGroup.java
 * @authors Leah Talkov, Jerry Tsui
 * @date 8/15/2016
 * Brings up a JDialog where users can edit the contents
 * or name of the group they've selected.
 */

package interfaceTest;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import interfaceTest.CheckBoxList.CheckBoxListItem;
import interfaceTest.CheckBoxList.CheckBoxListRenderer;

public class EditGroup extends JDialog {
	private JLabel lblGroupName;
	private JTextField tfKeywords;
	private String groupName;
	private String groupKeywords;
	private CheckBoxListItem[] cbListKeyWords;
	
	public EditGroup(AdminView admin, UserView view, String groupName, String keyWords) {
		prepareGUI(admin, view, groupName, keyWords);
		Utility.addEscapeListener(this);
	}
	/**
	 * Creates and displays GUI
	 * @param admin AdminView object passed in so preference values can be saved
	 * @param view UserView object passed in so GUI can be updated
	 * @param groupName The name of the current group being edited
	 * @param keyWords The keywords that are currently within this group
	 * as the administrator creates groups
	 */
	private void prepareGUI(AdminView admin, UserView view, String groupName, String keyWords){
		try {
		     ClassLoader cl = this.getClass().getClassLoader();
		     ImageIcon programIcon = new ImageIcon(cl.getResource("res/logo.png"));
		     setIconImage(programIcon.getImage());
		  } catch (Exception e) {
		     System.out.println("Could not load program icon.");
		  }
		JPanel pnlMain = new JPanel(false);
		getContentPane().add(pnlMain, BorderLayout.CENTER);
		pnlMain.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
		setBounds(200, 200, 500, 300);
		setResizable(false);
		setTitle("Edit Groups");
		setLocationRelativeTo(null);
	
		pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
		
		lblGroupName = new JLabel("Group Name: " + groupName);
		lblGroupName.setAlignmentX(CENTER_ALIGNMENT);
		
		pnlMain.add(lblGroupName);
		pnlMain.add(Box.createRigidArea(new Dimension(0, 5)));
		
		tfKeywords = new JTextField(keyWords);
		tfKeywords.setEditable(false);
		tfKeywords.setHorizontalAlignment(JTextField.CENTER);
		tfKeywords.setOpaque(true);
		tfKeywords.setBackground(Color.WHITE);
		
		
		pnlMain.add(tfKeywords);
		pnlMain.add(Box.createRigidArea(new Dimension(0, 5)));
		
		pnlMain.add(createListDisplay(view));
		pnlMain.add(Box.createRigidArea(new Dimension(0, 5)));
		
		initCheckBox(keyWords);
		
		JButton btnSaveChanges = new JButton("Save Changes");
		btnSaveChanges.setPreferredSize(new Dimension(125, 20));
		btnSaveChanges.setAlignmentX(CENTER_ALIGNMENT);
		btnSaveChanges.addActionListener(e -> {
			if (Utility.noCheckBoxSelected(cbListKeyWords)){
				//Do nothing
			}
			else {
				this.groupName = groupName;
				this.groupKeywords = tfKeywords.getText();
				try {
					editGroups(admin, view);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				this.setVisible(false);
			}
		});
		
		pnlMain.add(btnSaveChanges);
		
		pnlMain.add(Box.createVerticalGlue());
		
		setVisible(true);
	}
	
	/**
	 * Creates a checkbox display of the current groups
	 * @param view UserView associated with this object
	 * @return Returns a scrollpane with the new contents 
	 */
	private JScrollPane createListDisplay(UserView view){
		cbListKeyWords = new CheckBoxListItem[view.keyWords.size()];
		int index = 0;
		for (String s : view.keyWords){
			cbListKeyWords[index] = new CheckBoxListItem(s);
			++index;
		}
		JList<CheckBoxListItem> list = new JList<CheckBoxListItem>(cbListKeyWords);
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
		            String currKeywords = tfKeywords.getText();
		            String selectedKeyword = item.toString();
		            if (currKeywords.contains(selectedKeyword)){
		            	currKeywords = currKeywords.replace(selectedKeyword, "");
		            }
		            else {
		            	currKeywords += (selectedKeyword + " ");
		            }
		            tfKeywords.setText(currKeywords);
		         }
		});
		return new JScrollPane(list);
	}
	
	/**
	 * Creates queries to the database that updates the groups in the table
	 * @param admin AdminView associated with this object
	 * @param view UserView associated with this object
	 * @throws ClassNotFoundException If getClass was unsuccessful
	 * @throws SQLException If connection or querying to SQL server failed
	 */
	private void editGroups(AdminView admin, UserView view) throws ClassNotFoundException, SQLException{
		StringBuilder query = new StringBuilder();
		for (int i=0; i<cbListKeyWords.length; i++){
			if (cbListKeyWords[i].isSelected()){
				query.append(cbListKeyWords[i].toString() + " ");
			}
		}
		this.groupKeywords = query.toString();
		
		if (isDuplicate(this.groupKeywords, view)){
			return;
		}
		
		String driver = "net.sourceforge.jtds.jdbc.Driver";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection("jdbc:jtds:sqlserver://vwaswp02:1433/coeus", "coeus", "C0eus");

		Statement stmt = conn.createStatement();
		stmt.executeUpdate("update Groups set GroupKeywords = \'" + 
				Utility.addSingleQuote(groupKeywords) + "\' where GroupName = \'" + Utility.addSingleQuote(groupName) + "\'");
		view.loadGroupInfo(stmt);
		stmt.close();
		view.createGroupView();
		admin.updateGroupData(view);
	}
	
	/**
	 * Checks to see if there are groups with the same content.
	 * @param keyword The String containing the list of keywords
	 * @param view UserView associated with this object
	 * @return True if there are duplicate groups, false otherwise
	 */
	private boolean isDuplicate(String keyword, UserView view){
		String[] keywordArray = keyword.split(" ");
		HashSet<String> keywordSet = new HashSet<String>(Arrays.asList(keywordArray));
		ArrayList<HashSet<String>> listOfSets = new ArrayList<HashSet<String>>();
		
		for (Map.Entry<String, String> entry : view.GroupInfo.entrySet()){
			String[] array = entry.getValue().split(" ");
			HashSet<String> set = new HashSet<String>(Arrays.asList(array));
			listOfSets.add(set);
		}
		
		for (Set<String> set : listOfSets){
			if (keywordSet.equals(set)){
				JOptionPane.showMessageDialog(null, "No duplicates allowed");
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Initializes a checkbox object for the given String
	 * @param keyword String that holds the list of keywords for a group
	 */
	private void initCheckBox(String keyword){
		String[] keywordArray = keyword.split(" ");
		for (int i = 0; i < cbListKeyWords.length; i++){
			for (String s : keywordArray){
				if (cbListKeyWords[i].toString().equals(s)){
					cbListKeyWords[i].setSelected(true);
				}
			}
		}
	}
	
}
