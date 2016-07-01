/**
 * file: UserView.java
 * 
 */

package interfaceTest;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionEvent;

import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTable;

@SuppressWarnings("serial")

public class UserView extends JFrame{

	private String [] headers = {"Error #", "Timestamp",
								"U Code", "Error Message", "Suggested Solution"};
	protected List<Object[]> errorData = new ArrayList<Object[]>();
	private Object [][] data;
	private int errorCount;
	private ProgressDialog dialog;
	private long progress;
	private Thread t;
	//Holds path of the user given logfile
	protected String logFile;
	//Holds a line when the logfile is read
	protected String logLine;
	//Holds the individual entries from logLine, split by " "
	protected String[] logWords;
	//Holds a line from the csv file
	protected String errorLine;
	//Holds the individual cell entries from errorLine
	protected String [] errorWords;
	//ArrayList to hold all the UCodes from the csv file
	protected List<String> uCodes = new ArrayList<String>();
	
	//Holds the size of the file in bytes
	private long fileSize;
	//Divided by 100 to update the progress bar efficiently
	private long fileSizeDivHundred;
	
	private JTable table;
	private JPanel contentPane;
	private JTextField filePath;
	//User clicks after selecting directory for log file
	protected JButton submitButton;
	//Returns the User back to the Main Menu
	protected JButton backButton;
	private JButton chooseFile;
	private JScrollPane scrollPane;
	private JTextArea textArea;

