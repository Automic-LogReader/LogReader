/**
 * file: UserView.java
 */

package interfaceTest;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import interfaceTest.CheckBoxList.CheckBoxListItem;
import interfaceTest.CheckBoxList.CheckBoxListRenderer;

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JList;

@SuppressWarnings("serial")

public class UserView extends JFrame{

	protected String [] headers = {"Error #", "Timestamp",
								"Keywords", "Error Message", "Suggested Solution"};
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
	private Object[] entry;
	private HashSet<String> keyWords = new HashSet<String>();
	private HashSet<String> originalKeyWords;
	private boolean hasCopiedOriginalKeyWords;
	//Holds the size of the file in bytes
	private long fileSize;
	//Divided by 100 to update the progress bar efficiently
	private long fileSizeDivHundred;
	
	private JTable errorTable;
	private JPanel contentPane;
	private JTextField filePath;
	//User clicks after selecting directory for log file
	protected JButton submitButton;
	//Returns the User back to the Main Menu
	protected JButton backButton;
	private JButton chooseFile;
	private JScrollPane errorScrollPane;
	private JTextArea textArea;
	private AdminView admin;

	private CheckBoxListItem[] listOfKeyWords;
	private int numKeyWords;
	/**
	 * Create the frame.
	 * @throws IOException 
	 */
	public UserView(MainMenu menu, boolean isAdmin) throws IOException {
		hasCopiedOriginalKeyWords = false;
		data = new Object[11][];
		for(int i = 0; i < 11; i ++){
			Object[] temp = new Object[5];
			for(int j = 0; j < 5; j++){
				temp[j] = "";
			}
			data[i] = temp;
		}
		errorCount = 0;
		fillKeywords();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1200, 280);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel rightMainPanel = new JPanel();
		contentPane.add(rightMainPanel, BorderLayout.SOUTH);
		rightMainPanel.setLayout(new BoxLayout(rightMainPanel, BoxLayout.X_AXIS));
		
		Component horizontalStrut_2 = Box.createHorizontalStrut(20);
		rightMainPanel.add(horizontalStrut_2);
		
		Component horizontalStrut_4 = Box.createHorizontalStrut(20);
		rightMainPanel.add(horizontalStrut_4);
		
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
		rightMainPanel.add(chooseFile);
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		rightMainPanel.add(horizontalStrut);
		
		filePath = new JTextField();
		rightMainPanel.add(filePath);
		filePath.setColumns(10);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		rightMainPanel.add(horizontalStrut_1);
		
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
		rightMainPanel.add(submitButton);
		
		rightMainPanel.add(Box.createRigidArea(new Dimension(10,0)));
		
		backButton = new JButton("Back");
		backButton.setPreferredSize(new Dimension(80, 30));
		backButton.addActionListener(e ->{
			menu.setVisible(true);
			this.setVisible(false);
		});
		rightMainPanel.add(backButton);
		
		rightMainPanel.add(Box.createRigidArea(new Dimension(10,0)));
		
		
		JButton editButton = new JButton("Edit Entries");
		editButton.setPreferredSize(new Dimension(130, 30));
		editButton.addActionListener(e -> {
			try {
				admin = new AdminView();
				admin.setVisible(true);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		});
		rightMainPanel.add(editButton);
		
		if(isAdmin){
			editButton.setVisible(true);
			setTitle("Administrator View");
		} else {
			editButton.setVisible(false);
			setTitle("User View");
		}
		
		
		Component horizontalStrut_5 = Box.createHorizontalStrut(20);
		rightMainPanel.add(horizontalStrut_5);
		
		Component horizontalStrut_3 = Box.createHorizontalStrut(20);
		rightMainPanel.add(horizontalStrut_3);
		
		JPanel mainPanel = new JPanel();
		contentPane.add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
	
		DefaultTableModel tableModel = new DefaultTableModel(data, headers);
		
		JList<CheckBoxListItem> list = new JList<CheckBoxListItem>(listOfKeyWords);
		list.setCellRenderer(new CheckBoxListRenderer());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addMouseListener(new MouseAdapter(){
			 public void mouseClicked(MouseEvent event) {
		            @SuppressWarnings("unchecked")
					JList<CheckBoxListItem> list =
		               (JList<CheckBoxListItem>) event.getSource();
		            // Get index of item clicked
		            int index = list.locationToIndex(event.getPoint());
		            CheckBoxListItem item = (CheckBoxListItem) list.getModel()
		                  .getElementAt(index);
		            // Toggle selected state
		            item.setSelected(!item.isSelected());
		            // Repaint cell
		            list.repaint(list.getCellBounds(index, index));
		         }
		});
		JScrollPane keyWordScrollPane = new JScrollPane(list);
		mainPanel.add(keyWordScrollPane);
		
		errorTable = new JTable(data, headers);
		errorScrollPane = new JScrollPane(errorTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		mainPanel.add(errorScrollPane);
		
		setVisible(true);
	}
	
	
	/**
	 * This function fills the ArrayList uCodes by reading in 
	 * the LogErrors_Suggestions.csv file and taking in all of the data
	 * from the UCodes column. This data will eventually be used in the
	 * findLogErrors function. 
	 * @throws IOException
	 */
	void fillKeywords() throws IOException
	{
		
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
				keyWords.add(errorWords[0]);
				errorLine = errorbr.readLine();
			}
			else
				break;
		}
		errorbr.close();
		
