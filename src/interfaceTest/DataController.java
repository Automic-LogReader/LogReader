/**
 * @file DataController.java
 * @authors Leah Talkov, Jerry Tsui
 * @date 8/3/2016
 * This class contains functions that modify the table in AdminView
 * depending on whether the user adds, modifies, or deletes entries. The
 * class also has variables that keep track of default values of the database.
 * When the user wants to save their changes to the database, this class also
 * has functions that query to the database to reflect those changes. 
 */

package interfaceTest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DataController {

	//A list that contains the true current entries in the database
	private List <String[]> defaultList = new ArrayList<String[]>();
	//A list that starts out with the same contents as defaultList, and
	//then changes its contents to reflect the user changes
	private List <String[]> list = new ArrayList<String[]>();
	//A dataStructure that serves as a helper when transferring
	//contents between list and defaultList
	private List <String[]> tempList = new ArrayList<String[]>();
	//Queries to be made to the database
	private List <String> queries = new ArrayList<String>();
	//private Object [][] defaultData;
	//Entries to be into the JTable in adminview
	private Object [][] data;
	//Set in modify dialog, true if keyword was changed
	private boolean keywordChanged;
	//Set in modify dialog, true if error message was changed
	private boolean errorMessageChanged;
	//Set in modify dialog, true if solution message was changed
	private boolean suggestedSolutionChanged;
	//Set in modify dialog, true if folder name was changed
	private boolean folderChanged;
	//Admin object that contains the table that DataController is changing
	private AdminView admin;

	DataController(AdminView admin){
		this.admin = admin;
		keywordChanged = false;
		errorMessageChanged = false;
		suggestedSolutionChanged = false;
		folderChanged = false;
	}

	/**
	 * This function is called in AddDialog and ModifyDialog to
	 * access variables within Admin, particularly to see
	 * what keywords are already used so that no duplicates are allowed
	 * @return Returns the admin object that was given in the main
	 */
	AdminView getAdmin(){
		return admin;
	}

	/**
	 * Called in modify Dialog to see if the content was changed. Boolean 
	 * result is used in modifyData function to see if a query is needed for
	 * the change of the keyWord
	 * @param change True if keyWord was changed, false otherwise
	 */
	void setKeywordChanged(boolean change){
		keywordChanged = change;
	}

	/**
	 * Called in modify Dialog to see if the content was changed. Boolean 
	 * result is used in modifyData function to see if a query is needed for
	 * the change of the error message
	 * @param change True if error message was changed, false otherwise
	 */
	void setErrorMessageChanged(boolean change){
		errorMessageChanged = change;
	}

	/**
	 * Called in modify Dialog to see if the content was changed. Boolean 
	 * result is used in modifyData function to see if a query is needed for
	 * the change of the solution message
	 * @param change True if solution message was changed, false otherwise
	 */
	void setSuggestedSolutionChanged(boolean change){
		suggestedSolutionChanged = change;
	}

	/**
	 * Called in modify Dialog to see if the content was changed. Boolean 
	 * result is used in modifyData function to see if a query is needed for
	 * the change of the folder name
	 * @param change True if folder name was changed, false otherwise
	 */
	void setFolderChanged(boolean change){
		folderChanged = true;
	}

	/**
	 * This function is called in AdminView to intialize the contents of the list
	 * @param list A list of the entries of the table from AdminView
	 */
	void setList(List <String[]> list){
		this.list = list;
	}

	/**
	 * This function is called in AdminView to intialize the contents of the defaultList
	 * @param defaultList A list of the entries of the table from AdminView
	 */
	void setDefaultList(List <String[]> defaultList){
		this.defaultList = defaultList;
	}

	/**
	 * This function is called in AdminView to either retrieve the contents to change the 
	 * JTable, or to clear the list if the user wants to revert their changes
	 * @return Returns the list which reflects the user changes
	 */
	List<String[]> getList(){
		return list;
	}

	/**
	 * This function is called in AdminView to get the old content when the user
	 * wants to revert their changes back to the default 
	 * @return Returns the list which reflects the current contents of the DB
	 */
	List<String[]> getDefaultList(){
		return defaultList;
	}

	/**
	 * This function modfies the data by depending on the value of "choice". It is
	 * called in both ModifyDialog and AddDialog. If the value
	 * is "MODIFY" then the function modifies an entry by modifying list, and by adding a query
	 * to the queries data structure. If the value is "ADD", then the function adds an entry to
	 * list and adds a query that makes an entry reflecting the values given by the user. 
	 * @param folder The folder name given by the user
	 * @param keyWord The keyWord given by the user - cannot be an existing keyword
	 * @param message The error message given by the user
	 * @param solution The solution message given by the user
	 * @param choice Determines whether the user is modifying or adding entries
	 * @param row If the choice is MODIFY, the row index that is being modified
	 * @throws IOException thrown if connection failed
	 */
	protected void modifyData(String folder, String keyWord, String message, String solution, String choice, int row) throws IOException {
		String [] tempArray = new String[4];
		tempArray[0] = folder;
		tempArray[1] = keyWord;
		tempArray[2] = message;
		tempArray[3] = solution;
		//If we are modifying, we check the booleans to see which parts of the entry have been 
		//modifed to see which queries we need to add
		if(choice.equals("MODIFY")) {
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
		//Otherwise we are adding an entry and simply add to the list and queries
		else {
			queries.add("insert into logerrors values (\'" + addSingleQuote(keyWord) + "\',\'"
					+ addSingleQuote(message) + "\',\'" + addSingleQuote(solution) + "\',\'" +
					addSingleQuote(folder) + "\')");
			list.add(tempArray);
			admin.savedWords.add(keyWord);
		}
		//Reset the booleans and transfer data indicating that a change in the data has occurred
		transferData("CHANGE");
		folderChanged = false;
		keywordChanged = false;
		errorMessageChanged = false;
		suggestedSolutionChanged = false;
		admin.resetData();
	}


	/**
	 * Adds single quotes to the parameter old word, because SQL syntax uses
	 * single quotes to encapsulate; i.e. if we had an error message that said
	 * I want to make 'examples' 
	 * There would be an error with the single quotes. This function adds single
	 * quotes to change the message to: I want to make ''examples'' to that this 
	 * will bypass the error and keep the message intact. 
	 * @param oldWord A string that will be modified 
	 * @return Returns old word but with additional single quotes if need be
	 */
	private String addSingleQuote(String oldWord) {
		StringBuilder newWord = new StringBuilder();
		for(int i = 0; i < oldWord.length(); i++) {
			if(oldWord.charAt(i) == '\'')
				newWord.append("\'\'");
			else
				newWord.append(oldWord.charAt(i));
		}
		return newWord.toString();
	}

	/**
	 * When the user highlights a piece of data and then clicks the delete button, 
	 * this function will add a query to queries that will delete the row, and also
	 * deletes the corresponding row within list. 
	 * @param row - the row in which the data will be deleted
	 * @throws IOException thrown if connection failed
	 */
	protected void deleteData(int row) throws IOException {
		queries.add("delete from logerrors where Keyword = \'" + addSingleQuote(list.get(row)[1]) + "\'");
		list.remove(row);
		//Change the data accordingly since something has been deleted
		transferData("CHANGE");
		admin.resetData();
		admin.savedWords.remove(list.get(row)[1]);
	}

	/**
	 * Transfers the data within defaultList or list, and creates a new Object[][]
	 * for AdminView so that the table in AdminView accurately reflects what
	 * the user wants to see. If choice is "DEFAULT" then the contents of list
	 * become that of defaultList and the list of queries is cleared. Otherwise,
	 * the contents of list are used to create table entries. 
	 * @param choice Either sets contents of defaultList to list or vice versa
	 * 
	 */
	protected void transferData(String choice) {
		if(choice.equals("DEFAULT")) {
			tempList = defaultList;
			queries.clear();
			admin.savedWords = admin.keyWords;
		}
		else {
			tempList = list;
		}
		Object[][] myData = new Object[tempList.size()][];
		for(int i = 0; i < tempList.size(); i++) {
			myData[i] = tempList.get(i);
		}
		data = myData;

	}

	/**
	 * Called in AdminView after the data has been changed, and the contents
	 * of data are used to generate the table entries in AdminView.
	 * @return Returns the data object which contains entries reflecting user actions
	 */
	protected Object[][] getData(){
		return data;
	}

	/**
	 * This function goes through all the strings in queries and sends them to the database
	 * to save the changes that the user has made, so that the database now reflects what is
	 * seen in the interface table. The contents of defaultList are set to be the same
	 * as the contents in list. 
	 * @throws IOException thrown if connection failed
	 * @throws ClassNotFoundException thrown if classpath is broken
	 * @throws SQLException thrown if error connecting to database
	 */
	protected void saveDefault() throws IOException, ClassNotFoundException, SQLException{
		defaultList.clear();
		admin.keyWords.clear();
		for(int x = 0; x < admin.savedWords.size(); x++) {
			admin.keyWords.add(admin.savedWords.get(x));
		}
		for(int i = 0; i < list.size(); i++){
			defaultList.add(list.get(i));
		}
		for (int j = 0; j < queries.size(); j++) {
			System.out.println(queries.get(j));
		}

		String driver = "net.sourceforge.jtds.jdbc.Driver";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection("jdbc:jtds:sqlserver://vwaswp02:1433/coeus", "coeus", "C0eus");

		Statement stmt = conn.createStatement();
		for(int j = 0; j < queries.size(); j++) {
			stmt.executeUpdate(queries.get(j));
		}
		stmt.close();
		queries.clear();
	}

}



