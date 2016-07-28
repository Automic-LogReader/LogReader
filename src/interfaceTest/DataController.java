package interfaceTest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;



public class DataController {

	private List <String[]> defaultList = new ArrayList<String[]>();
	private List <String[]> list = new ArrayList<String[]>();
	private List <String[]> tempList = new ArrayList<String[]>();
	private List <String> queries = new ArrayList<String>();
	private Object [][] defaultData;
	private Object [][] data;
	private boolean keywordChanged;
	private boolean errorMessageChanged;
	private boolean suggestedSolutionChanged;
	private boolean folderChanged;
	//Holds a line from the csv file
	protected String errorLine;
	//Holds the line from the csv file (each part of the array is a cell from csv)
	protected String [] errorWords;
	private AdminView admin;
	
	DataController(AdminView admin)
	{
		this.admin = admin;
		keywordChanged = false;
		errorMessageChanged = false;
		suggestedSolutionChanged = false;
		folderChanged = false;
	}
	
	AdminView getAdmin()
	{
		return admin;
	}
	
	void setKeywordChanged(boolean change)
	{
		keywordChanged = change;
	}
	
	void setErrorMessageChanged(boolean change)
	{
		errorMessageChanged = change;
	}
	
	void setSuggestedSolutionChanged(boolean change)
	{
		suggestedSolutionChanged = change;
	}
	
	void setFolderChanged(boolean change)
	{
		folderChanged = true;
	}
	
	void setList(List <String[]> list)
	{
		this.list = list;
	}
	
	void setDefaultList(List <String[]> defaultList)
	{
		this.defaultList = defaultList;
	}
	
	List<String[]> getList(){
		return list;
	}
	
	List<String[]> getDefaultList()
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
	
	
	//****************************
	//Note that we will HAVE to go about doing modify a different way
	
	void modifyData(String folder, String keyWord, String message, String solution, String choice, int row) throws IOException
	{
		String [] tempArray = new String[4];
		tempArray[0] = folder;
		tempArray[1] = keyWord;
		tempArray[2] = message;
		tempArray[3] = solution;
		if(choice.equals("MODIFY"))
		{
			if(folderChanged)
				queries.add("update logerrors set Folder = \'" + 
					addSingleQuote(folder) + "\' where Keyword = \'" + addSingleQuote(list.get(row)[1]) + "\'");
			if(errorMessageChanged)
				queries.add("update logerrors set Log_Error_Description = \'" +
					 addSingleQuote(message) + "\' where Keyword = \'" + addSingleQuote(list.get(row)[1]) + "\'");
			if(suggestedSolutionChanged)
				queries.add("update logerrors set Suggested_Solution = \'" +
					 addSingleQuote(solution) + "\' where Keyword = \'" + addSingleQuote(list.get(row)[1]) + "\'");
			if(keywordChanged)
				queries.add("update logerrors set Keyword = \'" +
					addSingleQuote(keyWord) + "\' where Keyword = \'" + addSingleQuote(list.get(row)[1]) + "\'");
			admin.savedWords.remove(list.get(row)[1]);
			admin.savedWords.add(keyWord);
			list.set(row, tempArray);

		}
		else
		{
			queries.add("insert into logerrors values (\'" + addSingleQuote(keyWord) + "\',\'"
					+ addSingleQuote(message) + "\',\'" + addSingleQuote(solution) + "\',\'" +
					addSingleQuote(folder) + "\')");
			list.add(tempArray);
			admin.savedWords.add(keyWord);
		}
		
		transferData("CHANGE");
		folderChanged = false;
		keywordChanged = false;
		errorMessageChanged = false;
		suggestedSolutionChanged = false;
		
		admin.resetData();

	}
	
	
	String addSingleQuote(String oldWord)
	{
		StringBuilder newWord = new StringBuilder();
		for(int i = 0; i < oldWord.length(); i++)
		{
			if(oldWord.charAt(i) == '\'')
				newWord.append("\'\'");
			else
				newWord.append(oldWord.charAt(i));
		}
		return newWord.toString();
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
		queries.add("delete from logerrors where Keyword = \'" + addSingleQuote(list.get(row)[1]) + "\'");
		list.remove(row);
		transferData("CHANGE");
		admin.resetData();
		admin.savedWords.remove(list.get(row)[1]);
	}

	void transferData(String choice)
	{
		if(choice.equals("DEFAULT"))
		{
			tempList = defaultList;
			queries.clear();
			admin.savedWords = admin.keyWords;
		}
		else {
			tempList = list;
		}
		Object[][] myData = new Object[tempList.size()][];
		for(int i = 0; i < tempList.size(); i++)
		{
			myData[i] = tempList.get(i);
		}
		data = myData;
		
	}
	
	Object[][] getData()
	{
		return data;
	}
	
	void saveDefault() throws IOException, ClassNotFoundException, SQLException
	{
		defaultList.clear();
		admin.keyWords.clear();
		for(int x = 0; x < admin.savedWords.size(); x++)
		{
			admin.keyWords.add(admin.savedWords.get(x));
		}

		for(int i = 0; i < list.size(); i++)
		{
			defaultList.add(list.get(i));
		}
		
		for (int j = 0; j < queries.size(); j++)
		{
			System.out.println(queries.get(j));
		}
		
		String driver = "net.sourceforge.jtds.jdbc.Driver";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection("jdbc:jtds:sqlserver://vwaswp02:1433/coeus", "coeus", "C0eus");

		Statement stmt = conn.createStatement();
		for(int j = 0; j < queries.size(); j++)
		{
			int x = stmt.executeUpdate(queries.get(j));
		}
		
		stmt.close();
		
		queries.clear();
	}
	
}

	