		numKeyWords = keyWords.size();
		listOfKeyWords = new CheckBoxListItem[numKeyWords + 1];
		listOfKeyWords[0] = new CheckBoxListItem("All KeyWords");
		//All Keywords selected by default
		listOfKeyWords[0].setSelected(true);
		
		
		int index = 1;
		for (String s : keyWords){
			listOfKeyWords[index] = new CheckBoxListItem(s);
			index++;
		}
		
		if (!hasCopiedOriginalKeyWords){
			originalKeyWords = new HashSet<String>(keyWords);
			hasCopiedOriginalKeyWords = true;
		}
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
			if (noCheckBoxSelected()) return;
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
		updateKeyWords();
		
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
			entry = null;
			
			boolean keywordFound = false;
			boolean timeStampFound = false;
			
			for (String testWord : logWords){
				//Timestamp will always come first
				//Is this a reliable way to find timestamp?
				//Maybe change to regex
				if (testWord.length() == 19 && !timeStampFound){
					timeStamp = testWord;
					timeStampFound = true;
				}
				if(timeStampFound && !keywordFound)
				{
					//Testing the UCode from the file against the error UCodes
					if (keyWords.contains(testWord))
					{
						keywordFound = true;
						errorCount++;
						entry = new Object[5];
						entry[0] = errorCount;
						entry[1] = timeStamp;
						entry[2] = testWord;
						
						//Making new readers to go back to top of file
						FileReader errorInput = new FileReader("src/interfaceTest/resources/LogErrors_Suggestions.csv");
						BufferedReader newbr = new BufferedReader(errorInput);
						newbr.readLine();
						errorLine = newbr.readLine();
						
						//We read a new line until we get to the corresponding line
						while(errorLine != null)
						{
					
							errorWords = errorLine.split(",(?=([^\"]|\"[^\"]*\")*$)");
							if(testWord.equals(errorWords[0]))
							{
								entry[4] = errorWords[2];
								break;
							}
							errorLine = newbr.readLine();
						}
						newbr.close();
					}
				}
				
				else if(timeStampFound && keywordFound)
				{
					errorMessage += (testWord + " ");
					entry[3] = errorMessage;
				}
			}
			if(entry != null)
			{
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
		
		errorTable = new JTable(tableModel){
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
		
		errorTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		errorScrollPane.setViewportView(errorTable);
	}
	
	void updateKeyWords(){
		if (listOfKeyWords[0].isSelected()){
			System.out.println("default selection");
			keyWords.addAll(originalKeyWords);
			return;
		}
		else {
			keyWords.clear();
			for (int i = 1; i <= numKeyWords; i++){
				if (listOfKeyWords[i].isSelected()){
					System.out.println("Added:" + listOfKeyWords[i].toString());
					keyWords.add(listOfKeyWords[i].toString());
				}
			}
		}
	}
	
	boolean noCheckBoxSelected(){
		for (int i = 0; i <= numKeyWords; i++){
			if (listOfKeyWords[i].isSelected()){
				return false;
			}
		}
		JOptionPane.showMessageDialog(null, "Please select a checkbox");
		return true;
	}
}
	

