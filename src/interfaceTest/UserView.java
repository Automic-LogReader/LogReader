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
import javax.swing.AbstractButton;
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
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.MutableComboBoxModel;
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
	protected HashMap<String, String> GroupInfo = new HashMap<String, String>();
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
	protected HashMap<String, String> folderMap = new HashMap<String, String>();
	protected HashSet<String> folderSet = new HashSet<String>();
	protected HashMap<String, ArrayList<String>> treeMap = new HashMap<String, ArrayList<String>>();
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
	
	protected LogParser logParser;
	//Bounds for time-critical DB Calls
	protected Double lowerBound;
	protected Double upperBound;
	
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
	private JComboBox<String> key1;
	private JComboBox<String> logic1;
	private JComboBox<String> key2;
	private JComboBox<String> logic2;
	private JComboBox<String> key3;
	
	//For the CheckBoxTree view
	protected CBTree cbTree;
	protected DefaultMutableTreeNode rt;
	protected DefaultTreeModel treeModel;
	public HashMap<String, TreeNodeCheckBox> checkBoxMap = new HashMap<String, TreeNodeCheckBox>();
	protected JScrollPane treeScrollPane;
	private JMenuItem menuItemCopy;
	protected JPopupMenu popupMenu;
	private JPanel treePanel;
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
	
	void fillKeywords(Statement stmt) throws SQLException
	{
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
				System.out.println("tab 1");
				if (Utility.noCheckBoxSelected(cbTree)) {
					return;
				}
			}
			else if (tabbedPane.getSelectedIndex() == 1){
				System.out.println("tab 2");
				if (!saveAndOrNot()){
					return;
				}
			}
			else if (tabbedPane.getSelectedIndex() == 2){
				System.out.println("tab 3");
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
						// TODO Auto-generated catch block
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
		
		try {
		     ClassLoader cl = this.getClass().getClassLoader();
		     ImageIcon programIcon = new ImageIcon(cl.getResource("res/logo.png"));
		     setIconImage(programIcon.getImage());
		  } catch (Exception e) {
		     System.out.println("Could not load program icon.");
		  }
	
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
			new PreferenceEditor(this, isAdmin);
		});
		preferenceButton.setToolTipText("Groupings, Time Critical DB Call Intervals ...");
		bottomPanel.add(preferenceButton);
		
		bottomPanel.add(Box.createRigidArea(new Dimension(10,0)));
		
		chooseFile = new JButton("Choose File");
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
		
		
		JButton adminButton = new JButton("Admin Features");
		adminButton.setPreferredSize(new Dimension(130, 30));
		adminButton.setToolTipText("Modify entries, folders, solutions");
		adminButton.addActionListener(e -> {
			try {
				admin = new AdminView(this);
				admin.setVisible(true);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		});
		bottomPanel.add(adminButton);
		
		if(isAdmin){
			adminButton.setVisible(true);
			setTitle("Administrator View");
		} else {
			adminButton.setVisible(false);
			setTitle("User View");
		}
		
		Component horizontalStrut_5 = Box.createHorizontalStrut(20);
		bottomPanel.add(horizontalStrut_5);
		
		Component horizontalStrut_3 = Box.createHorizontalStrut(20);
		bottomPanel.add(horizontalStrut_3);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setMaximumSize(new Dimension(300, 280));
		contentPane.add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
			
		treePanel = new JPanel();
		treePanel.setLayout(new BoxLayout(treePanel, BoxLayout.Y_AXIS));
		createTreeView();
		
		
		JButton toggleAllButton = new JButton("Toggle All");
		toggleAllButton.addActionListener(e -> {
			expandAll(cbTree, new TreePath(cbTree.getModel().getRoot()));
			Enumeration g = ((DefaultMutableTreeNode) cbTree.getModel().getRoot()).preorderEnumeration();
			while (g.hasMoreElements()){
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) g.nextElement();
				Object obj = node.getUserObject();  
				if (obj instanceof TreeNodeCheckBox){
					TreeNodeCheckBox cb = (TreeNodeCheckBox) obj;
					cb.setSelected(!cb.isSelected());
				}
			}
    		treePanel.repaint();
		});
		toggleAllButton.setAlignmentX( Component.CENTER_ALIGNMENT);
		
		treeScrollPane = new JScrollPane(cbTree);
		treePanel.add(treeScrollPane);
		treePanel.add(toggleAllButton);
		
		/*** AND OR NOT PANEL ***/
		JPanel andOrNotPanel = new JPanel();
		andOrNotPanel.setMaximumSize(new Dimension(300, 280));
		andOrNotPanel.setOpaque(true);
		andOrNotPanel.setBackground(Color.WHITE);
		andOrNotPanel.setLayout(new BoxLayout(andOrNotPanel, BoxLayout.Y_AXIS));
		
		JPanel comboBoxPanel = new JPanel(new FlowLayout());
		comboBoxPanel.setOpaque(true);
		comboBoxPanel.setBackground(Color.WHITE);
		key1 = logicalComboBox(2);
		key1.addActionListener(e -> {
			if (key1.getSelectedIndex() != -1 && logic1.getSelectedIndex() != -1 && key2.getSelectedIndex() != -1){
				System.out.println("enabled new fields");
				logic2.setEnabled(true);
				key3.setEnabled(true);
			}
		});
		logic1 = logicalComboBox(3);
		logic1.addActionListener(e -> {
			if (key1.getSelectedIndex() != -1 && logic1.getSelectedIndex() != -1 && key2.getSelectedIndex() != -1){
				System.out.println("enabled new fields");
				logic2.setEnabled(true);
				key3.setEnabled(true);
			}
		});
		key2 = logicalComboBox(2);
		key2.addActionListener(e -> {
			if (key1.getSelectedIndex() != -1 && logic1.getSelectedIndex() != -1 && key2.getSelectedIndex() != -1){
				System.out.println("enabled new fields");
				logic2.setEnabled(true);
				key3.setEnabled(true);
			}
		});
		logic2 = logicalComboBox(1);
		logic2.setEnabled(false);
		key3 = logicalComboBox(2);
		key3.setEnabled(false);
		
		comboBoxPanel.add(key1);
		comboBoxPanel.add(logic1);
		comboBoxPanel.add(key2);
		comboBoxPanel.add(logic2);
		comboBoxPanel.add(key3);

		JButton undoButton = new JButton("Undo");
		undoButton.setAlignmentX(CENTER_ALIGNMENT);
		undoButton.addActionListener(e -> {
			if (!mostRecentCB.isEmpty()){
				mostRecentCB.pop().setSelectedIndex(-1);
			}
			if (logic1.getSelectedIndex() == -1 || key1.getSelectedIndex() == -1 || key2.getSelectedIndex() == -1){
				if (logic2.isEnabled() && key3.isEnabled()){
					logic2.setEnabled(false);
					key3.setEnabled(false);
				}
			}
		});
		
		andOrNotPanel.add(Box.createVerticalGlue());
		andOrNotPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		andOrNotPanel.add(comboBoxPanel);
		andOrNotPanel.add(undoButton);
		andOrNotPanel.add(Box.createVerticalGlue());
		
		/*** GROUP PANEL ***/
		JPanel groupPanel = new JPanel();
		groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.Y_AXIS));
		groupPanel.setMaximumSize(new Dimension(300, 280));
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
		mainPanel.add(errorScrollPane);
		
		setVisible(true);
	}
	
	JComboBox<String> logicalComboBox(int option){
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
	
	private void expandAll(JTree tree, TreePath parent) {
		    TreeNode node = (TreeNode) parent.getLastPathComponent();
		    if (node.getChildCount() >= 0) {
		      for (Enumeration e = node.children(); e.hasMoreElements();) {
		        TreeNode n = (TreeNode) e.nextElement();
		        TreePath path = parent.pathByAddingChild(n);
		        expandAll(tree, path);
		      }
		    }
		    tree.expandPath(parent);
	}
	
	void createTreeView(){
		cbTree = new CBTree();
		cbTree.addMouseListener(new MouseAdapter(){
			public void mousePressed (MouseEvent e){
	            if ( SwingUtilities.isRightMouseButton(e)){
	                TreePath path = cbTree.getPathForLocation ( e.getX (), e.getY () );
	                DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
                    Object obj = node.getUserObject();
                    if (obj instanceof String){
                    	System.out.println(obj);
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
    	                    	treePanel.repaint();
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
    	                    	treePanel.repaint();
    	                    });
    	                    menu.add(menuItemSelectAll);
    	                    menu.add(menuItemDeselectAll);
    	                    menu.show(cbTree, pathBounds.x, pathBounds.y + pathBounds.height); 
    	                }
                    }
                    else {
                    	return;
                    }
	            }
	        }
		});
		rt = new DefaultMutableTreeNode("Root");
		treeModel = new DefaultTreeModel(rt);
		cbTree.setModel(treeModel);
		for (Map.Entry<String, ArrayList<String>> entry : treeMap.entrySet()){
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(entry.getKey());
			rt.add(node);
			for (int j = 0; j < entry.getValue().size(); j++){
				node.add(new DefaultMutableTreeNode(new TreeNodeCheckBox(entry.getValue().get(j), false)));
			}
		}

		cbTree.expandRow(0);
		cbTree.setRootVisible(false);
	}
	
	boolean createGroupDisplay(){
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
	
	boolean saveAndOrNot(){
		keyWordArrayList.clear();
		operandArrayList.clear();
		notArrayList.clear();
		if (key1.getSelectedIndex() == -1 || key2.getSelectedIndex() == -1 || logic1.getSelectedIndex() == -1){
			JOptionPane.showMessageDialog(null, "Please enter a valid logical statement with at least one operator and two operands");
			return false;
		}
		if (key1.getSelectedIndex() != -1){
			keyWordArrayList.add(key1.getSelectedItem().toString());
			notArrayList.add(false); //Needed so the arraylists are the same size
		}
		if (key2.getSelectedIndex() != -1){
			keyWordArrayList.add(key2.getSelectedItem().toString());
		}
		if (key3.getSelectedIndex() != -1){
			keyWordArrayList.add(key3.getSelectedItem().toString());
		}

		if (logic1.getSelectedIndex() != -1){
			operandArrayList.add("AND");
			if (logic1.getSelectedItem().toString().equals("AND NOT")){
				notArrayList.add(true);
			}
			else {
				notArrayList.add(false);
			}
		}
		if (logic2.getSelectedIndex() != -1){
			if (logic2.getSelectedItem().toString().equals("AND")){
				operandArrayList.add("AND");
				notArrayList.add(false);
			}
			else if (logic2.getSelectedItem().toString().equals("AND NOT")){
				operandArrayList.add("AND");
				notArrayList.add(true);
			}
			else if (logic2.getSelectedItem().toString().equals("OR")){
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
	
	void loadGroupInfo(Statement stmt) throws SQLException
	{
		GroupInfo.clear();
		String query = "select GroupName, GroupKeywords from Groups";
		ResultSet rs = stmt.executeQuery(query);
		while(rs.next())
		{
			GroupInfo.put(rs.getString("GroupName"), rs.getString("GroupKeywords"));
		}
	}
}

