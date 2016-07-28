
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

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import java.awt.GridBagConstraints;
import javax.swing.JTable;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;

import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
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
	private String[] columnHeaders = {"Folder", "Keyword", "Log Error Description",
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
	Object[] options = {"Yes", "Cancel"};
	private DefaultTableModel tableModel;
	DataController dc;
	
	/**
	 * @param data - An array of object arrays that contains data from the error csv file. 
	 * 				 The data in here is filled into the JTable.
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	public AdminView() throws ClassNotFoundException, SQLException {
		
		
		try {
		     ClassLoader cl = this.getClass().getClassLoader();
		     ImageIcon programIcon = new ImageIcon(cl.getResource("res/logo.png"));
		     setIconImage(programIcon.getImage());
		  } catch (Exception e) {
		     System.out.println("Could not load program icon.");
		  }
		
		JComponent contentPane = new JPanel();
		setContentPane(contentPane);
			
		createDataTable();
		dc = new DataController(this);
		dc.setList(list);
		dc.setDefaultList(defaultList);
		dc.transferData("CHANGE");
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(200, 200, 1000, 300);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		JTabbedPane tabbedPane = new JTabbedPane();
		getContentPane().add(tabbedPane);
		
		JPanel tab1 = new JPanel();
		tab1.setLayout(new BoxLayout(tab1, BoxLayout.Y_AXIS));
		
		JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tab1.add(scrollPane);
		
		tableModel = new DefaultTableModel(dc.getData(), columnHeaders) {
		    @Override
		    public boolean isCellEditable(int row, int column) {
		       //all cells false
		       return false;
		    }     
		};
		table = new JTable(tableModel){
			//Renders each columnn to fit the data
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) 
			{
				Component component = super.prepareRenderer(renderer, row, column);
				int rendererWidth = component.getPreferredSize().width;
	           TableColumn tableColumn = getColumnModel().getColumn(column);
	           tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
	           return component;
			}

		};
		
		resizeColumnWidth(table);	
		
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scrollPane.setViewportView(table);
		
		tab1.add(Box.createRigidArea(new Dimension(0, 5)));
		
		JPanel buttonPanel = new JPanel();
		tab1.add(buttonPanel);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		buttonPanel.add(Box.createVerticalGlue());
		
		JPanel topButtonPanel = new JPanel();
		buttonPanel.add(topButtonPanel);
		topButtonPanel.setLayout(new BoxLayout(topButtonPanel, BoxLayout.X_AXIS));
		
		JButton addButton = new JButton("Add Entry");
		addButton.addActionListener(e -> {
			AddDialog add = new AddDialog(dc);
			add.setVisible(true);
		});
		
		topButtonPanel.add(addButton);
		topButtonPanel.add(Box.createRigidArea(new Dimension(5, 10)));
		
		JButton modifyButton = new JButton("Modify Entry");
		modifyButton.setHorizontalAlignment(SwingConstants.RIGHT);
		modifyButton.addActionListener(e -> {
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
		topButtonPanel.add(modifyButton);
		topButtonPanel.add(Box.createRigidArea(new Dimension(5, 10)));
		
		JButton deleteButton = new JButton("Delete Entry");
		deleteButton.addActionListener(e -> {
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
		topButtonPanel.add(deleteButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(5, 10)));
		
		JPanel bottomButtonPanel = new JPanel();
		buttonPanel.add(bottomButtonPanel);
		bottomButtonPanel.setLayout(new BoxLayout(bottomButtonPanel, BoxLayout.X_AXIS));
		
		JButton saveButton = new JButton("Save to Default");
		saveButton.addActionListener(e -> {
			try {
				dc.saveDefault();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		bottomButtonPanel.add(saveButton);
		bottomButtonPanel.add(Box.createRigidArea(new Dimension(5, 10)));
		
		JButton defaultButton = new JButton("Revert to Default");
		bottomButtonPanel.add(defaultButton);
		defaultButton.addActionListener(e -> {
			dc.getList().clear();
			for(int i = 0; i < dc.getDefaultList().size(); i++)
			{
				dc.getList().add(dc.getDefaultList().get(i));
			}
			dc.transferData("DEFAULT");
			resetData();
		});
		buttonPanel.add(Box.createRigidArea(new Dimension(5, 10)));
		buttonPanel.add(Box.createVerticalGlue());
		
		JPanel tab2 = new JPanel();
		
		
		tabbedPane.add("Edit Entries", tab1);
	}
	
	/**
	 * This function fills myData with arrays. Each array represents a line from
	 * LogErrors_Suggestions.csv, the array itself being the return value
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

			
		//Collections.sort(list);
		//Collections.sort(defaultList);
		stmt.close();
	}
	
	void resetData()
	{
		DefaultTableModel model = new DefaultTableModel(dc.getData(), columnHeaders); 
		table.setModel(model);
		resizeColumnWidth(table);
		model.fireTableDataChanged();
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
