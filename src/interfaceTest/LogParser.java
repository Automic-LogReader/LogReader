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


public class LogParser {
	protected final String [] headers = {"Error #", "Timestamp",
			"Keywords", "Error Message", "Suggested Solution"};
	protected List<Object[]> errorData = new ArrayList<Object[]>();
	private Object [][] data;
	private int errorCount;
	//private ProgressDialog dialog;
	private long progress;
	private int percent;
	private int oldPercent;
	private int selectedTab;
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
	private LogicEvaluator logicEvaluator;
	
	protected static UserView view;
	
	public LogParser(UserView view, int tab){
		selectedTab = tab;
		LogParser.view = view;
		logicEvaluator = new LogicEvaluator(this);
	
		if(view.keyWordArrayList != null)
			logicEvaluator.setkeyWords(view.keyWordArrayList);
		if(view.operandArrayList != null)
			logicEvaluator.setOperands(view.operandArrayList);
		if(view.notArrayList != null)
			logicEvaluator.setHasNot(view.notArrayList);
		//Something will set the arraylists here (if NOT null)
		if (tab == 1){
			logicEvaluator.addORs();
		}
	
	}
	
	void parseErrors(File file, ProgressDialog pd) throws IOException
	{
		view.updateKeyWords(selectedTab);
		
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
			updateProgress();
			///THIS IS JUST A PLACEHOLDER
			if(selectedTab == 1)
			{
				logicEvaluator.addLines(logLine, logbr);
			}
			else
			{
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
							if (testWord.equals("DEADLOCK")){
								entry = parseDeadlockError(logbr, timeStamp);
								specialCase = true;
								break;
							}
							else if (testWord.equals("===>") && logLine.contains("Time critical")) {
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
			
			}
			logLine = logbr.readLine();
		}
		logicEvaluator.makeEntries();
		//Logs execution time to the console
		long endTime = System.nanoTime();
		long duration = (endTime - startTime)/1000000;
		System.out.println("Operation completed in " + duration + " ms");
		
		if(selectedTab ==1){
			errorCount = logicEvaluator.getErrorCount();
		}
		view.dialog.doneParse(errorCount);
		logbr.close();
		data = new Object[errorData.size()][];
		
		for(int i = 0; i < errorData.size(); i++){
			data[i] = errorData.get(i);
		}
		makeTable();
	}
	
