/**
 * file: UserView.java
 */

package interfaceTest;

import java.sql.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import interfaceTest.CheckBoxList.CheckBoxListItem;
import interfaceTest.CheckBoxList.CheckBoxListRenderer;

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
	protected HashMap<String, String> solutions = new HashMap<String, String>();
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
	private JTabbedPane tabbedPane;
	protected JScrollPane groupScrollPane;
	
	protected JList<CheckBoxListItem> groupList;
	protected CheckBoxListItem[] listOfGroups;
	//For the and/or/not logic
	protected ArrayList<String> keyWordArrayList = new ArrayList<String>();
	protected ArrayList<String> andOrArrayList = new ArrayList<String>();
	protected ArrayList<Boolean> notArrayList = new ArrayList<Boolean>();
	private String comboBoxStatement;
	protected Vector<String> comboBoxKeyWords = new Vector<String>();
	private JTextField statementTextField;
	private Stack<Integer> strLen = new Stack<Integer>();
	private Stack<Integer> strPos = new Stack<Integer>();
	private Stack<JComboBox<String>> mostRecentCB = new Stack<JComboBox<String>>();
	private JComboBox<String> key1;
	private JComboBox<String> logic1;
	private JComboBox<String> key2;
	private JComboBox<String> logic2;
	private JComboBox<String> key3;
	private JComboBox<String> logic3;
	private JComboBox<String> key4;
	private JComboBox<String> logic4;
	private JComboBox<String> key5;
	
	//For the CheckBoxTree view
	protected JScrollPane treeScrollPane;
	protected JTree tree;
	protected CheckBoxNode allKeyWordCheckBox[];
	protected CheckBoxNode commonKeyWordCheckBox[];
	protected CheckBoxNode DBKeyWordCheckBox[];
	protected Vector<?> rootVector;
	protected Vector<?> commonKeyWordVector;
	protected Vector<?> DBKeyWordVector;
	/**
	 * Create the frame.
	 * @throws IOException 
	 */
	public UserView(MainMenu menu, boolean isAdmin) throws ClassNotFoundException, SQLException {
		hasCopiedOriginalKeyWords = false;
		data = new Object[20][];
		for(int i = 0; i < 20; i ++){
			Object[] temp = new Object[5];
			for(int j = 0; j < 5; j++){
				temp[j] = "";
			}
			data[i] = temp;
		}
		lowerBound = (double) 0;
		upperBound = Double.MAX_VALUE;
		strLen.clear();
		strPos.clear();
		mostRecentCB.clear();
		//String driver = "com.mysql.jdbc.Driver";
		String driver = "net.sourceforge.jtds.jdbc.Driver";
		Class.forName(driver);
//		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "Hakuna.Mattata!");
		Connection conn = DriverManager.getConnection("jdbc:jtds:sqlserver://vwaswp02:1433/coeus", "coeus", "C0eus");
		Statement stmt = conn.createStatement();

		fillKeywords(stmt);
		createErrorDictionary(stmt);
		stmt.close();
		prepareGUI(menu, isAdmin);
	}
	
	
	/**
	 * This function fills the ArrayList uCodes by reading in 
	 * the LogErrors_Suggestions.csv file and taking in all of the data
	 * from the UCodes column. This data will eventually be used in the
	 * findLogErrors function. 
	 * @throws IOException
	 * @throws SQLException 
	 */
	void fillKeywords(Statement stmt) throws SQLException
	{
		String query = "select Keyword from logerrors";
		ResultSet rs = stmt.executeQuery(query);
		while(rs.next())
		{
			keyWords.add(rs.getString("Keyword"));
		}
		
		numKeyWords = keyWords.size();
		listOfKeyWords = new CheckBoxListItem[numKeyWords + 1];
		allKeyWordCheckBox = new CheckBoxNode[numKeyWords + 1];
		allKeyWordCheckBox[0] = new CheckBoxNode("All KeyWords", true);
		listOfKeyWords[0] = new CheckBoxListItem("All KeyWords");
		//All Keywords selected by default
		listOfKeyWords[0].setSelected(true);
		
		int index = 1;
		comboBoxKeyWords.clear();
		for (String s : keyWords){
			listOfKeyWords[index] = new CheckBoxListItem(s);
			allKeyWordCheckBox[index] = new CheckBoxNode(s, false);
			comboBoxKeyWords.addElement(s);
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
						saveAndOrNot();
						submitButton.setEnabled(false);
						logParser.parseErrors(file, dialog);
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
		keyWords.clear();
		for (int i = 0; i < commonKeyWordCheckBox.length; i++){
			if (commonKeyWordCheckBox[i].isSelected()){
				System.out.println("Added:" + commonKeyWordCheckBox[i].toString());
				keyWords.add(commonKeyWordCheckBox[i].toString());
			}
		}
		for (int i = 0; i < DBKeyWordCheckBox.length; i++){
			if (DBKeyWordCheckBox[i].isSelected()){
				System.out.println("Added:" + DBKeyWordCheckBox[i].toString());
				keyWords.add(DBKeyWordCheckBox[i].toString());
			}
		}
	}
	
	boolean noCheckBoxSelected(){
		if (tabbedPane.getSelectedIndex() != 0){
			return false;
		}
		for (int i = 0; i < DBKeyWordCheckBox.length; i++){
			if (DBKeyWordCheckBox[i].isSelected()){
				return false;
			}
		}
		for (int i=0; i < commonKeyWordCheckBox.length; i++){
			if (commonKeyWordCheckBox[i].isSelected()){
				return false;
			}
		}
		JOptionPane.showMessageDialog(null, "Please select a checkbox");
		return true;
	}
	
	//Maps the key words to solution messages
	void createErrorDictionary(Statement stmt) throws SQLException{
		String query = "select Keyword, Suggested_Solution from logerrors";
		ResultSet rs = stmt.executeQuery(query);
		while(rs.next())
		{
			solutions.put(rs.getString("Keyword"), rs.getString("Suggested_Solution"));
		}
	}
	
	void prepareGUI(MainMenu menu, boolean isAdmin){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1500, 400);
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
		submitButton.setBackground(new Color(0, 209, 54));
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
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setMaximumSize(new Dimension(400, 280));
		contentPane.add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
			
		/*** TREE PANEL ***/
		JPanel treePanel = new JPanel();
		treePanel.setLayout(new BoxLayout(treePanel, BoxLayout.Y_AXIS));
		createTreeView();

		tree.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent me){
				if (SwingUtilities.isLeftMouseButton(me)){
					TreePath tp = tree.getPathForLocation(me.getX(), me.getY());
				    if (tp != null){
				    	String path = tp.getLastPathComponent().toString();
				    	String parent = tp.getParentPath().getLastPathComponent().toString();
				    	if (parent.equals("Common")){
				    		for (int i=0; i<commonKeyWordCheckBox.length; i++){
				    			if (path.equals(commonKeyWordCheckBox[i].toString())){
				    				System.out.println("keyword toggle: " + commonKeyWordCheckBox[i].toString());
				    				commonKeyWordCheckBox[i].setSelected(!commonKeyWordCheckBox[i].isSelected());
				    			}
				    		}
				    	}
				    	else if (parent.equals("Database")){
				    		for (int i=0; i<DBKeyWordCheckBox.length; i++){
				    			if (path.equals(DBKeyWordCheckBox[i].toString())){
				    				System.out.println("keyword toggle: " + DBKeyWordCheckBox[i].toString());
				    				DBKeyWordCheckBox[i].setSelected(!DBKeyWordCheckBox[i].isSelected());
				    			}
				    		}
				    	}
				    }
				    else
				      return;
				}
			}
		});
		CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
		tree.setCellRenderer(renderer);
		tree.setCellEditor(new CheckBoxNodeEditor(tree));
		tree.setEditable(true);        
	    
		JButton toggleAllButton = new JButton("Toggle All");
		toggleAllButton.addActionListener(e -> {
			for (int i=0; i<commonKeyWordCheckBox.length; i++){
    			commonKeyWordCheckBox[i].setSelected(!commonKeyWordCheckBox[i].isSelected());
    		}
    		for (int i=0; i<DBKeyWordCheckBox.length; i++){
    			DBKeyWordCheckBox[i].setSelected(!DBKeyWordCheckBox[i].isSelected());
    		}
    		treePanel.repaint();
		});
		toggleAllButton.setAlignmentX( Component.CENTER_ALIGNMENT);
		
		treeScrollPane = new JScrollPane(tree);
		treePanel.add(treeScrollPane);
		treePanel.add(toggleAllButton);
		
		/*** AND OR NOT PANEL ***/
		JPanel andOrNotPanel = new JPanel();
		andOrNotPanel.setMaximumSize(new Dimension(400, 280));
		andOrNotPanel.setOpaque(true);
		andOrNotPanel.setBackground(Color.WHITE);
		andOrNotPanel.setLayout(new BoxLayout(andOrNotPanel, BoxLayout.Y_AXIS));
		
		JPanel comboBoxPanel = new JPanel(new FlowLayout());
		comboBoxPanel.setOpaque(true);
		comboBoxPanel.setBackground(Color.WHITE);
		key1 = logicalComboBox(2);
		logic1 = logicalComboBox(3);
		key2 = logicalComboBox(2);
		logic2 = logicalComboBox(1);
		key3 = logicalComboBox(2);
		logic3 = logicalComboBox(1);
		key4 = logicalComboBox(2);
		logic4 = logicalComboBox(1);
		key5 = logicalComboBox(2);
		
		comboBoxPanel.add(key1);
		comboBoxPanel.add(logic1);
		comboBoxPanel.add(key2);
		comboBoxPanel.add(logic2);
		comboBoxPanel.add(key3);
		comboBoxPanel.add(logic3);
		comboBoxPanel.add(key4);
		comboBoxPanel.add(logic4);
		comboBoxPanel.add(key5);
		
		JPanel statementPanel = new JPanel(new FlowLayout());
		statementPanel.setOpaque(true);
		statementPanel.setBackground(Color.WHITE);
		statementTextField = new JTextField("");
		statementTextField.setPreferredSize(new Dimension(550, 20));
		statementTextField.setEditable(false);
		statementTextField.setOpaque(true);
		statementTextField.setBackground(Color.WHITE);
		statementPanel.add(statementTextField);
		
		JButton undoButton = new JButton("Undo");
		undoButton.setAlignmentX(CENTER_ALIGNMENT);
		undoButton.addActionListener(e -> {
			if (statementTextField.getText().isEmpty()){
				return;
			}
			String temp = statementTextField.getText().toString();
			int len = strLen.pop();
			int pos = strPos.pop();
			temp = temp.substring(0, pos) + temp.substring(len + pos);
			statementTextField.setText(temp);
			mostRecentCB.pop().setSelectedIndex(0);
		});
		
		andOrNotPanel.add(Box.createVerticalGlue());
		andOrNotPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		andOrNotPanel.add(comboBoxPanel);
		andOrNotPanel.add(statementPanel);
		andOrNotPanel.add(undoButton);
		andOrNotPanel.add(Box.createVerticalGlue());
		
		/*** GROUP PANEL ***/
		JPanel groupPanel = new JPanel();
		groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.Y_AXIS));
		
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
		
		tabbedPane.addTab("Tree View", treePanel);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		tabbedPane.addTab("AND/OR/NOT View", andOrNotPanel);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		tabbedPane.addTab("Group View", groupPanel);
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
		
		mainPanel.add(tabbedPane);
		DefaultTableModel tableModel = new DefaultTableModel(data, headers) {
		    @Override
		    public boolean isCellEditable(int row, int column) {
		       //all cells false
		       return false;
		    }
		}; 
		errorTable = new JTable(tableModel);
		errorScrollPane = new JScrollPane(errorTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		mainPanel.add(errorScrollPane);
		
		setVisible(true);
	}
	
	JComboBox<String> logicalComboBox(int option){
		JComboBox<String> cb = new JComboBox<String>();
		cb.setOpaque(true);
		cb.setBackground(Color.WHITE);
		switch (option){
		case 1:
			cb.addItem("-----");
			cb.setPreferredSize(new Dimension(60, 20));
			cb.addItem("AND");
			cb.addItem("OR");
			cb.addItem("AND NOT");
			cb.addItem("OR NOT");
			
			break;
		case 2:
			cb.addItem("--------------");
			cb.setPreferredSize(new Dimension(90, 20));
			for (String s: comboBoxKeyWords){
				cb.addItem(s);
			}
			break;
		case 3:
			cb.addItem("-----");
			cb.setPreferredSize(new Dimension(60, 20));
			cb.addItem("AND");
			cb.addItem("AND NOT");
			break;
		}
		cb.addActionListener(e ->{
			if (!cb.getSelectedItem().toString().equals("-----") && !cb.getSelectedItem().toString().equals("--------------")){
				strLen.push(cb.getSelectedItem().toString().length() + 1);
				strPos.push(statementTextField.getText().toString().length());
				comboBoxStatement = statementTextField.getText() + " " + cb.getSelectedItem().toString();
				statementTextField.setText(comboBoxStatement);
				mostRecentCB.push(cb);
			}
		});
		return cb;
	}
	void createTreeView(){
		DBKeyWordCheckBox = new CheckBoxNode[2];
		commonKeyWordCheckBox = new CheckBoxNode[numKeyWords-2];
		int i = 0;
		for (CheckBoxNode node : allKeyWordCheckBox){
			if (node.toString().equals("DEADLOCK")){
				System.out.println("deadlock");
				DBKeyWordCheckBox[0] = node;
			}
			else if (node.toString().equals("===>")){
				System.out.println("arrow");
				DBKeyWordCheckBox[1] = node;
			}
			else if (!node.toString().equals("All KeyWords")) {
				commonKeyWordCheckBox[i] = node;
				i++;
				System.out.println(i);
			}
		}
		commonKeyWordVector = new NamedVector("Common", commonKeyWordCheckBox);
		DBKeyWordVector = new NamedVector("Database", DBKeyWordCheckBox);
		Object rootNodes[] = {commonKeyWordVector, DBKeyWordVector};
		rootVector = new NamedVector("Root", rootNodes);
		tree = new JTree(rootVector);
	}
	
	boolean createGroupDisplay(){
		model.clear();
		if (keyWordGroups.isEmpty()){
			return false;
		}
		System.out.println("groups not empty");
		int index = 0;
		for (HashSet<String> list : keyWordGroups){
	    	StringBuilder listItem = new StringBuilder();
	    	listItem.setLength(0);
	    	for (String s : list){
	    		listItem.append(s + " ");
	    	}
	    	System.out.println("adding: " + listItem.toString());
	    	listOfGroups = new CheckBoxListItem[keyWordGroups.size()];
	    	listOfGroups[index] = new CheckBoxListItem(listItem.toString());
	    	model.addElement(listOfGroups[index]);
	    	++index;
	    }		
		return true;
	}
	
	void saveAndOrNot(){
		keyWordArrayList.clear();
		andOrArrayList.clear();
		notArrayList.clear();
		if (!key1.getSelectedItem().toString().equals("--------------")){
			keyWordArrayList.add(key1.getSelectedItem().toString());
		}
		if (!key2.getSelectedItem().toString().equals("--------------")){
			keyWordArrayList.add(key2.getSelectedItem().toString());
		}
		if (!key3.getSelectedItem().toString().equals("--------------")){
			keyWordArrayList.add(key3.getSelectedItem().toString());
		}
		if (!key4.getSelectedItem().toString().equals("--------------")){
			keyWordArrayList.add(key4.getSelectedItem().toString());
		}
		if (!key5.getSelectedItem().toString().equals("--------------")){
			keyWordArrayList.add(key5.getSelectedItem().toString());
		}
		if (!logic1.getSelectedItem().toString().equals("-----")){
			andOrArrayList.add("AND");
			if (logic1.getSelectedItem().toString().equals("AND NOT")){
				notArrayList.add(true);
			}
			else {
				notArrayList.add(false);
			}
		}
		if (!logic2.getSelectedItem().toString().equals("-----")){
			if (logic2.getSelectedItem().toString().equals("AND")){
				andOrArrayList.add("AND");
				notArrayList.add(false);
			}
			else if (logic2.getSelectedItem().toString().equals("AND NOT")){
				andOrArrayList.add("AND");
				notArrayList.add(true);
			}
			else if (logic2.getSelectedItem().toString().equals("OR")){
				andOrArrayList.add("OR");
				notArrayList.add(false);
			}
			else {
				andOrArrayList.add("OR");
				notArrayList.add(true);
			}
		}
		if (!logic3.getSelectedItem().toString().equals("-----")){
			if (logic3.getSelectedItem().toString().equals("AND")){
				andOrArrayList.add("AND");
				notArrayList.add(false);
			}
			else if (logic3.getSelectedItem().toString().equals("AND NOT")){
				andOrArrayList.add("AND");
				notArrayList.add(true);
			}
			else if (logic3.getSelectedItem().toString().equals("OR")){
				andOrArrayList.add("OR");
				notArrayList.add(false);
			}
			else {
				andOrArrayList.add("OR");
				notArrayList.add(true);
			}
		}
		if (!logic4.getSelectedItem().toString().equals("-----")){
			if (logic4.getSelectedItem().toString().equals("AND")){
				andOrArrayList.add("AND");
				notArrayList.add(false);
			}
			else if (logic4.getSelectedItem().toString().equals("AND NOT")){
				andOrArrayList.add("AND");
				notArrayList.add(true);
			} 
			else if (logic4.getSelectedItem().toString().equals("OR")){
				andOrArrayList.add("OR");
				notArrayList.add(false);
			}
			else {
				andOrArrayList.add("OR");
				notArrayList.add(true);
			}
		}
		for (String s : keyWordArrayList){
			System.out.println(s);
		}
		for (String s : andOrArrayList){
			System.out.println(s);
		}
		for (boolean b : notArrayList){
			System.out.println(b);
		}
	}
}
	

