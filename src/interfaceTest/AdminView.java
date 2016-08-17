
 /**
 * @file AdminView.java
 * @authors Leah Talkov, Jerry Tsui
 * @date 8/15/2016
 * Shows the frame for an Admin User. Consists of a JTable that displays
 * the information from the database table, and users can delete, add, or modify
 * entries of the data. The modifications are controlled by the functions 
 * in DataController.  
 */

package interfaceTest;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.sql.*;

import javax.swing.JFrame;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import java.awt.GridBagLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import java.awt.GridBagConstraints;
import javax.swing.JTable;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;

import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.JScrollBar;

@SuppressWarnings({ "serial", "unused" })
public class AdminView extends JFrame {
	
	/**Reflects the keywords that are currently in the database*/
	protected List<String> keyWords = new ArrayList<String>();
	/**Contains the keywords in the database, and serves as a temp list
	for modifications done to the table. Its contents are placed into
	keywords when the user presses save to default.*/
	List <String> savedWords = new ArrayList<String>();
	/**A list that contains the default values used to load up the JTable*/
	List <String[]> defaultList = new ArrayList<String[]>();
	/**A list that contains the original values, and serves as a temp list
	for modifications done to the table. */
	List <String[]> list = new ArrayList<String[]>();
	List <String[]> hyperlinkList = new ArrayList<String[]>();
	List <String[]> defaultHyperlinkList = new ArrayList<String[]>();
	/**Headers for the error JTable*/
	private final String[] errorTableColumnHeaders = {"Folder", "Keyword", "Log Error Description",
									"Suggested Solution"};
	/**Headers for the hyperlink JTable*/
	private final String[] hyperlinkColumnHeaders = {"Keyword", "Hyperlink"};
	/**Headers for the group JTable*/
	private final String[] groupColumnHeaders = {"Name", "Keywords"};
	/**JTable that displays the database contents and changes accordingly for user changes*/
	private JTable tblErrorEntries;
	/**JTable that displays the corresponding solution hyperlink for each keyword*/
	private JTable tblHyperlinkEntries;
	/**JTable that displays the current groups*/
	protected JTable tblGroupEntries;
	/**Main panel for the groups tab*/
	protected JPanel pnlGroups;
	/**Contains a list of the current groups*/
	protected JList<String> groupList;
	/**A model for the list of groups which is refreshed when groups are added or deleted*/
	protected DefaultListModel<String> defaultListModel;
	/**A selection of options for when the user wants to delete an entry*/
	Object[] options = {"Yes", "Cancel"};
	/**A model for the error table which is refreshed when the user adds, deletes, or mods an entry*/
	private DefaultTableModel errorTableModel;
	/**A model for the hyperlink table which is refreshed when the user adds, deletes, or mods an entry*/
	private DefaultTableModel hyperlinkTableModel;
	/**A model for the group table which is refreshed when the user adds, deletes, or mods an entry*/
	private DefaultTableModel groupTableModel;
	/**A private dataController that modifies the table content*/
	DataController dc;
	/**A 2-D Object array holding the information for the group*/
	protected Object groupRowData[][];
	private JTextField tfHyperlink;
	
