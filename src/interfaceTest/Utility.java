package interfaceTest;

import java.util.Enumeration;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

import interfaceTest.CheckBoxList.CheckBoxListItem;

public final class Utility {
	
	private Utility(){
		//Do nothing
	}
	
	static boolean noCheckBoxSelected(CheckBoxListItem[] cbList){
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
	
	static boolean noCheckBoxSelected(CBTree tree){
		if (tree == null){
			return true;
		}
		Enumeration g = ((DefaultMutableTreeNode) tree.getModel().getRoot()).preorderEnumeration();
		while (g.hasMoreElements()){
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) g.nextElement();
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
