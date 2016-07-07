
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
	//Number of rows in LogErrorSuggestions.csv
	private int dataLength;
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
	private List <String> list = new ArrayList<String>();
	Object [][] data;
	DefaultTableModel tableModel;
	
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
		setDataLength();
		data = createDataTable();
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
					DefaultTableModel model = (DefaultTableModel)(table.getModel());
					model.removeRow(modelIndex);
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
		
		JButton saveButton = new JButton("Save to Default");
		//This will save the data as is and go back to that
		panel_2.add(saveButton);
		
		
		Component rigidArea_2 = Box.createRigidArea(new Dimension(20, 20));
		panel_2.add(rigidArea_2);
		
		JButton defaultButton = new JButton("Revert to Default");
		panel_2.add(defaultButton);
		
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
	
	
	//NEED to have table reflect the changes after modifying and adding, otherwise things get wonky
	void modifyData(String keyWord, String message, String solution, String choice, int row) throws IOException
	{
		DefaultTableModel model = (DefaultTableModel)(table.getModel());
		if(choice.equals("MODIFY"))
		{
			table.setValueAt(keyWord, row, 0);
			table.setValueAt(message, row, 1);
			table.setValueAt(solution, row, 2);
		}
		else if(choice.equals("ADD"))
			model.addRow(new Object[] {keyWord, message, solution});
		
		File oldFile = new File ("src/interfaceTest/resources/LogErrors_Suggestions.csv");
		File temp = new File("src/interfaceTest/resources/temp.csv");
		int i = 0;
		list.clear();
		//InputStream errorInput = getClass().getResourceAsStream(MainController.errorFile);
		//BufferedReader errorbr = new BufferedReader(new InputStreamReader(errorInput));
		
		
		FileReader errorInput = new FileReader(oldFile);
		BufferedReader errorbr = new BufferedReader(errorInput);
		FileWriter fw = new FileWriter (temp, true);
		
		//Include headings
		String errorLine = errorbr.readLine();
		fw.write(errorLine + "\r\n");
		
		errorLine = errorbr.readLine();
		
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
		newLine += (solution + "\r\n");
		list.add(newLine);
		
		while (errorLine != null)
		{
			if(choice.equals("MODIFY"))
			{
				//If we're straight modifying rather than adding, 
				//then we skip the row 
				if(i != row)
					list.add(errorLine + "\r\n");
			}
			else if(choice.equals("ADD"))
				list.add(errorLine + "\r\n");
			errorLine = errorbr.readLine();
			i++;
			
		}
		Collections.sort(list);
		for(int j = 0; j < list.size(); j++)
		{
			fw.write(list.get(j));
		}
		
		errorInput.close();
		errorbr.close();
		fw.close();
		if(oldFile.delete())
		{
			temp.renameTo(oldFile);
			System.out.println("modify/add success");
		}
		else
		{	
			temp.delete();
			System.out.println("modify/add failed");
		}

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
		//InputStream errorInput = getClass().getResourceAsStream(MainController.errorFile);
		//BufferedReader errorbr = new BufferedReader(new InputStreamReader(errorInput));
		
		File oldFile = new File ("src/interfaceTest/resources/LogErrors_Suggestions.csv");
		File temp = new File("src/interfaceTest/resources/temp.csv");
		FileReader errorInput = new FileReader(oldFile);
		BufferedReader errorbr = new BufferedReader(errorInput);
		FileWriter fw = new FileWriter (temp, true);
		
		String errorLine = errorbr.readLine();
		fw.write(errorLine + "\r\n");
		errorLine = errorbr.readLine();
		
		while(errorLine != null)
		{
			//Write to the new file as long as it isn't the row to be deleted
			if (i != row)
			{
				fw.write(errorLine + "\r\n");
			}
			errorLine = errorbr.readLine();
			i++;
		}
		fw.close();
		errorbr.close();
		errorInput.close();
		if(oldFile.delete())
		{
			temp.renameTo(oldFile);
			System.out.println("delete success");
		}
		else
		{	
			temp.delete();
			System.out.println("delete failed");
		}

	}


	/**
	 * This function fills myData with arrays. Each array represents a line from
	 * LogErrors_Suggestions.csv, the array itself being the return value
	 * from calling split function on the line.
	 * @throws IOException
	 */
	Object[][] createDataTable() throws IOException
	{
		//InputStream errorInput = getClass().getResourceAsStream(MainController.errorFile);
		//BufferedReader errorbr = new BufferedReader(new InputStreamReader(errorInput));
		
		Object[][] myData = new Object[dataLength][];
		FileReader errorInput = new FileReader("src/interfaceTest/resources/LogErrors_Suggestions.csv");
		BufferedReader errorbr = new BufferedReader(errorInput);
		
		errorLine = errorbr.readLine();
		errorLine = errorbr.readLine();
		
		
		while(errorLine != null)
		{
			errorWords = errorLine.split(",(?=([^\"]|\"[^\"]*\")*$)");
			myData[dataIndex] = errorWords; 
			errorLine = errorbr.readLine();
			dataIndex++;
		}
		errorbr.close();
		return myData;
	}
	
	/**
	 * This function reads lines in the LogError_Suggestions.csv file until 
	 * hitting a line that is null, incrementing dataLength as it goes. At 
	 * the end, the value of dataLength will be the number of lines in the
	 * file (excluding the header line). 
	 * @throws IOException
	 */
	public void setDataLength() throws IOException
	{
		//InputStream errorInput = getClass().getResourceAsStream(MainController.errorFile);
		//BufferedReader errorbr = new BufferedReader(new InputStreamReader(errorInput));
		
		FileReader errorInput = new FileReader("src/interfaceTest/resources/LogErrors_Suggestions.csv");
		BufferedReader errorbr = new BufferedReader(errorInput);
		
		errorLine = errorbr.readLine();
		errorLine = errorbr.readLine();
		
		while(errorLine != null)
		{
			dataLength++;
			errorLine = errorbr.readLine();
		}
		errorbr.close();
	}

	
}