	/**
	 * Creates an interface for an admin user, allowing the modification of entries
	 * @param view 
	 * @throws ClassNotFoundException If getClass was unsuccessful
	 * @throws SQLException If the connection to the server did not succeed
	 */
	public AdminView(UserView view) throws ClassNotFoundException, SQLException {
			
		try {
		     ClassLoader cl = this.getClass().getClassLoader();
		     ImageIcon programIcon = new ImageIcon(cl.getResource("res/logo.png"));
		     setIconImage(programIcon.getImage());
		  } catch (Exception e) {
		     System.out.println("Could not load program icon.");
		  }
		
		//We create the datatable with the database info, and set the lists within DataController
		createDataTable();
		dc = new DataController(this, view);
		dc.setHyperlinkList(hyperlinkList);
		dc.setDefaultHyperlinkList(defaultHyperlinkList);
		dc.setErrorList(list);
		dc.setDefaultErrorList(defaultList);
		dc.transferData("CHANGE");
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Admin Features");
		setBounds(200, 200, 1025, 350);
		setResizable(false);
		setLocationRelativeTo(null);
		
		JButton btnModifyHyperlink = new JButton("Save Changes");
		
		JPanel pnlMain = new JPanel();
		pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
		add(pnlMain);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		pnlMain.add(tabbedPane);
		
		JPanel pnlTabOne = new JPanel();
		pnlTabOne.setLayout(new BoxLayout(pnlTabOne, BoxLayout.Y_AXIS));
		
		JScrollPane scrollPane = new JScrollPane(tblErrorEntries, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pnlTabOne.add(scrollPane);
		
		initErrorTable();
		
		scrollPane.setViewportView(tblErrorEntries);
		
		pnlTabOne.add(Box.createRigidArea(new Dimension(0, 5)));
		
		JPanel pnlTabOneButtons = new JPanel();
		pnlTabOne.add(pnlTabOneButtons);
		pnlTabOneButtons.setLayout(new FlowLayout());
		
		JButton btnAddEntry = new JButton("Add Entry");
		btnAddEntry.setToolTipText("Changes will be made locally");
		btnAddEntry.addActionListener(e -> {
			btnModifyHyperlink.setEnabled(false);
			AddDialog add = new AddDialog(dc);
			add.setVisible(true);
		});
		pnlTabOneButtons.add(btnAddEntry);
		
		JButton btnModifyEntry = new JButton("Modify Entry");
		btnModifyEntry.setToolTipText("Changes will be made locally");
		btnModifyEntry.setHorizontalAlignment(SwingConstants.RIGHT);
		btnModifyEntry.addActionListener(e -> {
			//If the admin wants to modify a value we send DataController the current
			//cell contents of the entry they chose
			if(tblErrorEntries.getSelectedRow() != -1){
				btnModifyHyperlink.setEnabled(false);
				int rowSelect = tblErrorEntries.getSelectedRow();
				ModifyDialog modify = new ModifyDialog((String)tblErrorEntries.getValueAt(rowSelect, 0),
													 (String)tblErrorEntries.getValueAt(rowSelect, 1),
													 (String)tblErrorEntries.getValueAt(rowSelect, 2),
													 (String)tblErrorEntries.getValueAt(rowSelect, 3),
													 dc, rowSelect);
				modify.setVisible(true);
			}
			else JOptionPane.showMessageDialog(null, "Please select an entry");	
		});
		pnlTabOneButtons.add(btnModifyEntry);
		
		JButton btnDeleteEntry = new JButton("Delete Entry");
		btnDeleteEntry.addActionListener(e -> {
			int viewIndex = tblErrorEntries.getSelectedRow();
			if(viewIndex != -1) {
				//Ensures user wants to delete selected entry
				int confirmation = JOptionPane.showOptionDialog(this,
				    "This will delete the entire entry. Are you sure you want to continue?",
				    "",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
				    null, options, options[1]);
				//If yes, then we continue delete process
				if (confirmation == JOptionPane.YES_OPTION){
					btnModifyHyperlink.setEnabled(false);
					int modelIndex = tblErrorEntries.convertRowIndexToModel(viewIndex); 
					try {
						dc.deleteData(viewIndex);
					} catch (Exception e1) {
					e1.printStackTrace();
					}
				}
			} 
		});
		pnlTabOneButtons.add(btnDeleteEntry);
		
		JButton btnSaveToDatabase = new JButton("Save to Database");
		btnSaveToDatabase.setToolTipText("Local changes will be saved");
		btnSaveToDatabase.addActionListener(e -> {
			/*
			If the user wants to save to the database, then we call the function
			from DataController which will send all the queries to the database
			to reflect the changes done. The database will then have the same
			contents as the table the admin sees. 
			*/
			try {
				dc.saveDefault();
				String driver = "net.sourceforge.jtds.jdbc.Driver";
				Class.forName(driver);
				Connection conn = DriverManager.getConnection("jdbc:jtds:sqlserver://vwaswp02:1433/coeus", "coeus", "C0eus");
				Statement stmt = conn.createStatement();
				view.fillKeywords(stmt);
				view.createErrorDictionary(stmt);
				view.updateTreeView();
				btnModifyHyperlink.setEnabled(true);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		btnSaveToDatabase.setAlignmentX(Component.RIGHT_ALIGNMENT);
		pnlTabOneButtons.add(btnSaveToDatabase);
		
		JButton btnRevertLocalChanges = new JButton("Revert local changes");
		btnRevertLocalChanges.setAlignmentX(Component.RIGHT_ALIGNMENT);
		pnlTabOneButtons.add(btnRevertLocalChanges);
		btnRevertLocalChanges.addActionListener(e -> {
			//The table will revert back to the contents of the database, erasing
			//all changes the admin has done
			dc.getErrorList().clear();
			btnModifyHyperlink.setEnabled(true);
			for(int i = 0; i < dc.getDefaultErrorList().size(); i++) {
				dc.getErrorList().add(dc.getDefaultErrorList().get(i));
			}
			dc.getHyperlinkList().clear();
			for(int j = 0; j < dc.getDefaultHyperlinkList().size(); j++) {
				dc.getHyperlinkList().add(dc.getDefaultHyperlinkList().get(j));
			}
			dc.transferData("DEFAULT");
			resetErrorData();
			resetHyperlinkData();
		});
		//Group Table
		JPanel pnlTabTwo = new JPanel();
		pnlTabTwo.setLayout(new BoxLayout(pnlTabTwo, BoxLayout.Y_AXIS));

		JPanel pnlGroup = new JPanel();
		pnlGroup.setLayout(new BoxLayout(pnlGroup, BoxLayout.Y_AXIS));
		
		pnlGroup.add(Box.createRigidArea(new Dimension(0, 5)));
		
		JLabel lblGroup = new JLabel("Current Groups");
		lblGroup.setAlignmentX(CENTER_ALIGNMENT);
		pnlGroup.add(lblGroup);
		
		pnlGroup.add(Box.createRigidArea(new Dimension(0, 15)));
		
	    createGroupData(view);
	    groupTableModel = new DefaultTableModel(groupRowData, groupColumnHeaders){
	    	@Override
	        public boolean isCellEditable(int row, int column) {
	           //all cells false
	           return false;
	        }
	    };
	    tblGroupEntries = new JTable(groupTableModel);
	    
	    JScrollPane groupScrollPane = new JScrollPane();
	    groupScrollPane.setViewportView(tblGroupEntries);
	    
	    pnlGroup.add(groupScrollPane);
		
		pnlTabTwo.add(pnlGroup);
		
		JPanel pnlTabTwoButtons = new JPanel();
		pnlTabTwoButtons.setLayout(new FlowLayout());
		
		JButton btnCreateGroup = new JButton("Create Group");
		btnCreateGroup.setAlignmentX(CENTER_ALIGNMENT);
		btnCreateGroup.addActionListener(e -> {
			//Allows the admin to create groups of keywords and 
			//creates a CreateGroup object
			CreateGroup groupView = new CreateGroup(this, view);
		});
		pnlTabTwoButtons.add(btnCreateGroup);
		
		JButton btnEditGroup = new JButton("Edit Group");
		btnEditGroup.setAlignmentX(CENTER_ALIGNMENT);
		btnEditGroup.addActionListener(e -> {
			//Allows the admin to create groups of keywords and 
			//creates a CreateGroup object
			int row = tblGroupEntries.getSelectedRow();
			if (row == -1) { 
				JOptionPane.showMessageDialog(this, "No group selected");
				return; //If no row is selected
			}
			Object x = tblGroupEntries.getValueAt(row, 0);
			String groupName = (String) x;
			Object y = tblGroupEntries.getValueAt(row, 1);
			String groupKeywords = (String) y;
			EditGroup editGroup = new EditGroup(this, view, groupName, groupKeywords);
		});
		pnlTabTwoButtons.add(btnEditGroup);
		
		JButton btnDeleteGroup = new JButton("Delete Group");
		btnDeleteGroup.setAlignmentX(CENTER_ALIGNMENT);
		btnDeleteGroup.addActionListener(e -> {
			//The admin can delete an existing group
			try {
				removeGroup(view);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		pnlTabTwoButtons.add(btnDeleteGroup);
		
		pnlTabTwo.add(pnlTabTwoButtons);
		
		//Hyperlink Table
		JPanel pnlTabThree = new JPanel();
		pnlTabThree.setLayout(new BoxLayout(pnlTabThree, BoxLayout.Y_AXIS));
		pnlTabThree.add(Box.createVerticalGlue());
		
		JScrollPane pnlHyperlinkTable = new JScrollPane();
		pnlTabThree.add(pnlHyperlinkTable);
		initHyperlinkTable();
		pnlHyperlinkTable.setViewportView(tblHyperlinkEntries);
		
		btnModifyHyperlink.addActionListener(e -> {
			tblHyperlinkEntries.setValueAt(tfHyperlink.getText(), tblHyperlinkEntries.getSelectedRow(), 1);
			for(int i = 0; i < dc.getDefaultHyperlinkList().size(); i++) {
				dc.getDefaultHyperlinkList().get(i)[1] = (String)tblHyperlinkEntries.getValueAt(i, 1);
				dc.getHyperlinkList().get(i)[1] = (String)tblHyperlinkEntries.getValueAt(i, 1);
			}
			try {
				dc.writeURLsToDB();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		
		JPanel pnlHyperlink = new JPanel();
		pnlHyperlink.setLayout(new BoxLayout(pnlHyperlink, BoxLayout.X_AXIS));
		Component rigidArea_1 = Box.createRigidArea(new Dimension (20, 20));
		Component rigidArea_2 = Box.createRigidArea(new Dimension (20, 20));
		Component rigidArea_3 = Box.createRigidArea(new Dimension (20, 20));
		Component rigidArea_4 = Box.createRigidArea(new Dimension (20, 20));
		Component rigidArea_5 = Box.createRigidArea(new Dimension (20, 20));
		Component rigidArea_6 = Box.createRigidArea(new Dimension (20, 20));
		Component rigidArea_7 = Box.createRigidArea(new Dimension (20, 20));
		Component rigidArea_8 = Box.createRigidArea(new Dimension (20, 20));
		Component rigidArea_9 = Box.createRigidArea(new Dimension (20, 20));
		Component rigidArea_10 = Box.createRigidArea(new Dimension (20, 20));
		Component rigidArea_11 = Box.createRigidArea(new Dimension (20, 20));
		tfHyperlink = new JTextField();
		pnlHyperlink.add(rigidArea_1);
		pnlHyperlink.add(rigidArea_2);
		pnlHyperlink.add(rigidArea_3);
		pnlHyperlink.add(rigidArea_4);
		pnlHyperlink.add(rigidArea_5);
		pnlHyperlink.add(tfHyperlink);
		pnlHyperlink.add(rigidArea_6);
		pnlHyperlink.add(btnModifyHyperlink);
		pnlHyperlink.add(rigidArea_7);
		pnlHyperlink.add(rigidArea_8);
		pnlHyperlink.add(rigidArea_9);
		pnlHyperlink.add(rigidArea_10);
		pnlHyperlink.add(rigidArea_11);
		//btnModifyHyperlink.setAlignmentX(CENTER_ALIGNMENT);
		pnlTabThree.add(Box.createRigidArea(new Dimension(0,5)));
		//pnlTabThree.add(btnModifyHyperlink);
		pnlTabThree.add(pnlHyperlink);
		pnlTabThree.add(Box.createRigidArea(new Dimension(0,5)));
		pnlTabThree.add(Box.createVerticalGlue());
		
		tabbedPane.add("Edit Entries", pnlTabOne);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		tabbedPane.add("Edit Groups", pnlTabTwo);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		tabbedPane.add("Edit Solution Hyperlinks", pnlTabThree);
		tabbedPane.setMnemonicAt(2,  KeyEvent.VK_3);
	}
	
	/**
	 * Fills myData with arrays. Each array represents a entry 
	 * from the database where each entry is loaded into an array index. 
	 * The resulting array is used as a JTable parameter. 
	 * @throws SQLException If connection to SQL server failed
	 * @throws ClassNotFoundException if getClass was unsuccessful
	 */
	void createDataTable() throws SQLException, ClassNotFoundException {		
		String driver = "net.sourceforge.jtds.jdbc.Driver";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection("jdbc:jtds:sqlserver://vwaswp02:1433/coeus", "coeus", "C0eus");

		Statement stmt = conn.createStatement();
		String query2 = "select Keyword, Log_Error_Description, "+
						"Suggested_Solution, Folder, Hyperlink from logerrors";
		ResultSet rs = stmt.executeQuery(query2);
		while(rs.next())
		{
			String[] entry = new String[4];
			entry[0] = rs.getString("Folder");
			entry[1] = rs.getString("Keyword");
			entry[2] = rs.getString("Log_Error_Description");
			entry[3] = rs.getString("Suggested_Solution");
			keyWords.add(rs.getString("Keyword"));
			savedWords.add(rs.getString("Keyword"));
			list.add(entry);
			defaultList.add(entry);
			
			String[] hyperlinkEntry = new String[2];
			hyperlinkEntry[0] = rs.getString("Keyword");
			hyperlinkEntry[1] = rs.getString("Hyperlink");
			hyperlinkList.add(hyperlinkEntry);
			defaultHyperlinkList.add(hyperlinkEntry);
		}
		stmt.close();
	}
	
	/**
	 * Creates a JPanel that shows the groups that are currently
	 * in the database. The panel is updated as groups are 
	 * added and deleted. 
	 * @param view The current JFrame for AdminView
	 * @return The Panel that displays the groups
	 */
	JPanel createGroupDisplay (UserView view){
		JPanel pnlGroup = new JPanel();
		pnlGroup.setLayout(new BoxLayout(pnlGroup, BoxLayout.Y_AXIS));
		
		JLabel lblGroup = new JLabel("Current Groups");
		lblGroup.setAlignmentX(CENTER_ALIGNMENT);
		pnlGroup.add(lblGroup);
		
	    createGroupData(view);
	    groupTableModel = new DefaultTableModel(groupRowData, groupColumnHeaders);
	    tblGroupEntries = new JTable(groupTableModel);
	    tblGroupEntries.setRowSelectionAllowed(true);
	    tblGroupEntries.setColumnSelectionAllowed(true);
	    pnlGroup.add(tblGroupEntries);
	    return pnlGroup;
	}
	
	/**
	 * Updates the defaultListModel depending on the changes
	 * the Admin has done (if they have created or deleted a group)
	 * @param view The current JFrame for AdminView
	 */
	protected void createGroupData(UserView view){
		groupRowData = new Object[view.GroupInfo.size()][2];
		int i = 0;
		for (Map.Entry<String, String> entry : view.GroupInfo.entrySet()){
			groupRowData[i][0] = entry.getKey();
			groupRowData[i][1] = entry.getValue();
	    	i++;
		}	
	}
	
	/**
	 * Function called in CreateGroup.java to help udpate the tablemodel when new groups are added
	 * @param view The UserView to extact the group data from
	 */
	protected void updateGroupData(UserView view){
		createGroupData(view);
		groupTableModel = new DefaultTableModel(groupRowData, groupColumnHeaders);
		tblGroupEntries.setModel(groupTableModel);
		groupTableModel.fireTableDataChanged();
	}
	
	/**
	 * Removes a group from the database. Brings
	 * up a JDialog if the admin presses the delete group button, but has
	 * not selected a group to delete. 
	 * @param view The current JFrame used for AdminView
	 * @throws ClassNotFoundException If getClass was unsuccessful
	 * @throws SQLException If connection to SQL server failed
	 */
	private void removeGroup(UserView view) throws ClassNotFoundException, SQLException{
		int row = tblGroupEntries.getSelectedRow();
		if (row == -1) { 
			JOptionPane.showMessageDialog(this, "No group selected");
			return; //If no row is selected
		}
		Object group = tblGroupEntries.getValueAt(row, 1);
		String groupToRemove = (String) group;
		
		String driver = "net.sourceforge.jtds.jdbc.Driver";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection("jdbc:jtds:sqlserver://vwaswp02:1433/coeus", "coeus", "C0eus");

		Statement stmt = conn.createStatement();
		stmt.executeUpdate("delete from Groups where GroupKeywords = \'" + groupToRemove + "\'");
		view.loadGroupInfo(stmt);
		stmt.close();

		System.out.println(groupToRemove);
		StringBuilder query = new StringBuilder();
		
		createGroupData(view);
		updateGroupData(view);
		view.createGroupView();
	}
	
	/**
	 * Updates the default table model, and is called by functions 
	 * in the DataController. This happens when a new entry is made, edited,
	 * or deleted, and the table properly reflects the changes made by the user.
	 */
	protected void resetErrorData(){
		DefaultTableModel model = new DefaultTableModel(dc.getErrorData(), errorTableColumnHeaders); 
		tblErrorEntries.setModel(model);
		resizeColumnWidth(tblErrorEntries);
		model.fireTableDataChanged();
	}
	
	protected void resetHyperlinkData() {
		DefaultTableModel model = new DefaultTableModel(dc.getHyperlinkData(), hyperlinkColumnHeaders); 
		tblHyperlinkEntries.setModel(model);
		resizeColumnWidth(tblHyperlinkEntries);
		model.fireTableDataChanged();
	}
	
	/**
	 * Initializes the table of errors, suggested solutions, folders, etc
	 * that can be modified by the Administrator
	 */
	private void initErrorTable(){
		errorTableModel = new DefaultTableModel(dc.getErrorData(), errorTableColumnHeaders) {
		    @Override
		    public boolean isCellEditable(int row, int column) {
		       //all cells false
		       return false;
		    }     
		    
		};
		tblErrorEntries = new JTable(errorTableModel){
			//Renders each columnn to fit the data
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component component = super.prepareRenderer(renderer, row, column);
				int rendererWidth = component.getPreferredSize().width;
	            TableColumn tableColumn = getColumnModel().getColumn(column);
	            tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
	            return component;
			}

		};
		resizeColumnWidth(tblErrorEntries);	
		tblErrorEntries.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tblErrorEntries.setRowSelectionAllowed(true);
	}
	
	private void initHyperlinkTable() {
		hyperlinkTableModel = new DefaultTableModel(dc.getHyperlinkData(), hyperlinkColumnHeaders) {
		    
		    public boolean isCellEditable(int row, int column) {
		    		return false;
		    }     
		};
		tblHyperlinkEntries = new JTable(hyperlinkTableModel) {
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component component = super.prepareRenderer(renderer, row, column);
				int rendererWidth = component.getPreferredSize().width;
	            TableColumn tableColumn = getColumnModel().getColumn(column);
	            tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
	            return component;
			}
		};
		tblHyperlinkEntries.addMouseListener(new MouseAdapter(){
			@Override
			//Fills the hyperlink textbox with the current 
		    public void mouseClicked(MouseEvent evnt) {
		        if (evnt.getClickCount() == 1) 
		            tfHyperlink.setText((String)tblHyperlinkEntries.getValueAt
		            		(tblHyperlinkEntries.getSelectedRow(), 1));
		    }
		});
		tblHyperlinkEntries.setRowSelectionAllowed(true);
		resizeColumnWidth(tblHyperlinkEntries);	
	}
	
	
	/**
	 * Resizes the columns of a Jtable to fit the contents of the entries given.
	 * The column will be the size of that max value within that column.
	 * @param table The JTable that is being displayed to the admin
	 */
	public void resizeColumnWidth(JTable table) {
	    final TableColumnModel columnModel = table.getColumnModel();
	    for (int column = 0; column < table.getColumnCount(); column++) {
	        int width = 50; // Min width
	        for (int row = 0; row < table.getRowCount(); row++) {
	            TableCellRenderer renderer = table.getCellRenderer(row, column);
	            Component comp = table.prepareRenderer(renderer, row, column);
	            width = Math.max(comp.getPreferredSize().width +1 , width);
	        }
	        columnModel.getColumn(column).setPreferredWidth(width);
	    }
	}
	
	
}
