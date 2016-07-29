package interfaceTest;


import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class LogicEvaluator {

	private LogParser logParse;
	private String firstKeyword;
	private ArrayList<Boolean> hasNot = new ArrayList<Boolean>();
	private ArrayList <String> deadlockOrLines = new ArrayList<String>();
	private ArrayList<String> validLines = new ArrayList<String>();
	private ArrayList<String> andWords = new ArrayList<String>();
	private ArrayList<String> operands = new ArrayList<String>();
	private int errorCount;
	private int noArrow;
	private String orWord;
	private boolean OR_FLAG;
	private boolean OR_DEADLOCK;
	private boolean AND_NOT_DEADLOCK;
	private boolean AND_DEADLOCK;
	
	LogicEvaluator(LogParser logParse){
		this.logParse = logParse;
		orWord = "";
		OR_FLAG = false;
		OR_DEADLOCK = false;
		AND_NOT_DEADLOCK = false;
		AND_DEADLOCK = false;
	}
	
	void setkeyWords(ArrayList <String> keyWords){
		this.andWords = keyWords;
	}
	
	void setOperands(ArrayList <String> operands){
		this.operands = operands;
	}
	
	void setHasNot(ArrayList <Boolean> hasNot){
		this.hasNot = hasNot;
	}
	
	int getErrorCount(){
		return errorCount;
	}
	
	
	//Entry titles will be based off of the first keyword selected
	void makeEntries() {
		if(validLines.isEmpty()){
			return;
		}
		else {
			errorCount = 0;
			for(int i = 0; i < validLines.size(); i++) {
				errorCount++;
				Object[] entry = new Object[5];
				boolean timeStampFound = false;
				boolean uCodeFound = false;
				boolean hitDeadLock = false;
				boolean hitArrow = false;
				StringBuilder errorMsg = new StringBuilder();
				String[] logWords = validLines.get(i).split(" ");
				//*************************
				//SOURCE OF THE PROBLEM HERE (not finding timestamp in ===>?)
				//*************************
				//System.out.println(validLines.get(i));
				for (String testWord : logWords){
					if (testWord.length() == 19 && !timeStampFound){
						entry[0] = errorCount;
						entry[1] = testWord;
						entry[2] = firstKeyword;
						timeStampFound = true;
					}
					else if(!uCodeFound && timeStampFound) {
						if(testWord.length() > 2) {
							if(testWord.charAt(0) == 'U' && Character.isDigit(testWord.charAt(1))) {
								uCodeFound = true;
							}
						}
					}
					else if(uCodeFound && timeStampFound) {
						if(validLines.get(i).contains("DEADLOCK")) {
							//System.out.println("I found a deadlock");
							if(testWord.equals("DEADLOCK")) {
								hitDeadLock = true;
								continue;
							}
							if(hitDeadLock) {
								errorMsg.append(testWord + " ");
							}
							//System.out.println("I passed all the ifs");
						}
						else if(validLines.get(i).contains("===>")){
							if(testWord.equals("===>")) {
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
				entry[4] = logParse.view.solutions.get(entry[2]);
				logParse.errorData.add(entry);
			}
		}
	}
	
	//There will be special cases if there is a deadlock or a ==> or a timecritical
	void addLines(String line, BufferedReader br) throws IOException {
		String testLine = line;
		//If they don't want deadlock lines, then we skip over them
		if(AND_NOT_DEADLOCK) {
			if(line.contains("DEADLOCK")) 
				//The line returned is the line after the matching (or single) deadlock
				testLine = progressDeadlockBr(br, line);
		}
		//If orWord = "", then no OR operand was set. Otherwise
		//if the line contains the orWord, it is valid
		if(testLine.contains(orWord) && (orWord != "")) {
			if(OR_DEADLOCK) {
				//This is the arraylist that has the deadlock lines (check duplicates on)
				deadlockOrLines.add(makeDeadlockLine(br, testLine));
			}
			else
				validLines.add(testLine);
		}	
		else {
			String parseLine;
			if(AND_DEADLOCK && testLine.contains("DEADLOCK"))
			{
				parseLine = makeDeadlockLine(br, testLine);
			}
			else{
				parseLine = testLine;
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
			validLines.add(parseLine);
		}

			
	}
		
	void addORs()
	{
		if(andWords != null){
			firstKeyword = andWords.get(0);
			if(operands.contains("OR")){
				OR_FLAG = true;
				orWord = andWords.get(2);
				andWords.remove(2);
				if(orWord.equals("DEADLOCK"))
					OR_DEADLOCK = true;
				
			}
			if(andWords.contains("DEADLOCK") && (orWord != "DEADLOCK")) {
				if	(hasNot.get(andWords.indexOf("DEADLOCK")) != true) {
					AND_DEADLOCK = true;
				}
				else
					AND_NOT_DEADLOCK = true;
			}
		}
	}

	String progressDeadlockBr(BufferedReader br, String line) throws IOException
	{
		boolean timeStampFound = false;
		String timeStamp = "";
		String[] temp = line.split(" ");
		String words [];
	    for(String word : temp) {
	        if(word.length() == 19) {
	               timeStamp = word;
	               break;
	        }
	    }
		String logLine = br.readLine();
		br.mark(1500);
		while (logLine != null) {
			logParse.updateProgress();
			words = logLine.split(" ");
			for(String testWord : words) {
				//If true, we've found the timestamp on the test line
				if(testWord.length() == 19 && !timeStampFound) {
					timeStampFound = true;
					if(logParse.timeStampDifference(testWord, timeStamp)) {
						//if single, we reset the buffered reader
						br.reset();
						return br.readLine();
					}
				}
					//We test the timestamps to see if this is a single deadlock
				else if(timeStampFound) {
						//If we've found a matching deadlock, 
						//we advance the reader and return
						if(logLine.contains("DEADLOCK")) {
							return br.readLine();
						
						}
						continue;
				}
			}
			return br.readLine();
		}
		return br.readLine();
	}
	
	String makeDeadlockLine(BufferedReader logbr, String line) throws IOException
	{
        ArrayList <String> errorLines = new ArrayList<String>();
        boolean matchingDeadlock = false;
        StringBuilder testLine = new StringBuilder();
        StringBuilder fullMsg = new StringBuilder();
        String timeStamp = "";
        errorLines.add(line + " ");
        String[] temp = line.split(" ");
        for(String word : temp){
            if(word.length() == 19){
                   timeStamp = word;
                   break;
            }
        }
        String[] words;
        String logLine = logbr.readLine();
        while(!matchingDeadlock && logLine != null){
               boolean timeStampFound = false;
               boolean uCodeFound = false;              
               testLine.setLength(0);
               logParse.updateProgress();
               words = logLine.split(" ");
               for(String testWord : words){
                     if(!timeStampFound && testWord.length() == 19){
                            timeStampFound = true;
                            //If our timestamps are not equal, we 
                            //don't have a matching deadlock
                            if(logParse.timeStampDifference(testWord, timeStamp)){
                                   return errorLines.get(0);
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
                                	   fullMsg.append(errorLines.get(i));
                                   }
                                   return fullMsg.toString();
                            }
                            else
                                   testLine.append(testWord + " ");  
                     }      
               }
               errorLines.add(testLine.toString());
               logLine = logbr.readLine();
        }
        
        return fullMsg.toString();
	}
	
	
	String makeArrowLine(BufferedReader logbr, String logLine, String[] currArray) throws IOException
	{
		String[] words;
		int arrowindex = 0;
		boolean timeStampFound = false;
        boolean uCodeFound = false;  
        boolean closingArrowTagFound = false;
        boolean outsideTimeStampBounds = false;
        StringBuilder errorMsg = new StringBuilder();
        System.out.println(logLine);
        //if (!logParse.compareTimeStamp(currArray)){
			//outsideTimeStampBounds = true;
        //}
		for (int i=0; i<currArray.length; i++){
			if (currArray[i].equals("===>")){
				arrowindex = i-1;
			}
		}
		String firstLineOfError = errorMsg.toString();
		errorMsg.append(logLine);
		
		logLine = logbr.readLine();
		while (!closingArrowTagFound && logLine != null){
			logParse.updateProgress();
			timeStampFound = false;
			uCodeFound = false;
			words = logLine.split(" ");
			
			if (logLine.contains("===>")){
				if (logLine.contains("Time critical")){
					//System.out.println(logLine);
					errorMsg.setLength(0);
					errorMsg.append(firstLineOfError);
					String[] tempArray = logLine.split(" ");
					String tempTimeStamp = "";
					for (String s : tempArray){
						if (s.length() == 19){
							tempTimeStamp = s;
							break;
						}
					}
					validLines.add(makeArrowLine(logbr, tempTimeStamp, tempArray));
					return errorMsg.toString();
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
		if (outsideTimeStampBounds){
			return null;
		}
		return errorMsg.toString();
	}	
}