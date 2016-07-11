
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

import javax.swing.JFrame;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

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
import java.awt.GridBagConstraints;
import javax.swing.JTable;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
	private String[] columnHeaders = {"U Code", "Log Error Description", "Suggested Solution"};
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
	private List <String> defaultList = new ArrayList<String>();
	private List <String> list = new ArrayList<String>();
	private List <String> tempList = new ArrayList<String>();
	private Object [][] defaultData;
	private Object [][] data;
	private DefaultTableModel tableModel;
	
	/**
	 * @param data - An array of object arrays that contains data from the error csv file. 
	 * 				 The data in here is filled into the JTable.
	 * @throws IOException 
	 */
	public AdminView() throws IOException {
		
		setBounds(200, 200, 1000, 300);
		setLocationRelativeTo(null);
		
		JComponent contentPane = new JPanel();
		setContentPane(contentPane);
		createDataTable();
		defaultData = data;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(200, 200, 1000, 300);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		getContentPane().add(scrollPane);
		
		tableModel = new DefaultTableModel(data, columnHeaders) {

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
		
		//Whenever user clicks on a cell, the cell's contents appear in modify textbox
		/*
		table.addMouseListener(new MouseAdapter(){
			@Override
		    public void mouseClicked(MouseEvent evnt) {
		        if (evnt.getClickCount() == 1) 
		            modifyText.setText((String)table.getValueAt(table.getSelectedRow(), table.getSelectedColumn()));
		    }
		});
		*/
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scrollPane.setViewportView(table);
		
		Component rigidArea_3 = Box.createRigidArea(new Dimension(20, 20));
		getContentPane().add(rigidArea_3);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));
		
		JButton addButton = new JButton("Add Entry");
		addButton.addActionListener(e -> {
			AddDialog add = new AddDialog(this);
			add.setVisible(true);
		});
		panel_1.add(addButton);
		
		Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
		panel_1.add(rigidArea);
		
		JButton modifyButton = new JButton("Modify Entry");
		modifyButton.setHorizontalAlignment(SwingConstants.RIGHT);
		modifyButton.addActionListener(e -> {
			if(table.getSelectedRow() != -1)
			{
				int rowSelect = table.getSelectedRow();
				ModifyDialog modify = new ModifyDialog((String)table.getValueAt(rowSelect, 0),
													 (String)table.getValueAt(rowSelect, 1),
													 (String)table.getValueAt(rowSelect, 2), 
													 this, rowSelect);
				modify.setVisible(true);
			}
			else JOptionPane.showMessageDialog(null, "Please select an entry");	
		});
		panel_1.add(modifyButton);
		
		Component rigidArea_1 = Box.createRigidArea(new Dimension(20, 20));
		panel_1.add(rigidArea_1);
		
		JButton deleteButton = new JButton("Delete Entry");
		deleteButton.addActionListener(e -> {
			
			int viewIndex = table.getSelectedRow();

			if(viewIndex != -1) 
			{
				//Ensures user wants to delete selected entry
				int confirmation = JOptionPane.showOptionDialog(this,
				    "This will delete the entire entry. Are you sure you want to continue?",
				    "",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
				    null, options, options[1]);
				//If yes, then we continue delete process
				if (confirmation == JOptionPane.YES_OPTION)
				{
					int modelIndex = table.convertRowIndexToModel(viewIndex); 
					//DefaultTableModel model = (DefaultTableModel)(table.getModel());
					//model.removeRow(modelIndex);
					try {
						deleteData(viewIndex);
						//makeTable();
					} catch (Exception e1) {
					e1.printStackTrace();
					}
				}
			}
			    
		});
		panel_1.add(deleteButton);
		
		Component rigidArea_4 = Box.createRigidArea(new Dimension(20, 20));
		panel.add(rigidArea_4);
		
		JPanel panel_2 = new JPanel();
		panel.add(panel_2);
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.X_AXIS));
		
		/////////////////////////////////////////
		JButton saveButton = new JButton("Save to Default");
		saveButton.addActionListener(e -> {
			try {
				saveDefault();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		
		panel_2.add(saveButton);
		
		
		Component rigidArea_2 = Box.createRigidArea(new Dimension(20, 20));
		panel_2.add(rigidArea_2);
		
		/////////////////////////////////////////////////////////
		JButton defaultButton = new JButton("Revert to Default");
		panel_2.add(defaultButton);
		defaultButton.addActionListener(e -> {
			list.clear();
			for(int i = 0; i < defaultList.size(); i++)
			{
				list.add(defaultList.get(i));
			}
			transferData("DEFAULT");
			resetData();
		});
		
		Component rigidArea_5 = Box.createRigidArea(new Dimension(20, 20));
		panel.add(rigidArea_5);

	}
	
	/**
	 * When the user chooses to modify the data, this function changes the output on the 
	 * screen and within the LogError_Suggestions.csv file to the input given by the user. 
	 * @param newMessage - the message input from the user
	 * @param row - the row in which the data will be modified
	 * @param col - the column in which the data will be modified
	 * @throws IOException 
	 */
	
	
	void modifyData(String keyWord, String message, String solution, String choice, int row) throws IOException
	{
		String newLine = "";
		//Commas are added between each entry so they are put in individual cells in the csv
		if(keyWord.contains(","))
			if(!keyWord.contains("\""))
				keyWord = "\"" + keyWord + "\"";
		newLine += (keyWord + ",");
		// \" is added so that if the message contains a comma, it isn't broken up into separate cells
		if(message.contains(","))
			if(!message.contains("\""))
				message = "\"" + message + "\"";
		newLine += (message + ",");
		if(solution.contains(","))
			if(!solution.contains("\""))
				solution = "\"" + solution + "\"";
		newLine += solution;
		if(choice.equals("MODIFY"))
			list.set(row, newLine);
		else
			list.add(newLine);
		Collections.sort(list);
		transferData("CHANGE");
		resetData();

	}
	
	/**
	 * When the user highlights a piece of data and then clicks the delete button, 
	 * this function will change the data within LogError_Suggestions.csv file, 
	 * reading all of the data into a new file, except the file line that
	 * corresponds to the row index that the user chose. 
	 * @param row - the row in which the data will be deleted
	 * @throws IOException 
	 */
	void deleteData(int row) throws IOException
	{
		int i = 0;
		list.remove(row);
		transferData("CHANGE");
		resetData();
	}


	/**
	 * This function fills myData with arrays. Each array represents a line from
	 * LogErrors_Suggestions.csv, the array itself being the return value
	 * from calling split function on the line.
	 * @throws IOException
	 */
	void createDataTable() throws IOException
	{
		dataIndex = 0;
		//InputStream errorInput = getClass().getResourceAsStream(MainController.errorFile);
		//BufferedReader errorbr = new BufferedReader(new InputStreamReader(errorInput));
		
		FileReader errorInput = new FileReader("src/interfaceTest/resources/LogErrors_Suggestions.csv");
		BufferedReader errorbr = new BufferedReader(errorInput);
		
		errorLine = errorbr.readLine();
		errorLine = errorbr.readLine();
		
		while(errorLine != null)
		{
			list.add(errorLine);
			defaultList.add(errorLine);
			errorLine = errorbr.readLine();
		}
		Collections.sort(list);
		Collections.sort(defaultList);
		errorbr.close();
		transferData("CHANGE");
	}
	
	void transferData(String choice)
	{
		if(choice.equals("DEFAULT"))
			tempList = defaultList;
		else
			tempList = list;
		Object[][] myData = new Object[tempList.size()][];
		for(int i = 0; i < tempList.size(); i++)
		{
			errorWords = tempList.get(i).split(",(?=([^\"]|\"[^\"]*\")*$)");
			myData[i] = errorWords;
		}
		data = myData;
	}
	
	
	void resetData()
	{
		DefaultTableModel model = new DefaultTableModel(data, columnHeaders); // for example
		table.setModel(model);
		model.fireTableDataChanged();
	}
	
	void saveDefault() throws IOException
	{
		defaultList.clear();
		File oldFile = new File ("src/interfaceTest/resources/LogErrors_Suggestions.csv");
		File temp = new File("src/interfaceTest/resources/temp.csv");
		
		FileWriter fw = new FileWriter (temp, true);
		fw.write("Keywords,Log Error Description,Suggested Solution\r\n");
		
		String newLine = "";
		for(int i = 0; i < list.size(); i++)
		{
			defaultList.add(list.get(i));
			newLine = list.get(i) + "\r\n";
			fw.write(newLine);
		}
		fw.close();
		if(oldFile.delete())
		{
			temp.renameTo(oldFile);
			System.out.println("success");
		}
		else
		{	
			temp.delete();
			System.out.println("failed");
		}
	}
	
}
