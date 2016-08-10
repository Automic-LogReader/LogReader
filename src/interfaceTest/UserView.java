/**
 * @file UserView.java
 * @authors Leah Talkov, Jerry Tsui
 * @date 8/4/2016
 * Creates the main user interface. The user can input a log file and
 * parse through it to find specified errors. The interface contains a
 * table which displays the results of the parse by showing the timestamp
 * for when the error occurred, the name of the error, the error description
 * from the file, and the suggested solution for the error. The three ways that can 
 * be used to find errors are through the checkbox tree, groups, and logical 
 * statements. The checkbox tree allows the user to select which keywords they 
 * want to find, and the parse will search for all the selected errors. The group
 * functionality is similar, but these are built in groups of keywords that are 
 * commonly searched for together. The logical statement allows users to refine
 * their search by putting more specifications on what they want within a single line
 * using a mixture of keywords and AND, OR, and NOT. 
 */
 

package interfaceTest;

import java.sql.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.MutableComboBoxModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import interfaceTest.CheckBoxList.CheckBoxListItem;
import interfaceTest.CheckBoxList.CheckBoxListRenderer;


@SuppressWarnings("serial")

public class UserView extends JFrame{
	/**Holds the content of the table from the database about group contents and names*/
	protected HashMap<String, String> GroupInfo = new HashMap<String, String>();
	/**Headers for the JTable in the interface*/
	protected final String [] headers = {"Error #", "Timestamp",
								"Keywords", "Error Message", "Suggested Solution"};
	/**Used to fill the contents of the JTable, will contains headers and errorData*/
	private Object [][] data;
	/**ProgressDialog with progress bar showing the progress of parsing process*/
	protected ProgressDialog dialog;
	/**Thread to run progress bar while parsing is happening*/
	private Thread t;
	/**Holds path of the user given logfile*/
	protected String logFile;
	/** Maps the keywords to a URL pertaining to the solution */ 
	protected static HashMap<String, String> urlMap = new HashMap<String, String>();
	/**Maps keywords to their solutions where the keyword is the key, and the solution is the value*/
	protected HashMap<String, String> solutions = new HashMap<String, String>();
	/**Maps keywords to their folders where the keyword is the key, and the folder is the value*/
	protected HashMap<String, String> folderMap = new HashMap<String, String>();
	/**Contains all the unique names of the folders*/
	protected HashSet<String> folderSet = new HashSet<String>();
	/**Links folders to keywords where folders are the key and the list of keywords is the value*/
	protected HashMap<String, ArrayList<String>> treeMap = new HashMap<String, ArrayList<String>>();
	/**Contains all of the keywords currently in the database*/
	protected HashSet<String> keyWords = new HashSet<String>();
	/**Contains the original keywords to compare against user selected keywords*/
	protected HashSet<String> originalKeyWords;
	/**A HashSet used to prevent duplicates when listing out the individual keywords in the groups */
	protected HashSet<String> groupKeywordsHashSet;
	/**Used to compare against user selected keywords*/
	private boolean hasCopiedOriginalKeyWords;
	/**The size of the logfile*/
	private long fileSize;
	/**The size of the file divided by 100, used for updating the ProgressDialog*/
	protected long fileSizeDivHundred;
	/**The JTable used to display the error results*/
	protected JTable errorTable;
	/**Textfield for the selected filepath*/
	private JTextField tfFilePath;
	/**User clicks after selecting directory for log file*/
	protected JButton btnSubmit;
	/**Scrollpane for errorTable*/
	protected JScrollPane errorScrollPane;
	/**AdminView that is initialized if isAdmin is true*/
	private AdminView admin;
	/**LogParser object used for file parsing*/
	protected LogParser logParser;
	
	//For the PreferenceEditor
	/**User selected lower bound for Time Critical errors*/
	protected Double lowerBound;
	/**User selected upper bound for Time Critical errors*/
	protected Double upperBound;
	/**User selected lines before an error*/
	protected Integer numLinesBefore;
	/**User selected lines after an error*/
	protected Integer numLinesAfter;
	protected JList<CheckBoxListItem> list;
	/**Model for the checkbox groups*/
	protected DefaultListModel<CheckBoxListItem> groupNameListModel;
	protected DefaultListModel<String> groupKeywordsListModel;
	private JTabbedPane tabbedPane;
	protected JScrollPane groupScrollPane;
	protected JList<CheckBoxListItem> groupNameList;
	protected JList<String> groupKeywordsList;
	
