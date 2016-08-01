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
	protected HashMap<String, String> GroupInfo = new HashMap<String, String>();
	protected final String [] headers = {"Error #", "Timestamp",
								"Keywords", "Error Message", "Suggested Solution"};
	
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

	protected HashMap<String, String> solutions = new HashMap<String, String>();
	protected HashMap<String, String> folderMap = new HashMap<String, String>();
	protected HashSet<String> folderSet = new HashSet<String>();
	protected HashMap<String, ArrayList<String>> treeMap = new HashMap<String, ArrayList<String>>();
	protected HashSet<String> keyWords = new HashSet<String>();
	protected HashSet<String> originalKeyWords;
	private boolean hasCopiedOriginalKeyWords;
	
	//Progress bar
	private long fileSize;
	protected long fileSizeDivHundred;
	
	protected JTable errorTable;
	private JPanel pnlMain;
	private JTextField filePath;
	//User clicks after selecting directory for log file
	protected JButton submitButton;
	//Returns the User back to the Main Menu
	//protected JButton backButton;
	
	protected JScrollPane errorScrollPane;
	private AdminView admin;
	
	protected LogParser logParser;
	
	//For the PreferenceEditor
	protected Double lowerBound;
	protected Double upperBound;
	protected Integer numLinesBefore;
	protected Integer numLinesAfter;
	
	protected JList<CheckBoxListItem> list;
	
	protected DefaultListModel<CheckBoxListItem> model;
	private JTabbedPane tabbedPane;
	protected JScrollPane groupScrollPane;
	
	protected JList<CheckBoxListItem> groupList;
	protected CheckBoxListItem[] listOfGroups;
	
	//For the and/or/not logic
	protected ArrayList<String> keyWordArrayList = new ArrayList<String>();
	protected ArrayList<String> operandArrayList = new ArrayList<String>();
	protected ArrayList<Boolean> notArrayList = new ArrayList<Boolean>();
	protected Vector<String> comboBoxKeyWords = new Vector<String>();
	private Stack<JComboBox<String>> mostRecentCB = new Stack<JComboBox<String>>();
	private JComboBox<String> cbKey1;
	private JComboBox<String> cbLogic1;
	private JComboBox<String> cbKey2;
	private JComboBox<String> cbLogic2;
	private JComboBox<String> cbKey3;
	
	//For the CheckBoxTree view
	protected CBTree cbTree;
	protected DefaultMutableTreeNode rootNode;
	protected DefaultTreeModel treeModel;
	public HashMap<String, TreeNodeCheckBox> checkBoxMap = new HashMap<String, TreeNodeCheckBox>();
	protected JScrollPane treeScrollPane;
	private JMenuItem menuItemCopy;
	protected JPopupMenu popupMenu;
	private JPanel pnlTreeView;
	/**
	 * Create the frame.
	 * @throws IOException 
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

		mostRecentCB.clear();
		
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
	 * Creates DB connection and then fills the data structures
	 * mapping the keywords to their respective folders
	 * @param stmt: SQL Statement
	 * @throws SQLException
	 */
	void fillKeywords(Statement stmt) throws SQLException{
		String query = "select Keyword, Folder from logerrors";
		ResultSet rs = stmt.executeQuery(query);
		while(rs.next())
		{
			keyWords.add(rs.getString("Keyword"));
			folderMap.put(rs.getString("Keyword"), rs.getString("Folder"));
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
	
	void findLogErrors(String path) throws IOException
	{
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
			t = new Thread(new Runnable()
			{
				public void run() {
					try {
						fileSize = file.length();
						fileSizeDivHundred = fileSize/100;
						
						submitButton.setEnabled(false);
						logParser.parseErrors(file, dialog);
						} catch (IOException e) {
							e.printStackTrace();
							}
						}			
					}
				);
		t.start();
	}
	
	//Called in LogParser.java
	void updateKeyWords(int selectedTab){
		keyWords.clear();
		if (selectedTab == 0){
			Enumeration g = ((DefaultMutableTreeNode) cbTree.getModel().getRoot()).preorderEnumeration();
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
			if (listOfGroups == null) return; //If list hasn't been intialized yet
			for (int i = 0; i < listOfGroups.length; i++){
				if (listOfGroups[i].isSelected()){
					String array[] = listOfGroups[i].toString().split(" ");
					for (String s : array){
						keyWords.add(s);
					}
				}
			}
		}
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
		setMinimumSize(new Dimension(1000, 300));
		
		try {
		     ClassLoader cl = this.getClass().getClassLoader();
		     ImageIcon programIcon = new ImageIcon(cl.getResource("res/logo.png"));
		     setIconImage(programIcon.getImage());
		  } catch (Exception e) {
		     System.out.println("Could not load program icon.");
		  }
	
		pnlMain = new JPanel();
		pnlMain.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(pnlMain);
		pnlMain.setLayout(new BorderLayout(0, 0));
		
		JPanel pnlBottom = new JPanel();
		pnlMain.add(pnlBottom, BorderLayout.SOUTH);
		
		pnlBottom.setLayout(new BoxLayout(pnlBottom, BoxLayout.X_AXIS));
		
		Component horizontalStrut_2 = Box.createHorizontalStrut(20);
		pnlBottom.add(horizontalStrut_2);
		
		Component horizontalStrut_4 = Box.createHorizontalStrut(20);
		pnlBottom.add(horizontalStrut_4);
		
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
		    //chooser.showOpenDialog(getRootPane());
		    FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        "Text Files", "txt", "text");
		    chooser.setFileFilter(filter);
		    chooser.setAcceptAllFileFilterUsed(false);
		    int returnVal = chooser.showOpenDialog(getRootPane());
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		    	 filePath.setText(chooser.getSelectedFile().getAbsolutePath());
		    }
		});
		pnlBottom.add(chooseFile);
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		pnlBottom.add(horizontalStrut);
		
		filePath = new JTextField();
		pnlBottom.add(filePath);
		filePath.setColumns(10);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		pnlBottom.add(horizontalStrut_1);
		
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
		pnlBottom.add(submitButton);
		
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
				// TODO Auto-generated catch block
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
		
		JPanel pnlComboBox = new JPanel(new FlowLayout());
		pnlComboBox.setOpaque(true);
		pnlComboBox.setBackground(Color.WHITE);
		cbKey1 = logicalComboBox(2);
		cbKey1.addActionListener(e -> {
			if (cbKey1.getSelectedIndex() != -1 && cbLogic1.getSelectedIndex() != -1 && cbKey2.getSelectedIndex() != -1){
				cbLogic2.setEnabled(true);
				cbKey3.setEnabled(true);
			}
		});
		cbLogic1 = logicalComboBox(3);
		cbLogic1.addActionListener(e -> {
			if (cbKey1.getSelectedIndex() != -1 && cbLogic1.getSelectedIndex() != -1 && cbKey2.getSelectedIndex() != -1){
				cbLogic2.setEnabled(true);
				cbKey3.setEnabled(true);
			}
		});
		cbKey2 = logicalComboBox(2);
		cbKey2.addActionListener(e -> {
			if (cbKey1.getSelectedIndex() != -1 && cbLogic1.getSelectedIndex() != -1 && cbKey2.getSelectedIndex() != -1){
				cbLogic2.setEnabled(true);
				cbKey3.setEnabled(true);
			}
		});
		cbLogic2 = logicalComboBox(1);
		cbLogic2.setEnabled(false);
		cbKey3 = logicalComboBox(2);
		cbKey3.setEnabled(false);
		
		pnlComboBox.add(cbKey1);
		pnlComboBox.add(cbLogic1);
		pnlComboBox.add(cbKey2);
		pnlComboBox.add(cbLogic2);
		pnlComboBox.add(cbKey3);

		JButton btnUndo = new JButton("Undo");
		btnUndo.setAlignmentX(CENTER_ALIGNMENT);
		btnUndo.addActionListener(e -> {
			if (!mostRecentCB.isEmpty()){
				mostRecentCB.pop().setSelectedIndex(-1);
			}
			if (cbLogic1.getSelectedIndex() == -1 || cbKey1.getSelectedIndex() == -1 || cbKey2.getSelectedIndex() == -1){
				if (cbLogic2.isEnabled() && cbKey3.isEnabled()){
					cbLogic2.setEnabled(false);
					cbKey3.setEnabled(false);
				}
			}
		});
		
		pnlAndOrNot.add(Box.createVerticalGlue());
		pnlAndOrNot.add(Box.createRigidArea(new Dimension(0, 10)));
		pnlAndOrNot.add(pnlComboBox);
		pnlAndOrNot.add(btnUndo);
		pnlAndOrNot.add(Box.createVerticalGlue());
		
		/*** GROUP PANEL ***/
		JPanel pnlGroupView = new JPanel();
		pnlGroupView.setLayout(new BoxLayout(pnlGroupView, BoxLayout.Y_AXIS));
		pnlGroupView.setMaximumSize(new Dimension(600, 280));
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
		
		pnlGroupView.add(groupScrollPane);
		
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
        popupMenu.add(menuItemCopy);
        
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
	 * @param option: Determines whether the JComboBox is an operand or an operator
	 * @return Returns the JComboBox based off of the option (operand, operator)
	 */
	private JComboBox<String> logicalComboBox(int option){
		JComboBox<String> cb = new JComboBox<String>();
		MutableComboBoxModel<String> model = (MutableComboBoxModel<String>)cb.getModel();
		cb.setOpaque(true);
		cb.setBackground(Color.WHITE);
		switch (option){
		case 1:
			cb.setPreferredSize(new Dimension(80, 20));
			model.addElement("AND");
			model.addElement("OR");
			model.addElement("AND NOT");
			break;
		case 2:
			cb.setPreferredSize(new Dimension(100, 20));
			for (String s: comboBoxKeyWords){
				model.addElement(s);
			}
			break;
		case 3:
			cb.setPreferredSize(new Dimension(80, 20));
			model.addElement("AND");
			model.addElement("AND NOT");
			break;
		}
		cb.addActionListener(e -> {
			if (cb.getSelectedIndex() != -1){
				if (!mostRecentCB.isEmpty()){
					if (mostRecentCB.peek() == cb) return;
				}
				mostRecentCB.push(cb);
			}
		});
		cb.setSelectedIndex(-1);

		return cb;
	}

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
	}
	
	protected boolean createGroupDisplay(){
		model.clear();
		if (GroupInfo.isEmpty()){
			return false;
		}
		int index = 0;
		listOfGroups = new CheckBoxListItem[GroupInfo.size()];
		for (Map.Entry<String, String> entry : GroupInfo.entrySet()){
			String txt = "(" + entry.getKey() + ")" + " " + entry.getValue();
	    	listOfGroups[index] = new CheckBoxListItem(txt);
	    	model.addElement(listOfGroups[index]);
	    	++index;
		}
		return true;
	}
	
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
	
	protected void loadGroupInfo(Statement stmt) throws SQLException{
		GroupInfo.clear();
		String query = "select GroupName, GroupKeywords from Groups";
		ResultSet rs = stmt.executeQuery(query);
		while(rs.next())
		{
			GroupInfo.put(rs.getString("GroupName"), rs.getString("GroupKeywords"));
		}
	}
	
	private void initPreferenceEditorValues(){
		lowerBound = (double) 0;
		upperBound = Double.MAX_VALUE;
		numLinesBefore = 0;
		numLinesAfter = 0;
	}
}

