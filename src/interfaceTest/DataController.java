/**
 * @file DataController.java
 * @authors Leah Talkov, Jerry Tsui
 * @date 8/3/2016
 * Contains functions that modify the table in AdminView
 * depending on whether the user adds, modifies, or deletes entries. The
 * class also has variables that keep track of default values of the database.
 * When the user wants to save their changes to the database, functions
 * will query to the database to reflect those changes. 
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

	/**List that contains the true current entries in the database*/
	private List <String[]> defaultErrorList = new ArrayList<String[]>();
	/**List that starts out with the same contents as defaultList, and
	then changes its contents to reflect the user changes*/
	private List <String[]> curErrorlist = new ArrayList<String[]>();
	/**Serves as a helper when transferring contents between list and defaultList*/
	private List <String[]> tempErrorList = new ArrayList<String[]>();
	/**Queries to be made to the database*/
	private List <String> errorQueries = new ArrayList<String>();
	private List <String> hyperlinkQueries = new ArrayList<String>();
	/**Entries to be into the JTable in AdminView*/
	private Object [][] errorData;
	protected Object [][] hyperlinkData;
	/**Set in modify dialog, true if keyword was changed*/
	private boolean keywordChanged;
	/**Set in modify dialog, true if error message was changed*/
	private boolean errorMessageChanged;
	/**Set in modify dialog, true if solution message was changed*/
	private boolean suggestedSolutionChanged;
	/**Set in modify dialog, true if folder name was changed*/
	private boolean folderChanged;
	/**AdminView object that contains the table DataController is changing*/
	private AdminView admin;
	protected List<String[]> defaultHyperlinkList = new ArrayList<String[]>();
	protected List<String[]> curHyperlinkList = new ArrayList<String[]>();
	protected List<String[]> tempHyperlinkList = new ArrayList<String[]>();

	/**
	 * Created in AdminView, contains functions that connects to the database
	 * and creates queries to modify the data
	 * @param admin AdminView associated with this object
	 */
	DataController(AdminView admin){
		this.admin = admin;
		keywordChanged = false;
		errorMessageChanged = false;
		suggestedSolutionChanged = false;
		folderChanged = false;
	}

	/**
	 * Called in AddDialog and ModifyDialog to
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
	 * Called in AdminView to initialize the contents of the list
	 * @param list A list of the entries of the table from AdminView
	 */
	void setList(List <String[]> list){
		this.curErrorlist = list;
	}

	/**
	 * TCalled in AdminView to initialize the contents of the defaultList
	 * @param defaultList A list of the entries of the table from AdminView
	 */
	void setDefaultList(List <String[]> defaultList){
		this.defaultErrorList = defaultList;
	}

	/**
	 * Called in AdminView to either retrieve the contents to change the 
	 * JTable, or to clear the list if the user wants to revert their changes
	 * @return Returns the list which reflects the user changes
	 */
	List<String[]> getList(){
		return curErrorlist;
	}

	/**
	 * Called in AdminView to get the old content when the user
	 * wants to revert their changes back to the default 
	 * @return Returns the list which reflects the current contents of the DB
	 */
	List<String[]> getDefaultList(){
		return defaultErrorList;
	}

	/**
	 * Modfies the data by depending on the value of "choice". If the value
	 * is "MODIFY" then the function modifies an entry by modifying list, and by adding a query
	 * to the queries data structure. If the value is "ADD", then the function adds an entry to
	 * list and adds a query that makes an entry reflecting the values given by the user. 
	 * @param folder The folder name given by the user
	 * @param keyWord The keyWord given by the user - cannot be an existing keyword
	 * @param message The error message given by the user
	 * @param solution The solution message given by the user
	 * @param choice Determines whether the user is modifying or adding entries
	 * @param row If the choice is MODIFY, the row index that is being modified
	 * @throws IOException
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
				errorQueries.add("update logerrors set Folder = \'" + 
						addSingleQuote(folder) + "\' where Keyword = \'" + addSingleQuote(curErrorlist.get(row)[1]) + "\'");
			if(errorMessageChanged)
				errorQueries.add("update logerrors set Log_Error_Description = \'" +
						addSingleQuote(message) + "\' where Keyword = \'" + addSingleQuote(curErrorlist.get(row)[1]) + "\'");
			if(suggestedSolutionChanged)
				errorQueries.add("update logerrors set Suggested_Solution = \'" +
						addSingleQuote(solution) + "\' where Keyword = \'" + addSingleQuote(curErrorlist.get(row)[1]) + "\'");
			if(keywordChanged)
				errorQueries.add("update logerrors set Keyword = \'" +
						addSingleQuote(keyWord) + "\' where Keyword = \'" + addSingleQuote(curErrorlist.get(row)[1]) + "\'");
			admin.savedWords.remove(curErrorlist.get(row)[1]);
			admin.savedWords.add(keyWord);
			curErrorlist.set(row, tempArray);
			curHyperlinkList.get(row)[0] = keyWord;

		}
		//Otherwise we are adding an entry and simply add to the list and queries
		else {
			errorQueries.add("insert into logerrors values (\'" + addSingleQuote(keyWord) + "\',\'"
					+ addSingleQuote(message) + "\',\'" + addSingleQuote(solution) + "\',\'" +
					addSingleQuote(folder) + "\',\'" + "http://google.com" + "\')");
			curErrorlist.add(tempArray);
			String[] someArray = new String[2];
			someArray[0] = keyWord;
			curHyperlinkList.add(someArray);
			admin.savedWords.add(keyWord);
		}
		//Reset the booleans and transfer data indicating that a change in the data has occurred
		transferData("CHANGE");
		folderChanged = false;
		keywordChanged = false;
		errorMessageChanged = false;
		suggestedSolutionChanged = false;
		admin.resetErrorData();	
		admin.resetHyperlinkData();
	}


	/**
	 * Adds single quotes to the parameter old word, because SQL syntax uses
	 * single quotes to encapsulate; i.e. if we had an error message that said
	 * I want to make 'examples', This function adds single quotes to change 
	 * the message to: I want to make ''examples'' and keeps the message intact. 
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
	 * When the user highlights a piece of data and clicks the delete button, 
	 * this function will add a query to the data structure queries which 
	 * will delete the row, and also deletes the corresponding row within list. 
	 * @param row - the row in which the data will be deleted
	 * @throws IOException 
	 */
	protected void deleteData(int row) throws IOException {
		errorQueries.add("delete from logerrors where Keyword = \'" + addSingleQuote(curErrorlist.get(row)[1]) + "\'");
		admin.savedWords.remove(curErrorlist.get(row)[1]);
		curErrorlist.remove(row);
		curHyperlinkList.remove(row);
		//Change the data accordingly since something has been deleted
		transferData("CHANGE");
		admin.resetErrorData();
		admin.resetHyperlinkData();
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
			tempErrorList = defaultErrorList;
			tempHyperlinkList = defaultHyperlinkList;
			errorQueries.clear();
			admin.savedWords.clear();
			for(int i = 0; i < admin.keyWords.size(); i++) {
				admin.savedWords.add(admin.keyWords.get(i));
			}
		}
		else {
			tempHyperlinkList = curHyperlinkList;
			tempErrorList = curErrorlist;
		}
		Object[][] myData = new Object[tempErrorList.size()][];
		for(int i = 0; i < tempErrorList.size(); i++) {
			myData[i] = tempErrorList.get(i);
		}
		errorData = myData;
		
		Object[][] tempData = new Object[tempHyperlinkList.size()][];
		for(int j = 0; j < tempHyperlinkList.size(); j++) {
			tempData[j] = tempHyperlinkList.get(j);
		}
		hyperlinkData = tempData;
	}

	/**
	 * Called in AdminView after the data has been changed, and the contents
	 * of data are used to generate the table entries in AdminView.
	 * @return Returns the data object which contains entries reflecting user actions
	 */
	protected Object[][] getData(){
		return errorData;
	}

	/**
	 * Goes through all the strings in queries and sends them to the database
	 * to save the changes that the user has made, so that the database now reflects what is
	 * seen in the interface table. The contents of defaultList are set to be the same
	 * as the contents in list. 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	protected void saveDefault() throws IOException, ClassNotFoundException, SQLException{
		defaultErrorList.clear();
		admin.keyWords.clear();
		for(int x = 0; x < admin.savedWords.size(); x++) {
			admin.keyWords.add(admin.savedWords.get(x));
		}
		for(int i = 0; i < curErrorlist.size(); i++){
			defaultErrorList.add(curErrorlist.get(i));
		}
		for (int j = 0; j < errorQueries.size(); j++) {
			System.out.println(errorQueries.get(j));
		}

		String driver = "net.sourceforge.jtds.jdbc.Driver";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection("jdbc:jtds:sqlserver://vwaswp02:1433/coeus", "coeus", "C0eus");

		Statement stmt = conn.createStatement();
		for(int j = 0; j < errorQueries.size(); j++) {
			stmt.executeUpdate(errorQueries.get(j));
		}
		stmt.close();
		errorQueries.clear();
	}

}