	protected CheckBoxListItem[] listOfGroups;
	
	//For the and/or/not logic
	/**Contains the list of keywords chosen for the logic statement*/
	protected ArrayList<String> keyWordArrayList = new ArrayList<String>();
	/**Contains the list of operands chosen for the logic statement*/
	protected ArrayList<String> operandArrayList = new ArrayList<String>();
	/**Contains the NOT values for the keywords chosen for the logic statement*/
	protected ArrayList<Boolean> notArrayList = new ArrayList<Boolean>();
	/**Combo box for drop down keywords*/
	protected Vector<String> comboBoxKeyWords = new Vector<String>();

	private LogicalComboBox cbKey1;
	private LogicalComboBox cbLogic1;
	private LogicalComboBox cbKey2;
	private LogicalComboBox cbLogic2;
	private LogicalComboBox cbKey3;

	//For the CheckBoxTree view
	/** Checkbox tree for the tree view */
	protected CBTree cbTree;
	/** Root node of the Checkbox tree */
	protected DefaultMutableTreeNode rootNode;
	/** Model for the checkbox tree*/
	protected DefaultTreeModel treeModel;
	/** JScrollPane holding the contents of the checkbox view */
	protected JScrollPane treeScrollPane;
	/** Popup menu to be displayed when the user right-clicks on the JTable */
	protected JPopupMenu popupMenu;
	/** JPanel holding the JScrollPane for the checkbox view */
	private JPanel pnlTreeView;
	
	/** Contains a list of lists holding the lines before an error message */
	protected ArrayList<ArrayList<String>> linesBeforeArrayList = new ArrayList<ArrayList<String>>();
	/** Maps an error number to the list of lines after an error message */
	protected HashMap<Integer, ArrayList<String>> linesAfterHashMap = new HashMap<Integer, ArrayList<String>>();
	
	/** JMenuItem displaying a dialog showing the lines before an error */
	protected JMenuItem menuItemLinesBefore;
	/** JMenuItem displaying a dialog showing the lines after an error */
	protected JMenuItem menuItemLinesAfter;
	/** JMenuItem opening a hyperlink to the solution online */
	protected JMenuItem menuItemUrl;
	/** JMemuItem that copies the value of a given JTable cell when clicked */
	protected JMenuItem menuItemCopy;
	
	/**
	 * Creates the UserView frame
	 * @param menu 		MainMenu window that instantiated the UserView
	 * @param isAdmin 		Administrator status
	 * @throws ClassNotFoundException	Class not found
	 * @throws SQLException	SQL Exception
	 */
	public UserView(MainMenu menu, boolean isAdmin) throws ClassNotFoundException, SQLException {
		hasCopiedOriginalKeyWords = false;
		//Initializes the blank table
		data = new Object[40][];
		for(int i = 0; i < 40; i ++){
			Object[] temp = new Object[5];
			for(int j = 0; j < 5; j++){
				temp[j] = "";
			}
			data[i] = temp;
		}
		
		initPreferenceEditorValues();
		
		String driver = "net.sourceforge.jtds.jdbc.Driver";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection("jdbc:jtds:sqlserver://vwaswp02:1433/coeus", "coeus", "C0eus");
		Statement stmt = conn.createStatement();

		fillKeywords(stmt);
		createErrorDictionary(stmt);
		
		//*****************************************************************
		//I called it here just because there is already a connection to the db
		//*****************************************************************
		loadGroupInfo(stmt);
		stmt.close();
		prepareGUI(menu, isAdmin);
	}
	
