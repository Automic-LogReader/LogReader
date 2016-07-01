/**
 * file: Admin.java
 * This class extends the user class and has functions that determine
 * the length of the LogErrors_Suggestions.csv file (how many rows it
 * contains, not including the header) and fills an Object[][] that 
 * prepares the data for the JTable in AdminView.java.
 */

package interfaceTest;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Admin extends User {
	
	//Holds a line from the csv file
	protected String errorLine;
	//Holds the line from the csv file (each part of the array is a cell from csv)
	protected String [] errorWords;
	//ArrayList that holds all the UCodes from the csv file
	protected List<String> uCodes = new ArrayList<String>();
	//Brings up the frame for an admin user
	private AdminView myView;
	//an array of object arrays to hold all data from LogError_Suggestions.csv
	private Object[][] myData;
	//Number of rows in LogErrorSuggestions.csv
	private int dataLength;
	//Used as a current index marker for the function createDataTable
	private int dataIndex;
	
	public Admin(MainMenu menu) throws IOException
	{
		super(menu);
		dataIndex = 0;
		setDataLength();
		myData = new Object[dataLength][];
		createDataTable();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {				
					myView = new AdminView();
					myView.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	

	/**
	 * This function fills myData with arrays. Each array represents a line from
	 * LogErrors_Suggestions.csv, the array itself being the return value
	 * from calling split function on the line.
	 * @throws IOException
	 */
	void createDataTable() throws IOException
	{
		//InputStream errorInput = getClass().getResourceAsStream(MainController.errorFile);
		//BufferedReader errorbr = new BufferedReader(new InputStreamReader(errorInput));
		
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
