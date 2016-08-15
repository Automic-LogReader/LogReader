/**
 * @file DataController.java
 * @authors Leah Talkov, Jerry Tsui
 * @date 8/15/2016
 * Contains functions that modify the table in AdminView
 * depending on whether the user adds, modifies, or deletes entries. The
 * class also has variables that keep track of default values of the database.
 * When the user wants to save their changes to the database, functions
 * will query to the database to reflect those changes. 
 */

package interfaceTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DataController {

	/**List that contains the true current entries in the database*/
	private List <String[]> defaultErrorList = new ArrayList<String[]>();
	/**List that contains contents that reflect user changes*/
	private List <String[]> curErrorlist = new ArrayList<String[]>();
	/**Serves as a helper when transferring contents between list and defaultList*/
	private List <String[]> tempErrorList = new ArrayList<String[]>();
	private List<String[]> defaultHyperlinkList = new ArrayList<String[]>();
	private List<String[]> curHyperlinkList = new ArrayList<String[]>();
	private List<String[]> tempHyperlinkList = new ArrayList<String[]>();
	/**Queries to be made to the database*/
	private List <String> errorQueries = new ArrayList<String>();
	/**Entries used for JTable parameter in AdminView*/
	private Object [][] errorData;
	/**Entries used for JTable parameter in AdminView*/
	private Object [][] hyperlinkData;
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
	void setErrorList(List <String[]> list){
		curErrorlist = list;
	}

	/**
	 * Called in AdminView to initialize the contents of the defaultList
	 * @param defaultList A list of the entries of the table from AdminView
	 */
	void setDefaultErrorList(List <String[]> defaultList){
		defaultErrorList = defaultList;
	}

	/**
	 * Called in AdminView to get the changes necessary for JTable content
	 * @return Returns the list which reflects the user changes
	 */
	List<String[]> getErrorList(){
		return curErrorlist;
	}

	/**
	 * Called in AdminView to get the old content when the user
	 * wants to revert their changes back to the default 
	 * @return Returns the list which reflects the current contents of the DB
	 */
	List<String[]> getDefaultErrorList(){
		return defaultErrorList;
	}
	
	/**
	 * Called in AdminView to initialize the contents of the curHyperlinkList
	 * @param list A list of the current hyperlinks and their keywords from the database
	 */
	void setHyperlinkList(List<String[]> list) {
		curHyperlinkList = list;
	}
	
	/**
	 * Called in AdminView to initialize the contents of the defaultHyperlinkList
	 * @param defaultList A list of the current hyperlinks and their keywords from the database
	 */
	void setDefaultHyperlinkList(List<String[]> defaultList) {
		defaultHyperlinkList = defaultList;
	}
	
	/**
	 * Called in AdminView to get the changes necessary for JTable content
	 * @return Returns the hyperlink list that reflect user changes
	 */
	List<String[]> getHyperlinkList() {
		return curHyperlinkList;
	}
	
	/**
	 * Called in AdminView when the user wants to revert their changes
	 * @return Returns the hyperlink list that reflects the database contents
	 */
	List<String[]> getDefaultHyperlinkList() {
		return defaultHyperlinkList;
	}

	/**
	 * Called in AdminView after the data has been changed, and the contents
	 * of errorData are used to generate table entries in AdminView.
	 * @return Returns errorData which contains entries reflecting user changes
	 */
	Object[][] getErrorData() {
		return errorData;
	}
	
	/**
	 * Called in AdminView after the data has been changed, and the contents
	 * of hyperlinkData are user to generate table entries in AdminView
	 * @return Returns hyperlinkData which contains entries reflecting user changes
	 */
	Object[][] getHyperlinkData() {
		return hyperlinkData;
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
	 */
	protected void modifyData(String folder, String keyWord, String message, String solution, String choice, int row) {
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
						Utility.addSingleQuote(folder) + "\' where Keyword = \'" + Utility.addSingleQuote(curErrorlist.get(row)[1]) + "\'");
			if(errorMessageChanged)
				errorQueries.add("update logerrors set Log_Error_Description = \'" +
						Utility.addSingleQuote(message) + "\' where Keyword = \'" + Utility.addSingleQuote(curErrorlist.get(row)[1]) + "\'");
			if(suggestedSolutionChanged)
				errorQueries.add("update logerrors set Suggested_Solution = \'" +
						Utility.addSingleQuote(solution) + "\' where Keyword = \'" + Utility.addSingleQuote(curErrorlist.get(row)[1]) + "\'");
			if(keywordChanged)
				errorQueries.add("update logerrors set Keyword = \'" +
						Utility.addSingleQuote(keyWord) + "\' where Keyword = \'" + Utility.addSingleQuote(curErrorlist.get(row)[1]) + "\'");
			admin.savedWords.remove(curErrorlist.get(row)[1]);
			admin.savedWords.add(keyWord);
			curErrorlist.set(row, tempArray);
			curHyperlinkList.get(row)[0] = keyWord;

		}
		//Otherwise we are adding an entry and simply add to the list and queries
		else {
			errorQueries.add("insert into logerrors values (\'" + Utility.addSingleQuote(keyWord) + "\',\'"
					+ Utility.addSingleQuote(message) + "\',\'" + Utility.addSingleQuote(solution) + "\',\'" +
					Utility.addSingleQuote(folder) + "\',\'" + "http://google.com" + "\')");
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
	 * When the user highlights a piece of data and clicks the delete button, 
	 * this function will add a query to the data structure queries which 
	 * will delete the row, and also deletes the corresponding row within list. 
	 * @param row - the row in which the data will be deleted
	 */
	protected void deleteData(int row) {
		errorQueries.add("delete from logerrors where Keyword = \'" + Utility.addSingleQuote(curErrorlist.get(row)[1]) + "\'");
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
	 * Goes through all the strings in queries and sends them to the database
	 * to save the changes that the user has made, so that the database now reflects what is
	 * seen in the interface table. The contents of defaultList are set to be the same
	 * as the contents in list. 
	 * @throws ClassNotFoundException If getClass fails
	 * @throws SQLException If connection to SQL server fails
	 */
	protected void saveDefault() throws ClassNotFoundException, SQLException {
		defaultErrorList.clear();
		admin.keyWords.clear();
		for(int x = 0; x < admin.savedWords.size(); x++) {
			admin.keyWords.add(admin.savedWords.get(x));
		}
		for(int i = 0; i < curErrorlist.size(); i++){
			defaultErrorList.add(curErrorlist.get(i));
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

	/**
	 * Takes in the contents of the defaultHyperlinkList and queries the matching
	 * hyperlinks to the keywords to the database
	 * @throws ClassNotFoundException If getClass fails
	 * @throws SQLException If connection to SQL server fails
	 */
	void writeURLsToDB() throws ClassNotFoundException, SQLException {
		String driver = "net.sourceforge.jtds.jdbc.Driver";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection("jdbc:jtds:sqlserver://vwaswp02:1433/coeus", "coeus", "C0eus");
		Statement stmt = conn.createStatement();
		for(int i = 0; i < curHyperlinkList.size(); i++) {
			stmt.executeUpdate("update logerrors set Hyperlink = \'" + 
					Utility.addSingleQuote(defaultHyperlinkList.get(i)[1]) + 
					"\' where Keyword = \'" + Utility.addSingleQuote(defaultHyperlinkList.get(i)[0]) + "\'");
		}
		stmt.close();
	}
	
}