	Object[] parseArrowError(BufferedReader logbr, String timeStamp, String[] currArray) throws IOException{
		
		Object[] tempEntry = new Object[5];
		String[] words;
		tempEntry[0] = errorCount;
		tempEntry[1] = timeStamp;
		tempEntry[2] = "===>";
		int arrowindex = 0;  
        boolean closingArrowTagFound = false;
        boolean outsideTimeStampBounds = false;
        StringBuilder errorMsg = new StringBuilder();
        
        if (!compareTimeStamp(currArray)){
			outsideTimeStampBounds = true;
		}
		if (view.solutions.get(tempEntry[2]) != null){
			tempEntry[4] = view.solutions.get(tempEntry[2]);
		}

		for (int i=0; i<currArray.length; i++){
			if (currArray[i].equals("===>")){
				arrowindex = i-1;
				for (int j=(i+1); j<currArray.length; j++){
					errorMsg.append(currArray[j] + " ");
				}
				break;
			}
		}
		String firstLineOfError = errorMsg.toString();
		
		logLine = logbr.readLine();
		while (!closingArrowTagFound && logLine != null){
			updateProgress();
			words = logLine.split(" ");
			
			if (logLine.contains("===>")){
				if (logLine.contains("Time critical")){
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
				if (arrowindex < words.length){
					for (int i = arrowindex; i < words.length; i++){
						errorMsg.append(words[i] + " ");
					}
				}
				else {
					errorMsg.append(logLine + " ");
				}
				closingArrowTagFound = true;
				break;
			}
			if (arrowindex < words.length){
				for (int i = arrowindex; i < words.length; i++){
					errorMsg.append(words[i] + " ");
				}
			} 
			else {
				errorMsg.append(logLine + " ");
			}
			if (!closingArrowTagFound){
				logLine = logbr.readLine();
			}
		}
		tempEntry[3] = errorMsg.toString();
		if (outsideTimeStampBounds){
			errorCount--;
			return null;
		}
		return tempEntry;
	}
	
	Object[] parseDeadlockError(BufferedReader logbr, String timeStamp) throws IOException {
           Object[] entry = new Object[5];
           int difference = 20;
           ArrayList <String> errorLines = new ArrayList<String>();
           boolean matchingDeadlock = false;
           StringBuilder testLine = new StringBuilder();
           StringBuilder errorMsg = new StringBuilder();
           String[] words;
           entry[0] = errorCount;
           entry[1] = timeStamp;
           entry[2] = "DEADLOCK";
           String Line = logbr.readLine();
           while(!matchingDeadlock && Line != null){
                  boolean timeStampFound = false;
                  boolean uCodeFound = false;              
                  testLine.setLength(0);
                  updateProgress();
                  words = Line.split(" ");
                  for(String testWord : words){
                        if(!timeStampFound && testWord.length() == 19){
                               timeStampFound = true;
                               //If our timestamps are not equal, we 
                               //don't have a matching deadlock
                               //Make a new if statement here
                               if(timeStampDifference(testWord, timeStamp)){
                                      entry[3] = " ";
                                      if (view.solutions.get(entry[2]) != null)
                                             entry[4] = view.solutions.get(entry[2]);
                                      return entry;
                               }
                        }
       
                        else if(!uCodeFound && timeStampFound){
                               if(testWord.length() > 2){
                                      if(testWord.charAt(0) == 'U' && Character.isDigit(testWord.charAt(1))){
                                             uCodeFound = true;
                                      }
                               }
                        }
                        else if(uCodeFound && timeStampFound){

                               if(testWord.equals("DEADLOCK")){
                                      matchingDeadlock = true;
                                      for(int i = 0; i < errorLines.size(); i++){
                                             errorMsg.append(errorLines.get(i));
                                      }
                                      entry[3] = errorMsg.toString();
                                      if (view.solutions.get(entry[2]) != null)
                                             entry[4] = view.solutions.get(entry[2]);
                                      
                                      return entry;
                               }
                               else
                                      testLine.append(testWord + " ");  
                        }      
                  }
                  //System.out.println("I am adding a line to errormsg");
                  errorLines.add(testLine.toString());
                  Line = logbr.readLine();
           }
           return entry;
    }

	void makeTable(){
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
		view.errorTable.setCellSelectionEnabled(true);
		view.errorTable.addMouseListener(new TableMouseListener(view.errorTable));
		view.errorTable.setComponentPopupMenu(view.popupMenu);
	}
	
	boolean timeStampDifference(String testStamp, String timeStamp){
           int i1 = Integer.parseInt(testStamp.substring(16));
           int i2 = Integer.parseInt(timeStamp.substring(16));
           if (i1 > i2){
                  if((i1 - i2) > 20)
                        return true;
                  else
                        return false;
           }
           else{
                  if((i2 - i1) > 20)
                        return true;
                  else 
                        return false;
           }
    }
    
	//Helper function for the progress bar
	void updateProgress(){
		progress += logLine.length();
		percent = (int) (progress / view.fileSizeDivHundred);
		if (percent > oldPercent){
			view.dialog.updateProgress(percent);
			oldPercent = percent;
		}
	}
	
	boolean compareTimeStamp(String[] line){
		String time = line[(line.length-1)].replaceAll("[.]", "");
		time = time.replaceAll("\'", "");
		time = time.replace(":", ".");
		//System.out.println(time);
		double t = Double.parseDouble(time);
		//System.out.println(t + " lower: " + view.lowerBound + " upper: " + view.upperBound + " result: " + Boolean.toString((t >= view.lowerBound) && (t <= view.upperBound)));
		return ((t >= view.lowerBound) && (t <= view.upperBound));
	}
}
