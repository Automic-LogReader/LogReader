/**
 * file: UserView.java
 */

package interfaceTest;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import interfaceTest.CheckBoxList.CheckBoxListItem;
import interfaceTest.CheckBoxList.CheckBoxListRenderer;

import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JList;

@SuppressWarnings("serial")

public class UserView extends JFrame{

	protected String [] headers = {"Error #", "Timestamp",
								"Keywords", "Error Message", "Suggested Solution"};
	protected List<Object[]> errorData = new ArrayList<Object[]>();
	private Object [][] data;
	protected ProgressDialog dialog;
	private Thread t;
	//Holds path of the user given logfile
	protected String logFile;
	//Holds a line when the logfile is read
	protected String logLine;
	//Holds the individual entries from logLine, split by " "
	protected String[] logWords;
	//Holds a line from the error suggestions csv file
	protected String errorLine;
	//Holds the individual cell entries from errorLine
	protected String [] errorWords;
	protected  HashMap<String, String> solutions = new HashMap<String, String>();
	protected HashSet<String> keyWords = new HashSet<String>();
	protected HashSet<String> originalKeyWords;
	private boolean hasCopiedOriginalKeyWords;
	
	//Holds the size of the file in bytes
	private long fileSize;
	//Divided by 100 to update the progress bar efficiently
	protected long fileSizeDivHundred;
	
	protected JTable errorTable;
	private JPanel contentPane;
	private JTextField filePath;
	//User clicks after selecting directory for log file
	protected JButton submitButton;
	//Returns the User back to the Main Menu
	protected JButton backButton;
	private JButton chooseFile;
	private JButton preferenceButton;
	protected JScrollPane errorScrollPane;
	private AdminView admin;

	private CheckBoxListItem[] listOfKeyWords;
	private int numKeyWords;
	
	protected LogParser logParser;
	//Bounds for time-critical DB Calls
	protected Double lowerBound;
	protected Double upperBound;
	
	protected JList<CheckBoxListItem> list;
	
	protected HashSet<HashSet<String>> keyWordGroups = new HashSet<HashSet<String>>();
	
	protected DefaultListModel<CheckBoxListItem> model;
	
	protected JScrollPane groupScrollPane;
	
