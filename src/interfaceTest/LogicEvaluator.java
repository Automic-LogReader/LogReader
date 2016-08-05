package interfaceTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class LogicEvaluator {

	/**LogParser object that sends lines from the buffer to this class*/
	private LogParser logParse;
	/**Holds the firstKeyword that the user chose*/
	private String firstKeyword;
	/**Contains matching booleans for whether a keyword is NOT-ed*/
	private ArrayList<Boolean> hasNot = new ArrayList<Boolean>();
	/**Contains the final valid lines that entries will be made from*/
	private ArrayList<String> validLines = new ArrayList<String>();
	/**Contains the list of words that are used with the operand "And"*/
	private ArrayList<String> andWords = new ArrayList<String>();
	/**Contains the operands that the user has chosen*/
	private ArrayList<String> operands = new ArrayList<String>();
	/**The total number of errors/entries from the given logic statement*/
	private int errorCount;
	/**The word that the user has chosen to use the operand OR with*/
	private String orWord;
	/**True if the user has used the keyword DEADLOCK with the OR operand*/
	private boolean OR_DEADLOCK;
	/**True if the user has used the keyword DEADLOCK with the AND NOT operand*/
	private boolean AND_NOT_DEADLOCK;
	/**True if the user has used the keyword DEADLOCK with the AND operand*/
	private boolean AND_DEADLOCK;
	/**True if the user has used the keyword ===> with the AND NOT operand*/
	private boolean AND_NOT_ARROW;
	/**True if the user has used the keyword ===> with the AND operand*/
	private boolean AND_ARROW;
	/**True if the user has used the keyword ===> with the OR operand*/
	private boolean OR_ARROW;
	private ArrayList<String> tempLinesBefore = new ArrayList<String>();	
	private int resetCount;
	private boolean earlyAdd;
	/**
	 * Contains functions that implements the logic parsing
	 * of a file, depending on the logic statement
	 * inputed by the user.
	 * @param logParse 
	 */
	LogicEvaluator(LogParser logParse) {
		this.logParse = logParse;
		orWord = "";
		OR_DEADLOCK = false;
		AND_NOT_DEADLOCK = false;
		AND_DEADLOCK = false;
		errorCount = 0;
		resetCount = 0;
		earlyAdd = false;
	}
	
	/**
	 * Called in UserView to set the array to the one generated
	 * by the user's logic statement
	 * @param keyWords Contains the keywords chosen by the user
	 */
	void setkeyWords(ArrayList <String> keyWords) {
		this.andWords = keyWords;
	}
	
	/**
	 * Called in UserView to set the array to the one generated
	 * by the user's logic statement
	 * @param operands Contains the operands for the associated keywords
	 */
	void setOperands(ArrayList <String> operands) {
		this.operands = operands;
	}
	
	/**
	 * Called in UserView to set the array to the one generated
	 * by the user's logic statement
	 * @param hasNot Contains the boolean flags for the associated keywords
	 */
	void setHasNot(ArrayList <Boolean> hasNot) {
		this.hasNot = hasNot;
	}
	
	/**
	 * Called in LogParser if the user has selected the LogicEval tab,
	 * gives the number of errors found 
	 * @return The errorCount that was generated during parsing 
	 */
	int getErrorCount() {
		return errorCount;
	}
	
	/**
	 * Checks for duplicate errors against DEADLOCK and arrow errors, and 
	 * then creates entries for the interface table based off of the entries in 
	 * the arraylist validLines. 
	 */
	void makeEntry(String makeLine) {
		errorCount++;
		Object[] entry = new Object[5];
		boolean timeStampFound = false;
		boolean uCodeFound = false;
		boolean hitDeadLock = false;
		boolean hitArrow = false;
		StringBuilder errorMsg = new StringBuilder();
		String[] logWords = makeLine.split(" ");
		for (String testWord : logWords) {
			//We find the timeStamp to load it as one of the Entry fields
			if (testWord.length() == 19 && !timeStampFound) {
				entry[0] = errorCount;
				entry[1] = testWord;
				entry[2] = firstKeyword;
				timeStampFound = true;
			}
			//We find the U-code to get to the start of the error message
			else if(!uCodeFound && timeStampFound) {
				if(testWord.length() > 2) {
					if(testWord.charAt(0) == 'U' && Character.isDigit(testWord.charAt(1))) {
						uCodeFound = true;
					}
				}
			}
			//After we find the U-code, then we can start building up the error message
			else if(uCodeFound && timeStampFound) {
				if(makeLine.contains("DEADLOCK")) {
					if(testWord.equals("DEADLOCK")) {
						hitDeadLock = true;
						continue;
					}
					//We don't start building the DEADLOCK message until
					//we hit DEADLOCK on the line
					if(hitDeadLock) {
						errorMsg.append(testWord + " ");
					}
				}
				else if(makeLine.contains("===>")) {
					if(testWord.equals("===>")) {
						//We don't start building the ===> message until
						//we hit ===> on the line
						hitArrow = true;
						continue;
					}
					if(hitArrow)
						errorMsg.append(testWord + " ");
				}
				else
					errorMsg.append(testWord + " ");	
			}
		}
		entry[3] = errorMsg.toString();
		if(logParse.view.solutions.get(entry[2]) != null)
			entry[4] = logParse.view.solutions.get(entry[2]);
		logParse.errorData.add(entry);
	}

	/**
	 * Adds lines to validLines, deadlockOrLines, and arrowOrLines
	 * depending on the logical statement that the user has inputed. Lines that
	 * fit the statement will be put into the above ArrayLists while invalid
	 * lines will be ignored. 
	 * @param line File line that is parsed against the logic statement
	 * @param br Buffered Reader to read through the file
	 * @throws IOException
	 */
	void addLines(String line, BufferedReader br) throws IOException {
		//If they don't want deadlock lines, then we skip over them
		if(AND_NOT_DEADLOCK) {
			if(line.contains("DEADLOCK")) {
				//The line returned is the line after the matching (or single) deadlock
				String newLine = progressDeadlockBr(br, line);
				addLines(newLine, br);
				}
		}
		//If they don't want arrow lines, then we skip over them
		if(AND_NOT_ARROW) {
			if(line.contains("===>")) {
				//The line returned is the line after the matching (or single) ===>
				String newLine = progressArrowBr(br, line);
				addLines(newLine, br);
			}
		}
		//If orWord = "", then no OR operand was set. Otherwise
		//if the line contains the orWord, it is valid
		if(line.contains(orWord) && (orWord != "")) {
			if(OR_DEADLOCK) {
				//This is the arraylist that has the deadlock lines (check duplicates on)
				logParse.addLinesBefore();
				makeEntry(makeDeadlockLine(br, line));
			}
			else if (OR_ARROW) {
				//Wanna put addlines here
				logParse.addLinesBefore();
				String tempLine = makeArrowLine(br, line);
				if(tempLine != null) {
					makeEntry(tempLine);
				}
				else
					logParse.view.linesBeforeArrayList.remove(logParse.view.linesBeforeArrayList.size() - 1);
				//And if null, then remove the last one
				
			}
			//Otherwise it is a normal OR word and we add the line to validLines
			else {
				logParse.addLinesBefore();
				makeEntry(line);
			}
		}	
		else {
			earlyAdd = false;
			String parseLine;
			//If we have an AND DEADLOCK or an AND ===>, we need to include
			//the entire error message to parse against
			if(AND_DEADLOCK && line.contains("DEADLOCK")) {
				saveCurLines();
				parseLine = makeDeadlockLine(br, line);
				earlyAdd = true;
			}
			else if(AND_ARROW && line.contains("===>")) {
				saveCurLines();
				String tempLine = makeArrowLine(br, line);
				if(tempLine != null) {
					parseLine = tempLine;
					earlyAdd = true;
				}
				else 
					return;
			}
			else {
				parseLine = line;
			}
			for(int j = 0; j < andWords.size(); j++) {
				//If the line contains a word we DON'T want, it is invalid
				if(hasNot.get(j)) {
					if(parseLine.contains(andWords.get(j)))  {
						return;
					}
				}
				else {
					//If the line doesn't contain a word we do want, it is invalid
					if(!parseLine.contains(andWords.get(j))) {
						return;
					}
				}
			}
			//If we make it here the line is added
			
			for(int i = 0; i < tempLinesBefore.size(); i ++)
			{
				//System.out.println(errorCount+ " " + tempLinesBefore.get(i));
			}
			
			if(earlyAdd) {
				System.out.println("Sdf");
				logParse.view.linesBeforeArrayList.add(tempLinesBefore);
				for (String s : tempLinesBefore){
					System.out.println(s);
				}
			}
			else {
				System.out.println("else");
				logParse.addLinesBefore();
			}
			makeEntry(parseLine);
			//}
		}
	
	}
		
	/**
	 * Looks at the logical statement that the user has given and checks
	 * for special cases (such as AND NOT DEADLOCK, AND NOT arrow and sees if an OR 
	 * operand was set. If so, then the variable orWord is set as the corresponding word. 
	 */
	void addORs() {
		if(andWords != null){
			firstKeyword = andWords.get(0);
			//Checks for OR operand
			if(operands.contains("OR")) {
				orWord = andWords.get(2);
				andWords.remove(2); 
				//Special case of OR_DEADLOCK (loads deadlockOrLines)
				if(orWord.equals("DEADLOCK"))
					OR_DEADLOCK = true;
				//Special case of OR_ARROW (loads arrowOrLines)
				if(orWord.equals("===>"))
					OR_ARROW = true;
			}
			if(andWords.contains("DEADLOCK") && (orWord != "DEADLOCK")) {
				if	(hasNot.get(andWords.indexOf("DEADLOCK")) != true) {
					AND_DEADLOCK = true;
				}
				else
					AND_NOT_DEADLOCK = true;
			}
			if(andWords.contains("===>") && (orWord != "===>")) {
				if(hasNot.get(andWords.indexOf("===>")) != true) {
					AND_ARROW = true;
				}
				else
					AND_NOT_ARROW = true;
				}
		}
	}

	/**
	 * Progresses the buffered reader if the user has
	 * indicated that they do not want to include any arrow errors.
	 * If there is a single arrow (indicated by a time stamp difference) 
	 * then the function will return the line right below the first arrow found.
	 * Otherwise if there are matching arrows, the line returned will be the line
	 * below the matching arrow.
	 * @param logbr A buffered reader to advance through the file, same br from LogParser
	 * @param line The file line where an arrow was found
	 * @return Returns a string that gives the program a new place to parse through
	 * @throws IOException
	 */
	String progressArrowBr(BufferedReader logbr, String line) throws IOException {
        boolean matchingArrow = false;
        String timeStamp = "";
        String[] temp = line.split(" ");
        for(String word : temp){
            if(word.length() == 19) {
                   timeStamp = word;
                   break;
            }
        }
        String[] words;
        String logLine = logbr.readLine();
        //Save the lines at the beginning of the process
        //in case we need to reset our buffer
        saveCurLines();
        logbr.mark(2500);
        while(!matchingArrow && logLine != null) {
           boolean timeStampFound = false;      
           logParse.linesBefore.push(logLine);
           logParse.updateProgress(logLine);
           words = logLine.split(" ");
           for(String testWord : words) {
        	   //Find the timestamp in the line we are testing
                 if(!timeStampFound && testWord.length() == 19) {
                        timeStampFound = true;
                        //If our timestamps are not equal, we 
                        //don't have a matching arrow
                        if(logParse.timeStampDifference(testWord, timeStamp)){
                        	logbr.reset();
                        	revertLines();
                            return logbr.readLine();
                        }
                 }
                 else if(timeStampFound) {
                	 	//If we've found an ===> and still have a matching
                	 	//timestamp, then we have a matching arrow error
                        if(testWord.equals("===>")) {
                               matchingArrow = true;
                               return logbr.readLine();
                        } 
                 }      
           }
           logLine = logbr.readLine();
        }
        return "";
	}
	
	/**
	 * Progresses the buffered reader if the user has indicated that they do not 
	 * want to include any DEADLOCK errors. If there is a single DEADLOCK (indicated by a time stamp dif) 
	 * then the function will return the line right below the first DEADLOCK found.
	 * Otherwise if there are matching DEADLOCKs, the line returned will be the line
	 * below the matching DEADLOCK.
	 * @param logbr A buffered reader to advance through the file, same br from LogParser
	 * @param line File line where a DEADLOCK was found
	 * @return Returns a string that gives the program a new place to parse through
	 * @throws IOException
	 */
	String progressDeadlockBr(BufferedReader logbr, String line) throws IOException {
        boolean matchingDeadlock = false;
        String timeStamp = "";
        String[] temp = line.split(" ");
        for(String word : temp) {
            if(word.length() == 19) {
                   timeStamp = word;
                   break;
            }
        }
        String[] words;
        String logLine = logbr.readLine();
        //Save the lines at the beginning of the process
        //in case we need to reset our buffer
        saveCurLines();
        logbr.mark(2500);
        while(!matchingDeadlock && logLine != null) {
           logParse.linesBefore.push(logLine);
           boolean timeStampFound = false;            
           logParse.updateProgress(logLine);
           words = logLine.split(" ");
           for(String testWord : words) {
                 if(!timeStampFound && testWord.length() == 19) {
                    timeStampFound = true;
                    //If our timestamps are not equal, we 
                    //don't have a matching deadlock
                    if(logParse.timeStampDifference(testWord, timeStamp)) {
                    	logbr.reset();
                    	revertLines();
                        return logbr.readLine();
                    }
                 }
                 else if(timeStampFound) {
            	 	//If we found deadlock and haven't had different 
            	 	//timestamps, we have a matching deadlock
                    if(testWord.equals("DEADLOCK")) {
                           matchingDeadlock = true;
                           return logbr.readLine();
                    } 
                }      
           }
           logLine = logbr.readLine();
           //logParse.linesBefore.push(logLine);
        }
        return "";
	}
	
	/**
	 * Generates a string that returns the entire deadlock error, 
	 * which includes the first line where a deadlock was found, and then the 
	 * subsequent lines in between the matching deadlock. If the error is a single
	 * error occurrence, then the error message is simply "".
	 * @param logbr Buffered Reader to read through file, same br from LogParser
	 * @param line File line where DEADLOCK first occurred
	 * @return Returns a string with the full DEADLOCK error
	 * @throws IOException
	 */
	String makeDeadlockLine(BufferedReader logbr, String line) throws IOException {
        ArrayList <String> errorLines = new ArrayList<String>();
        boolean matchingDeadlock = false;
        StringBuilder testLine = new StringBuilder();
        StringBuilder fullMsg = new StringBuilder();
        String timeStamp = "";
        errorLines.add(line + " ");
        String[] temp = line.split(" ");
        //We find the timeStamp in the given line
        for(String word : temp){
            if(word.length() == 19) {
                   timeStamp = word;
                   break;
            }
        }
        String[] words;
        String logLine = logbr.readLine();
        while(!matchingDeadlock && logLine != null) {
            logParse.linesBefore.push(logLine);
        	if(orWord.equals("===>") && logLine.contains("===>"))
        	{
        		logParse.addLinesBefore();
        		String tempLine = (makeArrowLine(logbr, logLine));
        		if(tempLine != null) {
        			makeEntry(tempLine);
        		}
        		else
        			logParse.view.linesBeforeArrayList.remove(logParse.view.linesBeforeArrayList.size() - 1);

        	}
           boolean timeStampFound = false;
           boolean uCodeFound = false;              
           testLine.setLength(0);
           logParse.updateProgress(logLine);
           words = logLine.split(" ");
           for(String testWord : words) {
        	   if(!timeStampFound && testWord.length() == 19) {
        		   timeStampFound = true;
                    //If our timestamps are not equal, we 
                    //don't have a matching deadlock and just return first line
                    if(logParse.timeStampDifference(testWord, timeStamp)) {
                           return errorLines.get(0);
                    }
             }
             
             //We find the U-code so that the error message contains only the content,
             //not the timestamp and ucode as well
             else if(!uCodeFound && timeStampFound) {
                    if(testWord.length() > 2) {
                           if(testWord.charAt(0) == 'U' && Character.isDigit(testWord.charAt(1))) {
                                  uCodeFound = true;
                           }
                    }
             }
             else if(uCodeFound && timeStampFound) {
            	 	//We've found a matching deadlock, and can now build the full error msg
                    if(testWord.equals("DEADLOCK")) {
                           matchingDeadlock = true;
                           for(int i = 0; i < errorLines.size(); i++) {
                        	   fullMsg.append(errorLines.get(i)); 
                           }
                           return fullMsg.toString();
                    }
                    //If we haven't found a deadlock, then we keep adding 
                    //onto the error message
                    else
                           testLine.append(testWord + " ");  
             }      
         }
         errorLines.add(testLine.toString());
         logLine = logbr.readLine();
       }
        
       return fullMsg.toString();
	}
	
	/**
	 * Generates the entire line of an arrow error, and includes all the lines
	 * between the first arrow and then the matching arrow error. The string returned
	 * includes the first line where the arrow was found and the full error message, but not
	 * the line including the matching arrow. If there is only a single arrow, then only the
	 * first line is returned.  
	 * @param logbr Buffered Reader to read through file, same br from LogParser
	 * @param logLine File line where an arrow was first found
	 * @return Returns a string with the full arrow error, null if outside
	 * 		   time stamp bounds user chose in the interface
	 * @throws IOException
	 */
	String makeArrowLine(BufferedReader logbr, String logLine) throws IOException {
		String[] words;
		int arrowindex = 0; 
        boolean closingArrowTagFound = false;
        boolean outsideTimeStampBounds = false;
        StringBuilder errorMsg = new StringBuilder();
        String[] currArray = logLine.split(" ");
        if (logLine.contains("Time critical")){
        	//Line is outside requested time bounds
        	if (!logParse.compareTimeStamp(currArray)) {
        		outsideTimeStampBounds = true;
        	}
        }
        //Find index where arrow is 
		for (int i=0; i<currArray.length; i++) {
			if (currArray[i].equals("===>")) {
				arrowindex = i-1;
			}
		}
		String firstLineOfError = errorMsg.toString();
		errorMsg.append(logLine + " ");
		
		logLine = logbr.readLine();
		while (!closingArrowTagFound && logLine != null) {
			logParse.linesBefore.push(logLine);
			logParse.updateProgress(logLine);
			words = logLine.split(" ");
			
			if (logLine.contains("===>")) {
				//A single arrow error, we make a recursive call
				if (logLine.contains("Time critical")) {
					errorMsg.setLength(0);
					errorMsg.append(firstLineOfError);
					return (makeArrowLine(logbr, logLine));
				}
				if (arrowindex < words.length){
					for (int i = arrowindex; i < words.length; i++) {
						errorMsg.append(words[i] + " ");
					}
				}
				//Add to the arrow error message
				else {
					errorMsg.append(logLine + " ");
				}
				closingArrowTagFound = true;
				break;
			}
			if (arrowindex < words.length) {
				for (int i = arrowindex; i < words.length; i++) {
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
		//We return null if not within time bounds
		if (outsideTimeStampBounds) {
			return null;
		}
		return errorMsg.toString();
	}	
	
	void saveCurLines()
	{
		tempLinesBefore.clear();
		for (String str : logParse.linesBefore){
			tempLinesBefore.add(str);
		}
	}
	
	void revertLines()
	{
		for(int i = 0; i < tempLinesBefore.size(); i++)
		{
			logParse.linesBefore.push(tempLinesBefore.get(i));
		}
	}
}