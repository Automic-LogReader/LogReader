/**
 * @file CheckBoxList.java
 * @authors Leah Talkov, Jerry Tsui
 * @date 8/3/2016
 * Constructs Checkbox items for the keywords in the database.
 */

package interfaceTest;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class CheckBoxList {	
	
	static class CheckBoxListItem {
		   private String label;
		   private boolean isSelected = false;
		 
		   /**
		    * Constructs a CheckBoxListItem given a specific label
		 * @param label Text for the checkbox 
		 */
		public CheckBoxListItem(String label) {
		      this.label = label;
		   }
		 
		   /**
		    * Returns the boolean isSelected value of the CheckBoxListItem
		 * @return boolean isSelected value of the CheckBoxListItem
		 */
		public boolean isSelected() {
		      return isSelected;
		   }
		 
		   /**
		    * Sets the boolean isSelected value of the CheckBoxListItem
		 * @param isSelected boolean value to be set for the CheckBoxListItem
		 */
		public void setSelected(boolean isSelected) {
		      this.isSelected = isSelected;
		   }
		 
		   /* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
		      return label;
		   }
		}
	
	@SuppressWarnings("serial")
	static class CheckBoxListRenderer extends JCheckBox implements
    ListCellRenderer<CheckBoxListItem> {

		 @Override
		 public Component getListCellRendererComponent(
		       JList<? extends CheckBoxListItem> list, CheckBoxListItem value,
		       int index, boolean isSelected, boolean cellHasFocus) {
		    setEnabled(list.isEnabled());
		    setSelected(value.isSelected());
		    setFont(list.getFont());
		    setBackground(list.getBackground());
		    setForeground(list.getForeground());
		    setText(value.toString());
		    return this;
		 }
	}
}
