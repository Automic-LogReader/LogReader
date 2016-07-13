package interfaceTest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class DataController {

	private List <String> defaultList = new ArrayList<String>();
	private List <String> list = new ArrayList<String>();
	private List <String> tempList = new ArrayList<String>();
	private Object [][] defaultData;
	private Object [][] data;
	//Holds a line from the csv file
	protected String errorLine;
	//Holds the line from the csv file (each part of the array is a cell from csv)
	protected String [] errorWords;
	private AdminView admin;
	
	DataController(AdminView admin)
	{
		this.admin = admin;
	}
	
	
	void setList(List <String> list)
	{
		this.list = list;
	}
	
	void setDefaultList(List <String> defaultList)
	{
		this.defaultList = defaultList;
	}
	
	List<String> getList(){
		return list;
	}
	
	List<String> getDefaultList()
	{
		return defaultList;
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
		admin.resetData();

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
		admin.resetData();
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
	
	Object[][] getData()
	{
		return data;
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

	

