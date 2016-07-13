package interfaceTest;

import java.io.FileWriter;
import java.io.IOException;

public class CSVFileWriter {
	private static UserView view;
	
	public CSVFileWriter(UserView view){
		CSVFileWriter.view = view;
	}
	
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
