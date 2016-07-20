package interfaceTest;

import java.util.ArrayList;

public class LogicEvaluator {

	private LogParser logParser;
	private String firstKeyword;
	private String Testline;
	private ArrayList<Boolean> hasNot = new ArrayList<Boolean>();
	private ArrayList<String> ORwords = new ArrayList<String>();
	private ArrayList<Integer> badIndexes = new ArrayList<Integer>();
	private ArrayList<String> validLines = new ArrayList<String>();
	private ArrayList<String> keyWords = new ArrayList<String>();
	private ArrayList<String> operands = new ArrayList<String>();
	
	
	LogicEvaluator(LogParser logParser)
	{
		this.logParser = logParser;
	}
	
	//Entry titles will be based off of the first keyword selected
	void makeEntries()
	{
		if(validLines == null)
			return;
		else
		{
			int errorCount = 0;
			for(int i = 0; i < validLines.size(); i++)
			{
				errorCount++;
				Object[] entry = new Object[5];
				boolean timeStampFound = false;
				boolean uCodeFound = true;
				StringBuilder errorMsg = new StringBuilder();
				String[] logWords = validLines.get(i).split(" ");
				for (String testWord : logWords){
					if (testWord.length() == 19 && !timeStampFound){
						entry[0] = errorCount;
						entry[1] = testWord;
						entry[2] = firstKeyword;
						timeStampFound = true;
					}
					else if(!uCodeFound && timeStampFound)
					{
						if(testWord.length() > 2)
						{
							if(testWord.charAt(0) == 'U' && Character.isDigit(testWord.charAt(1)))
							{
								uCodeFound = true;
							}
						}
					}
					if(uCodeFound && timeStampFound)
					{
						errorMsg.append(testWord + " ");	
					}
				}	
				entry[3] = errorMsg.toString();
				entry[4] = logParser.view.solutions.get(entry[2]);
			}
		}
	}
	
	
	//There will be special cases if there is a deadlock or a ==> or a timecritical
	void addLines(String line, String keyWord)
	{
		//If the line contains one of the OR keywords, we count it as valid
		for(int i = 0; i < ORwords.size(); i++)
		{
			if(line.contains(ORwords.get(i)))
			{
				if(ORwords.get(i).equals("DEADLOCK"))
					validLines.add(makeDeadlockLine());
				else if (ORwords.get(i).equals("===>"))
					validLines.add(makeArrowLine());
				else
				//If it contains just one OR word, we only add it once
					validLines.add(line);
				return;
			}
		}
	}
	
	//Add something here for "if not == true" to make things
	void parseLines()
	{
		//If keywords is empty, we don't have any AND operands so we 
		//will leave this function and make entries
		if(keyWords.isEmpty())
			return;
		for(int i = 0; i < validLines.size(); i++)
		{
			boolean isValid = true;
			//If the line doesn't contain all of the words
			//then it is invalid
			for(int j = 0; j < keyWords.size(); j++)
			{
				//If the line contains a word we DON'T want, it is invalid
				if(hasNot.get(j)) {
					if(validLines.get(i).contains(keyWords.get(j)))
						isValid = false;
				}
				else {
					if(!validLines.get(i).contains(keyWords.get(j)))
						isValid = false;
				}
			}
			if(!isValid)
				badIndexes.add(i);
		}
		
		for(int i = 0; i < badIndexes.size(); i++){
			System.out.println(badIndexes.get(i));
		}
		
		System.out.println("before the loop");
		for(int x = badIndexes.size() - 1; x >= 0; x--)
		{
			System.out.println("I am removing a line");
			validLines.remove((int)badIndexes.get(x));
		}
		System.out.println("out of the loop");
	}
	
	void addORs()
	{
		ORwords.add(keyWords.get(0));
		firstKeyword = keyWords.get(0);
		keyWords.remove(0);
		hasNot.remove(0);
		while(operands.contains("OR"))
		{
			int index = operands.indexOf("OR");
			ORwords.add(keyWords.get(index));
			keyWords.remove(index);
			operands.remove(index);
			hasNot.remove(index);
		}
	}

	String makeDeadlockLine()
	{
		return "temp";
	}
	
	String makeArrowLine()
	{
		return "temp";
	}
}







	