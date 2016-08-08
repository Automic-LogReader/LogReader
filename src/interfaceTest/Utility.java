/**
 * @file Utility.java
 * @authors Leah Talkov, Jerry Tsui
 * @date 8/3/2016
 * Contains helper functions for CBTree and CheckBoxList.
 */

package interfaceTest;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Enumeration;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;

import interfaceTest.CheckBoxList.CheckBoxListItem;

public final class Utility {
	
	private Utility(){
		//Do nothing
	}
	
	/**
	 * Helper function to determine whether elements within a CheckBoxList are selected or not
	 * @param cbList The CheckBoxListItem array to be evaluated
	 * @return Returns true if no checkbox was selected, false if at least one was selected.
	 */
	public static boolean noCheckBoxSelected(CheckBoxListItem[] cbList){
		if (cbList == null){
			return true;
		}
		for (int i = 0; i <cbList.length; i++){
			if (cbList[i].isSelected()){
				return false;
			}
		}
		JOptionPane.showMessageDialog(null, "Please select a checkbox");
		return true;
	}
	
	/**
	 * Helper function to determine whether elements within a CheckBoxList are selected or not
	 * @param tree CheckBox Tree to be evaluated
	 * @return Returns true if no checkbox was selected, false if at least one was selected.
	 */
	public static boolean noCheckBoxSelected(CBTree tree){
		if (tree == null){
			return true;
		}
		Enumeration<?> e = ((DefaultMutableTreeNode) tree.getModel().getRoot()).preorderEnumeration();
		while (e.hasMoreElements()){
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
			Object obj = node.getUserObject();  
			if (obj instanceof TreeNodeCheckBox){
				TreeNodeCheckBox cb = (TreeNodeCheckBox) obj;
				if (cb.isSelected()){
					return false;
				}
				
			}
		}
		JOptionPane.showMessageDialog(null, "No checkbox selected");
		return true;
	}
	
	/**
	 * Utility function that adds an Escape Key listener to a JDialog
	 * @param dialog JDialog to attach listener to
	 */
	public static void addEscapeListener(final JDialog dialog) {
	    ActionListener escListener = new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            dialog.setVisible(false);
	        }
	    };
	    dialog.getRootPane().registerKeyboardAction(escListener,
	            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
	            JComponent.WHEN_IN_FOCUSED_WINDOW);
	}
}