	protected JList<CheckBoxListItem> groupList;
	protected CheckBoxListItem[] listOfGroups;
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
		lowerBound = (double) 0;
		upperBound = Double.MAX_VALUE;
		fillKeywords();
		createErrorDictionary();
		prepareGUI(menu, isAdmin);
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
				if (errorWords[0].equals("[===>]")){
					keyWords.add("===>");
				}
				else {
					keyWords.add(errorWords[0]);
				}
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
			logParser = new LogParser(this);
			} catch (Exception e) {
				e.printStackTrace();
			};	
			t = new Thread(new Runnable()
			{
				public void run() {
					try {
						fileSize = file.length();
						fileSizeDivHundred = fileSize/100;
						
						submitButton.setEnabled(false);
						logParser.parseErrors(file, dialog);
						//parseErrors(file, dialog);
						} catch (IOException e) {
						// TODO Auto-generated catch block
							e.printStackTrace();
							}
						}			
					}
				);
		t.start();
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
	
	//Maps the key words to solution messages
	void createErrorDictionary() throws IOException{
		FileReader errorInput = new FileReader("src/interfaceTest/resources/LogErrors_Suggestions.csv");
		BufferedReader newbr = new BufferedReader(errorInput);
		newbr.readLine();
		errorLine = newbr.readLine();
			
			while(errorLine != null)
			{
				errorWords = errorLine.split(",(?=([^\"]|\"[^\"]*\")*$)");
				if (errorWords[0].equals("[===>]")){
					solutions.put("===>", errorWords[2]);
				}
				solutions.put(errorWords[0], errorWords[2]);
				errorLine = newbr.readLine();
			}
			newbr.close();
	}
	
	void prepareGUI(MainMenu menu, boolean isAdmin){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1200, 280);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel bottomPanel = new JPanel();
		contentPane.add(bottomPanel, BorderLayout.SOUTH);
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
		
		Component horizontalStrut_2 = Box.createHorizontalStrut(20);
		bottomPanel.add(horizontalStrut_2);
		
		Component horizontalStrut_4 = Box.createHorizontalStrut(20);
		bottomPanel.add(horizontalStrut_4);
		
		preferenceButton = new JButton("Preferences");
		preferenceButton.addActionListener(e -> {
			@SuppressWarnings("unused")
			PreferenceEditor preferenceEditor = new PreferenceEditor(this);
		});
		preferenceButton.setToolTipText("Groupings, Time Critical DB Call Intervals ...");
		bottomPanel.add(preferenceButton);
		
		bottomPanel.add(Box.createRigidArea(new Dimension(10,0)));
		
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
		bottomPanel.add(chooseFile);
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		bottomPanel.add(horizontalStrut);
		
		filePath = new JTextField();
		bottomPanel.add(filePath);
		filePath.setColumns(10);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		bottomPanel.add(horizontalStrut_1);
		
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
		bottomPanel.add(submitButton);
		
		bottomPanel.add(Box.createRigidArea(new Dimension(10,0)));
		
		backButton = new JButton("Back");
		backButton.setPreferredSize(new Dimension(80, 30));
		backButton.addActionListener(e ->{
			menu.setVisible(true);
			this.setVisible(false);
		});
		bottomPanel.add(backButton);
		
		bottomPanel.add(Box.createRigidArea(new Dimension(10,0)));
		
		
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
		bottomPanel.add(editButton);
		
		if(isAdmin){
			editButton.setVisible(true);
			setTitle("Administrator View");
		} else {
			editButton.setVisible(false);
			setTitle("User View");
		}
		
		Component horizontalStrut_5 = Box.createHorizontalStrut(20);
		bottomPanel.add(horizontalStrut_5);
		
		Component horizontalStrut_3 = Box.createHorizontalStrut(20);
		bottomPanel.add(horizontalStrut_3);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		JPanel mainPanel = new JPanel();
		contentPane.add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
	
		/***** Panel holding the keyword selectors *****/
		JPanel keyWordPanel = new JPanel();
		keyWordPanel.setLayout(new BoxLayout(keyWordPanel, BoxLayout.Y_AXIS));
		
		new DefaultTableModel(data, headers);
		
		list = new JList<CheckBoxListItem>(listOfKeyWords);
		list.setCellRenderer(new CheckBoxListRenderer());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addMouseListener(new MouseAdapter(){
			 public void mouseClicked(MouseEvent event) {
		            @SuppressWarnings("unchecked")
					JList<CheckBoxListItem> list =
		               (JList<CheckBoxListItem>) event.getSource();
		            int index = list.locationToIndex(event.getPoint());
		            CheckBoxListItem item = (CheckBoxListItem) list.getModel()
		                  .getElementAt(index);
		            item.setSelected(!item.isSelected());
		            list.repaint(list.getCellBounds(index, index));
		         }
		});
		JScrollPane keyWordScrollPane = new JScrollPane(list);
		keyWordScrollPane.setMinimumSize(new Dimension(50, 100));
		keyWordPanel.add(keyWordScrollPane);
		
		JPanel groupPanel = new JPanel();
		groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.Y_AXIS));
		
		/***** Panel holding the group selectors *****/
		model = new DefaultListModel<CheckBoxListItem>();
		createGroupDisplay();
		groupList = new JList<CheckBoxListItem>(model);
		groupList.setCellRenderer(new CheckBoxListRenderer());
		groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		groupList.addMouseListener(new MouseAdapter(){
			 public void mouseClicked(MouseEvent event) {
		            @SuppressWarnings("unchecked")
					JList<CheckBoxListItem> list =
		               (JList<CheckBoxListItem>) event.getSource();
		            int index = list.locationToIndex(event.getPoint());
		            CheckBoxListItem item = (CheckBoxListItem) list.getModel()
		                  .getElementAt(index);
		            item.setSelected(!item.isSelected());
		            list.repaint(list.getCellBounds(index, index));
		         }
		});
		groupScrollPane = new JScrollPane(groupList);
		
		groupPanel.add(groupScrollPane);
		
		tabbedPane.addTab("Keyword Checkbox", keyWordPanel);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		
		tabbedPane.addTab("Group Checkbox", groupPanel);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		
		mainPanel.add(tabbedPane);
		
		errorTable = new JTable(data, headers);
		errorScrollPane = new JScrollPane(errorTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		//errorScrollPane.setMinimumSize(new Dimension(500, 280));
		mainPanel.add(errorScrollPane);
		
		setVisible(true);
	}
	
	boolean createGroupDisplay(){
		model.clear();
		if (keyWordGroups.isEmpty()){
			return false;
		}
		int index = 0;
		for (HashSet<String> list : keyWordGroups){
	    	StringBuilder listItem = new StringBuilder();
	    	listItem.setLength(0);
	    	for (String s : list){
	    		listItem.append(s + " ");
	    	}
	    	listOfGroups = new CheckBoxListItem[keyWordGroups.size()];
	    	listOfGroups[index] = new CheckBoxListItem(listItem.toString());
	    	model.addElement(listOfGroups[index]);
	    	++index;
	    }
		return true;
	}
	
	/******* CODE FOR THE DATABASE ******/
	/*
	 * public UserView(MainMenu menu, boolean isAdmin) throws ClassNotFoundException, SQLException {
		hasCopiedOriginalKeyWords = false;
		data = new Object[11][];
		for(int i = 0; i < 11; i ++){
			Object[] temp = new Object[5];
			for(int j = 0; j < 5; j++){
				temp[j] = "";
			}
			data[i] = temp;
		}
		lowerBound = 0;
		upperBound = Double.MAX_VALUE;
		String driver = "com.mysql.jdbc.Driver";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "Hakuna.Mattata!");

		Statement stmt = conn.createStatement();
		String query = "use logsuggestions";
		int i = stmt.executeUpdate("use logsuggestions");
		
		fillKeywords(stmt);
		createErrorDictionary(stmt);
		stmt.close();
		prepareGUI(menu, isAdmin);
	}
	
	void fillKeywords(Statement stmt) throws SQLException 
	{
		String query = "select Keywords from logerrors";
		ResultSet rs = stmt.executeQuery(query);
		while(rs.next())
		{
			keyWords.add(rs.getString("Keywords"));
		}
		
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
	
	void createErrorDictionary(Statement stmt) throws SQLException {
	
		String query = "select Keywords, SuggestedSolution from logerrors";
		ResultSet rs = stmt.executeQuery(query);
		while(rs.next())
		{
			solutions.put(rs.getString("Keywords"), rs.getString("SuggestedSolution"));
		}
			
	}
	 * 
	 */
}
	

