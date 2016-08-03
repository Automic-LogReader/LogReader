package interfaceTest;

import java.util.Enumeration;

import javax.swing.JOptionPane;
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
}
