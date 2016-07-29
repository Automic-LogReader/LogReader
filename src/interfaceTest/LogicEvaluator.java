package interfaceTest;


import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class LogicEvaluator {

	private LogParser logParse;
	private String firstKeyword;
	private ArrayList<Boolean> hasNot = new ArrayList<Boolean>();
	private ArrayList<String> ORwords = new ArrayList<String>();
	private ArrayList<String> validLines = new ArrayList<String>();
	private ArrayList<String> keyWords = new ArrayList<String>();
	private ArrayList<String> operands = new ArrayList<String>();
	private int errorCount;
	private int noArrow;
	private boolean AND_NOT_DEADLOCK;
	private boolean DEADLOCK_occurence;
	
	LogicEvaluator(LogParser logParse){
		this.logParse = logParse;
		AND_NOT_DEADLOCK = false;
	}
	
	void setkeyWords(ArrayList <String> keyWords){
		this.keyWords = keyWords;
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
		if(validLines == null)
			return;
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
		String newLine;
		if(AND_NOT_DEADLOCK) {
			if(line.contains("DEADLOCK")) 
				newLine = progressDeadlockBr(br, line);
			else 
				newLine = line;
			for(int i = 0; i < ORwords.size(); i++) {
				 if(newLine.contains(ORwords.get(i))) {
					validLines.add(newLine);
					return;
				}
			}
		}
		else if (ORwords.contains("DEADLOCK") && line.contains("DEADLOCK"))
			validLines.add(makeDeadlockLine(br, line)); 
		else if (ORwords.contains("===>") && line.contains("===>"))
		{
			if(makeArrowLine(br, line, line.split(" ")) == null)
			{
				return;
			}
			else
			{
				validLines.add(makeArrowLine(br, line, line.split(" ")));
			}
		}
		
		//If the line contains one of the OR keywords, we count it as valid
		else {
			for(int i = 0; i < ORwords.size(); i++) {
				 if(line.contains(ORwords.get(i))) {
					validLines.add(line);
					return;
				}
			}
		}
	}
	
	//Add something here for "if not == true" to make things
	void parseLines() {
		//If keywords is empty, we don't have any AND operands so we 
		//will leave this function and make entries
		System.out.println(noArrow);
		if(keyWords.isEmpty() || keyWords == null)
			return;
		for(int i = validLines.size() - 1; i >= 0; i--) {
			boolean isValid = true;
			//If the line doesn't contain all of the words
			//then it is invalid
			for(int j = 0; j < keyWords.size(); j++) {
				//If the line contains a word we DON'T want, it is invalid
				if(hasNot.get(j)) {
					if(validLines.get(i).contains(keyWords.get(j))) {
						isValid = false;
						break;
					}
				}
				else {
					if(!validLines.get(i).contains(keyWords.get(j))) {
						isValid = false;
						break;
					}
				}
			}
			if(!isValid)
				validLines.remove(i);
		}

	}
	
	void addORs()
	{
		if(keyWords != null){
			firstKeyword = keyWords.get(0);
			System.out.println(firstKeyword);
			if(keyWords.contains("DEADLOCK") && (firstKeyword != "DEADLOCK")) {
				System.out.println("Except I'm here");
				if	(hasNot.get(keyWords.indexOf("DEADLOCK")) != true) {
					keyWords.set(keyWords.indexOf("DEADLOCK"), keyWords.get(0));
					keyWords.set(0, "DEADLOCK");
					ORwords.add(keyWords.get(0));
					keyWords.remove(0); 
					hasNot.remove(0);
					DEADLOCK_occurence = true;
					System.out.println("what the fuck");
				}
				else
					AND_NOT_DEADLOCK = true;
			}
			if(operands.contains("OR")) {
				while(operands.contains("OR")) {
					int index = operands.indexOf("OR");
					operands.remove(index);
					if(!DEADLOCK_occurence)
						index++;
					ORwords.add(keyWords.get(index));
					keyWords.remove(index);
					hasNot.remove(index);
					System.out.println("I am here");
				}
			}
			else if((!DEADLOCK_occurence || AND_NOT_DEADLOCK)) {
				System.out.println("Am I here?");
				ORwords.add(keyWords.get(0));
				keyWords.remove(0); 
				hasNot.remove(0);
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
        
		System.out.println("HERE");
		System.out.println(errorMsg.toString());
		return errorMsg.toString();
	
	}
	
}







	