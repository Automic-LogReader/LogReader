package interfaceTest;

import javax.swing.JOptionPane;

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
	
	static boolean noCheckBoxSelected(CheckBoxNode[] cbList){
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
}