	/**
	 * Fills the data structure keyWords from the database content,
	 * and also fills the folder map to see which keywords are associated
	 * with which folder, as well as fills the folderSet with all the
	 * unique folder names.
	 * @param stmt SQL statement to be executed within the function
	 * @throws SQLException if there is an error connecting 
	 */
	protected void fillKeywords(Statement stmt) throws SQLException{
		keyWords.clear();
		folderMap.clear();
		folderSet.clear();
		treeMap.clear();
		
		String query = "select Keyword, Folder, Hyperlink from logerrors";
		ResultSet rs = stmt.executeQuery(query);
		while(rs.next()){
			keyWords.add(rs.getString("Keyword"));
			folderMap.put(rs.getString("Keyword"), rs.getString("Folder"));
			urlMap.put(rs.getString("Keyword"), rs.getString("Hyperlink"));
			folderSet.add(rs.getString("Folder"));
		}
		//Fills the treeMap
		for (String folderName : folderSet){
			ArrayList<String> tempList = new ArrayList<String>();
			for (Map.Entry<String, String> entry : folderMap.entrySet()){
				if (entry.getValue().equals(folderName)){
					tempList.add(entry.getKey());
				}
			}	
			treeMap.put(folderName, tempList);
		}
		
		comboBoxKeyWords.clear();
		for (String s : keyWords){
			comboBoxKeyWords.addElement(s);
		}
		
		if (!hasCopiedOriginalKeyWords){
			originalKeyWords = new HashSet<String>(keyWords);
			hasCopiedOriginalKeyWords = true;
		}
	}
	
	/**
	 * Starts the thread to start parsing the log file. Returns if
	 * the path parameter given was not valid.
	 * @param path	The path of the log file to be parsed through
	 * @throws IOException	IO Exception
	 */
	private void findLogErrors(String path) throws IOException{
		logFile = path;
		File file = new File(path);
		if (!file.exists()){
			JOptionPane.showMessageDialog(null, "The file cannot be found");
			return;
		}
		try {
			if (tabbedPane.getSelectedIndex() == 0){
				if (Utility.noCheckBoxSelected(cbTree)) {
					return;
				}
			}
			else if (tabbedPane.getSelectedIndex() == 1){
				if (!saveAndOrNot()){
					return;
				}
			}
			else if (tabbedPane.getSelectedIndex() == 2){
				if (Utility.noCheckBoxSelected(listOfGroups)){
					return;
				}
			}
			dialog = new ProgressDialog(file, this);
			dialog.setVisible(true);
			logParser = new LogParser(this, tabbedPane.getSelectedIndex());
			} catch (Exception e) {
				e.printStackTrace();
			};	
			t = new Thread(new Runnable(){
				public void run() {
					try {
						fileSize = file.length();
						fileSizeDivHundred = fileSize/100;
						btnSubmit.setEnabled(false);
						logParser.parseErrors(file, dialog);
						} catch (IOException e) {
							e.printStackTrace();
							}
						}			
					}
				);
		t.start();
	}
	
