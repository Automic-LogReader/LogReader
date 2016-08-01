
 /**
 * file: AdminView.java
 * This class shows the frame for the AdminView. It consists of a JTable that displays
 * the information in LogErrors_Suggestions.csv, and users can delete, add, or modify
 * entries of the data. This class does this by creating a new file, rewriting the
 * data into it from the old file (depending on what action was chosen), deleting
 * the old file, and finally renaming the new file.  
 */

package interfaceTest;

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
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollBar;

@SuppressWarnings({ "serial", "unused" })
public class AdminView extends JFrame {
	
	List <String> savedWords = new ArrayList<String>();
	List <String[]> defaultList = new ArrayList<String[]>();
	List <String[]> list = new ArrayList<String[]>();
	//Holds a line from the csv file
	protected String errorLine;
	//Holds the line from the csv file (each part of the array is a cell from csv)
	protected String [] errorWords;
	//ArrayList that holds all the UCodes from the csv file
	protected List<String> keyWords = new ArrayList<String>();
	//Brings up the frame for an admin user
	private AdminView myView;
	//Used as a current index marker for the function createDataTable
	private int dataIndex;
	//Headers for the JTable
	private final String[] columnHeaders = {"Folder", "Keyword", "Log Error Description",
									"Suggested Solution"};
	//Solution field when the user wants to add an entry
	private JTextField solutionText;
	//Error field when the user wants to add an entry
	private JTextField errorText;
	//UCode field when the user wants to add an entry
	private JTextField uText;
	//Displays the data within LogErrors_Suggestions.csv, updates with user action
	private JTable table;
	//Field for when the user wants to modify an entry
	private JTextField modifyText;
	
	protected JPanel pnlGroups;
	protected JList<String> groupList;
	protected DefaultListModel<String> model;
	
	Object[] options = {"Yes", "Cancel"};
	private DefaultTableModel tableModel;
	DataController dc;
	
