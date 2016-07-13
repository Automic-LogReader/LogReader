package interfaceTest;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

@SuppressWarnings("unused")
public class LogParser {
	protected String [] headers = {"Error #", "Timestamp",
			"Keywords", "Error Message", "Suggested Solution"};
	protected List<Object[]> errorData = new ArrayList<Object[]>();
	private Object [][] data;
	private int errorCount;
	//private ProgressDialog dialog;
	private long progress;
	private int percent;
	private int oldPercent;

	protected String logLine;
	//Holds the individual entries from logLine, split by " "
	protected String[] logWords;
	//Holds a line from the error suggestions csv file
	protected String errorLine;
	//Holds the individual cell entries from errorLine
	protected String [] errorWords;
	//ArrayList to hold all the UCodes from the csv file
	private Object[] entry;
	//Holds the size of the file in bytes
	
	protected static UserView view;
	
	private int correct = 0;
	private int incorrect = 0;
	
	public LogParser(UserView view){
		LogParser.view = view;
	}
	
	void parseErrors(File file, ProgressDialog pd) throws IOException
	{
		view.updateKeyWords();
		
		percent = 0;
		oldPercent = 0;
		
		errorData.clear();
		errorCount = 0;
		progress = 0;
		String timeStamp = null;
		StringBuilder errorMessage = new StringBuilder();
		boolean keywordFound = false;
		boolean timeStampFound = false;
		boolean specialCase = false;
		
		FileReader logInput = new FileReader(view.logFile);
		BufferedReader logbr = new BufferedReader(logInput);

		logLine = logbr.readLine();
		
		//Timer for performance testing
		long startTime = System.nanoTime();
		while(logLine != null)
		{
			progress += logLine.length();
			percent = (int) (progress / view.fileSizeDivHundred);
			if (percent > oldPercent){
				view.dialog.updateProgress(percent);
				oldPercent = percent;
			}

			logWords = logLine.split(" ");
			
			timeStamp = null;
			errorMessage.setLength(0);
			entry = null;
			
			keywordFound = false;
			timeStampFound = false;
			specialCase = false;
			
			for (String testWord : logWords){
				//Timestamp will always come first
				//Is this a reliable way to find timestamp?
				//Maybe change to regex
				if (testWord.length() == 19 && !timeStampFound){
					timeStamp = testWord;
					timeStampFound = true;
				}
				if(timeStampFound && !keywordFound){
					//Testing the UCode from the file against the error UCodes
					if (view.keyWords.contains(testWord)){
						keywordFound = true;
						errorCount++;
						if (testWord.equals("===>") && logLine.contains("Time critical")) {
							entry = parseArrowError(logbr, timeStamp, logWords);
							specialCase = true;
							break;
						}
						else {
							entry = new Object[5];
							entry[0] = errorCount;
							entry[1] = timeStamp;
							entry[2] = testWord;
				
							if (view.solutions.get(entry[2]) != null){
								entry[4] = view.solutions.get(entry[2]);
							}
						}
					}
				}
				
				else if(timeStampFound && keywordFound){
					errorMessage.append(testWord + " ");
				}
			}
			
			if(entry != null){
				if (!specialCase){
					entry[3] = errorMessage.toString();
				}
				if (entry[3] == null){
					entry[3] = " ";
				}
				errorData.add(entry);
			}
			logLine = logbr.readLine();
		}
		
		//Logs execution time to the console
		long endTime = System.nanoTime();
		long duration = (endTime - startTime)/1000000;
		System.out.println("Operation completed in " + duration + " ms");
		
		view.dialog.doneParse(errorCount);
		logbr.close();
		data = new Object[errorData.size()][];
		
		for(int i = 0; i < errorData.size(); i++){
			data[i] = errorData.get(i);
		}
		makeTable();
		System.out.println("Non-matching arrow errors: " + incorrect + " hidden errors: " + correct);
	}
	
	Object[] parseArrowError(BufferedReader logbr, String timeStamp, String[] currArray) throws IOException{
		
		Object[] tempEntry = new Object[5];
		tempEntry[0] = errorCount;
		tempEntry[1] = timeStamp;
		tempEntry[2] = "===>";
		int arrowindex = 0;
		if (view.solutions.get(tempEntry[2]) != null){
			tempEntry[4] = view.solutions.get(tempEntry[2]);
		}
		StringBuilder errorMsg = new StringBuilder();

		boolean closingArrowTagFound = false;
		for (int i=0; i<currArray.length; i++){
			if (currArray[i].equals("===>")){
				for (int j=(i+1); j<currArray.length; j++){
					errorMsg.append(currArray[j] + " ");
				}
				break;
			}
		}
		String firstLineOfError = errorMsg.toString();
		
		logLine = logbr.readLine();
		while (!closingArrowTagFound && logLine != null){
			progress += logLine.length();
			
			percent = (int) (progress / view.fileSizeDivHundred);
			if (percent > oldPercent){
				view.dialog.updateProgress(percent);
				oldPercent = percent;
			}
			for (String s : view.keyWords){
				if (logLine.contains(s) && !s.equals("===>")){
					System.out.println(s);
					correct++;
				}
			}
			if (logLine.contains("===>")){
				if (logLine.contains("Time critical")){
					//System.out.println(logLine);
					incorrect++;
					errorCount++;
					tempEntry[3] = firstLineOfError;
					String[] tempArray = logLine.split(" ");
					String tempTimeStamp = "";
					for (String s : tempArray){
						if (s.length() == 19){
							tempTimeStamp = s;
							break;
						}
					}
					errorData.add(parseArrowError(logbr, tempTimeStamp, tempArray));
					return tempEntry;
				}
				errorMsg.append(logLine + " ");
				closingArrowTagFound = true;
				break;
			}
			errorMsg.append(logLine + " ");
			if (!closingArrowTagFound){
				logLine = logbr.readLine();
			}
		}
		tempEntry[3] = errorMsg.toString();
		return tempEntry;
	}

	void makeTable()
	{
		DefaultTableModel tableModel = new DefaultTableModel(data, headers) {
		    @Override
		    public boolean isCellEditable(int row, int column) {
		       //all cells false
		       return false;
		    }
		};
		
		view.errorTable = new JTable(tableModel){
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
		
		view.errorTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		view.errorScrollPane.setViewportView(view.errorTable);
	}
	
}
