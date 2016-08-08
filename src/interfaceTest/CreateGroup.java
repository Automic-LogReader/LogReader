/**
 * @file CreateGroup.java
 * @authors Leah Talkov, Jerry Tsui
 * @date 8/3/2016
 * Frame that allows the administrator to create, name, and save groups
 * of keywords to the database. These groups can be used to make
 * searching easier for the user. Admins cannot create duplicate
 * groups, and groups cannot have duplicate names. 
 */

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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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
import javax.swing.border.EmptyBorder;

import interfaceTest.CheckBoxList.CheckBoxListItem;
import interfaceTest.CheckBoxList.CheckBoxListRenderer;


/**
 * Dialog that allows a user to create and save search groups
 */
@SuppressWarnings("serial")
public class CreateGroup extends JDialog{
	private JTextField tfGroupName;
	private CheckBoxListItem[] cbListKeyWords;
	
	/**
	 * Constructor
	 * @param admin AdminView object passed in so preference values can be saved.
	 * @param view UserView object passed in so GUI can be updated 
	 * as the administrator creates groups
	 */
	public CreateGroup(AdminView admin, UserView view){
		prepareGUI(admin, view);
		Utility.addEscapeListener(this);
	};
	
	/**
	 * Creates and displays GUI
	 * @param admin AdminView object passed in so preference values can be saved
	 * @param view UserView object passed in so GUI can be updated
	 * as the administrator creates groups
	 */
	private void prepareGUI(AdminView admin, UserView view){
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
		setBounds(200, 200, 300, 300);
		setTitle("Create Groups");
		setLocationRelativeTo(null);
	
		pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
		pnlMain.add(Box.createVerticalGlue());
		pnlMain.add(createListDisplay(view));
		
		pnlMain.add(Box.createRigidArea(new Dimension(0, 5)));
		
		tfGroupName = new JTextField("Enter group name");
		tfGroupName.setForeground(Color.gray);
		tfGroupName.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
		        tfGroupName.setText("");
		        tfGroupName.setForeground(Color.black);
		    }

		    public void focusLost(FocusEvent e) {
		        // nothing
		    }
		});
		
		tfGroupName.setHorizontalAlignment(JTextField.CENTER);
		pnlMain.add(tfGroupName);
		
		pnlMain.add(Box.createRigidArea(new Dimension(0, 5)));
		
		JButton btnCreateGroup = new JButton("Create");
		btnCreateGroup.setPreferredSize(new Dimension(125, 20));
		btnCreateGroup.setAlignmentX(CENTER_ALIGNMENT);
		btnCreateGroup.addActionListener(e -> {
			if (Utility.noCheckBoxSelected(cbListKeyWords)){
				//Do nothing
			}
			else {
				try {
					saveGroups(admin, view);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				this.setVisible(false);
			}
		});
		pnlMain.add(btnCreateGroup);
		pnlMain.add(Box.createVerticalGlue());
		setVisible(true);
	}
	
	/**
	 * Helper function to create a list of checkboxes enumerating the 
	 * keywords that the user can select from to create a group
	 * @param view UserView object passed in so the interface can be updated
	 * as the adminstrator makes changes
	 * @return JScrollPane to be displayed in the frame
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
		         }
		});
		return new JScrollPane(list);
	}
	
	/**
	 * Helper function to save the group preferences that the Administrator created
	 * @param admin AdminView object passed in so preference values can be saved
	 * @param view UserView object passed in so GUI can be updated
	 * as the administrator creates groups
	 * @throws ClassNotFoundException if classpath is broken
	 * @throws SQLException if there is an error connecting to and querying the SQL server
	 */
	private void saveGroups(AdminView admin, UserView view) throws ClassNotFoundException, SQLException{
		StringBuilder query = new StringBuilder();
		for (int i=0; i<cbListKeyWords.length; i++){
			if (cbListKeyWords[i].isSelected()){
				query.append(cbListKeyWords[i].toString() + " ");
				System.out.println(cbListKeyWords[i].toString());
			}
		}
		if (view.GroupInfo.containsKey(tfGroupName.getText()) || view.GroupInfo.containsValue(query.toString())){
			JOptionPane.showMessageDialog(null, "No duplicates allowed");
			return;
		}
		else {
			String driver = "net.sourceforge.jtds.jdbc.Driver";
			Class.forName(driver);
			Connection conn = DriverManager.getConnection("jdbc:jtds:sqlserver://vwaswp02:1433/coeus", "coeus", "C0eus");
	
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("insert into Groups values (\'" + tfGroupName.getText() + 
								"\',\'" + query.toString() + "\')");
			view.loadGroupInfo(stmt);
			stmt.close();
			view.createGroupView();
			admin.updateGroups(view);
		}
	}

}