	public AdminView(UserView view) throws ClassNotFoundException, SQLException {
			
		try {
		     ClassLoader cl = this.getClass().getClassLoader();
		     ImageIcon programIcon = new ImageIcon(cl.getResource("res/logo.png"));
		     setIconImage(programIcon.getImage());
		  } catch (Exception e) {
		     System.out.println("Could not load program icon.");
		  }
			
		createDataTable();
		dc = new DataController(this);
		dc.setList(list);
		dc.setDefaultList(defaultList);
		dc.transferData("CHANGE");
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Admin Features");
		setBounds(200, 200, 1000, 300);
		setMinimumSize(new Dimension(750, 300));
		setLocationRelativeTo(null);
		
		
		JPanel pnlMain = new JPanel();
		pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
		add(pnlMain);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		pnlMain.add(tabbedPane);
		
		JPanel pnlTabOne = new JPanel();
		pnlTabOne.setLayout(new BoxLayout(pnlTabOne, BoxLayout.Y_AXIS));
		
		JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pnlTabOne.add(scrollPane);
		
		initTable();
		
		scrollPane.setViewportView(table);
		
		pnlTabOne.add(Box.createRigidArea(new Dimension(0, 5)));
		
		JPanel pnlTabOneButtons = new JPanel();
		pnlTabOne.add(pnlTabOneButtons);
		pnlTabOneButtons.setLayout(new FlowLayout());
		
		JButton btnAddEntry = new JButton("Add Entry");
		btnAddEntry.addActionListener(e -> {
			AddDialog add = new AddDialog(dc);
			add.setVisible(true);
		});
		pnlTabOneButtons.add(btnAddEntry);
		
		JButton btnModifyEntry = new JButton("Modify Entry");
		btnModifyEntry.setHorizontalAlignment(SwingConstants.RIGHT);
		btnModifyEntry.addActionListener(e -> {
			if(table.getSelectedRow() != -1){
				int rowSelect = table.getSelectedRow();
				ModifyDialog modify = new ModifyDialog((String)table.getValueAt(rowSelect, 0),
													 (String)table.getValueAt(rowSelect, 1),
													 (String)table.getValueAt(rowSelect, 2),
													 (String)table.getValueAt(rowSelect, 3),
													 dc, rowSelect);
				modify.setVisible(true);
			}
			else JOptionPane.showMessageDialog(null, "Please select an entry");	
		});
		pnlTabOneButtons.add(btnModifyEntry);
		
		JButton btnDeleteEntry = new JButton("Delete Entry");
		btnDeleteEntry.addActionListener(e -> {
			int viewIndex = table.getSelectedRow();
			if(viewIndex != -1) {
				//Ensures user wants to delete selected entry
				int confirmation = JOptionPane.showOptionDialog(this,
				    "This will delete the entire entry. Are you sure you want to continue?",
				    "",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
				    null, options, options[1]);
				//If yes, then we continue delete process
				if (confirmation == JOptionPane.YES_OPTION){
					int modelIndex = table.convertRowIndexToModel(viewIndex); 
					try {
						dc.deleteData(viewIndex);
					} catch (Exception e1) {
					e1.printStackTrace();
					}
				}
			} 
		});
		pnlTabOneButtons.add(btnDeleteEntry);
		
		JButton btnSaveToDefault = new JButton("Save to Default");
		btnSaveToDefault.addActionListener(e -> {
			try {
				dc.saveDefault();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		btnSaveToDefault.setAlignmentX(Component.RIGHT_ALIGNMENT);
		pnlTabOneButtons.add(btnSaveToDefault);
		
		JButton btnRevertToDefault = new JButton("Revert to Default");
		btnRevertToDefault.setAlignmentX(Component.RIGHT_ALIGNMENT);
		pnlTabOneButtons.add(btnRevertToDefault);
		btnRevertToDefault.addActionListener(e -> {
			dc.getList().clear();
			for(int i = 0; i < dc.getDefaultList().size(); i++)
			{
				dc.getList().add(dc.getDefaultList().get(i));
			}
			dc.transferData("DEFAULT");
			resetData();
		});
		
		JPanel pnlTabTwo = new JPanel();
		pnlTabTwo.setLayout(new BoxLayout(pnlTabTwo, BoxLayout.Y_AXIS));
		pnlTabTwo.add(Box.createVerticalGlue());
		
		pnlGroups = createGroupDisplay (view);
		pnlGroups.setBorder(new EmptyBorder(10,5,0,5));
		pnlTabTwo.add(pnlGroups);
		
		JPanel pnlTabTwoButtons = new JPanel();
		pnlTabTwoButtons.setLayout(new FlowLayout());
		pnlTabTwo.add(pnlTabTwoButtons);
		
		JButton btnCreateGroup = new JButton("Create Group");
		btnCreateGroup.setPreferredSize(new Dimension(125, 20));
		btnCreateGroup.setAlignmentX(CENTER_ALIGNMENT);
		btnCreateGroup.addActionListener(e -> {
			CreateGroup groupView = new CreateGroup(this, view);
		});
		pnlTabTwoButtons.add(btnCreateGroup);
		
		JButton btnDeleteGroup = new JButton("Delete Group");
		btnDeleteGroup.setPreferredSize(new Dimension(125, 20));
		btnDeleteGroup.setAlignmentX(CENTER_ALIGNMENT);
		btnDeleteGroup.addActionListener(e -> {
			try {
				removeGroup(view);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		pnlTabTwoButtons.add(btnDeleteGroup);
		pnlTabTwo.add(Box.createVerticalGlue());
		
		tabbedPane.add("Edit Entries", pnlTabOne);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		tabbedPane.add("Create Groups", pnlTabTwo);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
	}
	
	/**
	 * This function fills myData with arrays. Each array represents a line from
	 * the database, the array itself being the return value
	 * from calling split function on the line.
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IOException
	 */
	void createDataTable() throws SQLException, ClassNotFoundException 
	{		
		String driver = "net.sourceforge.jtds.jdbc.Driver";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection("jdbc:jtds:sqlserver://vwaswp02:1433/coeus", "coeus", "C0eus");

		Statement stmt = conn.createStatement();
		String query2 = "select Keyword, Log_Error_Description, "+
						"Suggested_Solution, Folder from logerrors";
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
		}
		stmt.close();
	}
	
	JPanel createGroupDisplay (UserView view){
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
	    panel.add(Box.createHorizontalGlue());

	    model = new DefaultListModel<String>();
	    updateGroups(view);
	    groupList = new JList<String>(model);
	    JScrollPane scrollPane = new JScrollPane(groupList);
	    panel.add(scrollPane);
	    return panel;
	}
	
	protected void updateGroups(UserView view){
		model.clear();
		for (Map.Entry<String, String> entry : view.GroupInfo.entrySet()){
			String keyWords = entry.getValue();
	    	model.addElement(keyWords);
		}
	}
	
	void removeGroup(UserView view) throws ClassNotFoundException, SQLException{
		String groupToRemove = groupList.getSelectedValue();
		if (groupToRemove == null){
			JOptionPane.showMessageDialog(this, "No group selected");
			return;
		}
		String driver = "net.sourceforge.jtds.jdbc.Driver";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection("jdbc:jtds:sqlserver://vwaswp02:1433/coeus", "coeus", "C0eus");

		Statement stmt = conn.createStatement();
		stmt.executeUpdate("delete from Groups where GroupKeywords = \'" + groupToRemove + "\'");
		view.loadGroupInfo(stmt);
		stmt.close();

		System.out.println(groupToRemove);
		StringBuilder query = new StringBuilder();
		
		updateGroups(view);
		view.createGroupDisplay();
	}
	
	void resetData()
	{
		DefaultTableModel model = new DefaultTableModel(dc.getData(), columnHeaders); 
		table.setModel(model);
		resizeColumnWidth(table);
		model.fireTableDataChanged();
	}
	
	
	/**
	 * Initializes the table of errors, suggested solutions, folders, etc
	 * that can be modified by the administrator
	 */
	private void initTable(){
		tableModel = new DefaultTableModel(dc.getData(), columnHeaders) {
		    @Override
		    public boolean isCellEditable(int row, int column) {
		       //all cells false
		       return false;
		    }     
		};
		table = new JTable(tableModel){
			//Renders each columnn to fit the data
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component component = super.prepareRenderer(renderer, row, column);
				int rendererWidth = component.getPreferredSize().width;
	            TableColumn tableColumn = getColumnModel().getColumn(column);
	            tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
	            return component;
			}

		};
		resizeColumnWidth(table);	
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}
	
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
