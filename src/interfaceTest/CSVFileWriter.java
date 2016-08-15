/**
 * @file CSVFileWriter.java
 * @authors Leah Talkov, Jerry Tsui
 * @date 8/15/2016
 * Takes the generated results from a search in LogParser
 * and writes the contents to a CSV file.
 */

package interfaceTest;

import java.io.FileWriter;
import java.io.IOException;

public class CSVFileWriter {
	private static UserView view;
	

	/**
	 * Has functionality that writes the parsing results to a CSV file
	 * @param view UserView object associated with this class
	 */
	public CSVFileWriter(UserView view){
		CSVFileWriter.view = view;
	}
	
	/**
	 * Takes the contents from LogParser and makes the content CSV ready by
	 * adding commas and "\n" where necessary so that the data is formatted
	 * the same as seen in the interface table. 
	 * @param fileName The file to write to, default name is Error_Log_DATE_TIME
	 * @throws IOException If there is a problem writing to the file
	 */
	public void writeTo(String fileName) throws IOException{
		FileWriter writer = new FileWriter(fileName);
		for (int i = 0; i< view.headers.length; i++){
			writer.append(view.headers[i]);
			writer.append(',');
		}
		writer.append('\n');
		for (int i = 0; i < view.logParser.errorData.size(); i++){
			String myLine = "";
			int errorCount = (int)view.logParser.errorData.get(i)[0];
			myLine += errorCount + ",";
			String timeStamp = (String)view.logParser.errorData.get(i)[1] + ",";
			myLine += timeStamp;
			String keyWord = (String) view.logParser.errorData.get(i)[2];
			//We put double quotes around commas so that the content of
			//one cell doesn't improperly get split into two cells
			if(keyWord.contains(","))
				keyWord = "\"" + keyWord + "\"";
			myLine += (keyWord + ",");
			String errorMsg = (String) view.logParser.errorData.get(i)[3];
			if(errorMsg.contains(","))
				errorMsg = "\"" + errorMsg + "\"";
			myLine += (errorMsg + ",");
			String solutionMsg = (String) view.logParser.errorData.get(i)[4];
			if(solutionMsg.contains(","))
				solutionMsg = "\"" + solutionMsg + "\"";
			myLine += solutionMsg + "\r\n";
			writer.write(myLine);
		}
		
		writer.flush();
		writer.close();
	}
}
