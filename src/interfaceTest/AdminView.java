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

import javax.swing.JFrame;
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

import javax.swing.JScrollPane;
import java.awt.GridBagConstraints;
import javax.swing.JTable;
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
	
	
	/**
	 * @param data - An array of object arrays that contains data from the error csv file. 
	 * 				 The data in here is filled into the JTable.
	 */
	public AdminView(Object[][] data) {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(200, 200, 1000, 300);
		setLocationRelativeTo(null);
		
		JComponent contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridheight = 4;
		gbc_scrollPane.gridwidth = 28;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		contentPane.add(scrollPane, gbc_scrollPane);
		
		DefaultTableModel tableModel = new DefaultTableModel(data, columnHeaders) {

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
		table.addMouseListener(new MouseAdapter(){
			@Override
		    public void mouseClicked(MouseEvent evnt) {
		        if (evnt.getClickCount() == 1) 
		            modifyText.setText((String)table.getValueAt(table.getSelectedRow(), table.getSelectedColumn()));
		    }
		});
		
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scrollPane.setViewportView(table);
		
		
		JButton deleteButton = new JButton("Delete Entry");
		GridBagConstraints gbc_deleteButton = new GridBagConstraints();
		gbc_deleteButton.insets = new Insets(0, 0, 5, 5);
		gbc_deleteButton.gridx = 1;
		gbc_deleteButton.gridy = 4;
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
					} catch (Exception e1) {
					e1.printStackTrace();
					}
				}
			}
			    
		});
		contentPane.add(deleteButton, gbc_deleteButton);
		
		
		JLabel lblNewLabel_1 = new JLabel("UCode:", SwingConstants.RIGHT);
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 16;
		gbc_lblNewLabel_1.gridy = 4;
		contentPane.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		uText = new JTextField();
		GridBagConstraints gbc_uText = new GridBagConstraints();
		gbc_uText.gridwidth = 11;
		gbc_uText.insets = new Insets(0, 0, 5, 0);
		gbc_uText.fill = GridBagConstraints.HORIZONTAL;
		gbc_uText.gridx = 17;
		gbc_uText.gridy = 4;
		contentPane.add(uText, gbc_uText);
		uText.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Error Message:", SwingConstants.RIGHT);
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 16;
		gbc_lblNewLabel_2.gridy = 5;
		contentPane.add(lblNewLabel_2, gbc_lblNewLabel_2);
		
		errorText = new JTextField();
		GridBagConstraints gbc_errorText = new GridBagConstraints();
		gbc_errorText.gridwidth = 11;
		gbc_errorText.insets = new Insets(0, 0, 5, 0);
		gbc_errorText.fill = GridBagConstraints.HORIZONTAL;
		gbc_errorText.gridx = 17;
		gbc_errorText.gridy = 5;
		contentPane.add(errorText, gbc_errorText);
		errorText.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Change to:", SwingConstants.RIGHT);
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 6;
		contentPane.add(lblNewLabel, gbc_lblNewLabel);
		
		modifyText = new JTextField();
		GridBagConstraints gbc_modifyText = new GridBagConstraints();
		gbc_modifyText.gridwidth = 11;
		gbc_modifyText.insets = new Insets(0, 0, 5, 5);
		gbc_modifyText.fill = GridBagConstraints.HORIZONTAL;
		gbc_modifyText.gridx = 2;
		gbc_modifyText.gridy = 6;
		contentPane.add(modifyText, gbc_modifyText);
		modifyText.setColumns(10);
		
		JLabel lblNewLabel_3 = new JLabel("Suggested Solution:");
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.gridx = 16;
		gbc_lblNewLabel_3.gridy = 6;
		contentPane.add(lblNewLabel_3, gbc_lblNewLabel_3);
		
		solutionText = new JTextField();
		GridBagConstraints gbc_solutionText = new GridBagConstraints();
		gbc_solutionText.gridwidth = 11;
		gbc_solutionText.insets = new Insets(0, 0, 5, 0);
		gbc_solutionText.fill = GridBagConstraints.HORIZONTAL;
		gbc_solutionText.gridx = 17;
		gbc_solutionText.gridy = 6;
		contentPane.add(solutionText, gbc_solutionText);
		solutionText.setColumns(10);
		
		JButton modifyButton = new JButton("Modify Entry");
		GridBagConstraints gbc_modifyButton = new GridBagConstraints();
		gbc_modifyButton.insets = new Insets(0, 0, 0, 5);
		gbc_modifyButton.gridx = 1;
		gbc_modifyButton.gridy = 7;
		modifyButton.addActionListener(e -> {
			if(table.getSelectedRow() != -1)
			{
				if (modifyText.getText().equals(""))
					JOptionPane.showMessageDialog(null, "No entry given");
				else
					table.setValueAt(modifyText.getText(), table.getSelectedRow(), table.getSelectedColumn());
			}
				
				try {
					modifyData(modifyText.getText(), table.getSelectedRow(), table.getSelectedColumn());
				} catch (Exception e1) {
					
					e1.printStackTrace();
				}
		});
		contentPane.add(modifyButton, gbc_modifyButton);
		
		JButton addButton = new JButton("Add Entry");
		GridBagConstraints gbc_addButton = new GridBagConstraints();
		gbc_addButton.insets = new Insets(0, 0, 0, 5);
		gbc_addButton.gridx = 16;
		gbc_addButton.gridy = 7;
		addButton.addActionListener(e -> {
			if (uText.getText().equals("") || errorText.getText().equals("") || 
					solutionText.getText().equals(""))
				JOptionPane.showMessageDialog(null, "Please fill out all entries");
			else
			{
				DefaultTableModel model = (DefaultTableModel)(table.getModel());
				model.addRow(new Object[] {uText.getText(), errorText.getText(), solutionText.getText()});
				try {
					addData(uText.getText(), errorText.getText(), solutionText.getText());
				} catch (Exception e1) {
					
					e1.printStackTrace();
				}
			}	
		});
		contentPane.add(addButton, gbc_addButton);
		
	
	}
	
	/**
	 * When the user chooses to modify the data, this function changes the output on the 
	 * screen and within the LogError_Suggestions.csv file to the input given by the user. 
	 * @param newMessage - the message input from the user
	 * @param row - the row in which the data will be modified
	 * @param col - the column in which the data will be modified
	 * @throws IOException 
	 */
	
	void modifyData(String newEntry, int row, int col) throws IOException
	{
		
		File oldFile = new File ("src/interfaceTest/resources/LogErrors_Suggestions.csv");
		File temp = new File("src/interfaceTest/resources/temp.csv");
		int i = 0;
		//InputStream errorInput = getClass().getResourceAsStream(MainController.errorFile);
		//BufferedReader errorbr = new BufferedReader(new InputStreamReader(errorInput));
		
		
		FileReader errorInput = new FileReader(oldFile);
		BufferedReader errorbr = new BufferedReader(errorInput);
		FileWriter fw = new FileWriter (temp, true);
		
		//Include headings
		String errorLine = errorbr.readLine();
		fw.write(errorLine + "\r\n");
		
		errorLine = errorbr.readLine();
		
		while (errorLine != null)
		{
			//We've hit the row that we want to modify
			if(i == row)
			{	
				String newLine = "";
				//If the user's entry includes a comma, make sure CSV
				//file doesn't break it up by adding \"
				if(newEntry.contains(","))
					newEntry = "\"" + newEntry + "\"";
				String[] theLine = errorLine.split(",(?=([^\"]|\"[^\"]*\")*$)");
				//Replace corresponding index depending on column we want to modify
				theLine[col] = newEntry;
				
				//Add commas so each entry goes in its individual cell
				for(int j = 0; j < theLine.length; j++)
					newLine = newLine + theLine[j] + ",";
				fw.write(newLine + "\r\n");
			}
			else
				fw.write(errorLine + "\r\n");
			errorLine = errorbr.readLine();
			i++;
		}
		errorInput.close();
		errorbr.close();
		fw.close();
		if(oldFile.delete())
		{
			temp.renameTo(oldFile);
			System.out.println("modify success");
		}
		else
			System.out.println("modify failed");

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
			System.out.println("delete failed");

	}

	/**
	 * When the user has filled out all the fields (ucode, message, and solution) and
	 * clicks on the add entry function, this function writes a new entry into 
	 * LogErrors_Suggestions.csv, with each field in its own cell. 
	 * @param ucode - A code that represents the error 
	 * @param message - The message that goes along with the error saying what went wrong
	 * @param solution - A suggested solution on how to fix the above error
	 * @throws IOException
	 */
	void addData(String ucode, String message, String solution) throws IOException
	{
		FileWriter fw = new FileWriter ("src/interfaceTest/resources/LogErrors_Suggestions.csv", true);
		//Commas are added between each entry so they are put in individual cells in the csv
		fw.append(ucode + ",");
		// \" is added so that if the message contains a comma, it isn't broken up into separate cells
		if(message.contains(","))
			message = "\"" + message + "\"";
		fw.append(message + ",");
		if(solution.contains(","))
			solution = "\"" + solution + "\"";
		fw.append(solution + "\r\n");
		fw.close();
	}




	
}