	/**
	 * Helper function called before LogParse. Fills the keyWords set with 
	 * the set of keywords that the parser uses to base its searches off of.
	 * Function is only called when the user is selecting from the CheckBoxTree
	 * or from the group view
	 * @param selectedTab	The current tab in the treeview
	 */
	void updateKeyWords(int selectedTab){
		keyWords.clear();
		if (selectedTab == 0){
			Enumeration<?> g = ((DefaultMutableTreeNode) cbTree.getModel().getRoot()).preorderEnumeration();
			while (g.hasMoreElements()){
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) g.nextElement();
				Object obj = node.getUserObject();  
				if (obj instanceof TreeNodeCheckBox){
					TreeNodeCheckBox cb = (TreeNodeCheckBox) obj;
					if (cb.isSelected()){
						keyWords.add(cb.getText());
						System.out.println("Added: " + cb.getText());
					}
					
				}
			}
		}
		else if (selectedTab == 2){
			System.out.println("Group search");
			if (groupKeywordsListModel.isEmpty()) return;
			for (int i=0; i<groupKeywordsListModel.size(); i++){
				keyWords.add(groupKeywordsListModel.getElementAt(i));
				System.out.println(groupKeywordsListModel.getElementAt(i));
			}
		}
	}
		
	/**
	 * Maps the keyywords to the solution messages in a HashMap
	 * @param stmt	SQL Statement
	 * @throws SQLException	SQL error
	 */
	protected void createErrorDictionary(Statement stmt) throws SQLException{
		String query = "select Keyword, Suggested_Solution from logerrors";
		ResultSet rs = stmt.executeQuery(query);
		while(rs.next())
		{
			solutions.put(rs.getString("Keyword"), rs.getString("Suggested_Solution"));
		}
	}
	
	/**
	 * Prepares the GUI for UserView. Since the Admin has all of the user functionality,
	 * the boolean isAdmin determines if the "Admin Settings" button is enabled
	 * @param menu MainMenu window that instantiated the UserView
	 * @param isAdmin Administrator status
	 */
	private void prepareGUI(MainMenu menu, boolean isAdmin){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1500, 400);
		setLocationRelativeTo(null);
		setMinimumSize(new Dimension(1000, 300));
		
		try {
		     ClassLoader cl = this.getClass().getClassLoader();
		     ImageIcon programIcon = new ImageIcon(cl.getResource("res/logo.png"));
		     setIconImage(programIcon.getImage());
		  } catch (Exception e) {
		     System.out.println("Could not load program icon.");
		  }
	
		JPanel pnlMain = new JPanel();
		pnlMain.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(pnlMain);
		pnlMain.setLayout(new BorderLayout(0, 0));
		
		JPanel pnlBottom = new JPanel();
		pnlMain.add(pnlBottom, BorderLayout.SOUTH);
		
		pnlBottom.setLayout(new BoxLayout(pnlBottom, BoxLayout.X_AXIS));
		
		pnlBottom.add(Box.createHorizontalStrut(20));
		pnlBottom.add(Box.createHorizontalStrut(20));
		
		JButton btnPreference = new JButton("Preferences");
		btnPreference.addActionListener(e -> {
			new PreferenceEditor(this, isAdmin);
		});
		btnPreference.setToolTipText("Groupings, Time Critical DB Call Intervals ...");
		pnlBottom.add(btnPreference);
		
		pnlBottom.add(Box.createRigidArea(new Dimension(10,0)));
		
		JButton chooseFile = new JButton("Choose File");
		chooseFile.addActionListener(e -> {
		    JFileChooser chooser = new JFileChooser();
		    FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        "Text Files", "txt", "text");
		    chooser.setFileFilter(filter);
		    chooser.setAcceptAllFileFilterUsed(false);
		    int returnVal = chooser.showOpenDialog(getRootPane());
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		    	 tfFilePath.setText(chooser.getSelectedFile().getAbsolutePath());
		    }
		});
		pnlBottom.add(chooseFile);
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		pnlBottom.add(horizontalStrut);
		
		tfFilePath = new JTextField();
		pnlBottom.add(tfFilePath);
		tfFilePath.setColumns(10);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		pnlBottom.add(horizontalStrut_1);
		
		btnSubmit = new JButton("Submit");
		btnSubmit.setBackground(new Color(0, 209, 54));
		btnSubmit.setPreferredSize(new Dimension(80, 30));
		btnSubmit.addActionListener(e -> {
			String path = tfFilePath.getText();
			if (path.equals(""))
				JOptionPane.showMessageDialog(null, "Please enter a path");
			else
				try {
					findLogErrors(tfFilePath.getText());
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "The file cannot be found");
					e1.printStackTrace();
				}
		});
		btnSubmit.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).
        put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER,0), "Enter pressed");
		btnSubmit.getActionMap().put("Enter pressed", new AbstractAction()
				{
					public void actionPerformed(ActionEvent e)
					{
						if (tfFilePath.getText().equals(""))
							JOptionPane.showMessageDialog(null, "Please enter a path");
						else
							try {
								findLogErrors(tfFilePath.getText());
							} catch (IOException e1) {
								JOptionPane.showMessageDialog(null, "The file cannot be found");
								e1.printStackTrace();
							}
					}
				});
		pnlBottom.add(btnSubmit);
		
		pnlBottom.add(Box.createRigidArea(new Dimension(10,0)));
		
		JButton btnBack = new JButton("Back");
		btnBack.setPreferredSize(new Dimension(80, 30));
		btnBack.addActionListener(e ->{
			menu.setVisible(true);
			this.setVisible(false);
		});
		pnlBottom.add(btnBack);
		
		pnlBottom.add(Box.createRigidArea(new Dimension(10,0)));
		
		JButton btnAdmin = new JButton("Admin Features");
		btnAdmin.setPreferredSize(new Dimension(130, 30));
		btnAdmin.setToolTipText("Modify entries, folders, solutions");
		btnAdmin.addActionListener(e -> {
			try {
				admin = new AdminView(this);
				admin.setVisible(true);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
		});
		pnlBottom.add(btnAdmin);
		
		if(isAdmin){
			btnAdmin.setVisible(true);
			setTitle("Administrator View");
		} else {
			btnAdmin.setVisible(false);
			setTitle("User View");
		}
		
		pnlBottom.add(Box.createHorizontalStrut(20));
		pnlBottom.add(Box.createHorizontalStrut(20));
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setPreferredSize(new Dimension(600, 280));
		
		JPanel mainPanel = new JPanel();
		mainPanel.setMaximumSize(new Dimension(600, 280));
		pnlMain.add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
			
		pnlTreeView = new JPanel();
		pnlTreeView.setLayout(new BoxLayout(pnlTreeView, BoxLayout.Y_AXIS));
		createTreeView();
		
		
		JButton btnToggleAll = new JButton("Toggle All");
		btnToggleAll.addActionListener(e -> {
			cbTree.expandAll(new TreePath(cbTree.getModel().getRoot()));
			Enumeration<?> g = ((DefaultMutableTreeNode) cbTree.getModel().getRoot()).preorderEnumeration();
			while (g.hasMoreElements()){
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) g.nextElement();
				Object obj = node.getUserObject();  
				if (obj instanceof TreeNodeCheckBox){
					TreeNodeCheckBox cb = (TreeNodeCheckBox) obj;
					cb.setSelected(!cb.isSelected());
				}
			}
    		pnlTreeView.repaint();
		});
		btnToggleAll.setAlignmentX( Component.CENTER_ALIGNMENT);
		
		treeScrollPane = new JScrollPane(cbTree);
		pnlTreeView.add(treeScrollPane);
		pnlTreeView.add(btnToggleAll);
		
		/*** AND OR NOT PANEL ***/
		
		JPanel pnlAndOrNot = new JPanel();
		pnlAndOrNot.setMaximumSize(new Dimension(600, 280));
		pnlAndOrNot.setOpaque(true);
		pnlAndOrNot.setBackground(Color.WHITE);
		pnlAndOrNot.setLayout(new BoxLayout(pnlAndOrNot, BoxLayout.Y_AXIS));
	
		JPanel pnlInstructions = new JPanel(new FlowLayout());
		JLabel lblInstructions = new JLabel("Select keywords and operators from the boxes below", JLabel.CENTER);
		lblInstructions.setAlignmentX(CENTER_ALIGNMENT);
		pnlInstructions.add(lblInstructions);
		pnlInstructions.setOpaque(true);
		pnlInstructions.setBackground(Color.WHITE);
		
		JPanel pnlComboBox = new JPanel(new FlowLayout());
		pnlComboBox.add(Box.createHorizontalGlue());
		pnlComboBox.setOpaque(true);
		pnlComboBox.setBackground(Color.WHITE);
		cbKey1 = new LogicalComboBox(2, this);
		cbKey1.addActionListener(e -> {
			if (cbKey1.getSelectedIndex() != -1 && cbLogic1.getSelectedIndex() != -1 && cbKey2.getSelectedIndex() != -1){
				cbLogic2.setEnabled(true);
				cbKey3.setEnabled(true);
			}
		});
		cbLogic1 = new LogicalComboBox(3, this);
		cbLogic1.addActionListener(e -> {
			if (cbKey1.getSelectedIndex() != -1 && cbLogic1.getSelectedIndex() != -1 && cbKey2.getSelectedIndex() != -1){
				cbLogic2.setEnabled(true);
				cbKey3.setEnabled(true);
			}
		});
		cbKey2 = new LogicalComboBox(2, this);
		cbKey2.addActionListener(e -> {
			if (cbKey1.getSelectedIndex() != -1 && cbLogic1.getSelectedIndex() != -1 && cbKey2.getSelectedIndex() != -1){
				cbLogic2.setEnabled(true);
				cbKey3.setEnabled(true);
			}
		});
		cbLogic2 = new LogicalComboBox(1, this);
		cbLogic2.setEnabled(false);
		cbKey3 = new LogicalComboBox(2, this);
		cbKey3.setEnabled(false);
		
		pnlComboBox.add(cbKey1);
		pnlComboBox.add(cbLogic1);
		pnlComboBox.add(cbKey2);
		pnlComboBox.add(cbLogic2);
		pnlComboBox.add(cbKey3);
		pnlComboBox.add(Box.createHorizontalGlue());
		
		JButton btnClear = new JButton("Clear");
		btnClear.setAlignmentX(CENTER_ALIGNMENT);
		btnClear.addActionListener(e -> {
			cbLogic1.setSelectedIndex(-1);
			cbLogic2.setSelectedIndex(-1);
			cbKey1.setSelectedIndex(-1);
			cbKey2.setSelectedIndex(-1);
			cbKey3.setSelectedIndex(-1);
			cbLogic2.setEnabled(false);
			cbKey3.setEnabled(false);
		});
		
		pnlAndOrNot.add(Box.createVerticalGlue());
		pnlAndOrNot.add(Box.createRigidArea(new Dimension(0, 10)));
		pnlAndOrNot.add(pnlInstructions);
		
		pnlAndOrNot.add(pnlComboBox);
		pnlAndOrNot.add(btnClear);
		pnlAndOrNot.add(Box.createVerticalGlue());
		
		/*** GROUP PANEL ***/
		JPanel pnlGroupView = new JPanel();
		pnlGroupView.setLayout(new BoxLayout(pnlGroupView, BoxLayout.Y_AXIS));
		pnlGroupView.setMaximumSize(new Dimension(600, 280));
		groupKeywordsListModel = new DefaultListModel<String>();
		groupKeywordsList = new JList<String>(groupKeywordsListModel);
		
		
		groupNameListModel = new DefaultListModel<CheckBoxListItem>();
		createGroupView();
		groupNameList = new JList<CheckBoxListItem>(groupNameListModel);
		groupNameList.setCellRenderer(new CheckBoxListRenderer());
		groupNameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		groupNameList.addMouseListener(new MouseAdapter(){
			 public void mouseClicked(MouseEvent event) {
		            @SuppressWarnings("unchecked")
					JList<CheckBoxListItem> list =
		               (JList<CheckBoxListItem>) event.getSource();
		            int index = list.locationToIndex(event.getPoint());
		            CheckBoxListItem item = (CheckBoxListItem) list.getModel()
		                  .getElementAt(index);
		            item.setSelected(!item.isSelected());
		            list.repaint(list.getCellBounds(index, index));
		            groupKeywordsHashSet.clear();
		            groupKeywordsListModel.clear();
		            for (int i=0; i<groupNameListModel.size(); i++){
	            		CheckBoxListItem cbItem = (CheckBoxListItem) list.getModel()
	            				.getElementAt(i);
	            		if (cbItem.isSelected()){
	            			String keywordString = GroupInfo.get(cbItem.toString());
				            String keywordStringArray[] = keywordString.split(" ");
				            for (String key : keywordStringArray){
				            	groupKeywordsHashSet.add(key);
				            }
	            		}
	            	}
		            for (String key : groupKeywordsHashSet){
		            	groupKeywordsListModel.addElement(key);
		            }
		         }
		});
		groupScrollPane = new JScrollPane(groupNameList);
		groupScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		pnlGroupView.add(groupScrollPane);
		pnlGroupView.add(Box.createRigidArea(new Dimension(0, 10)));
		JScrollPane pnlGroupViewBottom = new JScrollPane(groupKeywordsList);
		
		pnlGroupViewBottom.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		pnlGroupViewBottom.setViewportView(groupKeywordsList);
		
		pnlGroupView.add(pnlGroupViewBottom);
		
		tabbedPane.addTab("Tree View", pnlTreeView);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		tabbedPane.addTab("AND/OR/NOT View", pnlAndOrNot);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		tabbedPane.addTab("Group View", pnlGroupView);
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
		
		mainPanel.add(tabbedPane);
		DefaultTableModel tableModel = new DefaultTableModel(data, headers) {
		    @Override
		    public boolean isCellEditable(int row, int column) {
		       //all cells false
		       return false;
		    }
		}; 
		popupMenu = new JPopupMenu();
        menuItemCopy = new JMenuItem("Copy");
        menuItemCopy.addActionListener(e -> {
        	TableMouseListener.copyCellValueToClipBoard();
        });
        menuItemCopy.setEnabled(false);
        popupMenu.add(menuItemCopy);
        
        menuItemUrl = new JMenuItem("See suggested solution online");
        menuItemUrl.addActionListener(e -> {
        	try {
				TableMouseListener.openURI();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
        });
        menuItemUrl.setEnabled(false);
        popupMenu.add(menuItemUrl);
        
        menuItemLinesBefore = new JMenuItem("Show Lines Before");
        menuItemLinesBefore.addActionListener(e -> {
        	LineDialog linesBefore = new LineDialog(TableMouseListener.getCurrentRow(), this, "BEFORE");
        });
        menuItemLinesBefore.setEnabled(false);
        popupMenu.add(menuItemLinesBefore);
        
        menuItemLinesAfter = new JMenuItem("Show Lines After");
        menuItemLinesAfter.addActionListener(e -> {
        	LineDialog linesAfter = new LineDialog(TableMouseListener.getCurrentRow(), this, "AFTER");
        });
        menuItemLinesAfter.setEnabled(false);
        popupMenu.add(menuItemLinesAfter);
        
        
		errorTable = new JTable(tableModel);
		errorTable.setCellSelectionEnabled(true);
		errorTable.setComponentPopupMenu(popupMenu);
		errorTable.addMouseListener(new TableMouseListener(errorTable));
		
		errorScrollPane = new JScrollPane(errorTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		errorScrollPane.setPreferredSize(new Dimension(700, 280));
		mainPanel.add(errorScrollPane);
		
		setVisible(true);
	}

	/**
	 * Helper function that generates the CheckBoxTree view
	 * and adds a mouselistener to the tree to aid selection.
	 * At the end of the function, it calls updateTreeView() 
	 * which fills the DefaultTreeModel with nodes from the 
	 * treeMap HashMap.
	 */
	void createTreeView(){
		cbTree = new CBTree();
		cbTree.addMouseListener(new MouseAdapter(){
			public void mousePressed (MouseEvent e){
	            if ( SwingUtilities.isRightMouseButton(e)){
	            	try {
		                TreePath path = cbTree.getPathForLocation ( e.getX (), e.getY () );
		                DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
	                    Object obj = node.getUserObject();
	                    if (obj instanceof String){
	                    	//System.out.println(obj);
	                    	Rectangle pathBounds = cbTree.getUI ().getPathBounds (cbTree, path);
	                    	if ( pathBounds != null && pathBounds.contains (e.getX (), e.getY())){
	    	                    JPopupMenu menu = new JPopupMenu();
	    	                    JMenuItem menuItemSelectAll = new JMenuItem("Select All");
	    	                    menuItemSelectAll.addActionListener(actionEvent -> {
	    	                    	for (int i=0; i<node.getChildCount(); i++){
	    	                    		DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
	    	                    		Object childObject = child.getUserObject();
	    	                    		if (childObject instanceof TreeNodeCheckBox){
	    	                    			((TreeNodeCheckBox) childObject).setSelected(true);
	    	                    		}
	    	                    	}
	    	                    	pnlTreeView.repaint();
	    	                    	cbTree.expandPath(path);
	    	                    });
	    	                    JMenuItem menuItemDeselectAll = new JMenuItem("Deslect All");
	    	                    menuItemDeselectAll.addActionListener(actionEvent -> {
	    	                    	for (int i=0; i<node.getChildCount(); i++){
	    	                    		DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
	    	                    		Object childObject = child.getUserObject();
	    	                    		if (childObject instanceof TreeNodeCheckBox){
	    	                    			((TreeNodeCheckBox) childObject).setSelected(false);
	    	                    		}
	    	                    	}
	    	                    	pnlTreeView.repaint();
	    	                    	cbTree.expandPath(path);
	    	                    });
	    	                    menu.add(menuItemSelectAll);
	    	                    menu.add(menuItemDeselectAll);
	    	                    menu.show(cbTree, pathBounds.x, pathBounds.y + pathBounds.height); 
	    	                }
	                    } 
                    } catch (NullPointerException nullPtrException){
                    	return;
                    }
	            }
                else {
                	return;
                }
	            
	        }
		});
		updateTreeView();
	}
	
	/**
	 * Used as a helper function for the createTreeView() function
	 * and also updates the checkbox tree dynamically as the 
	 * administrator makes changes to the database. 
	 */
	protected void updateTreeView(){
		rootNode = new DefaultMutableTreeNode("Root");
		treeModel = new DefaultTreeModel(rootNode);
		cbTree.setModel(treeModel);
		for (Map.Entry<String, ArrayList<String>> entry : treeMap.entrySet()){
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(entry.getKey());
			rootNode.add(node);
			for (int j = 0; j < entry.getValue().size(); j++){
				node.add(new DefaultMutableTreeNode(new TreeNodeCheckBox(entry.getValue().get(j), false)));
			}
		}
		cbTree.expandRow(0);
		cbTree.setRootVisible(false);
		treeModel.reload();
	}
	
	/**
	 * Populates the DefaultListModel with the groups to be displayed in the group view
	 * @return Returns true for succesful creation of group view, false for empty group list
	 */
	protected boolean createGroupView(){
		groupKeywordsHashSet = new HashSet<String>();
		groupKeywordsListModel.clear();
		groupNameListModel.clear();
		if (GroupInfo.isEmpty()){
			return false;
		}
		int index = 0;
		listOfGroups = new CheckBoxListItem[GroupInfo.size()];
		for (Map.Entry<String, String> entry : GroupInfo.entrySet()){
			String txt = "(" + entry.getKey() + ")" + " " + entry.getValue();
	    	listOfGroups[index] = new CheckBoxListItem(entry.getKey());
	    	groupNameListModel.addElement(listOfGroups[index]);
	    	++index;
		}
		return true;
	}
	
	/**
	 * Saves the user's selections in the AND/OR/NOT view to arraylists 
	 * that will be passed to the LogicEvaluator file. 
	 * @return Returns true if save was succesful, false otherwise.
	 */
	private boolean saveAndOrNot(){
		keyWordArrayList.clear();
		operandArrayList.clear();
		notArrayList.clear();
		if (cbKey1.getSelectedIndex() == -1 || cbKey2.getSelectedIndex() == -1 || cbLogic1.getSelectedIndex() == -1){
			JOptionPane.showMessageDialog(null, "Please enter a valid logical statement with at least one operator and two operands");
			return false;
		}
		if (cbKey1.getSelectedIndex() != -1){
			keyWordArrayList.add(cbKey1.getSelectedItem().toString());
			notArrayList.add(false); //Needed so the arraylists are the same size
		}
		if (cbKey2.getSelectedIndex() != -1){
			keyWordArrayList.add(cbKey2.getSelectedItem().toString());
		}
		if (cbKey3.getSelectedIndex() != -1){
			keyWordArrayList.add(cbKey3.getSelectedItem().toString());
		}

		if (cbLogic1.getSelectedIndex() != -1){
			operandArrayList.add("AND");
			if (cbLogic1.getSelectedItem().toString().equals("AND NOT")){
				notArrayList.add(true);
			}
			else {
				notArrayList.add(false);
			}
		}
		if (cbLogic2.getSelectedIndex() != -1){
			if (cbLogic2.getSelectedItem().toString().equals("AND")){
				operandArrayList.add("AND");
				notArrayList.add(false);
			}
			else if (cbLogic2.getSelectedItem().toString().equals("AND NOT")){
				operandArrayList.add("AND");
				notArrayList.add(true);
			}
			else if (cbLogic2.getSelectedItem().toString().equals("OR")){
				operandArrayList.add("OR");
				notArrayList.add(false);
			}
		}
		//For finding duplicates
		Set<String> set = new HashSet<String>(keyWordArrayList);
		if (set.size() < keyWordArrayList.size()){
			JOptionPane.showMessageDialog(null, "Duplicate keywords not allowed");
			return false;
		}
		return true;
	}
	
	/**
	 * Loads the group info into a HashMap
	 * @param stmt SQL statement
	 * @throws SQLException SQL Exception
	 */
	protected void loadGroupInfo(Statement stmt) throws SQLException{
		GroupInfo.clear();
		String query = "select GroupName, GroupKeywords from Groups";
		ResultSet rs = stmt.executeQuery(query);
		while(rs.next())
		{
			GroupInfo.put(rs.getString("GroupName"), rs.getString("GroupKeywords"));
		}
	}
	
	/**
	 * Helper function that intializes the preferences for the preference editor
	 */
	private void initPreferenceEditorValues(){
		lowerBound = (double) 0;
		upperBound = Double.MAX_VALUE;
		numLinesBefore = 5;
		numLinesAfter = 5;
	}
}