	/**
	 * Create the frame.
	 * @throws IOException 
	 */
	public UserView(MainMenu menu) throws IOException {
		data = new Object[11][];
		for(int i = 0; i < 11; i ++){
			Object[] temp = new Object[5];
			for(int j = 0; j < 5; j++){
				temp[j] = "";
			}
			data[i] = temp;
		}
		errorCount = 0;
		fillUCodes();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1000, 300);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));
		
		Component horizontalStrut_2 = Box.createHorizontalStrut(20);
		panel_1.add(horizontalStrut_2);
		
		Component horizontalStrut_4 = Box.createHorizontalStrut(20);
		panel_1.add(horizontalStrut_4);
		
		chooseFile = new JButton("Choose File");
		chooseFile.addActionListener(e -> {
		    JFileChooser chooser = new JFileChooser();
		    FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        "Text Files", "txt", "text");
		    chooser.setFileFilter(filter);
		    chooser.setAcceptAllFileFilterUsed(false);
		    int returnVal = chooser.showOpenDialog(getParent());
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		    	 filePath.setText(chooser.getSelectedFile().getAbsolutePath());
		    }
		});
		panel_1.add(chooseFile);
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		panel_1.add(horizontalStrut);
		
		filePath = new JTextField();
		panel_1.add(filePath);
		filePath.setColumns(10);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		panel_1.add(horizontalStrut_1);
		
		submitButton = new JButton("Submit");
		submitButton.setPreferredSize(new Dimension(80, 30));
		submitButton.addActionListener(e -> {
			String path = filePath.getText();
			if (path.equals(""))
				JOptionPane.showMessageDialog(null, "Please enter a path");
			else
				try {
					findLogErrors(filePath.getText());
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "The file cannot be found");
					e1.printStackTrace();
				}
		});
		submitButton.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).
        put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER,0), "Enter pressed");
		submitButton.getActionMap().put("Enter pressed", new AbstractAction()
				{
					public void actionPerformed(ActionEvent e)
					{
						if (filePath.getText().equals(""))
							JOptionPane.showMessageDialog(null, "Please enter a path");
						else
							try {
								findLogErrors(filePath.getText());
							} catch (IOException e1) {
								JOptionPane.showMessageDialog(null, "The file cannot be found");
								e1.printStackTrace();
							}
					}
				});
		panel_1.add(submitButton);
		
		panel_1.add(Box.createRigidArea(new Dimension(10,0)));
		
		backButton = new JButton("Back");
		backButton.setPreferredSize(new Dimension(80, 30));
		backButton.addActionListener(e ->{
			menu.setVisible(true);
			this.setVisible(false);
		});
		panel_1.add(backButton);
		
		
		Component horizontalStrut_5 = Box.createHorizontalStrut(20);
		panel_1.add(horizontalStrut_5);
		
		Component horizontalStrut_3 = Box.createHorizontalStrut(20);
		panel_1.add(horizontalStrut_3);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		Component verticalStrut = Box.createVerticalStrut(20);
		panel.add(verticalStrut, BorderLayout.SOUTH);
	
		DefaultTableModel tableModel = new DefaultTableModel(data, headers);
		
		table = new JTable(data, headers);
		scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel.add(scrollPane, BorderLayout.CENTER);
	}
	
	
	/**
	 * This function fills the ArrayList uCodes by reading in 
	 * the LogErrors_Suggestions.csv file and taking in all of the data
	 * from the UCodes column. This data will eventually be used in the
	 * findLogErrors function. 
	 * @throws IOException
	 */
	void fillUCodes() throws IOException
	{
		
		//InputStream errorInput = getClass().getResourceAsStream(MainController.errorFile);
		//BufferedReader errorbr = new BufferedReader(new InputStreamReader(errorInput));
		
		FileReader errorInput = new FileReader("src/interfaceTest/resources/LogErrors_Suggestions.csv");
		BufferedReader errorbr = new BufferedReader(errorInput);
		
		//Gets to the second line (skips header row of csv)
		errorLine = errorbr.readLine();
		errorLine = errorbr.readLine();
		
		//Filling the uCodes arraylist
		while(errorLine != null)
		{
			errorWords = errorLine.split(",(?=([^\"]|\"[^\"]*\")*$)");
			if(errorWords.length > 2)
			{
				uCodes.add(errorWords[0]);
				errorLine = errorbr.readLine();
			}
			else
				break;
		}
		errorbr.close();
	}
	
	/**
	 * This function takes in the logfile given by the user and parses through it 
	 * to find errors by comparing the UCodes in the logFile against the error
	 * UCodes from LogErrors_Suggestions.csv. If UCodes match, then the timestamp,
	 * Ucode, corresponding error message, and suggested solution are displayed on the screen. 
	 * @param path - A filepath from the user 
	 * @throws IOException
	 */
	void findLogErrors(String path) throws IOException
	{
		logFile = path;
		File file = new File(path);
		if (!file.exists()){
			JOptionPane.showMessageDialog(null, "The file cannot be found");
			return;
		}
		try {
			dialog = new ProgressDialog(file, this);
			dialog.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			};	
			t = new Thread(new Runnable()
			{

				public void run() {
					try {
						fileSize = file.length();
						fileSizeDivHundred = fileSize/100;
						System.out.println(fileSize);
						System.out.println(fileSizeDivHundred);
						
						submitButton.setEnabled(false);
						parseErrors(file, dialog);
						} catch (IOException e) {
						// TODO Auto-generated catch block
							e.printStackTrace();
							}
								
						}
						
					}
				);
				
		t.start();
	}
	
	void parseErrors(File file, ProgressDialog pd) throws IOException
	{
		int percent = 0;
		int oldPercent = 0;
		
		errorData.clear();
		errorCount = 0;
		progress = 0;
		FileReader logInput = new FileReader(logFile);
		BufferedReader logbr = new BufferedReader(logInput);

		logLine = logbr.readLine();
		
		//Timer for performance testing
		long startTime = System.nanoTime();
		
		while(logLine != null)
		{
			progress += logLine.length();
			
			percent = (int) (progress / fileSizeDivHundred);
			if (percent > oldPercent){
				dialog.updateProgress(percent);
				oldPercent = percent;
			}

			logWords = logLine.split(" ");
			
			String uCode = null;
			String timeStamp = null;
			String errorMessage = "";
			
			boolean uCodeFound = false;
			boolean timeStampFound = false;
			
			for (String s : logWords){
				if (s.length() > 1){
					if (uCodeFound){
						errorMessage += (s + " ");
					}
					if (s.charAt(0) == 'U' && !uCodeFound){
						
						if (Character.isDigit(s.charAt(1))){
							uCode = s;
							uCodeFound = true;
						}
					}
				}
				if (s.length() == 19 && !timeStampFound){
					timeStamp = s;
					timeStampFound = true;
				}
			}
			
			//Testing the UCode from the file against the error UCodes
			for(int i = 0; i < uCodes.size(); i++)
			{
				if (uCode == null) 
					break;

				else if (uCode.equals(uCodes.get(i)))
				{
					errorCount++;
					Object[] entry = new Object[5];
					entry[0] = errorCount;
					entry[1] = timeStamp;
					entry[2] = uCode;
				
					//Making new readers to go back to top of file
					InputStream errorInput = getClass().getResourceAsStream(MainController.errorFile);
					BufferedReader newbr = new BufferedReader(new InputStreamReader(errorInput));
					newbr.readLine();
				
					//We read a new line until we get to the corresponding line
					for(int j = -1; j < i; j++)
					{
						errorLine = newbr.readLine();
					}
				
					errorWords = errorLine.split(",(?=([^\"]|\"[^\"]*\")*$)");
				
					entry[3] = errorMessage;
					entry[4] = errorWords[2];
					errorData.add(entry);
					newbr.close();
				}

			}
			logLine = logbr.readLine();
		}
		
		//Logs execution time to the console
		long endTime = System.nanoTime();
		long duration = (endTime - startTime)/1000000;
		System.out.println("Operation completed in " + duration + " ms");
		
		dialog.doneParse(errorCount);
		logbr.close();
		data = new Object[errorData.size()][];
		
		for(int i = 0; i < errorData.size(); i++)
		{
			data[i] = errorData.get(i);
		}
		makeTable();
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
		
		table = new JTable(tableModel){
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
		
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scrollPane.setViewportView(table);
	}
}
	
